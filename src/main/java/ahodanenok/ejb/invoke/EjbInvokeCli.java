package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationDescriptor;
import ahodanenok.ejb.invoke.formats.JsonFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class EjbInvokeCli {

    private static final String CLASSPATH_FILE = "classpath";
    private static final String SYSTEM_PROPERTIES_FILE = "system.properties";

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

        String descriptorPath = args[0];

        EjbInvocationDescriptor descriptor = new JsonFormat().parse(descriptorPath, EjbInvocationDescriptor.class);
        if (descriptor == null) {
            System.exit(INVOCATION_DESCRIPTOR_LOADING_ERROR_CODE);
            return;
        }

        setUpSystemProperties(descriptor.getSystemProperties());
        setUpClassLoader(descriptor.getClassPath());
        setUpExceptionHandler();

        EjbInvokeContext context = new EjbInvokeContext(descriptor.getContextProperties());

        EjbMethod remoteMethod = new EjbMethod(descriptor.getJndiName(), descriptor.getClassName(), descriptor.getMethodName());
        EjbMethodArguments methodArguments = new EjbMethodArguments(descriptor.getArguments());
        EjbMethodResponse response;
        try {
            response = remoteMethod.call(methodArguments, context);
        } catch (EjbInvokeException e) {
            System.exit(INVOCATION_ERROR_CODE);
            return;
        }

        if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
            System.out.println(response.getData());
        } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
            System.out.println(response.getError().getMessage());
        } else {
            System.out.println("Unknown response status: " + response.getStatus());
        }
    }

    private static void setUpExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // todo: log
                System.exit(GENERIC_ERROR_CODE);
            }
        });
    }

    private static void setUpClassLoader(List<String> paths) {
        List<String> classPath = new ArrayList<String>();
        try {
            paths.addAll(IOUtils.getLines(CLASSPATH_FILE));
        } catch (IOException e) {
            // todo: log
        }

        if (paths != null) {
            classPath.addAll(paths);
        }

        Thread.currentThread().setContextClassLoader(
                RefectionUtils.createClassLoader(classPath, Thread.currentThread().getContextClassLoader()));
    }

    private static void setUpSystemProperties(Properties properties) {
        for (String prop : PropertiesUtils.fromFile(SYSTEM_PROPERTIES_FILE).stringPropertyNames()) {
            System.setProperty(prop, properties.getProperty(prop));
        }

        if (properties != null) {
            for (String prop : properties.stringPropertyNames()) {
                System.setProperty(prop, properties.getProperty(prop));
            }
        }
    }
}
