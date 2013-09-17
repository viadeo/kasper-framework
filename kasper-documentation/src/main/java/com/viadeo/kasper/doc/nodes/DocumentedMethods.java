// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentedMethods extends ArrayList<DocumentedMethod> {
    private static final long serialVersionUID = -6292190148921922691L;
    private static final Logger LOGGER =LoggerFactory.getLogger(DocumentedMethods.class);

    // ------------------------------------------------------------------------
/*

    DocumentedMethods(final Class<?> componentClazz){
        final List<Method> methods=Lists.newArrayList();
        getAllMethods(methods,componentClazz);

        for (final Method method:methods){
            method.setAccessible(true);

            final String name=method.getName();
            final ArrayList<HashMap<String,String>> parameters=method.getTypeParameters();
            final String returnType=method.getReturnType();
        }
    }
*/

    private static List<Method> getAllMethods(List<Method> methods, final Type type) {
        final Class<?> typeClass= extractClassFromType(type);
        Collections.addAll(methods, typeClass.getDeclaredMethods());

        if (typeClass.getSuperclass()!=null){
            getAllMethods(methods,typeClass.getGenericSuperclass());
        }                                                                                   return null;
    }

    private static Class<?> extractClassFromType(final Type t){
        if (t instanceof Class<?>){
            return (Class<?>)t;
        }
        return (Class<?>)((ParameterizedType)t).getRawType();
    }
}
