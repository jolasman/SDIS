package FileManagement;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import FileManagement.FileToCkunk;
import FileManagement.MergeChunks;

public class FileManager {
	public String path = "./Files";

	public FileManager() throws NoSuchAlgorithmException, IOException {
		File file = new File(path);
		File afile[] = file.listFiles();

		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			String extensao = arquivos.getName().substring(arquivos.getName().lastIndexOf("."), arquivos.getName().length());
			
			FileToCkunk initial_file = new FileToCkunk(arquivos,extensao);
			MergeChunks merged_file = new MergeChunks(initial_file.getChunks(), new File(path + "/" + "_file_merged__" + arquivos.getName() ));

		}
	}

}
