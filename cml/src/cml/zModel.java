package cml;

public class zModel implements IF_Criterion {
	public MathVector   y;
	public MathVector[] z;
	public MathVector   yd;
	public MathVector[] zd;
	public int m,f;
	public int m1,mxz;
	public double a [][]; // coefficient [m1][f]
	public double cr[];   // criterion [f]
	public int    L [];
	public int    Lcurrent = 0;
	public double valCRmin = 1E30;
	public int iCRmin   = 0;
	
	public int   ij_z[][]; // [2][f] first factor, second factor
	public double aij[][];
	
	public zModel(int m, int f1) {
		int f = Math.max(1,Math.min(f1,m));
		this.m   = m;
		this.f   = f;
		this.mxz = m + 1 + f;
		this.m1  = m + 1;
		this.z   = new MathVector[mxz];
		this.zd  = new MathVector[mxz];
		this.a   = new double[m1][f];
		this.cr  = new double[f];
		this.L   = new int   [f];
		for( int i = 0; i < f; i++) { cr[i]=1E30; L[i]=-1; }
//		System.out.println(f);
		this.ij_z = new int[2][f];
		this.aij =  new double[2][f];
	}
	
	public void sety  (MathVector y, boolean prime) {
	    if (prime) { this.y = y; } else { this.yd = y; }
	}
	public void setxi (MathVector x, int i, boolean prime) {
		if (prime) { this.z[i] = x; } else { this.zd[i] = x; }
	}
	
//=================================	
	public void generation() {
		while(newgen()) { 
			
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
