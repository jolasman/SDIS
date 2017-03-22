package initiator;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import chunks.Chunk;
import fileManagement.*;
import message.CreateMessage;
import peer.Peer;

public class Iniciator {
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		/*	if (args.length != 3) {
		System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
		return;
	}
		 */
		Scanner in = new Scanner(System.in);
		// print menu

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
			System.out.print("\nChoose peerID(int): ");// verificar se e' unico em todos os peers
			int peerID = in.nextInt();
			Peer newPeer = new Peer(peerID);
			database.DatabasePeerID.StorePeerID(peerID);
			FileManager files = new FileManager();
			if(files.isHaveFiles()){
				int PORT = 5000;			
				InetAddress mcastAddr = InetAddress.getByName("225.4.5.6");
				DatagramSocket socket = new DatagramSocket(4000);
				
				for(int i = 0; i< Chunk.getChunksCreated().size(); i++){
					char[] version = {'1','.','1'};
					String fileID = Chunk.getChunksCreated().get(i).getFileID();
					int chunkNo = Chunk.getChunksCreated().get(i).getChunkNo();
					int replication_degree = Chunk.getChunksCreated().get(i).getReplication_degree();
					byte[] body = Chunk.getChunksCreated().get(i).getChunkData();

					Thread initiator = new Thread(){
						public void run(){
							try{
								String message_to_Send = CreateMessage.MessageToSendPut("PUTCHUNK", version, peerID, fileID, chunkNo, replication_degree, body);
								DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , mcastAddr, PORT);

								socket.send(msgDatagram_to_send);
								System.out.println("mensagem enviada para: " + mcastAddr + "----" + PORT);
							}catch (Exception e){e.printStackTrace();}
						}
					};
					initiator.start();
				}
			}
			else{


			}


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
}
