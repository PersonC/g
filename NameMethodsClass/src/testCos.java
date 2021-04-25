
public class testCos {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double a;
		a = Math.acos(2);
		System.out.println(a);
		for(double i=-1;i<=1.04;i=i+0.05) {
			a = Math.acos(i)*180/Math.PI;
			System.out.println(i + " " + a);
			
		}

	}

}
