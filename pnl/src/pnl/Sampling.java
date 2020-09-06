package pnl;

import java.util.ArrayList;

public class Sampling {
	public int m,f;
	ArrayList<X> y = new ArrayList<X>(1);
	ArrayList<X> x = new ArrayList<X>();
	ArrayList<X> z = new ArrayList<X>();
	public int NA, NB, NC;
	public int[] i_NA, i_NB, i_NC;
	public int n;
	
	public Sampling(int m,int f) {
		this.m = m; 
		if(f>m+1 || f<=0) this.f = m+1; else this.f =f;
		x.ensureCapacity(m);
		z.ensureCapacity(f);
	}
	
	public void set_n() {
		this.n=y.size();
	}
	
	public void set_nabc(int NA, int NB, int NC) {
		this.NA = NA;
		this.NB = NB;
		this.NC = NC; 
		if(NA<=0 || NA>n) 
		{
			this.NA = n;
			this.NB = 0;
			this.NC = 0; 
		} else {
			if(NB<=0 || NA+NB>n) {
				this.NB = n-NA;
				this.NC = 0;
		    	} else {
			    	if(NC<=0 || NA+NB+NC>n) this.NC=n-NA-NB;
     			       }
    		}
		}
	
	public void set_index_nabc() {
		this.i_NA = new int[NA];
		for (int i=0; i<NA; i++) { i_NA[i]=i; }
		if(NB>0) {
			this.i_NB = new int[NB];
			for (int i=NA; i<NA+NB;i++) { i_NB[i]=i; }
		}	
		if(NC>0) {
			this.i_NC = new int[NC];
			for (int i=NA+NB; i<n;i++) { i_NC[i]=i; }
		}
	}
		
    	

}
