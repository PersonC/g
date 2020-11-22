package pnl;

public interface PrepareXYZ {
	public static final double EPS = 1e-20;

	default double xmForj (double[][] x, int j, int n) {
		double s = 0;
		for ( int i=0; i<n; i++) { s += x[j][i]; }	
		s = s/(double) n;
		return(s);
	}

	default double xmForj (double[][] x, int j, int na, int[] i1) {
		double s = 0;
		for ( int i=0; i<na; i++) { s += x[j][ i1[i] ]; }	
		s = s/(double) na;
		return(s);
	}
	
	default double sumSquareForj (double[][] x, int j, int n) {
		double s = 0;
		for(int i=0; i<n; i++) { s += x[j][i] * x[j][i]; }
		return(s);
	}
	
	default double sumSquareForj (double[][] x, int j, int na, int[] i1) {
		double s = 0;
		for(int i=0; i<na; i++) { s += x[j][ i1[i] ] * x[j][ i1[i] ]; }
		return(s);
	}

	default double minForj (double[][] x, int j, int na, int[] i1) {
		double s = x[j][ i1[0] ];
		for(int i=1; i<na; i++) {
			if (x[j][ i1[i] ]<s) s = x[j][ i1[i] ];
		}
		return(s);
	}
	
	default double minForj (double[][] x, int j, int n) {
		double s = x[j][0];
		for(int i=1; i<n; i++) {
			if (x[j][i]<s) s = x[j][i];
		}
		return(s);
	}

	default double maxForj (double[][] x, int j, int n) {
		double s = x[j][0];
		for(int i=1; i<n; i++) {
			if (x[j][i]>s) s = x[j][i];
		}
		return(s);
	}
	
	default double maxForj (double[][] x, int j, int na, int[] i1) {
		double s = x[j][ i1[0] ];
		for(int i=1; i<na; i++) {
			if (x[j][ i1[i] ] > s) s = x[j][ i1[i] ];
		}
		return(s);
	}
	
	default double xyForj(double[] y, double[][] x, int j, int n) {
		double s = 0;
		for(int i=0; i<n; i++) {
			s += y[i] * x[j][i];
		}
		return(s);
	}

	default double xyForj(double[] y, double[][] x, int j, int na, int[] i1) {
		double s = 0;
		for(int i=0; i<na; i++) {
			s += y[ i1[i] ] * x[j][ i1[i] ];
		}
		return(s);
	}
	
	default double ym(double[] y, int n) {
		double s = 0;
		for(int i=0; i<n; i++) {
			s += y[i];
		}
		return(s / (double) n);
	}
	
	default double ym(double[] y, int na, int[] i1) {
		double s = 0;
		for(int i=0; i<na; i++) {
			s += y[ i1[i] ];
		}
		return(s / (double) na);
	}

	default double sum_square(double[] y, int n) {
		double s = 0;
		for(int i=0; i<n; i++) {
			s += y[i] * y[i];
		}
		return(s);
	}
	
	default double sum_square(double[] y, int na, int[] i1) {
		double s = 0;
		for(int i=0; i<na; i++) {
			s += y[ i1[i] ] * y[ i1[i] ];
		}
		return(s);
	}
	
	default double miny(double[] y, int n) {
		double s = y[0];
		for(int i=1; i<n; i++) {
			if (y[i] < s) s = y[i];
		}
		return(s);
	}
	
	default double miny(double[] y, int na, int[] i1) {
		double s = y[ i1[0] ];
		for(int i=1; i<na; i++) {
			if (y[ i1[i] ] < s) s = y[ i1[i] ];
		}
		return(s);
	}
	
	default double maxy(double[] y, int n) {
		double s = y[0];
		for(int i=1; i<n; i++) {
			if (y[i] > s) s = y[i];
		}
		return(s);
	}
	
	default double maxy(double[] y, int na, int[] i1) {
		double s = y[ i1[0] ];
		for(int i=1; i<na; i++) {
			if (y[ i1[i] ] > s) s = y[ i1[i] ];
		}
		return(s);
	}
}
