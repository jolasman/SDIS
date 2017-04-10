package tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;

import javax.naming.InsufficientResourcesException;

import initiator.Initiator;
import message.CreateMessage;
import peer.Peer;

public class TestApp {

	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
		/*if (args[1].equals("BACKUP") && args.length != 4) {
			System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return;
		}
		if (args[1].equals("RESTORE") && args.length != 3) {
			System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return;
		}
		int peerID = Integer.parseInt(args[0]);
		String protocol = args[1];
		String oper_1 = args[2];

		if(protocol.equals("BACKUP")){
			int oper_2 = Integer.parseInt(args[3]);
			Initiator.BackupFileInitiator(oper_1, oper_2);
		}
		
		if(protocol.equals("RESTORE")){
			Initiator.RestoreFiles(oper_1);

		}*/
	}
}
