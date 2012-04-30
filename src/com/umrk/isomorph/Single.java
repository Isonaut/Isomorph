package com.umrk.isomorph;

public class Single {
	protected Note note;
	protected Integer[] origin;
	protected long startTime;
	
	public Single(Note NOTE, Integer[] ORIGIN, long STARTTIME) {
		note = NOTE;
		origin = ORIGIN;
		startTime = STARTTIME;
	}
	
	public long getAge(long current) {
		return current-startTime;
	}
}