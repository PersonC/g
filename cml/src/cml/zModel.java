package cml;

import java.io.FileNotFoundException;

public class zModel implements IF_LSM {
	public MathVector   y;
	public MathVector[] z;
	public MathVector   yd;
	public MathVector[] zd;
	public MathVector   yc;
	public MathVector[] zc;
	public int    m,f;
	public int    m1,mxz;
	public double a [][]; // coefficient [m1][mxz]
	public double cr[];   // criterion [f]
	public int    L [];   //           [f]
	public int    Lcurrent = 0;
	public double valCRmax = 1E300;
	public int    iCRmax   = 0;
	
	public int    ij_z[][]; // [2][f] first factor, second factor
	public double aij [][];
	
	private String nameModel;
	private GMDH crit;
	
	public zModel(int m, int f1, String name, GMDH cri) {
		this.nameModel = name;
		int f = f1; // Math.max(1,Math.min(f1,m));
		this.m   = m;
		this.f   = f;
		this.mxz = m + 1 + f;
		this.m1  = m + 1;
		this.crit = cri;
		
		this.z   = new MathVector[mxz];
		this.zd  = new MathVector[mxz];
		this.zc  = new MathVector[m1];
		this.a   = new double[m1][mxz];
		for (int j=0; j<m1; j++) { a[j][j] = 1.0; }
		this.cr  = new double[f];
		this.L   = new int   [f];
		for( int i = 0; i < f; i++) { cr[i]=1E300; L[i]=-1; }
		this.ij_z = new int[2][f];
		this.aij  =  new double[2][f];
	}
	
//===========================================================

	public void set_coef0() {
		int n = y.n, nd = yd.n;
		for (int k=0; k<f; k++) {
			int     j = ij_z[0][k];
			double az = aij [0][k];
			int    kz = m1 + k;
			a[j][kz] = az;
			z[kz]  = new MathVector(n,  kz);
			z[kz].runZOne(az, z [j]); // c валидацией
//			System.out.println(kz + "z ");
//			z[kz].printVector();
			zd[kz] = new MathVector(nd, kz);
			zd[kz].runZOne(az, zd[j]); // c валидацией
			zd[kz].printVector();
		}
	}

	public boolean insertCR(double a1, int i, double a2, int j, double crnew) {
		System.out.println(
				"zModel-insertCR L="+Lcurrent  + " ICRmax: "+ iCRmax + 
				" cr=" + valCRmax + "/ crnew " + crnew + " " + (crnew < valCRmax) +
				" a[" + i + "]=" + a1 + " a[" + j + "]=" + a2);
		if (crnew < valCRmax) {
			cr     [iCRmax] = crnew;
			aij [0][iCRmax] = a1;
			ij_z[0][iCRmax] = i; 
			aij [1][iCRmax] = a2;
			ij_z[1][iCRmax] = j; 
			L      [iCRmax] = Lcurrent;
			detMinCr1();
			return true;
		} else return false;
	}
	
	public void detMinCr1() {
		double maxCR = cr[0];
  		int imax = 0;
		for (int k = 1; k < f; k++) {
			if (cr[k] > maxCR) {
				imax = k; maxCR = cr[k];
			}
		}
		iCRmax = imax; valCRmax = maxCR;
	}
//============================================================
//	public void init() {
//		Lcurrent++;
//
//		//---------
//		double[] bb = new double[4];
//		double[] c = new double[4];
//		double CRA, CRB;
//        //---------
//		
//		for (int j=0; j<m1; j++) {
//			double a1 = coef1(y, z[j]);
//			double CRB1, CRB2, a2;
//			switch(crit) {
//			case BIASCOEF:
//				a2 = coef1(yd, zd[j]);
//				CRB1 = Math.abs(a1-a2);
//				break;
//			case BIASREG:
//				a2 = coef1(yd, zd[j]);
//				CRB1 = detCR(yd,zd[j],a1);
//				CRB2 = detCR(y, z[j], a2);
//				CRB1 = Math.abs(CRB1 / (double) yd.n - CRB2 / (double) y.n);
//				break;
//			case REG:
//
//				c = coef(y, z[j],null,Lcurrent);
////				if (c[2] == -1) return c;
//				c[3] = CR(yd,zd[j],null,c[0],c[1],Lcurrent);
//				CRB1 = c[3];
//				
////				CRB1 = detCR(yd,zd[j], a1);
//				break;
//			case REGB:
//				a1 = coef1(yd, zd[j]);
//				CRB1 = detCR(y, z[j], a1);
//				break;
//			case LSM:
//			default:
//				CRB1 = detCR(y,z[j],a1);
//				break;
//			}
//			insertCR(a1, j, 0.0, -2, CRB1);
//		}
//		set_coef0();
//	}

