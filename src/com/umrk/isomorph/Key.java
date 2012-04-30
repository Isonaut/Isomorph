package com.umrk.isomorph;

import android.graphics.Paint;
import android.graphics.Path;

public class Key {
	protected Integer[] location;
	protected Note note;

	protected static Paint blackPaint;
	protected static Paint whitePaint;
	protected static Paint pressedPaint;
	protected static Paint rootPaint;
	protected static Paint borderPaint;
	protected static int size = 60;
	protected static boolean vertical = false;
	protected static Path hexPath = getHexPath();
	
	public Key(Integer[] LOCATION, Note NOTE) {
		location = LOCATION;
		note = NOTE;
	}
	
	public static void initPaints() {
		blackPaint.setARGB(255, 30, 30, 30);
		blackPaint.setStyle(Paint.Style.FILL);
		blackPaint.setStrokeWidth(2);
		blackPaint.setAntiAlias(true);
		
		whitePaint.setARGB(255, 255, 255, 255);
		whitePaint.setStyle(Paint.Style.FILL);
		whitePaint.setStrokeWidth(2);
		whitePaint.setAntiAlias(true);
		
		pressedPaint.setARGB(255, 95, 95, 127);
		pressedPaint.setStyle(Paint.Style.FILL);
		pressedPaint.setStrokeWidth(2);
		pressedPaint.setAntiAlias(true);
		
		rootPaint.setARGB(255, 127, 127, 127);
		rootPaint.setStyle(Paint.Style.FILL);
		rootPaint.setStrokeWidth(2);
		rootPaint.setAntiAlias(true);
		
		borderPaint.setARGB(255, 0, 0, 0);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(2);
		borderPaint.setAntiAlias(true);
	}
	
	public static Path getHexPath() {
		Path toReturn = new Path();
		double interval = Math.PI/3;
		double offset = 0;
		if (vertical) {offset = Math.PI/6;}
		for (int i = 0; i < 6; i++) {
			toReturn.lineTo((float)(size*Math.cos(i*interval+offset)), (float)(size*Math.sin(i*interval+offset)));
		}
		toReturn.close();
		return toReturn;
	}
	
}