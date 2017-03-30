package message;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

/**
 * esta class trata as mensagens recebidas no socket
 * recebe um array de bytes (byte[]) com todos os dados da mesnagem e separa o header do body
 * @author Joel Carneiro
 *
 */
public class MessageManager {
	private static int MAX_BYTES = 64000;
	/**
	 * 
	 * @param msg byte[] com a mesnagem recebida no socket
	 * @return objecto SeparatedMessage(com o header e o body ja separados)
	 */
	public static synchronized SeparatedMessage SeparateMsgContent(byte[] msg){

		byte[] original = msg;
		byte[] header_byte = new byte[800];
		byte[] body = new byte[MAX_BYTES];
		int index_body_begin = 0;
		
		if(original.length > 64000){
			for (int i = 0; i < original.length; i++){
				if (original[i] == (byte) '\r'){
					if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
							i + 2 < original.length && original[i+2] == (byte) '\r' &&
							i + 3 < original.length && original[i+3] == (byte) '\n'){

						index_body_begin = i+4;				
						System.arraycopy(original, 0, header_byte,0, i-1);
						System.arraycopy(original, index_body_begin, body, 0, MAX_BYTES);
						break;
					}
				}
				else{}
			}
		}

		String header = new String (header_byte);		
		SeparatedMessage msgSeparated = new SeparatedMessage(header, body);	
		return msgSeparated;
	}

	public static synchronized SeparatedMessage SeparateMsgContentStored(byte[] msg){

		byte[] original = msg;
		byte[] header_byte = new byte[800];

		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					System.arraycopy(original, 0, header_byte,0, i-1);					
					break;
				}
			}
			else{}
		}
		String stored = new String (header_byte);
		SeparatedMessage msgSeparated = new SeparatedMessage(stored);	
		return msgSeparated;
	}
	
	/*public static synchronized SeparatedMessage SeparateMsgContentGETCHUNK(byte[] msg){
		byte[] original = msg;
		byte[] header_byte = new byte[800];

		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					System.arraycopy(original, 0, header_byte,0, i-1);					
					break;
				}
			}
			else{}
		}
		String get = new String (header_byte);
		SeparatedMessage msgSeparated = new SeparatedMessage(get, "GETCHUNK");	
		return msgSeparated;
	}*/


	public static synchronized SeparatedMessage SeparateMsgContentCHUNK(byte[] msg){

		byte[] original = msg;
		byte[] header_byte = new byte[800];
		byte[] body = new byte[MAX_BYTES];
		int index_body_begin = 0;

		if(original.length > 64000){
			for (int i = 0; i < original.length; i++){
				if (original[i] == (byte) '\r'){
					if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
							i + 2 < original.length && original[i+2] == (byte) '\r' &&
							i + 3 < original.length && original[i+3] == (byte) '\n'){

						index_body_begin = i+4;				
						System.arraycopy(original, 0, header_byte,0, i-1);
						System.arraycopy(original, index_body_begin, body, 0, MAX_BYTES);
						break;
					}
				}
				else{}
			}
		}
		String header = new String (header_byte);
		SeparatedMessage msgSeparated = new SeparatedMessage(header, body, "CHUNK");	
		return msgSeparated;
	}
	
	/*public static synchronized SeparatedMessage SeparateMsgContentDelete(byte[] msg){

		byte[] original = msg;
		byte[] header_byte = new byte[800];

		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					System.arraycopy(original, 0, header_byte,0, i-1);					
					break;
				}
			}
			else{}
		}
		String delete = new String (header_byte);
		SeparatedMessage msgSeparated = new SeparatedMessage(delete, "DELETE");	
		return msgSeparated;
	}*/

}
