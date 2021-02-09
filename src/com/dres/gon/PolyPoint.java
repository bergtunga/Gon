package com.dres.gon;
//import static com.dres.util.Out.*;

import java.awt.Color;
import java.awt.Graphics;


public class PolyPoint implements Comparable<PolyPoint>{
	public static final int radius = 3;
	private int x, y, quadrent;
	private double slope, theta, distance;
	private boolean isIgnored;
	private Graphic top;
	private boolean willSort = true;
	public PolyPoint(int x, int y, Graphic top){
		this(x, y, top, false, aTan(y, x), Math.sqrt(x * x + y * y));
	}
	public PolyPoint(int x, int y, Graphic top, boolean isIgnored, double theta, double distance){
		this.x = x; this.y = y;
		if(x > 0){
			if(y > 0)
				quadrent = 1;
			else
				quadrent = 4;
		}else if(x < 0){
			if(y > 0)
				quadrent = 2;
			else
				quadrent = 3;
		}
		if(y == 0 || x == 0){
			quadrent = 0;
			if(x == y)
				willSort = false;
		}
		if(quadrent != 0)
			this.slope = y * 1.0 / x;
		this.isIgnored = isIgnored;
		this.top = top;
		this.theta = theta;
		this.distance = distance;
	}
	public void setWontSort(){
		willSort = false;
	}
	@Override
	public boolean equals(Object other){
		if(!(other instanceof PolyPoint))
			return false;
		PolyPoint o = (PolyPoint)other;
		return top == o.top && o.x == x && o.y == y;
	}
	/**
	 * Note: this class has a natural ordering that is inconsistent with equals." 
	 */
	@Override
	public int compareTo(PolyPoint o) {
		if(!willSort || !o.willSort)
			throw new UnsupportedOperationException();
		if(o.quadrent == 0 || quadrent == 0){
			
			out("Zero ");
			if (o.quadrent == quadrent) {
				out("Both ");
				boolean pA =   getX() > 0, pB =   getY() > 0, pC =   getX() < 0, pD =   getY() < 0;
				boolean qA = o.getX() > 0, qB = o.getY() > 0, qC = o.getX() < 0, qD = o.getY() < 0;
				if ((qA && pA) || (qC && pC) || (qB && pB) || (qD && pD)) {
					out("0 = " + PolyPoint.this + ", " + o);
					return 0;
				} else if(pA){ //o.x < 0 && y Irrev
					return 1;
				} else if((pB || pC) && (qC || qD)){
					return 1;
				}else{
					return -1;
				}
			} else{
				int mult = 1;
				PolyPoint letter, number;
				if(quadrent != 0){
					mult = -1;
					letter = o;
					number = this;
				}else{
					letter = this;
					number = o;
				}
				boolean a = letter.getX() > 0, b = letter.getY() > 0,
						//c = letter.getX() < 0,
						d = letter.getY() < 0;
				int q = number.quadrent;
				if(q == 4 || a){
					return  mult;
				}else if(q == 1 || d){
					return -mult;
				}else if(q == 3 || b){
					return mult;
				}else{
					return -mult;
				}
			}
			//System.out.println(Point.this + ", " + o);
			//throw new UnsupportedOperationException();
		}else if(quadrent != o.quadrent){
			out("Diff Quadrents ");
			int i = quadrent - o.quadrent;
			if (i < 0) {
				outln("1 = " + PolyPoint.this + ", " + o);
				return 1;
			} else if (i > 0) {
				outln("-1 = " + PolyPoint.this + ", " + o);
				return -1;
			}
		}
		double d = this.slope - o.slope;
		if (d < 0) {
			outln("1 = " + PolyPoint.this + ", " + o);
			return 1;
		} else if (d == 0) {
			outln("0 = " + PolyPoint.this + ", " + o);
			return 0;
		} else if (d > 0) {
			outln("-1 = " + PolyPoint.this + ", " + o);
			return -1;
		}
		outln(PolyPoint.this + ", " + o);
			throw new UnsupportedOperationException();
	}
	public String toString(){
		return "[x=" + getX() + ",y=" + getY() + ",slope=" + slope + ",quad=" + quadrent + "]";
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	public boolean isIgnored(){
		return isIgnored();
	}
	public void drawPt(Graphics g, int x, int y){
		drawPt(g, x, y, radius);
	}
	public void drawPt(Graphics g, int x, int y, int radius){
		x += this.x;
		y += this.y;
		if(this.isIgnored){
			g.setColor(Color.RED);
			g.drawLine(x + radius, y + radius, x - radius, y - radius);
			g.drawLine(x - radius, y + radius, x + radius, y - radius);
		}else{
			g.setColor(Color.cyan);
			g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
			g.setColor(Color.blue);
			g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
		}
		g.setColor(Color.RED);
		//pixel(g, x, y);
		
	}
	/*
	private void pixel(Graphics g, int x, int y){
		g.drawLine(x, y, x, y);
	}*/
	private static void out(String s){}
	private static void outln(String s){}
	public Graphic getTop() {
		return top;
	}
	private static double aTan(double y, double x){
		double newTheta = Math.atan2(y, x);
		if(newTheta < 0)
			newTheta += Math.PI * 2;
		return newTheta;
	}
	public double getTheta() {
		return theta;
	}
	public double getDistance(){
		return distance;
	}
}