package com.collcloud.yaohe.ui.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class MCResolution {
    private int currentDevHeight;
    private int currentDevWidth;
    private int designHeight;
    private int designWidth;
    private Context mContext;
    private static MCResolution mInstance;

    private MCResolution(Context context) {
        super();
        this.designWidth = 1080;
        this.designHeight = 1920;
        this.mContext = context;
        this.currentDevWidth = this.getDevWidth(this.mContext);
        this.currentDevHeight = this.getDevHeight(this.mContext);
    }

    //根据不同版本获得设备高度
	@SuppressLint("NewApi")
	public int getDevHeight(Context context) {
        DisplayMetrics metrics;
        int version_16 = 16;
        int version_13 = 13;
        Display display = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
        int version = Build.VERSION.SDK_INT;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int result = 0;
        if(version < version_13) {
            display.getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }

        if(version == version_13) {
            try {
                result = ((Integer)display.getClass().getMethod("getRealHeight").invoke(display)).intValue();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        if(version > version_13 && version <= version_16) {
            try {
                result = ((Integer)display.getClass().getMethod("getRawHeight").invoke(display)).intValue();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        if(version == version_16) {
            try {
                result = ((Integer)display.getClass().getMethod("getRawHeight").invoke(display)).intValue();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        if(version > version_16) {
            try {
                metrics = new DisplayMetrics();
                display.getRealMetrics(metrics);
                result = metrics.heightPixels;
            }
            catch(Exception e) {
                e.printStackTrace();
                metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                result = metrics.heightPixels;
            }
        }

        return result;
    }

    public String getDevScreenSize() {
        return String.valueOf(this.getDevWidth(this.mContext)) + "*" + this.getDevHeight(this.mContext);
    }

    public int getDevWidth(Context context) {
    	WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return window.getDefaultDisplay().getWidth();
    }

    public static MCResolution getInstance(Context context) {
        if(MCResolution.mInstance == null) {
            MCResolution.mInstance = new MCResolution(context);
        }
        return MCResolution.mInstance;
    }

    public int scaleHeight(int nowHeight) {
        int result;
        try {
            result = this.currentDevHeight * nowHeight / this.designHeight;
        }
        catch(Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }

    public int scaleHeightByScaleWidth(int scaleWidth) {
        int result;
        try {
            result = this.designWidth * scaleWidth / this.designHeight;
        }
        catch(Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }

    public int scaleWidth(int nowWidth) {
        int result =0 ;
        try {
            result = this.currentDevWidth * nowWidth / this.designWidth;
        }catch(Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }

    public void setDesignResolutionSize(int designWidth, int designHeight) {
        this.designHeight = designHeight;
        this.designWidth = designWidth;
    }
}

