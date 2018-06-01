package ahodanenok.ejb.invoke.util;

import java.io.File;
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
}
