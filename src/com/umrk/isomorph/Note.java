package com.umrk.isomorph;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class Note {

	protected static Paint blackPaint = new Paint();
	protected static Paint whitePaint = new Paint();
	protected static Paint pressedPaint = new Paint();
	protected static Paint rootPaint = new Paint();
	protected static Paint borderPaint = new Paint();
	
	protected static int keySize;
	protected static boolean vertical = false;
	protected static Instrument instrument;
	protected static Integer[] scale = {0, 2, 4, 5, 7, 9, 11};
	protected static int mode = 0;
	protected static int root = 0;
	protected static boolean allDirty = false;
	protected static boolean showNotes = true;
	protected static boolean showScales = true;
	protected static boolean showRoots = true;
	
	protected int midiNumber;
	protected ArrayList<Integer[]> locations;
	protected ArrayList<Path> paths = new ArrayList<Path>();
	protected boolean pressed = false;
	protected Paint paint = whitePaint;
	protected String label = "";
	protected int octave;
	protected int number;
	protected boolean dirty = true;
	protected int timer = 0;

	/**
	 * Constructs a note from its midinumber
	 * @param MIDINUMBER
	 */
	public Note(int MIDINUMBER) {
		midiNumber = MIDINUMBER;
		locations = new ArrayList<Integer[]>();
		init();
	}
	
	/**
	 * Constructs a note from its midi number and a single location
	 * @param MIDINUMBER
	 * @param LOCATION
	 */
	public Note(int MIDINUMBER, Integer[] LOCATION) {
		midiNumber = MIDINUMBER;
		locations = new ArrayList<Integer[]>();
		locations.add(new Integer[] {LOCATION[0], LOCATION[1]});
		paths.add(getHexagonPath(LOCATION));
		init();
	}
	
	/**
	 * Constructs a note from its midi number and a set of locations
	 * @param MIDINUMBER
	 * @param LOCATIONS
	 */
	public Note(int MIDINUMBER, ArrayList<Integer[]> LOCATIONS) {
		midiNumber = MIDINUMBER;
		locations = new ArrayList<Integer[]>(LOCATIONS);
		init();
	}
	
	/**
	 * Initializes the paints
	 */
	public void init() {
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
		
		number = midiNumber % 12;
		octave = (int)Math.floor(midiNumber/12);
	}
	
	/**
	 * Gets the path of 
	 * @param LOCATION
	 * @return
	 */
	public Path getHexagonPath(Integer[] LOCATION) {
    Path toReturn = new Path();
    double angle = Math.PI / 3;
    toReturn.moveTo(keySize, 0);
    for(int i = 1; i < 7; i++) {
    	toReturn.lineTo((int)(keySize*Math.cos(i*angle)), (int)(keySize*Math.sin(i*angle)));
    }
    toReturn.close();
    toReturn.offset((float)LOCATION[0], (float)LOCATION[1]);
    return toReturn;
	}
	
	/**
	 * Adds a key location to the note
	 * @param LOCATION
	 */
	public void addLocation(Integer[] LOCATION) {
		locations.add(LOCATION);
		paths.add(getHexagonPath(LOCATION));
	}
	
	/**
	 * Plays the note and presses the keys
	 */
	public void play() {
		dirty = true;
		pressed = true;
		instrument.play(midiNumber);
	}
	
	/**
	 * Stops the note and unpresses the keys
	 */
	public void stop() {
		dirty = true;
		pressed = false;
	}
	
	public boolean inScale() {
		int relative = (number-root)%12;
		for (int i = 0; i < scale.length; i++) {
			if (scale[i]==(relative+scale[mode%scale.length])%12) {
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * Returns a Paint object to color the key
	 * @return
	 */
	public Paint getColor() {
		if (pressed)
			return pressedPaint;
		if ((number-root)%12==0&&showRoots)
			return rootPaint;
		if (!inScale()&&showScales)
			return blackPaint;
		return whitePaint;
	}
	
	/**
	 * Paints a key at each associated location
	 * @param canvas
	 */
	public void paint(Canvas canvas) {
		paint(canvas, false);
	}
	public void paint(Canvas canvas, boolean all) {
		if (!dirty&&!allDirty&&!all) {
			return;
		}
		Path path;
		Paint color = getColor();
		for (int i = 0; i < paths.size(); i++) {
			path = paths.get(i);
    	canvas.drawPath(path, color);
    	canvas.drawPath(path, borderPaint);
    	if (showNotes) {
    		if (color==blackPaint)
    			canvas.drawText(octave+" | "+number, locations.get(i)[0], locations.get(i)[1], whitePaint);
    		else
    			canvas.drawText(octave+" | "+number, locations.get(i)[0], locations.get(i)[1], borderPaint);
			}
		}
		dirty = false;
	}
}