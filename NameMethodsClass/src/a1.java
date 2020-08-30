import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

public class a1 {

	public static void main(String[] args) {
		for(Method method: Object.class.getMethods()) {
			System.out.println(method);
		}
		System.out.println("==Methods=====");
		for(Method method: Class.class.getMethods()) {
			System.out.println(method);
		}
		System.out.println("==Declared Methods=====");
		for(Method method: Class.class.getDeclaredMethods()) {
			System.out.println(method);
		}
		System.out.println("==Constructor=====");
		for(Constructor cons: Calendar.class.getDeclaredConstructors()) {
			System.out.println(cons);
		}
		System.out.println("===fields====");
		for(Field f: Calendar.class.getFields()) {
			System.out.println(f);
		}
		System.out.println("=== declared fields====");
		for(Field f: Calendar.class.getDeclaredFields()) {
			System.out.println(f);
		}
	}
}
