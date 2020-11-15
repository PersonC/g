package pnl;

public class ZtA extends Zt {

	public ZtA(int n, int m, int f, int typeABC) {
		super(n, m, f, typeABC);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void set_zero_step(Xy xy) {
		int f1 = 0;
		for (int j=0; j<m; j++) {
			if (f0 < f) {
				double cr = 0, s = 0, zz = 0;
				a[j][f0] = xy.ym / xy.xm[j];
				for (int i1=0; i1<n; i1++) {
					int i = xy.id.ia[i1];
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
				a_temp = xy.ym / xy.xm[j];
				for (int i1=0; i1<n; i1++) { 
					int i = xy.id.ia[i1];
                    s = xy.y[i]- a_temp*xy.x[j][i]; 
                    cr += s*s; 
                }
				if (cr < r[ir[0]]) {
					f1 = ir[0]; for (int l=0; l<m; l++) { a[l][f1] = 0; }
					a[j][f1] = a_temp; r[f1] = cr;
	  				for (int i1=0; i1<n; i1++) { 
						int i = xy.id.ia[i1];
	  					z[f1][i1] = a[j][f1]*xy.x[j][i]; }
	  				sort_r();
				}
			}
		}
		calc();
		f_zx(xy);
	}
	@Override
	public void f_zx(Xy xy) {
		for (int j=0; j < f0; j++) {
			yz[j] = 0.0;
			for (int i1=0; i1<n; i1++) {
				yz[j] += xy.y[ xy.id.ia[i1] ] * z[j][i1];
			}
		}
	}

	
	

}
