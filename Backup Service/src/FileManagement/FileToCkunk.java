package FileManagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class FileToCkunk {

	public ArrayList<File> chunks = new ArrayList<File>();
	public String fileID;

	public FileToCkunk(File file) throws IOException, NoSuchAlgorithmException {
		
		int partCounter = 1;	
		int sizeOfFiles = 64000;
		byte[] buffer = new byte[sizeOfFiles];
		
		//sha256 algorithm ****************************************
		MessageDigest md = MessageDigest.getInstance( "SHA-512" );
		md.update( file.getName().getBytes("UTF-8"));
		byte[] aMessageDigest = md.digest();

		String sha256 = Base64.getEncoder().encodeToString( aMessageDigest );
		fileID = sha256.substring(0,10);
		//end *************************************************************
		

		try (BufferedInputStream file_data = new BufferedInputStream(new FileInputStream(file))) {

			String name = file.getName();
			int tmp = 0;

			while ((tmp = file_data.read(buffer)) > 0) {
				//write each chunk of data into separate file with different number in name

				File newChunk = new File("./Files", fileID + String.format("%03d", partCounter++));
				
				try (FileOutputStream out = new FileOutputStream(newChunk)) {
					out.write(buffer, 0, tmp); //tmp is chunk size
					System.out.println("Chunk " + newChunk + " created");
					chunks.add(newChunk);
				}
				catch (IOException e) {
					System.out.println("Error when we try to write into a new Chunk");
					e.printStackTrace();
				}
			}
		} 
		catch (FileNotFoundException e) {
			System.out.println("Error when we try to get file data");
			e.printStackTrace();

		}
	}

	public ArrayList<File> getChunks(){
		return chunks;
	}

	public String getFileID(){
		return fileID;
	}
}


