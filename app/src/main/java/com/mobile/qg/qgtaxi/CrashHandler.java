package com.mobile.qg.qgtaxi;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 11234 on 2018/8/11.
 * 全局错误日志记录
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss  ", Locale.CHINA);
    private Map<String, String> mInfo = new HashMap<>();
    private PackageInfo mPackageInfo;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private String mCrashPath;

    /**
     * 单例
     */
    private static CrashHandler sInstance = new CrashHandler();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {

        mCrashPath = Objects.requireNonNull(context.getExternalCacheDir()).getPath();
        Log.e("正在测试Crash", "init: " + mCrashPath);

        try {
            mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 有读写权限则写出错误日志
     * 没有则交由默认的CrashHandler来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && mCrashPath != null) {
            mInfo.put("thread", thread.toString());
            saveCrashInfo2File(ex);
            System.exit(1);
        } else if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }


    /**
     * 保存手机信息到info（map）中
     * 并且把info写出到文件
     *
     * @param ex 异常
     */
    private void saveCrashInfo2File(Throwable ex) {

        if (mPackageInfo != null) {
            String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
            String versionCode = mPackageInfo.versionCode + "";
            mInfo.put("versionName", versionName);
            mInfo.put("versionCode", versionCode);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.flush();
        printWriter.close();
        String result = writer.toString();
        String time = FORMATTER.format(new Date());
        sb.append(time).append(result);

        try {
            long timestamp = System.currentTimeMillis();
            String fileName = "crash" + timestamp + ".log";

            mCrashPath = mCrashPath + "/crash/";
            File dir = new File(mCrashPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.e("TEST", "saveCrashInfo2File: " + dir.getAbsolutePath());
            File file = new File(mCrashPath + fileName);
            file.createNewFile();
            Log.e("TSTS", "saveCrashInfo2File: " + file.getAbsolutePath() + file.exists());
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write((sb.toString()).getBytes());
            fos.flush();
            fos.close();
//            }

        } catch (Exception ignore) {

        }
    }


}
