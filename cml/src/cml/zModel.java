package cml;

import java.io.FileNotFoundException;

public class zModel implements IF_LSM {
	public MathVector   y, yd, yc;
	public MathVector[] z, zd, zc;
	public int    m,f,m1,mxz;
	public double a [][]; // coefficient [m1][mxz]
	public double cr[];   // criterion [f]
	public int    L [];   //           [f]
	public int    Lcurrent = 0, iCRmax = 0;
	public double valCRmax = 1E300;
	
	public int    ij_z[][]; // [2][f] first factor, second factor
	public double aij [][]; //
	
	private String nameModel;
	protected GMDH crit;
	private double a1,a2,CRvalue;

	public zModel(int m, int f1, String name, GMDH cri) {
		this.nameModel = name;
		this.m    = m;
		this.f    = f1;
		this.mxz  = m + 1 + this.f;
		this.m1   = m + 1;
		this.crit = cri;
		
		this.z   = new MathVector[mxz];
		this.zd  = new MathVector[mxz];
		this.zc  = new MathVector[mxz];
		this.a   = new double[m1][mxz];
		for (int j=0; j<m1; j++) { a[j][j] = 1.0; }
		this.cr  = new double[this.f];
		this.L   = new int   [this.f];
		for( int i = 0; i < this.f; i++) { cr[i]=1E300; L[i]=-1; }
		this.ij_z = new int[2][this.f];
		this.aij  =  new double[2][this.f];
	}
	
//===========================================================
	public void setScale(boolean sy, boolean[] sx) {
		if (sy) {
			double scale = whatScale(0);
			y.makeScale(scale);
			if (yd != null) yd.makeScale(scale);
			if (yc != null) yc.makeScale(scale);
		}
		if (sx == null) return;
		for (int l=0; l<sx.length; l++) {
			if (sx[l]) {
				double scale = whatScale(l+1);
				z[l+1].makeScale(scale);
				if (zd[l+1] != null) zd[l+1].makeScale(scale);
				if (zc[l+1] != null) zc[l+1].makeScale(scale);
			}
		}
	}
	
	public void revertScaleY() {
		if (y.scale == 1) return; // coefficient [m1][mxz]
		for (int i=0; i<m1; i++) {
			for (int j=0; j<mxz; j++) {	a[i][j] *= y.scale;	}
		}
	}
	
	public double whatScale(int l) {
		double nful = 0, a = 0;
		if (l == 0) { // y
			nful = y.n;
			a = y.vAverage * (double) y.n;
			if (yd != null) {
				nful += yd.n;
				a += yd.vAverage * (double) yd.n;
			}
//			nful += yc.n;
//			a += yc.vAverage * (double) yc.n;
		} else { // z[l]
			nful = z[l].n;
			a = z[l].vAverage * (double) z[l].n;
			if (zd[l] != null) {
				nful += zd[l].n;
				a += zd[l].vAverage * (double) zd[l].n;
			}
//			nful += yc.n;
//			a += zc[l].vAverage * (double) zc[l].n;
		}
		return(a / nful);
	}
	
	public void set_coef0() {
		for (int k=0; k<f; k++) {
			int     j = ij_z[0][k];
			double az = aij [0][k];
			int    kz = m1 + k;
			a[j][kz] = az;
			z[kz]    = new MathVector(y.n,  kz);
			z[kz].runZOne(az, z [j]);      // c валидацией
			if ( crit != GMDH.LSM ) {
				zd[kz] = new MathVector(yd.n, kz);
				zd[kz].runZOne(az, zd[j]); // c валидацией
			}
		}
	}

