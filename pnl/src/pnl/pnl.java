package pnl;

public class pnl {

	public static void main(String[] args) {
		//
		int n = 20;
		int m = 4;
		int f = 3;
		//
		Y y0 = new Y(n);
		X x0 = new X(n,m);
		
		for(int i=0; i<n; i++) {
			x0.x[0][i] = 1.0;
			x0.x[1][i] = (double)i+1;
			x0.x[2][i] = (double)i-1;
			x0.x[3][i] = (double)i * 0.1;
			y0.y[i] = 10.0 + 2.0 * x0.x[1][i] + 3.0 * x0.x[2][i];
		}
		y0.calc();
		x0.calc();
		System.out.println(y0.toPrint());
		System.out.println(x0.toPrint(0));
		System.out.println(x0.toPrint(1));
		System.out.println(x0.toPrint(2));
		System.out.println(x0.toPrint(3));
		//
		int na=12, nb=6, nc=2;
		CL_I ind = new CL_I(n, na, nb, nc);
		ind.set_indices();
		//
		Y ya = new Y(na);
		X xa = new X(na,m);
		//
		Y yb = new Y(nb);
		Y yc = new Y(nc);
		X xb = new X(nb,m);
		X xc = new X(nc,m);
		ind.set_a(ya, xa, y0, x0);
		ya.calc();
		xa.calc();
		ind.set_b(yb, xb, y0, x0);
		yb.calc();
		xb.calc();
		ind.set_c(yc, xc, y0, x0);
		yc.calc();
		xc.calc();
        //
		Z z = new Z(n,m,f);
		z.set_zero_step(y0, x0);
		z.toPrint("z");
//
		Z za = new Z(na,m,f);
		za.set_zero_step(ya, xa);
		za.toPrint("za");
		//
		Z zb = new Z(nb,m,f);
		zb.set_zero_step(yb, xb);
		zb.toPrint("zb");
		//
		Z zc = new Z(nc,m,f);
		zc.set_zero_step(yc, xc);
		zc.toPrint("zc");
		
	}
}
