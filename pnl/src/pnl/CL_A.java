package pnl;

public class CL_A {
//	public CL_XX zz;
	public int f, n, m;
	public double a[][];
	public double cr[];
	public CL_A(int n, int m, int f ) {
		this.f = f;
		this.n = n;
		this.m = m;
//		this.zz = new CL_XX(n, f);
		this.a = new double[m][f];
		this.cr = new double[f];
	}

}
