package com.collcloud.yaohe.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.collcloud.frame.viewpager.PagerSlidingTabStrip;
import com.collcloud.frame.viewpager.PagerSlidingTabStrip.TabsScrollListener;
import com.collcloud.frame.viewpager.PagerSlidingTabStrip.TabsizeListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.lbs.LBSCityActivity;
import com.collcloud.yaohe.activity.search.SearchActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.ResponseDataToUI;
import com.collcloud.yaohe.api.info.HomeTypeInfo;
import com.collcloud.yaohe.api.info.HomeTypeInfo.TypeInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseFragment;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.model.City;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.Utils;
import com.dtr.zbar.scan.CaptureActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.analytics.MobclickAgent;

/**
 * 吆喝APP 主页部分
 * 
 * @ClassName: HomeFragment
 * @Description: 主页相关（推荐，附近，我的，吆喝，关注商家，美食，生活服务，休闲等等。。）
 * @author CollCloud_小米
 */
public class HomeFragment extends BaseFragment implements AMapLocationListener {

	/**
	 * 二维码扫描
	 */
	private LinearLayout mLlScan = null;
	/**
	 * 主页定位
	 */
	private TextView mTvLbs = null;
	/**
	 * 主页定位显示的城市
	 */
	private String mStrCity;
	/**
	 * 城市ID
	 */
	private String mStrCityID = "12";
	private LocationManagerProxy mLocationManagerProxy;
	/**
	 * 搜索机能
	 */
	private EditText mEtSearch = null;
	/**
	 * 顶部TAB标签对应的子Fragment--【推荐】
	 */
	private HomeTuijianFragment mHomeTuijianFragment;
	/**
	 * 顶部TAB标签对应的子Fragment--【关注商家】
	 */
	private HomeGuanzhuFragment mHomeGuanzhuFragment;
	/**
	 * 顶部TAB标签对应的子Fragment--【其他动态添加的选项】
	 */
	private HomeOtherFragment mHomeOtherFragment;
	/**
	 * 顶部TAB标签 - TuiJian
	 */
	public static final String TAG_TUIJIAN = "TuiJian";
	/**
	 * 顶部TAB标签 - GuanZhu
	 */
	public static final String TAG_GUANZHU = "GuanZhu";
	/**
	 * 顶部TAB标签 - Other
	 */
	public static final String TAG_OTHER = "Other";
	private String hideTag;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	// ******* 首页分类列表 ******* //
	/**
	 * 获取首页分类列表
	 */
	private HomeTypeInfo mHomeTypeInfo;
	/**
	 * 顶部tab导航条，动态数据
	 */
	private List<String> mTopTabs = new ArrayList<String>();
	/**
	 * 顶部tab导航条，动态添加
	 */
	//private HorizontalListView hListView;
	/**
	 * 某些城市下没有其他分类，所以默认只有这样这两个便签。
	 */
	private String[] mTypeArray = { "推荐", "关注商家" };
	/**
	 * 适配器
	 */
	//private HorizontalListViewAdapter hListViewAdapter;
	/**
	 * 主页内容容器
	 */
	private LinearLayout mLlContainer;
	private LinearLayout mLlNetError;
	private TextView mTvNetTips;
	private LayoutInflater mInflater;
	//private Handler mUiHandler;
	
	private View view;
	//左边箭头
	private TextView leftArrow;
	//右边箭头
	private TextView rightArrow;
	
	private static String tag = HomeFragment.class.getSimpleName();

	/**
	 * 主页Fragment，构造方法
	 */
	public HomeFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ApiAccess.showProgressDialog(getActivity(), "数据加载中..",
				R.style.progress_dialog);
		view = inflater.inflate(R.layout.activity_home, null);
		mInflater = LayoutInflater.from(getActivity());
		mFragmentManager = this.getFragmentManager();
		mTvLbs = (TextView) view.findViewById(R.id.tv_activity_home_city);
		
		leftArrow = (TextView)view.findViewById(R.id.leftArrow);
		rightArrow = (TextView)view.findViewById(R.id.rightArrow);
		initWindowScreenParams();
		
		
		
