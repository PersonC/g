package pnl;

public interface PrepareXYZ {
	public static final double EPS = 1e-20;
//------------------------------------------------------------------------------
	default double xmForj (double[][] x, int j, int n, int[][] i1, int type) {
		double s = 0; 
		double nn = 0;
		for ( int i=0; i<n; i++) {
			if (i1[i][type] == 1) {
			   s  += x[j][i];
			   nn += 1;
			}
		}
		s = (nn>0) ? s/nn : 0;
		return(s);
	}
//------------------------------------------------------------------------------
	default double sumSquareForj (double[][] x, int j, int n, int[][] i1, int type) {
		double s = 0;
		for(int i=0; i<n; i++) { s += (i1[i][type] == 1) ? x[j][i] * x[j][i] : 0; }
		return(s);
	}

	default double minForj (double[][] x, int j, int n, int[][] i1, int type) {
		double s = 1e100;
		for(int i=1; i<n; i++) {
			if (i1[i][type] == 1) {
				if (x[j][i] < s) s = x[j][i];
			}
		}
		return(s);
	}
//------------------------------------------------------------------------------
	default double maxForj (double[][] x, int j, int n, int[][] i1, int type) {
		double s = 1e-100;
		for(int i=1; i<n; i++) {
			if (i1[i][type] == 1) {
				if (x[j][i] > s) s = x[j][i];
			}
		}
		return(s);
	}
//------------------------------------------------------------------------------
	default double xyForj(double[] y, double[][] x, int j, int n, int[][] i1, int type) {
		double s = 0;
		for(int i=0; i<n; i++) {
			s += (i1[i][type] == 1) ? y[i] * x[j][i] : 0;
		}
		return(s);
	}
//------------------------------------------------------------------------------
	default double ym(double[] y, int n, int[][] i1, int type) {
		double s = 0;
		double nn = 0;
		for(int i=0; i<n; i++) {
			if (i1[i][type] == 1) {
				s += y[i];
				nn += 1;
			}	
		}
		s = (nn>0) ? s/nn : 0;
		return(s);
	}
//------------------------------------------------------------------------------
	default double sum_square(double[] y, int n, int[][] i1, int type) {
		double s = 0;
		for(int i=0; i<n; i++) {
			s += (i1[i][type] == 1) ? y[i] * y[i] : 0;
		}
		return(s);
	}
//------------------------------------------------------------------------------
	default double miny(double[] y, int n, int[][] i1, int type) {
		double s = 1e100;
		for(int i=0; i<n; i++) {
			if (i1[i][type] == 1) {
				if (y[i] < s) s = y[i];
			}
		}
		return(s);
	}
//------------------------------------------------------------------------------
	default double maxy(double[] y, int n, int[][] i1, int type) {
		double s = 1e-100;
		for(int i=0; i<n; i++) {
			if (i1[i][type] == 1) {
				if (y[i] > s) s = y[i];
			}
		}
		return(s);
	}
}
