package ahodanenok.ejb.invoke;

import java.lang.reflect.Method;
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

        // todo: make it impl independent, create something like NamingContextProvider and use spi
        // todo: make some mechanism for registering before invoke intercepters

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
            LOGGER.info(String.format("Searching for method '%s'", methodName));
            ejbMethod = ejb.getClass().getMethod(methodName, args.getArgumentClasses());
        } catch (NoSuchMethodException e) {
            LOGGER.severe(String.format(
                    "Method '%s' hasn't been found on '%s', please check that method exist and argument types match",
                    methodName, className));
            throw new EjbInvokeException(e);
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
            return EjbMethodResponse.success(result, System.currentTimeMillis() - invokeStart);
        } catch (Exception e) {
            LOGGER.info("EJB threw an error: " + e.getMessage());
            return EjbMethodResponse.error(e, System.currentTimeMillis() - invokeStart);
        }
    }
}
