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
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = xy.ym[0] / xy.xm[j][0];
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
				a_temp = xy.ym[0] / xy.xm[j][0];
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
		f_zx();
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
		zm[j][0] = xmForj(z, j, n);
		zm[j][1] = xmForj(z, j, xy.id.na, xy.id.ia);
		if( xy.id.nb > 0 ) { zm[j][2] = xmForj(z, j, xy.id.nb, xy.id.ib); }
		if( xy.id.nc > 0 ) { zm[j][3] = xmForj(z, j, xy.id.nc, xy.id.ic); }
	}
	
	public void sum_square_j( int j ) {
		sz2[j][0] = sumSquareForj(z, j, n); 
		sz2[j][1] = sumSquareForj(z, j, xy.id.na, xy.id.ia);		
		if( xy.id.nb > 0 ) { sz2[j][2] = sumSquareForj(z, j, xy.id.nb, xy.id.ib); }
		if( xy.id.nc > 0 ) { sz2[j][3] = sumSquareForj(z, j, xy.id.nc, xy.id.ic); }
	}

	public void maxmin ( int j) {
		zmin[j][0] = minForj(z, j, n); 
		zmin[j][1] = minForj(z, j, xy.id.na, xy.id.ia);		
		if( xy.id.nb > 0 ) { zmin[j][2] = minForj(z, j, xy.id.nb, xy.id.ib); }
		if( xy.id.nc > 0 ) { zmin[j][3] = minForj(z, j, xy.id.nc, xy.id.ic); }

		zmax[j][0] = maxForj(z, j, n); 
		zmax[j][1] = maxForj(z, j, xy.id.na, xy.id.ia);		
		if( xy.id.nb > 0 ) { zmax[j][2] = maxForj(z, j, xy.id.nb, xy.id.ib); }
		if( xy.id.nc > 0 ) { zmax[j][3] = maxForj(z, j, xy.id.nc, xy.id.ic); }
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
			yz[j][0] = xyForj(xy.y, z, j, n);
			yz[j][1] = xyForj(xy.y, z, j, xy.id.na, xy.id.ia);
			if( xy.id.nb > 0 ) { yz[j][2] = xyForj(xy.y, z, j, xy.id.nb, xy.id.ib); }
			if( xy.id.nc > 0 ) { yz[j][3] = xyForj(xy.y, z, j, xy.id.nc, xy.id.ic); }
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
		int i1;
		
		switch (typeABC) {
		case 1:
			for(int i=0; i<xy.id.na; i++) {
				i1 = xy.id.ia[i];
				sxz += xy.x[j][i1] * z[k][i1]; 
			}
			break;
		case 2:
			for(int i=0; i<xy.id.nb; i++) {
				i1 = xy.id.ib[i];
				sxz += xy.x[j][i1] * z[k][i1]; 
			}
			break;
		default:
			for(int i=0; i<n; i++) {
				sxz += xy.x[j][i] * z[k][i]; 
			}
		}

		D = xy.sx2[j][typeABC] * sz2[k][typeABC] - sxz * sxz;
		if (Math.abs(D) < EPS) return(false);
		
		ax = (xy.yx [j][typeABC] * sz2[k][typeABC] - sxz * yz   [k][typeABC]) / D;
		az = (xy.sx2[j][typeABC] * yz [k][typeABC] - sxz * xy.yx[j][typeABC]) / D;
		
		double cr = 0, dd;
		switch(typeABC) {
		case 1:
			for (int i=0; i<xy.id.nb; i++) {
				i1 = xy.id.ib[i];
				dd = xy.y[i1] - ax * xy.x[j][i1] - az * z[k][i1]; cr += dd * dd;
			}
			break;
		case 2:
			for (int i=0; i<xy.id.na; i++) {
				i1 = xy.id.ia[i];
				dd = xy.y[i1] - ax * xy.x[j][i1] - az * z[k][i1]; cr += dd * dd;
			}
			break;
		default:
			for (int i=0; i<n; i++) {
				dd = xy.y[i] - ax * xy.x[j][i] - az * z[k][i]; cr += dd * dd;
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
