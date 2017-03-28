package initiator;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	private volatile static MulticastSocket socket_Peers_Receive; 
	private volatile static MulticastSocket socket_Peers_Send; 
	private static int PORT_MC_Channel = 5000;
	private static int PORT_MD_Channel = 5001;
	private static int PORT_MDR_Channel = 5002;
	private static int PORT_Peers_Channel = 5003;
	private static InetAddress mcastAddr_Channel_MC;
	private static InetAddress mcastAddr_Channel_MD;
	private static InetAddress mcastAddr_Channel_MDR;
	private static InetAddress mcastAddr_Peers_Channel;
	private static InetAddress mcastAddr_Peers_Channel_Receive;
	private static int mcastPORT_MC_Channel;
	private static int mcastPORT_MD_Channel;
	private static int mcastPORT_MDR_Channel;
	private static int mcastPORT_Peers_Channel;
	private static int mcastPORT_Peers_Channel_receive;
	private static char[] version = new char[3];
	private static int peerID;
	private static int filesNo;
	private static String file_Real_Name;
	private static int NUMBER_OF_PEERS;
	private static int activePeers;

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
		if(args.length != 8){			
			System.out.println("\nError : usage <protocol_version> <peerID> <MC mcasIP> <MC mcastPORT> <MD mcasIP> <MD mcastPORT>");
			return;
		}
		version[0] = args[0].charAt(0);
		version[1] = args[0].charAt(1);
		version[2] = args[0].charAt(2);
		peerID = Integer.parseInt(args[1]);
		mcastAddr_Channel_MC = InetAddress.getByName(args[2]);
		mcastPORT_MC_Channel = Integer.parseInt(args[3]);
		mcastAddr_Channel_MD = InetAddress.getByName(args[4]);
		mcastPORT_MD_Channel = Integer.parseInt(args[5]);
		mcastAddr_Channel_MDR = InetAddress.getByName(args[6]);
		mcastPORT_MDR_Channel = Integer.parseInt(args[7]);

		mcastAddr_Peers_Channel = InetAddress.getByName("225.4.5.7");
		mcastAddr_Peers_Channel_Receive = InetAddress.getByName("225.4.5.8");
		mcastPORT_Peers_Channel = 1111;
		mcastPORT_Peers_Channel_receive = 7777;


		Scanner in = new Scanner(System.in);
		System.out.println("\n1. Backup File");
		System.out.println("2. Restore File");
		System.out.println("3. Delete File");
		System.out.println("4. Just iniciate the Peer (connect to the multicast channels)");
		System.out.println("0. Quit");
		boolean quit = false;
		int menuItem;
		System.out.print("\nChoose menu item: ");
		menuItem = in.nextInt();
		switch (menuItem) {
		case 1: //backup
			Scanner resp_backup = new Scanner(System.in);
			System.out.print("\nChoose file to Backup and the Replication Degree:  Example :<file.pdf> <2>\n");
			String response_backup = resp_backup.nextLine();
			String rsp_trimmed_backup = response_backup.trim();
			String[] final_Resp_backup = rsp_trimmed_backup.split(" ");
			String file_backup = final_Resp_backup[0];
			int replication_degree_backup = Integer.parseInt(final_Resp_backup[1]);

			ReceiveKnowPeersActive();
			AlwaysSendingActvite();
			//print funny loading text
			ReceivePeersConsole();
			TimeUnit.SECONDS.sleep(1);
			if(replication_degree_backup <= getNUMBER_OF_PEERS()){
				System.out.println("\nStarting the backup of the file: " + file_backup);
				BackupFileInitiator(file_backup,replication_degree_backup);
			}else{
				System.out.println("\nYou need "+ replication_degree_backup +" Peers to backup! But you only have "+ getNUMBER_OF_PEERS());
			}

			break;
		case 2: //restore
			Scanner resp_restore = new Scanner(System.in);
			System.out.print("\nChoose file to Restore and the Replication Degree:  Example :<file.pdf> <2>\n");
			String response_restore = resp_restore.nextLine();
			String rsp_trimmed_restore = response_restore.trim();
			String[] final_Resp_restore = rsp_trimmed_restore.split(" ");
			String file_restore = final_Resp_restore[0];
			int replication_degree_restore = Integer.parseInt(final_Resp_restore[1]);

			ReceiveKnowPeersActive();
			AlwaysSendingActvite();
			//print funny loading text
			ReceivePeersConsole();
			TimeUnit.SECONDS.sleep(1);
			if(replication_degree_restore <= getNUMBER_OF_PEERS()){
				System.out.println("\nStarting the restore of the file: " + file_restore);
				RestoreFiles(file_restore,replication_degree_restore );
			}else{
				System.out.println("\nYou need "+ replication_degree_restore +" Peers to restore! But you only have "+ getNUMBER_OF_PEERS());
			}

			break;
		case 3:

			ReceiveKnowPeersActive();
			AlwaysSendingActvite();
			//print funny loading text
			ReceivePeersConsole();
			TimeUnit.SECONDS.sleep(1);
			DeleteFiles();
			break;
		case 4:
			File file = new File("./ChunksReceived");
			if(file.listFiles() == null){ 
				System.out.println("nenhum ficheiro na pasta ChunksReceived. Peer:" + peerID );
			}
			else{
				File afile[] = file.listFiles();
				int i = 0;
				for (int j = afile.length; i < j; i++) {
					File arquivos = afile[i];
					System.out.println("Peer : " + peerID + " Load chunks received: " + arquivos.getName());
					DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
				}
			}
			Peer newPeer = new Peer(peerID);
			AlwaysSendingActvite();
			break;			
		case 0:
			quit = true;
			break;
		default:
			System.out.println("Invalid choice.");
		}
	}

	//backup
	public synchronized static void BackupFileInitiator(String fileName, int repl_degree) throws IOException, NoSuchAlgorithmException{
		DatabasePeerID.StorePeerID(peerID);
		File file = new File("./ChunksReceived");
		if(file.listFiles() == null){ 
			System.out.println("\nNenhum ficheiro na pasta ChunksReceived. Peer:" + peerID );
		}
		else{
			File afile[] = file.listFiles();
			int i = 0;
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				System.out.println("Peer : " + peerID + " Load chunks received: " + arquivos.getName());
				DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
			}
		}
		Peer newPeer = new Peer(peerID);

		FileManager files = new FileManager(fileName, repl_degree);
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
						try {
							Thread.sleep((long)(Math.random() * 400));
						}  catch (InterruptedException e1) {
							System.out.println("\n Thread  Backup Initiator can not sleep");
							e1.printStackTrace();
						}
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
	}

	public synchronized static void RestoreFiles(String fileName, int repl_degree) throws IOException, NoSuchAlgorithmException{
		socket_restore = new MulticastSocket(getMcastPORT_Peers_Channel_receive());
	
		File file_restore_stored = new File("./Chunks");
		if(file_restore_stored.listFiles() == null){ 
			System.out.println("nenhum ficheiro na pasta");
		}
		else{ //carrega os ficheiros que estao na pagina Chunks
			File afile[] = file_restore_stored.listFiles();
			int i = 0;
			System.out.println("\n");
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				System.out.println("Loading chunks stored: " + arquivos.getName());
				DatabaseChunksStored.StoreChunkID(arquivos.getName());
			}
		}
		File fileArgs = new File("./Files/" + fileName); 
		ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
		boolean haveChunk= true;
		String fileHashName = SHA256.ToSha256(fileArgs);
		int chunkNO = 1;		
		do{
			String chunkIDtoCheck = fileHashName + chunkNO;
			for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
				if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
					try{
						System.out.println("\n chunk   " + chunksAlreadyStored.get(i));
						char[] version = {'1','.','0'};
						String extensao = fileName.substring(fileName.lastIndexOf("."), fileName.length());

						String message_to_Send = CreateMessage.MessageToSendGetChunk(version, peerID, fileHashName + chunkNO, chunkNO, extensao);
						DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , getMcastAddr_Channel_MC(), getMcastPORT_MC_Channel());
						TimeUnit.SECONDS.sleep(1);
						socket_restore.send(msgDatagram_to_send);
						
						System.out.println("\n Iniciator send message to: " + getMcastAddr_Channel_MC() + "----" + getMcastPORT_MC_Channel());
						System.out.println("\n" + message_to_Send);
						chunkNO++;
						//message_to_Send = "";

					}catch (Exception e){
						e.printStackTrace();
					}
					

				}else{
					if(i == chunksAlreadyStored.size() ){
						haveChunk = false;
					}
				}
			}
		}while(haveChunk);
		filesNo = chunkNO;
		socket_restore.close();

	}

	public synchronized static void DeleteFiles() throws IOException, NoSuchAlgorithmException{

	}
