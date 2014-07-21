package de.ricobrase.drivecheck;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class DriveCheck extends JFrame{

	private static final long serialVersionUID = 1620251908917275817L;
	
	JComboBox<String> logdate;
	JComboBox<String> computers;
	
	JButton open;
	JButton recheck;
	JButton setComputer;
	
	TrayIcon trayicon;
	
	ImageIcon icon;
	
	ArrayList<String> wrongFiles;
	
	public static DriveCheck dc;

	public static void main(String[] args){
		new DriveCheck();
	}
	
	public DriveCheck(){
		super("DriveCheck - v1.0 - by Rico Brase");
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("Error while loading System Look and Feel!");
		}
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setBounds(0, 0, 350, 155);
		this.setLocationRelativeTo(null);
		
		URL iconURL = getClass().getResource("/hdd.png");
		icon = new ImageIcon(iconURL);
		this.setIconImage(icon.getImage());
		
		dc = this;
		
		init();
	}
	
	public void init(){
		
		wrongFiles = new ArrayList<String>();
		
		trayicon = new TrayIcon(icon.getImage());
		trayicon.setImageAutoSize(true);
		PopupMenu menu = new PopupMenu();
		
		MenuItem show = new MenuItem("Fenster zeigen");
		show.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DriveCheck.dc.setVisible(true);
			}
		});
		MenuItem exit = new MenuItem("Beenden");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SystemTray.getSystemTray().remove(DriveCheck.dc.trayicon);
				System.exit(0);
			}
		});
		menu.add(show);
		menu.addSeparator();
		menu.add(exit);
		
		trayicon.setPopupMenu(menu);
		
		if(SystemTray.isSupported()){
			try {
				SystemTray.getSystemTray().add(trayicon);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		logdate = new JComboBox<String>(getFileNames(filterFiles(getFiles("")),"").toArray(new String[0]));
		logdate.setBounds(5, 5, 335, 25);
		if(logdate.getSelectedIndex() == -1){
			logdate.addItem("Keine verfügbaren Berichte!");
		}
		
		computers = new JComboBox<String>(getFileNames(getFiles(logdate.getSelectedItem().toString()),logdate.getSelectedItem().toString() + "/").toArray(new String[0]));
		if(computers.getSelectedIndex() == -1){
			computers.addItem("Keine verfügbaren Computer für dieses Datum!");
		}
		computers.setBounds(5, 35, 335, 25);
		
		logdate.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent ev) {
				computers.setModel(new DefaultComboBoxModel<String>(getFileNames(getFiles(ev.getItem().toString()),ev.getItem().toString() + "/").toArray(new String[0])));
				if(computers.getSelectedIndex() == -1){
					computers.addItem("Keine verfügbaren Computer für dieses Datum!");
				}
			}
		});
		
		open = new JButton("Log zeigen");
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StatusFile sf = getStatusFile(logdate.getSelectedItem().toString(), computers.getSelectedItem().toString());
				JOptionPane.showMessageDialog(null, sf.getContent(false), "Log: " + logdate.getSelectedItem().toString() + "/" + computers.getSelectedItem().toString().substring(52, computers.getSelectedItem().toString().length()-14), JOptionPane.PLAIN_MESSAGE);
			}
		});
		open.setBounds(5, 65, 165, 25);
		
		recheck = new JButton("Neu laden");
		recheck.setBounds(175, 65, 165, 25);
		recheck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DriveCheck.recheckFiles();
			}
		});
		
		setComputer = new JButton("Öffne Computerliste");
		setComputer.setBounds(5, 95, 335, 25);
		setComputer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextArea pcs = new JTextArea(5, 20);
				JScrollPane jsc = new JScrollPane(pcs);
				StatusFile sf = new StatusFile("SCRIPT/checkdrives.txt");
				jsc.setWheelScrollingEnabled(true);
				pcs.setText(sf.getContent(true));
				int result = JOptionPane.showConfirmDialog(null, jsc, "Computer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(result == JOptionPane.OK_OPTION){
					sf.writeContent(pcs.getText(), true);
				}
			}
		});
		
		this.add(logdate);
		this.add(computers);
		this.add(open);
		this.add(recheck);
		this.add(setComputer);
		
		this.setVisible(true);
		
		DriveCheck.recheckFiles();
		
		repaint();
	}
	
	public StatusFile getStatusFile(String date, String computer){
		computer = computer.substring(52, computer.length()-14);
		//System.out.println(date + " | " + computer);
		File f = new File(date, computer);
		if(f.exists()){
			//System.out.println("Datei existiert");
		}
		for(StatusFile s : getFiles(date)){
			//System.out.println(s.getFile().getAbsolutePath() + " | " + f.getAbsolutePath());
			if(s.getFile().getAbsolutePath().equalsIgnoreCase(f.getAbsolutePath())){
				//System.out.println("Gefunden!");
				return s;
			}
		}
		return null;
	}
	
	public ArrayList<String> getFileNames(ArrayList<StatusFile> list, String parent){
		ArrayList<String> filenames = new ArrayList<String>();
		
		for(StatusFile s : list){
			if(s.getFile().isDirectory()){
				filenames.add(s.getFile().getName());
			}else if(s.isOK()){
				filenames.add("<html><span style=\"color:#00aa00;font-weight:bold;\">"+s.getFile().getName()+ "</span></html>");
			}else{
				filenames.add("<html><span style=\"color:#ff0000;font-weight:bold;\">"+s.getFile().getName()+ "</span></html>");
				//wrongFiles.add(parent + s.getFile().getName());
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
	
	public static void recheckFiles(){
		
		try{
			Process p = Runtime.getRuntime().exec("cmd /k " + new File("SCRIPT/checkdrives.bat").getAbsolutePath());
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		DriveCheck.dc.wrongFiles = new ArrayList<String>();
		DriveCheck.dc.logdate.setModel(new DefaultComboBoxModel<String>(DriveCheck.dc.getFileNames(DriveCheck.dc.filterFiles(DriveCheck.dc.getFiles("")), "").toArray(new String[0])));
		if(DriveCheck.dc.logdate.getSelectedIndex() == -1){
			DriveCheck.dc.logdate.addItem("Keine verfügbaren Berichte!");
		}
		DriveCheck.dc.computers.setModel(new DefaultComboBoxModel<String>(DriveCheck.dc.getFileNames(DriveCheck.dc.getFiles(DriveCheck.dc.logdate.getSelectedItem().toString()), DriveCheck.dc.logdate.getSelectedItem().toString() + "/").toArray(new String[0])));
		DriveCheck.dc.checkForError();
		if(!DriveCheck.dc.wrongFiles.isEmpty()){
			String msg = "";
			for(String s : DriveCheck.dc.wrongFiles){
				msg += s + "\n";
			}
			DriveCheck.dc.trayicon.displayMessage("Fehler gefunden", msg, MessageType.ERROR);
		}else{
			DriveCheck.dc.trayicon.displayMessage("Keine Fehler gefunden", "Es wurden keine Fehlerhaften Logs gefunden!", MessageType.INFO);
		}
		
		DriveCheck.dc.repaint();
	}
	
	public void checkForError(){
		for(StatusFile sf : filterFiles(getFiles(""))){
			for(StatusFile s : getFiles(sf.getFile().getName())){
				if(!s.isOK()){
					wrongFiles.add(sf.getFile().getName() + "/" + s.getFile().getName());
				}
			}
		}
	}
	
	public void showErrorsAgain(){
		checkForError();
		if(!DriveCheck.dc.wrongFiles.isEmpty()){
			String msg = "";
			for(String s : DriveCheck.dc.wrongFiles){
				msg += s + "\n";
			}
			DriveCheck.dc.trayicon.displayMessage("Fehler gefunden", msg, MessageType.ERROR);
		}else{
			DriveCheck.dc.trayicon.displayMessage("Keine Fehler gefunden", "Es wurden keine Fehlerhaften Logs gefunden!", MessageType.INFO);
		}
	}
	
	
	
}
