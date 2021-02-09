package com.dres.gon;
import java.awt.Polygon;


interface Graphic{
	Polygon getPolygon(double rotation, double size);
	PolyPoint[] getTotalPolygon(double rotation, double size);
}