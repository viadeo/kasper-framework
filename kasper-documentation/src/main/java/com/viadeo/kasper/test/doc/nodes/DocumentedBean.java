package com.viadeo.kasper.test.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.values.annotation.XKasperValue;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DocumentedBean extends ArrayList<DocumentedProperty> {
	private static final long serialVersionUID = 4149894288444871301L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentedBean.class);
	
	DocumentedBean(final Class<?> componentClazz) {
		final List<Field> properties = Lists.newArrayList();
		getAllFields(properties, componentClazz);
		
		for (final Field property : properties) {
			property.setAccessible(true);
			if (!Modifier.isTransient(property.getModifiers()) && !Modifier.isStatic(property.getModifiers())) {
				String name = property.getName();				
				
				if (name.contentEquals("serialVersionUID")) {
					continue;
				}
				
				final XKasperValue annoValue = property.getAnnotation(XKasperValue.class);
				if ((null != annoValue) && (!annoValue.name().isEmpty())) {
					name = annoValue.name();
				}
				
				final Boolean isList;
				final Class<?> classType = property.getType();
				final String type;
				
				if (List.class.isAssignableFrom(classType)) {
					
					@SuppressWarnings("unchecked")
					final Optional<Class<?>> optType = (Optional<Class<?>>) 
							ReflectionGenericsResolver.getParameterTypeFromClass(
									classType, List.class, 0);
					
					if (!optType.isPresent()) {
						LOGGER.error(String.format("Unable to find list type for field %s in class %s", 
								name, componentClazz.getSimpleName()));
						type = "unknown";
					} else {
						type = optType.get().getSimpleName();
					}
					isList = true;
					
				} else if (Map.class.isAssignableFrom(classType)) {

					@SuppressWarnings("unchecked")
					final Optional<Class<?>> optType = (Optional<Class<?>>) 
							ReflectionGenericsResolver.getParameterTypeFromClass(
									classType, Map.class, 1);
					
					type = optType.get().getSimpleName();
					isList = true;					
					
				} else {
					type = classType.getSimpleName();
					isList = false;
				}
				
				this.add(new DocumentedProperty(name, type, isList));
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	private static List<Field> getAllFields(final List<Field> fields, final Class<?> type) {
        Collections.addAll(fields, type.getDeclaredFields());

	    if (type.getSuperclass() != null) {
	        getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
	
}
