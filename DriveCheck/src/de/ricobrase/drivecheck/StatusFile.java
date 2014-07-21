package de.ricobrase.drivecheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StatusFile{

	private String content;
	private File file;
	
	public StatusFile(String file){
		this.file = new File(file);
	}
	
	public boolean isOK(){
		return getContent(false).replaceAll("\\s+", "").endsWith("StatusOK");
	}
	
	public String getContent(boolean clist){
		
		if(this.getFile().isDirectory()){
			return "";
		}
		
		this.content = "";
		
		readContent(clist);
		
		return this.content;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public void readContent(boolean clist){
		
		String encoding = "UTF-16LE";
		if(clist){
			encoding = "UTF-8";
		}
		
		 BufferedReader br;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(this.getFile()), encoding));
			String line;
			int x = 0;
			while((line = br.readLine()) != null){
				if(x == 0){
					this.content += line.trim().replaceAll("\\s+", "") + System.getProperty("line.separator");
					x++;
				}else{
					this.content += line.trim() + System.getProperty("line.separator");
				}
			}
//			System.out.println(this.content.replaceAll("\\s+", ""));
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
		StatusFile[]  sfiles = new StatusFile[0];
		if(files != null){
			sfiles = new StatusFile[files.length];
			int i = 0;
			for(File f : files){
				sfiles[i] = new StatusFile(f.getPath());
				i++;
			}
		}
		return sfiles;
	}
	
	public void writeContent(String content, boolean clist){
		String encoding = "UTF-16LE";
		if(clist){
			encoding = "UTF-8";
		}
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.getFile()), encoding);
			if(this.getFile().exists()){
				this.getFile().delete();
			}
			this.getFile().createNewFile();
			writer.write(content);
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
