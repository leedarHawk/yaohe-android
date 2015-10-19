package com.collcloud.yaohe.common.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 程序内部全局变量类
 */
public class GlobalVariable {
	public static boolean sIsFirstIn = false;
	public static Map<String, String> mMapPurpose = new HashMap<String, String>();
	
	/**
	 * 当前城市
	 */
	public static String sChoiceCity="";
	/**
	 * 当前城市ID
	 */
	public static String sChoiceCityID;
	/**
	 * 当前位置对应的纬度
	 */
	public static double mLatitude;
	/**
	 * 当前位置对应的经度
	 */
	public static double mLongitude;
	/**
	 * 当前位置的所在区
	 */
	public static String mStrDistrict="朝阳";
	/**
	 * 当前定位的详细位置
	 */
	public static String sLbsInfo="";
	
	public static String sShopStar ;
	
	public static String currentCity="";
	/**定位结果*/
	public static boolean LBSRESULT = false;
	/**是否第一次定位成功*/
	public static boolean IsFirstLbs = false;

}
