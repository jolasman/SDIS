package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;

import chunks.Chunk;
import database.DatabaseChunksReceived;
import database.DatabaseChunksStored;
import initiator.Initiator;
import message.*;

/**
 * esta class vai iniciar to o processo 
 * @author Joel Carneiro
 *
 */
public class Peer  {
	private volatile MulticastSocket mcSocket_receive;
	private volatile MulticastSocket mcSocket_to_MC_Channel;
	private volatile MulticastSocket mcSocket_MC_Channel;
	private volatile MulticastSocket mcSocket_to_MDR_Channel;
	private volatile MulticastSocket mcSocket_MDR_receive;

	private String local_path = "./ChunksReceived";
	private int peerID;

	//como dar um peerID unico a cada um do sistema?
	public Peer(int peerID) throws IOException{
		this.peerID = peerID;	

		McDataChannel();
		McChannel();
		McDataRecovery();
		//testApp();
	}
	@SuppressWarnings("unused")
	public void  McDataChannel() throws IOException{
		mcSocket_receive = new MulticastSocket(Initiator.getMcastPORT_MD_Channel());
		InetAddress mcastAddr = Initiator.getMcastAddr_Channel_MD();
		InetAddress hostAddr_MD_Channel = InetAddress.getLocalHost();
		mcSocket_receive.joinGroup(mcastAddr);
		mcSocket_to_MC_Channel = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());

