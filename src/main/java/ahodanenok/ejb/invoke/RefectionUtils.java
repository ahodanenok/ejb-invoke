package ahodanenok.ejb.invoke;

import java.util.ArrayList;
import java.util.List;

public final class RefectionUtils {

    private RefectionUtils() { }

    public List<Class> toClasses(List<String> classNames) {
        try {
            List<Class> classes = new ArrayList<Class>(classNames.size());

            String argClass;
            for (int i = 0; i < classNames.size(); i++) {
                argClass = classNames.get(i);
                if ("double".equals(argClass)) {
                    classes.add(double.class);
                } else if ("float".equals(argClass)) {
                    classes.add(float.class);
                } else if ("byte".equals(argClass)) {
                    classes.add(byte.class);
                } else if ("short".equals(argClass)) {
                    classes.add(short.class);
                } else if ("int".equals(argClass)) {
                    classes.add(int.class);
                } else if ("long".equals(argClass)) {
                    classes.add(long.class);
                } else if ("boolean".equals(argClass)) {
                    classes.add(boolean.class);
                } else {
                    classes.add(Class.forName(argClass, true, Thread.currentThread().getContextClassLoader()));
                }
            }

            return classes;
        } catch (Exception e) {
            throw new RuntimeException("Can't load classes", e);
        }
    }
}
