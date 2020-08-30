package pnl;

public class X {
	public double[] x;
	public int n;
	public double xm;
	public double sx2;
	public X(int n) {
		this.x = new double[n];
		this.n = n;
	}
	public void Xm() {
		double s = x[0];
		for (int i=1;i<n;i++) {
			double c = (double) i / (i+1);
			s = c * (s + x[i] / i);
		}
		this.xm = s;
	}
	public void sum_square() {
		double s = 0;
		for(int i=0;i<n;i++) {
			s += x[i]*x[i];
		}
		this.sx2 = s;
	}

}
