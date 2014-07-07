package de.ricobrase.drivecheck;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class DriveCheck extends JFrame{

	private static final long serialVersionUID = 1620251908917275817L;
	
	private static String OK = "Status  \nOK      ";
	
	JComboBox<String> logdate;
	JComboBox<String> computers;
	
	public DriveCheck dc;

	public static void main(String[] args){
		new DriveCheck();
	}
	
	public DriveCheck(){
		super("DriveCheck - v0.1");
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("Error while loading System Look and Feel!");
		}
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, 350, 500);
		this.setLocationRelativeTo(null);
		
		dc = this;
		
		init();
	}
	
	public void init(){
		logdate = new JComboBox<String>(getFileNames(filterFiles(getFiles(""))).toArray(new String[0]));
		logdate.setBounds(5, 5, 335, 25);
		if(logdate.getSelectedIndex() == -1){
			logdate.addItem("Keine verf�gbaren Berichte!");
		}
		
		computers = new JComboBox<String>(getFileNames(filterFiles(getFiles(logdate.getSelectedItem().toString()))).toArray(new String[0]));
		if(computers.getSelectedIndex() == -1){
			computers.addItem("Keine verf�gbaren Computer f�r dieses Datum!");
		}
		computers.setBounds(5, 35, 335, 25);
		
		logdate.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent ev) {
				computers.setModel(new DefaultComboBoxModel<String>(filterFiles(getFiles(ev.getItem().toString())).toArray(new String[0])));
				if(computers.getSelectedIndex() == -1){
					computers.addItem("Keine verf�gbaren Computer f�r dieses Datum!");
				}
			}
		});
		
		this.add(logdate);
		this.add(computers);
		
		this.setVisible(true);
	}
	
	public ArrayList<String> getFileNames(ArrayList<StatusFile> list){
		ArrayList<String> filenames = new ArrayList<String>();
		
		for(StatusFile s : list){
			if(s.getFile().isDirectory()){
				filenames.add(s.getFile().getName());
			}else if(s.getContent().equalsIgnoreCase(DriveCheck.OK)){
				filenames.add("<html><span style=\"color:#00ff00;\">"+s.getFile().getName()+ "</span></html>");
			}else{
				filenames.add("<html><span style=\"color:#ff0000;\">"+s.getFile().getName()+ "</span></html>");
			}
		}
		
		return filenames;
	}
	
	public ArrayList<StatusFile> getFiles(String subDir){
		try{
			StatusFile path = new StatusFile(new File(subDir).getAbsolutePath());
			if(subDir.isEmpty()){
				return new ArrayList<StatusFile>(Arrays.asList(path.listStatusFiles()));
			}else{
				return new ArrayList<StatusFile>(Arrays.asList(path.listStatusFiles(new TXTFileFilter("txt"))));
			}
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<StatusFile>();
		}
	}
	
	public ArrayList<StatusFile> filterFiles(ArrayList<StatusFile> list){
		ArrayList<StatusFile> flist = new ArrayList<StatusFile>();
		for(StatusFile f : list){
			if(f.getFile().isDirectory()){
				flist.add(f);
			}
			if(f.getFile().getName().equalsIgnoreCase("SCRIPT")){
				flist.remove(f);
			}
		}
		return flist;
	}
	
	
	
}
