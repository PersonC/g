package pnl;

public class Z {
	public int n,m,f,na,nb,nc;
	public Xy x0;
/*	public double[]   zm, zmin, zmax, sz2, yz;
	public double[][] z;
	public double[][] a; // [m][f]
	public double[]   r; // [f]
	public int[]      ir; // [f] - индексы r по возрастанию
    private int f0 = 0, f2 = 0;
*/
/*	double[] crm; // [f]
	double[] a1;
	double[] a2;
	int[]    jx, kz;
*///
	public int typeCR = 0;
	public Zt z0;
	public ZtA za;
	public Zt zb;
	
	public Z(Xy x0, int f) {
		this.n = x0.n; this.na = x0.id.na; this.nb = x0.id.nb; this.nc = x0.id.nc; 
		this.m = x0.m;
		if (f<0) f = m;
		this.f = f;
		this.z0 = new Zt(n,m,f,0);
		this.za = new ZtA(na,m,f,1);
		this.zb = new Zt(nb,m,f,2);
/*		this.zm   = new double[f];
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
*/	}
	
	public boolean set_next_step(Xy xy) {
		boolean yes = false;
		z0.f2 = 0;
		for( int j=0; j<m; j++) {
			for( int k=0; k<z0.f0; k++) {
				if (z0.model(xy, j, k)) yes = true;
			}
		}
		if (yes) z0.build(xy);
		return yes;
	}
	
}
