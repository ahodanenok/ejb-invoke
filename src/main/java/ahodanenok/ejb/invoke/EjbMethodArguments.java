package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationArgument;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class EjbMethodArguments {

    private List<EjbInvocationArgument> arguments;

    public EjbMethodArguments(List<EjbInvocationArgument> arguments) {
        this.arguments = new ArrayList<EjbInvocationArgument>(arguments);
    }

    public Object[] getArguments() {
        Object[] objects = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            EjbInvocationArgument arg = arguments.get(i);
            objects[i] = arg.getValue();
        }

        return objects;
    }

    public Class[] getArgumentClasses() {
        Class[] classes = new Class[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            EjbInvocationArgument arg = arguments.get(i);
            Type argType = arg.getType();
            if (argType instanceof Class) {
                classes[i] = (Class) argType;
            } else if (argType instanceof ParameterizedType) {
                classes[i] = (Class) ((ParameterizedType) argType).getRawType();
            } else {
                throw new IllegalStateException("Unknown type for argument: " + argType);
            }
        }

        return classes;
    }
}
