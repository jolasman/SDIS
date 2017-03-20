package peer;
/**
 * esta class vai iniciar to o processo 
 * @author Joel Carneiro
 *
 */
public class Peer {
	
	private int peerID;
	//como dar um peerID unico a cada um do sistema?
	
	public Peer(int peerID){
		this.peerID = peerID;
	}


	public int getPeerID() {
		return peerID;
	}
	
	
	
}
