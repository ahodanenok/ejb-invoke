package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.util.PropertiesUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EjbInvokeContext {

    private static final Logger LOGGER = Logger.getLogger(EjbInvokeContext.class.getName());

    private static final File PROPERTIES_FILE = new File("context.properties");

    private InitialContext context;
    private Properties properties;

    public EjbInvokeContext(Properties properties) {
        if (PROPERTIES_FILE.exists()) {
            this.properties = PropertiesUtils.fromFile(PROPERTIES_FILE);
        } else {
            this.properties = new Properties();
        }

        if (properties != null) {
            for (String prop : properties.stringPropertyNames()) {
                this.properties.setProperty(prop, properties.getProperty(prop));
            }
        }
    }

    public <T> T lookup(String name, Class<T> objectClass) throws EjbInvokeException {
        initContext();

        try {
            LOGGER.info(String.format("Looking up object '%s'", name));
            Object obj = context.lookup(name);
            return objectClass.cast(PortableRemoteObject.narrow(obj, objectClass));
        } catch (Exception e) {
            LOGGER.severe(String.format("Couldn't lookup object '%s' with type '%s', reason: %s",
                    name, objectClass.getName(), e.getMessage()));
            throw new EjbInvokeException(e);
        }
    }

    private void initContext() {
        if (context != null) {
            return;
        }

        try {
            LOGGER.info("Creating initial context");
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config("Context properties:");
                for (String prop : properties.stringPropertyNames()) {
                    LOGGER.config(String.format("  %s: %s", prop, properties.getProperty(prop)));
                }
            }

            this.context = new InitialContext(properties);
        } catch (NamingException e) {
            LOGGER.severe(String.format("Couldn't create initial context, reason: %s", e.getMessage()));
            throw new EjbInvokeException(e);
        }
    }
}
