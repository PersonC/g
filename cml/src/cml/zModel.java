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
	
//= affine transformation==================================================
	public void setScale() {
		y.makeScale();
		if (yd != null) yd.makeScale();
		if (yc != null) yc.makeScale();
	}
	public void setScale(int vi) throws Exception {
		if (vi < 0 || vi > m1) throw new Exception("Index x[i] is wrong: " + vi + "not in [1," + m1); 
		z[vi].makeScale();
		if (zd[vi] != null) zd[vi].makeScale();
		if (zc[vi] != null) zc[vi].makeScale();
	}
	
	public void revertScale() {
		double s0 = y.getScale(), d0 = y.getParallax();
		double s[] = new double[m1], d[] = new double[m1];
		for (int i=1; i<m1; i++) {
			s[i] = z[i].getScale(); d[i] = z[i].getParallax();
		}
		for (int k=0; k<mxz; k++) {
			double a0 = a[0][k];
			for (int j=1; j<m1; j++) {
				a0 -= a[j][k] * d[j];
			}
			a[0][k] = s0 * (a0 + d0);
			for (int j=1; j<m1; j++) {
				a[j][k] = a[j][k] * s0 / s[j];
			}
		}
	}
	
	public void normaYX() {
		y.setScale(y.norma);
	}
	