		if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) {
			mTvLbs.setText(GlobalVariable.sChoiceCity);
			mStrCityID = GlobalVariable.sChoiceCityID;
//			mUiHandler = new Handler();
//			mUiHandler.post(new myRunnable());
			getAreaList();

			// 获取该城市下的相关商圈分类，商圈信息等
			//getHomeTypeInfo(mStrCityID);
			getData();
			// 推荐内容数据显示
			//setTuiJianData(mStrCityID);
		} else {
			setDefaultCityInfo();
			getAreaList();
			// 城市定位。
			lbsCity();
		}

		CCLog.i("HomeFragment", "onCreateView");
		return view;
	}
	//屏幕宽度
	private int windowWidth;
	private void initWindowScreenParams() {
		 DisplayMetrics dm = new DisplayMetrics();
	        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
	        windowWidth = dm.widthPixels;
	}

	@Override
	public void onResume() {
		super.onResume();
		CCLog.i("MainActivity", "onResume Homefragment..");
		CCLog.i(tag, "onResume Homefragment..");
		// 设置【主页】为选中状态
		setFooterChanged();
		MobclickAgent.onPageStart("HomeFragment");

		// 接收从城市定位页面传递的城市名称
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY) != null) {
			mStrCity = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY);
			GlobalVariable.sChoiceCity = mStrCity;
			mTvLbs.setText(mStrCity);
		}
		// 接收从城市定位页面传递的城市ID
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			mStrCityID = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			GlobalVariable.sChoiceCityID = mStrCityID;

			// 获取切换城市下的区域列表
			//AppApplacation.sAreaList = mBaseActivity.getAreaList(mStrCityID);
			mBaseActivity.getAreaList(mStrCityID);

		}

		// 获取该城市下的首页分类列表
		//getHomeTypeInfo(mStrCityID);
		getData();
		//setTuiJianData(mStrCityID);

	}
	
	
	//临时保存城市id 为了与当前的进行比较 。如果一样 就不进行数据加载，如果不一致则进行加载
		private String tmpStrCityId;
	/**
	 * 
	 * 
	 * @description 通过http请求数据
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月5日 上午11:00:46 
	 * @update 2015年10月5日 上午11:00:46
	 */
	private void getData() {
		if(tmpStrCityId != null) {
			if(tmpStrCityId.equals(mStrCityID)) {
				CCLog.d(tag, "tmpStrCityId is the same to the old id so not load data.......");
			} else {
				tmpStrCityId = mStrCityID;
				CCLog.d(tag, "tmpStrCityId is not null but different old id so load data.......");
				// 获取该城市下的首页分类列表
				getHomeTypeInfo(mStrCityID);
			}
		} else {
			tmpStrCityId = mStrCityID;
			CCLog.d(tag, "tmpStrCityId is null load data.......");
			// 获取该城市下的首页分类列表
			getHomeTypeInfo(mStrCityID);
		}
		
	}
	

	/**
	 * 获取首页上导航分类列表
	 */
	private void getHomeTypeInfo(String cityID) {
//		ApiAccess.sendRequestListener(mApiResponseFragmentListener);
//		ApiAccess.assessGetNet(ApiAccess.parseHomeTypeUrl(cityID));
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, ApiAccess.parseHomeTypeUrl(cityID), null, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				ApiAccess.dismissProgressDialog();
				ResponseDataToUI responseData = new ResponseDataToUI();
				responseData.result = responseInfo.result;
				responseData.netWorkErrorCode = ApiAccessErrorManager.OK;
				doOnApiAccessResponsedFragment(responseData);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ResponseDataToUI responseData = new ResponseDataToUI();
				responseData.netWorkErrorCode = ApiAccessErrorManager.RESPONSE_DATA_INVALID;
				doOnApiAccessResponsedFragment(responseData);
				ApiAccess.dismissProgressDialog();
			}
		});
		
	}

	@SuppressLint("HandlerLeak")
//	Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//
//			super.handleMessage(msg);
//			switch (msg.what) {
//
//			case 1:
//				// 刷新首页分类列表数据
//				refreshTypes(mTopTabs);
//				break;
//			default:
//				break;
//			}
//		};
//	};

	
	private void getAreaList() {
		if (AppApplacation.sAreaList != null
				&& AppApplacation.sAreaList.size() > 0) {
		} else {
			if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) {
//				AppApplacation.sAreaList = mBaseActivity
//						.getAreaList(GlobalVariable.sChoiceCityID);
				mBaseActivity.getAreaList(GlobalVariable.sChoiceCityID);
			}
		}
	}
//	private class myRunnable implements Runnable {
//		@Override
//		public void run() {
//			if (AppApplacation.sAreaList != null
//					&& AppApplacation.sAreaList.size() > 0) {
//			} else {
//				if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) {
////					AppApplacation.sAreaList = mBaseActivity
////							.getAreaList(GlobalVariable.sChoiceCityID);
//					mBaseActivity.getAreaList(GlobalVariable.sChoiceCityID);
//				}
//			}
//		}
//	}

	private void refreshTypes(List<String> topType) {
		this.mTopTabs = topType;
		if(viewPagerAdapter != null) {
			//removeFragment();
			//this.pager.setAdapter(viewPagerAdapter);
			pagerTabs.notifyDataSetChanged();
			viewPagerAdapter.notifyDataSetChanged();
			pagerTabs.setTabToFirstIndex();
		}
		this.initViewPager();
//		if (hListViewAdapter != null) {
//			hListViewAdapter.refreshData(mTopTabs);
//		} else {
//			hListViewAdapter = new HorizontalListViewAdapter(mBaseActivity,
//					mTopTabs);
//			hListViewAdapter.setSelectIndex(0);
//			// 设置适配器
//			hListView.setAdapter(hListViewAdapter);
//		}
	}

	/**
	 * 初始化顶部分类列表组件
	 */