	public int insertCR(double a1, int i, double a2, int j, double crnew) {
		if (j >= 0) {
			if ( (a1 == 1.0) && (a2 == 0.0)) return 0;
		    if ( a1 == 0.0 && a2 == 1.0) return 0;
		}
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
			if (cr[k] > maxCR) { imax = k; maxCR = cr[k]; }
		}
		iCRmax = imax; valCRmax = maxCR;
	}
	
	public double[] coef(MathVector y, MathVector x1, MathVector x2, int Lc) {
		double[] lsm;
		if (Lc <=1 ) lsm = coef2(y,x1); else lsm = coef2(y,x1,x2);
		if (CG.isRoundCoef2) {
			lsm[0] = roundAvoid(lsm[0],CG.round_a2);
			lsm[1] = roundAvoid(lsm[1],CG.round_a2);
		}
		return lsm;
	}

	public boolean model2(int ii, int jj) {
		double[] b = new double[2];
		double[] c = new double[2];

		switch (crit) {
			case BIAS_REG:
			case BIAS: 
			case REG_AB:	
			case BIASCOEF:
			case REG:
			case REGCOS:
			case LSM:
			default:
				c = coef(y, z[ii],z[jj],Lcurrent);
				if (c[0] == 0 && c[1] == 0) return false;
				break;
			case REGB:
			case REGCOSB:
				c = coef(yd,zd[ii],zd[jj],Lcurrent);
				if (c[0] == 0 && c[1] == 0) return false;
				break;
		}
		
		switch (crit) {
			case BIAS_REG:
			case REG_AB:	
			case BIASCOEF:
			case BIAS:	
				b = coef(yd,zd[ii],zd[jj],Lcurrent);
				if (b[0] == 0 && b[1] == 0) return false;
				break;
		}
		
		switch (crit) {
		case BIAS_REG:
			CRvalue = (Lcurrent > 1) ?
					detCR(z[ii],zd[ii],c[0], b[0]) :
			        detCR(z[ii],zd[ii],z[jj],zd[jj],c[0],c[1],b[0],b[1]);
			CRvalue = CRvalue + CG.muy * (
					  CR(yd,zd[ii],zd[jj],c[0],c[1],Lcurrent) +
					  CR(y, z[ii], z[jj] ,b[0],b[1],Lcurrent));
			break;
		case BIAS: // ||ym(A)-ym(B)||
			CRvalue = (Lcurrent > 1) ? 
					detCR(z[ii],zd[ii],c[0],b[0]) :
			        detCR(z[ii],zd[ii],z[jj],zd[jj],c[0],c[1],b[0],b[1]);
			break;
		case REG_AB:	
			CRvalue = CR(yd,zd[ii],zd[jj],c[0],c[1],Lcurrent) +
			          CR(y, z[ii] , z[jj],b[0],b[1],Lcurrent);
			break;
		case BIASCOEF:
			CRvalue = Math.abs(c[0]-b[0]) + Math.abs(c[1]-b[1]);
			break;
		case REG:
			CRvalue = CR(yd,zd[ii],zd[jj],c[0],c[1],Lcurrent);
			break;
		case REGB:
			CRvalue = CR(y,z[ii],z[jj],c[0],c[1],Lcurrent);
			break;
		case REGCOS:
			CRvalue = CRcos(yd,zd[ii],zd[jj],c[0],c[1],Lcurrent);
			break;
		case REGCOSB:
			CRvalue = CRcos(y,z[ii],z[jj],c[0],c[1],Lcurrent);
			break;
		case LSM:
		default:
			CRvalue = CR(y,z[ii],z[jj],c[0],c[1],Lcurrent);
			break;
		}
		a1 = c[0]; a2 = c[1];
		return true;
	}
//----------------------------------------------------------------
	public void generator(boolean printIteration, int Lmax) {
		gen0();
//		printModel();
		do { 
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
			for (int j=0; j<m1; j++) { old[j][k] = a[j][k]; }
		}
		// recalc a, z, zd
		if (crit != GMDH.LSM) {
			for (int k=0; k<f; k++) {
				if ( L[k] == Lcurrent ) {
					int    kz = m1 + k, k1 = ij_z[0][k], k2 = ij_z[1][k];
					double a1 = aij[0][k], a2 = aij[1][k];
					for (int j=0; j < m1; j++) {
						double anew = a1 * old[j][k1] + a2 * old[j][k2];
						if (CG.isRoundCoefZ) anew = roundAvoid(anew, CG.round_z);
						a[j][kz] = anew; // recalc z, zd [kz]
						z [kz].addV(anew, z [j], j);
						zd[kz].addV(anew, zd[j], j);
					}
					z[kz].valuation(); 	zd[kz].valuation();
				}
			}
		} else {
			for (int k=0; k<f; k++) {
				if ( L[k] == Lcurrent ) {
					int kz = m1 + k, k1 = ij_z[0][k], k2 = ij_z[1][k];
					double a1 = aij[0][k], a2 = aij[1][k];
					for (int j=0; j < m1; j++) {
						double anew = a1 * old[j][k1] + a2 * old[j][k2];
						a[j][kz] = anew;
						if (CG.isRoundCoefZ) anew = roundAvoid(anew, CG.round_z);
						z [kz].addV(anew, z [j], j);  // recalc z, zd [kz]
					}
					z[kz].valuation(); 
				}
			}
		}
	}
