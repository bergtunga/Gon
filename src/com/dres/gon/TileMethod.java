package com.dres.gon;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class TileMethod {
	private TheGon gon;
	private Preset settings;
	public TileMethod(TheGon tg, Preset s){
		gon = tg;
		settings = s;
	}
	
	protected abstract void doTiling(Graphics2D g, int i);
	
	protected void shapeOn(Graphics2D g, double xCord, double yCord, double upscaleFactor){
		//Calculate the rotation by checking the distance from the center.
		double x = xCord, y = yCord;
		Rectangle r = settings.getPolygon(0).getBounds();
		if(true){
			double a = settings.getDistanceMultiplierX();
			if(a != 0)
				x = x * a;
			a = settings.getDistanceMultiplierY();
			if(a != 0)
				y = y * a;
		}
		
		double rot = rotAt(gon.getTick() - Math.sqrt(x * x + y * y) * settings.getReactionDelay());
		gon.drawShape(g,
				xCord * settings.getDistanceMultiplierX() * r.width,
				yCord * settings.getDistanceMultiplierY() * r.height,
				rot, upscaleFactor);
	}/**
	 * Calculates the rotation (of the center object) for a tick.
	 * @param tick
	 * @return
	 */
	private double rotAt(double tick){
		int ti = (int)tick;
		double dur = settings.getDuration();
		while(ti < 0)
			ti += dur;
		if(ti > dur)
			throw new UnsupportedOperationException();
		double result;
		
		int stages = settings.getStages();
		
		
		//First, calculate the % of rotation (0 - 1)
		if(stages != 0){
			double pause = settings.getPause();
			double stageLength = dur / stages; //get the length of a stage
			int currentStage = (int)(ti / stageLength); // Calculate current stage 0 -> (stages-1) always round down.
			double moveTime = stageLength - pause; // the part of the time it is actually moving.
			ti %= stageLength; //How far it is into the current stage.
			if(ti < pause) // If it's during the time where it is paused
				result = currentStage * 1.0 / stages; //the % is the currentStage / # of stages
			else{ //If it's during the time it's shifting positions.
				double current = (ti - pause) / moveTime; // The % of movement completed in the current stage
				result = (current + currentStage) / stages; // Add the % of current & the num of stages,
																			//divide by Num of stages to get % complete.
			}
		}else{
			//If there are no stages, then the % is just tick / duration.
			result = ti / dur;
		}
		result *= settings.getRotationAmt();
		result += settings.getOffset();
		if(settings.isDirectionLeft())
			result = -result;
		return result;
	}

	public void settings(Preset settings2) {
		settings = settings2;
	}
}
