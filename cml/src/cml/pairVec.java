package cml;

public class pairVec {
	public MathVector y,x1,x2;
	public int n;
	public double a1,a2,cr0 = 99999,cr1 = 99999,cr2 = 99999;
	public boolean succ = false;
	
	public pairVec(MathVector y, MathVector x1, MathVector x2) {
		this.y  = y;
		this.x1 = x1;
		this.x2 = x2;
		this.n = Math.min(Math.min(y.n, x1.n), x2.n);
	}
	

}
