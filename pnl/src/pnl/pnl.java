package pnl;

public class pnl {
	
//	static final double EPS = 1e-15;
    private static final long MEGABYTE = 1024L * 1024L;
    private static final long KILOBYTE = 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public static long bytesToKilobytes(long bytes) {
        return bytes / KILOBYTE;
    }
    
    public static void printBytes(Runtime runtime) {
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        if (memory < MEGABYTE) {
            System.out.println("Used memory is bytes: " + memory + ". " + "Used memory is kilobytes: "
                + bytesToKilobytes(memory));
        } else {
            System.out.println("Used memory is bytes: " + memory + ". " + "Used memory is megabytes: "
                    + bytesToMegabytes(memory));
        }
    }
    
	public static void main(String[] args) {
		// Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        printBytes(runtime);
        
		int n = 20;
		int m = 4;
		int f = 3;
		//
		Xy x0 = new Xy(n,m);
		x0.test_data();
		x0.calc();

        printBytes(runtime);
		
		System.out.println(x0.toPrint());
		System.out.println(x0.toPrint(0));
		System.out.println(x0.toPrint(1));
		System.out.println(x0.toPrint(2));
		System.out.println(x0.toPrint(3));
		//
		int na=12, nb=6, nc=2;
		CL_I ind = new CL_I(n, na, nb, nc);
		x0.set(ind);
		//
		Z z = new Z(x0,f);
		z.z0.set_zero_step(x0);
		z.z0.toPrint("z");
//
		z.set_next_step(x0);
		z.z0.toPrintCr();
		z.z0.toPrint("z1");

		z.set_next_step(x0);
		z.z0.toPrintCr();
		z.z0.toPrint("z1");
//		
		z.za.set_zero_step(x0);
		z.za.toPrint("za");
		

        printBytes(runtime);
		
	}
}