		Thread md = new Thread(){
			public void run(){
				System.out.println("\nMcData Channel Started...");
				while(true){
					byte[] buffer = new byte[85000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket_receive.receive(packet);
						System.out.println("\nMcData Channel receiving a new message....");
						Thread md_msg = new Thread(){
							public void run(){
								System.out.println("\nMcData Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
								byte[] msg_received = packet.getData();	//msg recebida

								String fileID_msg = MessageManager.SeparateMsgContent(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContent(msg_received).getChunkNo();
								byte[] filedata_msg = MessageManager.SeparateMsgContent(msg_received).getBody();
								int repl_degree_msg = MessageManager.SeparateMsgContent(msg_received).getReplication_degree();
								String type_msg = MessageManager.SeparateMsgContent(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContent(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContent(msg_received).getSenderID();
								boolean stored = false;
								boolean received = false;
								String chunkIDtoCheck = fileID_msg+chunkNo_msg;
								if(type_msg.equals("PUTCHUNK")){
									ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
									ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
									for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
											stored = true;
											System.out.println("\nPeer will not store chunk. It's a chunk sent by him\n");
										}
									}

									for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksalreadyReceived.get(i))){
											received = true;
											System.out.println("\nPeer already have that chunk. Not Stored.\n");
										}
									}
									if(!received){
										if(!stored){
											Chunk newChunk = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, repl_degree_msg, local_path);
											String message_to_Send = CreateMessage.MessageToSendStore(version,senderID_msg , fileID_msg, chunkNo_msg);
											DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , Initiator.getMcastAddr_Channel_MC(), Initiator.getMcastPORT_MC_Channel());
											try {
												Thread.sleep((long)(Math.random() * 400));
											} catch (InterruptedException e1) {
												System.out.println("\nMcData Channel Thread can not sleep");
												e1.printStackTrace();
											}
											try {

												mcSocket_to_MC_Channel.send(msgDatagram_to_send);
												System.out.println("\nMcData Channel send a STORED message to: \n" + Initiator.getMcastAddr_Channel_MC() + " ----- " + Initiator.getMcastPORT_MC_Channel());
											} catch (IOException e) {
												System.out.println("\nError: McData Channel when trying to send de Stored message to: " + Initiator.getMcastAddr_Channel_MC() + " --- " + Initiator.getMcastPORT_MC_Channel());
												e.printStackTrace();
											}
										}
									}
								}
								else{
									System.out.println("\n Error: McData Channel just accept PUTCHUNK messages");
								}
							};

						};
						md_msg.start();
					} 
					catch (IOException e) {
						System.out.println("\nError: McData Channel when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}
				}
			};

		};
		md.start();
	}	

	public void McChannel() throws IOException{
		mcSocket_MC_Channel = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());
		mcSocket_to_MDR_Channel = new MulticastSocket(Initiator.getMcastPORT_MDR_Channel());
		//InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		InetAddress mcastAddr_MC = Initiator.getMcastAddr_Channel_MC();
		mcSocket_MC_Channel.joinGroup(mcastAddr_MC);
		//DatagramSocket udp_msg_Mc = new DatagramSocket(PORT_MC_Channel,hostAddr_McSocket);

		Thread mc = new Thread(){
			public void run(){
				System.out.println("\nMc Control Channel Started...");
				while(true){
					/*try {
						Thread.sleep((long)(Math.random() * 400));
					}  catch (InterruptedException e1) {
						System.out.println("\nMc Control Channel Thread can not sleep");
						e1.printStackTrace();
					}*/
					byte[] buffer = new byte[85000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket_MC_Channel.receive(packet);
						System.out.println("\nMc Control Channel receiving a new a message..." );
						Thread mc_msg = new Thread(){
							@SuppressWarnings("resource")
							public void run(){
								System.out.println("\nMc Control Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
								byte[] msg_received = packet.getData();	//msg recebida

								System.out.println(new String (msg_received));

								String fileID_msg = MessageManager.SeparateMsgContentStored(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();
								String type_msg = MessageManager.SeparateMsgContentStored(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContentStored(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContentStored(msg_received).getSenderID();
								boolean stored = false;
								boolean received = false;
								String chunkIDtoCheck = fileID_msg;

								if(type_msg.equals("STORED")){							
									DatabaseChunksReceived.StoreReceivedChunkID_Sender(fileID_msg + chunkNo_msg, senderID_msg);
									DatabaseChunksReceived.setReceivedChunksID(fileID_msg+chunkNo_msg);
									System.out.println("\nMc Control Channel stored chunk information received in the databse after receive STORED msg");
								}
								else if(type_msg.equals("GETCHUNK")){
									System.out.println("entrou getchunk if");

									ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
									ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
									for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
											stored = true;
											System.out.println("\n(GETCHUNK) " + peerID + " this chunk   is   in stored chunks \n");
											break;
										}
									}

									for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksalreadyReceived.get(i))){
											received = true;
											System.out.println("\n(GETCHUNK) " + peerID + " this chunk   is   in received chunks \n");
											break;
										}
									}
									if(received){
										byte[]  body = new byte[64000];
										File file = new File("./ChunksReceived");
										File afile[] = file.listFiles();
										int i = 0;
										for (int j = afile.length; i < j; i++) {
											File arquivos = afile[i];
											if(arquivos.getName().equals(chunkIDtoCheck)){
												body = new byte[(int) arquivos.length()];
												try {
													FileInputStream fileInputStream = new FileInputStream(arquivos);
													fileInputStream.read(body);

												} catch (FileNotFoundException e) {
													System.out.println("File Not Found in received.");
													e.printStackTrace();
												}
												catch (IOException e1) {
													System.out.println("Error Reading The File received.");
													e1.printStackTrace();
												}
											}
										}
										try {
											String message_to_MDR = CreateMessage.MessageToSendChunk(version,senderID_msg , fileID_msg, chunkNo_msg, body);
											DatagramPacket msgDatagram_to_send_MDR = new DatagramPacket(message_to_MDR.getBytes() , message_to_MDR.getBytes().length , Initiator.getMcastAddr_Channel_MDR(), Initiator.getMcastPORT_MDR_Channel());
											mcSocket_to_MDR_Channel.send(msgDatagram_to_send_MDR);
											System.out.println("\nPeer: " + peerID + " sending a CHUNK message to: \n" + Initiator.getMcastAddr_Channel_MDR() + " ----- " + Initiator.getMcastPORT_MDR_Channel());
										} catch (IOException e) {
											System.out.println("\nError: Mc Control Channel when trying to send de CHUNK message to: " + Initiator.getMcastAddr_Channel_MDR() + " --- " + Initiator.getMcastPORT_MDR_Channel());
											e.printStackTrace();
										}

									}
									if(stored){										
										byte[]  body = new byte[64000];

										File file = new File("./Chunks");
										File afile[] = file.listFiles();
										int i = 0;
										for (int j = afile.length; i < j; i++) {
											File arquivos = afile[i];
											if(arquivos.getName().equals(chunkIDtoCheck)){
												body = new byte[(int) arquivos.length()];
												try {
													FileInputStream fileInputStream = new FileInputStream(arquivos);
													fileInputStream.read(body);

												} catch (FileNotFoundException e) {
													System.out.println("File Not Found in stored.");
													e.printStackTrace();
												}
												catch (IOException e1) {
													System.out.println("Error Reading The File in stored.");
													e1.printStackTrace();
												}

											}
										}
										try {
											String message_to_MDR = CreateMessage.MessageToSendChunk(version,senderID_msg , fileID_msg, chunkNo_msg, body);
											DatagramPacket msgDatagram_to_send_MDR = new DatagramPacket(message_to_MDR.getBytes() , message_to_MDR.getBytes().length , Initiator.getMcastAddr_Channel_MDR(), Initiator.getMcastPORT_MDR_Channel());
											mcSocket_to_MDR_Channel.send(msgDatagram_to_send_MDR);
											System.out.println("\nPeer: " + peerID + " sending a CHUNK message to: \n" + Initiator.getMcastAddr_Channel_MDR() + " ----- " + Initiator.getMcastPORT_MDR_Channel());
										} catch (IOException e) {
											System.out.println("\nError: Mc Control Channel when trying to send de CHUNK message to: " + Initiator.getMcastAddr_Channel_MDR() + " --- " + Initiator.getMcastPORT_MDR_Channel());
											e.printStackTrace();
										}
									}
								}
								else{
									System.out.println("\nERROR: Mc Control Channel not received a STORED or GETCHUNK message type\n" + type_msg);
								}
							};

						};
						mc_msg.start();

					} 
					catch (IOException e) {
						System.out.println("\nError: Mc Control Channel when receiving in mcSocket_MC_Channel!\n");
						e.printStackTrace();
					}
				}
			};
		};
		mc.start();
	}

	public void McDataRecovery() throws IOException{
		mcSocket_MDR_receive = new MulticastSocket(Initiator.getMcastPORT_MDR_Channel());
		InetAddress mcastAddr = Initiator.getMcastAddr_Channel_MDR();
		//InetAddress hostAddr_MDR_Channel = InetAddress.getLocalHost();
		mcSocket_MDR_receive.joinGroup(mcastAddr);


		Thread mdr = new Thread(){
			public void run(){
				System.out.println("\nMc Data Recovery Channel Started...");
				while(true){
					byte[] buffer = new byte[85000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket_MDR_receive.receive(packet);
						System.out.println("\nMc Data Recovery Channel receiving a new message....");
						Thread mdr_msg = new Thread(){
							@SuppressWarnings("unused")
							public void run(){
								System.out.println("\nMc Data Recovery received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
								byte[] msg_received = packet.getData();	//msg recebida

								String fileID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getFileID();
								int chunkNo_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getChunkNo();
								byte[] filedata_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getBody();
								String type_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getType();
								char[] version = MessageManager.SeparateMsgContentCHUNK(msg_received).getVersion();
								int senderID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getSenderID();
								boolean stored = false;
								boolean received = false;
								String chunkIDtoCheck = fileID_msg+chunkNo_msg;

								if(type_msg.equals("CHUNK")){
									ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
									ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
									for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
											stored = true;
											System.out.println("\nPeer will not store chunk. It's a chunk sent by him\n");
										}
									}

									for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
										if(chunkIDtoCheck.equals(chunksalreadyReceived.get(i))){
											received = true;
											System.out.println("\nPeer already have that chunk. Not Stored.\n");
										}
									}
									if(!received){
										if(!stored){
											Chunk newChunk1 = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, local_path);
											String message_to_Send = CreateMessage.MessageToSendStore(version,senderID_msg , fileID_msg, chunkNo_msg);
											DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , Initiator.getMcastAddr_Channel_MC(), Initiator.getMcastPORT_MC_Channel());
											try {
												Thread.sleep((long)(Math.random() * 400));
											} catch (InterruptedException e1) {
												System.out.println("\nMcData Channel Thread can not sleep");
												e1.printStackTrace();
											}
											try {

												mcSocket_to_MC_Channel.send(msgDatagram_to_send);
												System.out.println("\nMcData Channel send a STORED message to: \n" + Initiator.getMcastAddr_Channel_MC() + " ----- " + Initiator.getMcastPORT_MC_Channel());
											} catch (IOException e) {
												System.out.println("\nError: McData Channel when trying to send de Stored message to: " + Initiator.getMcastAddr_Channel_MC() + " --- " + Initiator.getMcastPORT_MC_Channel());
												e.printStackTrace();
											}
										}
									}
								}
								else{
									System.out.println("\n Error: McData Channel just accept PUTCHUNK messages");
								}
							};

						};
						mdr_msg.start();
					} 
					catch (IOException e) {
						System.out.println("\nError: Mc Data Recovery Channel when receiving in udp_receive_McSocket!\n");
						e.printStackTrace();
					}
				}
			};

		};
		mdr.start();

	}	
	/**
	 * trata das mensagem enviadas pela testApp class
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void testApp() throws IOException{
		int PORT = 7777;
		InetAddress hostAddr_McSocket = InetAddress.getLocalHost();
		DatagramSocket udp_testApp = new DatagramSocket(PORT, hostAddr_McSocket);

		Thread mc = new Thread(){
			public void run(){
				System.out.println("\nTestApp Channel started...");
				while(true){
					byte[] buffer = new byte[65000];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						System.out.println("\nTestApp Channel trying to receive a message....");
						udp_testApp.receive(packet);

						Thread mc_msg = new Thread(){
							@SuppressWarnings("unused")
							public void run(){
								System.out.println("\nTestApp Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
								byte[] msg_received = packet.getData();	//msg recebida
								String s = new String(msg_received, 0, packet.getLength());
								String messageReceived = s.trim();
								String message[] = messageReceived.split(" ");
								System.out.println("\nTestApp Channel message received : " + s + "\n");

								if(message[1].equals("BACKUP")){
									int peerID = Integer.parseInt(message[0]);
									String fileName = message[2];
									int replicationDegree = Integer.parseInt(message[3]);
								}else {
									System.out.println("\nError: TestApp Channel received a message type different from BACKUP");
								}
							};

						};
						mc_msg.start();
					} 
					catch (IOException e) {
						System.out.println("\nError: TestApp Channel when receiving in udp_testApp!\n");
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
