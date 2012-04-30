package com.umrk.isomorph;

import java.util.ArrayList;
import com.umrk.isomorph.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

public class Controller extends Activity implements OnItemSelectedListener, OnClickListener {
	
	protected Keyboard keyboard;
	private Spinner spinnerKeys;
	private Spinner spinnerScales;
	private Spinner spinnerModes;
	private Spinner spinnerDimension1;
	private Spinner spinnerDimension2;
	private Spinner spinnerPairLimit;
	private Spinner spinnerHistoryLimit;
	private ToggleButton toggleButtonRoots;
	private ToggleButton toggleButtonNotes;
	private ToggleButton toggleButtonScales;
	private ToggleButton toggleButtonConnections;
	private ToggleButton[] toggleButtonsTones;
	private Button buttonClear;
	boolean[] scale = new boolean[12];
	private Integer[][] scales = {{0, 2, 4, 5, 7, 9, 11},
																{0, 3, 5, 6, 7, 10},
																{0, 2, 4, 7, 9},
																{0, 2, 3, 5, 7, 8, 11},
																{0, 2, 3, 5, 7, 9, 11},
																{0, 2, 3, 6, 7, 8, 11}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,	WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.main);
		keyboard = (Keyboard)findViewById(R.id.keyboard);

		spinnerKeys = (Spinner)findViewById(R.id.spinnerKeys);
		spinnerKeys.setOnItemSelectedListener(this);
		spinnerScales = (Spinner)findViewById(R.id.spinnerScales);
		spinnerScales.setOnItemSelectedListener(this);
		spinnerModes = (Spinner)findViewById(R.id.spinnerModes);
		spinnerModes.setOnItemSelectedListener(this);
		toggleButtonRoots = (ToggleButton)findViewById(R.id.toggleButtonRoots);
		toggleButtonRoots.setOnClickListener(this);
		toggleButtonRoots.setChecked(true);
		toggleButtonNotes = (ToggleButton)findViewById(R.id.toggleButtonNotes);
		toggleButtonNotes.setOnClickListener(this);
		toggleButtonNotes.setChecked(true);
		toggleButtonScales = (ToggleButton)findViewById(R.id.toggleButtonScales);
		toggleButtonScales.setOnClickListener(this);
		toggleButtonScales.setChecked(true);
		toggleButtonConnections = (ToggleButton)findViewById(R.id.toggleButtonConnections);
		toggleButtonConnections.setOnClickListener(this);
		toggleButtonConnections.setChecked(true);
		buttonClear = (Button)findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(this);

