package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chunks.Chunk;
import message.*;

/**
 * esta class vai iniciar to o processo 
 * os sockets devm ser volatile
 * criar uma thread por cada menssagem recebida
 * criar uma thread a espera 
 * @author Joel Carneiro
 *
 */
public class Peer  {
	private volatile MulticastSocket mcSocket;
	private volatile DatagramSocket udp_msg_McSocket;
	private String local_path = "./Chunks";
	private int peerID;
	//como dar um peerID unico a cada um do sistema?
	public Peer(int peerID) throws IOException{
		this.peerID = peerID;
		McDataChannel();
		McChannel();
		//McChannel();
	}
	@SuppressWarnings("unused")
	public void  McDataChannel() throws IOException{
		int PORT = 7777;
		mcSocket = new MulticastSocket(PORT);
		InetAddress mcastAddr = InetAddress.getByName("224.0.0.3");
		int mcastPORT = 4444;
		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		mcSocket.joinGroup(mcastAddr);

		udp_msg_McSocket = new DatagramSocket(PORT,hostAddr_McSocket);

		Thread md = new Thread(){
			public void run(){
				System.out.println("entrou 1 thread md");
				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
					// enviar mensagem stored exemplo
				/*	char[] version = {'1','.','1'};
					String message_to_Send = CreateMessage.MessageToSendStore("Stored",version,12345, "fileID_msg",1235);
					DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , hostAddr_McSocket, 6666);
					try {
						udp_msg_McSocket.send(msgDatagram_to_send);
					} catch (IOException e) {
						System.out.println("\nError when trying to send de Stored message to: " + packet.getAddress() + " --- " + packet.getPort());
						e.printStackTrace();
					}
					*/
					//end
					
					try {
						udp_msg_McSocket.receive(packet);
						
						Thread md_msg = new Thread(){
							public void run(){
								System.out.println("entrou 2 thread md");
								System.out.println("md address " + hostAddr_McSocket);

								byte[] msg_received = packet.getData();	//msg recebida

								String fileID_msg = MessageManager.SeparateMsgContent(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContent(msg_received).getChunkNo();
								byte[] filedata_msg = MessageManager.SeparateMsgContent(msg_received).getBody();
								int repl_degree_msg = MessageManager.SeparateMsgContent(msg_received).getReplication_degree();
								String type_msg = MessageManager.SeparateMsgContent(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContent(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContent(msg_received).getSenderID();

								if(type_msg.equals("PUTCHUNK")){
									Chunk newChunk = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, repl_degree_msg, local_path);
									String message_to_Send = CreateMessage.MessageToSendStore("Stored",version,senderID_msg , fileID_msg, chunkNo_msg);
									DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , packet.getAddress(), packet.getPort());
									try {
										udp_msg_McSocket.send(msgDatagram_to_send);
									} catch (IOException e) {
										System.out.println("\nError when trying to send de Stored message to: " + packet.getAddress() + " --- " + packet.getPort());
										e.printStackTrace();
									}
								}
								else{}
							};

						};
						md_msg.start();
					} 
					catch (IOException e) {
						System.out.println("\nError when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}
				}
			};

		};
		md.start();
	}	
	public void McChannel() throws IOException{
		int PORT = 6666;
		mcSocket = new MulticastSocket(PORT);
		InetAddress mcastAddr = InetAddress.getByName("224.0.0.6");
		int mcastPORT = 8888;
		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		mcSocket.joinGroup(mcastAddr);

		udp_msg_McSocket = new DatagramSocket(PORT,hostAddr_McSocket);

		Thread mc = new Thread(){
			public void run(){
				System.out.println("entrou 1 thread mc");
				System.out.println("mc addres: " + hostAddr_McSocket);
				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						udp_msg_McSocket.receive(packet);

						Thread mc_msg = new Thread(){
							public void run(){
								System.out.println("entrou 2 thread mc");
								byte[] msg_received = packet.getData();	//msg recebida

								String fileID_msg = MessageManager.SeparateMsgContent(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContent(msg_received).getChunkNo();
								String type_msg = MessageManager.SeparateMsgContent(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContent(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContent(msg_received).getSenderID();

								if(type_msg.equals("Stored")){
									//guardar dados
								}
								else{}
							};

						};
						mc_msg.start();

					} 
					catch (IOException e) {
						System.out.println("\nError when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}
				}
			};
		};
		mc.start();
	}

	public void McDataRecovery(){

	}	
	public int getPeerID() {
		return peerID;
	}
}
