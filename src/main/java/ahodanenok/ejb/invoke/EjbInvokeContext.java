package ahodanenok.ejb.invoke;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EjbInvokeContext {

    private static final Logger LOGGER = Logger.getLogger(EjbInvokeContext.class.getName());

    private InitialContext context;
    private Properties properties;

    public EjbInvokeContext(Properties properties) {
        this.properties = new Properties();

        if (properties != null) {
            for (String prop : properties.stringPropertyNames()) {
                this.properties.setProperty(prop, properties.getProperty(prop));
            }
        }
    }

    public <T> T lookup(String name, Class<T> objectClass) throws EjbInvokeException {
        initContext();

        T stub = null;
        try {
            LOGGER.info(String.format("Looking up object '%s'", name));
            Object obj = context.lookup(name);
            stub = objectClass.cast(PortableRemoteObject.narrow(obj, objectClass));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Couldn't lookup object '%s' with type '%s', reason: %s",
                    name, objectClass.getName(), e.getMessage()), e);
            throw new EjbInvokeException(e);
        }

        if (stub == null) {
            LOGGER.severe("Ejb was found, but PortableRemoteObject.narrow returned null, " +
                    "it could be because ejb stubs are not in classpath," +
                    " please make sure they are present in classpath");
            throw new EjbInvokeException("Couldn't load stub");
        }

        return stub;
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
            LOGGER.log(Level.SEVERE, String.format("Couldn't create initial context, reason: %s", e.getMessage()), e);
            throw new EjbInvokeException(e);
        }
    }
}
