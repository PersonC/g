package cml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class cml implements IF_LSM {
//	public static boolean TEST = false;
	public static boolean TEST = true;
	public static boolean PRFILE = true;

	public static void main(String[] args) throws FileNotFoundException {
		boolean printZ = false, printIteration = true;
        // Create new print stream for file.
        if (PRFILE) System.setOut(new PrintStream(new FileOutputStream(new File("file.txt"))));
System.out.println(new Date());
//---------------------------------------------------
		if (TEST) {
			int na =12, nb =12, ncc   = 5;
			int mt = 2, ff = 6, Lmaxt = 99859; // mt=6
//			GMDH test = GMDH.LSM; 
			GMDH test = GMDH.REG; 
//			GMDH test = GMDH.BIASCOEF;
//			GMDH test = GMDH.BIASREG; 

			zModel ft = new zModel(mt,ff,"Обучающая А",test);
			ft.utilityTest(na,nb,ncc,mt);
			ft.printMatrix();
			ft.generator(printZ,true,Lmaxt);
	        ft.printModel(true);
		} else {
			int m = 5, f = 2, Lmax = 19;
			zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
			int n = 12, nd = 10, nc = 5;
	        fa.createData(n, nd, nc);
	        int my = 3;
	        int[] mx = {2,4,5,6,7};
	        System.out.println(fa.readData(my,mx));
			fa.printMatrix();
			fa.generator(printZ,printIteration,Lmax);
	        fa.printModel(true);
		}
	}
}

