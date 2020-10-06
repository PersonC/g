package pnl;

public class Z {
	public int n,m,f;
	public double[] zm, zmin, zmax, sz2, yz;
	public double[][] z;
	public double[][] a; // [m][f]
	public double[] r; // [f]
	public int[]    ir; // [f] - индексы r по возрастанию
//	private int f_current;
	
	public Z(int n, int m, int f) {
		this.n = n;
		this.m = m;
		if (f<0 || f>m) f = m;
		this.f = f;
		this.zm = new double[f];
		this.zmin = new double[f];
		this.zmax = new double[f];
		this.sz2 = new double[f];
		this.yz = new double[f];
		this.z = new double[f][n];
		this.a = new double[m][f];
		this.r = new double[f];
		this.ir = new int[f];
	}
	
	public void set_zero_step(Y y, X x) {
		int f0 = 0, f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0<f) {
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = y.ym / x.xm[j];
				for (int i=0; i<n; i++) {
					zz = a[j][f0]*x.x[j][i];
					z[f0][i] =  zz;
					s = y.y[i]- zz;
					cr += s*s;
				}
				r[f0] = cr;
				ir[f0] = f0;
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				double a_temp = 0;
				double cr = 0, s = 0;
				a_temp = y.ym / x.xm[j];
				for (int i=0; i<n; i++) {
					s = y.y[i]- a_temp*x.x[j][i];
					cr += s*s;
				}
				if (cr < r[ir[0]]) {
					f1 = ir[0];
					for (int l=0; l<m; l++) {a[l][f1] = 0; }
					a[j][f1] = a_temp;
					r[f1] = cr;
	  				for (int i=0; i<n; i++) {
						z[f1][i]=a[j][f1]*x.x[j][i];
					}
	  				sort_r();
				}
			}
		}
		calc();
	}
	
	public void sort_r() {
		int i0, i1;
		boolean w;
		do {
			w = false;
			for(int fi=0; fi<f-1;fi++) {
				i0 = ir[fi]; i1 = ir[fi+1];
				if (r[i0]<r[i1]) {
					ir[fi] = i1; ir[fi+1] = i0;
					w = true;
				}
			}
		} while(w);
	}
	
	public void zm_j( int j ) {
		double s =0.0;
		for (int i=0; i<n; i++) { s = s + z[j][i]; }	
		zm[j] = s/(double) n;
	}
	
	public void sum_square_j( int j ) {
		double s = 0;
		for(int i=0;i<n;i++) { s += z[j][i] * z[j][i]; }
		this.sz2[j] = s;
	}

	public void maxmin ( int j) {
		zmin[j]=z[j][0]; zmax[j]=z[j][0];
		for(int i=1;i<n;i++) {
			if (z[j][i]<zmin[j]) zmin[j]=z[j][i];
			if (z[j][i]>zmax[j]) zmax[j]=z[j][i];
		}
	}
	
	public void calc() {
		for(int j=0; j<f; j++) {
			zm_j(j);
			sum_square_j(j);
			maxmin(j);
		}
	}
	
	public String toPrint(int k) {
		String s = "a:";
		for (int j=0; j<m; j++) {
			s = s + "   " + a[j][k];
		}
		return "f="+k + 
			   ", zmin=" + zmin[k] + 
			   ", zmax=" + zmax[k] +
			   ", zm=" + zm[k] +
			   ", sum(z**2)=" + sz2[k] +
			   ", cr=" + r[k] + 
			   "\n" + s; 
	}
	public void toPrint(String s) {
		System.out.println(s);
		for (int k=0; k<f; k++) System.out.println(toPrint(ir[k]));
	}

}
