package com.evpad.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Administrator
 *
 */
public class AppUtil {

	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}

	/**
	 * @param paramContext
	 * @param pkgname
	 * @return
	 */
	public static int getVersionCode(Context paramContext, String pkgname) {
		try {
			int i = paramContext.getPackageManager().getPackageInfo(pkgname, 0).versionCode;
			return i;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return -1;
	}

	/**
	 * @param paramContext
	 * @param pkgname
	 * @return
	 */
	public static String getVersionName(Context paramContext, String pkgname) {
		try {
			String versionName = paramContext.getPackageManager().getPackageInfo(pkgname, 0).versionName;
			return versionName;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return "";
	}
	
	public static boolean isRoot() {
		try {
			Process process = Runtime.getRuntime().exec("su");
			process.getOutputStream().write("exit\n".getBytes());
			process.getOutputStream().flush();
			int i = process.waitFor();
			if (0 == i) {
				process = Runtime.getRuntime().exec("su");
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;

	}
	
	public static boolean DoShell(String commod) {
		boolean resault = false;
		Process p;
		try {

			p = Runtime.getRuntime().exec(commod);
			int status = p.waitFor();
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				buffer.append(line);
			}
			// System.out.println("Return ============" + buffer.toString());
			if (status == 0) {
				resault = true;
			} else {
				resault = false;
			}
		} catch (IOException e) {

			e.printStackTrace();
			return resault;
		} catch (InterruptedException e) {

			e.printStackTrace();
			return resault;
		}
		// return resault;
		return resault;
	}
	
	public static boolean RootCommand(String command)

	{

		Process process = null;

		DataOutputStream os = null;

		try

		{

			process = Runtime.getRuntime().exec("su");

			os = new DataOutputStream(process.getOutputStream());

			os.writeBytes(command + "\n");

			os.writeBytes("exit\n");

			os.flush();

			process.waitFor();

		} catch (Exception e)

		{

			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());

			return false;

		} finally

		{

			try

			{

				if (os != null)

				{

					os.close();

				}

				process.destroy();

			} catch (Exception e)

			{

			}

		}

		Log.d("*** DEBUG ***", "Root SUC ");

		return true;

	}

	public static boolean Ping(String str) {
		boolean resault = false;
		Process p;
		try {
			// ping -c 3 -w 100 �??�??c 是指ping的次�??3是指ping 3�??�??w 10
			// 以秒为单位指定超时间隔，是指超时时间�??0�??
			p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + str);
			int status = p.waitFor();
			// System.out.println(status+"status");
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				buffer.append(line);
			}
			// System.out.println("Return ============" + buffer.toString());
			if (status == 0) {
				resault = true;
			} else {
				resault = false;
			}
		} catch (IOException e) {

			e.printStackTrace();
			return resault;
		} catch (InterruptedException e) {

			e.printStackTrace();
			return resault;
		}
		// return resault;
		return resault;
	}
}
