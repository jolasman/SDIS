package Interface;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

//import FileManagement.FileToCkunk;
//import FileManagement.MergeChunks;
import FileManagement.FileManager;

public class Interface {

	
	
	@SuppressWarnings("resource")
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
		do {
			System.out.print("\nChoose menu item: ");
			menuItem = in.nextInt();
			switch (menuItem) {
			case 1:
				
				FileManager files = new FileManager();
				
				/*File file_initiator = new File("./Files/comprovativo.pdf");
				FileToCkunk initial_file = new FileToCkunk(file_initiator,"pdf");
				MergeChunks merged_file = new MergeChunks(initial_file.getChunks(),new File("./Files/file_merged.pdf"));
				*/
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
		} while (!quit);
		System.out.println("Bye-bye!");



	}

	private static void MergeChunks(ArrayList<File> myList, File exper) {
		// TODO Auto-generated method stub
		
	}



}
