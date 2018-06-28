package ahodanenok.ejb.invoke.util;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class ReflectionUtils {

    private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

    private ReflectionUtils() { }

    public static ClassLoader createClassLoader(List<String> paths, ClassLoader parent) {
        List<URL> urls = new ArrayList<URL>(paths.size());
        for (int i = 0; i < paths.size(); i++) {

            // todo: expand /*, \*

            try {
                urls.add(new File(paths.get(i)).toURI().toURL());
            } catch (MalformedURLException e) {
                // skipping path if it's not valid
                LOGGER.warning(String.format("path '%s' is not a valid file url", paths.get(i)));
            }
        }

        if (urls.size() > 0) {
            LOGGER.finer("Creating a new classloader");
            return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
        } else {
            LOGGER.finer("Returning parent, urls list is empty");
            return parent;
        }
    }

    public static Type typeForName(String typeName) throws ClassNotFoundException {

        // todo: handle nested type arguments

        Class[] typeArgs = null;
        if (typeName.contains("<")) {
            String[] typeArgsClasses = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">")).split(",");
            typeArgs = new Class[typeArgsClasses.length];
            for (int i = 0; i < typeArgsClasses.length; i++) {
                typeArgs[i] = Class.forName(typeArgsClasses[i].trim(), true, Thread.currentThread().getContextClassLoader());
            }

            typeName = typeName.substring(0, typeName.indexOf("<")).trim();
        }

        if (typeArgs != null) {
            return getType(Class.forName(typeName, true, Thread.currentThread().getContextClassLoader()), typeArgs);
        } else {
            return Class.forName(typeName, true, Thread.currentThread().getContextClassLoader());
        }
    }

    private static Type getType(final Class<?> rawClass, final Class<?>[] parameters) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return parameters;
            }
            @Override
            public Type getRawType() {
                return rawClass;
            }
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
