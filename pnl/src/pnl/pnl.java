package pnl;
import java.lang.instrument.Instrumentation;

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
		
		for(int i=0; i<n; i++) {
			x0.x[0][i] = 1.0;
			x0.x[1][i] = (double)i+1;
			x0.x[2][i] = (double)i-1;
			x0.x[3][i] = (double)i * 0.1;
			x0.y[i] = 10.0 + 2.0 * x0.x[1][i] + 3.0 * x0.x[2][i];
		}
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
		ind.set_indices();
		x0.set(ind);
		//
		Xy xa = new Xy(na,m);
		Xy xb = new Xy(nb,m);
		Xy xc = new Xy(nc,m);
		//
		ind.set_a(xa, x0);
		xa.calc();
		ind.set_b(xb, x0);
		xb.calc();
		ind.set_c(xc, x0);
		xc.calc();
        //
		Z z = new Z(n,m,f);
		z.set_zero_step(x0);
		z.toPrint("z");
//
/*		Z za = new Z(na,m,f);
		za.set_zero_step(ya, xa);
		za.toPrint("za");
		//
		Z zb = new Z(nb,m,f);
		zb.set_zero_step(yb, xb);
		zb.toPrint("zb");
		//
		Z zc = new Z(nc,m,f);
		zc.set_zero_step(yc, xc);
		zc.toPrint("zc");*/
		//
		z.set_next_step(x0);
		z.toPrintCr();
		z.toPrint("z1");

		z.set_next_step(x0);
		z.toPrintCr();
		z.toPrint("z1");

        printBytes(runtime);
		
	}
}
