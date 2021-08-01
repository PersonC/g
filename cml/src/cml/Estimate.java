package cml;

public class Estimate implements IF_LSM {
	public double seps = 0; // (y-ym)^2
	
	public void calcEps2(zModel z) {
		double[] RSS = new double[z.f], TSS = new double[z.f],
				 R2  = new double[z.f], DWS = new double[z.f];
		double ymi, yt, ye, ymi_1;
		int N1 = z.y.n;
		int N2 = (z.yd == null) ? 0 : z.yd.n;
		int N3 = (z.yc == null) ? 0 : z.yc.n;
		
		String sRSS = "", sTSS = "", sR2 = "", sDW = "";
		
		for (int l=0,ll=z.m1; l < z.f; l++,ll++) {
			RSS[l] = 0; TSS[l] = 0; DWS[l] = 0;
			ymi_1 = z.y.v[0];
			for (int i=0; i<N1; i++) {
				ymi = z.y.v[i];
				yt  = (ymi - z.y.vAverage);
				for (int j=0; j<z.m1; j++) { // coefficient [m1][mxz]
					ymi -= z.a[j][ll]*z.z[j].v[i];
				}
				ye = (ymi - ymi_1); ymi_1 = ymi;
				RSS[l] += ymi*ymi;
				TSS[l] += yt * yt;
				DWS[l] += ye * ye;
			}
			for (int i=0; i<N2; i++) {
				ymi = z.yd.v[i];
				yt  = (ymi - z.yd.vAverage);
				for (int j=0; j<z.m1; j++) { // coefficient [m1][mxz]
					ymi -= z.a[j][ll]*z.zd[j].v[i];
				}
				ye = (ymi - ymi_1); ymi_1 = ymi;
				RSS[l] += ymi*ymi;
				TSS[l] += yt * yt; 
				DWS[l] += ye * ye;
			}
			for (int i=0; i<N3; i++) {
				ymi = z.yc.v[i];
				yt  = (ymi - z.yc.vAverage);
				for (int j=0; j<z.m1; j++) { // coefficient [m1][mxz]
					ymi -= z.a[j][ll]*z.zc[j].v[i];
				}
				ye = (ymi - ymi_1); ymi_1 = ymi;
				RSS[l] += ymi*ymi;
				TSS[l] += yt * yt; 
				DWS[l] += ye * ye;
			}
			R2[l] = 1 - RSS[l] / TSS[l];
			DWS[l] = DWS[l]/RSS[l];
			sRSS = sRSS + l + " " + RSS[l] + " ";
			sTSS = sTSS + l + " " + TSS[l] + " ";
			sR2  = sR2  + l + " " + R2[l]  + " ";
			sDW  = sDW  + l + " " + DWS[l] + " ";
		}
		System.out.println("RSS:" + sRSS);
		System.out.println("TSS:" + sTSS);
		System.out.println("R2:" + sR2);
		System.out.println("DW:" + sDW);
	}

}
