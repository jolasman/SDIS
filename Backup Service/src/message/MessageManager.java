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
		int index_body_begin = 0;
		SeparatedMessage msgSeparated = null;
		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					index_body_begin = i+4;	

					byte[] header_byte = new byte[i-1];
					byte[] cenas = new byte[i+4];
					System.arraycopy(original, 0, header_byte,0, i-1);
					String header = new String (header_byte);
					String coisas = new String (cenas);
					byte[] body = new byte[(original.length - (i+4))];
					System.arraycopy(original, coisas.length(), body, 0, body.length );

					msgSeparated = new SeparatedMessage(header, body);
					break;
				}
			}
			else{}
		}
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


	public static synchronized SeparatedMessage SeparateMsgContentCHUNK(byte[] msg){

		byte[] original = msg;
		int index_body_begin = 0;
		SeparatedMessage msgSeparated = null;
		for (int i = 0; i < original.length; i++){
			if (original[i] == (byte) '\r'){
				if (i + 1 < original.length && original[i+1] == (byte) '\n' &&
						i + 2 < original.length && original[i+2] == (byte) '\r' &&
						i + 3 < original.length && original[i+3] == (byte) '\n'){

					index_body_begin = i+4;	

					byte[] header_byte = new byte[i-1];
					byte[] cenas = new byte[i+4];
					System.arraycopy(original, 0, header_byte,0, i-1);
					String header = new String (header_byte);
					String coisas = new String (cenas);
					byte[] body = new byte[(original.length - (i+4))];
					System.arraycopy(original, coisas.length(), body, 0, body.length );

					msgSeparated = new SeparatedMessage(header, body, "CHUNK");	
					break;
				}
			}
			else{}
		}
		return msgSeparated;

	}

	public static synchronized SeparatedMessage SeparateMsgContentDelete(byte[] msg){

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
		SeparatedMessage msgSeparated = new SeparatedMessage(delete, true);	
		return msgSeparated;
	}

}
