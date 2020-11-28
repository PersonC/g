package pnl;

public class ZtA extends Zt {

	public ZtA(int f, int typeABC, Xy x0) {
		super(f, typeABC, x0);
	}

	@Override
	public void set_zero_step() {
		int f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0 < f) {
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = xy.ym[typeABC] / xy.xm[j][typeABC];
				
				for (int i=0; i<n; i++) {
					if (xy.im[i][typeABC] == 1) {
						zz = a[j][f0]*xy.x[j][i];
						z[f0][i] =  zz;
						s = xy.y[i] - zz;
						cr += s*s;
					}	
				}
				r[f0] = cr;
				ir[f0] = f0;
				f0 += 1;
				if (f0 == f) sort_r();
			} else {
				double a_temp = 0;
				double cr = 0, s = 0;
				a_temp = xy.ym[typeABC] / xy.xm[j][typeABC];
				for (int i=0; i<n; i++) {
					if (xy.im[i][typeABC] == 1) {
	                    s = xy.y[i]- a_temp*xy.x[j][i]; 
	                    cr += s*s;
					}    
                }
				if (cr < r[ir[0]]) {
					f1 = ir[0]; for (int l=0; l<m; l++) { a[l][f1] = 0; }
					a[j][f1] = a_temp; r[f1] = cr;
	  				for (int i=0; i<n; i++) { 
						if (xy.im[i][typeABC] == 1) {
							z[f1][i] = a[j][f1]*xy.x[j][i];
						}
	  				}	
	  				sort_r();
				}
			}
		}
		calc();
		f_zx();
	}
}
