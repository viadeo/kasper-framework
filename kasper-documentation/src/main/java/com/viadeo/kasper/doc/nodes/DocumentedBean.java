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
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.doc.nodes.validation.DefaultPropertyValidator;
import com.viadeo.kasper.doc.nodes.validation.PropertyValidationProcessor;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.lang.reflect.*;
import java.util.*;

/*
 * FIXME: use the same fields inference mechanisms than jackson serializer..
 * FIXME: improve Map resolution : the two types must be extracted
 */
public class DocumentedBean extends ArrayList<DocumentedProperty> {
	private static final long serialVersionUID = 4149894288444871301L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentedBean.class);

    // ------------------------------------------------------------------------

    public interface Extractor {

        boolean accept(Field field);
        Optional<DocumentedProperty> extract(Field field, Class clazz);

    }

    // ------------------------------------------------------------------------

    public static abstract class BaseExtractor implements Extractor{

        private final Extractor next;

        public BaseExtractor(final Extractor next) {
            this.next = next;
        }

        @Override
        public final Optional<DocumentedProperty> extract(final Field field, final Class clazz) {
            if (accept(field)) {
                return doExtract(field, clazz);
            } else if (null == next) {
                return Optional.absent();
            } else {
                return next.extract(field, clazz);
            }
        }

        public abstract Optional<DocumentedProperty> doExtract(final Field field, final Class clazz);

    }

    // ------------------------------------------------------------------------

    public static class NoTransient extends BaseExtractor {

        public NoTransient(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return (null != field.getAnnotation(Transient.class)) || Modifier.isTransient(field.getModifiers());
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            return Optional.absent();
        }
    }

    // ------------------------------------------------------------------------

    public static class NoConstant extends BaseExtractor {

        public NoConstant(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return Modifier.isStatic(field.getModifiers());
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            return Optional.absent();
        }

    }

    // ------------------------------------------------------------------------

    public static class NoThisDollarInFieldName extends BaseExtractor {

        public NoThisDollarInFieldName(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return field.getName().startsWith("this$");
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            return Optional.absent();
        }

    }

    // ------------------------------------------------------------------------

    public static class FieldExtractor implements Extractor {

        @Override
        public boolean accept(final Field field) {
            return true;
        }

        @Override
        public  Optional<DocumentedProperty> extract(final Field field, final Class clazz) {
            return Optional.of(
                    new DocumentedProperty(
                            field.getName(),
                            field.getType().getSimpleName(),
                            null,
                            false,
                            false,
                            QueryResult.class.isAssignableFrom(field.getType()),
                            Sets.<DocumentedConstraint>newHashSet()
                    )
            );
        }

    }

    // ------------------------------------------------------------------------

    public static class EnumExtractor extends BaseExtractor {

        public EnumExtractor(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return field.getType().isEnum();
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            return Optional.of(
                    new DocumentedProperty(
                            field.getName(),
                            field.getType().getSimpleName(),
                            Arrays.asList(field.getType().getEnumConstants()).toString(),
                            false,
                            false,
                            false,
                            Sets.<DocumentedConstraint>newHashSet()
                    )
            );
        }

    }

    // ------------------------------------------------------------------------

    public static class LinkedConceptExtractor extends BaseExtractor {

        public LinkedConceptExtractor(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return LinkedConcept.class.isAssignableFrom(field.getType());
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            final Class<?> type;

            @SuppressWarnings("unchecked")
            final Optional<Class> optType =
                    (Optional<Class>)
                            ReflectionGenericsResolver.getParameterTypeFromClass(
                                    field,
                                    clazz,
                                    LinkedConcept.class,
                                    LinkedConcept.CONCEPT_PARAMETER_POSITION
                            );

            if ( ! optType.isPresent()) {
                LOGGER.warn(String.format(
                        "Unable to find map enclosed type for field %s in class %s",
                        field.getName(), clazz.getSimpleName()
                ));
                type = null;
            } else {
                type = optType.get();
            }

            return Optional.of(
                    new DocumentedProperty(
                            field.getName(),
                            type == null ? "unknown" : type.getSimpleName(),
                            null,
                            false,
                            true,
                            QueryResult.class.isAssignableFrom(type),
                            Sets.<DocumentedConstraint>newHashSet()
                    )
            );
        }

    }

    // ------------------------------------------------------------------------

    public static class CollectionExtractor extends BaseExtractor {

        public CollectionExtractor(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return Collection.class.isAssignableFrom(field.getType());
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {

            if (field.getGenericType() instanceof ParameterizedType) {
                final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                final Type paramType =  genericType.getActualTypeArguments()[0];
                final Optional<Class> optionalParamClass = ReflectionGenericsResolver.getClass(paramType);

                if (optionalParamClass.isPresent()) {
                    final Class paramClass = optionalParamClass.get();

                    if( LinkedConcept.class.isAssignableFrom(paramClass)) {
                        final Type subParamType = ((ParameterizedType) paramType).getActualTypeArguments()[0];
                        final Class subParamClass = (Class) subParamType;

                        return Optional.of(
                                new DocumentedProperty(
                                        field.getName(),
                                        subParamClass.getSimpleName(),
                                        null,
                                        true,
                                        true,
                                        QueryResult.class.isAssignableFrom(subParamClass),
                                        Sets.<DocumentedConstraint>newHashSet()
                                )
                        );
                    } else {
                        return Optional.of(
                            new DocumentedProperty(
                                field.getName(),
                                paramClass.getSimpleName(),
                                null,
                                true,
                                false,
                                QueryResult.class.isAssignableFrom(paramClass),
                                Sets.<DocumentedConstraint>newHashSet()
                            )
                        );
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    final Optional<Class> optType = (Optional<Class>)
                            ReflectionGenericsResolver.getParameterTypeFromClass(field, clazz, Collection.class, 0);

                    if (optType.isPresent()) {
                        return Optional.of(
                                new DocumentedProperty(
                                        field.getName(),
                                        optType.get().getSimpleName(),
                                        null,
                                        true,
                                        false,
                                        QueryResult.class.isAssignableFrom(optType.get()),
                                        Sets.<DocumentedConstraint>newHashSet()
                                )
                        );
                    } else {
                        return Optional.of(
                                new DocumentedProperty(
                                        field.getName(),
                                        "unknown",
                                        null,
                                        true,
                                        false,
                                        false,
                                        Sets.<DocumentedConstraint>newHashSet()
                                )
                        );
                    }
                }
            }

            return null;
        }

    }

    // ------------------------------------------------------------------------

    public static class MapExtractor extends BaseExtractor {

        public MapExtractor(final Extractor next) {
            super(next);
        }

        @Override
        public boolean accept(final Field field) {
            return Map.class.isAssignableFrom(field.getType());
        }

        @Override
        public  Optional<DocumentedProperty> doExtract(final Field field, final Class clazz) {
            final Class<?> type;

            @SuppressWarnings("unchecked")
            final Optional<Class> optType = (Optional<Class>)
                    ReflectionGenericsResolver.getParameterTypeFromClass(
                            field, clazz, Map.class, 1
                    );

            if ( ! optType.isPresent()) {
                LOGGER.warn(String.format(
                        "Unable to find map enclosed type for field %s in class %s",
                        field.getName(), clazz.getSimpleName()
                ));
                type = null;
            } else {
                type = optType.get();
            }

            return Optional.of(
                    new DocumentedProperty(
                            field.getName(),
                            type == null ? "unknown" : type.getSimpleName(),
                            null,
                            true,
                            false,
                            QueryResult.class.isAssignableFrom(type),
                            Sets.<DocumentedConstraint>newHashSet()
                    )
            );
        }

    }

    // ------------------------------------------------------------------------

    public DocumentedBean(final Class componentClazz) {
        this(componentClazz,
            new NoThisDollarInFieldName(new NoTransient(new NoConstant(
                new CollectionExtractor(new MapExtractor(new LinkedConceptExtractor(new EnumExtractor(
                    new FieldExtractor()
                ))))
            )))
        );
    }

    public DocumentedBean(final Class componentClazz, final Extractor extractor) {
        final List<Field> properties = Lists.newArrayList();
        getAllFields(properties, componentClazz);

        final PropertyValidationProcessor processor = new PropertyValidationProcessor(new DefaultPropertyValidator());

        for (final Field field : properties) {
            field.setAccessible(true);

            final Optional<DocumentedProperty> optionalDocumentedProperty = extractor.extract(field, componentClazz);

            if (optionalDocumentedProperty.isPresent()) {
                final DocumentedProperty documentedProperty = optionalDocumentedProperty.get();
                processor.process(field, documentedProperty);
                this.add(documentedProperty);
            }
        }
    }
	
	public static List<Field> getAllFields(final List<Field> fields, final Type type) {
        final Class typeClass = extractClassFromType(type);
        Collections.addAll(fields, typeClass.getDeclaredFields());

	    if (null != typeClass.getSuperclass()) {
	        getAllFields(fields, typeClass.getGenericSuperclass());
	    }

	    return fields;
	}

    private static Class extractClassFromType(final Type t) {
        if (t instanceof Class) {
            return (Class) t;
        }
        return (Class)((ParameterizedType)t).getRawType();
    }
	
}
