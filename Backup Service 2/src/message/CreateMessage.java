package message;

import java.util.Arrays;

public class CreateMessage {
	/**
	 * constroi a mensagem a enviar com os parametros passados no construtor createMessage (putchunk)
	 * @return String message a enviar
	 */
	public static  synchronized byte[] MessageToSendPut( char[] version, int senderID, String fileID, int chunkNo, int replication_degree,byte[] body){
		byte[] send = new byte[64800];
		String message = "PUTCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + chunkNo + " " + replication_degree +" " + "\r\n\r\n"; 
		send = Arrays.copyOfRange(message.getBytes(), 0, message.getBytes().length);
		send= Arrays.copyOfRange(body, message.getBytes().length,body.length);		
		return send;
	}
	/**
	 * constroi msg a enviar com os parametros passados no construtor Create Message (Stored)
	 * @return String menssagem a enviar
	 */
	public static synchronized byte[] MessageToSendStore(char[] version, int senderID, String fileID, int chunkNo){
		byte[] send = new byte[64800];
		String message = "STORED" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n";
		send = Arrays.copyOfRange(message.getBytes(), 0, message.getBytes().length);
		return send;
	}

	//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	public static synchronized byte[] MessageToSendGetChunk(char[] version, int senderID, String fileID, int chunkNo, String extensao){
		byte[] send = new byte[64800];
		String message = "GETCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + extensao + " " + "\r\n\r\n"; 
		send = Arrays.copyOfRange(message.getBytes(), 0, message.getBytes().length);
		return send;
	}


	//CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
	public static synchronized byte[] MessageToSendChunk(char[] version, int senderID, String fileID, int chunkNo,String extensao, byte[] body){
		byte[] send = new byte[64800];
		String message = "CHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + extensao + " " + "\r\n\r\n";
		send = Arrays.copyOfRange(message.getBytes(), 0, message.getBytes().length);
		System.arraycopy(body, message.getBytes().length, send, 0, body.length);
		return send;
	}

}
