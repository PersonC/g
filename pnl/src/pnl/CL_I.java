package pnl;

public class CL_I {
	public int n, na, nb, nc;
	public int[] ia, ib, ic;
	public int type_ABC = 0; 
	
	public CL_I(int n, int na, int nb, int nc) {
		this.n = n;
		set_abc(na,nb,nc);
		set_indices();
	}

	public CL_I(int n, int na, int nb, int nc, int type_ABC) {
		this.n = n;
		this.type_ABC = type_ABC;
		set_abc(na,nb,nc);
		set_indices();
	}
	
	public void set_abc(int na, int nb, int nc) {
    // set samplings linear		
		this.nb = 0;
		this.nc = 0;
		if(na<=0 || na >= n) {
			this.na = n;
		} else {
			this.na = na;
			int nrest = n - na;
			if (nb <= 0 || nb >= nrest) {
				this.nb = nrest;
			} else {
				this.nb = nb;
				this.nc = n - na - nb;
			}
		}
		this.ia = new int[this.na];
		if (this.nb > 0) this.ib = new int[this.nb]; 
		if (this.nc > 0) this.ic = new int[this.nc];
	}
	
	public void set_indices() {
		for(int i = 0; i<na; i++) {
			ia[i] = i;
		}
		if (this.nb > 0) {
			for(int i=0; i<nb; i++) {
				ib[i] = i+na;
			}
		} 
		if (this.nc > 0) {
			for(int i=0; i<nc; i++) {
				ic[i] = i+na+nb;
			}
		}
	}
	
	public void set_a(Xy xy, Xy x0) {
		for(int i=0; i<na; i++) {
			xy.y[i] = x0.y[ia[i]];
			for(int j=0; j<x0.m; j++) {
				xy.x[j][i] = x0.x[j][ia[i]];
			}
		}
	}

	public void set_b(Xy xy, Xy x0) {
		if (nb <= 0) return;
		for(int i=0; i<nb; i++) {
			xy.y[i] = x0.y[ib[i]];
			for(int j=0; j<x0.m; j++) {
				xy.x[j][i] = x0.x[j][ib[i]];
			}
		}
	}

	public void set_c(Xy xy, Xy x0) {
		if (nc <= 0) return;
		for(int i=0; i<nc; i++) {
			xy.y[i] = x0.y[ic[i]];
			for(int j=0; j<x0.m; j++) {
				xy.x[j][i] = x0.x[j][ic[i]];
			}
		}
	}
	
}
