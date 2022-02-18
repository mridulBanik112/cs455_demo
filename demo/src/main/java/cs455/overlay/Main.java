package cs455.overlay;


import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Invalid number of args");
            System.out.println("Usage for registry => java -cp <jarfilepath> <main_class> registry <hostname> <port> <number of nodes>");
            System.out.println("Usage for node => java -cp <jarfilepath> <main_class> node <hostname> <registry_hostname> <registry_port> <num_messages>");
            return;
        }

        try{
            // extracts type of node(registry or regular "node").
            String type = args[0];
            
            if(Objects.equals(type, "registry")){
                // if type is registry then extracts the number of nodes, hostname and port;
                // creates the registry node and calls its runMethod
                String hostname = args[1];
                int port = Integer.parseInt(args[2]);
                int numNodes = Integer.parseInt(args[3]);
                
                Registry registry = new Registry(hostname, port, numNodes);
                registry.runRegistry();
            } else if (Objects.equals(type, "node")) {
                // if type == node then gets the registry hostname and port from the arguments and the number of msgs to send
                // creates the node and calls it runMethod
                String hostname = args[1];
                String registryHostname = args[2];
                int registryPort = Integer.parseInt(args[3]);
                int numMsgs = Integer.parseInt(args[4]);
                Node node = new Node(registryHostname, registryPort, numMsgs, hostname);
                node.runNode();
            } else {
                //invalid type supplied
                System.out.println("Invalid type supplied in argument");
            }
        } catch (Exception e) {
            System.out.println("Invalid arguments supplied");
            System.out.println("Usage for registry => java -cp <jarfilepath> <main_class> registry <hostname> <port> <number of nodes>");
            System.out.println("Usage for node => java -cp <jarfilepath> <main_class> node <hostname> <registry_hostname> <registry_port> <num_messages>");
            e.printStackTrace();
        }


    }



}
