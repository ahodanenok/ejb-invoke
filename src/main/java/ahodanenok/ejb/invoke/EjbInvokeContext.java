package ahodanenok.ejb.invoke;

import ahodanenok.ejb.sample.SampleEJBRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.util.Properties;

public class EjbInvokeContext {

    private static final String PROPERTIES_FILE_NAME = "context.properties";

    private InitialContext context;

    public EjbInvokeContext() { }

    public <T> T lookup(String name, Class<T> objectClass) throws EjbInvokeException {
        initContext();

        try {
            Object obj = context.lookup(name);
            return objectClass.cast(PortableRemoteObject.narrow(obj, objectClass));
        } catch (NamingException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }
    }

    private void initContext() {
        if (context != null) {
            return;
        }

        try {
            Properties properties = PropertiesUtils.fromFile(PROPERTIES_FILE_NAME);
//            properties.setProperty(InitialContext.PROVIDER_URL, "corbaloc:iiop:localhost:2809");///NameService");
//            properties.setProperty(InitialContext.PROVIDER_URL, "iiop://localhost:2809");///NameService");
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
//            properties.put(Context.PROVIDER_URL, "ejbd://localhost:4201");
            properties.put(Context.PROVIDER_URL, "http://127.0.0.1:8080/tomee/ejb");

            this.context = new InitialContext(properties);
        } catch (NamingException e) {
            // todo: log
            throw new EjbInvokeException(e);
        }
    }
}
