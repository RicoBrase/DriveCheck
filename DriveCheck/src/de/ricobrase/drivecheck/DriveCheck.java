package de.ricobrase.drivecheck;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class DriveCheck extends JFrame{

	private static final long serialVersionUID = 1620251908917275817L;
	
	JComboBox<String> logdate;
	JComboBox<String> computers;

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
		
		init();
	}
	
	public void init(){
		logdate = new JComboBox<String>(filterFiles(getFiles("")).toArray(new String[0]));
		logdate.setBounds(5, 5, 335, 25);
		
		computers = new JComboBox<String>(filterFiles(getFiles(logdate.getSelectedItem().toString())).toArray(new String[0]));
		if(computers.getSelectedIndex() == -1){
			computers.addItem("Keine Verfügbaren Berichte!");
		}
		computers.setBounds(5, 35, 335, 25);
		
		logdate.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent ev) {
				computers.setModel(new DefaultComboBoxModel<>(filterFiles(getFiles(ev.getItem().toString())).toArray(new String[0])));
				if(computers.getSelectedIndex() == -1){
					computers.addItem("Keine Verfügbaren Berichte!");
				}
			}
		});
		
		this.add(logdate);
		this.add(computers);
		
		this.setVisible(true);
	}
	
	public ArrayList<File> getFiles(String subDir){
		try{
			File path = new File(new File(subDir).getAbsolutePath());
			if(subDir.isEmpty()){
				return new ArrayList<File>(Arrays.asList(path.listFiles()));
			}else{
				return new ArrayList<File>(Arrays.asList(path.listFiles(new TXTFileFilter("txt"))));
			}
			
		}catch(Exception e){
			return new ArrayList<File>();
		}
	}
	
	public ArrayList<String> filterFiles(ArrayList<File> list){
		ArrayList<String> flist = new ArrayList<String>();
		for(File f : list){
			flist.add(f.getName());
			if(f.getName().equalsIgnoreCase("SCRIPT") || f.getPath().endsWith(".jar")){
				flist.remove(f.getName());
			}
		}
		return flist;
		
	}
	
	
	
}
