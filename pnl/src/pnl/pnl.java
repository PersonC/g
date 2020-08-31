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

	}

}
