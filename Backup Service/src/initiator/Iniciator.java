package initiator;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import fileManagement.FileManager;
import peer.Peer;

public class Iniciator {
	@SuppressWarnings({ "unused" })
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
				
				//FileManager files = new FileManager();
				Peer newPeer = new Peer(peerID);

				
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
