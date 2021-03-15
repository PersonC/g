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
		int f = Math.max(1,Math.min(f1,m));
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
	public void init() {
		Lcurrent++;
		for (int j=0; j<m1; j++) {
			double a1 = coef1(y, z[j]);
			double CRB1, CRB2, a2;
			switch(crit) {
			case BIASCOEF:
				a2 = coef1(yd, zd[j]);
				CRB1 = Math.abs(a1-a2);
				break;
			case BIASREG:
				a2 = coef1(yd, zd[j]);
				CRB1 = detCR(yd,zd[j],a1);
				CRB2 = detCR(y, z[j], a2);
				CRB1 = Math.abs(CRB1 / (double) yd.n - CRB2 / (double) y.n);
				break;
			case REG:
				CRB1 = detCR(yd,zd[j], a1);
				break;
			case LSM:
			default:
				CRB1 = detCR(y,z[j],a1);
				break;
			}
			insertCR(a1, j, 0.0, -2, CRB1);
		}
		set_coef0();
	}

	public void set_coef0() {
		int n = y.n, nd = yd.n;
		for (int k=0; k<f; k++) {
			int     j = ij_z[0][k];
			double az = aij [0][k];
			int    kz = m1 + k;
			a[j][kz] = az;
			z[kz]  = new MathVector(n,  kz);
			z[kz].runZOne(az, z [j]);
			zd[kz] = new MathVector(nd, kz);
			zd[kz].runZOne(az, zd[j]);
		}
	}

	public boolean insertCR(double a1, int i, double a2, int j, double crnew) {
		System.out.println("L="+Lcurrent  + ": "+ iCRmax + 
				" cr=" + valCRmax + "/" + crnew +
				" i=" + i + " " + a1 + " j=" + j + " " + a2);
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
	public double[] model2(int ii, int jj) {
		double[] b = new double[4], bb = new double[4];
		double[] c = new double[4];
		double CRA, CRB;
// common
		b = coef2(y, z[ii],z[jj]);
		c[0] = b[0]; // первый коэффициент 
		c[1] = b[1]; // второй коэффициент
		c[2] = b[2]; // внутренний критерий
		c[3] = 1e30; // внешний критерий
		if (b[2] == -1) return c;
// 		
		switch (crit) {
		case BIASCOEF:
			bb = coef2(yd, zd[ii],zd[jj]);
			if (bb[2] == -1) return c;
			c[3] = Math.abs(c[0]-bb[0]) + Math.abs(c[1]-bb[1]);
			break;
		case BIASREG:	
			bb = coef2(yd, zd[ii],zd[jj]);
			CRB = detCR(yd,zd[ii],zd[jj],c[0],c[1]);
			CRA = detCR(y,z[ii],z[jj],bb[0],bb[1]);
			if (bb[2] == -1) return c;
			c[3] = Math.abs(CRB / (double) yd.n - CRA / (double) y.n);
			break;
		case REG:
			c[3] = detCR(yd,zd[ii],zd[jj],c[0],c[1]);
			break;
		case LSM:
		default:
			c[3] = c[2];
			break;
		}
		return c;
	}
//----------------------------------------------------------------
	public void generator(boolean printZ, boolean printIteration, int Lmax) {
		if (printZ) printModel(printZ);
		do {
			if (printIteration) printModel(false);
		} while (genPopulation() && Lcurrent < Lmax);
	}

	public boolean genPopulation() {
		Lcurrent++;
		boolean gen = false;
		for (int j1 = 0; j1 < mxz-1; j1++) {
			for( int j2 = j1+1; j2 < mxz; j2++) {
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
					if (j == 0) {
						z [kz].addFirst (anew, z [j]);
						zd[kz].addFirst (anew, zd[j]);
					}
					else {
						z [kz].addSecond(anew, z [j]);
						zd[kz].addSecond(anew, zd[j]);
					}
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
		for (int i=0; i<m1; i++) {
			for (int j=i; j<m1; j++) {
				if (corr) R[i][j] = corr(z[i], z[j]); else R[i][j] = covar(z[i], z[j]);
			}
		}
		return R;
	}
	
	public void printMatrixR(boolean corr) {
		double[][] R = matrixR(corr);
		String sf;
		if (corr) {
			System.out.println("Корреляционная матрица");
			sf = " %+1.2f";
		} else {
			System.out.println("Корреляционная матрица");
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
