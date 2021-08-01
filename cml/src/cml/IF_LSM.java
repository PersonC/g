package cml;

public interface IF_LSM {
	public static final double EPS = 1e-20;
	public static enum GMDH { LSM,
		REG,REGB,REGCOS,REGCOSB,
		BIAS,BIASCOEF,REG_AB,BIAS_REG	};
	public static String[] nameGMDH = {
			"Метод наименьших квадратов",
			"Критерий регулярности А",
			"Критерий регулярности B",
			"Критерий регулярности на основе угла между векторами А",
			"Критерий регулярности на основе угла между векторами В",
			"Критерий несмещенности ||ym(A)-ym(B)||",
			"Критерий несмещенности 2",
			"Критерий регулярности ||y-ym(A)||(B) - ||y-ym(B)||(A)",
			"Комбинированный критерий"
	}; 	
	
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
		double[] lsm = {0,0};
		if (y == null || x1 == null || x2 == null) return lsm;
		double yx1 = xy(y,x1), yx2 = xy(y,x2), sxz = xy(x1,x2);
		double D = x1.sumv2 * x2.sumv2 - sxz * sxz;
		if (Math.abs(D) < EPS) return lsm;
		lsm[0] = (yx1 * x2.sumv2 - sxz * yx2) / D;
		lsm[1] = (x1.sumv2 * yx2 - sxz * yx1) / D; 
		return lsm;
	}

	default double[] coef2(MathVector y, MathVector x) {
		double[] lsm = {0,0};
		if (y == null || x == null) return lsm;
		lsm[0] = (x.vAverage == 0) ? 0 : y.vAverage / x.vAverage;
		lsm[1] = 0; 
		return lsm;
	}
	
	default double detCR(MathVector y, MathVector x1, MathVector x2, double c1, double c2) {
		// (y - ym)**2
		double s2 = 0.0, s;
		for ( int i = 0; i < y.n; i++) {
			s = (y.v[i] - c1 * x1.v[i] - c2 * x2.v[i]); s2 += s*s;
		}
		return s2;
	}
	
	default double detCR(MathVector y, MathVector x1, double c1) {
		double s2 = 0.0, s;
		for ( int i = 0; i < y.n; i++) {
			s = (y.v[i] - c1 * x1.v[i]);  s2 += s*s;
		}
		return s2;
	}
	
//---- сравнение моделей
	default double detCR(MathVector z1, MathVector z1d, 
			             MathVector z2, MathVector z2d, 
			             double a1, double a2, double b1, double b2) {
		// критерий смещенности (ymA(A+B)-ymB(A+B))**2
		double sxy, s1 = a1-b1, s2 = a2-b2, s11, s22;
		sxy = xy(z1,z2) + xy(z1d,z2d);
		s11 = s1 * s1; s22 = s2 * s2;
		return s11 * (z1.sumv2 + z1d.sumv2) + 2 * s1 * s2 * sxy + s22 * (z2.sumv2 + z2d.sumv2);
	}
	
	default double detCR(MathVector z, MathVector zd, double a, double b) {
		// критерий смещенности (ymA(A+B)-ymB(A+B))**2
		double s = a-b;
		return s * s * (z.sumv2 + zd.sumv2);
	}

//---- конец сравнения моделей
	
	default double CR(MathVector y, MathVector x1, MathVector x2, double c1, double c2, int Lc) {
		if (Lc <= 1 ) return detCR(y,x1,c1);
		return detCR(y,x1,x2,c1,c2);
	}

	default double detCRcos(MathVector y, MathVector x1, MathVector x2, double c1, double c2) {
		if (y.norma == 0) return 0;
		double s2 = 0.0, zm, znorma = 0;
		for ( int i = 0; i < y.n; i++) {
			zm = c1 * x1.v[i] + c2 * x2.v[i];
			znorma += zm*zm;
			s2 += y.v[i] * zm;
		}
		znorma = Math.sqrt(znorma);
		if (znorma == 0) return 0;
		return acos(s2 / (y.norma * znorma));
	}
	
	default double detCRcos(MathVector y, MathVector x1, double c1) {
		if (y.norma == 0) return 0;
		double s2 = 0.0, zm, znorma = 0;
		for ( int i = 0; i < y.n; i++) {
			zm = c1 * x1.v[i];
			znorma += zm*zm;
			s2 += y.v[i] * zm;
		}
		znorma = Math.sqrt(znorma);
		if (znorma == 0) return 0;
		return acos(s2 / (y.norma * znorma));
	}
	
	default double CRcos(MathVector y, MathVector x1, MathVector x2, double c1, double c2, int Lc) {
		if (Lc <= 1 ) return detCRcos(y,x1,c1);
		return detCRcos(y,x1,x2,c1,c2);
	}
	
	default double acos(double a) {
		if (a > 1) {
			System.out.println(a + "больше 1");
			return (90);
		}	
		if (a < -1) {
			System.out.println(a + "меньше 1");
			return (-90);
		}	
		return Math.acos(a)*180.0/Math.PI;
	}
	
//	default double yModel(MathVector y, MathVector x1, MathVector x2, double c1, double c2) {
//		double s2 = 0.0, s;
//		for ( int i = 0; i < y.n; i++) {
//			s = (y.v[i] - c1 * x1.v[i] - c2 * x2.v[i]); s2 += s*s;
//		}
//		return s2;
//	}
//	
//	default double yModel(MathVector y, MathVector x1, double c1) {
//		double s2 = 0.0, s;
//		for ( int i = 0; i < y.n; i++) {
//			s = (y.v[i] - c1 * x1.v[i]);  s2 += s*s;
//		}
//		return s2;
//	}
	
	default double roundAvoid(double value, int places) {
	    double scale = Math.pow(10, places);
	    return Math.round(value * scale) / scale;
	}	
	
	default double roundHalf(double value, int places) {
	    double scale = Math.pow(10, places);
	    return  Math.floor(value * scale +.5)/scale;
	}
	
	default double roundLast(double value, int r) {
		int lu = -((int) Math.log10(Math.ulp(value)) + r);
	    double scale = Math.pow(10, lu);
	    return Math.round(value * scale) / scale;
	}	

}
