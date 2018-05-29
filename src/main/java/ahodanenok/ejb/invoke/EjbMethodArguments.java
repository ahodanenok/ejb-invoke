package ahodanenok.ejb.invoke;

import java.util.ArrayList;
import java.util.List;

public final class EjbMethodArguments {

    public static EjbMethodArguments parseFile(String filePath) {
        // todo: impl
        return new EjbMethodArguments();
    }

    private List<Object> arguments;

    private EjbMethodArguments() {
        this.arguments = new ArrayList<Object>();
    }

    public Object getArgument(int pos) {
        // todo: out of bounds
        return arguments.get(pos);
    }

    public Object[] getArguments() {
        return arguments.toArray();
    }

    public Class[] getArgumentClasses() {
        return new Class[0];
    }
}
