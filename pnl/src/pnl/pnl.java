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
		int na=12, nb=6, nc=2;
		x0.set_sampling0(na, nb, nc);
		x0.calc();

/*		System.out.println(x0.toPrint());
		System.out.println(x0.toPrint(0));
		System.out.println(x0.toPrint(1));
		System.out.println(x0.toPrint(2));
		System.out.println(x0.toPrint(3));*/
		//
		Z z = new Z(x0,f);
		z.z0.set_zero_step();
		z.z0.toPrint("z");
//
		if(z.set_next_step()) {
			z.z0.toPrintCr();
			z.z0.toPrint("z1");
		} else {System.out.println("======== END 1 ==========");}

		if(z.set_next_step()) {
			z.z0.toPrintCr();
			z.z0.toPrint("z1");
		} else {System.out.println("======== END 2 ==========");}
//		
		z.za.set_zero_step();
		z.za.toPrint("za");
		z.zb.set_zero_step();
		z.zb.toPrint("zb");

        printBytes(runtime);
		
	}
}