//	private void initTypeListView() {
//		// 动态添加顶部tab标签栏
//		hListViewAdapter = new HorizontalListViewAdapter(mBaseActivity,
//				mTopTabs);
//		hListViewAdapter.setSelectIndex(0);
//		// 设置适配器
//		hListView.setAdapter(hListViewAdapter);
//		// 添加点击回调
//		hListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					final int position, long arg3) {
//				hListViewAdapter.setSelectIndex(position);
//				hListViewAdapter.notifyDataSetChanged();
//
//				if (position == 0) {
//
//					if (mHomeTuijianFragment == null) {
//						mHomeTuijianFragment = new HomeTuijianFragment(
//								mStrCityID);
//					}
//					switchFragment(mHomeTuijianFragment, TAG_TUIJIAN);
//				} else if (position == 1) {
//
//					if (mHomeGuanzhuFragment == null) {
//						mHomeGuanzhuFragment = new HomeGuanzhuFragment(
//								mStrCityID);
//					}
//					switchFragment(mHomeGuanzhuFragment, TAG_GUANZHU);
//				} else {
//
//					if (mHomeOtherFragment == null) {
//						mHomeOtherFragment = new HomeOtherFragment(mStrCityID);
//					}
//					switchFragment(mHomeOtherFragment, TAG_OTHER);
//					mHomeOtherFragment.refreshData(
//							AppApplacation.sTypeInfos.get(position - 2).city_id,
//							AppApplacation.sTypeInfos.get(position - 2).class_id);
//				}
//
//			}
//		});
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

