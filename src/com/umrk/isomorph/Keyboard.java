package com.umrk.isomorph;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Keyboard extends View {
	
	protected static HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	protected static ArrayList<ArrayList<Integer[]>> locations = new ArrayList<ArrayList<Integer[]>>();
	protected static ArrayList<Pair> pairs = new ArrayList<Pair>();
	protected static ArrayList<Single> singles = new ArrayList<Single>();
	protected int height;
	protected int width;
	protected static int rows;
	protected static int columns;
	protected static int keySize = 60;
	protected static HashMap<Integer[], Note> noteMap = new HashMap<Integer[], Note>();
	protected HashMap<Integer, ArrayList<Note>> pointerMap = new HashMap<Integer, ArrayList<Note>>();
	protected int[] keyPattern = {5, 7};
	protected int startNumber = 35;
	protected Bitmap bitmap;
	protected Handler timeHandler = new Handler();
	protected long time = 0L;
	protected static Instrument instrument;
	protected static Paint pairPaint = new Paint();
	protected static int pairLimit = 5;
	protected static int historyLimit = 2;
	protected static boolean showPairs = true;
	
	public Keyboard(Context context)	{
		super(context);
		instrument = new Instrument(context);
		Note.instrument=instrument;
		Note.keySize = keySize;
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		fillLocations();
		fillNotes();
		pairPaint.setStyle(Paint.Style.STROKE);
		pairPaint.setAntiAlias(true);
		this.invalidate();
		OnClickListener timeListener = new OnClickListener() {
		  public void onClick(View v) {
		    time = SystemClock.uptimeMillis();
		    timeHandler.removeCallbacks(updateTimeTask);
		    timeHandler.postDelayed(updateTimeTask, 100);
		  }
		};
	}

	public Keyboard(Context context, AttributeSet attributes) {
		super(context, attributes);
		instrument = new Instrument(context);
		Note.instrument=instrument;
		Note.keySize = keySize;
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		System.out.println("Inflated");
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		fillLocations();
		fillNotes();
		pairPaint.setStyle(Paint.Style.STROKE);
		pairPaint.setAntiAlias(true);
		this.invalidate();
		OnClickListener timeListener = new OnClickListener() {
		  public void onClick(View v) {
		    time = SystemClock.uptimeMillis();
		    timeHandler.removeCallbacks(updateTimeTask);
		    timeHandler.postDelayed(updateTimeTask, 100);
		  }
		};
	}
	
	public void fillLocations() {
		int xInterval = (int)(keySize*1.5);
		int yInterval = (int)(Math.sin(Math.PI/3)*keySize*2);
		int offset = (int)(Math.sin(Math.PI/3)*keySize);
		int useOffset;
		rows = (int)(height/yInterval)+1;
		columns = (int)(width/xInterval)+1;
		for (int y = 0; y < rows; y++) {
			ArrayList<Integer[]> row = new ArrayList<Integer[]>();
			locations.add(locations.size(), row);
			useOffset = 0;
			for (int x = 0; x < columns; x++) {
				row.add(row.size(), new Integer[] {(int)(keySize/4)+x*xInterval, height-(yInterval*y+offset*useOffset)-(int)(keySize*1.5)});
				useOffset = 1-useOffset;
			}
		}
	}
	
	public void setMode(int mode) {
		Note.mode = mode;
		redraw();
	}
	
	public void setScale(Integer[] scale) {
		Note.scale = scale;
		redraw();
	}
	
	public void setRoot(int root) {
		Note.root = root;
		redraw();
	}
	
	public void setPairLimit(int limit) {
		pairLimit = limit;
		this.invalidate();
	}
	
	public void setHistoryLimit(int limit) {
		historyLimit = limit;
		this.invalidate();
	}
	
	public void redraw() {
		for (Integer note : notes.keySet()) {
			notes.get(note).dirty=true;
		}
		this.invalidate();
	}
	
	public void toggleShowRoots() {
		Note.showRoots = !Note.showRoots;
		redraw();
	}
	
	public void toggleShowNotes() {
		Note.showNotes = !Note.showNotes;
		redraw();
	}
	
	public void toggleShowScales() {
		Note.showScales = !Note.showScales;
		redraw();
	}
	
	public void toggleShowConnections() {
		showPairs = !showPairs;
		redraw();
	}
	
	public void clearHistory() {
		pairs.clear();
		redraw();
	}
	
	public void reset() {
		System.out.println("Resetting");
		locations.clear();
		notes.clear();
		noteMap.clear();
		fillLocations();
		fillNotes();
		this.invalidate();
	}
	
	public void fillNotes() {
		int midiNumber;
		int rowStart;
		for (int y = 0; y < rows; y++) {
			rowStart = startNumber+keyPattern[1]*y;
			midiNumber = rowStart;
			for (int x = 0; x < columns; x+=2) {
				if (!notes.containsKey(midiNumber)) {
					notes.put(midiNumber, new Note(midiNumber, locations.get(y).get(x)));
				}
				else {
					notes.get(midiNumber).addLocation(locations.get(y).get(x));
				}
				noteMap.put(locations.get(y).get(x), notes.get(midiNumber));
				midiNumber += keyPattern[0]-(keyPattern[1]-keyPattern[0]);
			}
			midiNumber = rowStart+keyPattern[0];
			for (int x = 1; x < columns; x+=2) {
				if (!notes.containsKey(midiNumber)) {
					notes.put(midiNumber, new Note(midiNumber, locations.get(y).get(x)));
				}
				else {
					notes.get(midiNumber).addLocation(locations.get(y).get(x));
				}
				noteMap.put(locations.get(y).get(x), notes.get(midiNumber));
				midiNumber += keyPattern[0]-(keyPattern[1]-keyPattern[0]);
			}
		}
	}
	
	public static ArrayList<Note> getNotes(int x, int y) {
		ArrayList<Note> toReturn = new ArrayList<Note>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (Math.hypot(locations.get(i).get(j)[0]-x, locations.get(i).get(j)[1]-y)<keySize*0.92) {
					toReturn.add(noteMap.get(locations.get(i).get(j)));
				}
			}
		}
		return toReturn;
	}

	public void onDraw(Canvas canvas)	{ 
		Canvas tempCanvas = new Canvas(bitmap);
		for (Integer note : notes.keySet()) {
			notes.get(note).paint(tempCanvas);
		}
		//drawPaths(tempCanvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}
	
	public void drawPaths(Canvas tempCanvas) {
		for (int i = 0; i < pairs.size(); i++) {
			pairs.get(i).paint(tempCanvas);
		}
	}
	
	public void updatePairs(Single single) {
		Single single2;
		for (int i = 0; i < singles.size(); i++) {
			single2 = singles.get(i);
			pairs.add(new Pair(single, single2));
		}
		singles.add(single);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerID = event.getPointerId(pointerIndex);
		int x = (int)event.getX(pointerIndex);
		int y = (int)event.getY(pointerIndex);
		Single single;
		if (action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN) {
			pointerMap.put(pointerID, getNotes(x, y));
			for (Note note : pointerMap.get(pointerID)) {
				note.play();
				Integer[] origin = {x, y};
				//single = new Single(note, origin, SystemClock.uptimeMillis());
				//updatePairs(single);
			}
			this.invalidate();
		}
		else if (action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_POINTER_UP) {
			for (Note note : pointerMap.get(pointerID)) {
				note.stop();
			}
			pointerMap.remove(pointerID);
			this.invalidate();
		}
		return true;
	}

	public void setDimension1(int position) {
		System.out.println("Setting Pattern 1");
		keyPattern[0]=position;
		reset();
	}
	
	public void setDimension2(int position) {
		System.out.println("Setting Pattern 2");
		keyPattern[1]=position;
		reset();		
	}
	
	private Runnable updateTimeTask = new Runnable() {
	   public void run() {
	     time = SystemClock.uptimeMillis();
	     Log.d("Timer", "Reloading");
	     timeHandler.postDelayed(this, 1000);
	   }
	};
}