	public double[] model2(int ii, int jj) {
		double[] bb = new double[4];
		double[] c  = new double[4];
		double   CRA, CRB;
		switch (crit) {
		case BIASCOEF:
			c = coef(y, z[ii],z[jj],Lcurrent);
			if (c[2] == -1) return c;
			bb = coef(yd, zd[ii],zd[jj],Lcurrent);
			if (bb[2] == -1) return c;
			c[3] = Math.abs(c[0]-bb[0]) + Math.abs(c[1]-bb[1]);
			break;
		case BIASREG:	
			c = coef(y, z[ii],z[jj],Lcurrent);
			if (c[2] == -1) return c;
			bb  = coef(yd,zd[ii],zd[jj],Lcurrent);
			if (bb[2] == -1) return c;
			CRB = CR(yd,zd[ii],zd[jj], c[0],c[1] ,Lcurrent);
			CRA = CR(y, z[ii], z[jj], bb[0],bb[1],Lcurrent);
			c[3] = Math.abs(CRB / (double) yd.n - CRA / (double) y.n);
			break;
		case REG:
			c = coef(y, z[ii],z[jj],Lcurrent);
			if (c[2] == -1) return c;
			c[3] = CR(yd,zd[ii],zd[jj],c[0],c[1],Lcurrent);
			break;
		case REGB:
			c = coef(yd, zd[ii],zd[jj],Lcurrent);
			if (c[2] == -1) return c;
			c[3] = CR(y,z[ii],z[jj],c[0],c[1],Lcurrent);
			break;
		case LSM:
		default:
			c = coef(y, z[ii],z[jj],Lcurrent);
			if (c[2] == -1) return c;
			c[3] = c[2];
			break;
		}
		return c;
	}
//----------------------------------------------------------------
	public void generator(boolean printZ, boolean printIteration, int Lmax) {
		gen0();
		if (printZ) printModel(printZ);
		do { if (printIteration) printModel(false);
		} while (genPopulation() && Lcurrent < Lmax);
	}

	public void gen0() {
		Lcurrent++;
		for (int j1 = 0; j1 < m1; j1++) {
			double[] c = model2(j1, j1);
			insertCR(c[0], j1, 0.0, -2, c[3]); 
		}
		set_coef0();
	}
	
	
	public boolean genPopulation() {
		int jStart;
		Lcurrent++;
		boolean gen = false;
		for (int j1 = 0; j1 < mxz-1; j1++) {
			if (j1 < m1) jStart = m1; else jStart = j1+1;
			for( int j2 = jStart; j2 < mxz; j2++) {
				double[] c = model2(j1, j2);
				boolean is_model = insertCR(c[0], j1, c[1], j2, c[3]); 
				gen = is_model || gen;
			}
		}
		if (gen) set_coef2();
		return gen;
	}

