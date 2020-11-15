package pnl;

public class ZtA extends Zt {
	public int[] abc;

	public ZtA(int n, int m, int f, int typeABC, Xy x0) {
		super(n, m, f, typeABC, x0);
		// TODO Auto-generated constructor stub
		switch(typeABC) {
		case 1: abc = x0.id.ia; break;
		case 2: abc = x0.id.ib; break;
		}
	}

	@Override
	public void set_zero_step() {
		int f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0 < f) {
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = xy.ym[0] / xy.xm[j][0];
				for (int i1=0; i1<n; i1++) {
					int i = abc[i1];
					zz = a[j][f0]*xy.x[j][i];
					z[f0][i1] =  zz;
					s = xy.y[i] - zz;
					cr += s*s;
				}
				r[f0] = cr;
				ir[f0] = f0;
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				double a_temp = 0;
				double cr = 0, s = 0;
				a_temp = xy.ym[0] / xy.xm[j][0];
				for (int i1=0; i1<n; i1++) { 
					int i = abc[i1];
                    s = xy.y[i]- a_temp*xy.x[j][i]; 
                    cr += s*s; 
                }
				if (cr < r[ir[0]]) {
					f1 = ir[0]; for (int l=0; l<m; l++) { a[l][f1] = 0; }
					a[j][f1] = a_temp; r[f1] = cr;
	  				for (int i1=0; i1<n; i1++) { 
						int i = abc[i1];
	  					z[f1][i1] = a[j][f1]*xy.x[j][i]; }
	  				sort_r();
				}
			}
		}
		calc();
		f_zx();
	}
	@Override
	public void f_zx() {
		for (int j=0; j < f0; j++) {
			yz[j] = 0.0;
			for (int i1=0; i1<n; i1++) {
				yz[j] += xy.y[ abc[i1] ] * z[j][i1];
			}
		}
	}
	@Override
	//================
	public boolean model(int j, int k) { // расчет модели
		double sxz = 0, D = 0, ax, az;
		for(int i=0; i<n; i++) { sxz += xy.x[j][i] * z[k][i]; }
		D = xy.sx2[j][0] * sz2[k] - sxz * sxz;
		if (Math.abs(D) < EPS) return(false);
		ax = (xy.yx [j][0] * sz2[k] - sxz * yz   [k])    / D;
		az = (xy.sx2[j][0] * yz [k] - sxz * xy.yx[j][0]) / D;
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

	
}
