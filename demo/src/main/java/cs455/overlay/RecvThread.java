package cs455.overlay;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;




public class RecvThread extends Thread {
    private final DataInputStream clientDIS;
    private final DataOutputStream clientDOS;
    private final Socket clientSocket;
    private Registry registry = null;
    private Node msgNode=null;


    public RecvThread(Socket s, DataInputStream dis, DataOutputStream dos, Registry reg){
        this.clientSocket = s; this.clientDIS = dis; this.clientDOS = dos;
        this.registry = reg;
    }
    public RecvThread(Socket s, DataInputStream dis, DataOutputStream dos, Node msgNode){
        this.clientSocket = s; this.clientDIS = dis; this.clientDOS = dos;
        this.msgNode = msgNode;
    }
    
    @Override
    public void run() {
        int type = 0;
        int sizeMsg = 0;
        while (type != 3){
            // while we do not get the done message we keep on reading msgs from the node
            try {
                // checks the type of message received and the number of bytes that make up that message.
                // clientDIS.readFully takes a byte array(read buffer) of the size of the msg and reads all those bytes in the buffer.
                // the buffer can then be passed to the byte array constructor for each of the messages to be reconstructed. 
                type = this.clientDIS.readInt();
                sizeMsg = this.clientDIS.readInt();
                if (type == 1){ 
                    // nodes say hello to registry (Register)
                    byte[] b = new byte[sizeMsg];
                    clientDIS.readFully(b, 0, sizeMsg);

                    MessageRegister regMsg = new MessageRegister(b);
                    regMsg.printContents();
                    this.registry.sendRegisterResponse(this.clientDOS);


                } else if(type == 2) {
                    // nodes send payload to registry (Payload)
                    byte[] b = new byte[sizeMsg];
                    clientDIS.readFully(b, 0, sizeMsg);

                    MessagePayload payloadMsg = new MessagePayload(b);


                    // These are two ways you can synchronize how to add payloads. More details can be found in Registry.java 
                    this.registry.updatePayloads(payloadMsg.payload);
                    this.registry.updateTotals(payloadMsg.payload);
                    payloadMsg.printContents();
                } else if(type == 3) {
                    // nodes tell registry they are done messaging (Done/Deregister)
                    byte[] b = new byte[sizeMsg];
                    clientDIS.readFully(b, 0, sizeMsg);
                    
                } 
                else if (type == 4){ 
                    // nodes say hello to registry (Register)
                    byte[] b = new byte[sizeMsg];
                    clientDIS.readFully(b, 0, sizeMsg);

                    MessageRegisterResponse regResMsg = new MessageRegisterResponse(b);
                    regResMsg.printContents();


                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            clientDIS.close();
            clientDOS.close();
            clientSocket.close();
    
        } catch (IOException e) {
            e.printStackTrace();
        }
  
    }





}
