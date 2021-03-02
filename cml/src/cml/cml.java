package cml;

public class cml implements IF_LSM {

	public static void main(String[] args) {
//		int n = 12;
//		MathVector y = new MathVector(n,-1),
//				   e = new MathVector(n, 0),
//				   x1 = new MathVector(n, 1),
//				   x2 = new MathVector(n, 2);
//		y.test(1, 1.1);     // random * 1.1
//		e.test(0, 1);       // const = 1 
//		x1.test(2, 1.2);    // 1.2 * i
//		x2.test(1,2.0);
//--------------------------------------------------
//		int nd = 10;
//		MathVector yd = new MathVector(nd,-1),
//				   ed = new MathVector(nd, 0),
//				   x1d = new MathVector(nd, 1),
//				   x2d = new MathVector(nd, 2);
//		yd.test(1, 1.2);     // random * 1.2
//		ed.test(0, 1);       //  
//		x1d.test(2, 1.3);    // 1.3 * i
//		x2d.test(2, 2.3);
//---------------------------------------------------
		zModel fa = new zModel(2,2,"Обучающая А",GMDH.BIASCOEF);
		fa.utilityTest(12,10);
//		fa.sety(y,true);     fa.sety(yd,false);
//		fa.setxi(e,0,true);  fa.setxi(ed,0,false);
//		fa.setxi(x1,1,true); fa.setxi(x1d,1,false);
//		fa.setxi(x2,2,true); fa.setxi(x2d,2,false);
//		fa.init();

//-----------------------------------------------------
		fa.generator(true,true,19);
	}
}

