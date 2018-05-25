package ahodanenok.ejb.invoke;

public final class EjbInvokeCli {

    public static void main(String[] args) {

        // todo: get these from args
        String jndiName = "";
        String className = "";
        String methodName = "";

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


        EjbMethod remoteMethod = new EjbMethod(jndiName, className, methodName);
        EjbMethodArguments methodArguments = EjbMethodArguments.parseFile(argsFilePath);
        EjbMethodResponse response = remoteMethod.call(methodArguments);

        if (response.getStatus() == EjbMethodResponse.Status.SUCCESS) {
            System.out.println(response.getData());
        } else if (response.getStatus() == EjbMethodResponse.Status.ERROR) {
            System.out.println(response.getError().getMessage());
        } else {
            System.out.println("Unknown response status: " + response.getStatus());
        }
    }
}
