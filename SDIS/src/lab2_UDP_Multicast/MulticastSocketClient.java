package lab2_UDP_Multicast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.io.*;
import java.net.*;

public class MulticastSocketClient {
    
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException {
        // Get the address that we are going to connect to.
        InetAddress address = InetAddress.getByName(INET_ADDR);
        
        // Create a buffer of bytes, which will be used to store
        // the incoming bytes containing the information from the server.
        // Since the message is small here, 256 bytes should be enough.
        byte[] buf = new byte[256];
        
        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
        try (MulticastSocket clientSocket = new MulticastSocket(PORT)){
            //Joint the Multicast group.
            clientSocket.joinGroup(address);
            String s;
    		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

     
            while (true) {
            	
            	//take input and send the packet
            	 System.out.println("Multicast --> Enter message to send : ");
				
				s = (String)cin.readLine();
				byte[] b = s.getBytes();

				DatagramPacket  dp = new DatagramPacket(b , b.length , address , PORT);
				clientSocket.send(dp);

				//now receive reply
				//buffer to receive incoming data
				byte[] buffer = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				clientSocket.receive(reply);

				byte[] data = reply.getData();
				s = new String(data, 0, reply.getLength());

				//echo the details of incoming data - client ip : client port - client message
				System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);
			}
         
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
