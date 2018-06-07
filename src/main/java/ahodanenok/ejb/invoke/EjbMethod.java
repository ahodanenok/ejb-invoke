package ahodanenok.ejb.invoke;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EjbMethod {

    private static final Logger LOGGER = Logger.getLogger(EjbMethod.class.getName());

    private final String jndiName;
    private final String className;
    private final String methodName;

    public EjbMethod(String jndiName, String className, String methodName) {
        this.jndiName = jndiName;
        this.className = className;
        this.methodName = methodName;
    }

    public EjbMethodResponse call(EjbMethodArguments args, EjbInvokeContext context) throws EjbInvokeException {
        Class ejbClass;
        try {
            LOGGER.info(String.format("Loading EJB class '%s'", className));
            ejbClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.severe(String.format("Ejb class '%s' hasn't been found, please check classpath config", className));
            throw new EjbInvokeException(e);
        }

        Object ejb = context.lookup(jndiName, ejbClass);

        Method ejbMethod;
        try {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("Searching for method '%s' with arguments '%s'",
                        methodName, Arrays.toString(args.getArgumentClasses())));
            }

            ejbMethod = ejb.getClass().getMethod(methodName, args.getArgumentClasses());
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, String.format(
                    "Method '%s' hasn't been found on '%s', please check that method exist and argument types match",
                    methodName, className), e);
            throw new EjbInvokeException(e);
        }

        ServiceLoader<EjbInvocationListener> listeners =
                ServiceLoader.load(EjbInvocationListener.class, Thread.currentThread().getContextClassLoader());

        if (listeners.iterator().hasNext()) {
            LOGGER.info("Running beforeInvoke listeners");
            for (EjbInvocationListener listener : listeners) {
                listener.beforeInvoke(ejbMethod, args.getArguments());
            }
        }

        long invokeStart = System.currentTimeMillis();
        try {
            LOGGER.info(String.format("Invoking EJB method: %s#%s", className, methodName));

            Object result;
            if (ejbMethod.getReturnType() != Void.class) {
                result = ejbMethod.invoke(ejb, args.getArguments());
            } else {
                ejbMethod.invoke(ejb, args.getArguments());
                result = null;
            }

            LOGGER.info("EJB method has been successfully invoked");

            if (listeners.iterator().hasNext()) {
                LOGGER.info("Running afterInvoke listeners");
                for (EjbInvocationListener listener : listeners) {
                    listener.afterInvoke(ejbMethod, result);
                }
            }

            return EjbMethodResponse.success(result, System.currentTimeMillis() - invokeStart);
        } catch (Exception e) {
            LOGGER.info("EJB threw an error: " + e.getMessage());
            return EjbMethodResponse.error(e, System.currentTimeMillis() - invokeStart);
        }
    }
}
