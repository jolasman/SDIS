package message;

public class CreateMessage {
	/**
	 * constroi a mensagem a enviar com os parametros passados no construtor createMessage (putchunk)
	 * @return String message a enviar
	 */
	public static  synchronized String MessageToSendPut(String typeMessage, char[] version, int senderID, String fileID, int chunkNo, int replication_degree){
		String message = typeMessage + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
	chunkNo + " " + replication_degree +" " + "\r\n\r\n"; 
		return message;
	}
	/**
	 * constroi msg a enviar com os parametros passados no construtor Create Message (Stored)
	 * @return String menssagem a enviar
	 */
	public static synchronized String MessageToSendStore(String typeMessage, char[] version, int senderID, String fileID, int chunkNo){
		String message = typeMessage + " " + version[0]+version[1]+version[2] + " " + senderID + " "+ fileID + " " + 
	chunkNo + " " + "\r\n\r\n"; 
		return message;
	}
}
