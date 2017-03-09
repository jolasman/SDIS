
import java.io.*;
import java.net.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastSocketServer {


	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
			return;
		}
		int PORT = Integer.parseInt(args[0]);
		InetAddress mcastAddr = InetAddress.getByName(args[1]);
		int mcastPORT = Integer.parseInt(args[2]);
		Map<String, String> plates = new HashMap<String, String>();
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		InetAddress hostAddr = InetAddress.getLocalHost();
		MulticastSocket socket = new MulticastSocket(PORT);
		socket.joinGroup(mcastAddr);

		int initialDelay = 0;
		int period = 1;
		//executor for sending hello every 1sec
		String outMessage = hostAddr.getHostAddress().toString() + " " + PORT;
		System.out.println(outMessage);
		byte[] autoBuffer = outMessage.getBytes();
		DatagramPacket autoPacket = new DatagramPacket(autoBuffer, autoBuffer.length, mcastAddr, mcastPORT);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					socket.send(autoPacket);

					System.out.println("Sending multicast with message: " + " (Host Adress + PORT) " + outMessage);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Failed to multicast");
					e.printStackTrace();
				}
			}
		}, initialDelay, period , TimeUnit.SECONDS);

		//escutar se algum cliente est� a mandar dados
		System.out.println("Server started");

		while (true) {

			byte[] buffer = new byte[65000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			byte[] data = packet.getData();
			String s = new String(data, 0, packet.getLength());
			String messageReceived = s.trim();
			String message[] = messageReceived.split(" ");
			String t = "";

			if(message[0].equalsIgnoreCase("Register")){
				//echo the details of incoming data - client ip : client port - client message
				echo("\n" + packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);
				if(message.length != 3 ){
					t+= System.lineSeparator() + "\n Mensagem enviado com erro de syntax. "+
							"\n\n syntax: REGISTER <nn-nn-ll> <nome>\n";
				}
				else if(message[1].matches("^(\\d{2}-?\\d{2}-?\\w{2})$")){

					t = "\nModo registo activado ... estamos a registar o seu pedido";
					plates.put(message[1], message[2]);

					t += System.lineSeparator() + "\nRegistado com sucesso."  + System.lineSeparator() 
					+ "Matricula: " + message[1] + " " + "Dono: " + plates.get(message[1]); 					
				}

				else{
					t+= System.lineSeparator() + "\n Mensagem enviado com erro de syntax. "+
							"\n\n syntax: REGISTER <nn-nn-ll> <nome>\n";
				}
			}
			else if( message[0].equalsIgnoreCase("Lookup")){
				//echo the details of incoming data - client ip : client port - client message
				echo("\n" + packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);			

				if(message.length != 2){
					t+= System.lineSeparator() + "\n Mensagem enviado com erro de syntax. "+
							"\n\n syntax: REGISTER <nn-nn-ll> <nome>\n";
				}
				else if((message[1].matches("^(\\d{2}-?\\d{2}-?\\w{2})$"))){
					t = "\nEstamos � procura do dono da matricula e se a mesma existe....  " + message[1];
					if(plates.get(message[1]) == null){
						t += System.lineSeparator() + "\nNao existe a matricula " + message[1] + " registada.";
					}
					else{
						t += System.lineSeparator() + "\nO dono da matricula: " + plates.get(message[1]);
					}	

				}else{
					t+= System.lineSeparator() + "\nMensagem enviado com erro de syntax. "+
							"\n\n syntax: LOOKUP <nn-nn-ll>\n";
				}
			}
			else{
				echo("\n" + packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);	
				t += "\nA ligar com o servidor...., mensagem com erro de syntax\n"+
						"\n\n syntax: LOOKUP <nn-nn-ll>" + " or REGISTER <nn-nn-ll> <nome>\n";
			}			
			DatagramSocket sock = new DatagramSocket();
			DatagramPacket dp = new DatagramPacket(t.getBytes() , t.getBytes().length , packet.getAddress(), packet.getPort());
			sock.send(dp);
			echo("Mensagem enviada como resposta: \n" + t + "\n");

		}
	}

	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}