		spinnerDimension1 = (Spinner)findViewById(R.id.spinnerDimension1);
		spinnerDimension1.setOnItemSelectedListener(this);
		spinnerDimension1.setSelection(3);
		spinnerDimension2 = (Spinner)findViewById(R.id.spinnerDimension2);
		spinnerDimension2.setOnItemSelectedListener(this);
		spinnerDimension2.setSelection(6);
		toggleButtonsTones = new ToggleButton[12];
		toggleButtonsTones[0] = (ToggleButton)findViewById(R.id.toggleButton0);
		toggleButtonsTones[1] = (ToggleButton)findViewById(R.id.toggleButton1);
		toggleButtonsTones[2] = (ToggleButton)findViewById(R.id.toggleButton2);
		toggleButtonsTones[3] = (ToggleButton)findViewById(R.id.toggleButton3);
		toggleButtonsTones[4] = (ToggleButton)findViewById(R.id.toggleButton4);
		toggleButtonsTones[5] = (ToggleButton)findViewById(R.id.toggleButton5);
		toggleButtonsTones[6] = (ToggleButton)findViewById(R.id.toggleButton6);
		toggleButtonsTones[7] = (ToggleButton)findViewById(R.id.toggleButton7);
		toggleButtonsTones[8] = (ToggleButton)findViewById(R.id.toggleButton8);
		toggleButtonsTones[9] = (ToggleButton)findViewById(R.id.toggleButton9);
		toggleButtonsTones[10] = (ToggleButton)findViewById(R.id.toggleButton10);
		toggleButtonsTones[11] = (ToggleButton)findViewById(R.id.toggleButton11);
		toggleButtonsTones[0].setOnClickListener(this);
		toggleButtonsTones[1].setOnClickListener(this);
		toggleButtonsTones[2].setOnClickListener(this);
		toggleButtonsTones[3].setOnClickListener(this);
		toggleButtonsTones[4].setOnClickListener(this);
		toggleButtonsTones[5].setOnClickListener(this);
		toggleButtonsTones[6].setOnClickListener(this);
		toggleButtonsTones[7].setOnClickListener(this);
		toggleButtonsTones[8].setOnClickListener(this);
		toggleButtonsTones[9].setOnClickListener(this);
		toggleButtonsTones[10].setOnClickListener(this);
		toggleButtonsTones[11].setOnClickListener(this);
		spinnerPairLimit = (Spinner)findViewById(R.id.spinnerPairLimit);
		spinnerPairLimit.setOnItemSelectedListener(this);
		spinnerPairLimit.setSelection(5);
		spinnerHistoryLimit = (Spinner)findViewById(R.id.spinnerHistoryLimit);
		spinnerHistoryLimit.setOnItemSelectedListener(this);
		spinnerHistoryLimit.setSelection(2);
		
		syncButtonsToScale(0);
	}
	
	public void syncScaleToButtons() {
		int scaleLength = 0;
		for (int i = 0; i < 12; i++) {
			if (scale[i]) {
				scaleLength++;
			}
		}
		Integer[] newScale = new Integer[scaleLength];
		scaleLength = 0;
		for (int i = 0; i < 12; i++) {
			if (scale[i]) {
				newScale[scaleLength] = i;
				scaleLength++;
			}
		}
		keyboard.setScale(newScale);
	}
	
	public void syncButtonsToScale(int scaleIndex) {
		int nextNote = 0;
		for (int i = 0; i < 12; i++) {
			if (scales[scaleIndex][nextNote%scales[scaleIndex].length] == i) {
				scale[i] = true;
				toggleButtonsTones[i].setChecked(true);
				nextNote++;
			}
			else {
				scale[i] = false;
				toggleButtonsTones[i].setChecked(false);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v==toggleButtonsTones[0]) {
			((ToggleButton)v).setChecked(true);
			return;
		}
		for (int i = 1; i < 12; i++) {
			if (v==toggleButtonsTones[i]) {
				scale[i] = !scale[i];
				syncScaleToButtons();
				return;
			}
		}
		if (v==toggleButtonRoots) {
			keyboard.toggleShowRoots();
			return;
		}
		if (v==toggleButtonNotes) {
			keyboard.toggleShowNotes();
			return;
		}
		if (v==toggleButtonScales) {
			keyboard.toggleShowScales();
			return;
		}
		if (v==toggleButtonConnections) {
			keyboard.toggleShowConnections();
			return;
		}
		if (v==buttonClear) {
			keyboard.clearHistory();
			return;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
		if (spinner == spinnerScales) {
			keyboard.setScale(scales[position]);
			syncButtonsToScale(position);
			return;
		}
		if (spinner == spinnerKeys) {
			keyboard.setRoot(position);
			return;
		}
		if (spinner == spinnerModes) {
			keyboard.setMode(position);
			return;
		}
		if (spinner == spinnerDimension1) {
			keyboard.setDimension1(position+1);
			return;
		}
		if (spinner == spinnerDimension2) {
			keyboard.setDimension2(position+1);
			return;
		}
		if (spinner == spinnerPairLimit) {
			keyboard.setPairLimit(position+1);
			return;
		}
		if (spinner == spinnerHistoryLimit) {
			keyboard.setHistoryLimit(position+1);
			return;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}