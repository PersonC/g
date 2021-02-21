package cml;

public interface IF_LSM  extends IF_Criterion{
	
	public static final double EPS = 1e-20;
	
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

}
