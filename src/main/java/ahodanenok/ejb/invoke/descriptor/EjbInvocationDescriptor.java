package ahodanenok.ejb.invoke.descriptor;

import java.util.ArrayList;
import java.util.List;

public final class EjbInvocationDescriptor {

    private String jndiName;
    private String className;
    private String methodName;

    private List<EjbInvocationArgument> arguments = new ArrayList<EjbInvocationArgument>();

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<EjbInvocationArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<EjbInvocationArgument> arguments) {
        this.arguments = arguments;
    }
}
