package cs455.overlay;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Node {

    private final String registryHostname;
    private final int registryPort;
    private final String name;

    private long totalSentMessages,  totalSentSum;

    private int totalMsgsToSend;

    Node(String rhn, int rp, int tm, String n){
        this.registryHostname = rhn; this.registryPort = rp;
        this.totalMsgsToSend = tm;
        this.name = n;
    }

    //! Note: For each msg we write the TYPE of the msg first and then how long the msg actually is
    //! This way the reciever(in this case the registry) will know:
    //!     1. what type of msg and how to reconstruct it
    //!     2. how long or the total number of bytes that make up that particular msg and to be read from the input stream 
    public void sendRegisterMsg(DataOutputStream outStream) throws IOException {
        MessageRegister msg = new MessageRegister(this.name, -1);
        byte[] marshalledMsg = msg.getBytes();
        outStream.writeInt(MessageRegister.TYPE);
        outStream.writeInt(marshalledMsg.length);
        outStream.write(marshalledMsg);
        outStream.flush();

    }

    public void sendMsgs(DataOutputStream outStream) throws IOException, InterruptedException {
        Random randomizer = new Random();
        for (int i = 0; i < this.totalMsgsToSend; i++) {
            long payload = randomizer.nextLong();
            MessagePayload msg = new MessagePayload(i, i, payload, -1, this.name, this.registryPort, this.registryHostname);
            byte[] marshalledMsg = msg.getBytes();
            outStream.writeInt(MessagePayload.TYPE);
            outStream.writeInt(marshalledMsg.length);
            outStream.write(marshalledMsg);
            outStream.flush();

            this.totalSentMessages++;
            this.totalSentSum += payload;
            // Thread.sleep(2000);
        }
        
    }

    public void sendDoneMsg(DataOutputStream outStream) throws IOException {
        MessageDone msg = new MessageDone(this.name, -1);

        byte[] marshalledMsg = msg.getBytes();
        outStream.writeInt(MessageDone.TYPE);
        outStream.writeInt(marshalledMsg.length);
        outStream.write(marshalledMsg);
        outStream.flush();
    }


    // main runner of the node object
    public void runNode() throws IOException, InterruptedException {
        
        Socket registrySocket = new Socket(this.registryHostname, this.registryPort);
        DataOutputStream outReg = new DataOutputStream(registrySocket.getOutputStream());
        DataInputStream registryDIS = new DataInputStream(registrySocket.getInputStream());
        
        

        // send register msg to registry 
        // send n msgs with random payload to registry
        // tell registry message sending is done
        // close socket and outStream
        
        this.sendRegisterMsg(outReg);
        this.sendMsgs(outReg);
        this.sendDoneMsg(outReg);
        Thread nodeRecvThread = new RecvThread(registrySocket, registryDIS, outReg, this);
        nodeRecvThread.start();
        
        outReg.close();
        registrySocket.close();

        


        System.out.printf("Node %s: count: %d, sum: %d\n", this.name, this.totalSentMessages, this.totalSentSum);

    }
}
