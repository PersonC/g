package pnl;

public class Y {
	
	public int n;
	public double ym, sy2, ymin, ymax;
	public double[] y;
	
	public Y(int n) {
		this.n = n;
		this.y = new double[n];
	}
	
	public void ym() {
		double s = 0;
		for (int i=0; i<n; i++) { s = s + y[i]; }
		this.ym = s / (double) n;
	}
	
	public void sum_square() {
			double s = 0;
			for(int i=0;i<n;i++) { s += y[i]*y[i]; }
			this.sy2 = s;
	}
	
	public void maxmin () {
		ymin=y[0]; ymax=y[0];
		for(int i=1;i<n;i++) {
			if (y[i]<ymin) ymin=y[i];
			if (y[i]>ymax) ymax=y[i];
		}
	}
	
	public void calc() {
		ym(); sum_square(); maxmin();
	}
	
	public String toPrint() {
		return "n=" + n + 
			   ", ymin=" + ymin + 
			   ", ymax=" + ymax +
			   ", ym=" + ym +
			   ", sum(y**2)=" + sy2; 
	}
}