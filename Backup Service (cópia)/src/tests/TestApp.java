package tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import message.CreateMessage;
import peer.Peer;

public class TestApp {

	
	public static void main(String[] args) throws IOException{
		if (args.length != 4) {
			System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
			return;
		}
		int peerID = Integer.parseInt(args[0]);
		String protocol = args[1];
		String oper_1 = args[2];
		int oper_2 = Integer.parseInt(args[3]);

		if(protocol.equals("BACKUP")){
			DatagramSocket udp_msg = new DatagramSocket();
			int PORT = 7777;			
			InetAddress hostAddr = InetAddress.getLocalHost();

			String message_to_Send = peerID + " " + "BACKUP" + " " + oper_1 + " " + oper_2;
			DatagramPacket msgDatagram_to_send = new DatagramPacket(message_to_Send.getBytes() , message_to_Send.getBytes().length , hostAddr, PORT);
			
			udp_msg.send(msgDatagram_to_send);
			System.out.println("mensagem enviada para: " + hostAddr + "----" + PORT);


		}

	}
}
