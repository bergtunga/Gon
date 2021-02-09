package com.dres.gon;

import java.awt.Graphics2D;

public class HexagonTile extends TileMethod {
	
	private static final double SQRT3 = Math.sqrt(3);
	
	
	public HexagonTile(TheGon tg, Preset p) {
		super(tg, p);
	}
	
	@Override
	public void doTiling(Graphics2D g, int i){
		if(i == 0){
			shapeOn(g, 0, 0, 1);
		}else{
			double
				curY = 0,
				mFactorO2 = .5,
				tO2mFactorY = mFactorO2 * SQRT3,
				curX = - i;
				
			double l = i;
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX += mFactorO2;
				curY -= tO2mFactorY;
			}
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX += 1;
			}
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX += mFactorO2;
				curY += tO2mFactorY;
			}
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX -= mFactorO2;
				curY += tO2mFactorY;
			}
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX -= 1;
			}
			for(int j = 0; j < l; j++){
				shapeOn(g, curX, curY, 1);
				curX -= mFactorO2;
				curY -= tO2mFactorY;
			}
		}
	}

}
