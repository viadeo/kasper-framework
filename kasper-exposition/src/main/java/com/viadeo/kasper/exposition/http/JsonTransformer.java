// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JsonTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTransformer.class);

    private final NNode current;

    protected JsonTransformer(final Optional<Class> optionalClazz, final Map<String, String[]> parameters){
        this.current = new NNode("root", false);

        for (final String key : parameters.keySet()) {
            createNodes(current, optionalClazz, key, parameters.get(key));
        }
    }

    protected static boolean isArrayOrCollection(final String fieldName, final Optional<Class> optionalClazz) {
        if (optionalClazz.isPresent()) {
            try {
                final Class clazz = optionalClazz.get().getDeclaredField(fieldName).getType();
                return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
            } catch (NoSuchFieldException | NullPointerException e) {
                LOGGER.debug("The '{}' parameter is ignored due to an unexpected error with '{}'", fieldName, optionalClazz.get().getName(), e);
            }
        }
        return false;
    }

    protected static Optional<Class> getSubClass(final String fieldName, final Optional<Class> optionalClazz) {
        Optional<Class> optionalSubClass = Optional.absent();

        if (optionalClazz.isPresent()) {
            try {
                final Field declaredField = optionalClazz.get().getDeclaredField(fieldName);
                if (declaredField != null) {
                    if (Collection.class.isAssignableFrom(declaredField.getType())) {
                        final Class clazz = (Class) ((ParameterizedType) declaredField.getGenericType()).getActualTypeArguments()[0];
                        optionalSubClass = Optional.of(clazz);
                    } else {
                        final Class type = declaredField.getType();
                        optionalSubClass = Optional.of(type);
                    }
                }
            } catch (NoSuchFieldException e) {
                LOGGER.debug("The '{}' parameter is ignored : unable to identify the class related to the parameter from '{}'", fieldName, optionalClazz.get().getName(), e);
            }
        }

        return optionalSubClass;
    }

    protected void createNodes(
            final NNode parent,
            final Optional<Class> optionalClazz,
            final String key,
            final String[] values
    ) {
        final int index = key.indexOf('.');

        if(values.length == 0) {
            return;
        }

        if (index == -1) {
            parent.append(key, new Leaf(key, values, isArrayOrCollection(key, optionalClazz)));
        } else {
            final String actualName = key.substring(0, index);
            final String subName = key.substring(index + 1, key.length());
            final NNode node;

            if (parent.hasElement(actualName)) {
                final Collection<Node> elements = parent.getElements(actualName);
                final Object element = elements.iterator().next();

                if( element instanceof NNode) {
                    node = (NNode) element;
                } else {
                    throw new JsonTransformerException("unexpected error");
                }
            } else {
                node = new NNode(actualName, isArrayOrCollection(actualName, optionalClazz));
                parent.append(actualName, node);
            }

            createNodes(node, getSubClass(actualName, optionalClazz), subName, values);
        }
    }

    public static JsonTransformer from(final Map<String, String[]> parameters) {
        return new JsonTransformer(Optional.<Class>absent(), parameters);
    }

    public static JsonTransformer from(final Class clazz, final Map<String, String[]> parameters) {
        return new JsonTransformer(Optional.fromNullable(clazz), parameters);
    }

    public static JsonTransformer from(final Optional<Class> optionalClazz, final Map<String, String[]> parameters) {
        return new JsonTransformer(optionalClazz, parameters);
    }

    public String toJson() {
        return current.toJson().toJSONString();
    }

    private static interface Node {
        JSONAware toJson();
        String getName();
    }

    private static class Leaf implements Node {
        private final String name;
        private final List<String> values;
        private final boolean arrayOrCollection;

        public Leaf(String name, String[] values, boolean arrayOrCollection) {
            checkNotNull(values);
            checkState(values.length > 0, "should contains at least one value");
            this.name = checkNotNull(name);
            this.values = Lists.newArrayList(values);
            this.arrayOrCollection = checkNotNull(arrayOrCollection);
        }

        @Override
        public String getName() {
            return name;
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONAware toJson() {
            if (arrayOrCollection || values.size() > 1) {
                final JSONArray array = new JSONArray();
                array.addAll(values);
                return array;
            } else {
                return new JSONAware() {
                    @Override
                    public String toJSONString() {
                        return "\"" + String.valueOf(values.get(0)) + "\"";
                    }
                };
            }
        }
    }

    private static class NNode implements Node {
        public static final Pattern MULTI_COMPLEX_FIELD = Pattern.compile("_[1-9]+");

        private final Multimap<String, Node> elements;
        private final String name;
        private final boolean arrayOrCollection;

        private NNode(final String name, final boolean arrayOrCollection) {
            this.name = name;
            this.arrayOrCollection = arrayOrCollection;
            this.elements = ArrayListMultimap.create();
        }

        @Override
        public String getName() {
            return name;
        }

        public boolean hasElement(final String name) {
            return elements.containsKey(name);
        }

        public Collection<Node> getElements(final String name) {
            return elements.get(name);
        }

        public NNode append(final String name, final Node value) {
            if (elements.get(name).size() > 0) {
                final Class<?> elementClass = elements.get(name).iterator().next().getClass();
                if (elementClass != value.getClass()) {
                    throw new JsonTransformerException("Unexpected error : an array should be a series of elements of the same type");
                }
            }

            this.elements.put(name, value);

            return this;
        }

        protected boolean containsMultiComplexField() {
            if ( ! elements.isEmpty()) {
                final String key = elements.keySet().iterator().next();
                return MULTI_COMPLEX_FIELD.matcher(key).matches();
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONAware toJson() {
            final JSONAware jsonAware;

            if (arrayOrCollection) {
                if (containsMultiComplexField()) {
                    jsonAware = doToJson();

                } else {
                    final JSONArray array = new JSONArray();
                    array.add(doToJson());
                    jsonAware = array;
                }
            } else {
                jsonAware = doToJson();
            }

            return jsonAware;
        }

        @SuppressWarnings("unchecked")
        private JSONAware doToJson() {
            final JSONAware jsonAware;

            if ( elements.isEmpty()) {
                jsonAware = new JSONObject();

            } else if (containsMultiComplexField()) {
                final JSONArray array = new JSONArray();
                for (final Node value:elements.values()) {
                    array.add(value.toJson());
                }
                jsonAware = array;

            } else {
                final JSONObject obj = new JSONObject();

                for (final String name:elements.keySet()) {
                    final Collection<Node> values = elements.get(name);

                    if (values.size() == 1) {
                        final Node value = values.iterator().next();
                        obj.put(name, value.toJson());
                    } else if (values.size() > 0) {
                        final JSONArray array = new JSONArray();

                        for (final Node value:values) {
                            array.add(value.toJson());
                        }
                        obj.put(name, array);
                    }
                }

                jsonAware = obj;
            }

            return jsonAware;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("arrayOrCollection", arrayOrCollection)
                    .toString();
        }
    }

    public static class JsonTransformerException extends RuntimeException {

        private static final long serialVersionUID = -6254523548731940786L;

        public JsonTransformerException(String message) {
            super(message);
        }
    }

}
