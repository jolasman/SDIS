package message;
/**
 * recebe o header e o body da menssage e subdivide o header em todos os campos que ele tem
 * @author Joel Carneiro
 *
 */
public class SeparatedMessage {
	private String type;
	private String header;
	private byte[] body;
	private char[] version = new char[3];
	private int senderID;
	private String fileID;
	private int chunkNo;
	private int replication_degree;
	/**
	 * 
	 * @param header byte[] com o header data
	 * @param body byte[] com o body data
	 */
	public SeparatedMessage(String header, byte[] body){
		String header_trimed = header.trim();
		String[] final_Header = header_trimed.split(" ");

		String msgType = final_Header[0].toUpperCase();
		this.header = header;
		this.body = body;
		type = msgType;
		version[0] = final_Header[1].charAt(0);
		version[1] = final_Header[1].charAt(1);
		version[2] = final_Header[1].charAt(2);
		senderID = Integer.parseInt(final_Header[2]);
		fileID = final_Header[3];
		chunkNo = Integer.parseInt(final_Header[4]);
		replication_degree = Integer.parseInt(final_Header[5]);
	}

	public SeparatedMessage(String header){
		String header_trimed = header.trim();
		String[] final_Header = header_trimed.split(" ");

		String msgType = final_Header[0].toUpperCase();
		this.header = header;
		type = msgType;
		version[0] = final_Header[1].charAt(0);
		version[1] = final_Header[1].charAt(1);
		version[2] = final_Header[1].charAt(2);
		senderID = Integer.parseInt(final_Header[2]);
		fileID = final_Header[3];
		chunkNo = Integer.parseInt(final_Header[4]);
	}



	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSenderID() {
		return senderID;
	}
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	public String getFileID() {
		return fileID;
	}
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}
	public int getReplication_degree() {
		return replication_degree;
	}
	public void setReplication_degree(int replication_degree) {
		this.replication_degree = replication_degree;
	}
	public char[] getVersion() {
		return version;
	}
	public void setVersion(char[] version) {
		this.version = version;
	}
}
