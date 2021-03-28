package cml;

public interface IF_LSM {
	public static final double EPS = 1e-20;
	public enum GMDH { LSM,REG,BIASCOEF,BIASREG	};
	
	default double xy(MathVector y, MathVector x) {
		if (y == null || x == null) return 0.0;
		double xy = 0.0;
		for (int i = 0; i < y.n; i++) { xy += y.v[i] * x.v[i]; }
		return xy;
	}
	
	default double cosxy(MathVector x1, MathVector x2) {
		// cos угла между векторами
		double DD = x1.sumv2 * x2.sumv2;
		if (DD == 0 ) return 0;
		double xx = xy(x1,x2) / Math.sqrt(DD);
		xx = Math.acos(xx)*180.0/Math.PI;
		return xx;
	}
	
	default double covar(MathVector x1, MathVector x2) {
		// ковариация
		double xx = xy(x1,x2) / (double) x1.n;
		xx = xx - x1.vAverage * x2.vAverage;
		return xx;
	}

	default double corr(MathVector x1, MathVector x2) {
		// ковариация
		double DD = x1.D * x2.D;
		if (DD == 0 ) return 0;
		double xx = covar(x1,x2) / (Math.sqrt(DD));
		return xx;
	}
	
	default double[] coef2(MathVector y, MathVector x1, MathVector x2) {
		double[] lsm = {1,0,-1,1e30};
		if (y == null || x1 == null || x2 == null) return lsm;
		double yx1 = xy(y,x1), yx2 = xy(y,x2), sxz = xy(x1,x2);
		double D = x1.sumv2 * x2.sumv2 - sxz * sxz;
		if (Math.abs(D) < EPS) return lsm;
		lsm[0] = (yx1 * x2.sumv2 - sxz * yx2) / D;
		lsm[1] = (x1.sumv2 * yx2 - sxz * yx1) / D;
		lsm[2] = detCR(y,x1,x2,lsm[0],lsm[1]);
		return lsm;
	}
	
	default double coef1(MathVector y, MathVector x) {
		return (x.vAverage == 0) ? 0 : y.vAverage / x.vAverage;
	}
	
	default double detCR(MathVector y, MathVector x1, MathVector x2, double c1, double c2) {
		double s2 = 0.0;
		for ( int i = 0; i < y.n; i++) {
			double s = (y.v[i] - c1 * x1.v[i] - c2 * x2.v[i]);
			s2 += s*s;
		}
		return s2;
	}
	
	default double detCR(MathVector y, MathVector x1, double c1) {
		double s2 = 0.0;
		for ( int i = 0; i < y.n; i++) {
			double s = (y.v[i] - c1 * x1.v[i]);
			s2 += s*s;
		}
		return s2;
	}

}
