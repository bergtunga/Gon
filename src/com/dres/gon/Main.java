package com.dres.gon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.dres.grpx.FullScreenJFrame;
import com.dres.util.ImageLoader;


public class Main {
	public static void main(String... argv){
		//System.out.println(Converter.toCodeString(Converter.convert("")));
		//if(true) return;
		//System.out.println(argv);
		if(argv!= null)
			for (String s : argv)
				if (s.equalsIgnoreCase("-RAM"))
					Preset.vRAMCache = false;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				File f = new File(System.getProperty("user.home") + File.separator + "ignoreSeizureWarning");
				if(!f.exists()){
					String[] opts;
					if(f.getParentFile().canWrite())
						opts = new String[]{"Do not display again", "Continue", "Exit"};
					else
						opts = new String[]{"Continue", "Exit"};
					int i = JOptionPane.showOptionDialog(null,
						"IMPORTANT NOTICE!\n"
						+ " Changing settings could cause seziures in those prone to problems.\n"
						+ " The author of this program is not responsible for any issues\n"
						+ " arising from its use. Use at your own risk.",
						"Seizure Warning", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, opts, opts[0]);
					if(opts[i].equals("Exit"))
						return;
					else if(opts[i].equals("Do not display again"))
						try {
							f.createNewFile();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Problem creating file!");
						}
				}
				createAndShowGUI();
			}
		});

	}
	static JMenuItem back;
	static FullScreenJFrame x;
	public static void createAndShowGUI() {
		x = new FullScreenJFrame();
		x.setTitle("HexaGonWild");
		System.out.println(x.getParent());
		final TheGon tg = new TheGon();
		x.addWindowStateListener(new WindowStateListener(){
			@Override
			public void windowStateChanged(WindowEvent e) {
				if(!x.isReturning()){
				int i = e.getNewState();
				if(i == (i & JFrame.MAXIMIZED_BOTH))
					tg.addMenuItem(back);
				}
			}});
		back = new JMenuItem("Return");
		back.addActionListener(x.getBackListener());
		back.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tg.removeMenuItem(back);
			}});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				x.dispose();
				System.exit(0);
			}});
		tg.addMenuItem(exit);
		x.setIconImage(ImageLoader.load("/hexa.JPG"));
		x.add(tg);
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.pack();
		x.setVisible(true);
	}
}
