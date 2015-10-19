package com.collcloud.yaohe.activity.lbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.shenqingweishangjia.ChoiceDistrictActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.ResponseCityInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.LBSCityAdapter;
import com.collcloud.yaohe.ui.adapter.LBSHotCityAdapter;
import com.collcloud.yaohe.ui.model.City;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.BladeView;
import com.collcloud.yaohe.ui.view.BladeView.OnItemClickListener;
import com.collcloud.yaohe.ui.view.MyGridView;
import com.collcloud.yaohe.ui.view.PinnedHeaderListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 城市定位（基于高德定位SDK）
 * 
 * @ClassName LBSCityActivity
 * @Description 基准定位或者选择热门城市等
 * @author CollCloud_小米
 */
public class LBSCityActivity extends CommonActivity implements OnClickListener,
		AMapLocationListener,
		android.widget.AdapterView.OnItemClickListener {

	/** 标题 */
	private TextView mTvTitle = null;
	/** 返回 */
	private LinearLayout mLlBack = null;
	private LocationManagerProxy mLocationManagerProxy;

	/** 定位成功 城市相关组件 */
	private LinearLayout mLlLbsCity = null;
	/** 城市尚未开通说明 */
	private TextView mTvCityDiscri = null;
	/** 当前的城市 */
	private TextView mTvCurrentCity = null;
	/** 城市对于的ID */
	private String mStrCityId = "12";
	/** 遍历检查城市是否已开通，如果开通，记录其遍历位置 */
	private int mValidPos;
	private boolean mIsValid = true;

	/** 定位失败 */
	private LinearLayout mLlLbsFail = null;
	/** 定位失败，重新尝试 */
	private TextView mTvRetry = null;

	private MyGridView mGvList;
	private LBSHotCityAdapter mGvAdapter;
	private List<String> mHotCitys = new ArrayList<String>();
	private List<String> mOpenCity = new ArrayList<String>();
	private TextView mTvHotTitle;

	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<City>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private AppApplacation mApplication;

	private PinnedHeaderListView mCityListView;
	private BladeView mLetter;
	private List<City> mCities;
	/**
	 * 快速索引城市适配器
	 */
	private LBSCityAdapter mCityAdapter;

	/**
	 * 从服务器取得热门城市信息集合（包含城市名和城市ID）
	 */
	private ResponseCityInfo mHotCityInfo;
	/**
	 * 从服务器取得已开通城市信息集合（包含城市名和城市ID）
	 */
	// private ResponseCityInfo mOpenCityInfo;

	private String mStrActivityType;
	private String mStrGetCity;
	private String mStrGetCityId;
	
	//当没有进行定位时，从 homefragment定位获取的城市 以及城市id
	private String currentLbsCity;
	private String currentLbsCityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_lbs_city);
		setFooterType(1);
		super.onCreate(savedInstanceState);
		
		//AppApplacation.mListeners.add(this);
		mStrGetCity = getStringExtra(IntentKeyNames.KEY_CHOICE_CITY);
		mStrGetCityId = getStringExtra(IntentKeyNames.KEY_CHOICE_CITY_ID);
		mStrActivityType = getStringExtra("shenqing");

		if(GlobalVariable.LBSRESULT) {
			currentLbsCity = getStringExtra("currentLbsCity");
			currentLbsCityId = getStringExtra("currentLbsCityId");
			mTvCurrentCity.setText(currentLbsCity);
			mLlLbsFail.setVisibility(View.GONE);
			mLlLbsCity.setVisibility(View.VISIBLE);
		}
		
		// 初始化处理快速索引的城市列表内容
		initData();
		// 获取已开通的城市列表
		getOpenCity();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setFooterType(1);
	}

	/**
	 * 从服务器获取热门城市信息
	 * 
	 * @Title getHotCity
	 * @Description 热门城市
	 */
	private void getHotCity() {

		mHotCityInfo = new ResponseCityInfo();
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, URLs.HOST + URLs.CITY_LIST,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						CCLog.i("正在加载 热门城市列表。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						mTvHotTitle.setVisibility(View.VISIBLE);
						try {
							JSONObject jsonObject = new JSONObject(
									responseInfo.result);
							mHotCityInfo = mHotCityInfo
									.parseHotCity(jsonObject);
							if (mHotCityInfo.getmHotCity().size() > 0) {
								for (int i = 0; i < mHotCityInfo.getmHotCity()
										.size(); i++) {
									CCLog.i("热门城市：", mHotCityInfo.getmHotCity()
											.get(i).getmStrTitle());
									mHotCitys.add(mHotCityInfo.getmHotCity()
											.get(i).getmStrTitle());
								}
							}

							mGvAdapter = new LBSHotCityAdapter(
									LBSCityActivity.this, mHotCitys);
							mGvList.setAdapter(mGvAdapter);
							mGvAdapter.notifyDataSetChanged();

						} catch (Exception e) {
							mTvHotTitle.setVisibility(View.GONE);
							if (e.getMessage() != null) {
								CCLog.e("热门城市数据返回异常\n" + " "
										+ e.getMessage().toString());
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mTvHotTitle.setVisibility(View.GONE);
					}
				});
	}

	/**
	 * 获取已开通城市
	 * 
	 * @Title getOpenCity
	 */
	private void getOpenCity() {
		// mOpenCityInfo = new ResponseCityInfo();
		// HttpUtils http = new HttpUtils();
		// http.send(HttpMethod.GET, URLs.HOST + URLs.CITY_LIST,
		// new RequestCallBack<String>() {
		// @Override
		// public void onSuccess(ResponseInfo<String> responseInfo) {
		// try {
		// JSONObject jsonObject = new JSONObject(
		// responseInfo.result);
		// mOpenCityInfo = mOpenCityInfo
		// .parseOpenCity(jsonObject);
		// if (mOpenCityInfo.getmOpenCity().size() > 0) {
		// for (int i = 0; i < mOpenCityInfo
		// .getmOpenCity().size(); i++) {
		// mOpenCity.add(mOpenCityInfo.getmOpenCity()
		// .get(i).getmStrTitle());
		// }
		// }
		//
		// } catch (Exception e) {
		// if (e.getMessage() != null) {
		// CCLog.e("已开通数据返回异常\n" + " "
		// + e.getMessage().toString());
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(HttpException arg0, String msg) {
		// }
		// });

		// TODO 第二种方式
		if (mCities != null && mCities.size() > 0) {
			for (int i = 0; i < mCities.size(); i++) {
				mOpenCity.add(mCities.get(i).getCity());
			}
		}
	}

	/**
	 * 初始化处理快速索引的城市列表内容
	 * 
	 * @Description
	 */
	private void initData() {
		if(!GlobalVariable.LBSRESULT) {
			lbsCity();
		}

		mApplication = AppApplacation.getCityInstance();

		if (mApplication.isCityListComplite()) {
			mCities = mApplication.getCityList();
			mSections = mApplication.getSections();
			mMap = mApplication.getMap();
			mPositions = mApplication.getPositions();
			mIndexer = mApplication.getIndexer();

			mCityAdapter = new LBSCityAdapter(LBSCityActivity.this, mCities,
					mMap, mSections, mPositions);
			mCityListView.setAdapter(mCityAdapter);
			mCityListView.setOnScrollListener(mCityAdapter);
			mCityListView.setPinnedHeaderView(LayoutInflater.from(
					LBSCityActivity.this).inflate(
					R.layout.item_pinnerlist_group_, mCityListView, false));
			// TODO 字母集
			 mLetter.setVisibility(View.VISIBLE);

		}
	}
	
	private void lbsCity() {
		if (ApiAccess.isProgressDialogShow(mBaseActivity)) {
			ApiAccess.dismissProgressDialog();
		}
		ApiAccess.showProgressDialog(LBSCityActivity.this, "定位中...");
		// 初始化定位，采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_lbs_city_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		// 页面标题
		mTvTitle = (TextView) this
				.findViewById(R.id.tv_commonn_back_title_text);
		mLlBack = (LinearLayout) this
				.findViewById(R.id.ll_commonn_text_title_back);
		mLlBack.setOnClickListener(this);

		mLlLbsCity = (LinearLayout) this
				.findViewById(R.id.ll_current_city_show);
		mTvCityDiscri = (TextView) this
				.findViewById(R.id.tv_current_city_shuoming);
		mTvCurrentCity = (TextView) this.findViewById(R.id.tv_current_city_);
		mTvCurrentCity.setOnClickListener(this);

		mLlLbsFail = (LinearLayout) this
				.findViewById(R.id.ll_lbs_retry_viewgroup);
		mTvRetry = (TextView) this.findViewById(R.id.tv_lbs_retry);
		mTvRetry.setOnClickListener(this);
		mLlLbsFail.setVisibility(View.GONE);

		mTvTitle.setText("城市定位");
		mGvList = (MyGridView) this
				.findViewById(R.id.gv_activity_details_hot_city);
		mGvList.setOnItemClickListener(this);

		// 从服务器获取热门城市信息
		getHotCity();

		mTvHotTitle = (TextView) findViewById(R.id.tv_lbs_hot_city);

		mCityListView = (PinnedHeaderListView) findViewById(R.id.citys_list);
		mCityListView.setOnItemClickListener(this);
		mLetter = (BladeView) findViewById(R.id.ll_lbs_bladeview);
		mLetter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(String s) {
				if (mIndexer.get(s) != null) {
					mCityListView.setSelection(mIndexer.get(s));
				}
			}

		});

		// TODO 字母集
		// mLetter.setVisibility(View.GONE);
		mBtnIsCancelButton = true;

	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();

		// 返回主页面
		// backHome(Utils.strFromView(mTvCurrentCity), mStrCityId);
		this.finish();
	}

	/**
	 * 返回主页面
	 * 
	 * @Title backHome
	 */
	private void backHome(String city, String cityID,boolean resetFragmentInfo) {
		if (!mIsValid) {
			UIHelper.ToastMessage(LBSCityActivity.this, "该城市尚未开通，请选择其他。");
			return;
		}
		if (!Utils.isStringEmpty(mStrActivityType)
				&& mStrActivityType.equals("shenqing")) {
			Intent lbsIntent = new Intent(LBSCityActivity.this,
					MainActivity.class);
			Bundle bundle = new Bundle();
			lbsIntent.putExtra("lbsCityID", cityID);
			lbsIntent.putExtra("lbsCity", city);
			lbsIntent.putExtras(bundle);
			lbsIntent.putExtra("resetFragmentInfo", resetFragmentInfo);
			setResult(33, lbsIntent);
			LBSCityActivity.this.finish();
		} else {

			Intent maIntent = new Intent(LBSCityActivity.this,
					MainActivity.class);
			maIntent.putExtra(IntentKeyNames.KEY_LBS_CURRENT_CITY, city);
			maIntent.putExtra(IntentKeyNames.KEY_LBS_CURRENT_CITY_ID, cityID);
			maIntent.putExtra("resetFragmentInfo", resetFragmentInfo);
			baseStartActivity(maIntent);
			this.finish();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_commonn_text_title_back:// 标题返回
			// 返回主页面
			// backHome(Utils.strFromView(mTvCurrentCity), mStrCityId);
			this.finish();
			break;
		case R.id.tv_current_city_: // 当前城市
			// 返回主页面
			backHome(Utils.strFromView(mTvCurrentCity),currentLbsCityId,true);
			break;
		case R.id.tv_lbs_retry:
			ApiAccess.showProgressDialog(LBSCityActivity.this, "定位中...");
			// 注意更换定位时间后，需要先将定位请求删除，再进行定位请求
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
			mLocationManagerProxy.setGpsEnable(false);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// 移除定位请求
		if(mLocationManagerProxy !=null) {
			mLocationManagerProxy.removeUpdates(this);
			// 销毁定位
			mLocationManagerProxy.destroy();
		}
		
	}

	protected void onDestroy() {
		super.onDestroy();

	}

//	@Override
//	public void onCityComplite() {
//
//	}
//
//	@Override
//	public void onNetChange() {
//
//	}

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

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (ApiAccess.isProgressDialogShow(this)) {
			ApiAccess.dismissProgressDialog();
		}
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			mLlLbsFail.setVisibility(View.GONE);
			mLlLbsCity.setVisibility(View.VISIBLE);

			// 定位成功回调信息，设置相关消息
			if (amapLocation.getCity() == null) {
				mTvCurrentCity.setText("北京");
			} else {
				mTvCurrentCity.setText(Utils.resetCity(amapLocation.getCity()));
				if (checkValid(Utils.resetCity(amapLocation.getCity()))) {
					// mStrCityId = mOpenCityInfo.getmOpenCity().get(mValidPos)
					// .getmStrId();
					// TODO 第二种方式，
					if (mCities != null && mCities.size() > 0) {
						mStrCityId = mCities.get(mValidPos).getCityId();
					}
					CCLog.i("定位成功的城市 ID ：", " " + mStrCityId);
					mTvCityDiscri.setVisibility(View.GONE);
					mIsValid = true;
					currentLbsCityId = mStrCityId;
				} else {
					mTvCityDiscri.setVisibility(View.VISIBLE);
					mIsValid = false;
				}
			}

			if (amapLocation.getProvince() != null
					&& amapLocation.getCity() != null
					&& amapLocation.getDistrict() != null) {
				CCLog.i("城市定位页面成功：",
						amapLocation.getProvince() + amapLocation.getCity()
								+ amapLocation.getDistrict());
			}

		} else {
			mLlLbsFail.setVisibility(View.VISIBLE);
			mLlLbsCity.setVisibility(View.GONE);
			CCLog.e("城市定位页面：", "定位失败:"
					+ amapLocation.getAMapException().getErrorCode());
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		final int pos = position;
		if (arg0.getAdapter().equals(mCityAdapter)) { // 快速索引城市列表
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					// mTvCurrentCity.setText(mCityAdapter.getItem(pos).getCity());
					if (checkValid(mCityAdapter.getItem(pos).getCity())) {

						mStrCityId = mCityAdapter.getItem(pos).getCityId();
						CCLog.e("选择列表城市 ID ：", " " + mStrCityId);
						mTvCityDiscri.setVisibility(View.GONE);
						mIsValid = true;
						backHome(mCityAdapter.getItem(pos).getCity(),
								mStrCityId,true);
					} else {
						UIHelper.ToastMessage(LBSCityActivity.this,
								"该城市尚未开通，请选择其他。");
						mIsValid = false;
						mTvCityDiscri.setVisibility(View.VISIBLE);
					}

				}
			});

		} else {
			if (mHotCitys != null && mHotCitys.size() > 0) {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						// mTvCurrentCity.setText(mHotCitys.get(pos));
						if (checkValid(mHotCitys.get(pos))) {
							mTvCityDiscri.setVisibility(View.GONE);
							mStrCityId = mHotCityInfo.getmHotCity().get(pos)
									.getmStrId();
							CCLog.i("选择热门城市 ID ：", " " + mStrCityId);
							mIsValid = true;
							backHome(mHotCitys.get(pos), mStrCityId,true);
						} else {
							UIHelper.ToastMessage(LBSCityActivity.this,
									"该城市尚未开通，请选择其他。");
							mIsValid = false;
							mTvCityDiscri.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		}
	}
}
