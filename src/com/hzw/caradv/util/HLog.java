package com.hzw.caradv.util;

import android.util.Log;

public class HLog {
	private static final String TAG = "aaa";

	public static void d(String log){
		if(getIsLogMode()){
			Log.d(TAG, log);
		}
	}

	private static boolean getIsLogMode() {
		// TODO Auto-generated method stub
		return true;
	}
}
