package message;

public class CreateMessage {
	/**
	 * constroi a mensagem a enviar com os parametros passados no construtor createMessage (putchunk)
	 * @return String message a enviar
	 */
	public static  synchronized String MessageToSendPut( char[] version, int senderID, String fileID, int chunkNo, int replication_degree,byte[] body){
		String message = "PUTCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + replication_degree +" " + "\r\n\r\n" + body; 
		return message;
	}
	/**
	 * constroi msg a enviar com os parametros passados no construtor Create Message (Stored)
	 * @return String menssagem a enviar
	 */
	public static synchronized String MessageToSendStore(char[] version, int senderID, String fileID, int chunkNo){
		String message = "STORED" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n"; 
		return message;
	}

	//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	public static synchronized String MessageToSendGetChunk(char[] version, int senderID, String fileID, int chunkNo){
		String message = "GETCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n"; 
		return message;
	}


	//CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
	public static synchronized String MessageToSendChunk(char[] version, int senderID, String fileID, int chunkNo, byte[] body){
		String message = "CHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n" + body; 
		return message;
	}

}
