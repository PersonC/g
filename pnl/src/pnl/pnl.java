package pnl;

public class pnl {

	public static void main(String[] args) {
		int n = 10;
		X x = new X(n);
		for(int i=0;i<n;i++) {
			x.x[i] = (double)i+1;
		}
		x.Xm();
		System.out.println(x.xm);
		for(int i=0;i<n;i++) {
			System.out.println(x.x[i]);
		}
		x.sum_square();
		System.out.println(x.sx2);

	}

}
