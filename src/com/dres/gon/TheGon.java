package com.dres.gon;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.dres.util.ImageLoader;

class TheGon extends JPanel{
	/**
	 * 
	 */
	
	public static final double PI = Math.PI,  SQRT3 = Math.sqrt(3);
	
	private static final long serialVersionUID = 1L;
	private Cursor blankCursor;
	private Polygon[] preCalcs;
	private TileMethod tiling;
	
	private Preset settings;
	private Timer cursorTimer, mainTimer;
	private SelectionBar selectionBar;
	private JScrollPane selectionBarScroll;
	private JPopupMenu jp;
	private boolean willHide = false;
	private int countdownTick = 0, tick = 0;
	private JMenuItem disable;
	private String secretString = "";
	private PolyAtRot[] tst;
	private byte[] secret = new byte[]{-31, -29, -26, 124, -18, -122, -114, -22, 26, -98, 114, 117, 9, 72, -3, -45};
	private transient Image img;
	private GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	public TheGon(){
		this(new Preset());
	}
	public TheGon(Preset p){
		super(new BorderLayout());
		settings = p;
		p.caches(true);
		tiling = new HexagonTile(this, settings);
		//tiling = new SquareTile(this, settings);
		selectionBar = new SelectionBar(this, p);
		final int wait = 1000;
		final Timer ta = new Timer(15, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Point p = getMousePosition();
				boolean b = p == null? false : selectionBarScroll.getBounds().contains(p);
				if(b && willHide){
					willHide = false;
					selectionBarScroll.setVisible(true);
					setCursor(Cursor.getDefaultCursor());
				}else if(!willHide && !b ){
					if(countdownTick > wait){
						selectionBarScroll.setVisible(false);
						cursorTimer.restart();
						willHide = true;
					}else{
						countdownTick += 15;
					}
				}else{
					countdownTick = 0;
				}
			}});
		JScrollPane jspa = selectionBarScroll = new JScrollPane(selectionBar,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(jspa, BorderLayout.WEST);
		
		jp = new JPopupMenu(){
			@Override
			public void show(Component invoker,int x, int y){
				Dimension d = getPreferredSize();
				int w = d.width > this.getWidth() ? d.width : this.getWidth() ;
				int h = d.height > this.getHeight() ? d.height : this.getHeight() ;
				if(w + x > invoker.getWidth())
					x -= w;
				if(h + y > invoker.getHeight())
					y -= h;
				super.show(invoker, x, y);
			}
		};
		JMenuItem mi = new JMenuItem("Save");
		mi.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Preset toSave = settings.clone();
				Preset.save(toSave);
			}
		});
		jp.add(mi);
		mi = new JMenuItem("Open");
		mi.addActionListener(new ActionListener(){
			int i = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				Preset p = Preset.open();
				if(p == null)
					return;
				jp.insert(buildPreset(p.getName(), p), 6 + (i++));
				changeSettings(p);
			}
		});
		jp.add(mi);
		jp.addSeparator();
		
		
		jp.add(buildPreset("Default", Preset.preset1));
		disable = buildPreset("Super secret preset", Preset.preset2);
		jp.add(disable);
		//jp.add(buildPreset("OpticalIllusion", Preset.preset3));
		disable.setToolTipText("The Jewish One");
		disable.setVisible(false);
		
		jp.addSeparator();
		
		this.setPreferredSize(new Dimension(780,700));
		setBackground(Color.black);
		setFocusable(true);
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent arg0) {
				refreshSettingSize();
			}});
		mouseSetup();
		initCalcs();
		
		mainTimer = new Timer(settings.getLagReduction(), new ActionListener(){
			int delay = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				tick(settings.getLagReduction());
				if(delay != settings.getLagReduction())
					((Timer)e.getSource()).setDelay(delay = settings.getLagReduction());
				repaint();
			}});
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				secretCheck(e);
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				secretCheck(arg0);
				if(arg0.getKeyChar() == ' ')
					if(mainTimer.isRunning())
						mainTimer.stop();
					else
						mainTimer.start();
			}});
		mainTimer.start();
		ta.start();
		this.setComponentPopupMenu(jp);
	}
	/**
	 * Ya Know.
	 * @param e
	 */
	private void secretCheck(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_F8)
			secretString = "";
		else if(e.getKeyCode() == KeyEvent.VK_UNDEFINED && !disable.isVisible()){
			secretString += e.getKeyChar();
			if (Arrays.equals(getMD5(secretString), secret)) {
				disable.setVisible(true);
				/*Container c = this;
				while ((c = c.getParent()) != null) {
					if (c instanceof JFrame) {
						((JFrame) c).setTitle("");
						break;
					}
				}*/
				disable.doClick();
				mainTimer.start();
				JOptionPane.showMessageDialog(this, "I don't really care, but here's a Star of David.", "", JOptionPane.QUESTION_MESSAGE);
			}
		}
	}
	/**
	 * Does stuff
	 * @param strToBeEncrypted
	 * @return
	 */
	private static byte[] getMD5(String strToBeEncrypted) {
		try {
			return MessageDigest.getInstance("MD5")
					.digest(strToBeEncrypted.getBytes("UTF-8"));
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * ensures the selection bar on left is the correct size.
	 */
	private void refreshSettingSize(){
		int width = selectionBar.getWidth();
		if(selectionBarScroll.getVerticalScrollBar().isVisible())
			width +=  selectionBarScroll.getVerticalScrollBar().getWidth();
		Dimension d = new Dimension(width, this.getHeight());
		selectionBarScroll.setPreferredSize(d);
		selectionBarScroll.setSize(d);
	}
	/**
	 * removes from the popup menu
	 * @param jmi
	 */
	public void removeMenuItem(JMenuItem jmi){
		jp.remove(jmi);
		jp.pack();
	}
	/**
	 * Adds to the popup menu
	 * @param jmi
	 */
	public void addMenuItem(JMenuItem jmi){
		jp.add(jmi);
		jp.pack();
	}
	/**
	 * Sets up the mouse for going transparent if it's not used, & for opening the popup menu if rmb.
	 */
	private void mouseSetup(){
		// Transparent 16 x 16 pixel cursor image.
    	BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    	
    	// Create a new blank cursor.
    	blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
    	    cursorImg, new Point(0, 0), "blank cursor");
    	
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(SwingUtilities.isLeftMouseButton(e))
					setCursor(blankCursor);
			}
		});
		cursorTimer = new Timer(1000, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(willHide && !jp.isShowing())
		    		setCursor(blankCursor);
		    }
		});
		cursorTimer.start();
		
		addMouseMotionListener(new MouseMotionAdapter() {
		    @Override
		    public void mouseMoved(MouseEvent e) {
		        setCursor(Cursor.getDefaultCursor());
		        cursorTimer.restart();
		    }
		});
	}
	/**
	 * calculates all the polygons which may be necessary for drawing.
	 */
	public void initCalcs(){
		Polygon[] pa;
		if(preCalcs == null)
			pa = preCalcs = new Polygon[628+1];
		else
			pa = preCalcs;
		
		if(tst == null)
			tst = new PolyAtRot[628+1];
		
		PolyAtRot[] pr = tst;
		
		for(int i = 0; i < pa.length; i++){
			pa[i] = settings.getPolygon(i/100d);
			pr[i] = null;
		}
	}
	/**
	 * Creates a menu item for a preset. Used to let user easily select previously opened saves.
	 * @param name
	 * @param ps
	 * @return
	 */
	private JMenuItem buildPreset(String name,final Preset ps){
		JMenuItem mi = new JMenuItem(name);
		mi.setToolTipText(ps.toString());
		mi.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeSettings(ps);
			}
		});
		return mi;
	}
	@Override
	public void paintComponent(Graphics G){
		//long l = System.currentTimeMillis();
		Graphics2D g = (Graphics2D)G;
		super.paintComponent(g);
		if(settings.isAntiAliasing())
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(int i = 0; i < settings.getRows(); i ++){
			//Paint every row.
			tiling.doTiling(g, i);
		}
		
		
		if(settings.isAntiAliasing())
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		imageOverlay(g);
		/*
		for(int i = 0; i < 50; i++){
			if(i % 2 == 0)
				g.setColor(Color.WHITE);
			else
				g.setColor(Color.DARK_GRAY);
			g.drawOval(getWidth()/2 - i * 20, getHeight()/2 - i * 20, 40 * i, 40 * i);
		}
		
		if(redraw){
		String s = "Redraw:" + redraw;
		redraw = false;
		
		//Estimated FPS from paint.
		l = System.currentTimeMillis() - l;
		String s = Math.round(1000f / l ) + " FPS";
		int i = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.setColor(Color.WHITE);
		g.drawString(s, getWidth() - i - 5, 10);
		}*/
		
	}
	/**
	 * draws an image over top of the shapes.
	 * @param g
	 */
	private void imageOverlay(Graphics2D g){
		//g.fillRect(0, 0, getWidth(), getHeight());
		if(img == null)
			img = ImageLoader.load(settings.getBackgroundImage());
		if(img == null)
			return;
		Composite c = g.getComposite();
		float f = settings.alphaFor(tick);
		g.setComposite(AlphaComposite.SrcOver.derive(f));
		g.drawImage(img,(getWidth() - img.getWidth(this))/2, (getHeight() - img.getHeight(this))/2, this);
		g.setComposite(c);
	}
	/**
	 * Changes the settings to the passed in ones. The passed in settings will not change if newly visible ones are altered.
	 * @param ps
	 */
	public void changeSettings(Preset ps){
		settings = ps.clone();
		selectionBar.refresh(settings);
		tiling.settings(settings);
		tick = 0;
		img = null;
		initCalcs();
		repaint();
	}
	
	
	
	public int getTick(){
		return tick;
	}
	
	/**
	 * draws A single shape.
	 * @param g
	 * @param xCord
	 * @param yCord
	 * @param rot
	 */
	protected void drawShape(Graphics2D g, double xCord, double yCord, double rot, double upscaleFactor){
		int x = (int)((xCord + getWidth()/2) * upscaleFactor),y = (int)((yCord + getHeight()/2 )* upscaleFactor);
		//Determine which polygon to use
		int l = (int)Math.round(100 * rot);
		while(l < 0)
			l += 628;
		while(l >= 628)
			l -= 628;
		if(upscaleFactor < 1 && upscaleFactor > -1)
			throw new UnsupportedOperationException(" |upscaleFactor| must be >= 1");
		
		boolean caches = settings.caches();
		//If it needs to reset the cache
		if(settings.update()){
			for(int i = 0; i < tst.length; i++){
				tst[i] = null;
			}
		}
		
		if(!caches){ //If it doesn't cache, draw the polygon where it should appear
			Polygon p = preCalcs[l];
			g.setColor(settings.determineColor(rot));
			p.translate(x, y);
			g.scale(1/upscaleFactor, 1/upscaleFactor);
			g.fill(p);
			g.scale(upscaleFactor, upscaleFactor);
			p.translate(-x,-y);
		}else if(tst[l] == null){ //If the image needed isn't cached
			if(Preset.vRAM())
				(tst[l] = new PolyAtRotVolatile(preCalcs[l], settings.determineColor(rot))).draw(g, x, y);
			else{
				try{
					(tst[l] = new PolyAtRotBuffered(preCalcs[l], settings.determineColor(rot))).draw(g, x, y);
				}catch(OutOfMemoryError m){
					//If out of memory, release data, force caching off, notify user, and reset variables.
					tst = null;
					settings.caches(false);
					JOptionPane.showMessageDialog(null, "Unable to cache. Out of memory.\nTry running java with more memory.", "Caching error", JOptionPane.ERROR_MESSAGE);
					this.selectionBar.refresh();
				}
			}
		}else //If the image needed is already cached
			tst[l].draw(g, x, y);
		
	}
	/**
	 * Advances the time.
	 * @param i How much to advance.
	 */
	public void tick(int i) {
		tick += i;
		tick %= settings.getDuration();
	}
	public VolatileImage createVolatileImage(int width, int height){
		return gc.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);
	}
	
	
	abstract class PolyAtRot{
		private int xO, yO;
		private Polygon p;
		private Color c;
		Rectangle r;
		public PolyAtRot(Polygon p, Color c){
			r = p.getBounds();
			xO = r.x;
			yO = r.y;
			if(r.width == 0) r.width = 1;
			if(r.height == 0) r.height = 1;
			this.p = p;
			this.c = c;
		}
		protected void doForImage(Image i, Graphics2D g2){
			if(settings.isAntiAliasing())
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(c);
			p.translate(-r.x, -r.y);
			g2.fill(p);
			g2.dispose();
			p.translate(r.x, r.y);
		}
		public void draw(Graphics2D g2, int x, int y){
			g2.drawImage(getImage(), x+xO, y+yO, TheGon.this);
		}
		abstract Image getImage();
	}
	/**
	 * Wrapper class for VolatileImage, to buffer & assist w/ drawing.
	 * @author Andrew
	 *
	 */
	class PolyAtRotVolatile extends PolyAtRot{
		VolatileImage bi;
		public PolyAtRotVolatile(Polygon p, Color c){
			super(p, c);
			rebuild();
			redraw();
		}
		private void rebuild(){
			bi = createVolatileImage(r.width, r.height);
			bi.validate(gc);
		}
		private void redraw(){
			System.out.println("redraw");
			Graphics2D g2 = bi.createGraphics();
		    Composite buff = g2.getComposite();
			g2.setComposite(AlphaComposite.DstOut);
		    g2.fillRect(0, 0, r.width, r.height);
		    g2.setComposite(buff);
			doForImage(bi, g2);
		}
		public Image getImage(){
			int valid = bi.validate(gc);
			if(valid == VolatileImage.IMAGE_INCOMPATIBLE){
				rebuild();
				redraw();
			}else if(valid == VolatileImage.IMAGE_RESTORED){
				do{
					valid = bi.validate(gc);
					redraw();
				}while(bi.contentsLost());
			}
			return bi;
		}
	}
	/**
	 * Wrapper class for Buffered Image, to buffer & assist w/ drawing.
	 * @author Andrew
	 *
	 */
	class PolyAtRotBuffered extends PolyAtRot{
		BufferedImage bi;
		public PolyAtRotBuffered(Polygon p, Color c){
			super(p, c);
			bi = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
			doForImage(bi, bi.createGraphics());
		}
		public Image getImage(){
			return bi;
		}
	}
}