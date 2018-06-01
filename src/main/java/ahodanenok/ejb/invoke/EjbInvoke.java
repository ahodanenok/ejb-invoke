package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationDescriptor;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EjbInvoke {

    private static final Logger LOGGER = Logger.getLogger(EjbInvoke.class.getName());

    private EjbInvoke() { }

    public static EjbMethodResponse call(EjbInvocationDescriptor descriptor) {
        setUpSystemPropertiesFromDescriptor(descriptor);

        EjbInvokeContext context = new EjbInvokeContext(descriptor.getContextProperties());
        EjbMethod remoteMethod = new EjbMethod(descriptor.getJndiName(), descriptor.getClassName(), descriptor.getMethodName());
        EjbMethodArguments methodArguments = new EjbMethodArguments(descriptor.getArguments());
        return remoteMethod.call(methodArguments, context);
    }

    private static void setUpSystemPropertiesFromDescriptor(EjbInvocationDescriptor descriptor) {
        Properties properties = descriptor.getSystemProperties();

        LOGGER.config("Properties from invocation descriptor:");
        if (LOGGER.isLoggable(Level.CONFIG) && (properties == null || properties.size() == 0)) {
            LOGGER.config("  -- None --");
        }

        if (properties != null) {
            // override with properties from descriptor
            for (String prop : properties.stringPropertyNames()) {
                if (LOGGER.isLoggable(Level.CONFIG)) {
                    LOGGER.config(String.format("  %s: %s", prop, properties.getProperty(prop)));
                }

                System.setProperty(prop, properties.getProperty(prop));
            }
        }
    }
}
