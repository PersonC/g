package cml;

public class cml {

	public static void main(String[] args) {
		int n = 12;
		MathVector y = new MathVector(n,-1),
				   e = new MathVector(n, 0),
				   x = new MathVector(n, 1);
		y.test(1, 1); e.test(0, 1); x.test(2, 1);
		

	}

}

