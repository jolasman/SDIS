package lab1_UDP;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Server {

	public static void main(String args[])
	{
		DatagramSocket sock = null;
		Map<String, String> plates = new HashMap<String, String>();
		try
		{
			//1. creating a server socket, parameter is local port number
			sock = new DatagramSocket(7777);

			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

			//2. Wait for an incoming data
			echo("Server socket created. Waiting for incoming data...");

			//communication loop
			while(true)
			{
				sock.receive(incoming);
				byte[] data = incoming.getData();
				String s = new String(data, 0, incoming.getLength());
				String t = "";
				  
			
				if(s.charAt(0) == 'R'){
					String register = s.substring(0, 10);
					String plateRegister = s.substring(10,18);
					char zzRegister = s.charAt(18);
					char beforenameRegister = s.charAt(20);
					String nomeRegister = s.substring(21,s.length()-1);
					char endRegister = s.charAt(s.length()-1);

					//echo the details of incoming data - client ip : client port - client message
					echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

					if((register.equals("REGISTER <")) && (plateRegister.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") 
							&& zzRegister =='>' && beforenameRegister == '<' && endRegister == '>' )){
						t = "modo registo activado ... estamos a registar o seu pedido";
						plates.put(plateRegister,nomeRegister);
						
						t += System.lineSeparator() + "registado com sucesso."  + System.lineSeparator() 
						+ "matricula: " + plateRegister + " " + "nome: " + plates.get(plateRegister); 
										
					}
				}
				else if(s.charAt(0) == 'L'){
					String lookup = s.substring(0, 8);
					String plateLookup = s.substring(8,16);
					char endLookup = s.charAt(s.length()-1);


					//echo the details of incoming data - client ip : client port - client message
					echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

					if((lookup.equals("LOOKUP <")) && (plateLookup.matches("^(\\d{2}-?\\d{2}-?\\w{2})$") && endLookup == '>' )){

						t = "estamos à procura do dono da matricula e se a mesma existe....  " + plates.get(plateLookup);
						
						if(plates.get(plateLookup) == null){
							t += System.lineSeparator() + " nao existe a matricula " + plateLookup + " registada.";
						}
						else{
							t += System.lineSeparator() + "o dono da matricula: " + plates.get(plateLookup);						}
							
					}

				}


				/*else{
					 t = "OK, a tua mensagem foi recebida. esta foi a tua mensagem : " + s;
				}*/
				DatagramPacket dp = new DatagramPacket(t.getBytes() , t.getBytes().length , incoming.getAddress() , incoming.getPort());
				sock.send(dp);
			}
		}

		catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
	}

	//simple function to echo data to terminal
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}

