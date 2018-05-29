package ahodanenok.ejb.invoke.descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class EjbInvocationDescriptor {

    private String jndiName;
    private String className;
    private String methodName;

    private List<EjbInvocationArgument> arguments = new ArrayList<EjbInvocationArgument>();

    private Properties contextProperties;
    private Properties systemProperties;
    private List<String> classPath = new ArrayList<String>();

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

    public Properties getContextProperties() {
        return contextProperties;
    }

    public void setContextProperties(Properties contextProperties) {
        this.contextProperties = contextProperties;
    }

    public Properties getSystemProperties() {
        return systemProperties;
    }

    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }

    public List<String> getClassPath() {
        return classPath;
    }

    public void setClassPath(List<String> classPath) {
        this.classPath = classPath;
    }
}
