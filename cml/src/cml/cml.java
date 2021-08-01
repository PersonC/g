package cml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class cml implements IF_LSM {
	public static boolean TEST = true;
	public static boolean PRFILE = true;
	
	public static void main(String[] args) throws FileNotFoundException {
		boolean printZ = false, printIteration = false;
        // Create new print stream for file.
        if (PRFILE) System.setOut(new PrintStream(new FileOutputStream(new File("file.txt"))));
        
        System.out.println(new Date());
//---------------------------------------------------
		if (TEST) {
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

			zModel ft = new zModel(mt,ff,"Тестовая модель",t7);
			new TestCml(ft); // создание тестовых данных на все выборки
//			ft.setScale(false, null);
			new putTable(ft); // вывод данных в графическое окно
//			ft.printMatrix(); // вывод коррелляционных матриц
			ft.generator(printIteration,Lmaxt);
//			ft.revertScaleY();
			new putTable(ft,0);
			ft.bestModel();
	        ft.printModel();
//	        Estimate es = new Estimate();
//	        es.calcEps2(ft);
	        new Estimate().calcEps2(ft);
		} else {
			int m = 5, f = 2, Lmax = 19;
			zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
			int n = 12, nd = 10, nc = 5;
	        int my = 3;
	        int[] mx = {2,4,5,6,7};
	        System.out.println(fa.readData(n,nd,nc,my,mx));
			fa.printMatrix();
			fa.generator(printIteration,Lmax);
			fa.bestModel();
	        fa.printModel();
		}
	}
}