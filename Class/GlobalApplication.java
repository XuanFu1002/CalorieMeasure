package com.Class;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class GlobalApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}
}
