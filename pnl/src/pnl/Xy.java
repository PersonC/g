package pnl;

public class Xy {
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
		double s =0.0;
		for (int i=0; i<n; i++) { s = s + x[j][i]; }	
		xm[j][0] = s/(double) n;
		//
		int i1;
		s = 0;
		for (int i=0; i<id.na; i++ ) {
			i1 = id.ia[i];
			s += x[j][i1];
		}
		this.xm[j][1] = s / (double) id.na;
		//
		if( id.nb > 0 ) {
			s = 0;
			for (int i=0; i<id.nb; i++ ) {
				i1 = id.ib[i];
				s += x[j][i1];
			}
			this.xm[j][2] = s / (double) id.nb;
		}
		//
		if( id.nc > 0 ) {
			s = 0;
			for (int i=0; i<id.nc; i++ ) {
				i1 = id.ic[i];
				s += x[j][i1];
			}
			this.xm[j][3] = s / (double) id.nc;
		}
	}
	
	public void sum_square_j( int j ) {
		double s = 0;
		for(int i=0;i<n;i++) { s += x[j][i] * x[j][i]; }
		this.sx2[j][0] = s;
		//
		int i1;
		s = 0;
		for (int i=0; i<id.na; i++ ) {
			i1 = id.ia[i];
			s += x[j][i1] * x[j][i1];
		}
		this.sx2[j][1] = s / (double) id.na;
		//
		if( id.nb > 0 ) {
			s = 0;
			for (int i=0; i<id.nb; i++ ) {
				i1 = id.ib[i];
				s += x[j][i1] * x[j][i1];
			}
			this.sx2[j][2] = s / (double) id.nb;
		}
		//
		if( id.nc > 0 ) {
			s = 0;
			for (int i=0; i<id.nc; i++ ) {
				i1 = id.ic[i];
				s += x[j][i1] * x[j][i1];
			}
			this.sx2[j][3] = s / (double) id.nc;
		}
		
	}

	public void maxmin ( int j) {
		xmin[j][0] = x[j][0]; xmax[j][0] = x[j][0];
		for(int i=1;i<n;i++) {
			if (x[j][i]<xmin[j][0]) xmin[j][0] = x[j][i];
			if (x[j][i]>xmax[j][0]) xmax[j][0] = x[j][i];
		}
		//
		int i1 = id.ia[0];
		xmin[j][1] = x[j][i1]; xmax[j][1] = x[j][i1];
		for (int i=1; i<id.na; i++ ) {
			i1 = id.ia[i];
			if (x[j][i1]<xmin[j][1]) xmin[j][1] = x[j][i1];
			if (x[j][i1]>xmax[j][1]) xmax[j][1] = x[j][i1];
		}
		//
		if( id.nb > 0 ) {
			i1 = id.ib[0];
			xmin[j][1] = x[j][i1]; xmax[j][1] = x[j][i1];
			for (int i=1; i<id.nb; i++ ) {
				i1 = id.ib[i];
				if (x[j][i1]<xmin[j][2]) xmin[j][2] = x[j][i1];
				if (x[j][i1]>xmax[j][2]) xmax[j][2] = x[j][i1];
			}
		}
		//
		if( id.nc > 0 ) {
			i1 = id.ic[0];
			xmin[j][1] = x[j][i1]; xmax[j][1] = x[j][i1];
			for (int i=1; i<id.nc; i++ ) {
				i1 = id.ic[i];
				if (x[j][i1]<xmin[j][3]) xmin[j][3] = x[j][i1];
				if (x[j][i1]>xmax[j][3]) xmax[j][3] = x[j][i1];
			}
		}
	}
	
	public void f_yx() {
		int i1;
		for (int j=0; j<m; j++) {
			yx[j][0] = 0.0;
			for (int i=0; i<n; i++) {
				yx[j][0] += y[i] * x[j][i];
			}
			//
			yx[j][1] = 0.0;
			for (int i=0; i<id.na; i++) {
				i1 = id.ia[i];
				yx[j][1] += y[i1] * x[j][i1];
			}
			//
			if( id.nb > 0 ) {
				yx[j][2] = 0.0;
				for (int i=0; i<id.nb; i++ ) {
					i1 = id.ib[i];
					yx[j][2] += y[i1] * x[j][i1];
				}
			}
			//
			if( id.nc > 0 ) {
				yx[j][3] = 0.0;
				for (int i=0; i<id.nc; i++ ) {
					i1 = id.ic[i];
					yx[j][3] += y[i1] * x[j][i1];
				}
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
			   ", min=" + xmin[j][0] + 
			   ", max=" + xmax[j][0] +
			   ", xm=" + xm[j][0] +
			   ", sum(xy**2)=" + sx2[j][0] +
			   ", yx=" + yx[j][0]; 
	}
	// methods for y
	public void ym() {
		double s = 0;
		for (int i=0; i<n; i++) { s = s + y[i]; }
		this.ym[0] = s / (double) n;
		//
		int i1;
		s = 0;
		for (int i=0; i<id.na; i++ ) {
			i1 = id.ia[i];
			s += y[i1];
		}
		this.ym[1] = s / (double) id.na;
		//
		if( id.nb > 0 ) {
			s = 0;
			for (int i=0; i<id.nb; i++ ) {
				i1 = id.ib[i];
				s += y[i1];
			}
			this.ym[2] = s / (double) id.nb;
		}
		//
		if( id.nc > 0 ) {
			s = 0;
			for (int i=0; i<id.nc; i++ ) {
				i1 = id.ic[i];
				s += y[i1];
			}
			this.ym[3] = s / (double) id.nc;
		}
	}
	public void sum_square() {
		double s = 0;
		for(int i=0;i<n;i++) { s += y[i]*y[i]; }
		this.sy2[0] = s;
		//
		int i1;
		s = 0;
		for (int i=0; i<id.na; i++ ) {
			i1 = id.ia[i];
			s += y[i1]*y[i1];
		}
		this.sy2[1] = s / (double) id.na;
		//
		if( id.nb > 0 ) {
			s = 0;
			for (int i=0; i<id.nb; i++ ) {
				i1 = id.ib[i];
				s += y[i1]*y[i1];;
			}
			this.sy2[2] = s / (double) id.nb;
		}
		//
		if( id.nc > 0 ) {
			s = 0;
			for (int i=0; i<id.nc; i++ ) {
				i1 = id.ic[i];
				s += y[i1]*y[i1];;
			}
			this.sy2[3] = s / (double) id.nc;
		}
		
	}

	public void maxmin () {
		ymin[0]=y[0]; ymax[0]=y[0];
		for(int i=1;i<n;i++) {
			if (y[i]<ymin[0]) ymin[0]=y[i];
			if (y[i]>ymax[0]) ymax[0]=y[i];
		}
		//
		int i1 = id.ia[0];
		ymin[1]=y[i1]; ymax[1]=y[i1];
		for (int i=1; i<id.na; i++ ) {
			i1 = id.ia[i];
			if (y[i1]<ymin[1]) ymin[1]=y[i1];
			if (y[i1]>ymax[1]) ymax[1]=y[i1];
		}
		//
		if( id.nb > 0 ) {
			i1 = id.ib[0];
			ymin[2]=y[i1]; ymax[2]=y[i1];
			for (int i=1; i<id.nb; i++ ) {
				i1 = id.ib[i];
				if (y[i1]<ymin[2]) ymin[2]=y[i1];
				if (y[i1]>ymax[2]) ymax[2]=y[i1];
			}
		}
		//
		if( id.nc > 0 ) {
			i1 = id.ic[0];
			ymin[3]=y[i1]; ymax[3]=y[i1];
			for (int i=1; i<id.nc; i++ ) {
				i1 = id.ic[i];
				if (y[i1]<ymin[3]) ymin[3]=y[i1];
				if (y[i1]>ymax[3]) ymax[3]=y[i1];
			}
		}
	}
	public void calc_y() {
		ym(); sum_square(); maxmin();
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
