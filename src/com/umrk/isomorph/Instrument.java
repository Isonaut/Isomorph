package com.umrk.isomorph;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.umrk.isomorph.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Instrument {

	public static final int POLYPHONY_COUNT = 8;
	private SoundPool mSoundPool; 
	protected static HashMap<Integer, Integer> mSounds; 
	protected static HashMap<Integer, Float> mRates;
	protected static HashMap<Integer, Integer> mRootNotes;
	private AudioManager  mAudioManager;
	private Context mContext;

	public Instrument(Context context) {
		mContext = context;
		init(context);

		Pattern pat = Pattern.compile("^pno0*([0-9]+)v");
		Class raw = R.raw.class;
		Field[] fields = raw.getFields();
		for (Field field : fields) {
		    try {
		    	String fieldName = field.getName();
		        if (fieldName.startsWith("pno", 0)) {
		        	int midiNoteNumber;
		        	Matcher mat = pat.matcher(fieldName);
		        	if (mat.find())	{
		        		String midiNoteNumberStr = mat.group(1);
		        		midiNoteNumber = Integer.parseInt(midiNoteNumberStr);
		        		int fieldValue = field.getInt(null);
		        		addSound(midiNoteNumber, fieldValue);
		        	  mRootNotes.put(midiNoteNumber, midiNoteNumber);
		        		mRates.put(midiNoteNumber, 1.0f);
		        	}
		      }
		    }
		    catch(IllegalAccessException e) {
		        Log.e("REFLECTION", String.format("%s threw IllegalAccessException.", field.getName()));
		    }
		}

		float previousRate = 1.0f;
		int previousRootNote = 21;
		for (int noteId = 21; noteId < 110; noteId++)	{
			if (mRootNotes.containsKey(noteId))	{
				previousRootNote = noteId;
				previousRate = 1.0f;
			}
			else {
				mRootNotes.put(noteId, previousRootNote);
				double oneTwelfth = 1.0/12.0;
		    double newRate = previousRate * Math.pow(2, oneTwelfth);
		    previousRate = (float)newRate;
				mRates.put(noteId, previousRate);
			}
		}
	}

	public void init(Context context)	{ 
		mSoundPool = new SoundPool(POLYPHONY_COUNT, AudioManager.STREAM_MUSIC, 0); 
		mSounds = new HashMap<Integer, Integer>(); 
		mRates = new HashMap<Integer, Float>(); 
		mRootNotes = new HashMap<Integer, Integer>(); 
		mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	}

	public void addSound(int index, int soundId) {
		mSounds.put(index, mSoundPool.load(mContext, soundId, 1));
	}

	public int play(int midiNoteNumber)	{ 
		int index = mRootNotes.get(midiNoteNumber);
	  float rate = mRates.get(midiNoteNumber);
	  
		int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		return  mSoundPool.play(mSounds.get(index), streamVolume, streamVolume, 1, 0, rate); 
	}

	public void stop(int streamId) { 
		mSoundPool.stop(streamId);
	}

	public void loop(int index)	{ 
		int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		mSoundPool.play(mSounds.get(index), streamVolume, streamVolume, 1, -1, 1f); 
	}
}
