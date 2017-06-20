package com.evpad.common.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by caij on 2017/03/07 0007.
 */

public class AppDownloader {
    private final static String TAG = "" + AppDownloader.class.getSimpleName();

    private final static String KEY_APK_FILEPATH = "apk_filepath";
    private final static String KEY_APK_DOWNSTATE = "downState";//下载状�?�，0为下载失败，1为下载完�?
    private final static int MSG_PROG = 0x1;
    private final static int MSG_FINISH = 0x2;

    private static AppDownloader instance = null;
    private Context ctx = null;
    private String savedir = null;

    //private Set<String> dlingSet = new HashSet<String>();

    public static AppDownloader getInstance(Context ctx) {
        if (null == instance) {
            instance = new AppDownloader(ctx);
        }
        return instance;
    }

    private AppDownloader(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 设置应用下载的存放目录（相对路径)
     *
     * @param dir 目录�?
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void setDownloadDir(String dir) {
        File fdir = ctx.getDir(dir, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

        if (!fdir.exists()) {
            fdir.mkdirs();
            fdir.setWritable(true);
            fdir.setReadable(true, false);
            fdir.setExecutable(true, false);
        }
        savedir = fdir.getAbsolutePath();
        Log.d(TAG, "setDownloadDir:  savedir:" + savedir);
    }

    /**
     * 启动apk下载
     *
     * @param url      apk对应的下载地�?
     * @param listener 下载监听�?
     */
    public void start_download(final String downUrl, final IDownloadProgListener listener) {

//        final APPItemInfo item = "";
//        Log.d(TAG, "start_download:  new  downloading...url:" + item.mDownloadURL);


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_PROG: {
//                        int appID = (int) msg.arg1;
//                        int prog = msg.arg2;
//                        listener.onProgress(appID, prog);
                    	int prog = msg.arg1;
                    	listener.onProgress(prog);
                    }
                    break;

                    case MSG_FINISH: {
//                        int appID = (int) msg.arg1;
//                        Bundle data = msg.getData();
//                        String apk_filepath = data.getString(KEY_APK_FILEPATH);
//                        listener.onFinish(appID, apk_filepath);
//                        Log.d(TAG, "handleMessage:  MSG_FINISH");
                    	Bundle data = msg.getData();
                    	String apk_filepath = data.getString(KEY_APK_FILEPATH);
                    	String downState = data.getString(KEY_APK_DOWNSTATE);
                    	listener.onFinish(apk_filepath, downState);
                    }
                    break;
                }
            }
        };

        Thread dlthread = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@SuppressLint("NewApi")
			@Override
            public void run() {
                int pos = downUrl.lastIndexOf("/");
                if (pos < 0) {
                    return;
                }
                String filename = downUrl.substring(pos + 1);
                Log.d(TAG, "start_download run: apkname:" + filename);
                String fileFullpath = "";
//
                try {
                	fileFullpath = savedir + File.separator + filename;

                    FileOutputStream fout = new FileOutputStream(fileFullpath);
                    URL download_url = new URL(downUrl);
                    HttpURLConnection conn = (HttpURLConnection) download_url.openConnection();
                    InputStream is = conn.getInputStream();
                    byte[] buff = new byte[128 * 1024];
                    long fileLen = conn.getContentLength();
                    int read = 0;
                    long totalRead = 0;
                    int lastProg = -5;

                    while ((read = is.read(buff)) > 0) {
                        fout.write(buff, 0, read);
                        totalRead += read;

                        int prog = (int) (100 * totalRead / fileLen);
//                        System.out.println("prog="+prog);

                        if (prog - lastProg >= 5) {//减少界面刷新次数
                            Message msg = Message.obtain();
//                            msg.arg1 = appID;
                            msg.arg2 = prog;
                            msg.what = MSG_PROG;
                            handler.sendMessage(msg);
                            lastProg = prog;
                        }
                    }
                    fout.flush();
                    fout.close();

                    File file = new File(fileFullpath);
                    if (file.exists()) {
                        file.setWritable(true);
                        file.setReadable(true, false);
                        file.setExecutable(true, false);
                    }


                    Message msg = Message.obtain();
//                    msg.arg1 = appID;
                    msg.arg2 = 100;
                    Bundle data = new Bundle();
                    data.putString(KEY_APK_FILEPATH, fileFullpath);
                    data.putString(KEY_APK_DOWNSTATE, "1");
                    msg.setData(data);
                    msg.what = MSG_FINISH;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.arg2 = 0;
                    Bundle data = new Bundle();
                    data.putString(KEY_APK_FILEPATH, fileFullpath);
                    data.putString(KEY_APK_DOWNSTATE, "0");
                    msg.setData(data);
                    msg.what = MSG_FINISH;
                    handler.sendMessage(msg);
                }
            }
        });
        dlthread.start();

    }

    public interface IDownloadProgListener {
        void onProgress(int progress);

        void onFinish(String apkPath, String downState);
    }
}
