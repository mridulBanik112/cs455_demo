package cs455.overlay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageRegisterResponse {
    public static final int TYPE = 4;
	public int id;

	public MessageRegisterResponse( int id){
		 this.id = id;
	}
    public MessageRegisterResponse(byte[] marshalledBytes) throws IOException{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(byteArrayInputStream));
		//int hostNameLength = din.readInt();
		//byte[] hostNameBytes = new byte[hostNameLength];
		//din.readFully(hostNameBytes);
		//String hn = new String(hostNameBytes);
		int id = din.readInt();
		byteArrayInputStream.close();
		din.close();
		this.id = id;
	}

    public byte[] getBytes() throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

		//byte[] hostnameBytes = this.hostName.getBytes();
		//int id = id;
		dout.writeInt(id);
		//dout.write(hostnameBytes);
		//dout.writeInt(this.portNumber);
		dout.flush();

		byte[] marshalledBytes = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		dout.close();

		return marshalledBytes;
	}
    public void printContents(){
		System.out.printf("ID %s \n", this.id);
	}
}
