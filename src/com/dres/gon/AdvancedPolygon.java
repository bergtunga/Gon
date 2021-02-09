package com.dres.gon;
import java.awt.Polygon;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import com.dres.util.Apt;

//import static com.dres.util.Out.*;

public class AdvancedPolygon implements Graphic, Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Point> points;
	private int currentGroup = 0;
	private boolean isGroupValid = false;
	private transient ArrayList<int[]> group;
	private transient ArrayList<Apt> lst = new ArrayList<Apt>();
	private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		group = new ArrayList<int[]>();
		isGroupValid = false;
		lst = new ArrayList<Apt>();
	}
	public AdvancedPolygon(){
		points = new ArrayList<Point>();
	}
	public static AdvancedPolygon fromString(String s){
		String[] sarr = s.split("_");
		AdvancedPolygon ap = new AdvancedPolygon();
		ap.currentGroup = Integer.parseInt(sarr[0]);
		for(int i = 1; i < sarr.length; i++)
			ap.points.add(Point.fromString(sarr[i]));
		return ap;
	}
	public String asString(){
		StringBuilder sb = new StringBuilder().
				append(currentGroup).append('_');
		for(Point p: points)
			sb.append(p.asString()).append('_');
		sb.substring(0, sb.length() - 2);
		return sb.toString();
	}
	public AdvancedPolygon clone(){
		try {
			AdvancedPolygon p = (AdvancedPolygon) super.clone();
			ArrayList<Point> np = new ArrayList<Point>();
			for(Point px: points)
				np.add(px.clone());
			p.points = np;
			
			p.group = new ArrayList<int[]>();
			p.isGroupValid = false;
			ArrayList<Apt> nl = new ArrayList<Apt>();
			for(Apt a: lst)
				nl.add(a);
			p.lst = nl;
			return p;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	public void orderByRotation(){
		Collections.sort(points);
		change();
	}
	public void addRegularGroup(int sides, double size, double rot){
		double turn = Math.PI * 2 / sides;
		for(int i = 0; i < sides; i++){
			Point p = new Point(size, turn * i + rot, currentGroup);
			points.add(p);
		}
		change();
		invalidateGroup();
		currentGroup++;
	}
	@Override
	public Polygon getPolygon(double rotation, double size) {
		Polygon result = new Polygon();
		for(Point p : points){
			if(!p.hidden){
				xyPt pt = new xyPt(p, rotation, size);
				result.addPoint(pt.x, pt.y);
			}
		}
		return result;
	}

	@Override
	public PolyPoint[] getTotalPolygon(double rotation, double size) {
		PolyPoint[] result = new PolyPoint[points.size()];
		for(int i = 0; i < result.length; i++){
			Point p = points.get(i);
			xyPt xy = new xyPt(p, rotation, size);
			result[i] = xy.toPolyPoint();
			result[i].setWontSort();
		}
		return result;
	}
	public Polygon getGroup(int pt, double rot, double size){
		int i = points.get(pt).group;
		Polygon result = new Polygon();
		for(Point p: points){
			if(p.group == i){
				xyPt xp = new xyPt(p, rot, size);
				result.addPoint(xp.x, xp.y);
			}
		}
		return result;
	}
	
	/*
	public Polygon[] polygonsOf(int pt, double rot, double size){
		ArrayList<int[]> groups = groups();
		boolean[] x = new boolean[groups.size()];
		int ctr = 0;
		for(int j = 0; j < groups.size(); j++){
			int[] group = groups.get(j);
			for(int i : group){
				if(i == pt){
					ctr++;
					x[j] = true;
					break;
				}
			}
		}
		Polygon[] result = new Polygon[ctr];
		for(int i = 0; i < x.length; i++){
			if(!x[i])
				continue;
			Polygon p = new Polygon();
			for(int j: groups.get(i)){
				xyPt xy = new xyPt(points.get(j), rot, size);
				p.addPoint(xy.x, xy.y);
			}
			result[--ctr] = p;
		}
		return result;
	}*/
	
	protected void invalidateGroup(){
		isGroupValid = false;
	}
	public ArrayList<int[]> groups(){
		if(isGroupValid)
			return group;
		ArrayList<int[]> result = new ArrayList<int[]>();
		for(int i = 0; i < currentGroup; i++){
			int j = 0;
			for(Point p: points)
				if(p.group == i)
					j++;
			if(j != 0){
				//outln("Array of " + j);
				int[] add = new int[j];
				j--;
				for(int k = 0; k < points.size(); k++){
					Point p = points.get(k);
					if(p.group == i){
						add[j] = k;
						j--;
						if(j == -1)
							break;
					}
				}
				result.add(add);
					
			}
		}
		group = result;
		isGroupValid = true;
		return result;
	}
	

	class xyPt{
		int x, y;
		Point p;
		xyPt(Point p, double rt, double size){
			double mult = p.distance * size;
			x = (int)Math.round(Math.cos(p.getTheta() + rt) * mult);
			y = (int)Math.round(Math.sin(p.getTheta() + rt) * mult);
			this.p = p;
		}
		PolyPoint toPolyPoint(){
			return new PolyPoint(x, y, AdvancedPolygon.this, p.hidden, p.getTheta(), p.distance);
		}
	}
	public void rotate(int current, double d) {
		int group = points.get(current).group;
		for(Point p: points){
			if(p.group == group)
				p.changeTheta(d);
		}
		change();
	}
	public void scale(int current, double d) {
		if(Double.isInfinite(d))
			return;
		int group = points.get(current).group;
		for(Point p: points){
			if(p.group == group){
				p.distance *= d;
			}
		}
		change();
	}
	public void onChange(Apt a){
		lst.add(a);
	}
	public void clearUpdates(){
		lst.clear();
	}
	private void change(){
		for(Apt a: lst)
			a.call();
	}
	public String getInfo() {
		//TODO: FIX
		return null;
	}
	public void newGroup(int... current) {
		invalidateGroup();
		for(int i: current){
			points.get(i).group = currentGroup;
		}
		currentGroup++;
	}
	public void delete(int current) {
		points.remove(current);
		invalidateGroup();
		change();
	}
	static class Point implements Serializable, Cloneable, Comparable<Point>{
		private static final long serialVersionUID = 1L;
		
		int group;
		double distance;
		private double theta;
		boolean hidden = false;
		
		private Point(){}
		Point(double size, double theta, int group){
			this.distance = size;
			this.theta = 0;
			changeTheta(theta);
			this.group = group;
		}
		public Point clone(){
			try {
				return (Point) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
		protected void changeTheta(double change){
			theta += change;
			while(theta > 2*Math.PI)
				theta -= 2 * Math.PI;
			while(theta < 0)
				theta += 2 * Math.PI;
			this.theta = theta;
		}
		public StringBuilder asString(){
			StringBuilder sb = new StringBuilder().
					append(group).append(',').
					append(distance).append(',').
					append(theta).append(',').
					append(hidden);
			return sb;
		}
		public static Point fromString(String s){
			Point p = new Point();
			String[] sarr = s.split(",");
			if(sarr.length != 4)
				throw new IllegalArgumentException();
			p.group = Integer.parseInt(sarr[0]);
			p.distance = Double.parseDouble(sarr[1]);
			p.theta = Double.parseDouble(sarr[2]);
			p.hidden = Boolean.parseBoolean(sarr[3]);
			return p;
		}
		public double getTheta(){
			return theta;
		}
		@Override
		public int compareTo(Point o) {
			return Double.compare(theta, o.theta);
		}
	}
}

