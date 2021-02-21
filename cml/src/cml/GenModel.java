package cml;

public class GenModel implements IF_LSM {
	public zModel A,B;
	
	public GenModel(zModel A,zModel B) {
		this.A = A; this.B = B;
		init();
	}
	
	public void init() {
//		double[] lsmA = new double[3];
//		double[] lsmB = new double[3];
		for (int j = 0; j < A.m1; j++) {
			
			double a = coef1(A.y, A.z[j]);
			double CRB = detCR(A.yd,A.zd[j], a);
			insertCR(A, a, j, CRB);

			double b = coef1(B.y, B.z[j]);
			double CRA = detCR(B.yd,B.zd[j], b);
			insertCR(B, b, j, CRA);
			
		}
		// рассчитать коэффициенты
		set_coef0(A); set_coef0(B);
	}
	
	public void set_coef0(zModel Q) {
		int n = Q.y.n, nd = Q.yd.n;
		for (int k=0; k<Q.f; k++) {
			int j = Q.ij_z[1][k];
			int kz = Q.m + 1 + k;
			double a = Q.aij[1][k];
			Q.a[j][k] = a;
			// calculation z
			MathVector z  = new MathVector(n,  kz);
			MathVector zd = new MathVector(nd, kz);
			z. runZOne(a, Q.z [j]);
			zd.runZOne(a, Q.zd[j]);
			Q.setxi(z, kz, true);
			Q.setxi(zd, kz, false);
		}
	}
	
	public void insertCR(zModel Q, double a1, int j, double cr) {
		int fcur = Q.iCRmin; 
		Q.cr     [fcur] = cr;
		Q.ij_z[1][fcur] = j; 
		Q.aij [1][fcur] = a1;
		Q.L      [fcur] = 0;
		// определение нового значения для записи критерия
		detMinCr0(Q,cr);
	}
	
	public void detMinCr0(zModel Q, double cr) {
		double maxCR = Q.cr[0];
		for (int k = 1; k < Q.f; k ++) {
			if (Q.L[k] == -1) {
				Q.iCRmin = k;
				Q.valCRmin = Q.cr[k];
				break;
			}
			if (Q.L[k] > maxCR) {
				Q.iCRmin = k;
				maxCR = Q.cr[k];
				Q.valCRmin = Q.cr[k];
			}
		}
	}
}
