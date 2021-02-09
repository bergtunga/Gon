package com.dres.gon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.dres.grpx.JNumField;
import com.dres.util.ReflectionUtil;

//import static com.dres.util.Out.*;
/**
 * left click to activate whole group
 * right click to remove from group
 * middle to delete point
 * @author Andrew
 *
 */
public class AdvancedPolygonEditor extends JPanel {
	private static final long serialVersionUID = 1L;
	private AdvancedPolygon sp;
	private PolyPoint[] pts;
	private int current, xOffset, yOffset, xtraOff, ytraOff, changeVal3x, changeVal3y;
	private boolean selected;
	private double sizeFactor, changeValue1, changeValue2, rotation;
	private Box left;
	private String rot = "[R]otation", len = "[L]ength", addS = "Sh[a]pe", reorderS = "Re[o]rder", centerS = "[C]enter";
	private AbstractButton addButton, reorderButton, centerButton, rotationButton, distanceButton;
	private void setup(AdvancedPolygon spa){
		sizeFactor = 10.0d;
		current = -1;
		this.sp = spa;
		selected = false;
		xOffset = 100;
		yOffset = 100;
		updatePoints();
	}
	public AdvancedPolygonEditor (AdvancedPolygon spa){
		super(new BorderLayout());
		setup(spa);
		setPreferredSize(new Dimension(210, 210));
		rotationButton = new JToggleButton(rot);
		rotationButton.setToolTipText("Edit the rotation of a group of points");
		distanceButton = new JToggleButton(len);
		distanceButton.setToolTipText("Edit the length of a group of points");
		addButton = new JButton(addS);
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JDialog jd = new Adder();
				jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				jd.setVisible(true);
			}});
		addButton.setToolTipText("Add a shape");
		reorderButton = new JButton(reorderS);
		reorderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				sp.orderByRotation();
				updatePoints();
			}});
		reorderButton.setToolTipText("Order the points by their angle");
		centerButton = new JButton(centerS);
		centerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				xtraOff = 0;
				ytraOff = 0;
				repaint();
			}});
		left = Box.createVerticalBox();
		left.add(Box.createVerticalStrut(5));
		left.add(doButton(rotationButton));
		left.add(Box.createVerticalStrut(5));
		left.add(doButton(distanceButton));
		left.add(Box.createVerticalStrut(5));
		left.add(doButton(addButton));
		left.add(Box.createVerticalStrut(5));
		left.add(doButton(reorderButton));
		left.add(Box.createVerticalStrut(5));
		left.add(doButton(centerButton));
		left.setMaximumSize(new Dimension(200, 10000));
		left.add(Box.createVerticalStrut(1000));
		add(left, BorderLayout.WEST);
		setFocusable(true);
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent arg0) {
				xOffset = getWidth()/2;
				yOffset = getHeight()/2;
			}});
		addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				sizeFactor -= ReflectionUtil.getMouseRot(arg0);
				updatePoints();
				if(!selected){
					current = isOver(arg0.getX(), arg0.getY());
				}
				repaint();
			}
		});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				requestFocus();
				if(SwingUtilities.isRightMouseButton(arg0)){
					if(current != -1)
						sp.newGroup(current);
				}else if(SwingUtilities.isMiddleMouseButton(arg0)){
					if(current != -1){
						sp.delete(current);
						updatePoints();
						current = -1;
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println(isOver(arg0.getX(), arg0.getY()));
				if(SwingUtilities.isLeftMouseButton(arg0)){
					if(rotationButton.isSelected()){
						selected = true;
							changeValue1 = aTan(arg0.getY() - (yOffset + ytraOff), arg0.getX() - (xOffset + xtraOff));
					}
					if(distanceButton.isSelected()){
						selected = true;
						changeValue2 = distanceFromCenter(arg0.getX(), arg0.getY());
					}
				}else if(SwingUtilities.isRightMouseButton(arg0)){
					if(current == -1){
						changeVal3x = arg0.getX();
						changeVal3y = arg0.getY();
					}
				}
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				selected = false;
				changeValue1 = 0;
				changeValue2 = 0;
				changeVal3x = -1;
				changeVal3y = -1;
			}});
		addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if(rotationButton.isSelected()){
					double newTheta = aTan(arg0.getY() - (yOffset + ytraOff), arg0.getX()- (xOffset + xtraOff));
					if(selected && current == -1){
						rotation += newTheta - changeValue1;
						changeValue1 = newTheta;
						//angle = 0;
						updatePoints();
						repaint();
					}else if(selected){
						sp.rotate(current, newTheta - changeValue1);
						changeValue1 = newTheta;
						updatePoints();
						repaint();
					}
				}
				if(distanceButton.isSelected()){
					if(selected && current == -1){
						double dFC = distanceFromCenter(arg0.getX(), arg0.getY());
						sizeFactor *= (dFC/changeValue2);
						changeValue2 = dFC;
						updatePoints();
						repaint();
					}else if(selected){
						double dFC = distanceFromCenter(arg0.getX(), arg0.getY());
						sp.scale(current, dFC / changeValue2);
						changeValue2 = dFC;
						updatePoints();
						repaint();
					}
				}
				if(changeVal3x != -1){
					int x = arg0.getX(), y = arg0.getY();
					xtraOff += x - changeVal3x;
					ytraOff += y - changeVal3y;
					changeVal3x = x;
					changeVal3y = y;
				}
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				requestFocus();
				if(!selected){
					current = isOver(arg0.getX(), arg0.getY());
					repaint();
				}
			}});
		addKeyListener(new KeyAdapter(){

			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if(c == 'r' || c == 'R')
					rotationButton.doClick();
				else if(c == 'l' || c == 'L')
					distanceButton.doClick();
				else if(c == 'a' || c == 'A')
					addButton.doClick();
				else if(c == 'o' || c == 'O')
					reorderButton.doClick();
				else if(c == 'c' || c == 'C')
					centerButton.doClick();
					
			}});
	}
	/**
	 * Calculates the distance from the 'center' of the polygon
	 * @param x
	 * @param y
	 * @return the distance from the 'center' of the polygon
	 */
	private double distanceFromCenter(int x, int y){
		return Math.sqrt(Math.pow(x - (xOffset + xtraOff), 2) + Math.pow(y - (yOffset + ytraOff), 2));
	}
	/**
	 * does Math.atan2(x,y) and returns a positive value
	 * @param y
	 * @param x
	 * @return
	 */
	private static double aTan(double y, double x){
		double newTheta = Math.atan2(y, x);
		if(newTheta < 0)
			newTheta += Math.PI * 2;
		return newTheta;
	}
	private Box doButton(JComponent jtb){
		Dimension d = new Dimension(150, 25);
		jtb.setMinimumSize(d);
		jtb.setMaximumSize(d);
		jtb.setFocusable(false);
		Box b = Box.createHorizontalBox();
		b.setMaximumSize(d);
		b.setMinimumSize(d);
		b.add(Box.createHorizontalStrut(5));
		b.add(jtb);
		return b;
	}
	/**
	 * Checks if the x,y pt is over one of the points
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return Which point it is over.
	 */
	private int isOver(int x, int y){
		x -= (xOffset + xtraOff);
		y -= (yOffset + ytraOff);
		PolyPoint[] pts = this.pts;
		for(int i = 0; i < pts.length; i++){
			double dis = Math.sqrt(Math.pow(pts[i].getX()-x,2) + Math.pow(pts[i].getY()- y, 2));
			if((dis < PolyPoint.radius+1) || (i == current && dis < PolyPoint.radius * 2 + 1))
				return i;
		}
		return -1;
	}
	private void updatePoints(){
		pts = sp.getTotalPolygon(rotation, sizeFactor);
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.black);
		g.drawLine((xOffset + xtraOff), (yOffset + ytraOff), (int)(1000*Math.cos(rotation)) + (xOffset + xtraOff), (int)(1000* Math.sin(rotation)) + (yOffset + ytraOff));
		g.setColor(Color.RED);
		PolyPoint[] pts = this.pts;
		for(int i = 0; i < pts.length; i++){
			PolyPoint pt2;
			if(i == pts.length-1)
				pt2 = pts[0];
			else
				pt2 = pts[i+1];
			g.drawLine((xOffset + xtraOff)+pts[i].getX(), (yOffset + ytraOff)+pts[i].getY(), (xOffset + xtraOff)+pt2.getX(), (yOffset + ytraOff)+pt2.getY());
		}
		if(current != -1){
			int factor = 3;
			Graphics2D g2 =(Graphics2D)g;
			g2.scale(factor, factor);
			Polygon p = sp.getGroup(current, rotation, sizeFactor/factor);
			//for(Polygon p : sp.polygonsOf(current, rotation, sizeFactor/factor)){
					// = pts[current].getTop().getPolygon(0, sizeFactor / factor);
			p.translate((xOffset + xtraOff)/factor, (yOffset + ytraOff)/factor);
			g2.drawPolygon(p);
			//}
			g2.scale(1.0/factor, 1.0/factor);
		}
		for(int i = 0; i < pts.length; i++){
			PolyPoint pt = pts[i];
			if(i == current){}
			else{
				pt.drawPt(g, (xOffset + xtraOff), (yOffset + ytraOff));
			}
		}
		if(current != -1)
			pts[current].drawPt(g, (xOffset + xtraOff), (yOffset + ytraOff), PolyPoint.radius * 2);
	}
	/**
	 * Changes this so that it is instead representing the passed in polygon.
	 * @param polygon
	 */
	public void changeTo(AdvancedPolygon polygon) {
		setup(polygon);
		repaint();
	}
	/**
	 * This class lets the user add a set of points in the shape of a regular polygon to the AdvancedPolygon
	 * @author Andrew
	 *
	 */
	class Adder extends JDialog{
		int sides = 3;
		double rot, size;
		public Adder(){
			setTitle("Add Regular Polygon");
			JPanel p = new JPanel(new GridLayout(2,3));
			p.add(new JLabel("Sides"));
			p.add(new JLabel("Size"));
			p.add(new JLabel("rotation"));
			JNumField jf;
			this.setAlwaysOnTop(true);
			this.setPreferredSize(new Dimension(200, 100));
			p.add(jf = new JNumField<Integer>(Integer.valueOf(3)));
			jf.addCaretListener(new CaretListener(){
				@Override
				public void caretUpdate(CaretEvent e) {
					JNumField<Integer> numF = (JNumField)e.getSource();
					if(numF.get() <= 0)
						numF.setBackground(Color.red);
					else
						numF.setBackground(Color.GREEN);
					sides = numF.get();
				}});
			p.add(jf = new JNumField<Double>(Double.valueOf(1.1)));
			jf.addCaretListener(new CaretListener(){
				@Override
				public void caretUpdate(CaretEvent e) {
					JNumField<Double> numF = (JNumField)e.getSource();
					if(numF.get() <= 0)
						numF.setBackground(Color.red);
					else
						numF.setBackground(Color.GREEN);
					size = numF.get();
				}});
			p.add(jf =new JNumField<Double>(Double.valueOf(1.1)));
			jf.addCaretListener(new CaretListener(){
				@Override
				public void caretUpdate(CaretEvent e) {
					JNumField<Double> numF = (JNumField)e.getSource();
					numF.setBackground(Color.GREEN);
					rot = numF.get() %( Math.PI * 2);
				}});
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Adder.this.dispose();
					sp.addRegularGroup(sides, sizeFactor, rot);
					updatePoints();
				}});
			Box vBox = Box.createVerticalBox();
			vBox.add(p);
			vBox.add(ok);
			this.setContentPane(vBox);
			pack();
			this.setLocationRelativeTo(AdvancedPolygonEditor.this);
		}
	}
}
