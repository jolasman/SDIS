package message;

public class MessageManager {

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
		System.arraycopy(original, index_body_begin, body, 0, original.length - index_body_begin);
		String header = new String (header_byte);

		SeparatedMessage msgSeparated = new SeparatedMessage(header, body);		
		return msgSeparated;
	}




}
