package message;

import message.*;

public class CreateMessage {
	private String typeMessage;
	private char[] version;
	private String fileID;
	private int chunkNo;
	private int replication_degree;

	public CreateMessage(String typeMessage, char[] version, String fileID, int chunkNo, int replication_degree){

		this.typeMessage = typeMessage;
		this.version = version;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replication_degree = replication_degree;

		MessageToSend();
		System.out.println("Message to send : " + MessageToSend());
	}
	
	public String MessageToSend(){
		String message = typeMessage + " " + version[0]+version[1]+version[2] + " " + "192.3.2.3" + " "+ fileID + " " + 
	chunkNo + " " + replication_degree +" " + "\r\n\r\n"; 
	
		
		return message;

	}
}
