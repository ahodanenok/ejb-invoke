package ahodanenok.ejb.invoke;

public final class EjbInvokeCli {

    public static void main(String[] args) {

        // todo: get these from args

//            Object found = PortableRemoteObject.narrow(context.lookup("corbaloc:iiop:localhost:2809/NameService#ejb/global/ear-1\\.0-SNAPSHOT/ahodanenok\\.ejb-ejb-1\\.0-SNAPSHOT/SampleEJB!ahodanenok\\.ejb\\.sample\\.SampleEJBRemote"), SampleEJBRemote.class);
//            Object found = PortableRemoteObject.narrow(context.lookup("corbaname:iiop:localhost:2809#ejb/global/ear-1\\.0-SNAPSHOT/ahodanenok\\.ejb-ejb-1\\.0-SNAPSHOT/SampleEJB!ahodanenok\\.ejb\\.sample\\.SampleEJBRemote"), SampleEJBRemote.class);
//            Object found = PortableRemoteObject.narrow(context.lookup("#ejb/global/ear-1.0-SNAPSHOT/ahodanenok.ejb-ejb-1.0-SNAPSHOT/SampleEJB!ahodanenok.ejb.sample.SampleEJBRemote"), SampleEJBRemote.class);

        String jndiName = "global/ear-1.0-SNAPSHOT/ahodanenok.ejb-ejb-1.0-SNAPSHOT/SampleEJB!ahodanenok.ejb.sample.SampleEJBRemote";
        String className = "ahodanenok.ejb.sample.SampleEJBRemote";
        String methodName = "remoteMethod";

        /*

        [
          {
            type: boolean,
            value: true
          },
          {
            type: java.util.List,
            value: [0, 1, 2, 3, 4]
          }
        ]

         */
        String argsFilePath = "args.json";


        // todo: register ClassLoader with Ejb implementations/interfaces


        EjbInvokeContext context = new EjbInvokeContext();

        EjbMethod remoteMethod = new EjbMethod(jndiName, className, methodName);
        EjbMethodArguments methodArguments = EjbMethodArguments.parseFile(argsFilePath);
        EjbMethodResponse response = remoteMethod.call(methodArguments, context);

        if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
            System.out.println(response.getData());
        } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
            System.out.println(response.getError().getMessage());
        } else {
            System.out.println("Unknown response status: " + response.getStatus());
        }
    }
}
