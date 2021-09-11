package cml;
import javax.swing.*;
import java.awt.*;

public class putTable extends JFrame{
	JFrame frame;
	
	
	public putTable() {
		this.frame = (JFrame) getFrames()[0];
		
		
	}
	
	public putTable(zModel z) {
		int ncol = z.m + 1;
		int nrow = z.y.n + 
				(z.yd == null ? 0 : z.yd.n) + 
				(z.yc == null ? 0 : z.yc.n) + 2;
		
		String[] headings = new String[ncol];
		headings[0] = "y";
		for (int i=1;i<ncol;i++) { headings[i] = "x" + i; }
		Object[][] data = new Object[nrow][ncol];

		int j0 = 0, j1 = z.y.n;
		
		for (int j=j0;j<j1;j++) {
			data[j][0] = z.y.v[j];
			for (int i=1;i<ncol;i++) { data[j][i] = z.z[i].v[j]; }
		}
		
		if (z.yd != null) {
			j0 = j1 + 1; j1 = j0 + z.yd.n; 
			for (int j=j0,jj = 0;j<j1;j++,jj++) {
				data[j][0] = z.yd.v[jj];
				for (int i=1;i<ncol;i++) { data[j][i] = z.zd[i].v[jj]; }
			}
			
			if (z.yc != null) {
				j0 = j1 + 1; j1 = j0 + z.yc.n; 
				for (int j=j0,jj = 0;j<j1;j++,jj++) {
					data[j][0] = z.yc.v[jj];
					for (int i=1;i<ncol;i++) { data[j][i] = z.zc[i].v[jj]; }
				}
			}
		}
		
		JTable table = new JTable(data,headings);
		JFrame frame = (JFrame) getFrames()[0];
//		JFrame frame = new JFrame("Sample data table");
		frame.add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,600);
		frame.setVisible(true);
	}
	
	public putTable(zModel z, int c) {
		int ncol = z.m + 1 + 1 + 1;
		int nAddCol = 5;
		
		int nrow = z.f;
		String[] headings = new String[ncol + nAddCol];
		for (int i=0;i<=z.m;i++) { headings[i] = "a" + i; }
		headings[ncol-2] = "CR";
		headings[ncol-1] = "L";
		
		headings[ncol  ] = "CR_a";
		headings[ncol+1] = "CR_b";
		headings[ncol+2] = "CR_c";
		headings[ncol+3] = "CR_a+b";
		headings[ncol+4] = "CR_a+b+c";
		
		Object[][] data = new Object[nrow][ncol + nAddCol];
        
		double cra, crb, crc, crab, crabc;
		int na = (z.y  == null) ? 0 : z.y.n,
			nb = (z.yd  == null) ? 0 : z.yd.n,
			nc = (z.yc  == null) ? 0 : z.yc.n;
		
		for (int j=0;j<z.f;j++) {
			
			for (int k=0;k<=z.m;k++) {
				data[j][k] = z.a[k][j+z.m1];
			}
			data[j][z.m+1] = z.cr[j];
			data[j][z.m+2] = z.L[j];
			// cra...cR_a+b+c
			cra = CR(z, j, "A");
			data[j][z.m+3] = (cra == 0) ? 0 : cra /  na;

			crb = CR(z, j, "B");
			data[j][z.m+4] = (crb == 0) ? 0 : crb /  nb;

			crc = CR(z, j, "C");
			data[j][z.m+5] = (crc == 0) ? 0 : crc /  nc;

			crab = cra + crb;
			data[j][z.m+6] = (crab == 0) ? 0 : crab / (na + nb); 
			
			crabc = cra + crb + crc;
			data[j][z.m+7] = (crabc == 0) ? 0 : crabc / (na + nb + nc);  
			
		}
		
		JTable table = new JTable(data,headings);
		JFrame frame = new JFrame("Coefficient");
		frame.add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,700);
		frame.setVisible(true);
	}
	
	public double CR(zModel z, int j, String outset) {
		double cr = 0, ymi = 0;
		int n, m = z.m, mz = j + z.m1;
		switch (outset) {
		case "C":
			if (z.yc == null) break;
			if (z.yc.n <=0) break;
			n = z.yc.n;
			for (int i=0; i<n; i++) {
				// y модели
				ymi = 0;
				for (int jm=0; jm<=m; jm++) {
					ymi += z.a[jm][mz] * z.zc[jm].v[i];
				}
				cr += Math.pow(z.yc.v[i] - ymi,2);
			}
			break;
		case "B":
			if (z.yd == null) break;
			if (z.yd.n <=0) break;
			n = z.yd.n;
			for (int i=0; i<n; i++) {
				// y модели
				ymi = 0;
				for (int jm=0; jm<=m; jm++) {
					ymi += z.a[jm][mz] * z.zd[jm].v[i];
				}
				cr += Math.pow(z.yd.v[i] - ymi,2);
			}
			break;
		case "A":
		default:
			if (z.y == null) break;
			if (z.y.n <=0) break;
			n = z.y.n;
			for (int i=0; i<n; i++) {
				// y модели
				ymi = 0;
				for (int jm=0; jm<=m; jm++) {
					ymi += z.a[jm][mz] * z.z[jm].v[i];
				}
				cr += Math.pow(z.y.v[i] - ymi,2);
			}
			break;
		}
		return cr;
	}


}
