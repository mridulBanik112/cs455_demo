package cs455.overlay;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class Registry {
    final private String hostname;
    final private int port;
    private long totalCount;
    private long totalSum;
    
    private SummaryData data = new SummaryData();

    private int connectedNodesSoFar = 0;
    private int totalNodes;
    private boolean done = false;
    private final ArrayList<Thread> allThreads;

    Registry(String hn, int p, int totalNs){
        this.hostname = hn;
        this.port = p;
        this.totalNodes  = totalNs;
        this.allThreads = new ArrayList<>(0);
    }

    // The synchronized keyword here specifies that 
    // only one thread can run this registry.updateTotals(payload) at a time
    // Note: here the lock is on the entire function i.e. no two threads can 
    // read/write totalSum and totalCount at the same time, and threads about to call 
    // this function will have to wait in a queue while another thread is executing updateTotals
    public synchronized void updateTotals(long payload){
        this.totalSum += payload;
        this.totalCount ++;
    }

    // The synchronized keyword here specifies that 
    // only one thread can run the synchronized block at a time
    // Note: here the lock is on the data object only i.e. any number of threads can 
    // be executing the registry.updatedPayloads(payload) function, but
    // only one thread can read/write the data object at a time, and threads currently executing this 
    // this function will have to wait in a queue while another thread is currently in the synchronized block
    // Note: this method is better if you need to do some (pre/post)processing on the payload that is 
    // guarenteed to not have any race conditions with other variables. 
    public void updatePayloads(long payload){

        // may do preprocessing  
        synchronized(data){
            data.totalSum += payload;
            data.totalCount++;
        }
        // may do postprocessing
    }
    public void sendRegisterResponse(DataOutputStream outStream) throws IOException, InterruptedException {
        // MessageRegister msg = new MessageRegister(this.name, -1);
        // byte[] marshalledMsg = msg.getBytes();
        // outStream.writeInt(MessageRegister.TYPE);
        // outStream.writeInt(marshalledMsg.length);


        Random randomizer = new Random();
        int id = randomizer.nextInt();

        MessageRegisterResponse msg = new MessageRegisterResponse(id);
        byte[] marshalledMsg = msg.getBytes();
        outStream.writeInt(MessageRegisterResponse.TYPE);
        outStream.writeInt(marshalledMsg.length);
        outStream.write(marshalledMsg);
        outStream.flush();
        //this.totalSentMessages++;
        //this.totalSentSum += payload;
        // Thread.sleep(2000);
    }
        

    public void runRegistry() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(this.port, 100);

        System.out.println("Registry has started listening for connections");
        
        while (! this.done) {
            // accept a connection, create a receiver thread and start it. append the thread to a list
            // of spawned threads, and update the total number of connected nodes so far.
            // will keep accepting connections till we haven't connected to n nodes so far.
            Socket clientSocket = serverSocket.accept();
            DataInputStream clientDIS = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream clientDOS = new DataOutputStream(clientSocket.getOutputStream());
            Thread recvThread = new RecvThread(clientSocket, clientDIS, clientDOS, this);
            allThreads.add(recvThread);
            recvThread.start();

            this.connectedNodesSoFar ++;
            this.done = this.connectedNodesSoFar == this.totalNodes;
        }

        serverSocket.close();

        // wait for each thread to complete its execution  by calling the join method on it. 
        for (int i =0; i < this.totalNodes; i++){
            allThreads.get(i).join();
        }

        // prints the total count and sum for the two different synchronization methods. 
        System.out.printf("Registry: Total count messages: %d, Total sum messages %d\n", this.totalCount, this.totalSum);
        System.out.printf("Registry: Total count messages: %d, Total sum messages %d\n", this.data.totalCount, this.data.totalSum);
    }
}
