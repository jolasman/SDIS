package Interface;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import FileManagement.FileToCkunk;
import FileManagement.MergeChunks;;

public class Interface {

	public static void main(String[] args) throws IOException {
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
				File file = new File("./Files/imagem.jpg");
				File f1 = new File("./Files/imagem.jpg.001");
				File f2 = new File("./Files/imagem.jpg.002");
				File f3 = new File("./Files/imagem.jpg.003");
				File f4 = new File("./Files/imagem.jpg.004");
				File f5 = new File("./Files/imagem.jpg.005");
				File f6 = new File("./Files/imagem.jpg.006");
				File f7 = new File("./Files/imagem.jpg.007");
				File f8 = new File("./Files/imagem.jpg.008");
				
				File f9 = new File("./Files/imagem.jpg.009");
				File f10 = new File("./Files/imagem.jpg.010");
				File f11 = new File("./Files/imagem.jpg.011");
				File f12 = new File("./Files/imagem.jpg.012");
				File f13 = new File("./Files/imagem.jpg.013");
				File f14 = new File("./Files/imagem.jpg.014");
				File f15 = new File("./Files/imagem.jpg.015");
				File f16 = new File("./Files/imagem.jpg.016");
				File f17 = new File("./Files/imagem.jpg.017");
				File f18 = new File("./Files/imagem.jpg.018");
				File f19 = new File("./Files/imagem.jpg.019");
				File f20 = new File("./Files/imagem.jpg.020");
				File f21 = new File("./Files/imagem.jpg.021");
				File f22 = new File("./Files/imagem.jpg.022");
				File f23 = new File("./Files/imagem.jpg.023");
				File f24 = new File("./Files/imagem.jpg.024");
				File f25 = new File("./Files/imagem.jpg.025");
				File f26 = new File("./Files/imagem.jpg.026");
				
				File exper = new File("./Files/exp.jpg");
				FileToCkunk exp = new FileToCkunk(file);
				ArrayList<File> myList = new ArrayList<File>();
				myList.add(f1);
				myList.add(f2);
				myList.add(f3);
				myList.add(f4);
				myList.add(f5);
				myList.add(f6);
				myList.add(f7);
				myList.add(f8);
				myList.add(f9);
				myList.add(f10);
				myList.add(f11);
				myList.add(f12);
				myList.add(f13);
				myList.add(f14);
				myList.add(f15);
				myList.add(f16);
				myList.add(f17);
				myList.add(f18);
				myList.add(f19);
				myList.add(f20);
				myList.add(f21);
				myList.add(f22);
				myList.add(f23);
				myList.add(f24);
				myList.add(f25);
				myList.add(f26);
				MergeChunks merge = new MergeChunks(myList,exper);
			
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
