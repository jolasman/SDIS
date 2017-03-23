package initiator;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import chunks.Chunk;
import database.DatabaseChunksReceived;
import fileManagement.*;
import message.CreateMessage;
import peer.Peer;

public class Initiator {

	private static int PORT_MC_Channel = 5000;
	private static int PORT_MD_Channel = 5001;
	private static InetAddress mcastAddr_Channel_MC;
	private static InetAddress mcastAddr_Channel_MD;
	private static int mcastPORT_MC_Channel;
	private static int mcastPORT_MD_Channel;
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

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
			Scanner resp = new Scanner(System.in);
			System.out.print("\nChoose <protocol_version> <peerID> \n<MC mcasIP> <MC mcastPORT> <MD mcasIP> <MD mcastPORT>: ");// verificar se e' unico em todos os peers
			String response = resp.nextLine();
			String response_trimmed = response.trim();
			String[] final_response = response_trimmed.split(" ");
			
			File file = new File("./ChunksReceived");

			if(file.listFiles() == null){ System.out.println("nenhum ficheiro na pasta");
			}else{
				File afile[] = file.listFiles();
				int i = 0;
				for (int j = afile.length; i < j; i++) {
					File arquivos = afile[i];
					System.out.println("Load chunks received: " + arquivos.getName());
					DatabaseChunksReceived.setReceivedChunksID(arquivos.getName());
				}
			}
			
			if(final_response.length != 6){
				System.out.println("\nError : usage <protocol_version> <peerID> <MC mcasIP> <MC mcastPORT> <MD mcasIP> <MD mcastPORT>");
			}
			char[] version = new char[final_response.length];
			version[0] = final_response[0].charAt(0);
			version[1] = final_response[0].charAt(1);
			version[2] = final_response[0].charAt(2);
			int peerID = Integer.parseInt(final_response[1]);
			mcastAddr_Channel_MC = InetAddress.getByName(final_response[2]);
			mcastPORT_MC_Channel = Integer.parseInt(final_response[3]);
			mcastAddr_Channel_MD = InetAddress.getByName(final_response[4]);
			mcastPORT_MD_Channel = Integer.parseInt(final_response[5]);

			database.DatabasePeerID.StorePeerID(peerID);
			Peer newPeer = new Peer(peerID);

			FileManager files = new FileManager();
			if(files.isHaveFiles()){
				InetAddress mcastAddr = mcastAddr_Channel_MD;
				MulticastSocket socket = new MulticastSocket(mcastPORT_MD_Channel);
				socket.joinGroup(mcastAddr);
				socket.setTimeToLive(1);

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
								socket.send(msgDatagram_to_send);
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
}