//===========================================================	
	
	public void set_coef0() {
		for (int k=0; k<f; k++) {
			int     j = ij_z[0][k];
			double az = aij [0][k];
			int    kz = m1 + k;
			a[j][kz] = az;
			z[kz]    = new MathVector(y.n,  kz);
			z[kz].runZOne(az, z [j]);      // c ����������
			if ( crit != GMDH.LSM ) {
				zd[kz] = new MathVector(yd.n, kz);
				zd[kz].runZOne(az, zd[j]); // c ����������
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

	public int insertCR(double a1, int i, double crnew) {
		if (crnew < valCRmax) {
			cr     [iCRmax] = crnew;
			aij [0][iCRmax] = a1;
			ij_z[0][iCRmax] = i; 
			aij [1][iCRmax] = 0;
			ij_z[1][iCRmax] = -2; 
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
			// ��������� �� ��������� ���������� ���� ����� �������
			lsm[0] = roundHalf(lsm[0],CG.round_a2);
			lsm[1] = roundHalf(lsm[1],CG.round_a2);
		}
		if (CG.isRoundLastCoef2) {
			// ��������� �������� ���������� ���� �����
			lsm[0] = roundLast(lsm[0],CG.r_a2);
			lsm[1] = roundLast(lsm[1],CG.r_a2);
		}
		return lsm;
	}

	public boolean model2(int ii, int jj) {
		double[] b = new double[2];
		double[] c = new double[2];

		switch (crit) {
			case BIAS_REG:
			case REG_AB:	
			case BIASCOEF:
			case BIAS: 
				b = coef(yd,zd[ii],zd[jj],Lcurrent);
				if (b[0] == 0 && b[1] == 0) return false;
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
	public void generator(int Lmax) {
		gen0();
		do {} while (genPopulation()>0 && Lcurrent < Lmax);
	}

	public void gen0() {
		Lcurrent++;
		for (int j1 = 0; j1 < m1; j1++) {
			if (model2(j1, j1)) insertCR(a1, j1, CRvalue); 
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
		// �������� ����������� ���������
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
						if (CG.isRoundCoefZ) anew = roundHalf(anew, CG.round_z);
						if (CG.isRoundLastCoefZ) anew = roundLast(anew, CG.r_z);					
						a[j][kz] = anew; // recalc z, zd [kz]
						z [kz].addV(anew, z [j], j);
						zd[kz].addV(anew, zd[j], j);
					}
					try {z[kz].valuation();	zd[kz].valuation();} catch (Exception e) {
						System.out.println(e.toString());
					};
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
						if (CG.isRoundCoefZ) anew = roundHalf(anew, CG.round_z);
						if (CG.isRoundLastCoefZ) anew = roundHalf(anew, CG.r_z);
						z [kz].addV(anew, z [j], j);  // recalc z, zd [kz]
					}
					try {
					z[kz].valuation(); }catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
	}
//----------------------------------------------------------------
	public void bestModel() {
		System.out.println("\nModel " + nameModel + " �������� " + crit + ": " + 
                GMDH.getModel(crit.ordinal()) + "\n������ ������: ");
		double c = cr[0];
		int ll = 0;
		for (int m=1; m < f; m++) {
			if (cr[m] < c) { c = cr[m]; ll=m; }
		}
		System.out.println("������ " + ll + " � ������ " + L[ll] + ": �������� " + cr[ll]);
		for (int j=0; j < m1; j++) {
			System.out.print("a[" + j + "]=" + a[j][ll+m1] + " ");
		}
		System.out.println("\n");
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
		try {
		y.valuation(); yd.valuation(); yc.valuation(); } catch (Exception e) {
			// TODO: handle exception
		}
		for (int j=1; j<m1; j++) {
			try {
			z[j].valuation(); zd[j].valuation(); zc[j].valuation(); }catch (Exception e) {
				// TODO: handle exception
			}
		}
		return in.nameFile + " " + isfr;
	}
	
	public void accuracyC() {
		if (yc == null) {
			System.out.println("����������� ��������������� �������");
			return;
		}
	}
	
	public static class CG {
		static final double EPS = 1e-20;
		// round method 2
				static boolean isRoundLastCoef2 = false;
				static boolean isRoundLastCoefZ = false;
				static int r_a2 = 8;             // drop last digits
				static int r_z  = 2;             // drop last digits
// round coefficient
		static boolean isRoundCoef2 = true && !isRoundLastCoef2;
		static boolean isRoundCoefZ = false && !isRoundLastCoefZ;
		static int round_a2 = 1;             // scale a2
		static int round_z  = 8;             // scale z
		
		static double muy = 0.5; // for BIAS_REG
	}
//==========================================================	
	public void printMatrix() {
		printMatrixR(true); // �������������� �������
		printMatrixR(false); // �������������� �������
		printMatrixU(); // ���� ����� ���������
	}
	
	public void printMatrixR(boolean corr) {
		double[][] R = matrixR(corr);
		String sf;
		if (corr) {
			System.out.println("\n�������������� �������");
			sf = " %+1.2f";
		} else {
			System.out.println("\n�������������� �������");
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
		System.out.println("\n���� ����� ���������");
		for ( int i=0; i < m1; i++) {
			for ( int j=0; j < m1; j++) {
				if(i<j) System.out.printf(sf,U[i][j]); 
				else    System.out.printf(sf,U[j][i]);
			}
			System.out.println("\n");
		}
	}

	public void printZ() {
		System.out.println("\n��������� ��������� �������");
		if (y != null) y.printVector();
		else System.out.println("����������� y");
		for (int i=0; i < mxz; i++) {
			if (z[i] != null) z[i].printVector();
			else System.out.println("����������� ������ " + i);
		}
		if (crit != GMDH.LSM) {
			System.out.println("��������� ����������� �������");
			if (yd != null) yd.printVector();
			else System.out.println("����������� y");
			for (int i=0; i < mxz; i++) {
				if (zd[i] != null) zd[i].printVector();
				else System.out.println("����������� ������ " + i);
			}
			System.out.println("��������� ��������������� �������");
			if (yc != null) yc.printVector();
			else System.out.println("����������� y");
			for (int i=0; i < m1; i++) {
				if (zc[i] != null) zc[i].printVector();
				else System.out.println("����������� ������ " + i);
			}
		}
	}
	
	public void printModel() {
		System.out.println("\n�������� " + Lcurrent + " max �R[" + iCRmax + "]=" + valCRmax);
		for (int l=m1; l<mxz; l++) {
			System.out.println("������ " + (l-m1) + " � ������ " + L[l-m1] + ": �������� " + cr[l-m1]);
			if (yc != null) zc[l] = new MathVector(yc.n, l);
			for (int j=0; j<m1; j++) {
				System.out.print("a[" + j + "]=" + a[j][l] + " ");
				if (yc != null) zc[l].addV(a[j][l], zc[j], j);
			}
			if (yc != null) {
				try {
				zc[l].valuation(); } catch (Exception e) {
					// TODO: handle exception
				}
				double CRC = Math.sqrt(detCR(yc,zc[l],1))/yc.norma;
				System.out.println("�������� �� ��������������� �������: " + CRC);
			}
			System.out.print("������ ���������� �� ");
			System.out.println("z{"+ij_z[0][l-m1] + "}=" + aij[0][l-m1]+" � " +
					"z{"+ij_z[1][l-m1] + "}= " + aij[1][l-m1] + "\n");
		}
	}

}