//----------------------------------------------------------------
	public void bestModel() {
		System.out.println("\nModel " + nameModel + " Критерий " + crit + ": " + 
                nameGMDH[ crit.ordinal()] + "\nЛучшая модель: ");
		double c = cr[0];
		int ll = 0;
		for (int m=1; m < f; m++) {
			if (cr[m] < c) { c = cr[m]; ll=m; }
		}
		System.out.println("Модель " + ll + " с уровня " + L[ll] + ": критерий " + cr[ll]);
		for (int j=0; j < m1; j++) {
			System.out.print("a[" + j + "]=" + a[j][ll+m1] + " ");
		}
		System.out.println("\n");
	}
	
	public void printZ() {
		System.out.println("\nПараметры обучающей выборки");
		if (y != null) y.printVector();
		else System.out.println("Отсутствует y");
		for (int i=0; i < mxz; i++) {
			if (z[i] != null) z[i].printVector();
			else System.out.println("Отсутствует фактор " + i);
		}
		if (crit != GMDH.LSM) {
			System.out.println("Параметры проверяющей выборки");
			if (yd != null) yd.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i < mxz; i++) {
				if (zd[i] != null) zd[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
			System.out.println("Параметры экзаменационной выборки");
			if (yc != null) yc.printVector();
			else System.out.println("Отсутствует y");
			for (int i=0; i < m1; i++) {
				if (zc[i] != null) zc[i].printVector();
				else System.out.println("Отсутствует фактор " + i);
			}
		}
	}
	
	public void printModel() {
		System.out.println("\nИтерация " + Lcurrent + " max СR[" + iCRmax + "]=" + valCRmax);
		for (int l=m1; l<mxz; l++) {
			System.out.println("Модель " + (l-m1) + " с уровня " + L[l-m1] + ": критерий " + cr[l-m1]);
			if (yc != null) zc[l] = new MathVector(yc.n, l);
			for (int j=0; j<m1; j++) {
				System.out.print("a[" + j + "]=" + a[j][l] + " ");
				if (yc != null) zc[l].addV(a[j][l], zc[j], j);
			}
			if (yc != null) {
				zc[l].valuation();
				double CRC = Math.sqrt(detCR(yc,zc[l],1))/yc.norma;
				System.out.println("Критерий на экзаменационной выборке: " + CRC);
			}
			System.out.print("Модель образована из ");
			System.out.println("z{"+ij_z[0][l-m1] + "}=" + aij[0][l-m1]+" и " +
					"z{"+ij_z[1][l-m1] + "}= " + aij[1][l-m1] + "\n");
		}
	}
	
	public double[][] matrixR(boolean corr) {
		double[][] R = new double[m1][m1];
		double[][] U = new double[m1][m1];
		for (int i=1; i < m1; i++) {
			for (int j=i; j < m1; j++) {
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
		for (int i=1; i < m1; i++) {
			for (int j=i; j < m1; j++) { U[i][j] = cosxy(z[i], z[j]); }
		}
		U[0][0] = cosxy(y, y);
		for (int j=1; j < m1; j++) { U[0][j] = cosxy(y, z[j]); }
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
		for ( int i=0; i < m1; i++) {
			for ( int j=0; j < m1; j++) {
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
		for ( int i=0; i < m1; i++) {
			for ( int j=0; j < m1; j++) {
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
	
	public void accuracyC() {
		if (yc == null) {
			System.out.println("Отсутствует экзаменационная выборка");
			return;
		}
	}
	
	public static class CG {
		static final double EPS = 1e-20;
// round coefficient
		static boolean isRoundCoef2 = false;
		static boolean isRoundCoefZ = false;
		static int round_a2 = 2;             // scale a2
		static int round_z  = 2;             // scale z

		static double muy = 0.5; // for BIAS_REG
		
	}
	
}
