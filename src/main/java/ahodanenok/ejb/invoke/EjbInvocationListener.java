package ahodanenok.ejb.invoke;

import java.lang.reflect.Method;

public interface EjbInvocationListener {

    void beforeInvoke(Method method, Object[] arguments);

    void afterInvoke(Method method, Object result);
}
