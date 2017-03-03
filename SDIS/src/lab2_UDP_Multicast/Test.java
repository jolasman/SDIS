package lab2_UDP_Multicast;


public class Test {
	public static void main(String[] args) {
		String s = "ola <tudo> bem ?";
		String r = s.trim(); // retira espacos antes e depois da String
		String[] t = s.split(" "); // divide a string sempre que tem um carater de espaco (pode ser usado outro)
		System.out.println(r);
		System.out.println(t[0]);
		System.out.println(t[1]);
		System.out.println(t[2]);
		System.out.println(t[3]);
		
	}
}

