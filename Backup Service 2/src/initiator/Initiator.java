package initiator;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import algorithms.SHA256;
import chunks.Chunk;
import database.DatabaseChunksReceived;
import database.DatabaseChunksStored;
import database.DatabasePeerID;
import fileManagement.*;
import message.CreateMessage;
import peer.Peer;

public class Initiator {
	private volatile static MulticastSocket socket_backup; 
	private volatile static MulticastSocket socket_restore; 
	private static int PORT_MC_Channel = 5000;
	private static int PORT_MD_Channel = 5001;
	private static int PORT_MDR_Channel = 5002;
	private static InetAddress mcastAddr_Channel_MC;
	private static InetAddress mcastAddr_Channel_MD;
	private static InetAddress mcastAddr_Channel_MDR;
	private static int mcastPORT_MC_Channel;
	private static int mcastPORT_MD_Channel;
	private static int mcastPORT_MDR_Channel;

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

		if(args.length != 8){			
			System.out.println("\nError : usage <protocol_version> <peerID> <MC mcasIP> <MC mcastPORT> <MD mcasIP> <MD mcastPORT>");
			return;
		}

		char[] version = new char[3];
		version[0] = args[0].charAt(0);
		version[1] = args[0].charAt(1);
		version[2] = args[0].charAt(2);
		int peerID = Integer.parseInt(args[1]);
		mcastAddr_Channel_MC = InetAddress.getByName(args[2]);
		mcastPORT_MC_Channel = Integer.parseInt(args[3]);
		mcastAddr_Channel_MD = InetAddress.getByName(args[4]);
		mcastPORT_MD_Channel = Integer.parseInt(args[5]);
		mcastAddr_Channel_MDR = InetAddress.getByName(args[6]);
		mcastPORT_MDR_Channel = Integer.parseInt(args[7]);


		Scanner in = new Scanner(System.in);
		System.out.println("\n1. Backup File");
		System.out.println("2. Restore File");
		System.out.println("3. Delete File");
		System.out.println("4. Manage Local Service Storage");
		System.out.println("5. Retrieve Local Service State information");
		System.out.println("0. Quit");
		// handle user commands
		boolean quit = false;
		int menuItem;
		System.out.print("\nChoose menu item: ");
		menuItem = in.nextInt();
		switch (menuItem) {
		case 1:
			File file = new File("./ChunksReceived");
			if(file.listFiles() == null){ 
				System.out.println("nenhum ficheiro na pasta");
			}
			else{
				File afile[] = file.listFiles();
				int i = 0;
				for (int j = afile.length; i < j; i++) {
					File arquivos = afile[i];
					System.out.println("Load chunks received: " + arquivos.getName());
					DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
				}
			}

			database.DatabasePeerID.StorePeerID(peerID);
			Peer newPeer = new Peer(50);

			FileManager files = new FileManager();
			if(files.isHaveFiles()){
				InetAddress mcastAddr = mcastAddr_Channel_MD;
				socket_backup = new MulticastSocket(mcastPORT_MD_Channel);
				socket_backup.joinGroup(mcastAddr);
				socket_backup.setTimeToLive(1);

				for(int i = 0; i< Chunk.getChunksCreated().size(); i++){
					String fileID = Chunk.getChunksCreated().get(i).getFileID();
					int chunkNo = Chunk.getChunksCreated().get(i).getChunkNo();
					int replication_degree = Chunk.getChunksCreated().get(i).getReplication_degree();
					byte[] body = Chunk.getChunksCreated().get(i).getChunkData();
					Thread initiator = new Thread(){
						public void run(){
							try{
								String message_to_Send = CreateMessage.MessageToSendPut(version, peerID, fileID, chunkNo, replication_degree, body);
								DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr, mcastPORT_MD_Channel);
								socket_backup.send(msgDatagram_to_send);
								System.out.println("\n Iniciator send message to: " + mcastAddr + "----" + mcastPORT_MD_Channel);
							}catch (Exception e){e.printStackTrace();}
						}
					};
					initiator.start();
				}
			}
			else{}
			// do something...
			break;
		case 2:
			File file_restore_received = new File("./ChunksReceived");
			if(file_restore_received.listFiles() == null){ 
				System.out.println("nenhum ficheiro na pasta");
			}
			else{
				File afile[] = file_restore_received.listFiles();
				int i = 0;
				for (int j = afile.length; i < j; i++) {
					File arquivos = afile[i];
					System.out.println("Loading chunks received: " + arquivos.getName());
					DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
				}
			}
			File file_restore_stored = new File("./Chunks");
			if(file_restore_stored.listFiles() == null){ 
				System.out.println("nenhum ficheiro na pasta");
			}
			else{
				File afile[] = file_restore_stored.listFiles();
				int i = 0;
				System.out.println("\n");
				for (int j = afile.length; i < j; i++) {
					File arquivos = afile[i];
					System.out.println("Loading chunks stored: " + arquivos.getName());
					DatabaseChunksStored.StoreChunkID(arquivos.getName());
				}
			}
			
			
			DatabasePeerID.StorePeerID(peerID);
			Peer newPeer_restore = new Peer(50);
			InetAddress mcastAddr = mcastAddr_Channel_MC;
			socket_restore = new MulticastSocket(mcastPORT_MC_Channel);
			socket_restore.joinGroup(mcastAddr);
			socket_restore.setTimeToLive(1);

