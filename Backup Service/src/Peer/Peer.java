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

import chunks.Chunk;
import message.CreateMessage;
import message.MessageManager;
import message.SeparatedMessage;

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

		udp_msg_McSocket = new DatagramSocket(PORT,hostAddr_McSocket);

		new Thread(){
			public void run(){
				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						udp_msg_McSocket.receive(packet);
					} 
					catch (IOException e) {
						System.out.println("\nError when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}
					new Thread(){
						public void run(){
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
								String message_to_Send = CreateMessage.MessageToSendPut("Stored",version,senderID_msg , fileID_msg, chunkNo_msg,repl_degree_msg);
								DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , packet.getAddress(), packet.getPort());
								try {
									udp_msg_McSocket.send(msgDatagram_to_send);
								} catch (IOException e) {
									System.out.println("\nError when trying to send de Stored message to: " + packet.getAddress() + " --- " + packet.getPort());
									e.printStackTrace();
								}
							}

						};

					};
				}
			};

		};
	}
}
