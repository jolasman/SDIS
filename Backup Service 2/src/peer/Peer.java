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
import java.util.List;
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
						Thread md_msg = new Thread(){
							public void run(){
								System.out.println("\nMcData Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
								byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getData().length);	//msg recebida	//msg recebida

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
										setSenderPeerID(senderID_msg);
										ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
										ArrayList<String> chunksalreadyReceived = DatabaseChunksReceived.getReceivedChunksID();
										for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
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
										}
										if(!received){
											if(!stored){
												Chunk newChunk = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, repl_degree_msg, local_path);
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

	public synchronized void McChannel() throws IOException{
		mcSocket_MC_Channel = new MulticastSocket(Initiator.getMcastPORT_MC_Channel());
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
								if(!isReceiveone()){
									try {
										System.out.println("\nPeer : " + getPeerID() + " Testing the option of reSend the Chunk files\n");
										ReenviaPut();
									} catch (NoSuchAlgorithmException | IOException e) {
										System.out.println("\nPeer : " + getPeerID() + " do not receive any message");
										e.printStackTrace();
									} catch (InterruptedException e) {e.printStackTrace();}
								}
							}

						}, 20000);
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
						}, 40000);
					}
				}
				while(true){
					try {
						mcSocket_MC_Channel.receive(packet);
						System.out.println("\nMc Control Channel received a new message from: " + packet.getAddress() + " ----- " + packet.getPort() + "\n");
						byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getData().length);	//msg recebida	//msg recebida
						//String aa = new String(msg_received);
						//System.out.println("\n mensagem recebida no MC : " + aa);
						String fileID_msg = MessageManager.SeparateMsgContentStored(msg_received).getFileID();
						int chunkNo_msg = MessageManager.SeparateMsgContentStored(msg_received).getChunkNo();
						String type_msg = MessageManager.SeparateMsgContentStored(msg_received).getType();
						char[] version = MessageManager.SeparateMsgContentStored(msg_received).getVersion();
						int senderID_msg = MessageManager.SeparateMsgContentStored(msg_received).getSenderID();
						boolean stored = false;
						boolean received = false;
						String chunkIDtoCheck = fileID_msg;
						//System.out.println("\n type: " + type_msg );
						if(type_msg.equals("STORED")){
							setReceiveone(true);
							if(senderID_msg == Initiator.getPeerID()){
								// se for do proprio nao faz nada
							}else{
								DatabaseChunksReceived.setReceivedChunksID(fileID_msg+chunkNo_msg);
							}
						}
						else if(type_msg.equals("GETCHUNK")){
							setReceiveone(true);
							setGetChunkMessage(true);
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

												byte[] message_to_MDR = CreateMessage.MessageToSendChunk(version, getPeerID(), fileID_msg, chunkNo_msg, chunkFile.getChunkData());
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

						}
						else{
							System.out.println("\nERROR: Mc Control Channel not received a STORED or GETCHUNK message type\n" + type_msg);
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
				if(Initiator.isRestoreMode()){
					if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu backup
						new Timer().schedule(new TimerTask() {          
							@Override
							public void run() {
								if(!isReceivedOneChunk()){
									try {
										System.out.println("\nPeer : " + getPeerID() + " Testing the option of reSend the Chunk files\n");
										ReenviaGetChunk();
									} catch (NoSuchAlgorithmException | IOException e) {
										System.out.println("\nPeer : " + getPeerID() + " do not receive any message");
										e.printStackTrace();
									} catch (InterruptedException e) {e.printStackTrace();}
								}
							}

						}, 20000);
						new Timer().schedule(new TimerTask() {          
							@Override
							public void run() {
								try {
									System.out.println("\nPeer : " + getPeerID() + " Testing the option of reSend the Chunk files. Just some amount of Stored Messages received\n");
									ReenviaGetChunk();
								} catch (NoSuchAlgorithmException | IOException e) {
									System.out.println("\nPeer : " + getPeerID() + " do not receive and can reSend");
									e.printStackTrace();
								} catch (InterruptedException e) {e.printStackTrace();}
							}
						}, 40000);
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
						byte[] msg_received = Arrays.copyOfRange(packet.getData(), 0, packet.getData().length);	//msg recebida

						String fileID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getFileID();
						int chunkNo_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getChunkNo();
						byte[] filedata_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getBody();
						String type_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getType();
						char[] version = MessageManager.SeparateMsgContentCHUNK(msg_received).getVersion();
						int senderID_msg = MessageManager.SeparateMsgContentCHUNK(msg_received).getSenderID();

						if(type_msg.equals("CHUNK")){
							if(senderID_msg == Initiator.getPeerID()){
								Chunk newChunk1 = new Chunk(fileID_msg, chunkNo_msg, filedata_msg, 2);
								Chunk.setChunksRestore(chunkNo_msg + "",newChunk1);

								System.out.println("Peer: " + peerID + " stored chunk received in MC Data Recovery Channel");
								setChunkNoVerify(chunkNo_msg); //nao sei o que faz mas deixa estar xD ja nao me lembro xD

								DatabaseChunksReceived.setReceivedChunksIDFromCHUNK(fileID_msg + chunkNo_msg);

							}else{
							}
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
		ArrayList<String> IDs = DatabaseChunksReceived.getReceivedChunksID();
		ArrayList<Integer> chunks = new ArrayList<Integer>(Initiator.getChunksforBackup());
		boolean reSend = false;
		for (int i = 0; i < Initiator.getChunksforBackup() +1; i++) {
			chunks.add(0);
		}
		if(Initiator.getPeerID() == peerID){ // verifica se e o peer que pediu backup
			for(int i = 0; i < Initiator.getChunksforBackup(); i++){ // por cada chunk 
				int count = 0;
				for(int j = 0; j < IDs.size(); j++){// por cada message Stored de um chunkID
					String name = Initiator.getFile_Hash_Name() + (i+1); //ChunkID 
					if(name.equals(IDs.get(j))){ // se o hashname + chunkNo ==  chunkID da stored message received
						count++;
						chunks.set(i,count);// muda a posicao i para o valor do count
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
				reSend = true;
			}
		}
		if(Initiator.getPeerID() == peerID){
			if(reSend){
				System.out.println("\n Resending all chunks of file : " + Initiator.getFile_REAL_Name() + " with replication degree : " + Initiator.getReplicationDegree_backup() );
				System.out.println("chunks for backup " + Initiator.getChunksforBackup());
				System.out.println("replication degree" + Initiator.getReplicationDegree_backup());
				System.out.println("numero de CHunkID" + IDs.size());
				for (int j = 0; j < 25; ++j) System.out.println();
				Initiator.setFirstTimeBackup(false);
				Initiator.BackupFileInitiator(Initiator.getFile_REAL_Name(), Initiator.getReplicationDegree_backup());
			}
			else{
				System.out.println("Received the minimum Stored Messages for each Chunk");
			}
		}

	}

	public synchronized void ReenviaGetChunk() throws NoSuchAlgorithmException, IOException, InterruptedException{
		ArrayList<String> IDs = DatabaseChunksReceived.getReceivedChunksIDFromCHUNK();
		ArrayList<Integer> chunks = new ArrayList<Integer>(Initiator.getChunksforRestore());
		boolean reSend = false;
		for (int i = 0; i < Initiator.getChunksforRestore() +1; i++) {
			chunks.add(0);
		}
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
				reSend = true;
			}
		}
		if(reSend){
			System.out.println("\n Resending all GETchunks messages to restore the file");
			System.out.println("chunks for resotre " + Initiator.getChunksforBackup());
			System.out.println("replication degree" + Initiator.getReplicationDegree_backup());
			System.out.println("numero de CHunkID" + IDs.size());
			for (int j = 0; j < 25; ++j) System.out.println();
			Initiator.setFirstTimeRestore(false);
			Initiator.RestoreFiles(Initiator.getFile_REAL_Name_Restore());
		}
		else{
			System.out.println("Received all chunks to restore the File");

			MergeChunks.MergeChunks(Chunk.getChunksRestore(), "merged_file__" + Initiator.getFile_REAL_Name());
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

}
