package de.ricobrase.drivecheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

public class StatusFile{

	private String content;
	private File file;
	
	public StatusFile(String file){
		this.file = new File(file);
	}
	
	public String getContent(){
		return this.content;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public void readContent(){
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(this.getFile()));
			String line;
			while((line = br.readLine()) != null){
				this.content += line + System.getProperty("line.separator");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StatusFile[] listStatusFiles(){
		File[] files = this.getFile().listFiles();
		StatusFile[] sfiles = new StatusFile[files.length];
		int i = 0;
		for(File f : files){
			sfiles[i] = new StatusFile(f.getAbsolutePath());
			i++;
		}
		return sfiles;
	}
	
	public StatusFile[] listStatusFiles(FileFilter filter){
		File[] files = this.getFile().listFiles(filter);
		StatusFile[] sfiles = new StatusFile[files.length];
		int i = 0;
		for(File f : files){
			sfiles[i] = new StatusFile(f.getPath());
			i++;
		}
		return sfiles;
	}
	
}
