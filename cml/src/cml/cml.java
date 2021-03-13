package cml;

import java.io.FileNotFoundException;

public class cml implements IF_LSM {
//	public static boolean TEST = false;
	public static boolean TEST = true;

	public static void main(String[] args) throws FileNotFoundException {
		int m = 5, f = 2, Lmax = 19;
		boolean printZ = false, printIteration = true;
//---------------------------------------------------
//		zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
//---------------------------------------------------
		if (TEST) {
			int na =12, nb =10, ncc   = 5;
			int mt = 6, ff = 2, Lmaxt = 19;
			GMDH test = GMDH.LSM; // GMDH.BIASCOEF
			zModel ft = new zModel(mt,ff,"Обучающая А",test);
			ft.utilityTest(na,nb,ncc,mt);
    		ft.init();
			ft.printMatrixR(true);
			ft.printMatrixR(false);
			ft.generator(printZ,true,Lmaxt);
	        ft.printModel(true);
		} else {
			zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
			int n = 12, nd = 10, nc = 5;
	        fa.createData(n, nd, nc);
	        int my = 3;
	        int[] mx = {2,4,5,6,7};
	        System.out.println(fa.readData(my,mx));
			fa.init();
			fa.printMatrixR(true);
			fa.printMatrixR(false);
			fa.generator(printZ,printIteration,Lmax);
	        fa.printModel(true);
		}
//-----------------------------------------------------

	}
}

