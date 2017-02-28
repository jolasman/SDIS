package lab2_UDP_Multicast;

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

	final static String INET_ADDR = "224.0.0.3";
	final static int PORT1 = 8888;



	public static void main(String[] args) throws IOException {
		String s;
		String t;
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));


		InetAddress mcastAddr = InetAddress.getByName(INET_ADDR);
		int mcastPORT = 7777;

		int PORT = PORT1;//TODO
		byte[] buffer = new byte[65000];
		MulticastSocket socket = new MulticastSocket(mcastPORT);
		socket.joinGroup(mcastAddr);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mcastAddr, mcastPORT);

		boolean not_connected = true;
		int i = 0;
		InetAddress srvcAddr;
		int srvcPort;
		String raw_data = null;

		while(not_connected){
			socket.receive(packet);
			raw_data = new String(packet.getData(), 0, packet.getLength());
			System.out.println(i++ + " Trying to connect\nReceived: " + raw_data);
			srvcAddr = packet.getAddress();
			System.out.println("multicast adress: " + srvcAddr.toString());
			not_connected = false;
		}


		try{

			while(true){
				//take input and send the packet
				echo("Multicast --> Enter message to send : ");
				t = (String)cin.readLine();
				byte[] b = t.getBytes();

				String[] host_things = raw_data.split(" ");

				DatagramPacket  dp = new DatagramPacket(b , b.length , InetAddress.getByName(host_things[0]), Integer.parseInt(host_things[1]));
				socket.send(dp);

				//now receive reply
				//buffer to receive incoming data
				byte[] buffer1 = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer1, buffer1.length);
				socket.receive(reply);

				byte[] data = reply.getData();
				s = new String(data, 0, reply.getLength());


				String example = "Mensagem enviada para o servidor com sucesso: " + t;
				echo(example);

				//echo the details of incoming data - client ip : client port - client message
				echo("Mensagem recebida: (servidor e porta) "  + reply.getAddress().getHostAddress() + " : " + reply.getPort() + 
						System.lineSeparator() + s);
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
