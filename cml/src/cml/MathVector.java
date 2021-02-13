package cml;

public class MathVector {
	public int      iv,n;
	public double[] v;
	public double   vmin = 1e30, vmax = -1e-30, vAverage = 0, sumv2 = 0;
	public boolean artifical = false;

	public MathVector(int n, int iv) {
		this.n  = n;
		this.v  = new double[n];
		this.iv = iv;
	}

	public void valuation() {
		for (int i = 0; i < n; i++) {
			if (v[i] < vmin)
				vmin = v[i];
			if (v[i] > vmax)
				vmax = v[i];
			vAverage += v[i];
			sumv2 += v[i] * v[i];
		}
		vAverage /= (double) n;
	}

	public void test(int alg, double A) {
		artifical = true;
		switch (alg) {
		case (0):
			for (int i = 0; i < n; i++) {
				v[i] = A;
			}
			break;
		case (1):
			for (int i = 0; i < n; i++) {
				v[i] = A * Math.random();
			}
			break;
		default:
			for (int i = 0; i < n; i++) {
				v[i] = A * (double) i;
			}
			break;
		}
	    valuation();
	}
}
