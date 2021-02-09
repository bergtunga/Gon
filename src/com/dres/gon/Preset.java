package com.dres.gon;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dres.grpx.FlexibleGradient2D;
import com.dres.grpx.GradientIcon2D;
import com.dres.util.Apt;

public class Preset implements Serializable, Cloneable, FlexibleGradient2D {
	private static final long serialVersionUID = 1L;
	public static final Preset
		preset1, preset2;
	private static final double
			PI;
	public static boolean vRAMCache = true;
	public static final JFileChooser chooser;
	public static final FileFilter presetFF = new FileNameExtensionFilter("HexaGonWild Presets",
			"gon", "pst");
	static{
		PI = Math.PI;
		preset1 = new Preset();
		preset1.caches(true);
		Preset x =preset2 = new Preset();
		x.gradientSta = new Point(130, 199);
		x.gradientEnd = new Point(130, 0);
		x.gradientRepeats = 12;
		x.stages = 6;
		x.duration = 2000;
		x.rows = 12;
		x.lagReduction = 10;
		x.sizeFactor = 9.0;
		x.pause = 80.0;
		x.reactionDelay = 3600000.0;
		x.timingMultiplier = 2.0;
		x.offset = 1.5707963267948966;
		x.rotationAmt = 6.283185307179586;
		x.distanceMultiplierX = 0.001;
		x.distanceMultiplierY = 1.0E-4;
		x.directionLeft = false;
		x.needsUpdate = false;
		x.bouncing = true;
		x.antiAliasing = true;
		x.caches = true;
		x.polygon = AdvancedPolygon.fromString("3_1,10.0,0.1,false_2,22.373003037638266,1.0471975511965976,false_2,22.373003037638266,5.235987755982988,false_0,10.0,6.183185307179587,false_");
		x.backgroundImage = null;
		x.maxAlpha = 0.6f;
		x.minAlpha = 0.3f;
		
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setFileFilter(presetFF);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	public transient Color[][] gradientValues;
	private Point
			gradientSta = new Point(100, 180),
			gradientEnd = new Point(0, 199);
	private int
			gradientRepeats = 6, // How many times the gradient from start-end is traveled during a cycle
			stages = 6, // How many times it pauses during a cycle
			duration = 4860, // How long a cycle is (milliseconds)
			rows = 10, // How many rings are used.
			lagReduction = 10; // The time between each update. Also is multiplied by each tick (milliseconds)
	private double
			sizeFactor = 2.95, //How large the shapes appear.
				//TODO: decide if it should be changed to use graphics's upscale vs Polygon's Upscale
			pause = 440, // How long the shape will pause for.
			reactionDelay = 40, // The factor by which the distance of the shape from the center is multiplied by.
			timingMultiplier = 1, // reactionDelay, duration, and pause are multiplied by this to their effective value.
			offset = PI / 2, // Additional rotation added to start & end.
			rotationAmt = 2 * PI, // Total amount in radians the shape rotates.
			distanceMultiplierX = 1.1, // How far the shapes are from each other. Uses the shape at a duration of 0 as base value.
			distanceMultiplierY = .93; // How far the shapes are from each other. Uses the shape at a duration of 0 as base value.
	private boolean
			directionLeft = false, // whether or not the shape is spinning left.
			needsUpdate = false, //Codewise: whether the cache needs to update.
			bouncing = true, // Whether the variable gradient will reverse directions at end of one pass or start over.
			antiAliasing = true, // Whether or not antiAliasing is enabled.
			caches = false;//, // Whether or not shape caching is enabled.
			//scircleExpansion = true;
			//vRAMCache = true;
	private AdvancedPolygon polygon; // The polygon.
	private String
			backgroundImage = null; // A string that indicates where an image is.
	private transient String name = null; // The save file's name.
	private float
			maxAlpha = .6f,
			minAlpha = .3f;

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		gradientValues = createGradient(200);
		System.out.println(toCodeString());
	}
	public static Preset read(File f) {
		try{
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Preset result = (Preset)ois.readObject();
			ois.close();
			return result;
		}catch(Exception e){
			writeProblem(f);
			e.printStackTrace();
			return null;
		}
	}

