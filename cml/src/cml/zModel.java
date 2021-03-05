package cml;

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
	public double valCRmax = 1E30;
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
		for( int i = 0; i < f; i++) { cr[i]=1E30; L[i]=-1; }
		this.ij_z = new int[2][f];
		this.aij  =  new double[2][f];
	}
	
	public void sety  (MathVector y, boolean prime) { if(prime) this.y = y; else this.yd = y; }
	public void sety  (MathVector y) { this.yc = y; }

	public void setxi (MathVector x, int i, boolean prime) { if(prime) this.z[i] = x; else this.zd[i] = x; }
	public void setxi (MathVector x, int i) { this.zc[i] = x; }
	
//===========================================================
	public void init() {
		for (int j=0; j<m1; j++) {
			double a1 = coef1(y, z[j]);
			double CRB1 = detCR(yd,zd[j], a1);
			double a2;
			switch(crit) {
			case BIASCOEF:
				a2 = coef1(yd, zd[j]);
				CRB1 = Math.abs(a1-a2);
				break;
			case BIASREG:
				a2 = coef1(yd, zd[j]);
				double CRB2 = detCR(y,z[j], a2);
				CRB1 = Math.abs(CRB1 / (double) yd.n - CRB2 / (double) y.n);
				break;
			case REG:
				break;
			case LSM:
				CRB1 = detCR(y,z[j],a1);
			default:
				break;
			}
			insertCR(a1, j, CRB1);
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

			MathVector qz  = new MathVector(n,  kz);
			qz. runZOne(az, z [j]);
			setxi(qz, kz, true);
			
			MathVector qzd = new MathVector(nd, kz);
			qzd.runZOne(az, zd[j]);
			setxi(qzd, kz, false);
			
		}
	}

	public void insertCR(double a1, int j, double crnew) {
		int fcur = iCRmax; 
		cr     [fcur] = crnew;
		ij_z[0][fcur] = j; 
		aij [0][fcur] = a1;
		
		L      [fcur] = 0;
		valCRmax      = crnew;
		detMinCr1();
	}
	
	public void insertCR(double a1, int i, double a2, int j, double crnew) {
		int fcur = iCRmax; 
		cr     [fcur] = crnew;
		aij [0][fcur] = a1;
		ij_z[0][fcur] = i; 
		aij [1][fcur] = a2;
		ij_z[1][fcur] = j; 
		L      [fcur] = Lcurrent;
		valCRmax      = crnew;
		// определение нового значения для записи критерия
		detMinCr1();
		
	}
	
	public void detMinCr1() {
		double maxCR = 1e-30;
		for (int k = 0; k < f; k++) {
			if (cr[k] > maxCR) {
				iCRmax = k;
				valCRmax = cr[k];
				maxCR = cr[k];
			}
		}
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
		for (int j1 = 0; j1 < mxz-1; j1++) {
			for( int j2 = j1+1; j2 < mxz; j2++) {
				double[] c = model2(j1, j2);
				if (c[3] < valCRmax) {
					insertCR(c[0], j1, c[1], j2, c[3]);
				}
			}
		}
		boolean gen = false;
		for (int i=0; i<f; i++) {
			if (L[i] == Lcurrent) {
				gen = true;
				break;
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
			}
		}
	}
//----------------------------------------------------------------
	public void printModel(boolean pZ) {
		
		System.out.println("\nModel " + nameModel + " Критерий " + crit);
		if (pZ) {
			System.out.println("Параметры обучающей выборки");
			y.printVector();
			for (int i=0; i<mxz; i++) {
				z[i].printVector();
			}
			System.out.println("Параметры проверяющей выборки");
			yd.printVector();
			for (int i=0; i<mxz; i++) {
				zd[i].printVector();
			}
		} else {
			System.out.println("\nИтерация " + Lcurrent + " max СR[" + iCRmax + "]=" + valCRmax);
			for (int l=m1; l<mxz; l++) {
				System.out.println("Модель " + (l-m1) + " с уровня " + L[l-m1] + ": критерий " + cr[l-m1]);
				for (int j=0; j<m1; j++) {
					System.out.print("a[" + j + "]=" + a[j][l] + " ");
				}
				System.out.print("\n");
			}
		}
	}
	
	public double[][] matrixR(boolean corr) {
		double[][] R = new double[m1][m1];
		for (int i=1; i<m1; i++) {
			for (int j=i; j<m1; j++) {
				if (corr) R[i][j] = corr(z[i], z[j]); else R[i][j] = covar(z[i], z[j]);
			}
		}
		return R;
	}
	
	public void printMatrixR(boolean corr) {
		double[][] R = matrixR(corr);
		System.out.println( ((corr) ?"Корреляционная " : "Ковариационная") + "матрица");
		for ( int i=1; i<m1; i++) {
			for ( int j=1; j<m1; j++) {
				if(i<j) System.out.print(R[j][i] + " "); else System.out.print(R[i][j] + " ");
			}
			System.out.println("\n");
		}
	}
//=========================================================================
	public void utilityTest(int n) {
		MathVector y = new MathVector(n,-1),
				   e = new MathVector(n, 0),
				   x1 = new MathVector(n, 1),
				   x2 = new MathVector(n, 2);
		y.test(1, 1.1);     // random * 1.1
		e.test(0, 1);       // const = 1 
		x1.test(2, 1.2);    // 1.2 * i
		x2.test(1,2.0);
		sety(y,true);
		setxi(e,0,true);
		setxi(x1,1,true);
		setxi(x2,2,true);
	}
	
	public void utilityTest(int n, int nd) {
		utilityTest(n);
		MathVector yd = new MathVector(nd,-1),
				   ed = new MathVector(nd, 0),
				   x1d = new MathVector(nd, 1),
				   x2d = new MathVector(nd, 2);
		yd. test(1, 1.2);     // random * 1.2
		ed. test(0, 1);       //  
		x1d.test(2, 1.3);    // 1.3 * i
		x2d.test(2, 2.3);
		sety(yd,false);
		setxi(ed,0,false);
		setxi(x1d,1,false);
		setxi(x2d,2,false);
	}
	
	public void utilityTest(int n, int nd, int nc) {
		utilityTest(n,nd);
		MathVector y = new MathVector(nc,-1),
				   e = new MathVector(nc, 0),
				   x1 = new MathVector(nc, 1),
				   x2 = new MathVector(nc, 2);
		y. test(1, 1.1);     // random * 1.1
		e. test(0, 1);       // const = 1 
		x1.test(2, 1.2);    // 1.2 * i
		x2.test(1,2.0);
		sety(y);
		setxi(e,0);
		setxi(x1,1);
		setxi(x2,2);
	}

}
