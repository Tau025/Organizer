package com.devtau.organizer;

import android.app.Application;

public class OrganizerApp extends Application {

	private static OrganizerApp sApp;
	private PermissionObserver mPermissionObserver;

	public OrganizerApp() {
		sApp = this;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		mPermissionObserver = new PermissionObserver(this);
	}

	public static OrganizerApp get() {
		return sApp;
	}

	public PermissionObserver getPermissionObserver() {
		return mPermissionObserver;
	}
}
