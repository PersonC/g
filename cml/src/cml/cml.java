package cml;

public class cml implements IF_LSM {

	public static void main(String[] args) {
		int n = 12, nd = 10, nc = 5, m = 2, f = 2, Lmax = 19;
		boolean printZ = true, printIteration = true;
//---------------------------------------------------
		zModel fa = new zModel(m,f,"Обучающая А",GMDH.BIASCOEF);
		fa.utilityTest(n,nd,nc);
		fa.init();
		fa.printMatrixR(true);
		fa.printMatrixR(false);
//-----------------------------------------------------
		fa.generator(printZ,printIteration,Lmax);
	}
}

