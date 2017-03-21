package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * esta class vai iniciar to o processo 
 * os sockets devm ser volatile
 * criar uma thread por cada menssagem recebida
 * criar uma thread a espera 
 * @author Joel Carneiro
 *
 */
public class Peer  {
	private MulticastSocket mcSocket;
	private DatagramSocket udp_receive_McSocket;

	private int peerID;
	//como dar um peerID unico a cada um do sistema?

	public Peer(int peerID){
		this.peerID = peerID;
		//McChannel();
	}


	public int getPeerID() {
		return peerID;
	}

	@SuppressWarnings("unused")
	public void  McChannel() throws IOException{
		int PORT = 7777;
		mcSocket = new MulticastSocket(PORT);
		InetAddress mcastAddr = InetAddress.getByName("224.0.0.3");
		int mcastPORT = 4444;

		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		mcSocket.joinGroup(mcastAddr);

		udp_receive_McSocket = new DatagramSocket(PORT,hostAddr_McSocket);

		new Thread(){
			public void run(){

				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						udp_receive_McSocket.receive(packet);
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("\nError when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}

					byte[] data = packet.getData();	//msg recebida			}






				}
			};



		};



	}
}
