package pnl;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class X {
	public double[] x;
	public int n;
	public String nameX;
	public double xm, sx2, xmin, xmax;
	
	public X(int n) {
		this.x = new double[n];
		this.n = n;
	}
	public X(int n, String name) {
		this.x = new double[n];
		this.n = n;
		this.nameX = name;
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
	public void maxmin () {
		xmin=x[0]; xmax=x[0];
		for(int i=1;i<n;i++) {
			if (x[i]<xmin) xmin=x[i];
			if (x[i]>xmax) xmax=x[i];
		}
		
	}
	public void calc() {
		Xm();
		sum_square();
		maxmin();
	}
	public String toPrint() {
		return "n="+n + 
			   ", min=" + xmin + 
			   ", max=" + xmax +
			   ", xm=" + xm +
			   ", sum(x**2)=" + sx2; 
	}

}
