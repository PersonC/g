package cml;

import java.io.FileNotFoundException;

public class cml implements IF_LSM {

	public static void main(String[] args) throws FileNotFoundException {
		int n = 12, nd = 10, nc = 5, m = 5, f = 2, Lmax = 19;
		boolean printZ = true, printIteration = true;
//---------------------------------------------------
		zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
//---------------------------------------------------		
        fa.createData(n, nd, nc);
        int my = 3;
        int[] mx = {2,4,5,6,7};
        System.out.println(fa.readData(my,mx));
//---------------------------------------------------		
//		fa.utilityTest(n,nd,nc);
		fa.init();
		fa.printMatrixR(true);
		fa.printMatrixR(false);
//-----------------------------------------------------
		fa.generator(printZ,printIteration,Lmax);
        fa.printModel(true);

	}
}

