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
public class FileManager{
	private String path = "./Files";
	private boolean haveFiles;

	public FileManager() throws NoSuchAlgorithmException, IOException {
		try{
			File file = new File(path);

			File afile[] = file.listFiles();
			//TODO se nao houver ficheiros dar erro
			int i = 0;
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];
				String extensao = arquivos.getName().substring(arquivos.getName().lastIndexOf("."), arquivos.getName().length());
				FileToCkunk initial_file = new FileToCkunk(arquivos,extensao);
				haveFiles = true;
				//MergeChunks merged_file = new MergeChunks(initial_file.getChunks(), new File(path + "/" + "_file_merged__" + arquivos.getName() ));
			}
		}catch (IOException e){
			haveFiles = false;
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public FileManager(String nameFile) throws NoSuchAlgorithmException, IOException {
		try{
			File file = new File(path);

			File afile[] = file.listFiles();
			//TODO se nao houver ficheiros dar erro
			int i = 0;
			for (int j = afile.length; i < j; i++) {
				File arquivos = afile[i];

				if(arquivos.getName().equals(nameFile)){
					String extensao = arquivos.getName().substring(arquivos.getName().lastIndexOf("."), arquivos.getName().length());
					FileToCkunk initial_file = new FileToCkunk(arquivos,extensao);
					haveFiles = true;
				}
				//MergeChunks merged_file = new MergeChunks(initial_file.getChunks(), new File(path + "/" + "_file_merged__" + arquivos.getName() ));
			}
		}catch (IOException e){
			haveFiles = false;
			e.printStackTrace();
		}
	}

	public boolean isHaveFiles() {
		return haveFiles;
	}

	public void setHaveFiles(boolean haveFiles) {
		this.haveFiles = haveFiles;
	}
}
