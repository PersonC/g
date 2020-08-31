package pnl;

public class pnl {

	public static void main(String[] args) {
		int n = 20;
		X x = new X(n);
		for(int i=0;i<n;i++) {
			x.x[i] = (double)i+1;
		}
		x.calc();
		System.out.println(x.toPrint());
		
		X x2 = new X(n,"x2");
		for(int i=0;i<n;i++) {
			x2.x[i] = x.x[i]*0.1;
		}
		x2.calc();
		System.out.println(x2.toPrint());
		
		X y = new X(n);
		for(int i=0;i<n;i++) {
			y.x[i] = 1.0 + 2.0 * x.x[i] + 3.0 * x2.x[i];
		}
		y.calc();
		System.out.println(y.toPrint());
		

	}

}