	public void set_coef2() {
		// копируем коэффициент моделелей
		double[][] old  = new double[m1][mxz];
		for (int k=0; k<mxz; k++) {
			for (int j=0; j<m1; j++) {
				old[j][k] = a[j][k];
			}
		}
		// recalc a, z, zd
		for (int k=0; k<f; k++) {
			if ( L[k] == Lcurrent ) {
				int kz = m1 + k;
				int k1 = ij_z[0][k];
				double a1 = aij[0][k];
				int k2 = ij_z[1][k];
				double a2 = aij[1][k];
				for (int j=0; j < m1; j++) {
					double anew = a1 * old[j][k1] + a2 * old[j][k2];
					a[j][kz] = anew;
					// recalc z, zd [kz]
					z [kz].addV(anew, z [j], j);
					zd[kz].addV(anew, zd[j], j);
				}
				z[kz].valuation(); zd[kz].valuation();
			}
		}
	}
//----------------------------------------------------------------
	public void printModel(boolean pZ) {
		
		System.out.println("\nModel " + nameModel + " Критерий " + crit);
		if (pZ) {
			System.out.println("\nПараметры обучающей выборки");
			if (y != null) y.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i<mxz; i++) {
				if (z[i] != null) z[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
			System.out.println("Параметры проверяющей выборки");
			if (yd != null) yd.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i<mxz; i++) {
				if (zd[i] != null) zd[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
			System.out.println("Параметры экзаменационной выборки");
			if (yc != null) yc.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i<m1; i++) {
				if (zc[i] != null) zc[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
		} else {
			System.out.println("\nИтерация " + Lcurrent + " max СR[" + iCRmax + "]=" + valCRmax);
			for (int l=m1; l<mxz; l++) {
				System.out.println("Модель " + (l-m1) + " с уровня " + L[l-m1] + ": критерий " + cr[l-m1]);
				for (int j=0; j<m1; j++) {
					System.out.print("a[" + j + "]=" + a[j][l] + " ");
				}
				System.out.print("\nМодель образована из ");
				System.out.println("z{"+ij_z[0][l-m1] + "}=" + aij[0][l-m1]+" и " +
						"z{"+ij_z[1][l-m1] + "}= " + aij[1][l-m1]);
			}
		}
	}
	
	public double[][] matrixR(boolean corr) {
		double[][] R = new double[m1][m1];
		double[][] U = new double[m1][m1];
		for (int i=1; i<m1; i++) {
			for (int j=i; j<m1; j++) {
				if (corr) R[i][j] = corr(z[i], z[j]); else R[i][j] = covar(z[i], z[j]);
				U[i][j] = cosxy(z[i], z[j]);
			}
		}
		
		if (corr) R[0][0] = corr(y, y); else R[0][0] = covar(y, y);
		U[0][0] = cosxy(y, y);
		for (int j=1; j<m1; j++) {
			if (corr) R[0][j] = corr(y, z[j]); else R[0][j] = covar(y, z[j]);
			U[0][j] = cosxy(y, z[j]);
		}
		return R;
	}

	public double[][] matrixU() {
		double[][] U = new double[m1][m1];
		for (int i=1; i<m1; i++) {
			for (int j=i; j<m1; j++) { U[i][j] = cosxy(z[i], z[j]); }
		}
		U[0][0] = cosxy(y, y);
		for (int j=1; j<m1; j++) { U[0][j] = cosxy(y, z[j]); }
		return U;
	}
	
	public void printMatrix() {
		printMatrixR(true);
		printMatrixR(false);
		printMatrixU();
	}
	
	public void printMatrixR(boolean corr) {
		double[][] R = matrixR(corr);
		String sf;
		if (corr) {
			System.out.println("\nКорреляционная матрица");
			sf = " %+1.2f";
		} else {
			System.out.println("\nКовариационная матрица");
			sf = " %+8e";
		}
		for ( int i=0; i<m1; i++) {
			for ( int j=0; j<m1; j++) {
				if(i<j) System.out.printf(sf,R[i][j]); 
				else    System.out.printf(sf,R[j][i]);
			}
			System.out.println("\n");
		}
	}
	
	public void printMatrixU() {
		double[][] U = matrixU();
		String sf = " %+05.1f";
		System.out.println("\nУглы между векторами");
		for ( int i=0; i<m1; i++) {
			for ( int j=0; j<m1; j++) {
				if(i<j) System.out.printf(sf,U[i][j]); 
				else    System.out.printf(sf,U[j][i]);
			}
			System.out.println("\n");
		}
	}
	
//=========================================================================
	public void utilityTest(int n, int m) {
		if (n<1) {
			System.out.println("Не верный объем обучающей выборки " + n);
			return;
		}
		y = new MathVector(n,-1); y.oneVector(2);
		// единичный вектор с валидацией
		z[0] = new MathVector(n,0); z[0].oneVector();
		// нетривиальные факторы
		for (int j=1; j<=m; j++) {
			z[j] = new MathVector(n,j);
			z[j].test(j, 3, 0);
			z[j].valuation();
			for (int i=0; i<y.n; i++) y.v[i]+=z[j].v[i]*(double)j;
		}
		y.valuation();
	}
	
	public void utilityTest(int n, int nd, int m) {
		if (nd<1) {
			System.out.println("Не задан объем проверочной выборки " + nd);
			return;
		}
		utilityTest(n,m);
		yd = new MathVector(nd,-1); yd.oneVector(2);
		// единичный вектор с валидацией
		zd[0] = new MathVector(nd,0); zd[0].oneVector();
		// нетривиальные факторы
		for (int j=1; j<=m; j++) {
			zd[j] = new MathVector(nd,j);
			zd[j].test(j, 3, n);
			zd[j].valuation();
			for (int i=0; i<yd.n; i++) yd.v[i]+=zd[j].v[i]*(double)j;
		}
		yd.valuation();
	}
	
	public void utilityTest(int n, int nd, int nc, int m) {
		if (nc<1) {
			System.out.println("Не верный объем экзаменационной выборки " + nc);
			return;
		}
		utilityTest(n,nd, m);
		yc = new MathVector(nc,-1); yc.oneVector(2);
		// единичный вектор с валидацией
		zc[0] = new MathVector(nc,0); zc[0].oneVector();
		// нетривиальные факторы
		for (int j=1; j<=m; j++) {
			zc[j] = new MathVector(nc,j);
			zc[j].test(j, 3, n+nd);
			zc[j].valuation();
			for (int i=0; i<yc.n; i++) yc.v[i]+=zc[j].v[i]*(double)j;
		}
		yc.valuation();
	}
	
	public void createData(int n, int nd, int nc) {
		if (n>0) {
			y = new MathVector(n, -1);
			for (int j=0; j<m1; j++) { z[j] = new MathVector(n, j); }
			z[0].oneVector();
		}
		if (nd>0) {
			yd = new MathVector(nd, -1);
			for (int j=0; j<m1; j++) { zd[j] = new MathVector(nd, j); }
			zd[0].oneVector();
		}
		if (nc>0) {
			yc = new MathVector(nc, -1);
			for (int j=0; j<m1; j++) { zc[j] = new MathVector(nc, j); }
			zc[0].oneVector();
		}
	}
	
	public String readData(int my, int mx[]) throws FileNotFoundException {
		InputNameFile in =  new InputNameFile();
		boolean isfr = in.openFr();
		if (!isfr) return "";
		int n = y.n, nd = yd.n, nc = yc.n;
		if (in.line == 0) { in.scan.nextLine(); in.line++; }
		int mj = mx.length;
		for (int l=0; l<n; l++) {
			if (in.hasL()) {
				Double d[] = in.getLineDouble();
				for (int i=0; i<d.length; i++) {
					if (i == my) { y.v[l] = d[i]; } 
					else {
						for (int j=0; j<mj; j++) {
							if (i == mx[j]) { z[j+1].v[l] = d[i]; }
						}
					}
				}
			} else { break; }
		}
		for (int l=0; l<nd; l++) {
			if (in.hasL()) {
				Double d[] = in.getLineDouble();
				for (int i=0; i<d.length; i++) {
					if (i == my) { yd.v[l] = d[i]; } 
					else {
						for (int j=0; j<mj; j++) {
							if (i == mx[j]) { zd[j+1].v[l] = d[i]; }
						}
					}
				}
			} else { break; }
		}
		for (int l=0; l<nc; l++) {
			if (in.hasL()) {
				Double d[] = in.getLineDouble();
				for (int i=0; i<d.length; i++) {
					if (i == my) { yc.v[l] = d[i];} 
					else {
						for (int j=0; j<mj; j++) {
							if (i == mx[j]) { zc[j+1].v[l] = d[i]; }
						}
					}
				}
			} else { break; }
		}
		y.valuation(); yd.valuation(); yc.valuation();
		for (int j=1; j<m1; j++) {
			z[j].valuation(); zd[j].valuation(); zc[j].valuation();
		}
		return in.nameFile + " " + isfr;
	}
}
