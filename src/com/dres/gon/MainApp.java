package com.dres.gon;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class MainApp extends JApplet {
	private static final long serialVersionUID = 1L;

	public void init(){
		Preset.vRAMCache = false;
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    add(new TheGon());
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
	}
}
