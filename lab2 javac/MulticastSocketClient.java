import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastSocketClient {


	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("java Client <mcast_addr> <mcast_port>");
			return;
		}
		InetAddress mcastAddr = InetAddress.getByName(args[0]);
		int mcastPORT = Integer.parseInt(args[1]);
			
		String s;
		String t;
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));

		byte[] buffer = new byte[65000];
		MulticastSocket socket = new MulticastSocket(mcastPORT);
		socket.joinGroup(mcastAddr);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		boolean not_connected = true;
		int i = 0;
		InetAddress srvcAddr = null;

		int srvcPort = 0;
		String raw_data = null;

		while(not_connected){
			socket.receive(packet);
			raw_data = new String(packet.getData(), 0, packet.getLength());
			System.out.println(i++ + " Trying to connect\nReceived: " + raw_data);
			srvcAddr = packet.getAddress();
			srvcPort = packet.getPort();
			System.out.println("communication adress: " + srvcAddr.toString());
			not_connected = false;
		}

		DatagramSocket sock = new DatagramSocket();
		
		try{
				
			while(true){
				
				//take input and send the packet
				echo("\n\n\nMulticast --> Enter message to send : ");
				t = (String)cin.readLine();
				if(t.equalsIgnoreCase("Exit")){
					 System.exit(0);
				}else{
				byte[] b = t.getBytes();
				
				DatagramPacket  dp = new DatagramPacket(b , b.length , srvcAddr, srvcPort);
				sock.send(dp);

				//now receive reply
				//buffer to receive incoming data
				byte[] buffer1 = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer1, buffer1.length);
				sock.receive(reply);

				byte[] data = reply.getData();
				s = new String(data, 0, reply.getLength());

				String example = "Mensagem enviada para o servidor com sucesso: " + t;
				echo(example);
				//echo the details of incoming data - client ip : client port - client message
				echo("\nMensagem recebida de (servidor - porta) "  + reply.getAddress().getHostAddress() + " - " + reply.getPort()+ ": " + 
						System.lineSeparator() + s);
			}
			}

		}catch(IOException e)
		{
			System.err.println("IOException " + e);
		}


	}
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}
