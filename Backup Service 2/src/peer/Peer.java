package peer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import algorithms.SHA256;
import chunks.Chunk;
import database.DatabaseChunksReceived;
import database.DatabaseChunksStored;
import fileManagement.FileManager;
import fileManagement.FileToCkunk;
import fileManagement.MergeChunks;
import initiator.Initiator;
import message.*;

/**
 * esta class vai iniciar to o processo 
 * @author Joel Carneiro
 *
 */
public class Peer  {
	private volatile static MulticastSocket socket_backup; 
	private volatile static MulticastSocket socket_restore;
	private volatile MulticastSocket mcSocket_receive;
	private volatile MulticastSocket mcSocket_to_MC_Channel;
	private volatile MulticastSocket mcSocket_MC_Channel;
	private volatile MulticastSocket mcSocket_to_MDR_Channel;
	private volatile MulticastSocket mcSocket_MDR_receive;
	private volatile MulticastSocket mcSocket_REMOVED_SEND;

	private String local_path = "./ChunksReceived";
	private String local_path_getchunk = "./ChunksToRestore";
	private int peerID;
	private int chunkNoVerify;
	private int packetsReceived;
	private int senderPeerID;
	private boolean minimum;
	private boolean receiveone;
	private boolean storedMessage;
	private boolean getChunkMessage;
	private boolean receivedChunk;
	private boolean receivedOneChunk;
	private static HashMap<String,Integer> remoteChunks = new HashMap<String,Integer>();

	//como dar um peerID unico a cada um do sistema?
	public Peer(int peerID) throws IOException{
		this.peerID = peerID;	

		McDataChannel();
		McChannel();
		McDataRecovery();

	}
	@SuppressWarnings("unused")
	public synchronized void  McDataChannel() throws IOException{
		mcSocket_receive = new MulticastSocket(Initiator.getMcastPORT_MD_Channel());
		InetAddress mcastAddr = Initiator.getMcastAddr_Channel_MD();
		InetAddress hostAddr_MD_Channel = InetAddress.getLocalHost();
		mcSocket_receive.joinGroup(mcastAddr);
		mcSocket_to_MC_Channel = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());

