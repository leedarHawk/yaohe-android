package com.collcloud.yaohe.common.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.BaseResponseInfo;
import com.collcloud.yaohe.api.info.AreaListInfo.AreaList;
import com.collcloud.yaohe.api.info.ClassifyListInfo.Classify;
import com.collcloud.yaohe.api.info.DistrictListInfo.DistrictList;
import com.collcloud.yaohe.api.info.HomeTypeInfo;
import com.collcloud.yaohe.api.info.HomeTypeInfo.TypeInfo;
import com.collcloud.yaohe.ui.model.City;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.CityDB;
import com.collcloud.yaohe.ui.utils.StringUtils;

/**
 * 全局应用程序类
 * 
 * @ClassName AppApplacation
 * @Description 用于保存和调用全局应用配置及访问网络数据
 * @author CollCloud_小米
 */
public class AppApplacation extends Application {

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 10;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

	// **** 城市列表相关
	public static List<BaseResponseInfo> sOpenCitysInfo = new ArrayList<BaseResponseInfo>();
	public static List<City> sNativeCityList = new ArrayList<City>();
	/** 获取首页分类列表数据list */
	public static List<TypeInfo> sTypeInfos = new ArrayList<HomeTypeInfo.TypeInfo>();

	// **** 获取的一级和二级分类 **** //
	public static List<Classify> sOneClassFy = new ArrayList<Classify>();

	// **** 获取的区域和商圈列表 **** //
	public static List<AreaList> sAreaList = new ArrayList<AreaList>();
	public static List<DistrictList> sDistrictList = new ArrayList<DistrictList>();

	private static AppApplacation mInstance = null;
	private List<BaseActivity> mActivityList = null;
	private List<Activity> mDefinedActivityList = null;

	// public static String sLbsCity ;//记录定位成功的城市
	// public static String sLbsCityID ;//记录定位成功的城市ID

	public static RequestQueue requestQueue;
	public static int memoryCacheSize;

	public static Bitmap bimap;
	/**
	 * LEE
	 * ========================================
	 */
	//是否从新获取个人信息 用于修改头像以及昵称后 重新加载个人信息 当返回 我的 页面时
	//public static boolean isReloadPersonDate=false;
	/**
	 * ========================================
	 */

	public static AppApplacation getInstance() {
		return mInstance;
	}

	public void add(BaseActivity baseActivity) {
		if (mActivityList == null) {
			mActivityList = new ArrayList<BaseActivity>();
		}
		mActivityList.add(baseActivity);
	}

	public void remove(BaseActivity activity) {
		if (mActivityList == null) {
			return;
		}
		if (mActivityList.contains(activity)) {
			mActivityList.remove(activity);
		}
	}

	public void finishAll() {
//		for (int i = 0; i < mActivityList.size(); i++) {
//			mActivityList.get(i).finish();
//		}
//		mActivityList.clear();
	}

	public void addDefinedActivity(Activity activity) {
		if (mDefinedActivityList == null) {
			mDefinedActivityList = new ArrayList<Activity>();
		}
		mDefinedActivityList.add(activity);
	}

