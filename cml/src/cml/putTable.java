package cml;
import javax.swing.*;

public class putTable {
	
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
		JFrame frame = new JFrame("Sample data table");
		frame.add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,600);
		frame.setVisible(true);
	}
	
	public putTable(zModel z, int c) {
		int ncol = z.m + 1 + 1 + 1;
		
		int nrow = z.f;
		String[] headings = new String[ncol];
		for (int i=0;i<=z.m;i++) { headings[i] = "a" + i; }
		headings[ncol-2] = "CR";
		headings[ncol-1] = "L";
		Object[][] data = new Object[nrow][ncol];

		for (int j=0;j<z.f;j++) {
			for (int k=0;k<=z.m;k++) {
				data[j][k] = z.a[k][j+z.m1];
			}
			data[j][z.m+1] = z.cr[j];
			data[j][z.m+2] = z.L[j];
		}
		
		JTable table = new JTable(data,headings);
		JFrame frame = new JFrame("Coefficient");
		frame.add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,700);
		frame.setVisible(true);
	}


}
