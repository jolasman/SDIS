package message;

import java.util.Arrays;

public class CreateMessage {
	/**
	 * constroi a mensagem a enviar com os parametros passados no construtor createMessage (putchunk)
	 * @return String message a enviar
	 */
	public static  synchronized byte[] MessageToSendPut( char[] version, int senderID, String fileID, int chunkNo, int replication_degree,byte[] body){
		String message = "PUTCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + chunkNo + " " + replication_degree +" " + "\r\n\r\n"; 
		byte[] send = new byte[message.getBytes().length];
		byte[] send_final = new byte[body.length + send.length];
		send = message.getBytes();		
		System.arraycopy(send,0,send_final,0,send.length);
		System.arraycopy(body,0,send_final,send.length,body.length);
		return send_final;
	}
	/**
	 * constroi msg a enviar com os parametros passados no construtor Create Message (Stored)
	 * @return String menssagem a enviar
	 */
	public static synchronized byte[] MessageToSendStore(char[] version, int senderID, String fileID, int chunkNo){
		String message = "STORED" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n";
		byte[] send = new byte[message.getBytes().length];
		send = message.getBytes();
		return send;
	}

	//GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	public static synchronized byte[] MessageToSendGetChunk(char[] version, int senderID, String fileID, int chunkNo){
		String message = "GETCHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n";
		byte[] send = new byte[message.getBytes().length];
		send = message.getBytes();
		return send;
	}


	//CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
	public static synchronized byte[] MessageToSendChunk(char[] version, int senderID, String fileID, int chunkNo, byte[] body){
		String message = "CHUNK" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
				chunkNo + " " + "\r\n\r\n";
		byte[] send = new byte[message.getBytes().length];
		byte[] send_final = new byte[body.length + send.length];
		send = message.getBytes();		
		System.arraycopy(send,0,send_final,0,send.length);
		System.arraycopy(body,0,send_final,send.length,body.length);
		return send_final;
	}

	//DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
	public static synchronized byte[] MessageToSendDelete(char[] version, int senderID, String fileID){
		String message = "DELETE" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + "\r\n\r\n"; 
		byte[] send = new byte[message.getBytes().length];
		send = message.getBytes();
		return send;
	}
	
	//REMOVED <Version> <SenderId> <FileId> <chunkNo> <CRLF><CRLF>
		public static synchronized byte[] MessageToSendRemoved(char[] version, int senderID, String fileID, int chunkNo){
			String message = "REMOVED" + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + chunkNo + " " + "\r\n\r\n"; 
			byte[] send = new byte[message.getBytes().length];
			send = message.getBytes();
			return send;
		}
}