//	@Override
//	protected void doOnErrorResponseAction() {
//		super.doOnErrorResponseAction();
//		mTopTabs.clear();
//		mTopTabs.addAll(Arrays.asList(mTypeArray));
//		mHandler.sendEmptyMessage(1);
//		ApiAccess.dismissProgressDialog();
//	}

	protected void doOnApiAccessResponsedFragment(ResponseDataToUI responseData) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(responseData.result);

			if (jsonObject.has("data")) {
				mHomeTypeInfo = GsonUtils.json2Bean(responseData.result,
						HomeTypeInfo.class);
				mTopTabs.clear();
				if (mHomeTypeInfo.data != null && mHomeTypeInfo.data.size() > 0) {
					mTopTabs.addAll(Arrays.asList(mTypeArray));
					AppApplacation.sTypeInfos.clear();
					for (int i = 0; i < mHomeTypeInfo.data.size(); i++) {
						TypeInfo typeInfo = new TypeInfo();
						if (mHomeTypeInfo.data.get(i).show_title != null) {
							typeInfo.show_title = mHomeTypeInfo.data.get(i).show_title;
							mTopTabs.add(typeInfo.show_title);
						}
						if (mHomeTypeInfo.data.get(i).city_id != null) {
							typeInfo.city_id = mHomeTypeInfo.data.get(i).city_id;
						}
						if (mHomeTypeInfo.data.get(i).class_id != null) {
							typeInfo.class_id = mHomeTypeInfo.data.get(i).class_id;
						}
						if (mHomeTypeInfo.data.get(i).id != null) {
							typeInfo.id = mHomeTypeInfo.data.get(i).id;
						}
						AppApplacation.sTypeInfos.add(typeInfo);
					}
					//mHandler.sendEmptyMessage(1);
					refreshTypes(mTopTabs);
				} else {
					mTopTabs.addAll(Arrays.asList(mTypeArray));
					refreshTypes(mTopTabs);
					//mHandler.sendEmptyMessage(1);
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * 城市定位。
	 */
	private void lbsCity() {
		// 初始化定位，采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(mBaseActivity);
		mLocationManagerProxy.setGpsEnable(false);
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 10 * 1000, 15, this);
	}

//	/**
//	 * 选择不同的fragment
//	 */
//	private void switchFragment(Fragment fragment, String tag) {
//		mFragmentManager = getChildFragmentManager();
//		mFragmentTransaction = mFragmentManager.beginTransaction();
//
//		Fragment tagFragment = mFragmentManager.findFragmentByTag(tag);
//
//		if (tagFragment == null) {
//			mFragmentTransaction.add(R.id.fl_home_tab_menu_container, fragment,
//					tag);
//		} else {
//			mFragmentTransaction.show(tagFragment);
//		}
//
//		if (!tag.equals(hideTag)) {
//			tagFragment = mFragmentManager.findFragmentByTag(hideTag);
//
//			if (tagFragment != null) {
//				mFragmentTransaction.hide(tagFragment);
//			}
//
//			hideTag = tag;
//		}
//
//		mFragmentTransaction.commit();
//	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HomeFragment");
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private String currentLbsCity="";
	private String currentLbsCityId="";
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			setJdWd(amapLocation);
			// 定位成功回调信息，设置相关消息
			if (amapLocation.getCity() == null) {
				GlobalVariable.LBSRESULT = false;
				mTvLbs.setText(GlobalConstant.DEFAULT_CITY);
				GlobalVariable.sChoiceCity = GlobalConstant.DEFAULT_CITY;
				GlobalVariable.sChoiceCityID = GlobalConstant.DEFAULT_CITY_ID;
			} else {
				GlobalVariable.LBSRESULT = true;
				if(!GlobalVariable.IsFirstLbs) {
					GlobalVariable.IsFirstLbs = true;
					CCLog.d(tag, "onLocationChanged lbs success. is first time");
					dialog(amapLocation);
					
				} else {
					//当非第一次 定位 和第一次定位 结果不相同时 弹出提示框
					String tmpCityId = AppApplacation
							.queryNativeCityID(Utils.resetCity(amapLocation
									.getCity()));
					if(!tmpCityId.equals(currentLbsCityId)) {
						CCLog.d(tag, "onLocationChanged lbs success. and different result.. ");
						dialog(amapLocation);
					}
					CCLog.d(tag, "onLocationChanged lbs success. but no first time");
				}
				
				CCLog.d(tag, "onLocationChanged lbs success and set currentLbsCityId.....");
				try {
					currentLbsCityId = AppApplacation
							.queryNativeCityID(Utils.resetCity(amapLocation
									.getCity()));
					currentLbsCity = Utils.resetCity(amapLocation.getCity());
				} catch(Exception e) {
					e.printStackTrace();
				}
				getAreaList();
			}
		} else {
			setDefaultCityInfo();
		}

	}
	
	
	/**
	 * 设置经度维度
	 * @param amapLocation
	 */
	private void setJdWd(AMapLocation amapLocation) {
		if (amapLocation.getLatitude() != 0) {
			GlobalVariable.mLatitude = amapLocation.getLatitude();
			CCLog.i("setJdWd当前位置的 纬度：", " " + amapLocation.getLatitude());
		} else {
			GlobalVariable.mLatitude = 40.003662;
		}

		if (amapLocation.getLongitude() != 0) {
			GlobalVariable.mLongitude = amapLocation.getLongitude();
			CCLog.i("setJdWd当前位置的 经度：", " " + amapLocation.getLongitude());
		} else {
			GlobalVariable.mLongitude = 116.465271;
		}
		if (amapLocation.getDistrict() != null) {
			GlobalVariable.mStrDistrict = amapLocation.getDistrict();
			CCLog.i("setJdWd当前位置对应的区域：", " " + amapLocation.getDistrict());
		} else {
			GlobalVariable.mStrDistrict = GlobalConstant.DEFAULT_DISTRACT;
		}
	}
	
	/**
	 * 
	 * @param amapLocation
	 * @description 这只定位后的城市
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月4日 上午11:12:32 
	 * @update 2015年10月4日 上午11:12:32
	 */
	private void setLBSCity(AMapLocation amapLocation) {
		Bundle locBundle = amapLocation.getExtras();
		mTvLbs.setText(Utils.resetCity(amapLocation.getCity()));
		GlobalVariable.sChoiceCity = Utils.resetCity(amapLocation.getCity());
		GlobalVariable.LBSRESULT = true;
		// 第一种做法
		// GlobalVariable.mCurrentCityID = AppApplacation
		// .queryCityID(Utils.resetCity(amapLocation.getCity()));
		// mStrCityID = GlobalVariable.mCurrentCityID;

		// TODO 第二种从本地db处理反转城市id
		if (!Utils
				.isStringEmpty(Utils.resetCity(amapLocation.getCity()))) {
			GlobalVariable.sChoiceCityID = AppApplacation
					.queryNativeCityID(Utils.resetCity(amapLocation
							.getCity()));
			CCLog.i("HomeFragment从本地查找出城市id为："
					+ GlobalVariable.sChoiceCityID);
			// TODO 删除
			// GlobalVariable.sChoiceCityID = "12";
			mStrCityID = GlobalVariable.sChoiceCityID;
		}

		// 更新UI数据信息
//		if (mUiHandler == null) {
//			mUiHandler = new Handler();
//		}
//		mUiHandler.post(new myRunnable());
		getAreaList();
		if (AppApplacation.sAreaList != null
				&& AppApplacation.sAreaList.size() > 0) {
			CCLog.i("该城市 " + mStrCityID + " 下的区域列表已经存在。");
		} else {

			// 获取切换城市下的区域列表
//			AppApplacation.sAreaList = mBaseActivity
//					.getAreaList(mStrCityID);
			mBaseActivity.getAreaList(mStrCityID);
		}
		
		if (amapLocation.getLatitude() != 0) {
			GlobalVariable.mLatitude = amapLocation.getLatitude();
			CCLog.i("setLBSCity 当前位置的 纬度：", " " + amapLocation.getLatitude());
		} else {
			GlobalVariable.mLatitude = 40.003662;
		}

		if (amapLocation.getLongitude() != 0) {
			GlobalVariable.mLongitude = amapLocation.getLongitude();
			CCLog.i("setLBSCity 当前位置的 经度：", " " + amapLocation.getLongitude());
		} else {
			GlobalVariable.mLongitude = 116.465271;
		}
		if (amapLocation.getDistrict() != null) {
			GlobalVariable.mStrDistrict = amapLocation.getDistrict();
			CCLog.i("当前位置对应的区域：", " " + amapLocation.getDistrict());
		} else {
			GlobalVariable.mStrDistrict = GlobalConstant.DEFAULT_DISTRACT;
		}

		if (amapLocation.getProvince() != null
				&& amapLocation.getCity() != null
				&& amapLocation.getDistrict() != null
				&& amapLocation.getRoad() != null) {
			CCLog.i("首页定位成功：",
					amapLocation.getProvince() + amapLocation.getCity()
							+ amapLocation.getDistrict()
							+ amapLocation.getRoad());
		}
		
		String desc = "";
		if (locBundle != null) {
			desc = locBundle.getString("desc");
			GlobalVariable.sLbsInfo = desc;
			CCLog.i("定位详细信息：" + desc);
		}
	}
	
	/**
	 * 
	 * 
	 * @description 设置默认城市
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月4日 上午10:27:31 
	 * @update 2015年10月4日 上午10:27:31
	 */
	private void setDefaultCityInfo() {
		CCLog.i("首页城市", "定位失败");
		mTvLbs.setText(GlobalConstant.DEFAULT_CITY);// 默认北京
		GlobalVariable.sChoiceCity = GlobalConstant.DEFAULT_CITY;// 默认北京
		GlobalVariable.sChoiceCityID = GlobalConstant.DEFAULT_CITY_ID;// 默认北京12
		GlobalVariable.mStrDistrict = GlobalConstant.DEFAULT_DISTRACT; // 默认朝阳区
		GlobalVariable.mLatitude = GlobalConstant.DEFAULT_LATITUDE; // 默认维度
		GlobalVariable.mLongitude = GlobalConstant.DEFAULT_LONGITUDE;// 默认经度
		GlobalVariable.LBSRESULT = false;
	}

	@Override
	protected void resetLayout(View view) {
		RelativeLayout rlLayout = (RelativeLayout) view
				.findViewById(R.id.rl_activity_home_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		CCLog.i("HomeFragment", "resetLayout");
		// 顶部切换标签组
//		hListView = (HorizontalListView) view
//				.findViewById(R.id.horizon_listview);
//		// 初始化顶部分类列表组件
//		initTypeListView();

		// 设置【首页】推荐数据
		// setTuiJianData(mStrCityID);

		// 二维码扫描
		mLlScan = (LinearLayout) view
				.findViewById(R.id.ll_activity_home_erweima);
		mLlScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 首页二维码扫描 页面跳转
//				Intent scanIntent = new Intent(mBaseActivity,
//						CaptureActivity.class);
				
				Intent scanIntent = new Intent(mBaseActivity,
						CaptureActivity.class);
						
				mBaseActivity.baseStartActivity(scanIntent);

			}
		});

		// 点击当前城市
		mTvLbs = (TextView) view.findViewById(R.id.tv_activity_home_city);
		mTvLbs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 首页定位城市修改页面跳转
				Intent lbsIntent = new Intent(mBaseActivity,
						LBSCityActivity.class);
				lbsIntent.putExtra(IntentKeyNames.KEY_CHOICE_CITY,
						GlobalVariable.sChoiceCity);
				lbsIntent.putExtra(IntentKeyNames.KEY_CHOICE_CITY_ID,
						GlobalVariable.sChoiceCityID);
				lbsIntent.putExtra("currentLbsCity",
						currentLbsCity);
				lbsIntent.putExtra("currentLbsCityId",
						currentLbsCityId);
				mBaseActivity.baseStartActivity(lbsIntent);

			}
		});

		// 点击搜索
		mEtSearch = (EditText) view.findViewById(R.id.et_activity_home_search);
		mEtSearch.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 首页搜索页面跳转
				Intent searchIntent = new Intent(mBaseActivity,
						SearchActivity.class);
				mBaseActivity.baseStartActivity(searchIntent);

				return true;
			}
		});

		mLlContainer = (LinearLayout) view
				.findViewById(R.id.ll_home_bottom_menu_container);
		mLlNetError = (LinearLayout) view
				.findViewById(R.id.ll_home_fragment_net_empty);
		mTvNetTips = (TextView) view.findViewById(R.id.tv_yaohe_no_data_text);
		// 改变底部切换布局
		setBottomTabChanged();
		if (!isNetworkConnect()) { // 网络检查
			mLlNetError.setVisibility(View.VISIBLE);
			mTvNetTips.setText("请检查您的网络设置。");
		} else {
			mLlNetError.setVisibility(View.GONE);
		}
		// 更新UI数据信息
