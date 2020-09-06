import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

public class a1 {

	public static void main(String[] args) {

		System.out.println("==Constructor=====");
		for(Constructor cons: ArrayList.class.getDeclaredConstructors()) {
			System.out.println(cons);
		}
		System.out.println("==Declared Methods=====");
		for(Method method: ArrayList.class.getDeclaredMethods()) {
			System.out.println(method);
		}
		
		System.out.println("==Methods=====");
		for(Method method: ArrayList.class.getMethods()) {
			System.out.println(method);
		}
		System.out.println("===fields====");
		for(Field f: ArrayList.class.getFields()) {
			System.out.println(f);
		}
		System.out.println("=== declared fields====");
		for(Field f: ArrayList.class.getDeclaredFields()) {
			System.out.println(f);
		}
	}
}
