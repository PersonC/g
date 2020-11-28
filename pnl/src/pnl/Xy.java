package pnl;

public class Xy implements PrepareXYZ {
	public int n,m;
	public double[][] xm, xmin, xmax, sx2, yx;
	public double[][] x; // [m][n]
	// for y
	public double ym[]   = new double[4];
	public double sy2[]  = new double[4];
	public double ymin[] = new double[4];
	public double ymax[] = new double[4];
	public double[] y;
	
	// for sampling
	public int im[][];
//	public CL_I id;

	public Xy(int n, int m) {
		this.n    = n;
		this.m    = m;
		this.xm   = new double[m][4];
		this.xmin = new double[m][4];
		this.xmax = new double[m][4];
		this.sx2  = new double[m][4];
		this.yx   = new double[m][4];
		this.x    = new double[m][n];
		this.y    = new double[n];
		this.im   = new int   [n][4];
	}
	
	public void set_sampling0(int na, int nb, int nc) {
//		this.id = ind;
		int nab = na + nb;
		for(int i=0; i<n; i++) {
			im[i][0] = 1;
			im[i][1] = (i<na) ? 1 : 0;
			im[i][2] = (i>=na && i<nab) ? 1 : 0;
			im[i][3] = (i>=nab) ? 1 : 0;		
		}
	}
	
	public void xm_j( int j ) {
		xm[j][0] = xmForj(x, j, n, im, 0);
		xm[j][1] = xmForj(x, j, n, im, 1);
		xm[j][2] = xmForj(x, j, n, im, 2);
		xm[j][3] = xmForj(x, j, n, im, 3);
	}

	public void ym( ) {
		ym[0] = ym(y, n, im, 0);
		ym[1] = ym(y, n, im, 1);
		ym[2] = ym(y, n, im, 2);
		ym[3] = ym(y, n, im, 3);
	}
	
	public void sum_square( ) {
		sy2[0] = sum_square(y, n, im, 0);
		sy2[1] = sum_square(y, n, im, 1);
		sy2[2] = sum_square(y, n, im, 2);
		sy2[3] = sum_square(y, n, im, 3);
	}

	public void sum_square_j( int j ) {
		sx2[j][0] = sumSquareForj(x, j, n, im, 0); 
		sx2[j][1] = sumSquareForj(x, j, n, im, 1); 
		sx2[j][2] = sumSquareForj(x, j, n, im, 2); 
		sx2[j][3] = sumSquareForj(x, j, n, im, 3); 
	}

	public void maxmin ( int j) {
		xmin[j][0] = minForj(x, j, n, im, 0); 
		xmin[j][1] = minForj(x, j, n, im, 1); 
		xmin[j][2] = minForj(x, j, n, im, 2); 
		xmin[j][3] = minForj(x, j, n, im, 3); 

		xmax[j][0] = maxForj(x, j, n, im, 0); 
		xmax[j][1] = maxForj(x, j, n, im, 1); 
		xmax[j][2] = maxForj(x, j, n, im, 2); 
		xmax[j][3] = maxForj(x, j, n, im, 3); 
	}
	

	public void maxminy () {
		ymin[0] = miny(y, n, im, 0); 
		ymin[1] = miny(y, n, im, 1); 
		ymin[2] = miny(y, n, im, 2); 
		ymin[3] = miny(y, n, im, 3); 

		ymax[0] = maxy(y, n, im, 0); 
		ymax[1] = maxy(y, n, im, 1); 
		ymax[2] = maxy(y, n, im, 2); 
		ymax[3] = maxy(y, n, im, 3); 
	}
	
	public void f_yx() {
		for ( int j=0; j<m; j++ ) {
			yx[j][0] = xyForj(y, x, j, n, im, 0);
			yx[j][1] = xyForj(y, x, j, n, im, 1);
			yx[j][2] = xyForj(y, x, j, n, im, 2);
			yx[j][3] = xyForj(y, x, j, n, im, 3);
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
			   ", min=" + xmin[j][0] + 
			   ", max=" + xmax[j][0] +
			   ", xm=" + xm[j][0] +
			   ", sum(xy**2)=" + sx2[j][0] +
			   ", yx=" + yx[j][0]; 
	}
	
	public void calc_y() {
		ym(); sum_square(); maxminy();
	}
	
	public String toPrint() {
		return "n=" + n + 
			   ", ymin=" + ymin[0] + 
			   ", ymax=" + ymax[0] +
			   ", ym=" + ym[0] +
			   ", sum(y**2)=" + sy2[0]; 
	}
	public void test_data() {
		for(int i=0; i<n; i++) {
			x[0][i] = 1.0;
			x[1][i] = (double)i+1;
			x[2][i] = (double)i-1;
			x[3][i] = (double)i * 0.1;
			y[i] = 10.0 + 2.0 * x[1][i] + 3.0 * x[2][i];
		}
	}
}  // end class