//		if (mUiHandler == null) {
//			mUiHandler = new Handler();
//		}
	}

	/**
	 * 设置【首页】推荐数据
	 * 
	 * @Title setTuiJianData
	 */
//	private void setTuiJianData(String city_id) {
//		if (mHomeTuijianFragment == null) {
//			// mHomeTuijianFragment = new HomeTuijianFragment();
//			mHomeTuijianFragment = new HomeTuijianFragment(city_id);
//			switchFragment(mHomeTuijianFragment, TAG_TUIJIAN);
//		}
//	}

	/**
	 * 底部切换布局改变
	 */
	private void setBottomTabChanged() {
		// 设定【首页】选中
		setFooterType(1);
		if (mLoginDataManager.getUserType() != null) {
			if (mLoginDataManager.getUserType().equals(
					GlobalConstant.VALID_VALUE)) {
				View businessView = mInflater.inflate(R.layout.yaohe_footer_no,
						mLlContainer);
				LinearLayout linearLayout = (LinearLayout) businessView
						.findViewById(R.id.ll_yaohe_footer_common);
				SupportDisplay.resetAllChildViewParam(linearLayout);
				// 底部切换标签组,带有吆喝按钮
				getViewById(businessView);
				setYaoHeSelectedFooter();

			} else {
				View personalView = mInflater.inflate(R.layout.yaohe_footer_no,
						mLlContainer);
				LinearLayout linearLayout = (LinearLayout) personalView
						.findViewById(R.id.ll_yaohe_footer_common);
				SupportDisplay.resetAllChildViewParam(linearLayout);
				// 底部切换标签组，没有吆喝
				getViewById(personalView);
				setSelectedFooter();
			}
		} else {
			View personalView = mInflater.inflate(R.layout.yaohe_footer_no,
					mLlContainer);
			LinearLayout linearLayout = (LinearLayout) personalView
					.findViewById(R.id.ll_yaohe_footer_common);
			SupportDisplay.resetAllChildViewParam(linearLayout);
			// 底部切换标签组，没有吆喝
			getViewById(personalView);
			setSelectedFooter();
		}

	}

	/**
	 * 底部切换布局改变
	 */
	private void setFooterChanged() {
		// 设定【首页】选中
		setFooterType(1);
		if (mLoginDataManager.getUserType() != null) {
			if (mLoginDataManager.getUserType().equals(
					GlobalConstant.VALID_VALUE)) {
				View businessView = mInflater.inflate(R.layout.yaohe_footer_no,
						mLlContainer);
				// 底部切换标签组,带有吆喝按钮
				getViewById(businessView);
				setYaoHeSelectedFooter();

			} else {
				View personalView = mInflater.inflate(R.layout.yaohe_footer_no,
						mLlContainer);
				// 底部切换标签组，没有吆喝
				getViewById(personalView);
				setSelectedFooter();
			}
		} else {
			View personalView = mInflater.inflate(R.layout.yaohe_footer_no,
					mLlContainer);
			// 底部切换标签组，没有吆喝
			getViewById(personalView);
			setSelectedFooter();
		}

	}

	protected AppApplacation mApplication = null;
	private List<String> mOpenCity = new ArrayList<String>();
	private int mValidPos;
	private List<City> mCities;

	private void getNativeOpenCity() {
		mApplication = AppApplacation.getCityInstance();

		if (mApplication.isCityListComplite()) {
			mCities = mApplication.getCityList();
			if (mCities != null && mCities.size() > 0) {
				for (int i = 0; i < mCities.size(); i++) {
					mOpenCity.add(mCities.get(i).getCity());
				}
			}
		}
	}

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

	private boolean isNetworkConnect() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
	
	/**
	 * 提示对话框
	 */
	private void dialog(final AMapLocation amapLocation) {

		AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity())
			.setTitle("提示")
		    .setMessage("定位到您的当前城市为"+amapLocation.getCity()+"\n"+"是否使用该城市")
		    .setPositiveButton("切换",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 首页定位城市修改页面跳转
						Intent lbsIntent = new Intent(mBaseActivity,
								LBSCityActivity.class);
						lbsIntent.putExtra(IntentKeyNames.KEY_CHOICE_CITY,
								GlobalVariable.sChoiceCity);
						lbsIntent.putExtra(IntentKeyNames.KEY_CHOICE_CITY_ID,
								GlobalVariable.sChoiceCityID);
						lbsIntent.putExtra("currentLbsCity",
								currentLbsCity);
						lbsIntent.putExtra("currentLbsCityId",
								currentLbsCityId);
						mBaseActivity.baseStartActivity(lbsIntent);
					}
				})
			.setNegativeButton("使用",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//无论定位后的与当前显示的是否为同一个 都需要设置一下定位后的数据，
						//如果不是同一个城市 需要 更新一下数据。
						setLBSCity(amapLocation);
						//if(!currentLbsCityId.equals(GlobalVariable.sChoiceCityID)) {
							// 获取该城市下的首页分类列表
							getData();
							//getHomeTypeInfo(mStrCityID);
							//setTuiJianData(mStrCityID);
							if(mHomeTuijianFragment !=null) {
								mHomeTuijianFragment.getData(mStrCityID);
							}
							if(mHomeGuanzhuFragment != null) {
								mHomeGuanzhuFragment.getData(mStrCityID);
							}
							
						//}
					}
				}).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(true); 
	}
	
	
	private ViewPager pager = null;
	private PagerSlidingTabStrip pagerTabs;
	private ViewPagerAdapter viewPagerAdapter;
	
	public void initViewPager() {
		if(pager == null) {
			pager = (ViewPager) view.findViewById(R.id.pager);
			pagerTabs = (PagerSlidingTabStrip)view.findViewById(R.id.pagerTabs);
			pagerTabs.setTextColor(getActivity().getResources().getColor(R.color.common_home_text_color_title));
			pagerTabs.setTextSize((int)getActivity().getResources().getDimension(R.dimen.common_textsize_middle));
			viewPagerAdapter = new ViewPagerAdapter(mFragmentManager);
			pager.setAdapter(viewPagerAdapter);          
			final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getActivity().getResources()
					.getDisplayMetrics());
			pager.setPageMargin(pageMargin);
			pagerTabs.setViewPager(pager);
			pagerTabs.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					pagerTabs.updateTabStyles(position);
					//setArrowStatus(position);
					CCLog.d("onPageSelected", "onPageSelected position:"+position); 
					//表示是 HomeOtherFragment刷新数据
					if(position>1) {
						mHomeOtherFragment = (HomeOtherFragment) registeredFragments.get(position);
						if(mHomeOtherFragment.isNeedRefreshData()) {
							mHomeOtherFragment.refreshData(
									AppApplacation.sTypeInfos.get(position - 2).city_id,
									AppApplacation.sTypeInfos.get(position - 2).class_id);
						}
			    		
					}
					int len = registeredFragments.size();
					FragmentTransaction ft = mFragmentManager.beginTransaction();
					for(int i=0;i<len;i++) {
						Fragment fragment = registeredFragments.get(i);
						if(fragment !=null) {
							ft.hide(fragment);
						}
						
					}
					ft.commit();
					Message msg = Message.obtain();
					msg.what = 0;
					msg.arg1=position;
					handler.sendMessageDelayed(msg, 200);
					
				}
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
//					CCLog.d(tag, "arg0:"+arg0); 
//					CCLog.d(tag, "arg1:"+arg1); 
//					CCLog.d(tag, "arg2:"+arg2); 
				}
				@Override
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
			pagerTabs.setTabsScrollListener(new TabsScrollListener() {
				
				@Override
				public void onScrollChanged(int l, int t, int oldl, int oldt) {
					
					 if (!getActivity().isFinishing()  && rightArrow != null
		                && leftArrow != null) {
		            if (pagerTabs.getTabsContainerLen() <= windowWidth) {
		            	leftArrow.setVisibility(View.INVISIBLE);
		            	rightArrow.setVisibility(View.INVISIBLE);
		            } else {
		                if (l == 0) {
		                	leftArrow.setVisibility(View.INVISIBLE);
		                	rightArrow.setVisibility(View.VISIBLE);
		                } else if (pagerTabs.getTabsContainerLen() - l <= windowWidth) {
		                	leftArrow.setVisibility(View.VISIBLE);
		                    rightArrow.setVisibility(View.INVISIBLE);
		                } else {
		                	leftArrow.setVisibility(View.VISIBLE);
		                	rightArrow.setVisibility(View.VISIBLE);
		                }
		            }
		        }
					
				}
			});
			
			pagerTabs.updateTabStyles(0);
			pagerTabs.setTabsizeListener(new TabsizeListener() {
				
				public void width(int width) {
					CCLog.i(tag, "getTabsContainerLen:"+width);
					if(width <=windowWidth) {
						leftArrow.setVisibility(View.INVISIBLE); 
						rightArrow.setVisibility(View.INVISIBLE); 
					} else {
						leftArrow.setVisibility(View.VISIBLE); 
						rightArrow.setVisibility(View.VISIBLE); 
					}
				}
			});
			
			CCLog.d(tag, "screen width:"+windowWidth);
			
		} else {
			//donothing...
		}
		
	}
	
	
	private void setArrowStatus(int position) {
		if(mTopTabs != null && mTopTabs.size()>0) {
			if(position ==0) {
				this.leftArrow.setVisibility(View.INVISIBLE); 
				this.rightArrow.setVisibility(View.VISIBLE); 
			} else if(position ==mTopTabs.size()-1){
				this.leftArrow.setVisibility(View.VISIBLE); 
				this.rightArrow.setVisibility(View.INVISIBLE); 
			} else {
				this.leftArrow.setVisibility(View.VISIBLE); 
				this.rightArrow.setVisibility(View.VISIBLE); 
			}
		}
		
		
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.setCustomAnimations(R.anim.umeng_socialize_fade_in,  
                        R.anim.umeng_socialize_fade_out);  
				ft.show(registeredFragments.get(msg.arg1));
				ft.commit();
				break;
			}
		};
	};
	
	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
	public class ViewPagerAdapter extends FragmentPagerAdapter {

	    
	     private int mChildCount = 0;
	    /**
	     * 
	     * @param mTopTabs
	     * @param fm
	     */
	    public ViewPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	        Fragment result = null;
	        switch (position) {
	            case 0:
	                // 推荐
	            	CCLog.d("getItem", "HomeTuijianFragment........");
	                result = new HomeTuijianFragment(mStrCityID);
	                mHomeTuijianFragment = (HomeTuijianFragment) result;
	                break;
	            case 1:
	                // 关注
	            	CCLog.d("getItem", "HomeGuanzhuFragment........");
	                result = new HomeGuanzhuFragment(mStrCityID);
	                mHomeGuanzhuFragment = (HomeGuanzhuFragment) result;
	                break;
	            default:
	            	// Other
	            	CCLog.d("getItem", "HomeOtherFragment........");
	            	mHomeOtherFragment = new HomeOtherFragment(mStrCityID);
	            	result = mHomeOtherFragment;

	                break;
	        }
        	registeredFragments.put(position, result);
	        return result;
	    }

	    @Override
	    public int getCount() {
	        return mTopTabs.size();
	    }

	    @Override
	    public CharSequence getPageTitle(final int position) {
	    	if(mTopTabs != null && mTopTabs.size() >0) {
	    		return mTopTabs.get(position);
	    	}
	    	return "";
	    }

	    /**
	     * On each Fragment instantiation we are saving the reference of that Fragment in a Map
	     * It will help us to retrieve the Fragment by position
	     *
	     * @param container
	     * @param position
	     * @return
	     */
	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	    	//CCLog.d("instantiateItem", "position:"+position);
	    	Fragment fragment = null;
	    	fragment = (Fragment) super.instantiateItem(container, position);
	         return fragment; 
	    }

	    /**
	     * Remove the saved reference from our Map on the Fragment destroy
	     *
	     * @param container
	     * @param position
	     * @param object
	     */
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        //registeredFragments.remove(position);
//	    	CCLog.d(tag, "destroyItem:"+position);
//	        super.destroyItem(container, position, object);
	    }
