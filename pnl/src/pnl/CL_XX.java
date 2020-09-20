package pnl;

public class CL_XX {
	public int n,m1;
	public double[] xm, xmin, xmax, sx2, yx;
	public double[][] x;
	
	public CL_XX(int n, int m) {
		this.n = n;
		this.m1 = m + 1;
		this.xm = new double[m1];
		this.xmin = new double[m1];
		this.xmax = new double[m1];
		this.sx2 = new double[m1];
		this.yx = new double[m1];
		this.x = new double[m1][n];
	} 
	
	public void xm1( int j ) {
		if (j==0) {
			double s = x[j][0];
			for (int i=1;i<n;i++) {
				double c = (double) i / (i+1);
				s = c * (s + x[j][i] / i);
			}
			xm[j] = s;
		} else {
			xm[0] = 1;
		}	
	}
	
	public void sum_square( int j ) {
		if (j==0) {
			double s = 0;
			for(int i=0;i<n;i++) {
				s += x[j][i] * x[j][i];
			}
			sx2[j] = s;
		} else {
			sx2[0] = (double) n;
		}
	}

	

}
