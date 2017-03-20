package fileManagement;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import fileManagement.FileToCkunk;
import fileManagement.MergeChunks;
/**
 * class que importa todos os ficheiros de uma class e os "manda" dividir em chunks
 * @author Joel Carneiro
 *
 */
public class FileManager {
	public String path = "./Files";

	@SuppressWarnings("unused")
	public FileManager() throws NoSuchAlgorithmException, IOException {
		File file = new File(path);
		File afile[] = file.listFiles();

		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			String extensao = arquivos.getName().substring(arquivos.getName().lastIndexOf("."), arquivos.getName().length());
			
			FileToCkunk initial_file = new FileToCkunk(arquivos,extensao);
			//MergeChunks merged_file = new MergeChunks(initial_file.getChunks(), new File(path + "/" + "_file_merged__" + arquivos.getName() ));

		}
	}

}
