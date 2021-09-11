package cml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class cml implements IF_LSM {
	
	
	public static void main(String[] args) throws FileNotFoundException {
		setFileOut("file.txt");
		CmlUI UI = new CmlUI();
		
        // Create new print stream for file.
        if (PRFILE) System.setOut(cml.fileOut);
        // 
        System.out.println(new Date());
	}
//--------main model calculation-------------------------------------------
	public static void run(CmlUI UI) throws FileNotFoundException {
		int m = 5, f = 2, Lmax = 19;
		zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
		int n = 12, nd = 10, nc = 5;
        int my = 3;
        int[] mx = {2,4,5,6,7};
        System.out.println(fa.readData(n,nd,nc,my,mx));
		fa.printMatrix();
		fa.generator(Lmax);
		fa.bestModel();
        fa.printModel();
	}
//---------------------------------------------------
	

//	public static boolean TEST = true;
//	static void setTEST(boolean value) {TEST = value;}
//	static boolean getTEST() {return TEST;}
//------------------------------------------------------------------------	
	public static boolean PRFILE = true;
	public static PrintStream standartOut = System.out;
	public static PrintStream fileOut;
	static void setFileOut(String nameFile) throws FileNotFoundException {
		fileOut = new PrintStream(new FileOutputStream(new File(nameFile)));
	}
	static void setPRFILE(boolean value) {PRFILE = value;}
	static boolean getPRFILE() {return PRFILE;}
//------------------------------------------------------------------------
    // test model
	public static void runTest(CmlUI UI) {
		int mt = 2, ff = 6, Lmaxt = 99859;
		GMDH t0 = GMDH.LSM; 
		GMDH t1 = GMDH.REG; 
		GMDH t2 = GMDH.REGB; 
		GMDH t3 = GMDH.REG_AB;
		GMDH t4 = GMDH.REGCOS; 
		GMDH t5 = GMDH.REGCOSB;
		GMDH t6 = GMDH.BIAS_REG; 
		GMDH t7 = GMDH.BIAS; 
		GMDH t8 = GMDH.BIASCOEF;

		zModel ft = new zModel(mt,ff,"Тестовая модель",t0);
		new TestCml(ft); // создание тестовых данных на все выборки
//		ft.setScale(false, null);
		UI.putTable(ft);
//		ft.printMatrix(); // вывод коррелляционных матриц
		ft.generator(Lmaxt);
//		ft.revertScaleY();
		UI.putTable(ft,0);
		ft.bestModel();
        ft.printModel();
        new Estimate().calcEps2(ft);
	}
	
	
}