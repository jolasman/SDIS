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
import database.DatabaseChunksStored;
import message.*;

/**
 * esta class vai iniciar to o processo 
 * @author Joel Carneiro
 *
 */
public class Peer  {
	private volatile MulticastSocket mcSocket;
	private volatile DatagramSocket udp_msg_McSocket;
	private String local_path = "./ChunksReceived";
	private int peerID;
	private int PORT_MC_Channel = 5001;
	private InetAddress mcastAddr_Channels = InetAddress.getByName("225.4.5.6");
	private int mcastPORT_MC_Channel = 8888;
	//como dar um peerID unico a cada um do sistema?
	public Peer(int peerID) throws IOException{
		this.peerID = peerID;
		McDataChannel();
		McChannel();
		//testApp();
		//McChannel();
	}
	@SuppressWarnings("unused")
	public void  McDataChannel() throws IOException{
		int PORT = 5000;
		int mcastPORT = 4444;
		mcSocket = new MulticastSocket(mcastPORT);
		InetAddress mcastAddr = mcastAddr_Channels;
		
		InetAddress hostAddr_MD_Channel = InetAddress.getLocalHost();
		mcSocket.joinGroup(mcastAddr);

		MulticastSocket mcSocket_to_MC_Channel = new MulticastSocket(mcastPORT_MC_Channel);
		udp_msg_McSocket = new DatagramSocket(PORT,hostAddr_MD_Channel);

		Thread md = new Thread(){
			public void run(){
				System.out.println("entrou 1 thread md");
				while(true){
					byte[] buffer = new byte[85000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket.receive(packet);
						Thread md_msg = new Thread(){
							public void run(){
								System.out.println("entrou 2 thread md");
								System.out.println("\nlocal host address " + hostAddr_MD_Channel);
								System.out.println("\nreceived from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");

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
									DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr_Channels, PORT_MC_Channel);
									
									try {
										mcSocket_to_MC_Channel.send(msgDatagram_to_send);
										System.out.println("mandou mensagem para: " + mcastAddr_Channels + " ----- " + PORT_MC_Channel);
									} catch (IOException e) {
										System.out.println("\nError when trying to send de Stored message to: " + mcastAddr_Channels + " --- " + mcastAddr_Channels);
										e.printStackTrace();
									}
								}
								else{
									System.out.println("só sao aceites msg do tipo PUTCHUNK");
								}
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

		MulticastSocket mcSocket_MC_Channel = new MulticastSocket(PORT_MC_Channel);
		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		mcSocket_MC_Channel.joinGroup(mcastAddr_Channels);
		DatagramSocket udp_msg_Mc = new DatagramSocket(PORT_MC_Channel,hostAddr_McSocket);

		Thread mc = new Thread(){
			public void run(){
				System.out.println("entrou 1 thread mc");
				System.out.println("mc addres: " + hostAddr_McSocket);
				while(true){
					byte[] buffer = new byte[85000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket_MC_Channel.receive(packet);
						System.out.println("tentou receber packet MC Channel" );
						Thread mc_msg = new Thread(){
							public void run(){
								System.out.println("entrou 2 thread mc");
								byte[] msg_received = packet.getData();	//msg recebida

								String fileID_msg = MessageManager.SeparateMsgContentStored(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();
								String type_msg = MessageManager.SeparateMsgContentStored(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContentStored(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContentStored(msg_received).getSenderID();


								if(type_msg.equals("STORED")){
									DatabaseChunksStored.StoreChunkID(fileID_msg + chunkNo_msg, senderID_msg);
									System.out.println("\ngravou na BD após receber STORED msg\n");
								}
								else{
									System.out.println("\nERROR: Mensagem recebida diferente de STORED syntax no MC Channel\n");
								}
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
	/**
	 * trata das mensagem enviadas pela testApp class
	 * @throws IOException
	 */
	public void testApp() throws IOException{
		int PORT = 7777;
		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		DatagramSocket udp_testApp = new DatagramSocket(PORT, hostAddr_McSocket);

		Thread mc = new Thread(){
			public void run(){
				System.out.println("entrou 1 thread testApp");
				System.out.println("address: " + hostAddr_McSocket);
				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						udp_testApp.receive(packet);

						Thread mc_msg = new Thread(){
							public void run(){
								System.out.println("entrou 2 thread testApp");
								byte[] msg_received = packet.getData();	//msg recebida
								String s = new String(msg_received, 0, packet.getLength());
								String messageReceived = s.trim();
								String message[] = messageReceived.split(" ");
								System.out.println("mensagem recebida testApp: " + s);


								if(message[1].equals("BACKUP")){
									int peerID = Integer.parseInt(message[0]);
									String fileName = message[2];
									int replicationDegree = Integer.parseInt(message[3]);
									System.out.println("mensagem recebida de testApp com: "+
											peerID + " - " + fileName + " - " + replicationDegree);

								}else {
									System.out.println("mensagem recebida de testApp com erro de syntax");
								}
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

	public int getPeerID() {
		return peerID;
	}
}
