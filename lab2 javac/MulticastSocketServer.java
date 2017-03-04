
import java.io.*;
import java.net.*;
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
		
		//escutar se algum cliente está a mandar dados
		System.out.println("Server started");
		
		while (true) {
			
			byte[] buffer = new byte[65000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			byte[] data = packet.getData();
			String s = new String(data, 0, packet.getLength());
			String t = "";
			
			if(s.charAt(0) == 'R'){
				String register = s.substring(0, 10);
				//echo the details of incoming data - client ip : client port - client message
				echo(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);
				if(register.equals("REGISTER <")){
					String plateRegister = s.substring(10,18);
					char zzRegister = s.charAt(18);
					char beforenameRegister = s.charAt(20);
					String nomeRegister = s.substring(21,s.length()-1);
					char endRegister = s.charAt(s.length()-1);
					if((register.equals("REGISTER <")) && (plateRegister.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") 
							&& zzRegister =='>' && beforenameRegister == '<' && endRegister == '>' )){
						t = "\nModo registo activado ... estamos a registar o seu pedido";
						plates.put(plateRegister,nomeRegister);

						t += System.lineSeparator() + "\nRegistado com sucesso."  + System.lineSeparator() 
						+ "Matricula: " + plateRegister + " " + "Dono: " + plates.get(plateRegister); 					
					}
				}
				else{
					t+= System.lineSeparator() + "\n Mensagem enviado com erro de syntax. "+
							"\n\n syntax: REGISTER <nn-nn-ll> <nome>\n";
				}
			}
			else if(s.charAt(0) == 'L'){
				String lookup = s.substring(0, 8);

				//echo the details of incoming data - client ip : client port - client message
				echo(packet.getAddress().getHostAddress() + " : " + packet.getPort() + " - " + s);

				if(lookup.equals("LOOKUP <")){
					String plateLookup = s.substring(8,16);
					char endLookup = s.charAt(s.length()-1);
					if((lookup.equals("LOOKUP <")) && (plateLookup.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") && endLookup == '>' )){
						t = "\nEstamos à procura do dono da matricula e se a mesma existe....  " + plateLookup;
						if(plates.get(plateLookup) == null){
							t += System.lineSeparator() + "\nNao existe a matricula " + plateLookup + " registada.";
						}
						else{
							t += System.lineSeparator() + "\nO dono da matricula: " + plates.get(plateLookup);						}	
					}
				}else{
					t+= System.lineSeparator() + "\nMensagem enviado com erro de syntax. "+
							"\n\n syntax: LOOKUP <nn-nn-ll>\n";
				}
			}
			else{
				echo("\nMensagem recebida do cliente, com erro de syntax : " + s);
				t += "\nA ligar com o servidor...., mensagem com erro de syntax\n"+
						"\n\n syntax: LOOKUP <nn-nn-ll>" + " or REGISTER <nn-nn-ll> <nome>\n";
			}			
			DatagramSocket sock = new DatagramSocket();
			DatagramPacket dp = new DatagramPacket(t.getBytes() , t.getBytes().length , packet.getAddress(), packet.getPort());
			sock.send(dp);
			
		}
	}

	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}
