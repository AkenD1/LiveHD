package com.evpad.common.util;

public class CommonConfigs {
	
	public static final int AUTH_FAILED = 0;
	public static final int AUTH_SUCCESS = 1;

	public static String THIS_APP_PACKAGE_NAME = "com.moon.android.evpad";
	public static String SERVER = "http://cms.ievpad.com/";
	public static String SERVER2 = "http://etvhk.com/";

	public static class URL {
		public static String Auth = SERVER + "stbauth.php";
		public static String UpData = SERVER + "firmwareup.php";
		public static String MSg = SERVER + "stbmsg_group.php";
		public static String Ad = SERVER2 + "BoxAdNew.php";
		public static String Model = SERVER2 + "AppModel.php";
		public static String Info = SERVER2 + "Info.php";
		public static String IP = SERVER2 + "Get_ip.php";

	}
}
