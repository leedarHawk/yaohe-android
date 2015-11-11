package com.collcloud.yaohe.photo.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 存放所有的list在最后退出时一起关闭
 */
public class PublicWay {
	public static List<Activity> activityList = new ArrayList<Activity>();
	
	public static int num = 6;
	
	public static int numOne=1;
	
}
