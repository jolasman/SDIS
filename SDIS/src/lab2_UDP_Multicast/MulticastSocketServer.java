package lab2_UDP_Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastSocketServer {
	final static String INET_ADDR = "224.0.0.3";
	final static int PORT1 = 8888;

	public static void main(String[] args) throws IOException {
		Map<String, String> plates = new HashMap<String, String>();

		int PORT = PORT1;
		InetAddress mcastAddr = InetAddress.getByName(INET_ADDR);
		int mcastPORT = 7777;
		byte[] buffer = new byte[65000];
		InetAddress hostAddr = InetAddress.getLocalHost();
		MulticastSocket socket = new MulticastSocket(PORT);
		socket.joinGroup(mcastAddr);
		DatagramPacket packet = new DatagramPacket(buffer, 65000, hostAddr, PORT);

		//executor for sending hello every 1sec
		String outMessage = hostAddr.getHostAddress().toString() + " " + PORT;
		System.out.println(outMessage);
		byte[] autoBuffer = outMessage.getBytes();
		DatagramPacket autoPacket = new DatagramPacket(autoBuffer, autoBuffer.length, mcastAddr, mcastPORT);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		Runnable task = () -> {
			try {
				socket.send(autoPacket);

				System.out.println("Sending multicast with message: " + " (Host Adress + PORT) " + outMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to multicast");
				e.printStackTrace();
			}
		};

		int initialDelay = 0;
		int period = 10;
		executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

		//escutar se algum cliente está a mandar dados
		System.out.println("Server started");
		
		
		
		while (true) {
			
			socket.receive(packet);
			byte[] data = packet.getData();
			String s = new String(data, 0, packet.getLength());
			String t = "";

			if(s.charAt(0) == 'R'){
				String register = s.substring(0, 10);
				String plateRegister = s.substring(10,18);
				char zzRegister = s.charAt(18);
				char beforenameRegister = s.charAt(20);
				String nomeRegister = s.substring(21,s.length()-1);
				char endRegister = s.charAt(s.length()-1);
				//echo the details of incoming data - client ip : client port - client message
				echo(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);

				if((register.equals("REGISTER <")) && (plateRegister.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") 
						&& zzRegister =='>' && beforenameRegister == '<' && endRegister == '>' )){
					t = "modo registo activado ... estamos a registar o seu pedido";
					plates.put(plateRegister,nomeRegister);

					t += System.lineSeparator() + "registado com sucesso."  + System.lineSeparator() 
					+ "matricula: " + plateRegister + " " + "nome: " + plates.get(plateRegister); 					
				}
			}
			if(s.charAt(0) == 'L'){
				String lookup = s.substring(0, 8);
				String plateLookup = s.substring(8,16);
				char endLookup = s.charAt(s.length()-1);


				//echo the details of incoming data - client ip : client port - client message
				echo(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);

				if((lookup.equals("LOOKUP <")) && (plateLookup.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") && endLookup == '>' )){
					t = "estamos à procura do dono da matricula e se a mesma existe....  " + plates.get(plateLookup);
					if(plates.get(plateLookup) == null){
						t += System.lineSeparator() + " nao existe a matricula " + plateLookup + " registada.";
					}
					else{
						t += System.lineSeparator() + "o dono da matricula: " + plates.get(plateLookup);						}	
				}
			}
			else if( (s.charAt(0) != 'L') && (s.charAt(0) != 'R')){
				echo("mensagem recebida do cliente, com erro de syntax : " + s);
				t += " a ligar com o servidor...., mensagem com erro de syntax";
			}			
			
				
						

			DatagramPacket dp = new DatagramPacket(t.getBytes() , t.getBytes().length , mcastAddr, mcastPORT);
			socket.send(dp);
		}
	}

	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}
