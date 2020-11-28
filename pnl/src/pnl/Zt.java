package pnl;

public class Zt implements PrepareXYZ {
	public int n,m,f;
	public int typeABC;
	public Xy xy;
	//
	public double[][]   zm, zmin, zmax, sz2, yz;
	public double[][] z;
	public double[][] a; // [m][f]
	public double[]   r; // [f]
	public int[]      ir; // [f] - индексы r по возрастанию
    public int f0 = 0, f2 = 0;
//
	double[] crm; // [f]
	double[] a1;
	double[] a2;
	int[]    jx, kz;
	
	public Zt(int f, int typeABC, Xy x0) {
		this.n = x0.n;
		this.m = x0.m;
		this.f = f;
		this.xy = x0;
		this.typeABC = typeABC;
		this.zm   = new double[f][4];
		this.zmin = new double[f][4];
		this.zmax = new double[f][4];
		this.sz2  = new double[f][4];
		this.yz   = new double[f][4];
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
	//==========
	public void set_zero_step() {
		int f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0 < f) {
				a[j][f0] = xy.ym[0] / xy.xm[j][0];
				for (int i=0; i<n; i++) { z[f0][i] =  a[j][f0] * xy.x[j][i]; }
				r[f0] = f_cr(a[j][f0],j); // here method
				ir[f0] = f0;
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				double a_temp = 0;
				double cr = 0;
				a_temp = xy.ym[0] / xy.xm[j][0];
				cr = f_cr(a_temp,j);// here method
				if (cr < r[ir[0]]) {
					f1 = ir[0]; for (int l=0; l<m; l++) { a[l][f1] = 0; }
					a[j][f1] = a_temp; r[f1] = cr;
	  				for (int i=0; i<n; i++) { z[f1][i] = a[j][f1] * xy.x[j][i]; }
	  				sort_r();
				}
			}
		}
		calc();
		f_zx();
	}
	//============
	public double f_cr(double a, int j) {
		double cr = 0;
		double s;
		for (int i=0; i<n; i++) {
			s = xy.y[i] - a * xy.x[j][i];
			cr += s*s;
		}
		return (cr);
	}
    //=========
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
	//========
	public void zm_j( int j ) {
		zm[j][0] = xmForj(z, j, n, xy.im, 0);
		zm[j][1] = xmForj(z, j, n, xy.im, 1);
		zm[j][2] = xmForj(z, j, n, xy.im, 2);
		zm[j][3] = xmForj(z, j, n, xy.im, 3);
	}
	
	public void sum_square_j( int j ) {
		sz2[j][0] = sumSquareForj(z, j, n, xy.im, 0); 
		sz2[j][1] = sumSquareForj(z, j, n, xy.im, 1); 
		sz2[j][2] = sumSquareForj(z, j, n, xy.im, 2); 
		sz2[j][3] = sumSquareForj(z, j, n, xy.im, 3); 
	}

	public void maxmin ( int j) {
		zmin[j][0] = minForj(z, j, n, xy.im, 0); 
		zmin[j][1] = minForj(z, j, n, xy.im, 1); 
		zmin[j][2] = minForj(z, j, n, xy.im, 2); 
		zmin[j][3] = minForj(z, j, n, xy.im, 3); 

		zmax[j][0] = maxForj(z, j, n, xy.im, 0); 
		zmax[j][1] = maxForj(z, j, n, xy.im, 1); 
		zmax[j][2] = maxForj(z, j, n, xy.im, 2); 
		zmax[j][3] = maxForj(z, j, n, xy.im, 3); 
	}
	
	public void calc() {
		for(int j=0; j<f0; j++) {
			zm_j(j);
			sum_square_j(j);
			maxmin(j);
		}
	}

	public void f_zx() {
		for (int j=0; j<f0; j++) {
			yz[j][0] = xyForj(xy.y, z, j, n, xy.im, 0);
			yz[j][1] = xyForj(xy.y, z, j, n, xy.im, 1);
			yz[j][2] = xyForj(xy.y, z, j, n, xy.im, 2);
			yz[j][3] = xyForj(xy.y, z, j, n, xy.im, 3);
		}
	}

	//=========
	public void toPrint(String s) {
		System.out.println(s);
		for (int k=0; k<f0; k++) System.out.println(toPrint(ir[k]));
	}
	public String toPrint(int k) {
		String s = "a:";
		for (int j=0; j<m; j++) {
			s = s + "   " + a[j][k];
		}
		return "f="+k + 
			   ", zmin=" + zmin[k][typeABC] + 
			   ", zmax=" + zmax[k][typeABC] +
			   ", zm=" + zm[k][typeABC] +
			   ", sum(z**2)=" + sz2[k][typeABC] +
			   ", zy=" + yz[k][typeABC] +
			   ", cr=" + r[k] + 
			   "\n" + s; 
	}
	public void toPrintCr() {
		String s = "F=" + f2 + " List CR:\n";
		for (int k=0; k<f2; k++) {
			s += "k=" + k + " cr=" + crm[k] + " x[" + jx[k] + "]= " + a1[k] + " z[" + kz[k] + "]= " + a2[k] + "\n";
		} 
		System.out.println(s);
	}
//================
	public boolean model(int j, int k) { // расчет модели
		double sxz = 0, D = 0, ax, az;
		
		for (int i=0; i<n; i++) {
			sxz += (xy.im[i][typeABC] == 1) ? xy.x[j][i] * z[k][i] : 0;
		}

		D = xy.sx2[j][typeABC] * sz2[k][typeABC] - sxz * sxz;
		if (Math.abs(D) < EPS) return(false);
		
		ax = (xy.yx [j][typeABC] * sz2[k][typeABC] - sxz * yz   [k][typeABC]) / D;
		az = (xy.sx2[j][typeABC] * yz [k][typeABC] - sxz * xy.yx[j][typeABC]) / D;
		
		double cr = 0, dd;
		for (int i=0; i<n; i++) {
			if (xy.im[i][typeABC] == 1) {
				dd = xy.y[i] - ax * xy.x[j][i] - az * z[k][i]; 
				cr += dd * dd;
			}
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
	//
	public void build() {
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
				fillz(f0);
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
					fillz(l);
					sort_r();
				}
			} // end if
		} // next k
	}
	//
	public void fillz(int f) {
		for (int i=0; i<n; i++) {
			z[f][i] = 0; for(int j=0; j<m; j++) { z[f][i] += a[j][f]*xy.x[j][i]; }
		}
		zm_j(f); sum_square_j(f); maxmin(f);
	}
}