/*
	public synchronized static void BackupAFile(String fileName, int repl_degree, int peerID_rec) throws IOException, NoSuchAlgorithmException{

		mcastAddr_Channel_MC = InetAddress.getByName("225.4.5.6");
		mcastPORT_MC_Channel = 4444;
		mcastAddr_Channel_MD = InetAddress.getByName("225.4.5.6");
		mcastPORT_MD_Channel = 5555;
		mcastAddr_Channel_MDR = InetAddress.getByName("225.4.5.6");
		mcastPORT_MDR_Channel = 2222;

		File file = new File("./ChunksReceived");
		if(file.listFiles() == null){ 
			System.out.println("nenhum ficheiro na pasta");
		}
		else{ //carregar ficheiros que estão na pasta Chunks
			File afile[] = file.listFiles();
			int i = 0;
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				System.out.println("Load chunks received: " + arquivos.getName());
				DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
			}
		}
		Peer newPeer = new Peer(peerID_rec);

		FileManager files = new FileManager(fileName, repl_degree);
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
	}

	public synchronized static void RestoreAFile(String fileName, int peerID_R) throws IOException, NoSuchAlgorithmException{
		mcastAddr_Channel_MC = InetAddress.getByName("225.4.5.6");
		mcastPORT_MC_Channel = 4444;
		mcastAddr_Channel_MD = InetAddress.getByName("225.4.5.6");
		mcastPORT_MD_Channel = 5555;
		mcastAddr_Channel_MDR = InetAddress.getByName("225.4.5.6");
		mcastPORT_MDR_Channel = 2222;

		InetAddress mcastAddr = mcastAddr_Channel_MC;
		socket_restore = new MulticastSocket(mcastPORT_MC_Channel);
		//socket_restore.joinGroup(mcastAddr);
		//socket_restore.setTimeToLive(1);		

		File file_restore_stored = new File("./Chunks");
		if(file_restore_stored.listFiles() == null){ 
			System.out.println("nenhum ficheiro na pasta");
		}
		else{ //carrega os ficheiros que estao na pagina Chunks
			File afile[] = file_restore_stored.listFiles();
			int i = 0;
			System.out.println("\n");
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				System.out.println("Loading chunks stored: " + arquivos.getName());
				DatabaseChunksStored.StoreChunkID(arquivos.getName());
			}
		}
		File fileArgs = new File("./Files/" + fileName); 
		ArrayList<String> chunksAlreadyStored = DatabaseChunksStored.getChunkIDStored();
		boolean haveChunk= true;
		String fileHashName = SHA256.ToSha256(fileArgs);
		int chunkNO = 1;		
		do{
			String chunkIDtoCheck = fileHashName + chunkNO;
			for(int i = 0; i< chunksAlreadyStored.size(); i++ ){
				if(chunkIDtoCheck.equals(chunksAlreadyStored.get(i))){
					try{
						System.out.println("\n chunk   " + chunksAlreadyStored.get(i));
						char[] version = {'1','.','0'};
						String extensao = fileName.substring(fileName.lastIndexOf("."), fileName.length());
						String message_to_Send = CreateMessage.MessageToSendGetChunk(version, 33, fileHashName + chunkNO, chunkNO, extensao);
						DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr, mcastPORT_MC_Channel);
						TimeUnit.SECONDS.sleep(1);
						socket_restore.send(msgDatagram_to_send);
						message_to_Send = "";
						System.out.println("\n Iniciator send message to: " + mcastAddr + "----" + mcastPORT_MC_Channel);
						System.out.println("\n" + message_to_Send);
						chunkNO++;

					}catch (Exception e){
						e.printStackTrace();
					}
					

				}else{
					if(i == chunksAlreadyStored.size() ){
						haveChunk = false;
					}
				}
			}
		}while(haveChunk);
		filesNo = chunkNO;
		socket_restore.close();

	}
*/

	public static void ReceivePeersConsole() throws InterruptedException{
		System.out.print("\n\nReceiving how many Peers are in the System ");
		System.out.print("[0%--");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("----");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("--");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-------");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("---");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("-");
		TimeUnit.SECONDS.sleep(1);
		System.out.print("---------->100%]");
		TimeUnit.SECONDS.sleep(1);
		System.out.println("\n\n Done! We have " + getNUMBER_OF_PEERS() + " Peers actives in the System");
		activePeers = getNUMBER_OF_PEERS();
	}


	public synchronized static void ReceiveKnowPeersActive() throws IOException{
		InetAddress mcastAddr = getMcastAddr_Peers_Channel_Receive();
		socket_Peers_Receive = new MulticastSocket(getMcastPORT_Peers_Channel_receive());
		socket_Peers_Receive.joinGroup(mcastAddr);

		Thread receivingPeers = new Thread(){
			public void run(){
				byte[] buffer = new byte[65000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				long start_time = System.currentTimeMillis();
				long wait_time = 10000;
				long end_time = start_time + wait_time;
				boolean existe = false;
				ArrayList<Integer> arrayPeerIDs = new ArrayList<Integer>();
				while (System.currentTimeMillis() < end_time){ //System.currentTimeMillis() < end_time
					try {
						socket_Peers_Receive.receive(packet);
						String msg = new String(packet.getData(), packet.getOffset(),packet.getLength());

						if(Integer.parseInt(msg) < 100 && Integer.parseInt(msg) > 0 ){
							for(int i = 0; i < arrayPeerIDs.size(); i++){
								if(arrayPeerIDs.get(i).equals(Integer.parseInt(msg))){
									existe = true;
									break;
								}
							}
							if(!existe){
								NUMBER_OF_PEERS++;
								arrayPeerIDs.add(Integer.parseInt(msg));
							}
						}else{
							System.out.println("PeerID: "+ msg +" must be:  0 < PeerID > 100");
						}
					} catch (IOException e) {
						System.out.println("can't receive in socker_Peers_receive");
						e.printStackTrace();
					}
				}

			}
		};receivingPeers.start();
	}

	public synchronized static void SendKnowPeersActive() throws IOException{
		InetAddress mcastAddr = getMcastAddr_Peers_Channel_Receive();
		socket_Peers_Send = new MulticastSocket(getMcastPORT_Peers_Channel_receive());
		String message_to_Send = peerID + "";
		DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr, getMcastPORT_Peers_Channel_receive());
		try{
			socket_Peers_Send.send(msgDatagram_to_send);
		} catch (Exception e) {
			System.out.println("can't send in socket_Peers_Send");
		}
		socket_Peers_Send.close();
	}

	public synchronized static void AlwaysSendingActvite(){
		Runnable helloRunnable = new Runnable() {// envia que esta activo de 10 em 10 segundos
			public void run() {
				try {
					SendKnowPeersActive();
				} catch (IOException e) {
					System.out.println("Error : trying to send that i'm active in the system.");
					e.printStackTrace();
				}
			}
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
		
	}
	
	
	public static char[] getVersion() {
		return version;
	}

	public static void setVersion(char[] version) {
		Initiator.version = version;
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

	public static int getFilesNo() {
		return filesNo;
	}

	public static void setFilesNo(int filesNo) {
		Initiator.filesNo = filesNo;
	}

	public static String getFile_Real_Name() {
		return file_Real_Name;
	}

	public static void setFile_Real_Name(String file_Real_Name) {
		Initiator.file_Real_Name = file_Real_Name;
	}

	public static int getPORT_Peers_Channel() {
		return PORT_Peers_Channel;
	}

	public static void setPORT_Peers_Channel(int pORT_Peers_Channel) {
		PORT_Peers_Channel = pORT_Peers_Channel;
	}

	public static InetAddress getMcastAddr_Peers_Channel() {
		return mcastAddr_Peers_Channel;
	}

	public static void setMcastAddr_Peers_Channel(InetAddress mcastAdd_Peers_Channel) {
		Initiator.mcastAddr_Peers_Channel = mcastAdd_Peers_Channel;
	}

	public static int getMcastPORT_Peers_Channel() {
		return mcastPORT_Peers_Channel;
	}

	public static void setMcastPORT_Peers_Channel(int mcastPORT_Peers_Channel) {
		Initiator.mcastPORT_Peers_Channel = mcastPORT_Peers_Channel;
	}

	public static int getNUMBER_OF_PEERS() {
		return NUMBER_OF_PEERS;
	}

	public static void setNUMBER_OF_PEERS(int nUMBER_OF_PEERS) {
		NUMBER_OF_PEERS = nUMBER_OF_PEERS;
	}

	public static InetAddress getMcastAddr_Peers_Channel_Receive() {
		return mcastAddr_Peers_Channel_Receive;
	}

	public static void setMcastAddr_Peers_Channel_Receive(InetAddress mcastAddr_Peers_Channel_Receive) {
		Initiator.mcastAddr_Peers_Channel_Receive = mcastAddr_Peers_Channel_Receive;
	}

	public static int getMcastPORT_Peers_Channel_receive() {
		return mcastPORT_Peers_Channel_receive;
	}

	public static void setMcastPORT_Peers_Channel_receive(int mcastPORT_Peers_Channel_receive) {
		Initiator.mcastPORT_Peers_Channel_receive = mcastPORT_Peers_Channel_receive;
	}

	public static int getActivePeers() {
		return activePeers;
	}

	public static void setActivePeers(int activePeers) {
		Initiator.activePeers = activePeers;
	}
}
