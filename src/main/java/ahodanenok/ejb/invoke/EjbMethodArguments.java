package ahodanenok.ejb.invoke;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationArgument;

import java.util.ArrayList;
import java.util.List;

public final class EjbMethodArguments {

    private List<Object> arguments;

    public EjbMethodArguments(List<EjbInvocationArgument> arguments) {
        this.arguments = new ArrayList<Object>();
        for (EjbInvocationArgument arg : arguments) {
            this.arguments.add(arg.getValue());
        }
    }

    public Object[] getArguments() {
        return arguments.toArray();
    }

    public Class[] getArgumentClasses() {
        Class[] classes = new Class[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            classes[i] = arguments.get(i).getClass();
        }

        return classes;
    }
}
