package pnl;

public class Xy {
	public int n,m;
	public double[] xm, xmin, xmax, sx2, yx;
	public double[][] x; // [m][n]
	// for y
	public double ym, sy2, ymin, ymax;
	public double[] y;
	// for Ind
	public CL_I id;

	public Xy(int n, int m) {
		this.n = n;
		this.m = m;
		this.xm = new double[m];
		this.xmin = new double[m];
		this.xmax = new double[m];
		this.sx2 = new double[m];
		this.yx = new double[m];
		this.x = new double[m][n];
		this.y = new double[n];
	}
	
	public void set(CL_I ind) {
		this.id = ind;
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
	
	public void f_yx() {
		for (int j=0; j<m; j++) {
			yx[j] = 0.0;
			for (int i=0; i<n; i++) {
				yx[j] += y[i] * x[j][i];
			}
		}
	}
	
	public void calc() {
		for(int j=0; j<m; j++) {
			xm_j(j);
			sum_square_j(j);
			maxmin(j);
		}
		calc_y();
		f_yx();
	}
	
	public String toPrint(int j) {
		return "j="+j + 
			   ", min=" + xmin[j] + 
			   ", max=" + xmax[j] +
			   ", xm=" + xm[j] +
			   ", sum(xy**2)=" + sx2[j] +
			   ", yx=" + yx[j]; 
	}
	// methods for y
	public void ym() {
		double s = 0;
		for (int i=0; i<n; i++) { s = s + y[i]; }
		this.ym = s / (double) n;
	}
	public void sum_square() {
		double s = 0;
		for(int i=0;i<n;i++) { s += y[i]*y[i]; }
		this.sy2 = s;
	}

	public void maxmin () {
		ymin=y[0]; ymax=y[0];
		for(int i=1;i<n;i++) {
			if (y[i]<ymin) ymin=y[i];
			if (y[i]>ymax) ymax=y[i];
		}
	}
	public void calc_y() {
		ym(); sum_square(); maxmin();
	}
	
	public String toPrint() {
		return "n=" + n + 
			   ", ymin=" + ymin + 
			   ", ymax=" + ymax +
			   ", ym=" + ym +
			   ", sum(y**2)=" + sy2; 
	}	
}  // end class
