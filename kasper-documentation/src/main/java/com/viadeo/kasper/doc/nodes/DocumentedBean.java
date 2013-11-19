// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/*
 * FIXME: use the same fields inference mechanisms than jackson serializer..
 * FIXME: improve Map resolution : the two types must be extracted
 */
public class DocumentedBean extends ArrayList<DocumentedProperty> {
	private static final long serialVersionUID = 4149894288444871301L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentedBean.class);

    // ------------------------------------------------------------------------

	DocumentedBean(final Class componentClazz) {
		final List<Field> properties = Lists.newArrayList();
		getAllFields(properties, componentClazz);
		
		for (final Field property : properties) {
			property.setAccessible(true);

			if (!Modifier.isTransient(property.getModifiers()) && !Modifier.isStatic(property.getModifiers())) {
				final String name = property.getName();

                if (null != property.getAnnotation(Transient.class)) {
                    continue;
                }

                if (LinkedConcept.class.isAssignableFrom(property.getType())) {
                    continue;
                }
				
				if (name.contentEquals("serialVersionUID")) {
					continue;
				}
				
				final Boolean isList;
                final Class propClass = property.getType();
				final String type;

				if (Collection.class.isAssignableFrom(propClass)) {

					@SuppressWarnings("unchecked")
					final Optional<Class> optType = (Optional<Class>)
							ReflectionGenericsResolver.getParameterTypeFromClass(
									property, componentClazz, Collection.class, 0);
					
					if (!optType.isPresent()) {
						LOGGER.warn(String.format("Unable to find collection enclosed type for field %s in class %s",
                                name, componentClazz.getSimpleName()));
						type = "unknown";
					} else {
						type = optType.get().getSimpleName();
					}
					isList = true;
					
				} else if (Map.class.isAssignableFrom(propClass)) {

					@SuppressWarnings("unchecked")
					final Optional<Class> optType = (Optional<Class>)
							ReflectionGenericsResolver.getParameterTypeFromClass(
									property, componentClazz, Map.class, 1);
					
 					if (!optType.isPresent()) {
						LOGGER.warn(String.format("Unable to find map enclosed type for field %s in class %s",
								name, componentClazz.getSimpleName()));
						type = "unknown";
					} else {
						type = optType.get().getSimpleName();
					}
					isList = true;					
					
				} else {
					type = propClass.getSimpleName();
					isList = false;
				}

                if (!name.startsWith("this$")) {
				    this.add(new DocumentedProperty(name, type, isList));
                }
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	private static List<Field> getAllFields(final List<Field> fields, final Type type) {
        final Class typeClass = extractClassFromType(type);
        Collections.addAll(fields, typeClass.getDeclaredFields());

	    if (typeClass.getSuperclass() != null) {
	        getAllFields(fields, typeClass.getGenericSuperclass());
	    }

	    return fields;
	}

    private static Class extractClassFromType(final Type t) {
        if (t instanceof Class) {
            return (Class)t;
        }
        return (Class)((ParameterizedType)t).getRawType();
    }
	
}