		Thread md = new Thread(){
			public void run(){
				System.out.println("\nMcData Channel Started...");
				while(true){
					byte[] buffer = new byte[64800];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					try {
						mcSocket_receive.receive(packet);
						System.out.println("\nMcData Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
						byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());	//msg recebida	//msg recebida

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
						if(senderID_msg == Initiator.getPeerID()){
							//se for msg dele proprio nao faz nada
						}else{
							if(type_msg.equals("PUTCHUNK")){
								ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
								ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
								/*for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
									if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
										stored = true;
										System.out.println("\nPeer " + getPeerID() + " not store chunk. It's a chunk sent by him\n");
									}
								}

								for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
									if(chunkIDtoCheck.equals(chunksalreadyReceived.get(i))){
										received = true;
										System.out.println("\nPeer " + getPeerID() + " already have that chunk. Not Stored.\n");
									}
								}*/
								if(!received){
									if(!stored){
										Chunk newChunk = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, repl_degree_msg, local_path);
										DatabaseChunksReceived.setReceivedChunksID(fileID_msg + chunkNo_msg);
										byte[] message_to_Send = CreateMessage.MessageToSendStore(version,getPeerID() , fileID_msg, chunkNo_msg);
										DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send , message_to_Send.length , Initiator.getMcastAddr_Channel_MC(), Initiator.getMcastPORT_MC_Channel());
										try {
											Thread.sleep((long)(Math.random() * 400));
										} catch (InterruptedException e1) {
											System.out.println("\nMcData Channel Thread can not sleep");
											e1.printStackTrace();
										}
										try {
											mcSocket_to_MC_Channel.send(msgDatagram_to_send);
											System.out.println("\nPeer : " + getPeerID() +" send a STORED message to: \n" + Initiator.getMcastAddr_Channel_MC() + " ----- " + Initiator.getMcastPORT_MC_Channel());
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
						}
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

	public synchronized void McChannel() throws IOException{
		mcSocket_MC_Channel = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());
		mcSocket_REMOVED_SEND = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());
		mcSocket_to_MDR_Channel = new MulticastSocket(Initiator.getMcastPORT_MDR_Channel());
		InetAddress mcastAddr_MC = Initiator.getMcastAddr_Channel_MC();
		mcSocket_MC_Channel.joinGroup(mcastAddr_MC);
		Thread mc = new Thread(){
			public void run(){
				System.out.println("\nMc Control Channel Started...");
				byte[] buffer = new byte[64800];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				setReceiveone(false);
				setMinimum(false);
				setStoredMessage(false);
				setGetChunkMessage(false);
				if(Initiator.isBackupMode()){
					if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu backup
						new Timer().schedule(new TimerTask() {          
							@Override
							public void run() {
								try {
									System.out.println("\nPeer : " + getPeerID() + " Testing the option of reSend the Chunk files. Just some amount of Stored Messages received\n");
									ReenviaPut();
								} catch (NoSuchAlgorithmException | IOException e) {
									System.out.println("\nPeer : " + getPeerID() + " do not receive and can reSend");
									e.printStackTrace();
								} catch (InterruptedException e) {e.printStackTrace();}
							}
						}, Initiator.getTimetoBackup());
					}
				}
				while(true){
					try {
						mcSocket_MC_Channel.receive(packet);
						System.out.println("\nMc Control Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
						byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());	//msg recebida	//msg recebida
						String fileID_msg = MessageManager.SeparateMsgContentStored(msg_received).getFileID();
						String type_msg = MessageManager.SeparateMsgContentStored(msg_received).getType();
						char[] version = MessageManager.SeparateMsgContentStored(msg_received).getVersion();
						int senderID_msg = MessageManager.SeparateMsgContentStored(msg_received).getSenderID();
						boolean stored = false;
						boolean received = false;
						String chunkIDtoCheck = fileID_msg;
						switch(type_msg){

						case "STORED":
							System.out.println("A STORED Message received in MC Channel");
							setReceiveone(true);
							int chunkNo_msg = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();
							if(senderID_msg == Initiator.getPeerID()){
								// se for do proprio nao faz nada
							}else{
								DatabaseChunksReceived.setReceivedChunksID(fileID_msg+chunkNo_msg);
							}

						case "GETCHUNK":
							System.out.println("A GETCHUNK Message received in MC Channel");
							setReceiveone(true);
							setGetChunkMessage(true);
							int chunkNo_msgget = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();
							if(senderID_msg == Initiator.getPeerID()){
								// se for do proprio nao faz nada
							}else{
								ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
								for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
									if(chunkIDtoCheck.equals(chunksalreadyReceived.get(i))){
										received = true;
										System.out.println("\n(GETCHUNK) " + getPeerID() + " this chunk is in my received chunks \n");
										break;
									}
								}
								if(received){
									Chunk chunkFile;
									File file = new File("./ChunksReceived");
									File afile[] = file.listFiles();
									int i = 0;
									for (int j = afile.length; i < j; i++) {
										File arquivos = afile[i];
										if(arquivos.getName().equals(chunkIDtoCheck)){
											try (ObjectInputStream file_data = new ObjectInputStream(new FileInputStream(arquivos))) {										
												chunkFile = (Chunk) file_data.readObject();
												file_data.close();

												byte[] message_to_MDR = CreateMessage.MessageToSendChunk(version, getPeerID(), fileID_msg, chunkNo_msgget, chunkFile.getChunkData());
												DatagramPacket msgDatagram_to_send_MDR = new DatagramPacket(message_to_MDR , message_to_MDR.length , Initiator.getMcastAddr_Channel_MDR(), Initiator.getMcastPORT_MDR_Channel());
												try {
													Thread.sleep((long)(Math.random() * 400));
												}  catch (InterruptedException e1) {
													System.out.println("\nTestApp Thread can not sleep");
													e1.printStackTrace();
												}
												mcSocket_to_MDR_Channel.send(msgDatagram_to_send_MDR);
												System.out.println("\nPeer: " + getPeerID() + " sending a CHUNK message to: \n" + Initiator.getMcastAddr_Channel_MDR() + " ----- " + Initiator.getMcastPORT_MDR_Channel() +
														"\nbody length : " + chunkFile.getChunkData().length);
											} 
											catch (FileNotFoundException e) {
												System.out.println("Error when we try to get file data");
												e.printStackTrace();

											} catch (IOException e) {
												System.out.println("Error when we try to get file");
												e.printStackTrace();
											} catch (ClassNotFoundException e) {
												System.out.println("Error when we try to get file OBJECT data");
												e.printStackTrace();
											}
										}
									}
								}
							}
							break;

						case "DELETE":
							System.out.println("A DELETE Message received in MC Channel");
							if(senderID_msg == Initiator.getPeerID()){}
							else{

								ArrayList<String> chunksalreadyReceived = new ArrayList<String>();
								File file1 = new File("./ChunksReceived");
								if(file1.listFiles() == null){ 
									System.out.println("nenhum ficheiro na pasta ChunksReceived. Peer:" + getPeerID() );
								}
								else{
									File afile[] = file1.listFiles();
									int i = 0;
									for (int j = afile.length; i < j; i++) {
										File arquivos = afile[i];
										System.out.println("Peer : " + getPeerID() + " Load chunks received: " + arquivos.getName());
										chunksalreadyReceived.add(arquivos.getName());
									}
								}
								int size = chunksalreadyReceived.size();
								Set<String> foo = new HashSet<String>();
								for(int i = 0; i< chunksalreadyReceived.size(); i++ ){
									foo.add(chunksalreadyReceived.get(i));
								} 
								for(int i = 0; i< size; i++){
									String toCheck = fileID_msg + (i+1);
									if (foo.contains(toCheck)) { 
										try{
											File file = new File("./ChunksReceived/" + toCheck);
											if(file.delete()){
												System.out.println(file.getName() + " is deleted! --> Peer: " + getPeerID());
												
												Iterator<String>iterator = DatabaseChunksReceived.getReceivedChunksID().iterator();
												while(iterator.hasNext()){
													if(iterator.next().equals(toCheck)){
														iterator.remove();
													}
												}
												
												byte[] message_to_MDR = CreateMessage.MessageToSendRemoved(version, getPeerID(), fileID_msg, (i+1));
												DatagramPacket msgDatagram_to_send_MC = new DatagramPacket(message_to_MDR , message_to_MDR.length , Initiator.getMcastAddr_Channel_MC(), Initiator.getMcastPORT_MC_Channel());
												try {
													Thread.sleep((long)(Math.random() * 400));
												}  catch (InterruptedException e1) {
													System.out.println("\nRemoved can not sleep");
													e1.printStackTrace();
												}
												try{
													mcSocket_REMOVED_SEND.send(msgDatagram_to_send_MC);
													System.out.println("\nPeer: " + getPeerID() + " sending a Removed message to: \n" + Initiator.getMcastAddr_Channel_MC() + " ----- " + Initiator.getMcastPORT_MC_Channel());
												} catch (Exception e) {
													System.out.println("\nPeer: " + getPeerID() + " ERROR sending a Removed message to: \n" + Initiator.getMcastAddr_Channel_MC() + " ----- " + Initiator.getMcastPORT_MC_Channel());

												}

											}else{
												System.out.println("\n	Delete operation is failed! --> Peer: " + getPeerID() + " --> chunk : " + file.getName() + (i+1) + " not deleted\n");
											}
										}catch(Exception e){e.printStackTrace();}
									}
								}
								System.out.println("\n\nPeer : "+ getPeerID() + " -->Attempt to Delete Chunks Done\n\n");
							}
							break;


						case "REMOVED":
							System.out.println("A REMOVED Message received in MC Channel");
							int chunkNo_msg_Removed = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();

							if(senderID_msg == Initiator.getPeerID()){
								/*se fui eu a enviar nao faco nada*/
							}
							else{
								String nameID = fileID_msg + chunkNo_msg_Removed;

								HashMap<String, Integer> remote = getRemoteChunks();

								for (String key:remote.keySet()){
									if (remote.containsKey(nameID))
									{
										setRemoteChunks(nameID, remoteChunks.get(nameID)-1);
									}
								}
								getRemoteChunks().forEach((k,v)-> System.out.println(k+", "+v));

							}


							break;

						default:
							System.out.println("\nNOT a STORED,GETCHUNK, REMOVED or DELETE MEssage in MC Channel\n");
						}
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

	public synchronized void McDataRecovery() throws IOException{
		mcSocket_MDR_receive = new MulticastSocket(Initiator.getMcastPORT_MDR_Channel());
		InetAddress mcastAddr = Initiator.getMcastAddr_Channel_MDR();
		mcSocket_MDR_receive.joinGroup(mcastAddr);

		Thread mdr = new Thread(){
			public void run(){
				System.out.println("\nMc Data Recovery Channel Started...");
				byte[] buffer = new byte[64800];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);	
				setReceivedChunk(false);
				setReceivedOneChunk(false);
				int packets = 0;
				//System.out.println("\n\n restore mode : " + Initiator.isRestoreMode() + "\n\n");
				if(Initiator.isRestoreMode()){
					if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu backup
						new Timer().schedule(new TimerTask() {          
							@Override
							public void run() {
								try {
									System.out.println("\nPeer : " + getPeerID() + " Testing the option of reSend the GetChunk Messages. \n");
									ReenviaGetChunk();
								} catch (NoSuchAlgorithmException | IOException e) {
									System.out.println("\nPeer : " + getPeerID() + " do not receive and can reSend");
									e.printStackTrace();
								} catch (InterruptedException e) {e.printStackTrace();}
							}
						}, Initiator.getTimetoRestore());
					}
				}
				while(true){
					try {
						mcSocket_MDR_receive.receive(packet);
						packets++;
						if(packets == 1){
							setReceivedOneChunk(true);
						}

						System.out.println("\nMc Data Recovery received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
						byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());	//msg recebida

						String fileID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getFileID();
						int chunkNo_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getChunkNo();
						byte[] filedata_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getBody();
						String type_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getType();
						char[] version = MessageManager.SeparateMsgContentCHUNK(msg_received).getVersion();
						int senderID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getSenderID();

						if(type_msg.equals("CHUNK")){
							if(getPeerID() == Initiator.getPeerID()){
								Chunk newChunk1 = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, 2);
								Chunk.setChunksRestore(chunkNo_msg + "",newChunk1);

								System.out.println("Peer: " + peerID + " stored chunk received in MC Data Recovery Channel");
								setChunkNoVerify(chunkNo_msg); //nao sei o que faz mas deixa estar xD ja nao me lembro xD

								DatabaseChunksReceived.setReceivedChunksIDFromCHUNK(fileID_msg);

							}else{}
						}
						else{
							System.out.println("\n Error: Mc Data Recovery Channel just accept CHUNK messages");
						}

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

	public synchronized void ReenviaPut() throws NoSuchAlgorithmException, IOException, InterruptedException{
		InetAddress mcastAddr = Initiator.getMcastAddr_Channel_MD();
		MulticastSocket socket_backup_reenvia = new MulticastSocket(Initiator.getMcastPORT_MD_Channel());
		ArrayList<String> IDs = DatabaseChunksReceived.getReceivedChunksID();
		ArrayList<Integer> chunks = new ArrayList<Integer>(Initiator.getChunksforBackup());
		boolean reSend = false;
		for (int i = 0; i < Initiator.getChunksforBackup() +1; i++) {chunks.add(0);}

		if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu backup
			for(int i = 0; i < Initiator.getChunksforBackup(); i++){ // por cada chunk 
				int count = 0;
				for(int j = 0; j < IDs.size(); j++){// por cada message Stored de um chunkID
					String name = Initiator.getFile_Hash_Name() + (i+1); //ChunkID 
					if(name.equals(IDs.get(j))){ // se o hashname + chunkNo ==  chunkID da stored message received
						count++;
						chunks.set(i,count);// muda a posicao i para o valor do count
						setRemoteChunks(name, count);
					}
				}
			}			
		}
		for(int i = 0; i < Initiator.getChunksforBackup(); i++){
			if(chunks.get(i) >= Initiator.getReplicationDegree_backup()){
				System.out.println("\n Numero de chunksNo :" + (i+1) + " Enough Stored Messages received : " + chunks.get(i));
			}
			else {
				System.out.println("\n Numero de chunksNo :" + (i+1) + " lower than the replication degree : " + chunks.get(i));
				String fileID = Chunk.getChunksCreated().get(i).getFileID();
				int chunkNo = Chunk.getChunksCreated().get(i).getChunkNo();
				int replication_degree = Chunk.getChunksCreated().get(i).getReplication_degree();
				byte[] body = Chunk.getChunksCreated().get(i).getChunkData();
				try{
					byte[] message_to_Send = CreateMessage.MessageToSendPut(Initiator.getVersion(), getPeerID(), fileID, chunkNo, replication_degree, body);
					DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send , message_to_Send.length , mcastAddr, Initiator.getMcastPORT_MD_Channel());
					socket_backup_reenvia.send(msgDatagram_to_send);
					System.out.println("\n Iniciator send message to: " + mcastAddr + "----" + Initiator.getMcastPORT_MD_Channel());
				}catch (Exception e){e.printStackTrace();}
				try {
					Thread.sleep((long)(Math.random() * 400));
				} catch (InterruptedException e) {e.printStackTrace();}				
				reSend = true;
			}
		}
		if(Initiator.getPeerID() == peerID){
			if(reSend){
				System.out.println("\n Resending all chunks of file : " + Initiator.getFile_REAL_Name() + " with replication degree : " + Initiator.getReplicationDegree_backup() );
				try {
					System.out.println("Waiting to receive the Stored Messages ...");
					Thread.sleep(15000);
				}catch (Exception e) {}
				ReenviaPut();
			}
			else{
				for (int j = 0; j < 25; ++j) System.out.println();
				System.out.println("\n******** Received the minimum Stored Messages for each Chunk ********\n");
				remoteChunks.forEach((k,v)-> System.out.println(k+", "+v));
			}
		}
	}

	public synchronized void ReenviaGetChunk() throws NoSuchAlgorithmException, IOException, InterruptedException{
		MulticastSocket socket_getChunk = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());
		ArrayList<String> IDs = DatabaseChunksReceived.getReceivedChunksIDFromCHUNK();
		ArrayList<Integer> chunks = new ArrayList<Integer>(Initiator.getChunksforRestore());
		boolean reSend = false;
		for (int i = 0; i < Initiator.getChunksforRestore() +1; i++) {chunks.add(0);}
		if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu restore
			for(int i = 0; i < Initiator.getChunksforRestore(); i++){ // por cada chunk 
				int count = 0;
				for(int j = 0; j < IDs.size(); j++){// por cada message chunk de um chunkID
					String name = Initiator.getFile_Hash_Name_Restore() + (i+1); //ChunkID 
					if(name.equals(IDs.get(j))){ // se o hashname + chunkNo ==  chunkID da stored message received
						count++;
						chunks.set(i,count);// muda a posicao i para o valor do count
					}
				}
			}			
		}
		for(int i = 0; i < Initiator.getChunksforRestore(); i++){
			if(chunks.get(i) >= 1){
				System.out.println("\n Numero de chunksNo :" + (i+1) + " Enough chunks received : " + chunks.get(i));
			}
			else {
				System.out.println("\n Numero de chunksNo :" + (i+1) + " lower chunks than we expected : " + chunks.get(i));
				try{
					byte[] message_to_Send = CreateMessage.MessageToSendGetChunk(Initiator.getVersion(), getPeerID(), Initiator.getFile_Hash_Name_Restore() + (i+1), (i+1));
					DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send , message_to_Send.length , Initiator.getMcastAddr_Channel_MC(), Initiator.getMcastPORT_MC_Channel());
					socket_getChunk.send(msgDatagram_to_send);

					System.out.println("\n\n\n ReSend getCuunk message to: " + Initiator.getMcastAddr_Channel_MC() + "----" + Initiator.getMcastPORT_MC_Channel());
					System.out.println("\n" + new String(message_to_Send) + "\n\n\n");	
					try {
						Thread.sleep((long)(Math.random() * 1000));
					} catch (InterruptedException e) {e.printStackTrace();}
				}catch (Exception e){e.printStackTrace();}
				reSend = true;
			}
		}
		if(reSend){
			System.out.println("chunks for restore : " + Initiator.getChunksforRestore());
			System.out.println("numero de CHunkID : " + IDs.size());
			try {
				System.out.println("Waiting to receive the chunks to Restore ...");
				Thread.sleep(15000);
			}catch (Exception e) {}
			ReenviaGetChunk();
		}
		else{
			for (int j = 0; j < 25; ++j) System.out.println();
			System.out.println("\n******** Received all chunks to restore the File ********\n");
			MergeChunks.MergeChunks(Chunk.getChunksRestore(), "merged_file__" + Initiator.getFile_REAL_Name_Restore());
			Chunk.getChunksRestore().clear();
		}
	}

	public int getPeerID() {
		return peerID;
	}
	public int getChunkNoVerify() {
		return chunkNoVerify;
	}
	public void setChunkNoVerify(int chunkNoVerify) {
		this.chunkNoVerify = chunkNoVerify;
	}
	public int getPacketsReceived() {
		return packetsReceived;
	}
	public void setPacketsReceived(int packetsReceived) {
		this.packetsReceived = packetsReceived;
	}
	public int getSenderPeerID() {
		return senderPeerID;
	}
	public void setSenderPeerID(int senderPeerID) {
		this.senderPeerID = senderPeerID;
	}
	public boolean isMinimum() {
		return minimum;
	}
	public void setMinimum(boolean minimum) {
		this.minimum = minimum;
	}
	public boolean isReceiveone() {
		return receiveone;
	}
	public void setReceiveone(boolean receiveone) {
		this.receiveone = receiveone;
	}
	public boolean isStoredMessage() {
		return storedMessage;
	}
	public void setStoredMessage(boolean storedMessage) {
		storedMessage = storedMessage;
	}
	public boolean isGetChunkMessage() {
		return getChunkMessage;
	}
	public void setGetChunkMessage(boolean getChunkMessage) {
		this.getChunkMessage = getChunkMessage;
	}
	public boolean isReceivedChunk() {
		return receivedChunk;
	}
	public void setReceivedChunk(boolean receivedChunk) {
		this.receivedChunk = receivedChunk;
	}
	public boolean isReceivedOneChunk() {
		return receivedOneChunk;
	}
	public void setReceivedOneChunk(boolean receivedOneChunk) {
		this.receivedOneChunk = receivedOneChunk;
	}
	public static MulticastSocket getSocket_backup() {
		return socket_backup;
	}
	public static void setSocket_backup(MulticastSocket socket_backup) {
		Peer.socket_backup = socket_backup;
	}
	public static MulticastSocket getSocket_restore() {
		return socket_restore;
	}
	public static void setSocket_restore(MulticastSocket socket_restore) {
		Peer.socket_restore = socket_restore;
	}
	public MulticastSocket getMcSocket_REMOVED_SEND() {
		return mcSocket_REMOVED_SEND;
	}
	public void setMcSocket_REMOVED_SEND(MulticastSocket mcSocket_REMOVED_SEND) {
		this.mcSocket_REMOVED_SEND = mcSocket_REMOVED_SEND;
	}
	public static HashMap<String,Integer> getRemoteChunks() {
		return remoteChunks;
	}
	public static void setRemoteChunks(String key, int value) {
		remoteChunks.put(key, value);
	}

}
