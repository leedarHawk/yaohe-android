package com.collcloud.yaohe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.collcloud.yaohe.api.BaseResponseInfo;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.ResponseCityInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.ui.fragment.HomeFragment;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.update.UmengUpdateAgent;

/**
 * 程序主页-【主页】入口
 * 
 * @ClassName: MainActivity
 * @author CollCloud_小米
 */
public class MainActivity extends BaseActivity implements
		AppApplacation.EventHandler {

	private static String tag = MainActivity.class.getSimpleName();
	private HomeFragment mHomeFragment;
	/**
	 * 从服务器取得已开通城市信息集合（包含城市名和城市ID）
	 */
	private ResponseCityInfo mOpenCityInfo;
	
	private FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	private String aa;
	private Intent onNewIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_fragment_container);
		AppApplacation.mListeners.add(this);
		mHomeFragment = new HomeFragment();
		baseAddFragment(R.id.fl_fragment_container, mHomeFragment);

		// 自动更新
		UmengUpdateAgent.setUpdateCheckConfig(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setDeltaUpdate(false);
		UmengUpdateAgent.update(MainActivity.this);

		// lbsCity();
		getNativeOpenCity();
		CCLog.i("当前member_id:", " " + mLoginDataManager.getMemberId());
	}
	
	private void baseAddFragment(int container, Fragment fragment) {
	    fragmentManager = this.getSupportFragmentManager();
		fragmentTransaction = fragmentManager
				.beginTransaction();
		if (fragment != null) {
			if (fragment.isAdded()) {
				fragmentTransaction.show(fragment);
			} else {
				fragmentTransaction.replace(container, fragment);
			}
		}
		fragmentTransaction.commit();

	}
	/**
	 * 
	 * 
	 * @description 删除fragment
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月5日 上午3:20:38 
	 * @update 2015年10月5日 上午3:20:38
	 */
	private void removeFragment() {
		try {
			fragmentTransaction = fragmentManager.beginTransaction();
			mHomeFragment.removeFragment();
			fragmentTransaction.remove(mHomeFragment);
			fragmentTransaction.commit();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onResume() {
		CCLog.d(tag, "onresume......");
//		if(onNewIntent != null) {
//			boolean resetFragmentInfo = onNewIntent.getBooleanExtra("resetFragmentInfo", false);
//			if(resetFragmentInfo) {
//				CCLog.d(tag, "onresume removeFragment");
//				this.removeFragment();
//				mHomeFragment = new HomeFragment();
//				CCLog.d(tag, "onresume addFragment");
//				baseAddFragment(R.id.fl_fragment_container, mHomeFragment);
//			} else {
//				CCLog.d(tag, "onresume resumeFragment");
//			}
//		}
		
		super.onResume();
	}

	protected void resetLayout() {
		ApiAccess.showProgressDialog(this, "数据加载中..",
				R.style.progress_dialog);
		mBtnIsLogout = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取已开通的城市列表
				// getOpenCity();
				// 获取一级分类列表
				getOneClassifyList("0");
			}
		}).start();
	}

	private void getNativeOpenCity() {
		mApplication = AppApplacation.getCityInstance();

		if (mApplication.isCityListComplite()) {
			AppApplacation.sNativeCityList = mApplication.getCityList();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		CCLog.d(tag, "onNewIntent.......");
		onNewIntent = intent;
		super.onNewIntent(intent);
		intent.putExtra("refreshData", true);
		setIntent(intent);
		
		
		
	}

	/**
	 * 获取已开通城市
	 */
	private void getOpenCity() {
		if (AppApplacation.sOpenCitysInfo != null
				&& AppApplacation.sOpenCitysInfo.size() > 0) {
			CCLog.i("已保存城市列表信息。");
		} else {
			mOpenCityInfo = new ResponseCityInfo();
			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.GET, URLs.HOST + URLs.CITY_LIST,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								mOpenCityInfo = mOpenCityInfo
										.parseOpenCity(jsonObject);
								if (mOpenCityInfo.getmOpenCity().size() > 0) {
									BaseResponseInfo cityInfo = null;
									for (int i = 0; i < mOpenCityInfo
											.getmOpenCity().size(); i++) {
										cityInfo = new BaseResponseInfo();
										cityInfo.setmStrTitle(mOpenCityInfo
												.getmOpenCity().get(i)
												.getmStrTitle());
										cityInfo.setmStrId(mOpenCityInfo
												.getmOpenCity().get(i)
												.getmStrId());
										CCLog.i("城市： ", mOpenCityInfo
												.getmOpenCity().get(i)
												.getmStrTitle());
										AppApplacation.sOpenCitysInfo
												.add(cityInfo);
									}
								}

							} catch (Exception e) {
								if (e.getMessage() != null) {
									CCLog.e("MainActivity已开通数据返回异常\n" + " "
											+ e.getMessage().toString());
								}
							}
						}

						@Override
						public void onFailure(HttpException arg0, String msg) {
						}
					});
		}

	}

	/**
	 * 城市定位。
	 */
	// private void lbsCity() {
	// // 初始化定位，采用网络定位
	// mLocationManagerProxy = LocationManagerProxy.getInstance(mBaseActivity);
	// mLocationManagerProxy.setGpsEnable(false);
	// // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
	// mLocationManagerProxy.requestLocationData(
	// LocationProviderProxy.AMapNetwork, 10 * 1000, 15, this);
	// }
	//
	// @Override
	// public void onLocationChanged(Location location) {
	//
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	//
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	//
	// }
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	//
	// }
	//
	// @Override
	// public void onLocationChanged(AMapLocation amapLocation) {
	// if (amapLocation != null
	// && amapLocation.getAMapException().getErrorCode() == 0) {
	// // 定位成功回调信息，设置相关消息
	// if (amapLocation.getCity() == null) {
	// GlobalVariable.mCurrentCity = GlobalConstant.DEFAULT_CITY;
	// GlobalVariable.mCurrentCityID = GlobalConstant.DEFAULT_CITY_ID;
	// } else {
	// GlobalVariable.mCurrentCity = amapLocation.getCity();
	// GlobalVariable.mCurrentCityID = AppApplacation
	// .queryCityID(Utils.resetCity(amapLocation.getCity()));
	// AppApplacation.sLbsCity = amapLocation.getCity();
	// if (checkValid(Utils.resetCity(amapLocation.getCity()))) {
	// // TODO 第二种方式，
	// if (mCities != null && mCities.size() > 0) {
	// AppApplacation.sLbsCityID = mCities.get(mValidPos)
	// .getCityId();
	// CCLog.i("MainActivity 查找出城市id为："
	// + AppApplacation.sLbsCityID);
	// }
	// } else {
	// AppApplacation.sLbsCityID = GlobalConstant.DEFAULT_CITY_ID;
	// }
	// }
	//
	// if (amapLocation.getLatitude() != 0) {
	// GlobalVariable.mLatitude = amapLocation.getLatitude();
	// } else {
	// GlobalVariable.mLatitude = 40.003662;
	// }
	//
	// if (amapLocation.getLongitude() != 0) {
	// GlobalVariable.mLongitude = amapLocation.getLongitude();
	// } else {
	// GlobalVariable.mLongitude = 116.465271;
	// }
	// if (amapLocation.getDistrict() != null) {
	// GlobalVariable.mStrDistrict = amapLocation.getDistrict();
	// } else {
	// GlobalVariable.mStrDistrict = GlobalConstant.DEFAULT_DISTRACT;
	// }
	// } else {
	// GlobalVariable.mCurrentCity = GlobalConstant.DEFAULT_CITY;// 默认北京
	// GlobalVariable.mCurrentCityID = GlobalConstant.DEFAULT_CITY_ID;// 默认北京ID
	// GlobalVariable.mStrDistrict = GlobalConstant.DEFAULT_DISTRACT; // 默认朝阳区
	// GlobalVariable.mLatitude = GlobalConstant.DEFAULT_LATITUDE; // 默认经度
	// GlobalVariable.mLongitude = GlobalConstant.DEFAULT_LONGITUDE;// 默认维度
	// }
	//
	// }

	@Override
	public void onCityComplite() {

	}

	@Override
	public void onNetChange() {

	}

	private List<String> mOpenCity = new ArrayList<String>();
	private int mValidPos;

	/**
	 * 检查城市有效性，是否已开通
	 * 
	 * @Description 该城市是否已开通
	 * @param city
	 *            城市
	 */
	private boolean checkValid(String city) {
		boolean flag = true;
		if (mOpenCity != null && mOpenCity.size() > 0) {
			for (int i = 0; i < mOpenCity.size(); i++) {
				if (city.equals(mOpenCity.get(i))) {
					flag = true;
					mValidPos = i;
					break;
				} else {
					flag = false;
				}
			}
		}
		return flag;
	}

}
