package ahodanenok.ejb.invoke;

public final class EjbMethod {

    private final String jndiName;
    private final String className;
    private final String methodName;

    public EjbMethod(String jndiName, String className, String methodName) {
        this.jndiName = jndiName;
        this.className = className;
        this.methodName = methodName;
    }

    public EjbMethodResponse call(EjbMethodArguments args) {

        // todo: make it impl independent, create something like NamingContextProvider and use spi

        // todo: make some mechanism for registering before invoke intercepters

        // todo: impl
        return null;
    }
}
