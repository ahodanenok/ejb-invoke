package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbEnvironmentDescriptor;
import ahodanenok.ejb.invoke.descriptor.EjbInvocationDescriptor;
import ahodanenok.ejb.invoke.formats.JsonFormat;
import ahodanenok.ejb.invoke.util.ReflectionUtils;

import java.io.IOException;
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

    private static final int GENERIC_ERROR_CODE = 1;
    private static final int INVOCATION_ERROR_CODE = 2;
    private static final int ENVIRONMENT_DESCRIPTOR_LOADING_ERROR_CODE = 5;
    private static final int INVOCATION_DESCRIPTOR_LOADING_ERROR_CODE = 6;
    private static final int USAGE_ERROR_CODE = 10;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ejbInvoke <env-descriptor-path> <invocation-descriptor-path>");
            System.exit(USAGE_ERROR_CODE);
            return;
        }

        setUpExceptionHandler();

        JsonFormat format = new JsonFormat();

        String envDescriptorPath = args[0];
        EjbEnvironmentDescriptor envDescriptor = format.parse(envDescriptorPath, EjbEnvironmentDescriptor.class);
        if (envDescriptor == null) {
            LOGGER.severe("Invocation descriptor is null");
            System.exit(ENVIRONMENT_DESCRIPTOR_LOADING_ERROR_CODE);
            return;
        }

        setUpSystemPropertiesFromDescriptor(envDescriptor);
        setUpClassLoaderFromDescriptor(envDescriptor);

        String invocationDescriptorPath = args[1];
        EjbInvocationDescriptor invocationDescriptor = format.parse(invocationDescriptorPath, EjbInvocationDescriptor.class);
        if (invocationDescriptor == null) {
            LOGGER.severe("Invocation descriptor is null");
            System.exit(INVOCATION_DESCRIPTOR_LOADING_ERROR_CODE);
            return;
        }

        try {
            EjbMethodResponse response = EjbInvoke.call(envDescriptor.getContextProperties(), invocationDescriptor);

            if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
                System.out.println(format.format(response.getData()));
            } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
                System.out.println(response.getError().getMessage());
            } else {
                System.out.println("Unknown response status: " + response.getStatus());
            }
        } catch (EjbInvokeException e) {
            LOGGER.log(Level.SEVERE, "Invocation failed, reason: " + e.getMessage(), e);
            System.exit(INVOCATION_ERROR_CODE);
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
                LOGGER.log(Level.SEVERE, "Uncaught error: " + e.getMessage(), e);
                System.exit(GENERIC_ERROR_CODE);
            }
        });
    }

    private static void setUpClassLoaderFromDescriptor(EjbEnvironmentDescriptor descriptor) {
        LOGGER.finer("Setting up classpath from config file");

        List<String> classpath = descriptor.getClasspath();
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config("Classpath from classpath config file:");
            if (classpath.size() == 0) {
                LOGGER.config("  -- None --");
            }

            for (String path : classpath) {
                LOGGER.config("  " + path);
            }
        }

        Thread.currentThread().setContextClassLoader(
                ReflectionUtils.createClassLoader(classpath, Thread.currentThread().getContextClassLoader()));
    }

    private static void setUpSystemPropertiesFromDescriptor(EjbEnvironmentDescriptor descriptor) {
        Properties properties = descriptor.getSystemProperties();

        LOGGER.config("System properties from invocation descriptor:");
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
