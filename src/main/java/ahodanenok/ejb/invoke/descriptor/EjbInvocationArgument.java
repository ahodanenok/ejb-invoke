package ahodanenok.ejb.invoke.descriptor;

import java.lang.reflect.Type;

public final class EjbInvocationArgument {

    private Object value;
    private Type type;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
