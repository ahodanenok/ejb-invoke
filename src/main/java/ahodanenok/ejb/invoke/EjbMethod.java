package ahodanenok.ejb.invoke;

import java.lang.reflect.Method;

public final class EjbMethod {

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
            ejbClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }

        Object ejb = context.lookup(jndiName, ejbClass);

        Method ejbMethod;
        try {
            ejbMethod = ejb.getClass().getMethod(methodName, args.getArgumentClasses());
        } catch (NoSuchMethodException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }

        long invokeStart = System.currentTimeMillis();
        try {
            Object result = ejbMethod.invoke(ejb, args.getArguments());
            return EjbMethodResponse.success(result, System.currentTimeMillis() - invokeStart);
        } catch (Exception e) {
            // todo: log
            return EjbMethodResponse.error(e, System.currentTimeMillis() - invokeStart);
        }
    }
}