	public synchronized void unRegisterActivity(Activity activity) {

		if (mDefinedActivityList.size() != 0) {
			mDefinedActivityList.remove(activity);
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}

	public void finishDefinedActivity() {
		for (int i = 0; i < mDefinedActivityList.size(); i++) {
			mDefinedActivityList.get(i).finish();
		}
		mDefinedActivityList.clear();
	}

	public List<BaseActivity> getmActivityList() {
		return mActivityList;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		// 城市定位相关
		initData();
		requestQueue = Volley.newRequestQueue(this);
		// 计算内存缓存
		memoryCacheSize = getMemoryCacheSize();
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
	}

	/**
	 * 得到需要分配的缓存大小
	 * 
	 * @param context
	 * @return 得到需要分配的缓存大小，这里用八分之一的大小来做
	 */
	public int getMemoryCacheSize() {
		// Get memory class of this device, exceeding this amount will throw an
		// OutOfMemory exception.
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		// Use 1/8th of the available memory for this memory cache.
		return maxMemory / 8;
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 是否加载显示文章图片
	 * 
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);

		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 * 
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否左右滑动
	 * 
	 * @return
	 */
	public boolean isScroll() {
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		// 默认是关闭左右滑动
		if (StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}

	/**
	 * 设置是否左右滑动
	 * 
	 * @param b
	 */
	public void setConfigScroll(boolean b) {
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}

	/**
	 * 是否Https登录
	 * 
	 * @return
	 */
	public boolean isHttpsLogin() {
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		// 默认是http
		if (StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}

	/**
	 * 设置是是否Https登录
	 * 
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b) {
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// //清除webview缓存
		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		// deleteDatabase("webview.db");
		// deleteDatabase("webview.db-shm");
		// deleteDatabase("webview.db-wal");
		// deleteDatabase("webviewCache.db");
		// deleteDatabase("webviewCache.db-shm");
		// deleteDatabase("webviewCache.db-wal");
		// //清除数据缓存
		// clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		// clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		// //2.2版本才有将应用缓存转移到sd卡的功能
		// if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
		// clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		// }
		// //清除编辑器保存的临时内容
		// Properties props = getProperties();
		// for(Object key : props.keySet()) {
		// String _key = key.toString();
		// if(_key.startsWith("temp"))
		// removeProperty(_key);
		// }
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).getProperties();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	public static boolean checkCityValid(String city) {
		boolean flag = true;
		if (sOpenCitysInfo != null && sOpenCitysInfo.size() > 0) {
			for (int i = 0; i < sOpenCitysInfo.size(); i++) {
				if (city.equals(sOpenCitysInfo.get(i).getmStrTitle())) {
					flag = true;
					break;
				} else {
					flag = false;
				}
			}
		}
		return flag;
	}

	public static String queryCityID(String city) {
		String cityId = "12";
		if (sOpenCitysInfo != null && sOpenCitysInfo.size() > 0) {
			for (int i = 0; i < sOpenCitysInfo.size(); i++) {
				if (city.equals(sOpenCitysInfo.get(i).getmStrTitle())) {
					cityId = sOpenCitysInfo.get(i).getmStrId();
					CCLog.i("从城市列表中查询到城市ID ：", cityId);
					break;
				}
			}
		}
		return cityId;
	}

	/**
	 * 第二种方式，从本地获取
	 * 
	 * @Title queryCityID
	 * @Description
	 * @param @param city
	 * @param @return
	 * @return String
	 */
	public static String queryNativeCityID(String city) {
		String cityId = "12";
		if (sNativeCityList != null && sNativeCityList.size() > 0) {
			for (int i = 0; i < sNativeCityList.size(); i++) {
				if (city.equals(sNativeCityList.get(i).getCity())) {
					cityId = sNativeCityList.get(i).getCityId();
					CCLog.i("从本地查询到城市ID ：", cityId);
					break;
				}
			}
		}
		return cityId;
	}

	// +++++++++++++++++ 城市定位页面，快速索引相关 +++++++++++++++++++++
	private CityDB cityDB;
	private static final int CITY_LIST_SCUESS = 0;
	private static final String FORMAT = "^[a-z,A-Z].*$";
	private static AppApplacation mApplication;
	private List<City> cityList;
	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<City>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private static boolean isCityListComplite;

	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();

	public static abstract interface EventHandler {
		public abstract void onCityComplite();

		public abstract void onNetChange();
	}

	private static Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CITY_LIST_SCUESS:
				isCityListComplite = true;
				if (mListeners.size() > 0)// 通知接口完成加载
					for (EventHandler handler : mListeners) {
						handler.onCityComplite();
					}
				break;
			default:
				break;
			}
		}
	};

	public static synchronized AppApplacation getCityInstance() {
		return mApplication;
	}

	private void initData() {
		mApplication = this;
		initCityList();
	}

	private void initCityList() {
		cityList = new ArrayList<City>();
		mSections = new ArrayList<String>();
		mMap = new HashMap<String, List<City>>();
		mPositions = new ArrayList<Integer>();
		mIndexer = new HashMap<String, Integer>();
		cityDB = openCityDB();// 这个必须最先复制完,所以我放在单线程中处理
		new Thread(new Runnable() {

			@Override
			public void run() {
				isCityListComplite = false;
				prepareCityList();
				mHandler.sendEmptyMessage(CITY_LIST_SCUESS);
			}
		}).start();
	}

	public synchronized CityDB getCityDB() {
		if (cityDB == null)
			cityDB = openCityDB();
		return cityDB;
	}

	private boolean prepareCityList() {
		cityList = cityDB.getNewAllCity();// 获取数据库中所有城市
		for (City city : cityList) {
			String firstName = city.getFirstPY();// 第一个字拼音的第一个字母
			if (firstName.matches(FORMAT)) {
				if (mSections.contains(firstName)) {
					mMap.get(firstName).add(city);
				} else {
					mSections.add(firstName);
					List<City> list = new ArrayList<City>();
					list.add(city);
					mMap.put(firstName, list);
				}
			} else {
				if (mSections.contains("#")) {
					mMap.get("#").add(city);
				} else {
					mSections.add("#");
					List<City> list = new ArrayList<City>();
					list.add(city);
					mMap.put("#", list);
				}
			}
		}
		Collections.sort(mSections);// 按照字母重新排序
		int position = 0;
		for (int i = 0; i < mSections.size(); i++) {
			mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
			mPositions.add(position);// 首字母在listview中位置，存入list中
			position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
		}
		return true;
	}

	private CityDB openCityDB() {
		String path = "/data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ File.separator + "com.collcloud.yaohe" + File.separator
				+ CityDB.CITY_DB_NAME;
		File db = new File(path);
		if (!db.exists()) {
			// L.i("db is not exists");
			try {
				InputStream is = getAssets().open("city.db");
				FileOutputStream fos = new FileOutputStream(db);
				int len = -1;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					fos.flush();
				}
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return new CityDB(this, path);
	}

	public List<City> getCityList() {
		return cityList;
	}

	public List<String> getSections() {
		return mSections;
	}

	public Map<String, List<City>> getMap() {
		return mMap;
	}

	public List<Integer> getPositions() {
		return mPositions;
	}

	public Map<String, Integer> getIndexer() {
		return mIndexer;
	}

	public boolean isCityListComplite() {
		return isCityListComplite;
	}
	
	
}
