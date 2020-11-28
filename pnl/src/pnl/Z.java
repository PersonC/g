package pnl;

public class Z {
	public int n,m,f,na,nb,nc;
	public Xy x0;

	public int typeCR = 0;
	public Zt  z0;
	public Zt  za;
	public Zt  zb;
	
	public Z(Xy x0, int f) {
		this.m = x0.m;
		if (f<0) f = m;
		this.f = f;
		this.x0 = x0;
		this.z0 = new Zt (f,0,0,x0);
		this.za = new Zt (f,1,2,x0);
		this.zb = new Zt (f,2,1,x0);
	}
	
	public void calc_model(Zt zset, String text) {
		zset.set_zero_step();
		zset.toPrint(text + "_0");
//
		if(zset.set_next_step()) {
			zset.toPrintCr();
			zset.toPrint(text + "_1");
		} else {System.out.println("======== END 1 ==========");}

		if(zset.set_next_step()) {
			zset.toPrintCr();
			zset.toPrint(text + "_2");
		} else {System.out.println("======== END 2 ==========");}
		
	}
}
