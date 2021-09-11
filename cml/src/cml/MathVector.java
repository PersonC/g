package cml;

public class MathVector {
	public int      iv,n;
	public double[] v;
	public double   vmin = 1e30, vmax = -1e-30, vAverage = 0, sumv2 = 0, D = 0, norma = 0;
	public boolean  artifical = false;
	// v = v / scale - parallax
	private double   scale = 1, parallax = 0;
	private double minValue = Math.ulp(10);
	protected enum TYPE_SCALE {
		NORMA,
		CENTER,
		MEAN,
		AFFINE
	};
	private double[] vOrigin;
//>>>>>... constructor	

	public MathVector(int n, int iv) {
		this.n  = n;
		this.v  = new double[n];
		this.iv = iv;
	}
	
	public MathVector(MathVector x1, MathVector x2) {
		this.n = x1.n + x2.n;
		this.iv = x1.iv + x2.iv * 1000;
		this.v = new double[n];
		for (int i=0; i<x1.n; i++) v[i] = x1.v[i];
		for (int i=x1.n, j=0; j<x2.n; i++, j++) v[i] = x2.v[j];
	}
	
// construtor ...<<<<<<<<<<<<
	
//-----> scaling 
	
	public void setScale (double scale) {
		if (Math.abs(scale) > minValue) this.scale = scale;
	}
	public double getScale() { return scale; }
	public void setParallax(double p) { this.parallax = p; }
	public double getParallax() { return parallax; }
	public void setTypeScale(TYPE_SCALE t) {
		switch(t) {
		case NORMA:
			scale = norma;
			parallax = 0;
			break;
		case CENTER:
			scale = 1;
			parallax = vAverage;
			break;
		case MEAN:
			if (Math.abs(vAverage) > minValue) scale = vAverage; else scale = 1;
			parallax = 0;
			break;
		default:	
			scale = 1;
			parallax = 0;
		}
	}
	
	public void makeScale() {
		this.vOrigin  = new double[n];
		for (int i=0; i<n; i++) {
			vOrigin[i] = v[i];
			v[i] = v[i] / scale - parallax;
		}
		try {valuation();} catch (Exception e) {}
	}
	
	public void undoScale() {
		if (vOrigin == null) return;
		for (int i=0; i<n; i++) v[i] = vOrigin[i];
		try {valuation();} catch (Exception e) {}
	}

//  end scaling <------	
	public void oneVector( ) {
		for (int i = 0; i < n; i++) { v[i] = 1; }
		try {valuation();} catch (Exception e) {}
	}
	
	public void oneVector( double yv ) {
		for (int i = 0; i < n; i++) { v[i] = yv; }
		try {valuation();} catch (Exception e) {}
	}
	
	public void addV(double a, MathVector x, int j) {
		if (j==0) addFirst(a,x);
		else addSecond(a,x);
	}

	
	private void addFirst(double a, MathVector x) {
		for (int i=0; i<n; i++) { 
			if (a ==0) v[i]=0; else v[i] = a * x.v[i]; }
	}

	private void addSecond(double a, MathVector x) {
		if (a == 0) return;
		for (int i=0; i<n; i++) { v[i] += a * x.v[i]; }
	}
	
	public void valuation() throws Exception {
		vmin = vmax = vAverage = v[0]; sumv2 = v[0] * v[0];
		for (int i = 1; i < n; i++) {
			if (v[i] < vmin) vmin = v[i];
			if (v[i] > vmax) vmax = v[i];
			vAverage += v[i];
			sumv2 += v[i] * v[i];
		}
		vAverage /= (double) n;
		D = sumv2 / (double) n - vAverage * vAverage;
		norma = Math.sqrt(sumv2);
		if (norma <= minValue) throw new Exception("Norma v="+norma+" is small");
	}
	
	public void runZOne(double a, MathVector x) {
		artifical = false;
		for (int i = 0; i < n; i++) { v[i] = x.v[i] * a; }
		try {valuation();} catch (Exception e) {}
	}

	public void printVector() {
		System.out.println("Фактор " + iv);
		for (int i = 0; i < n; i++) {
			System.out.print(v[i] + " ");
		}
		System.out.println("\n" + "[" + vmin + "," + vAverage + "," 
		                        + vmax + "]" + " s2=" + sumv2 + " D=" + D);
	}
}
