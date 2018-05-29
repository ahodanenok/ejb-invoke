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

    public static void main(String[] args) {
        if (args.length == 0) {
            // todo: show message
            return;
        }

        String descriptorPath = args[0];

        EjbInvocationDescriptor descriptor = new JsonFormat().parse(descriptorPath, EjbInvocationDescriptor.class);
        if (descriptor == null) {
            // todo: code
            System.exit(-1);
            return;
        }

        setUpSystemProperties(descriptor.getSystemProperties());
        setUpClassLoader(descriptor.getClassPath());

        EjbInvokeContext context = new EjbInvokeContext(descriptor.getContextProperties());

        EjbMethod remoteMethod = new EjbMethod(descriptor.getJndiName(), descriptor.getClassName(), descriptor.getMethodName());
        EjbMethodArguments methodArguments = new EjbMethodArguments(descriptor.getArguments());
        EjbMethodResponse response = remoteMethod.call(methodArguments, context);

        if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
            System.out.println(response.getData());
        } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
            System.out.println(response.getError().getMessage());
        } else {
            System.out.println("Unknown response status: " + response.getStatus());
        }
    }

    private static void setUpClassLoader(List<String> paths) {
        try {
            List<String> classPath = new ArrayList<String>(IOUtils.getLines(CLASSPATH_FILE));
            if (paths != null) {
                classPath.addAll(paths);
            }

            RefectionUtils.createClassLoader(classPath, Thread.currentThread().getContextClassLoader());
        } catch (IOException e) {
            // todo: log
        }
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
