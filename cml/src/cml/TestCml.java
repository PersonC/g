package cml;

public class TestCml  implements IF_LSM  {
	public zModel z;
	int na =12, nb =12, nc   = 5;
	
	public TestCml(zModel z, int na, int nb, int nc) {
		this.z = z;
		this.na = na; this.nb=nb; this.nc = nc;
		utilityTest(na, nb, nc, z.m);
	}
	public TestCml(zModel z) {
		this.z = z;
		if (z.crit == GMDH.LSM) {
			na = na + nb + nc; nb = 0; nc = 0;
			utilityTest(na, z.m);
		} else utilityTest(na, nb, nc, z.m);
	}
	
	public void test(int alg, double A, double istart, MathVector vector) {
		// 0 - константа
		// 1 - случайное число * А
		// 2 - линейная функция х
		// 3 - линейная фунция * А * случайное число
		// 4 - ехр ( А * случайное число )
		// 5 - cos ( А * случайное число )
		// 6... - 100 * случайное число * случайное число
		int n = vector.n;
		vector.artifical = true;
				switch (alg) {
				case (1):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = A * ((double) i + istart);	
						vector.iv=alg; }
					break;
				case (2):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = A * Math.exp(- (i + istart) * 0.2); 
						vector.iv=alg; }
					break;
				case (3):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = Math.abs(A)+ A * 1/Math.sqrt(i+0.1); 
						vector.iv=alg; }
					break;
				case (4):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = A * (double) i * (double) i; 
						vector.iv=alg; }
					break;
				case (5):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = A * Math.cos( A * (double) i *0.15 ); 
						vector.iv=alg; }
					break;
				case (6):
					for (int i = 0; i < n; i++) { 
						vector.v[i] = -A * Math.log(Math.abs(A) + i +1) / (double) (A+i-9.5); 
						vector.iv=alg;	}
					break;
					
				default:
					for (int i = 0; i < n; i++) { 
						vector.v[i] = A * i * Math.random() * Math.random(); 
						vector.iv=alg; }
					break;
				}
				try { vector.valuation(); }catch (Exception e) {
					// TODO: handle exception
				}
			}
	
	//=========================================================================
		public void utilityTest(int n, int m) {
			if (n<1 ) {
				System.out.println("Не верный объем обучающей выборки " + n);
				return;
			}
			z.y = new MathVector(n,-1); z.y.oneVector(2);
			// единичный вектор с валидацией
			z.z[0] = new MathVector(n,0); z.z[0].oneVector();
			// нетривиальные факторы
			for (int j=1; j<=m; j++) {
				z.z[j] = new MathVector(n,j);
				test(j, 3, 0, z.z[j]);
				for (int i=0; i<z.y.n; i++) z.y.v[i]+=z.z[j].v[i]*(double)j;
			}
			try { z.y.valuation(); }catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		public void utilityTest(int n, int nd, int m) {
			if (nd<1) {
				System.out.println("Не задан объем проверочной выборки " + nd);
				return;
			}
			utilityTest(n,m);
			z.yd = new MathVector(nd,-1); z.yd.oneVector(2);
			// единичный вектор с валидацией
			z.zd[0] = new MathVector(nd,0); z.zd[0].oneVector();
			// нетривиальные факторы
			for (int j=1; j<=m; j++) {
				z.zd[j] = new MathVector(nd,j);
				test(j, 3, n, z.zd[j]);
				for (int i=0; i<z.yd.n; i++) z.yd.v[i]+=z.zd[j].v[i]*(double)j;
			}
			try { z.yd.valuation(); } catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		public void utilityTest(int n, int nd, int nc, int m) {
			if (nc<1) {
				System.out.println("Не верный объем экзаменационной выборки " + nc);
				return;
			}
			utilityTest(n,nd, m);
			z.yc = new MathVector(nc,-1); z.yc.oneVector(2);
			// единичный вектор с валидацией
			z.zc[0] = new MathVector(nc,0); z.zc[0].oneVector();
			// нетривиальные факторы
			for (int j=1; j<=m; j++) {
				z.zc[j] = new MathVector(nc,j);
				test(j, 3, n+nd, z.zc[j]);
				for (int i=0; i<z.yc.n; i++) z.yc.v[i]+=z.zc[j].v[i]*(double)j;
			}
			try { z.yc.valuation(); } catch (Exception e) {
				// TODO: handle exception
			}
		}

}
