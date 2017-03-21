package message;
/**
 * esta class trata as mensagens recebidas no socket
 * recebe um array de bytes (byte[]) com todos os dados da mesnagem e separa o header do body
 * @author Joel Carneiro
 *
 */
public class MessageManager {
	/**
	 * 
	 * @param msg byte[] com a mesnagem recebida no socket
	 * @return objecto SeparatedMessage(com o header e o body ja separados)
	 */
	public static synchronized SeparatedMessage SeparateMsgContent(byte[] msg){

		byte[] original = msg;
		byte[] header_byte = new byte[original.length];
		byte[] body = new byte[original.length];
		int index_body_begin = 0;

		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					index_body_begin = i+4;				
					System.arraycopy(original, 0, header_byte,0, i-1);					
					break;
				}
			}
			else{}
		}
		String header = new String (header_byte);
		
		if(original.length == header_byte.length){ // sem body --> Stored
			SeparatedMessage msgSeparated = new SeparatedMessage(header);
			return msgSeparated;
		}else{
			System.arraycopy(original, index_body_begin, body, 0, original.length - index_body_begin);
			SeparatedMessage msgSeparated = new SeparatedMessage(header, body);	
			return msgSeparated;
		}
		
	}
}