//	     @Override
//	     public void notifyDataSetChanged() {         
//	           mChildCount = getCount();
//	           super.notifyDataSetChanged();
//
//	     }
//
//	     @Override
//	     public int getItemPosition(Object object)   {          
//	           if ( mChildCount > 0) {
//	        	   mChildCount --;
//	           return POSITION_NONE;
//	           }
//	           return super.getItemPosition(object);
//
//	     }
	    
	    @Override
	    public int getItemPosition(Object object) {
	         return POSITION_NONE;
	    }
	
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
	public void removeFragment() {
		try {
			int len = registeredFragments.size();
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			for(int i=0;i<len;i++) {
				Fragment fragment = registeredFragments.get(i);
				if(fragment !=null) {
					ft.remove(fragment);
				}
				
			}
			ft.commit();
			ft=null;
			registeredFragments.clear();
			mFragmentManager.executePendingTransactions();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void doResetData() {
		super.doResetData();
		mHomeTuijianFragment.doResetData();
		mHomeGuanzhuFragment.doResetData();
		if(registeredFragments != null && registeredFragments.size()>2) {
			int len = registeredFragments.size();
			for(int i=0;i<len;i++) {
				try {
					HomeOtherFragment fragment = (HomeOtherFragment)registeredFragments.get(i-2);
					fragment.refreshData(
							AppApplacation.sTypeInfos.get(i-2).city_id,
							AppApplacation.sTypeInfos.get(i-2).class_id);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}
		}
		
		
	}

}