	public static void noExist(File f){
		JOptionPane.showMessageDialog(null, f + "\nDoes not exist!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}
	public static void writeProblem(File f) {
		JOptionPane.showMessageDialog(null, "Problem processing " + f, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private static boolean ensure(File f, Preset p) {
		int con = JOptionPane.showConfirmDialog(null, "Do you wish to overwrite\n" + f
				+ " ?", "Confirm file", JOptionPane.YES_NO_CANCEL_OPTION);
		if(con == JOptionPane.NO_OPTION)
			return false;
		if(con == JOptionPane.CANCEL_OPTION)
			return true;
		write(f, p);
		return true;
	}

	private static void write(File f, Preset p) {
		try {
			if (f.exists())
				f.delete();
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fo);
			oos.writeObject(p);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Problem Writing " + f
					+ " exception " + e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	public static void save(Preset toSave){
		chooser.setSelectedFile(new File("Save.gon"));
		boolean complete = false;
		while (!complete) {
			int i = chooser.showSaveDialog(null);
			if (i == JFileChooser.CANCEL_OPTION
					|| i == JFileChooser.ERROR_OPTION)
				complete = true;
			else {
				File f = chooser.getSelectedFile();
				if (!presetFF.accept(f))
					f = new File(f.getParentFile(), f.getName()
							+ ".pst");
				if (f.exists() && f.canWrite())
					complete = ensure(f, toSave);
				else if (f.exists()) {
					writeProblem(f);
				} else {
					write(f, toSave);
					complete = true;
				}
			}
		}
	}
	public static Preset open(){
		Preset p = null;
		int i = Preset.chooser.showOpenDialog(null);
		if (i == JFileChooser.CANCEL_OPTION || i == JFileChooser.ERROR_OPTION)
			return null;
		else {
			File f = Preset.chooser.getSelectedFile();
			if (!f.exists())
				Preset.noExist(f);
			else {
				p = Preset.read(f);
				p.name = f.getName();
			}
		}
		//System.out.println(p);
		return p;
	}
	public Preset(){
		AdvancedPolygon ap = new AdvancedPolygon();
		polygon = ap;
		ap.addRegularGroup(3, 10, -.10);
		ap.addRegularGroup(3, 10, 0.10);
		ap.addRegularGroup(3, 1.15, Math.PI / 3);
		ap.orderByRotation();
		gradientValues = createGradient(200);
	}

	private Color[][] createGradient(int size) {
		Color[][] one = new Color[size][size];
		for(int h = 0; h < one.length; h++)
			for(int s = 0; s < one[h].length; s++)
				one[h][s] = Color.getHSBColor(
						h / (float) one.length,
						s / (float) one[h].length,
						1);
		return one;
	}
	public Preset clone(){
		try {
			Preset p = (Preset) super.clone();
			p.gradientEnd = new Point(gradientEnd.x, gradientEnd.y);
			p.gradientSta = new Point(gradientSta.x, gradientSta.y);
			p.polygon = (AdvancedPolygon) polygon.clone();
			return p;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String toString(){
		String tab = "\t";
		char nl = '\n';
		StringBuilder s = new StringBuilder();
		s.
		append("Display Info:\n").
		append(tab).append("Rows=" + rows).append(nl).
		append(tab).append("sizeF=" + sizeFactor).append(nl).
		append(tab).append("dis*@X=" + distanceMultiplierX).append(nl).
		append(tab).append("dis*@Y=" + distanceMultiplierY).append(nl).
		append(tab).append("offset=" + offset / PI).append(nl).
		append(tab).append("RotAmt=" + rotationAmt / PI).append(nl).
		append("Gradient Info:\n").
		append(tab).append("S=" + gradientSta).append(nl).
		append(tab).append("E=" + gradientEnd).append(nl).
		append(tab).append("R=" + gradientRepeats).append(nl).
		append("Timing Info:\n").
		append(tab).append("Durati=" + duration).append(nl).
		append(tab).append("pause =" + pause).append(nl).
		append(tab).append("ReactD=" + reactionDelay).append(nl).
		append(tab).append("TimMul=" + timingMultiplier).append(nl).
		append(tab).append("Stages=" + stages).append(nl).
		append(tab).append("lagRed=" + lagReduction).append(nl).
		append("Booleans\n").
		append(tab).append("dirLeft =" + directionLeft).append(nl).
		append(tab).append("FieldUpd=" + needsUpdate).append(nl)
		.append("AdvancedPolygon\n").append(polygon.getInfo());
		return s.toString();
	}
	public String toCodeString(){
		String tab = "\t";
		char nl = '\n';
		StringBuilder s = new StringBuilder().
		append("x = new Preset();").append(nl).
		//append("private Point").append(nl).
		append(tab).append("x.gradientSta = new Point(").append(gradientSta.x).append(", ").append(gradientSta.y).append(");").append(nl).
		append(tab).append("x.gradientEnd = new Point(").append(gradientEnd.x).append(", ").append(gradientEnd.y).append(");").append(nl).
		//append("private int").append(nl).
		append(tab).append("x.gradientRepeats = ").append(gradientRepeats).append(';').append(nl).
		append(tab).append("x.stages = ").append(stages).append(';').append(nl).
		append(tab).append("x.duration = ").append(duration).append(';').append(nl).
		append(tab).append("x.rows = ").append(rows).append(';').append(nl).
		append(tab).append("x.lagReduction = ").append(lagReduction).append(';').append(nl).
		//append("private double").append(nl).
		append(tab).append("x.sizeFactor = ").append(sizeFactor).append(';').append(nl).
		append(tab).append("x.pause = ").append(pause).append(';').append(nl).
		append(tab).append("x.reactionDelay = ").append(reactionDelay).append(';').append(nl).
		append(tab).append("x.timingMultiplier = ").append(timingMultiplier).append(';').append(nl).
		append(tab).append("x.offset = ").append(offset).append(';').append(nl).
		append(tab).append("x.rotationAmt = ").append(rotationAmt).append(';').append(nl).
		append(tab).append("x.distanceMultiplierX = ").append(distanceMultiplierX).append(';').append(nl).
		append(tab).append("x.distanceMultiplierY = ").append(distanceMultiplierY).append(';').append(nl).
		//append("private boolean").append(nl).
		append(tab).append("x.directionLeft = ").append(directionLeft).append(';').append(nl).
		append(tab).append("x.needsUpdate = ").append(needsUpdate).append(';').append(nl).
		append(tab).append("x.bouncing = ").append(bouncing).append(';').append(nl).
		append(tab).append("x.antiAliasing = ").append(antiAliasing).append(';').append(nl).
		append(tab).append("x.caches = ").append(caches).append(';').append(nl).
		//private AdvancedPolygon polygon;
		append(tab).append("x.polygon = AdvancedPolygon.fromString(\"").append(polygon.asString()).append("\");").append(nl).
		append(tab).append("x.backgroundImage = ").append(backgroundImage).append(';').append(nl).
		//append("private float").append(nl).
		append(tab).append("x.maxAlpha = ").append(maxAlpha).append("f;").append(nl).
		append(tab).append("x.minAlpha = ").append(minAlpha).append("f;").append(nl);
		return s.toString();
	}
	
	public boolean update(){
		if(needsUpdate){
			needsUpdate = false;
			return true;
		}else
			return false;
	}

	public void setGradientStart(int x, int y) {
		if(x != -1){
			if (x < 0)
				x = 0;
			else if (x >= gradientValues.length)
				x = gradientValues.length - 1;
			gradientSta.x = x;
			needsUpdate = true;
		}
		if(y != -1){
			if (y < 0)
				y = 0;
			else if (y >= gradientValues.length)
				y = gradientValues.length - 1;
			gradientSta.y = y;
			needsUpdate = true;
		}
	}

	public void setGradientEnd(int x, int y) {
		if(x != -1){
			if (x < 0)
				x = 0;
			else if (x >= gradientValues.length)
				x = gradientValues.length - 1;
			gradientEnd.x = x;
			needsUpdate = true;
		}
		if(y != -1){
			if (y < 0)
				y = 0;
			else if (y >= gradientValues.length)
				y = gradientValues.length - 1;
			gradientEnd.y = y;
			needsUpdate = true;
		}
	}

	/**
	 * @return the lagReduction
	 */
	public int getLagReduction() {
		return lagReduction;
	}

	/**
	 * @param lagReduction
	 *            the lagReduction to set
	 */
	public void setLagReduction(int lagReduction) {
		this.lagReduction = lagReduction;
	}
	public int getBaseDuration(){
		return duration;
	}
	/**
	 * @return the effective duration
	 */
	public double getDuration() {
		return duration * timingMultiplier;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the timingMultiplier
	 */
	public double getBaseMultiplier() {
		return timingMultiplier;
	}

	/**
	 * @param timingMultiplier
	 *            the timingMultiplier to set
	 */
	public void setTimingMultiplier(double timingMultiplier) {
		this.timingMultiplier = timingMultiplier;
	}

	/**
	 * @return the gradientSta
	 */
	public Point getGradientStart() {
		return gradientSta;
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return the stages
	 */
	public int getStages() {
		return stages;
	}

	/**
	 * @param stages
	 *            the stages to set
	 */
	public void setStages(int stages) {
		this.stages = stages;
	}

	/**
	 * @return the pause
	 */
	public double getPause() {
		return pause * timingMultiplier;
	}
	public double getBasePause(){
		return pause;
	}

	/**
	 * @param pause
	 *            the pause to set
	 */
	public void setPause(double pause) {
		this.pause = pause;
	}

	/**
	 * @return the rotationAmt
	 */
	public double getRotationAmt() {
		return rotationAmt;
	}

	/**
	 * @param rotationAmt
	 *            the rotationAmt to set
	 */
	public void setRotationAmt(double rotationAmt) {
		this.rotationAmt = rotationAmt;
	}

	/**
	 * @return the offset
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}

	/**
	 * @return the bouncing
	 */
	public boolean isBouncing() {
		return bouncing;
	}
	/**
	 * @param bouncing the bouncing to set
	 */
	public void setBouncing(boolean bouncing) {
		this.bouncing = bouncing;
		needsUpdate = true;
	}
	/**
	 * @return the directionRight
	 */
	public boolean isDirectionLeft() {
		return directionLeft;
	}

	/**
	 * @param directionRight
	 *            the directionRight to set
	 */
	public void setDirectionLeft(boolean direction) {
		this.directionLeft = direction;
		needsUpdate = true;
	}
	/**
	 * @return the sizeFactor
	 */
	public double getSizeFactor() {
		return sizeFactor;
	}

	/**
	 * @param sizeFactor
	 *            the sizeFactor to set
	 */
	public void setSizeFactor(double sizeFactor) {
		this.sizeFactor = sizeFactor;
		needsUpdate = true;
	}

	/**
	 * @return the reactionDelay
	 */
	public double getBaseReactionDelay() {
		return reactionDelay;
	}
	
	/**
	 * @return the reactionDelay
	 */
	public double getReactionDelay() {
		return reactionDelay * timingMultiplier;
	}
	/**
	 * @param reactionDelay
	 *            the reactionDelay to set
	 */
	public void setReactionDelay(double reactionDelay) {
		this.reactionDelay = reactionDelay;
	}

	/**
	 * @return the gradientEnd
	 */
	public Point getGradientEnd() {
		return gradientEnd;
	}

	/**
	 * @return the gradientRepeats
	 */
	public int getGradientRepeats() {
		return gradientRepeats;
	}

	/**
	 * @param gradientRepeats
	 *            the gradientRepeats to set
	 */
	public void setGradientRepeats(int gradientRepeats) {
		this.gradientRepeats = gradientRepeats;
		needsUpdate = true;
	}
	@Override
	public Color[][] getArray() {
		return gradientValues;
	}
	public Color determineColor(double rot){
		rot = Math.abs(rot);
		rot -= offset;
		
		int range = (int)Math.round(Math.sqrt(Math.pow(gradientEnd.x - gradientSta.x,2) + Math.pow(gradientEnd.y - gradientSta.x, 2)));
		if(range == 0)
			return gradientValues[gradientEnd.x][gradientEnd.y];
		
		rot /= rotationAmt;
		if(isBouncing())
			rot = reduce(rot * gradientRepeats, 1);
		else
			rot = (rot * gradientRepeats) % 1.0;
		return getColor((float)rot);
	}
	private double reduce(double reduce, double max){
		reduce %= 2 * max;
		if(reduce > max)
			reduce = 2 * max - reduce;
		return reduce;
	}
	private Color getColor(float f){
		if (f < 0 || f > 1)
			throw new IllegalArgumentException("Does not meet conditions: 0 < " + f + " < 1");
		
		Point pt = new Point(
				Math.round(gradientSta.x + f * (gradientEnd.x - gradientSta.x)),
				Math.round(gradientSta.y + f * (gradientEnd.y - gradientSta.y)));
		return gradientValues[pt.x][pt.y];
	}

	/**
	 * @return the polygon
	 */
	public Polygon getPolygon(double i) {
		return polygon.getPolygon(i, sizeFactor);
	}
	public AdvancedPolygon getPolygon(){
		return polygon;
	}
	public void setDistanceMultiplierX(double d){
		distanceMultiplierX = d;
	}
	public double getDistanceMultiplierX() {
		return distanceMultiplierX;
	}
	public void setDistanceMultiplierY(double d){
		distanceMultiplierY = d;
	}
	public double getDistanceMultiplierY() {
		return distanceMultiplierY;
	}
	/**
	 * @return the backgroundImage
	 */
	public String getBackgroundImage() {
		return backgroundImage;
	}
	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public String getName() {
		return name;
	}
	public float alphaFor(float tick) {
		float f = 2 * (float) (tick / (getDuration()));
		if(f < 1.0f)
			return f * (maxAlpha - minAlpha) + minAlpha;
		else
			return (2 - f) * (maxAlpha - minAlpha) + minAlpha;
	}
	public boolean caches() {
		return caches;
	}
	public void caches(boolean b) {
		if(caches == b)
			return;
		this.caches = b;
		needsUpdate = true;
	}
	public boolean isAntiAliasing(){
		return antiAliasing;
	}
	public void setAntiAliasing(boolean b){
		if(b == antiAliasing)
			return;
		antiAliasing = b;
		needsUpdate = true;
	}
	public static boolean vRAM(){
		return vRAMCache;
	}
	/*
	public void vRAM(boolean s) {
		if(s == vRAMCache)
			return;
		needsUpdate = true;
		vRAMCache = s;
	}*/
}