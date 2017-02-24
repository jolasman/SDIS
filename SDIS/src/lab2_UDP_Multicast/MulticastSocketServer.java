package lab2_UDP_Multicast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
 
public class MulticastSocketServer {
     
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;
    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        // Get the address that we are going to connect to.
        InetAddress addr = InetAddress.getByName(INET_ADDR);
      
        // Open a new DatagramSocket, which will be used to send the data.
        try (DatagramSocket serverSocket = new DatagramSocket()) {
        	
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			//2. Wait for an incoming data
			System.out.println("Multicast Server socket created. Waiting for incoming data...");
			//communication loop
			while(true)
			{
				serverSocket.receive(incoming);
				byte[] data = incoming.getData();
				String s = new String(data, 0, incoming.getLength());
				

				DatagramPacket dp = new DatagramPacket(s.getBytes() , s.getBytes().length ,
						incoming.getAddress() , incoming.getPort());
				serverSocket.send(dp);
			}
                
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
