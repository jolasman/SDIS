package message;

public class SeparatedMessage {

	private String type;
	private String header;
	private byte[] body;
	private char[] version = new char[3];
	private String senderID;
	private String fileID;
	private int chunkNo;
	private int replication_degree;

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
		senderID = final_Header[2];
		fileID = final_Header[3];
		chunkNo = Integer.parseInt(final_Header[4]);
		
		if(final_Header.length == 6){
			replication_degree = Integer.parseInt(final_Header[5]);
		}



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
	public String getSenderID() {
		return senderID;
	}
	public void setSenderID(String senderID) {
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
