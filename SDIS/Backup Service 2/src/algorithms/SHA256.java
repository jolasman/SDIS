package algorithms;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	
	public static String ToSha256(File file){
		//sha256 algorithm ****************************************
				String stringToHash = file.getName();
				MessageDigest digest = null;
				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				byte[] hashedBytes = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
				String fileID = String.format("%064x", new java.math.BigInteger(1,hashedBytes));

				//end *************************************************************
		return fileID;
		
	}
	
	public static String ToSha256(String file_name){
		//sha256 algorithm ****************************************
				String stringToHash = file_name;
				MessageDigest digest = null;
				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				byte[] hashedBytes = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
				String fileID = String.format("%064x", new java.math.BigInteger(1,hashedBytes));

				//end *************************************************************
		return fileID;
		
	}
	
}
