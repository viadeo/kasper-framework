package com.viadeo.kasper.index.hadoop.common;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class JavaHelpers {

    public static void addClasspath(final String s) throws Exception {
        final File f = new File(s);
        final URL u = f.toURI().toURL();
        final URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        final Class urlClass = URLClassLoader.class;

        @SuppressWarnings("unchecked")
        final Method method = urlClass.getDeclaredMethod("addURL", new Class[]{ URL.class });
        method.setAccessible(true);
        method.invoke(urlClassLoader, u);
    }

    // ------------------------------------------------------------------------

    public static <O> O cnn(final O obj) {
        return checkNotNull(obj);
    }

    // ------------------------------------------------------------------------

    public static void setEnv(final String key,final String value) throws Exception {
        final Class[] classes = Collections.class.getDeclaredClasses();
        final Map<String, String> env = System.getenv();
        for(final Class cl : classes) {
            if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                final Field field = cl.getDeclaredField("m");
                field.setAccessible(true);

                final Object obj = field.get(env);
                @SuppressWarnings("unchecked")
                final Map<String, String> map = (Map<String, String>) obj;

                map.clear();
                map.put(key, value);
            }
        }
    }

}
