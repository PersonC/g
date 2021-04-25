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
	public double muy = 0.5;
	
	private String nameModel;
	protected GMDH crit;
	private double a1,a2,CRvalue;
	
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
		for (int k=0; k<f; k++) {
			int     j = ij_z[0][k];
			double az = aij [0][k];
			int    kz = m1 + k;
			a[j][kz] = az;
			z[kz]  = new MathVector(y.n,  kz);
			z[kz].runZOne(az, z [j]); // c валидацией
//			System.out.println(kz + "z ");
//			z[kz].printVector();
			if ( crit != GMDH.LSM ) {
				zd[kz] = new MathVector(yd.n, kz);
				zd[kz].runZOne(az, zd[j]); // c валидацией
				zd[kz].printVector();
			}
		}
	}

	public int insertCR(double a1, int i, double a2, int j, double crnew) {
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
			return 1;
		} else return 0;
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

	public boolean model2(int ii, int jj) {
		double[] bb = new double[2];
		double[] c  = new double[2];
		switch (crit) {
		case BIAS_REG:
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			bb  = coef(yd,zd[ii],zd[jj],Lcurrent);
			if (bb[0] == 0 && bb[1] == 0) return false;
			if (Lcurrent > 1) CRvalue = detCR(z[ii], zd[ii], a1, bb[0]);
			else CRvalue = detCR(z[ii], zd[ii], z[jj], zd[jj], a1, a2, bb[0], bb[1]);
			CRvalue = CRvalue + muy * (
					  CR(yd,zd[ii],zd[jj], a1,   a2,   Lcurrent) +
					  CR(y, z[ii], z[jj],  bb[0],bb[1],Lcurrent)  );
			break;
		case BIAS: // ||ym(A)-ym(B)||
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			bb  = coef(yd,zd[ii],zd[jj],Lcurrent);
			if (bb[0] == 0 && bb[1] == 0) return false;
			if (Lcurrent > 1) CRvalue = detCR(z[ii], zd[ii], a1, bb[0]);
			else CRvalue = detCR(z[ii], zd[ii], z[jj], zd[jj], a1, a2, bb[0], bb[1]);
			break;
		case REG_AB:	
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			bb  = coef(yd,zd[ii],zd[jj],Lcurrent);
			if (bb[0] == 0 && bb[1] == 0) return false;
			CRvalue = CR(yd,zd[ii],zd[jj], a1,   a2   ,Lcurrent) +
			          CR(y, z[ii] , z[jj], bb[0],bb[1],Lcurrent);
			break;
		case BIASCOEF:
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			bb = coef(yd, zd[ii],zd[jj],Lcurrent);
			if (bb[0] == 0 && bb[1] == 0) return false;
			CRvalue = Math.abs(a1-bb[0]) + Math.abs(a2-bb[1]);
			break;
		case REG:
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			CRvalue = CR(yd,zd[ii],zd[jj],a1,a2,Lcurrent);
			break;
		case REGB:
			c = coef(yd, zd[ii],zd[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			CRvalue = CR(y,z[ii],z[jj],a1,a2,Lcurrent);
			break;
		case REGCOS:
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			CRvalue = CRcos(yd,zd[ii],zd[jj],a1,a2,Lcurrent);
			break;
		case REGCOSB:
			c = coef(yd, zd[ii],zd[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			CRvalue = CRcos(y,z[ii],z[jj],a1,a2,Lcurrent);
			break;
		case LSM:
		default:
			c = coef(y, z[ii],z[jj],Lcurrent);
			a1 = c[0]; a2 = c[1];
			if (a1 == 0 && a2 == 0) return false;
			CRvalue = CR(y,z[ii],z[jj],a1,a2,Lcurrent);
			break;
		}
		return true;
	}
//----------------------------------------------------------------
	public void generator(boolean printZ, boolean printIteration, int Lmax) {
		gen0();
		if (printZ) printModel(printZ);
		do { if (printIteration) printModel(false);
		} while (genPopulation()>0 && Lcurrent < Lmax);
	}

	public void gen0() {
		Lcurrent++;
		for (int j1 = 0; j1 < m1; j1++) {
			if (model2(j1, j1)) insertCR(a1, j1, 0.0, -2, CRvalue); 
		}
		set_coef0();
	}
	
	public int genPopulation() {
		int jStart, gen = 0;
		Lcurrent++;
		for (int j1 = 0; j1 < mxz-1; j1++) {
			if (j1 < m1) jStart = m1; else jStart = j1+1;
			for( int j2 = jStart; j2 < mxz; j2++) {
				if ( model2(j1, j2) ) gen += insertCR(a1, j1, a2, j2, CRvalue); 
			}
		}
		if (gen > 0) set_coef2();
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
		if (crit != GMDH.LSM) {
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
					z[kz].valuation(); 
					zd[kz].valuation();
				}
			}
		} else {
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
//						zd[kz].addV(anew, zd[j], j);
					}
					z[kz].valuation(); 
//					zd[kz].valuation();
				}
			}
		}
	}
//----------------------------------------------------------------
	public void printModel(boolean pZ) {
		
		System.out.println("\nModel " + nameModel + " Критерий " + crit + ": " + nameGMDH[ crit.ordinal()]);
		if (pZ) {
			System.out.println("\nПараметры обучающей выборки");
			if (y != null) y.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i<mxz; i++) {
				if (z[i] != null) z[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
			if (crit != GMDH.LSM) {
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
	
	public String readData(int na, int nb, int nc, int my, int mx[]) throws FileNotFoundException {
		createData(na, nb, nc);
		return readData(my, mx);
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
