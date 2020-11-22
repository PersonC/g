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
	// for Ind
	public CL_I id;

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
	}
	
	public void set(CL_I ind) {
		this.id = ind;
	}
	
	public void xm_j( int j ) {
		xm[j][0] = xmForj(x, j, n);
		xm[j][1] = xmForj(x, j, id.na, id.ia);
		if( id.nb > 0 ) { xm[j][2] = xmForj(x, j, id.nb, id.ib); }
		if( id.nc > 0 ) { xm[j][3] = xmForj(x, j, id.nc, id.ic); }
	}

	public void ym( ) {
		ym[0] = ym(y, n);
		ym[1] = ym(y, id.na, id.ia);
		if( id.nb > 0 ) { ym[2] = ym(y, id.nb, id.ib); }
		if( id.nc > 0 ) { ym[3] = ym(y, id.nc, id.ic); }
	}
	
	public void sum_square( ) {
		sy2[0] = sum_square(y, n);
		sy2[1] = sum_square(y, id.na, id.ia);
		if( id.nb > 0 ) { sy2[2] = sum_square(y, id.nb, id.ib); }
		if( id.nc > 0 ) { sy2[3] = sum_square(y, id.nc, id.ic); }
	}

	public void sum_square_j( int j ) {
		sx2[j][0] = sumSquareForj(x, j, n); 
		sx2[j][1] = sumSquareForj(x, j, id.na, id.ia);		
		if( id.nb > 0 ) { sx2[j][2] = sumSquareForj(x, j, id.nb, id.ib); }
		if( id.nc > 0 ) { sx2[j][3] = sumSquareForj(x, j, id.nc, id.ic); }
	}

	public void maxmin ( int j) {
		xmin[j][0] = minForj(x, j, n); 
		xmin[j][1] = minForj(x, j, id.na, id.ia);		
		if( id.nb > 0 ) { xmin[j][2] = minForj(x, j, id.nb, id.ib); }
		if( id.nc > 0 ) { xmin[j][3] = minForj(x, j, id.nc, id.ic); }

		xmax[j][0] = maxForj(x, j, n); 
		xmax[j][1] = maxForj(x, j, id.na, id.ia);		
		if( id.nb > 0 ) { xmax[j][2] = maxForj(x, j, id.nb, id.ib); }
		if( id.nc > 0 ) { xmax[j][3] = maxForj(x, j, id.nc, id.ic); }
	}
	

	public void maxminy () {
		ymin[0] = miny(y, n); 
		ymin[1] = miny(y, id.na, id.ia);		
		if( id.nb > 0 ) { ymin[2] = miny(y, id.nb, id.ib); }
		if( id.nc > 0 ) { ymin[3] = miny(y, id.nc, id.ic); }

		ymax[0] = maxy(y, n); 
		ymax[1] = maxy(y, id.na, id.ia);		
		if( id.nb > 0 ) { ymax[2] = maxy(y, id.nb, id.ib); }
		if( id.nc > 0 ) { ymax[3] = maxy(y, id.nc, id.ic); }
	}
	
	public void f_yx() {
		for ( int j=0; j<m; j++ ) {
			yx[j][0] = xyForj(y, x, j, n);
			yx[j][1] = xyForj(y, x, j, id.na, id.ia);
			if( id.nb > 0 ) { yx[j][2] = xyForj(y, x, j, id.nb, id.ib); }
			if( id.nc > 0 ) { yx[j][3] = xyForj(y, x, j, id.nc, id.ic); }
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
