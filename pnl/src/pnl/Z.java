package pnl;

public class Z {
	private static final double EPS = 1e-15;
	public int n,m,f;
	public double[]   zm, zmin, zmax, sz2, yz;
	public double[][] z;
	public double[][] a; // [m][f]
	public double[]   r; // [f]
	public int[]      ir; // [f] - индексы r по возрастанию
    private int f0 = 0, f2 = 0;
//
	double[] crm; // [f]
	double[] a1;
	double[] a2;
	int[]    jx, kz;
//    
	
	public Z(int n, int m, int f) {
		this.n = n;
		this.m = m;
		if (f<0) f = m;
		this.f = f;
		this.zm   = new double[f];
		this.zmin = new double[f];
		this.zmax = new double[f];
		this.sz2  = new double[f];
		this.yz   = new double[f];
		this.z    = new double[f][n];
		this.a    = new double[m][f];
		this.r    = new double[f];
		this.ir   = new int[f];
		this.crm  = new double[f];
		this.a1   = new double[f];
		this.a2   = new double[f];
		this.jx   = new int[f];
		this.kz   = new int[f];
	}
	
	public boolean set_next_step(Xy xy) {
		boolean yes = false;
		f2 = 0;
		for( int j=0; j<m; j++) {
			for( int k=0; k<f0; k++) {
				if (model(xy, j, k)) yes = true;
			}
		}
		if (yes) build(xy);
		return yes;
	}
	
	public void build(Xy xy) {
		double[][] b = new double[m][f2];
		for (int j=0; j<m; j++) {
			for(int k=0; k<f2; k++) {
				b[j][k] = a[j][kz[k]];
			}
		}
		for(int k=0; k<f2; k++) {
			if(f0<f) {
				int l = f0; // куда пишем
				for(int j=0; j<m; j++) { // [m][f]
					a[j][l] = a2[k]*b[j][k];
				}
				a[jx[k]][l] += a1[k];
				r[f0] = crm[k];
				ir[f0] = f0;
				fillz(f0,xy);
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				if(crm[k]<r[ir[0]]) {
					int l = ir[0]; // куда пишем
					for(int j=0; j<m; j++) { // [m][f]
						a[j][l] = a2[k]*b[j][k];
					}
					a[jx[k]][l] += a1[k];
					r[l] = crm[k];
					ir[0] = l;
					fillz(l,xy);
					sort_r();
				}
			} // end if
		} // next k
	}
	
	public void fillz(int f, Xy xy) {
		for (int i=0; i<n; i++) {
			z[f][i] = 0; for(int j=0; j<m; j++) { z[f][i] += a[j][f]*xy.x[j][i]; }
		}
		zm_j(f); sum_square_j(f); maxmin(f);
	}
	
	public boolean model(Xy xy, int j, int k) { // расчет модели
		double sxz = 0, D = 0, ax, az;
		for(int i=0; i<n; i++) { sxz += xy.x[j][i] * z[k][i]; }
		D = xy.sx2[j] * sz2[k] - sxz * sxz;
		if (Math.abs(D) < EPS) return(false);
		ax = (xy.yx [j] * sz2[k] - sxz*yz  [k])/D;
		az = (xy.sx2[j] * yz [k] - sxz*xy.yx[j])/D;
		double cr = 0, dd;
		for (int i=0; i<n; i++) {
			dd = xy.y[i] - ax * xy.x[j][i] - az * z[k][i]; cr += dd * dd;
		}
		if (cr >= r[ir[0]]) return(false);
// записываем модель		
		if (f2 < f) {
			crm[f2] = cr; a1[f2] = ax; a2[f2] = az; jx[f2] = j; kz[f2] = k; f2 += 1;
		} else {
			double crmax = crm[0];
			int fmax = 0;
			for (int l=0; l<f; l++) {
				if (crmax<crm[l]) {	crmax = crm[l]; fmax = l; }
			}
			crm[fmax] = cr; a1[fmax] = ax; a2[fmax] = az; jx[fmax] = j; kz[fmax] = k;
		}
		
		return(true);
	}
	
	public void set_zero_step(Xy xy) {
		int f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0<f) {
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = xy.ym / xy.xm[j];
				for (int i=0; i<n; i++) {
					zz = a[j][f0]*xy.x[j][i]; z[f0][i] =  zz;
					s = xy.y[i]- zz;          cr += s*s;
				}
				r[f0] = cr;
				ir[f0] = f0;
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				double a_temp = 0;
				double cr = 0, s = 0;
				a_temp = xy.ym / xy.xm[j];
				for (int i=0; i<n; i++) { s = xy.y[i]- a_temp*xy.x[j][i]; cr += s*s; }
				if (cr < r[ir[0]]) {
					f1 = ir[0]; for (int l=0; l<m; l++) { a[l][f1] = 0; }
					a[j][f1] = a_temp; r[f1] = cr;
	  				for (int i=0; i<n; i++) { z[f1][i]=a[j][f1]*xy.x[j][i]; }
	  				sort_r();
				}
			}
		}
		calc();
		f_zx(xy);
	}
	
	public void sort_r() {
		int i0, i1;
		boolean w;
		do {
			w = false;
			for(int fi=0; fi<f0-1;fi++) {
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
		for(int j=0; j<f0; j++) {
			zm_j(j);
			sum_square_j(j);
			maxmin(j);
		}
	}

	public void f_zx(Xy xy) {
		for (int j=0; j<f0; j++) {
			yz[j] = 0.0;
			for (int i=0; i<n; i++) {
				yz[j] += xy.y[i] * z[j][i];
			}
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
			   ", zy=" + yz[k] +
			   ", cr=" + r[k] + 
			   "\n" + s; 
	}
	public void toPrint(String s) {
		System.out.println(s);
		for (int k=0; k<f0; k++) System.out.println(toPrint(ir[k]));
	}
	
	public void toPrintCr() {
		String s = f2 + " CR:\n";
		for (int k=0; k<f2; k++) {
			s += crm[k] + " " + jx[k] + ": " + a1[k] + " " + kz[k] + ": " + a2[k] + "\n";
		} 
		System.out.println(s);
	}
}
