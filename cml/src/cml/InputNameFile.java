package cml;

import java.awt.FileDialog;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;

public class InputNameFile {
	public int line = 0;
	public String nameFile;
	public FileReader fr;
	public Scanner scan;
	protected String dlmt = " ";
	private boolean openfr = false;

	InputNameFile() {
//		Scanner Inpt = new Scanner(System.in);
//		System.out.println("What is name of file?");
//		this.nameFile = Inpt.next();
//		Inpt.close();
		this.nameFile = openFile();
	}
	
	InputNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	
	public boolean openFr() throws FileNotFoundException {
		try {
			fr = new FileReader(nameFile);
			this.scan = new Scanner(fr);
			this.openfr = true;
			return true;
		} catch (FileNotFoundException e) {
//			fr = null;
//			this.scan = null;
//			this.openfr = false;
			return false;
		}
	}
	
	public String[] getLineString() {
		line++;
		return scan.nextLine().split(dlmt);
	}
	
	public Double[] getLineDouble( ) {
		String[] s;
		s = scan.nextLine().split(dlmt);
		Double[] d = new Double[s.length];
		for (int i=0; i<s.length; i++) {
			try {
				d[i] = Double.parseDouble(s[i]);
			} catch (NumberFormatException e) {
				d[i] = -2e-300;
			}
		}
		line++;
		return d;
	}
	
	public boolean hasL() {
		return scan.hasNext();
	}
	
	public void closefr() throws IOException {
		if (openfr) {
			scan.close();
			try { fr.close(); } catch (FileNotFoundException e) {
				System.out.println("fr.close " + e);
			};
		}
	}
	public String openFile() {
		
		JFrame shell = new JFrame();
		FileDialog fDia = new FileDialog(shell, "Open file with data", FileDialog.LOAD);
		fDia.setVisible(true);
        String name = fDia.getDirectory()+fDia.getFile();
        shell.dispose();
        return(name);
	}
	
}
