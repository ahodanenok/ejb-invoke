package ahodanenok.ejb.invoke;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class RefectionUtils {

    private RefectionUtils() { }

    public List<Class> toClasses(List<String> classNames) {
        Class[] params = new Class[argClassNames.length];
        try {
            LOGGER.fine("Loading argument classes");
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("Argument classes: " + Arrays.toString(argClassNames));
            }

            String argClass;
            for (int i = 0; i < argClassNames.length; i++) {
                argClass = argClassNames[i];
                if ("double".equals(argClass)) {
                    params[i] = double.class;
                } else if ("float".equals(argClass)) {
                    params[i] = float.class;
                } else if ("byte".equals(argClass)) {
                    params[i] = byte.class;
                } else if ("short".equals(argClass)) {
                    params[i] = short.class;
                } else if ("int".equals(argClass)) {
                    params[i] = int.class;
                } else if ("long".equals(argClass)) {
                    params[i] = long.class;
                } else if ("boolean".equals(argClass)) {
                    params[i] = boolean.class;
                } else {
                    params[i] = Class.forName(argClass, true, context.getHECClassLoader());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Invocation failed", e);
            throw new HECException("Can't load argument classes", e);
        }
    }
}
