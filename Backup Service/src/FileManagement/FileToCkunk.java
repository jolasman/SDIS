package FileManagement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileToCkunk {

	public FileToCkunk(File file) throws IOException {
		// TODO Auto-generated constructor stub
		int partCounter = 1;	
		int sizeOfFiles = 64000;
		byte[] buffer = new byte[sizeOfFiles];
		

		try (BufferedInputStream file_data = new BufferedInputStream(new FileInputStream(file))) {
			System.out.println("aceder a dados do file");
			String name = file.getName();
			int tmp = 0;

			while ((tmp = file_data.read(buffer)) > 0) {
				//write each chunk of data into separate file with different number in name
				
				File newChunk = new File("./Files", name + "." + String.format("%03d", partCounter++));

				try (FileOutputStream out = new FileOutputStream(newChunk)) {
					out.write(buffer, 0, tmp); //tmp is chunk size
					
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
	
	
}


