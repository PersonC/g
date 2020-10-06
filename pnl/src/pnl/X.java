package pnl;

public class X {
	public int n,m;
	public double[] xm, xmin, xmax, sx2, yx;
	public double[][] x; // [m][n]
	
	public X(int n, int m) {
		this.n = n;
		this.m = m;
		this.xm = new double[m];
		this.xmin = new double[m];
		this.xmax = new double[m];
		this.sx2 = new double[m];
		this.yx = new double[m];
		this.x = new double[m][n];
	} 
	
	public void xm_j( int j ) {
		double s =0.0;
		for (int i=0; i<n; i++) { s = s + x[j][i]; }	
		xm[j] = s/(double) n;
	}
	
	public void sum_square_j( int j ) {
		double s = 0;
		for(int i=0;i<n;i++) { s += x[j][i] * x[j][i]; }
		this.sx2[j] = s;
	}

	public void maxmin ( int j) {
		xmin[j]=x[j][0]; xmax[j]=x[j][0];
		for(int i=1;i<n;i++) {
			if (x[j][i]<xmin[j]) xmin[j]=x[j][i];
			if (x[j][i]>xmax[j]) xmax[j]=x[j][i];
		}
	}
	
	public void calc() {
		for(int j=0; j<m; j++) {
			xm_j(j);
			sum_square_j(j);
			maxmin(j);
		}
	}
	
	public String toPrint(int j) {
		return "j="+j + 
			   ", min=" + xmin[j] + 
			   ", max=" + xmax[j] +
			   ", xm=" + xm[j] +
			   ", sum(x**2)=" + sx2[j]; 
	}
}  // end class
