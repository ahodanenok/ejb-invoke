package ahodanenok.ejb.invoke;

public class EjbInvokeException extends RuntimeException {

    public EjbInvokeException(String message) {
        super(message);
    }

    public EjbInvokeException(Throwable cause) {
        super(cause);
    }
}
