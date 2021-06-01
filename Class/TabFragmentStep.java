package com.Class;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;



import android.widget.TextView;
import android.widget.Toast;

public class TabFragmentStep extends Fragment implements OnClickListener,
		OnChronometerTickListener, OnCheckedChangeListener {
	private static final String TAG = TabFragmentStep.class.getSimpleName();


	SharedPreferences mySharedPreferences;
	SharedPreferences.Editor editor;

	private View view;

	private TextView tvPercent;
	private ProgressBar pbPercent;
	private TextView tvGoal;
	private TextView tvSteps;
	private Button btReset;
	
	private Chronometer cmPasstime;
	private Button btControl;

	private TextView tvCalorie;
	private TextView tvDistance;
	private TextView tvSpeed;

	private TextView tvSex;
	private TextView tvHeight;
	private TextView tvWeight;
	private TextView tvAge;
	private TextView tvSteplen;

	private RadioGroup rgMode;
	private RadioButton rbStepNormal;
	private RadioButton rbStepPocket;

	private AlertDialog.Builder dialog;
	private NumberPicker numberPicker;

	
	private float calorie;
	private float distance;
	private float speed;

	private String sex;
	private float height;
	private float weight;
	private float steplen;
	private int age;

	private int steps;
	private int seconds;
	
	public static boolean isOpenMap = false;
	
	protected void calAddData() {

		distance = steps * steplen / (100);		//供分辨公尺
		tvDistance.setText(distance + "");
		float msSpeed;

		if (seconds == 0) {
			msSpeed = 0;
		} else {
			msSpeed = distance / seconds;
		}

		float kmhSpeed = (float) (3.6 * msSpeed); //公尺/秒,公里/小時
		speed = kmhSpeed;	//速率
		tvSpeed.setText(speed + "");

		calorie = (float) (weight * steps * steplen * 0.01 * 0.01);
		
		tvCalorie.setText(calorie + "");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.tab_fragment_step, container,
				false);
		Log.i(TAG, "onCreateView");

		mSubThread();

		initView();

		Intent intent = new Intent(getActivity(), LightSensorService.class);
		getActivity().startService(intent);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");

		restorePersonalData();
		initPersonalData();

	}

	
	private void restorePersonalData() {
		
		mySharedPreferences = getActivity().getSharedPreferences(
				"personalData", Activity.MODE_PRIVATE);
		
		sex = mySharedPreferences.getString("sex", "男");
		height = mySharedPreferences.getFloat("height", 175);
		weight = mySharedPreferences.getFloat("weight", 65);
		steplen = mySharedPreferences.getFloat("steplen", 80);
		age = mySharedPreferences.getInt("age", 24);
		sensitive = mySharedPreferences.getFloat("sensitive", 8);
		lightive = mySharedPreferences.getFloat("lightive", 10);
		LIGHT_BORDER = lightive;

	}

	private void initPersonalData() {
		tvSex.setText(sex);
		tvHeight.setText(height + "");
		tvWeight.setText(weight + "");
		tvSteplen.setText(steplen + "");
		tvAge.setText(age + "");
		tvSensitive.setText(sensitive + "");
		AccelerometerSensorListener.SENSITIVITY = sensitive;
		tvLightive.setText(lightive + "");
		LIGHT_BORDER = lightive;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void savePersonalData() {
		Log.i(TAG, "save data");

		editor = mySharedPreferences.edit();

		editor.putString("sex", sex);
		editor.putFloat("height", height);
		editor.putFloat("weight", weight);
		editor.putFloat("steplen", steplen);
		editor.putInt("age", age);
		editor.putFloat("sensitive", sensitive);
		editor.putFloat("lightive", lightive);
		editor.commit();
	}

	private void initView() {
		tvPercent = (TextView) view.findViewById(R.id.tv_percent);
		pbPercent = (ProgressBar) view.findViewById(R.id.pb_percent);
		tvGoal = (TextView) view.findViewById(R.id.tv_goal);
		tvGoal.setOnClickListener(this);
		tvSteps = (TextView) view.findViewById(R.id.tv_steps);
		tvSteps.setOnClickListener(this);
		btReset = (Button) view.findViewById(R.id.bt_reset);
		btReset.setOnClickListener(this);
		cmPasstime = (Chronometer) view.findViewById(R.id.cm_passtime);
		btControl = (Button) view.findViewById(R.id.bt_control);
		btControl.setOnClickListener(this);
		tvCalorie = (TextView) view.findViewById(R.id.tv_calorie);
		tvDistance = (TextView) view.findViewById(R.id.tv_distance);
		tvSpeed = (TextView) view.findViewById(R.id.tv_speed);

		tvSex = (TextView) view.findViewById(R.id.tv_sex);
		tvSex.setOnClickListener(this);
		tvHeight = (TextView) view.findViewById(R.id.tv_height);
		tvHeight.setOnClickListener(this);
		tvWeight = (TextView) view.findViewById(R.id.tv_weight);
		tvWeight.setOnClickListener(this);
		tvAge = (TextView) view.findViewById(R.id.tv_age);
		tvAge.setOnClickListener(this);
		tvSensitive = (TextView) view.findViewById(R.id.tv_sensitive);
		tvSensitive.setOnClickListener(this);
		tvLightive = (TextView) view.findViewById(R.id.tv_lightive);
		tvLightive.setOnClickListener(this);
		tvSteplen = (TextView) view.findViewById(R.id.tv_steplen);
		tvSteplen.setOnClickListener(this);

		rgMode = (RadioGroup) view.findViewById(R.id.step_mode);
		rgMode.setOnCheckedChangeListener(this);
		rbStepNormal = (RadioButton) view.findViewById(R.id.step_normal);
		rbStepPocket = (RadioButton) view.findViewById(R.id.step_pocket);
		tvLight = (TextView) view.findViewById(R.id.tv_light);

		cvLight = (ChartView) view.findViewById(R.id.cv_light);

		pbPercent.setMax(10000);
		cmPasstime.setOnChronometerTickListener(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// 相当于Fragment的onResume
			// Log.v("tag", "resume");
			// Toast.makeText(getActivity(), "resume",
			// Toast.LENGTH_SHORT).show();
		} else {
			// 相当于Fragment的onPause
			// Log.v("tag", "pause");

		}
	}

	
	@Override
	public void onChronometerTick(Chronometer arg0) {
		seconds++;
		cmPasstime.setText(formatseconds());
	}

	public String formatseconds() {
		String hh = seconds / 3600 > 9 ? seconds / 3600 + "" : "0" + seconds
				/ 3600;
		String mm = (seconds % 3600) / 60 > 9 ? (seconds % 3600) / 60 + ""
				: "0" + (seconds % 3600) / 60;
		String ss = (seconds % 3600) % 60 > 9 ? (seconds % 3600) % 60 + ""
				: "0" + (seconds % 3600) % 60;

		return hh + ":" + mm + ":" + ss;
	}

}