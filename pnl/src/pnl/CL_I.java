package pnl;

public class CL_I {
	public int n, na, nb, nc;
	public int[] ia, ib, ic;
	public CL_I(int n) {
		this.n = n;
	}
	
	public void set_abc(int na, int nb, int nc) {
    // set samplings		
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
	
	public void set_indices(int order) {
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
	
}
