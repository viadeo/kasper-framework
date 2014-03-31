// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.doc.nodes.validation.DefaultPropertyValidator;
import com.viadeo.kasper.doc.nodes.validation.PropertyValidationProcessor;
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

	public DocumentedBean(final Class componentClazz) {
		final List<Field> properties = Lists.newArrayList();
		getAllFields(properties, componentClazz);

        final PropertyValidationProcessor processor = new PropertyValidationProcessor(new DefaultPropertyValidator());
		
		for (final Field field : properties) {
			field.setAccessible(true);

			if ( (! Modifier.isTransient(field.getModifiers())) && (! Modifier.isStatic(field.getModifiers()))) {
				final String name = field.getName();

                if (null != field.getAnnotation(Transient.class)) {
                    continue;
                }

                if (LinkedConcept.class.isAssignableFrom(field.getType())) {
                    continue;
                }
				
				if (name.contentEquals("serialVersionUID")) {
					continue;
				}
				
				final Boolean isList;
                final Class propClass = field.getType();
				final String type;

                if (Collection.class.isAssignableFrom(propClass)) {

					@SuppressWarnings("unchecked")
					final Optional<Class> optType = (Optional<Class>)
							ReflectionGenericsResolver.getParameterTypeFromClass(
									field, componentClazz, Collection.class, 0);
					
					if ( ! optType.isPresent()) {
						LOGGER.warn(String.format(
                                "Unable to find collection enclosed type for field %s in class %s",
                                name, componentClazz.getSimpleName())
                        );
						type = "unknown";
					} else {
						type = optType.get().getSimpleName();
					}
					isList = true;
					
				} else if (Map.class.isAssignableFrom(propClass)) {

					@SuppressWarnings("unchecked")
					final Optional<Class> optType = (Optional<Class>)
							ReflectionGenericsResolver.getParameterTypeFromClass(
									field, componentClazz, Map.class, 1
                            );
					
 					if (!optType.isPresent()) {
						LOGGER.warn(String.format(
                                "Unable to find map enclosed type for field %s in class %s",
								name, componentClazz.getSimpleName()
                        ));
						type = "unknown";
					} else {
						type = optType.get().getSimpleName();
					}
					isList = true;					
					
				} else {
                    if(propClass.isEnum()) {
                        type = propClass.getSimpleName() + Arrays.asList(propClass.getEnumConstants());
                    } else {
                        type = propClass.getSimpleName();
                    }
					isList = false;
				}

                if ( ! name.startsWith("this$")) {
                    final DocumentedProperty documentedProperty = new DocumentedProperty(
                            name,
                            type,
                            isList,
                            Sets.<DocumentedConstraint>newHashSet()
                    );
                    processor.process(field, documentedProperty);
                    this.add(documentedProperty);
                }
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	private static List<Field> getAllFields(final List<Field> fields, final Type type) {
        final Class typeClass = extractClassFromType(type);
        Collections.addAll(fields, typeClass.getDeclaredFields());

	    if (null != typeClass.getSuperclass()) {
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
