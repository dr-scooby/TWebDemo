package com.jah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ProcessFile extends Thread{
	
	private File afile;
	
	
	public ProcessFile() {
		
	}
	
	public ProcessFile(File f) {
		afile = f;
	}
	
	// the thread run
	public void run() {
		
		try {
			if( afile.canRead() )
				System.out.println("can read");
			String filename = afile.getName();
			System.out.println("file name: " + filename );
			System.out.println("dir: " + afile.getAbsolutePath());
			String ext = getExt(afile);
			if(ext.equals("csv")) {
				System.out.println("csv file");
				try {
					BufferedReader buff = new BufferedReader(new FileReader(afile)) ;
					String line = null;
					int linecount =0;
					int headerline = 0; // header line is the first line
					int headercols; // number of columns
					while( (line = buff.readLine()) != null ) {
						if(headerline == 0) {
							String[] headsplit = line.split("[,]");
							headercols = headsplit.length;
							System.out.println("number of columns: " + headercols);
							headerline ++;
						}else {
							String[] splitup = line.split("[,]"); // split line by , 
							System.out.println("serial: " + splitup[0]);
							System.out.println("model: " + splitup[1]);
							System.out.println("Manufacture: " + splitup[2]);
						}
					}
				}catch(Exception e) {
					
				}
			}else {
				System.out.println("not csv file");
			}
		}catch(Exception e) {
			System.err.println("error in Thread ProcessFile \n" + e.getMessage());
		}
	}// end run

	
	private String getExt(File f) {
		String ext = "";
		
		char dot = '.';
		String filename = f.getName();
		int doti = filename.lastIndexOf(dot);
		ext = filename.substring(doti+1);
		
		return ext;
	}
}
