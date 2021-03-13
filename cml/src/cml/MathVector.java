package cml;

public class MathVector {
	public int      iv,n;
	public double[] v;
	public double   vmin = 1e30, vmax = -1e-30, vAverage = 0, sumv2 = 0, D = 0;
	public boolean  artifical = false;

	public MathVector(int n, int iv) {
		this.n  = n;
		this.v  = new double[n];
		this.iv = iv;
	}

	public MathVector(int n) {
		this.n  = n;
		this.v  = new double[n];
	}
	
	public void addFirst(double a, MathVector x) {
		for (int i=0; i<n; i++) { v[i] = a * x.v[i]; }
	}

	public void addSecond(double a, MathVector x) {
		for (int i=0; i<n; i++) { v[i] += a * x.v[i]; }
	}
	
	public void valuation() {
		for (int i = 0; i < n; i++) {
			if (v[i] < vmin) vmin = v[i];
			if (v[i] > vmax) vmax = v[i];
			vAverage += v[i];
			sumv2 += v[i] * v[i];
		}
		vAverage /= (double) n;
		D = sumv2 / (double) n - vAverage * vAverage;
	}
	
	public void runZOne(double a, MathVector x) {
		artifical = false;
		for (int i = 0; i < n; i++) { v[i] = x.v[i] * a; }
		valuation();
	}

	public void test(int alg, double A) {
// 0 - константа
// 1 - случайное число * А
// 2 - линейная функция х
// 3 - линейная фунция * А * случайное число
// 4 - ехр ( А * случайное число )
// 5 - cos ( А * случайное число )
// 6... - 100 * случайное число * случайное число		
		artifical = true;
		switch (alg) {
		case (0):
			for (int i = 0; i < n; i++) { v[i] = A;	iv=alg; }
			break;
		case (1):
			for (int i = 0; i < n; i++) { v[i] = A * Math.random(); iv=alg;	}
			break;
		case (2):
			for (int i = 0; i < n; i++) { v[i] = A * (double) i; iv=alg; }
			break;
		case (3):
			for (int i = 0; i < n; i++) { v[i] = A * Math.random() * (double) i; iv=alg; }
			break;
		case (4):
			for (int i = 0; i < n; i++) { v[i] = Math.exp( A * Math.random() ); iv=alg; }
			break;
		case (5):
			for (int i = 0; i < n; i++) { v[i] = Math.cos( A * Math.random() ); iv=alg; }
			break;
			
		default:
			for (int i = 0; i < n; i++) { v[i] = 100 * Math.random() * Math.random(); iv=alg; }
			break;
		}
	    valuation();
	}
	
	public void oneVector( ) {
		for (int i = 0; i < n; i++) { v[i] = 1; }
		valuation();
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
