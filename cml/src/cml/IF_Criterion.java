package cml;

public interface IF_Criterion {
	
	default double detCR(MathVector y, MathVector x1, MathVector x2, double c1, double c2) {
		int n = y.n;
		double s2 = 0.0;
		for ( int i = 0; i < n; i++) {
			double s = (y.v[i] - c1 * x1.v[i] - x2.v[i]);
			s2 += s*s;
		}
		return s2;
	}
	
	default double detCR(MathVector y, MathVector x1, double c1) {
		int n = y.n;
		double s2 = 0.0;
		for ( int i = 0; i < n; i++) {
			double s = (y.v[i] - c1 * x1.v[i]);
			s2 += s*s;
		}
		return s2;
	}
}
