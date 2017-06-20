package com.evpad.common.util;

import java.io.File;

import com.evpad.common.model.Model_Auth;
import com.evpad.common.model.Model_Msg;
import com.evpad.common.model.Model_MsgConten;
import com.evpad.common.model.Model_UpMsg;
import com.evpad.common.view.RegionLimitPrompt;
import com.evpad.livehd.activity.Configs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

/**
 * @author Administrator
 *
 */
public class HttpInterfaceUtil {

	public HttpInterfaceUtil() {
		// TODO Auto-generated constructor stub
	}

	private static WindowManager mWindowManager;
	private static LayoutParams mLayoutParam;
	private static RegionLimitPrompt mRegionPrompt;

	/**
	 * do auth
	 * 
	 * @param Url
	 */
	public static void DoAuth(String Url, final Handler mHandler) {
		// TODO Auto-generated method stub
		System.out.println("DoAuth Url = "+Url);
		FinalHttp fn = new FinalHttp();
		fn.post(Url, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				System.out.println("DoAuth..................onFailure");
				mHandler.sendEmptyMessage(CommonConfigs.AUTH_FAILED);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				System.out.println("DoAuth..................onSuccess");
				try {
					Model_Auth auth = new Model_Auth();
					Gson g = new Gson();
					auth = g.fromJson(t.toString(), new TypeToken<Model_Auth>() {
					}.getType());
					if (auth.getCode().equals("0")) {
						System.out.println("DoAuth..................==0");
						mHandler.sendEmptyMessage(CommonConfigs.AUTH_SUCCESS);
						dismissRegionLimitPrompt();
					} else {
						System.out.println("DoAuth..................!=0");
						mHandler.sendEmptyMessage(CommonConfigs.AUTH_FAILED);
						showRegionLimitPrompt(auth.getMsg());
					}
				} catch (Exception e) {
					// TODO: handle exception

				}

			}

		});
	}

	/**
	 * do update for app
	 * 
	 * @param mContext
	 *            上下文
	 * @param pkgname
	 *            当前应用名称
	 * 
	 */
	public void DoUpData(final Context mContext, String Url) {
		// TODO Auto-generated method stub
		System.out.println("DoUpData Url = "+Url);
		FinalHttp fn = new FinalHttp();
		fn.post(Url, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				System.out.println("DoUpData  onFailure...................");
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				System.out.println("DoUpData  onSuccess...................");
				super.onSuccess(t);
				Log.d("updatare:", t.toString());
				try {
					Model_UpMsg upmsg = new Model_UpMsg();
					Gson g = new Gson();
					upmsg = g.fromJson(t.toString(), new TypeToken<Model_UpMsg>() {
					}.getType());
					if (upmsg.getCode().equals("0")) {
						final String DownUrl = upmsg.getUrl();
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setTitle("Info");
						builder.setMessage(upmsg.getMsg());

						builder.setCancelable(false);
						builder.setPositiveButton("OK", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								FinalHttp fn = new FinalHttp();

								final String path = Environment.getExternalStorageDirectory() + "/DownUrl";
								Log.d("down", path);
								Log.d("downUrl", DownUrl);
								fn.download(DownUrl, path, new AjaxCallBack<File>() {

									@Override
									public void onSuccess(File t) {
										// TODO Auto-generated method stub
										super.onSuccess(t);
										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setDataAndType(Uri.fromFile(new File(path)),
												"application/vnd.android.package-archive");
										mContext.startActivity(intent);
									}

									@Override
									public void onLoading(long count, long current) {
										// TODO Auto-generated method stub
										Log.d("DOWN", count + "----" + current);
										super.onLoading(count, current);
									}

								});
							}
						});
						builder.setNegativeButton("EXIT", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						builder.create().show();
					}

				} catch (Exception e) {
					// TODO: handle exception

				}

			}

		});
	}

	/**
	 * @param Url
	 * @param mTv
	 */
	public void DoMsg(String Url, final TextView mTv) {
		// TODO Auto-generated method stub
		FinalHttp fn = new FinalHttp();
		Log.d("msgUrl:", Url);
		fn.get(Url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				try {
					Model_Msg msg = new Model_Msg();
					Gson g = new Gson();
					msg = g.fromJson(t.toString(), new TypeToken<Model_Msg>() {
					}.getType());
					Model_MsgConten con = msg.getMsg().get(0);
					mTv.setText(con.getBody());
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		});
	}

	private static void showRegionLimitPrompt(String text) {
		mLayoutParam.type = LayoutParams.TYPE_PHONE;
		mLayoutParam.alpha = PixelFormat.RGB_888;
		mLayoutParam.alpha = 0.9f;
		mLayoutParam.x = 0;
		mLayoutParam.y = 0;

		mLayoutParam.width = WindowManager.LayoutParams.MATCH_PARENT;
		mLayoutParam.height = WindowManager.LayoutParams.MATCH_PARENT;
		mRegionPrompt.setPromptText(text);
		try {
			mWindowManager.addView(mRegionPrompt, mLayoutParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dismissRegionLimitPrompt() {
		if (null != mRegionPrompt)
			try {
				mWindowManager.removeView(mRegionPrompt);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
