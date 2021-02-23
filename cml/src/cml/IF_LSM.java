package cml;

public interface IF_LSM {
	
	public static final double EPS = 1e-20;
	public enum GMDH {
		LSM,
		REG,
		BIASCOEF,
		BIASREG
	}
	
	default double xy(MathVector y, MathVector x) {
		double xy = 0.0;
		for (int i = 0; i < y.n; i++) { xy += y.v[i] * x.v[i]; }
		return xy;
	}
	
	default double[] coef2(MathVector y, MathVector x1, MathVector x2) {
		
		double yx1 = xy(y,x1), yx2 = xy(y,x2), sxz = xy(x1,x2);
		double D = x1.sumv2 * x2.sumv2 - sxz * sxz;

		double[] lsm = new double[3];
		if (Math.abs(D) < EPS) { lsm[2] = -1; return lsm; }
		lsm[0] = (yx1 * x2.sumv2 - sxz * yx2) / D;
		lsm[1] = (x1.sumv2 * yx2 - sxz * yx1) / D;
		lsm[2] = detCR(y,x1,x2,lsm[0],lsm[1]);
		
		return lsm;
	}
	
	default double coef1(MathVector y, MathVector x) {
		return (x.vAverage == 0) ? 0 : y.vAverage / x.vAverage;
	}
	
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
