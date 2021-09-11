package cml;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CmlUI extends JFrame 
                   implements IF_LSM, ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3253759880173195473L;
	final static String LOOKANDFEEL = null;
	JFrame frame;
	JMenuBar menuBar;
	
	private CmlUI Ithis;
	JMenu menu, submenu;
	JMenuItem[] menuItem = new JMenuItem[5];
//	ButtonGroup group;
//	JRadioButtonMenuItem rbMenuItem[] = new JRadioButtonMenuItem[2];
	int GMDHlength = GMDH.values().length;
	JCheckBoxMenuItem cbMenuItem[] = new JCheckBoxMenuItem[GMDHlength];
	
	JTabbedPane tabbedPane;
	
	JCheckBoxMenuItem checkBoxTypeOutput;
	
	public CmlUI() {
		Ithis = this;
		initLookAndFeel();
		this.frame = new JFrame("CML");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.menuBar = new JMenuBar();
		menu = new JMenu("Параметры");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItem[0] = new JMenuItem("Запуск");
		menuItem[0].addActionListener(
		new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cml.runTest(Ithis);
			}
		});
		menu.add(menuItem[0]);

		menuItem[1] = new JMenuItem("Расчет");
		menuItem[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cml.run(Ithis);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		menu.add(menuItem[1]);

		menu.addSeparator();

		//a group of radio button menu items
//		group = new ButtonGroup();
//		rbMenuItem[0] = new JRadioButtonMenuItem("Расчет модели");
//		rbMenuItem[0].addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					System.out.println("Расчет модели");
//				}
//			}
//		});
//		group.add(rbMenuItem[0]);
//		menu.add(rbMenuItem[0]);

//		rbMenuItem[1] = new JRadioButtonMenuItem("Тестовый режим");
//		rbMenuItem[1].addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					System.out.println("Тестовый режим");
//				}
//			}
//		});
//		rbMenuItem[1].setSelected(true);
//		group.add(rbMenuItem[1]);
//		menu.add(rbMenuItem[1]);
        // end group of radio button

//		menu.addSeparator();

		//a group of check box menu items
		for (int i=0; i<GMDHlength; i++) {
			
			cbMenuItem[i] = new JCheckBoxMenuItem(GMDH.getModel(i));
			cbMenuItem[i].setName(GMDH.getGMDH(i) );
			if (i==0) cbMenuItem[i].setSelected(true);
			cbMenuItem[i].addItemListener(this);
			menu.add(cbMenuItem[i]);
		}

		menu.addSeparator();

		//a submenu
		submenu = new JMenu("Управление закладками");

		menuItem[3] = new JMenuItem("Убрать закладки");
		submenu.add(menuItem[3]);

		menuItem[4] = new JMenuItem("Прочее...");
		submenu.add(menuItem[4]);
		menu.add(submenu);

		//Build second menu in the menu bar.
		menu = new JMenu("Вывод");
		checkBoxTypeOutput = new JCheckBoxMenuItem("Вывод в текстовый файл");
		checkBoxTypeOutput.setSelected(true);
		checkBoxTypeOutput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean c = ((JCheckBoxMenuItem) e.getSource()).getState();
				cml.setPRFILE(c);
				if(c) System.setOut(cml.fileOut); else System.setOut(cml.standartOut);
			}
		});
		
		menu.add(checkBoxTypeOutput);
		menuBar.add(menu);

		frame.setJMenuBar(menuBar);

//=========================================================		
		this.tabbedPane = new JTabbedPane();
		tabbedPane.addTab("START", null, new RadioRound(), "Configuration");
//=========================================================
		frame.add(tabbedPane);
//		statusbar = new JLabel("Easy");
//		statusbar.setBorder(BorderFactory.createEtchedBorder());
//		add(statusbar, BorderLayout.SOUTH);
		frame.setSize(1600,600);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void putTable(zModel z) {
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
//===============================================		
		JTable table = new JTable(data,headings);
		table.setFillsViewportHeight(true);
		tabbedPane.addTab("DATA", null, new JScrollPane(table), "DATA");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		frame.pack();
	}
	
	public void putTable(zModel z, int c) {
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
		table.setFillsViewportHeight(true);
		tabbedPane.addTab("RESULT", null, new JScrollPane(table), "Model");
		frame.setSize(1600,600);
		frame.pack();
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

	@Override
	public void itemStateChanged(ItemEvent e) {
		int h = e.getItemSelectable().hashCode();
		System.out.println("Select " + e.getItemSelectable() + "\n" +
				"state=" + ((JCheckBoxMenuItem) e.getItemSelectable()).getState() +
				"\nhash " + h +
				"\nName = " + ((JCheckBoxMenuItem) e.getItemSelectable()).getName()
				);
		
		System.out.println(" s= " + e.getSource());
		System.out.println("getStateChange=" + e.getStateChange());
		System.out.println("getItem.getClass.getName= \n" + e.getItem().getClass().getName());
		System.out.println("getItem.getClass.to String= \n" + e.getItem().getClass().toString());
	}
//==============================================================================
	
    private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }
	
//=== class ==	
public class RadioRound extends JPanel implements ActionListener {
String first = "Стандартное округление";
String last = "Отбросить последние цифры";
String notRound = "Без округления";
JPanel jp;

public RadioRound() {
//super(new BorderLayout());
//this.jp = jp;
Box box = Box.createVerticalBox();
box.add(new JLabel("Округление коэффициентов"));

//Create the radio buttons.
JRadioButton firstButton = new JRadioButton(first);
firstButton.setActionCommand(first);
firstButton.setSelected(true);

JRadioButton lastButton = new JRadioButton(last);
lastButton.setActionCommand(last);

JRadioButton notButton = new JRadioButton(notRound);
notButton.setActionCommand(notRound);

//Group the radio buttons.
ButtonGroup group1 = new ButtonGroup();
group1.add(firstButton);
group1.add(lastButton);
group1.add(notButton);


//Register a listener for the radio buttons.
firstButton.addActionListener(this);
lastButton.addActionListener(this);
notButton.addActionListener(this);

//Put the radio buttons in a column in a panel.
JPanel r1 = new JPanel();

r1.add(firstButton);
r1.add(lastButton);
r1.add(notButton);

//setBorder(BorderFactory.createEmptyBorder());

//add(r1, BorderLayout.AFTER_LAST_LINE);
box.add(r1);
add(box);
}

/** Listens to the radio buttons. */
public void actionPerformed(ActionEvent e) {
	System.out.println(e.toString());
}	
//==============================================================================	
	
}
	
}