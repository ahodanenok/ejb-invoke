package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationDescriptor;
import ahodanenok.ejb.invoke.formats.JsonFormat;
import ahodanenok.ejb.invoke.util.IOUtils;
import ahodanenok.ejb.invoke.util.PropertiesUtils;
import ahodanenok.ejb.invoke.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class EjbInvokeCli {

    static {
        setUpLogging();
    }

    private static final Logger LOGGER = Logger.getLogger(EjbInvokeCli.class.getName());

    private static final File CLASSPATH_FILE = new File("classpath");
    private static final File SYSTEM_PROPERTIES_FILE = new File("system.properties");

    private static final int GENERIC_ERROR_CODE = 1;
    private static final int INVOCATION_ERROR_CODE = 2;
    private static final int INVOCATION_DESCRIPTOR_LOADING_ERROR_CODE = 5;
    private static final int USAGE_ERROR_CODE = 10;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("ERROR: provide a file with invocation descriptor");
            System.exit(USAGE_ERROR_CODE);
            return;
        }

        setUpExceptionHandler();
        setUpSystemProperties();
        setUpClassLoader();

        String descriptorPath = args[0];

        JsonFormat format = new JsonFormat();
        EjbInvocationDescriptor descriptor = format.parse(descriptorPath, EjbInvocationDescriptor.class);
        if (descriptor == null) {
            LOGGER.severe("Invocation descriptor is null");
            System.exit(INVOCATION_DESCRIPTOR_LOADING_ERROR_CODE);
            return;
        }

        setUpSystemPropertiesFromDescriptor(descriptor);

        EjbInvokeContext context = new EjbInvokeContext(descriptor.getContextProperties());

        EjbMethod remoteMethod = new EjbMethod(descriptor.getJndiName(), descriptor.getClassName(), descriptor.getMethodName());
        EjbMethodArguments methodArguments = new EjbMethodArguments(descriptor.getArguments());
        EjbMethodResponse response;
        try {
            response = remoteMethod.call(methodArguments, context);
        } catch (EjbInvokeException e) {
            LOGGER.log(Level.SEVERE, "Invocation failed", e);
            System.exit(INVOCATION_ERROR_CODE);
            return;
        }

        if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
            System.out.println(format.format(response.getData()));
        } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
            System.out.println(response.getError().getMessage());
        } else {
            System.out.println("Unknown response status: " + response.getStatus());
        }
    }

    private static void setUpLogging() {
        try {
            LogManager.getLogManager().readConfiguration(EjbInvokeCli.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.out.print("WARN: Couldn't read logging configuration logging, reason: " + e.getMessage());
        }
    }

    private static void setUpExceptionHandler() {
        LOGGER.finer("Setting up default exception handler");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.log(Level.SEVERE, "Error", e);
                System.exit(GENERIC_ERROR_CODE);
            }
        });
    }

    private static void setUpClassLoader() {
        LOGGER.finer("Setting up classpath from config file");

        List<String> classPath = new ArrayList<String>();
        if (CLASSPATH_FILE.exists()) {
            try {
                LOGGER.finer("Reading classpath config file");
                classPath.addAll(IOUtils.getLines(CLASSPATH_FILE));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, String.format("Can't read classpath config file '%s'", CLASSPATH_FILE), e);
            }
        } else {
            LOGGER.config(String.format("file '%s' doesn't exit, skipping", CLASSPATH_FILE));
        }

        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config("Classpath from classpath config file:");
            if (classPath.size() == 0) {
                LOGGER.config("  -- None --");
            }

            for (String path : classPath) {
                LOGGER.config("  " + path);
            }
        }

        Thread.currentThread().setContextClassLoader(
                ReflectionUtils.createClassLoader(classPath, Thread.currentThread().getContextClassLoader()));
    }

    private static void setUpSystemProperties() {
        LOGGER.finer("Setting up system properties");

        if (SYSTEM_PROPERTIES_FILE.exists()) {
            LOGGER.finer("Reading system properties config file");
            Properties fileProperties = PropertiesUtils.fromFile(SYSTEM_PROPERTIES_FILE);
            LOGGER.config("Properties from system properties config file:");
            if (LOGGER.isLoggable(Level.CONFIG) && fileProperties.size() == 0) {
                LOGGER.config("  -- None --");
            }

            for (String prop : fileProperties.stringPropertyNames()) {
                if (LOGGER.isLoggable(Level.CONFIG)) {
                    LOGGER.config(String.format("  %s: %s", prop, fileProperties.getProperty(prop)));
                }

                System.setProperty(prop, fileProperties.getProperty(prop));
            }
        } else {
            LOGGER.config(String.format("file '%s' doesn't exit, skipping", SYSTEM_PROPERTIES_FILE));
        }
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
