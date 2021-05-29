import java.math.BigDecimal;

public class testCos {
	
	public static double roundAvoid(double value, int places) {
	    double scale = Math.pow(10, places);
	    return Math.round(value * scale) / scale;
	}	
	
	public static double roundHalf(double value, int places) {
	    double scale = Math.pow(10, places);
	    return  Math.floor(value * scale +.5)/scale;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double a;
		int scale = 3;
		double b1, b2;
		for(double i=-1; i<=1.04;i=i+0.05) {
//			i =(double) Math.round(i * 1000) / 1000;
			a = Math.acos(i)*180/Math.PI;
			b1 = roundAvoid(a,scale);
			b2 = roundHalf(a,scale);
			System.out.println(i + " " + a + " " + Math.ulp(i) + " " +
			b1 + " " + b2 + " " + (b1-b2));
			for (int s=1; s<15; s++) {
				double c2 = roundHalf(-a,s);
				double c1 = roundAvoid(-a,s);
				System.out.println(c1 + "-" + c2 + "=" + (c2-c1) );
			}
			
		}

	}

}
