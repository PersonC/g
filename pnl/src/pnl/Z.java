package pnl;

public class Z {
	public int n,m,f,na,nb,nc;
	public Xy x0;

	public int typeCR = 0;
	public Zt z0;
	public ZtA za;
	public ZtA zb;
	
	public Z(Xy x0, int f) {
		this.n = x0.n; this.na = x0.id.na; this.nb = x0.id.nb; this.nc = x0.id.nc; 
		this.m = x0.m;
		if (f<0) f = m;
		this.f = f;
		this.x0 = x0;
		this.z0 = new Zt(n,m,f,0,x0);
		this.za = new ZtA(na,m,f,1,x0);
		this.zb = new ZtA(nb,m,f,2,x0);
	}
	
	public boolean set_next_step() {
		boolean yes = false;
		z0.f2 = 0;
		for( int j=0; j<m; j++) {
			for( int k=0; k<z0.f0; k++) {
				if (z0.model( j, k)) yes = true;
			}
		}
		if (yes) z0.build();
		return yes;
	}
// критерий регулярности
	public boolean set_next_step_CR1() {
		boolean yes = false;
		z0.f2 = 0;
		for( int j=0; j<m; j++) {
			for( int k=0; k<z0.f0; k++) {
				if (za.model( j, k)) yes = true;
			}
		}
		if (yes) za.build();
		return yes;
	}

}
