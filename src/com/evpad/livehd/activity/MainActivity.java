package com.evpad.livehd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import com.evpad.common.util.AppUtil;
import com.evpad.common.util.CommonConfigs;
import com.evpad.common.util.DeviceUtil;
import com.evpad.common.util.HttpInterfaceUtil;
import com.example.evpadlivehd.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context mContext;
	private Handler myHandler;
	private TimerTask mTimerTask;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TextView mtv = null;
		// new HttpInterfaceUtil().DoMsg(Configs.URL.MSg + "?mac=" +
		// DeviceUtil.getMac() + "&version="
		// + AppUtil.getVersionCode(this, "com.moon.android.evpad2"), mtv);

		mContext = this;
		HandlerRegister();
		if(AppUtil.isConnectingToInternet(mContext)){
			System.out.println("NETWORK_OK");
		}else{
			System.out.println("NETWORK_NO");
		}
		TimerRegister();
		DoUpGrade(myHandler);
		DoVerify(myHandler);

	}

	@SuppressLint("HandlerLeak")
	private void HandlerRegister() {
		// TODO Auto-generated method stub
		myHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case CommonConfigs.AUTH_FAILED:
					Toast.makeText(mContext, "AUTH_FAILED", Toast.LENGTH_LONG).show();
					break;
				case CommonConfigs.AUTH_SUCCESS:
					Toast.makeText(mContext, "AUTH_SUCCESS", Toast.LENGTH_LONG).show();
					gotoLiveActivity();
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void TimerRegister() {
		// TODO Auto-generated method stub
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
			}
		};
		mTimer = new Timer();
	}

	private void DoVerify(Handler myHandler) {
		// TODO Auto-generated method stub
		HttpInterfaceUtil.DoAuth(CommonConfigs.URL.Auth + "?mac=" + DeviceUtil.getMac(), myHandler);
	}

	private void DoUpGrade(Handler myHandler) {
		// TODO Auto-generated method stub
		new HttpInterfaceUtil().DoUpData(this, CommonConfigs.URL.UpData + "?mac=" + DeviceUtil.getMac() + "&version="
				+ AppUtil.getVersionCode(this, Configs.PKG_NAME));
	}
	
	private void gotoLiveActivity() {
		finish();
		startActivity(this, LiveActivity.class);
	}
	
	public void startActivity(Activity context, Class classs){
		Intent intent = new Intent();
    	intent.setClass(context, classs);
    	context.startActivity(intent);
	}
}
