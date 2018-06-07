package ahodanenok.ejb.invoke.descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EjbEnvironmentDescriptor {

    private List<String> classpath = new ArrayList<String>();
    private Properties contextProperties = new Properties();
    private Properties systemProperties = new Properties();

    public List<String> getClasspath() {
        return classpath;
    }

    public void setClasspath(List<String> classpath) {
        this.classpath = classpath;
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
}
