package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationDescriptor;

import java.util.Properties;

public final class EjbInvoke {

    private EjbInvoke() { }

    public static EjbMethodResponse call(Properties contextProperties, EjbInvocationDescriptor descriptor) {
        EjbInvokeContext context = new EjbInvokeContext(contextProperties);
        EjbMethod remoteMethod = new EjbMethod(descriptor.getJndiName(), descriptor.getClassName(), descriptor.getMethodName());
        EjbMethodArguments methodArguments = new EjbMethodArguments(descriptor.getArguments());
        return remoteMethod.call(methodArguments, context);
    }
}