			Thread initiator_restore = new Thread(){
				public void run(){
					System.out.println("SHA256 File_Name(with chunkNo) and chunkNo separated too. <file_name> <chunkNo>" );
					Scanner in = new Scanner(System.in);
					String response_restore = in.nextLine();
					String rsp_trimmed = response_restore.trim();
					String[] final_resp = rsp_trimmed.split(" ");
					String fileID = final_resp[0];
					int chunkNo = Integer.parseInt(final_resp[1]);
					try{
						String message_to_Send = CreateMessage.MessageToSendGetChunk(version, peerID, fileID, chunkNo);
						DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr, mcastPORT_MC_Channel);
						socket_restore.send(msgDatagram_to_send);
						System.out.println("\n Iniciator send message to: " + mcastAddr + "----" + mcastPORT_MC_Channel);
					}catch (Exception e){e.printStackTrace();}
				}
			};
			initiator_restore.start();

			//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
			
			
			// do something...
			break;
		case 3:

			// do something...
			break;
		case 4:

			// do something...
			break;
		case 5:

			// do something...
			break;
		case 0:
			quit = true;
			break;
		default:
			System.out.println("Invalid choice.");

		}
	}
	
	public static InetAddress getMcastAddr_Channel_MDR() {
		return mcastAddr_Channel_MDR;
	}
	public static void setMcastAddr_Channel_MDR(InetAddress mcastAddr_Channel_MDR) {
		Initiator.mcastAddr_Channel_MDR = mcastAddr_Channel_MDR;
	}
	public static int getMcastPORT_MDR_Channel() {
		return mcastPORT_MDR_Channel;
	}
	public static void setMcastPORT_MDR_Channel(int mcastPORT_MDR_Channel) {
		Initiator.mcastPORT_MDR_Channel = mcastPORT_MDR_Channel;
	}
	public static int getPORT_MC_Channel() {
		return PORT_MC_Channel;
	}
	public static void setPORT_MC_Channel(int pORT_MC_Channel) {
		PORT_MC_Channel = pORT_MC_Channel;
	}
	public static InetAddress getMcastAddr_Channel_MC() {
		return mcastAddr_Channel_MC;
	}
	public static void setMcastAddr_Channel_MC(InetAddress mcastAddr_Channels) {
		Initiator.mcastAddr_Channel_MC = mcastAddr_Channels;
	}
	public static int getMcastPORT_MC_Channel() {
		return mcastPORT_MC_Channel;
	}
	public static void setMcastPORT_MC_Channel(int mcastPORT_MC_Channel) {
		Initiator.mcastPORT_MC_Channel = mcastPORT_MC_Channel;
	}
	public static InetAddress getMcastAddr_Channel_MD() {
		return mcastAddr_Channel_MD;
	}
	public static void setMcastAddr_Channel_MD(InetAddress mcastAddr_Channel_MD) {
		Initiator.mcastAddr_Channel_MD = mcastAddr_Channel_MD;
	}
	public static int getPORT_MD_Channel() {
		return PORT_MD_Channel;
	}
	public static void setPORT_MD_Channel(int pORT_MD_Channel) {
		PORT_MD_Channel = pORT_MD_Channel;
	}
	public static int getMcastPORT_MD_Channel() {
		return mcastPORT_MD_Channel;
	}
	public static void setMcastPORT_MD_Channel(int mcastPORT_MD_Channel) {
		Initiator.mcastPORT_MD_Channel = mcastPORT_MD_Channel;
	}
	public static int getPORT_MDR_Channel() {
		return PORT_MDR_Channel;
	}
	public static void setPORT_MDR_Channel(int pORT_MDR_Channel) {
		PORT_MDR_Channel = pORT_MDR_Channel;
	}
}
