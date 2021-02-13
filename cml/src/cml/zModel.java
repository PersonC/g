package cml;

public class zModel implements IF_Criterion {
	public MathVector   y;
	public MathVector[] z;
	public MathVector   yb;
	public MathVector[] zb;
	public int m,f;
	public String typeCR;
	public int m1,mxz;
	public double a [][]; // coefficient [m1][mxz]
	public double cr[];   // criterion [f]
	public double r [];   // criterion [m+1+f]
	public int L = 0;
	public int indexCRmin = 0;
	public double valueCRmin = 1e30;
	
	private double[]  b1, b2;
	private int[]     ii, jj;
	private boolean[] newm;
	
	public zModel(int m, int f, String typeCR) {
		this.typeCR = typeCR;
		this.m   = m;
		this.f   = Math.max(1,Math.min(f,m));
		this.mxz = m + 1 + f;
		this.m1  = m + 1;
		this.z   = new MathVector[mxz];
		this.a   = new double[m1][mxz];
		this.cr  = new double[f];
		this.r   = new double[mxz];
		for( int i = 0; i < f;   f++) { cr[i]=1E30; }
		for( int i = 0; i < mxz; i++) {  r[i]=1E30; }
		this.b1   = new double[f];
		this.b2   = new double[f];
		this.ii   = new int[f];
		this.jj   = new int[f];
		this.newm = new boolean[f];
		for (int i = 0; i < f; i++) { newm[i]=false; }
	}
	
	public void sety  (MathVector y)        { this.y = y;    }
	public void setxi (MathVector x, int i) { this.z[i] = x; }
	public void setyb (MathVector y)        { this.yb = y;    }
	public void setxbi(MathVector x, int i) { this.zb[i] = x; }
	
	public void constructz() {
		for (int i = 0; i < m1; i++) {
			if (i<f) {
				b1[i] = z[i].vAverage / y.vAverage;
				b2[i] = 0.0;
				ii[i] = i;
				jj[i] = i;
				newm[i] = true;
				// посчитать критерий; i - номер модели
				switch(typeCR) {
				case("M"):
					cr[i] = detCR(y,z[i],b1[i]);
				    break;
				case("R"):
					cr[i] = detCR(yb,zb[i],b1[i]);
				    break;
				default:
					cr[i] = 2E30;
					break;
				}
				if (cr[i] < valueCRmin) {
					valueCRmin = cr[i];
					indexCRmin = i;
				}
 			} else {
				double c1 = z[i].vAverage / y.vAverage;
				double temp_cr;
				switch(typeCR) {
				case("M"):
					temp_cr = detCR(y,z[i],c1);
				    break;
				case("R"):
					cr[i] = detCR(yb,zb[i],c1);
				    break;
				default:
					temp_cr = 2E30;
					break;
				}
				
				if (temp_cr < valueCRmin) insertCR(i,i,c1,0,temp_cr);
			}
		}
	}
	
	public void insertCR(int ii_, int jj_, double c1, double c2, double cr_) {
		b1  [indexCRmin] = c1;
		b2  [indexCRmin] = c2;
		ii  [indexCRmin] = ii_;
		jj  [indexCRmin] = jj_;
		cr  [indexCRmin] = cr_;
		newm[indexCRmin] = true;
		// поиск минимума
		double c_ = cr[0];
		int    i_ = 0;
		for ( int i = 1; i < f; i++) {
			if (cr[i] < c_) {
				c_ = cr[i];
				i_ = i;
			}
		}
		// запись минимума
		valueCRmin = c_;
		indexCRmin = i_;
	}
	
	public void generation() {
		while(newgen()) { 
			L++;
			redefine();
		}
	}
	
	public void redefine() {}
	
	public boolean newgen() {
		boolean res = false;
		for (int ii = 0; ii < mxz; ii++) {
			for (int jj = ii+1; jj < mxz; jj++) {
				if(model2(ii,jj)) {
					res = res || placeCR();
				}
			}
		}
		return res;
	}
	
	public boolean model2(int ii, int jj) {
		return false;
	}
	
	public boolean placeCR() {
		return false;
	}

}
