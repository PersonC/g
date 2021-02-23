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
	
	public void addFirst(double a, MathVector x) {
		for (int i=0; i<n; i++) {
			v[i] = a * x.v[i];
		}
	}

	public void addSecond(double a, MathVector x) {
		for (int i=0; i<n; i++) {
			v[i] += a * x.v[i];
		}
	}
	
	public void valuation() {
		for (int i = 0; i < n; i++) {
			if (v[i] < vmin) vmin = v[i];
			if (v[i] > vmax) vmax = v[i];
			vAverage += v[i];
			sumv2 += v[i] * v[i];
		}
		vAverage /= (double) n;
	}
	
	public void runZOne(double a, MathVector x) {
		artifical = false;
		for (int i = 0; i < n; i++) {
			v[i] = x.v[i] * a;
		}
		valuation();
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
	
	public void printVector() {
		System.out.println("Фактор " + iv);
		for (int i = 0; i < n; i++) {
			System.out.print(v[i] + " ");
		}
		System.out.println("\n" + "[" + vmin + "," + vAverage + "," 
		                        + vmax + "]" + " s2=" + sumv2);
	}
}
