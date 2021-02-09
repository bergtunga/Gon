package com.dres.gon;

import java.awt.Graphics2D;

public class SquareTile extends TileMethod {
	public SquareTile(TheGon tg, Preset p) {
		super(tg, p);
	}

	public void doTiling(Graphics2D g, int i){
		int cX = 0, cY = 0;
		i *= 2;
		if(i == 0){
			shapeOn(g, cX, cY, 1);
		}else{
			double
				curY = cY + i/2, 
				curX = cX - i/2;
			
			for(int j = 0; j < i; j++){
				shapeOn(g, curX, curY, 1);
				curY -= 1;
			}
			for(int j = 0; j < i; j++){
				shapeOn(g, curX, curY, 1);
				curX += 1;
			}
			for(int j = 0; j < i; j++){
				shapeOn(g, curX, curY, 1);
				curY += 1;
			}
			for(int j = 0; j < i; j++){
				shapeOn(g, curX, curY, 1);
				curX -= 1;
			}
		}
	}

}
