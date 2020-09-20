package pnl;

public class pnl {

	public static void main(String[] args) {
		//
		int n = 20;
		int m = 4;
		int f = 3;
		CL_Y y = new CL_Y(n);
		CL_XX x = new CL_XX(n,m);
		CL_A z = new CL_A(n,m,f);
		//	
		
		for(int i=0;i<n;i++) {
			x.x[0][i] = 1.0;
			x.x[1][i] = (double)i+1;
			x.x[2][i] = (double)i-1;
			x.x[3][i] = (double)i * 0.1;
			y.y[i] = 10.0 + 2.0 * x.x[1][i] + 3.0 * x.x[2][i];
		}
		y.calc();
		x.calc();
		System.out.println(y.toPrint());
		System.out.println(x.toPrint(0));
		System.out.println(x.toPrint(1));
		System.out.println(x.toPrint(2));
		System.out.println(x.toPrint(3));

	}
	
			
}
