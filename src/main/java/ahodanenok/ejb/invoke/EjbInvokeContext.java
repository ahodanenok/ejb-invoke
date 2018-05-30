package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.util.PropertiesUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.util.Properties;

public class EjbInvokeContext {

    private static final String PROPERTIES_FILE_NAME = "context.properties";

    private InitialContext context;
    private Properties properties;

    public EjbInvokeContext(Properties properties) {
        this.properties = PropertiesUtils.fromFile(PROPERTIES_FILE_NAME);
        if (properties != null) {
            for (String prop : properties.stringPropertyNames()) {
                this.properties.setProperty(prop, properties.getProperty(prop));
            }
        }
    }

    public <T> T lookup(String name, Class<T> objectClass) throws EjbInvokeException {
        initContext();

        try {
            Object obj = context.lookup(name);
            return objectClass.cast(PortableRemoteObject.narrow(obj, objectClass));
        } catch (NamingException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }
    }

    private void initContext() {
        if (context != null) {
            return;
        }

        try {
            this.context = new InitialContext(properties);
        } catch (NamingException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }
    }
}
