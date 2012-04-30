package com.umrk.isomorph;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;


public class Pair {
	protected Paint paint = new Paint();
	protected Path path = new Path();
	protected ArrayList<Note> conflicts;
	protected Single single1;
	protected Single single2;
	protected static long maxAge = 100000;
	protected static Integer[][] paintColors = {
		{255, 255, 0},
		{0, 255, 0},
		{255, 255, 0},
		{255, 0, 0},
		{0, 0, 255},
		{127, 0, 127},
		{127, 0, 0},
		{255, 0, 127},
		{0, 127, 127},
		{127, 255, 0},
		{255, 127, 0},
		{0, 255, 255}};
	
	public Pair(Single SINGLE1, Single SINGLE2) {
		single1 = SINGLE1;
		single2 = SINGLE2;
		int color = Math.abs(single1.note.midiNumber-single2.note.midiNumber)%12;
		paint.setARGB(255, paintColors[color][0], paintColors[color][1], paintColors[color][2]);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		path.moveTo(single1.origin[0], single1.origin[1]);
		path.lineTo(single2.origin[0], single2.origin[1]);
		conflicts = new ArrayList<Note>();
		for (ArrayList<Integer[]> a : Keyboard.locations) {
			for (Integer[] location : a) {
				if (Keyboard.keySize < Math.abs(((single1.origin[0]-single2.origin[0])*(single1.origin[1]-location[1])-(single1.origin[0]-location[0])*(single2.origin[1]-single1.origin[1])))/Math.sqrt(Math.hypot(single2.origin[0]-single1.origin[0], single2.origin[1]-single1.origin[1]))) {
					conflicts.add(Keyboard.getNotes(location[0], location[1]).get(0));
				}
			}
		}
	}
	
	public void paint(Canvas canvas) {
		for (Note note : conflicts) {
			note.paint(canvas, true);
		}
		long time = SystemClock.uptimeMillis();
		long age1 = single1.getAge(time);
		long age2 = single2.getAge(time);
		if (age1 > maxAge) {
			Keyboard.singles.remove(single1);
		}
		if (age2 > maxAge) {
			Keyboard.singles.remove(single2);
		}
		if (age1 > maxAge || age2 > maxAge) {
			Keyboard.pairs.remove(this);
			return;
		}
		int alpha = 255-Math.round((255*Math.max(age1, age2)/maxAge));
		paint.setAlpha(alpha);
		canvas.drawPath(path, paint);
	}
	
	public void scrub() {
		for (Note note : conflicts) {
			note.dirty = true;
		}
	}
}