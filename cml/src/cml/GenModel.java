package cml;

public class GenModel implements IF_LSM {
	public zModel A,B;
	public GMDH criterion;
	
	public GenModel(zModel A,zModel B, GMDH cri) {
//		this.A = A; this.B = B;
//		this.criterion = cri;
//		A.init();
//		B.init();
	}
	
//	public void init() {
//		for (int j = 0; j < A.m1; j++) {
//			
//			switch(criterion) {
//			case BIASCOEF:
//				break;
//			case REG:
//				double b = coef1(B.y, B.z[j]);
//				double CRA = detCR(B.yd,B.zd[j], b);
//				insertCR(B, b, j, CRA);
//				set_coef0(B);
//				break;
//			default: // REGA
//				double a = coef1(A.y, A.z[j]);
//				double CRB = detCR(A.yd,A.zd[j], a);
//				insertCR(A, a, j, CRB);
//				set_coef0(A);
//				break;
//			}
//		}
//	}
	
//	public void set_coef0(zModel Q) {
//		int n = Q.y.n, nd = Q.yd.n;
//		for (int k=0; k<Q.f; k++) {
//			int j   = Q.ij_z[0][k];
//			double a = Q.aij[0][k];
//
//			int kz = Q.m1 + k;
//			Q.a[j][kz] = a;
//
//			// calculation z
//			MathVector z  = new MathVector(n,  kz);
//			MathVector zd = new MathVector(nd, kz);
//			z. runZOne(a, Q.z [j]);
//			zd.runZOne(a, Q.zd[j]);
//			Q.setxi(z, kz, true);
//			Q.setxi(zd, kz, false);
//		}
//	}

//	public void set_coef2(zModel Q) {
//		// копируем коэффициент моделелей
//		double[][] old  = new double[Q.m1][Q.mxz];
//		for (int k=0; k<Q.mxz; k++) {
//			for (int j=0; j<Q.m1; j++) {
//				old[j][k] = Q.a[j][k];
//			}
//		}
//		// recalc a, z, zd
//		for (int k=0; k<Q.f; k++) {
//			if ( Q.L[k] == Q.Lcurrent ) {
//				int kz = Q.m1 + k;
//				int k1 = Q.ij_z[0][k];
//				double a1 = Q.aij[0][k];
//				int k2 = Q.ij_z[1][k];
//				double a2 = Q.aij[1][k];
//				for (int j=0; j < Q.m1; j++) {
//					double anew = a1 * old[j][k1] + a2 * old[j][k2];
//					Q.a[j][kz] = anew;
//					// recalc z, zd [kz]
//					if (j == 0) {
//						Q.z [kz].addFirst (anew, Q.z [j]);
//						Q.zd[kz].addFirst (anew, Q.zd[j]);
//					}
//					else {
//						Q.z [kz].addSecond(anew, Q.z [j]);
//						Q.zd[kz].addSecond(anew, Q.zd[j]);
//					}
//				}
//			}
//		}
//	}
	
	
//	public void insertCR(zModel Q, double a1, int j, double cr) {
//		int fcur = Q.iCRmax; 
//		Q.cr     [fcur] = cr;
//		Q.ij_z[0][fcur] = j; 
//		Q.aij [0][fcur] = a1;
//		Q.L      [fcur] = 0;
//		Q.valCRmax      = cr;
//		// определение нового значени€ дл€ записи критери€
//		detMinCr1(Q,cr);
//	}
	
//	public void insertCR(zModel Q, double a1, int i, double a2, int j, double cr) {
//		int fcur = Q.iCRmax; 
//		Q.cr     [fcur] = cr;
//		Q.aij [0][fcur] = a1;
//		Q.ij_z[0][fcur] = i; 
//		Q.aij [1][fcur] = a2;
//		Q.ij_z[1][fcur] = j; 
//		Q.L      [fcur] = Q.Lcurrent;
//		Q.valCRmax      = cr;
//		// определение нового значени€ дл€ записи критери€
//		detMinCr1(Q,cr);
//		
//	}

//	public void detMinCr1(zModel Q, double cr) {
//		double maxCR = 1e-30;
//		for (int k = 0; k < Q.f; k++) {
//			if (Q.cr[k] > maxCR) {
//				Q.iCRmax = k;
//				Q.valCRmax = Q.cr[k];
//				maxCR = Q.cr[k];
//			}
//		}
//	}
	
//	public void detMinCr0(zModel Q, double cr) {
//		double maxCR = Q.cr[0];
//		for (int k = 1; k < Q.f; k++) {
//			if (Q.L[k] == -1) {
//				Q.iCRmax = k;
//				Q.valCRmax = Q.cr[k];
//				break;
//			}
//			if (Q.cr[k] > maxCR) {
//				Q.iCRmax = k;
//				Q.valCRmax = Q.cr[k];
//				maxCR = Q.cr[k];
//			}
//		}
//	}
	
//	public boolean genPopulation() {
//		A.Lcurrent++;
//		for (int j1 = 0; j1 < A.mxz-1; j1++) {
//			for( int j2 = j1+1; j2 < A.mxz; j2++) {
//				double[] c = A.model2(j1, j2);
//				if (c[3] < A.valCRmax) {
//					insertCR(A, c[0], j1, c[1], j2, c[3]);
//				}
//			}
//		}
//		boolean gen = false;
//		for (int i=0; i<A.f; i++) {
//			if (A.L[i] == A.Lcurrent) {
//				gen = true;
//				break;
//			}
//		}
//		if (gen) set_coef2(A);
//		return gen;
//	}
}