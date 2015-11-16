package com.collcloud.yaohe.activity.fujin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.map.ShowPoiSearchActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.ClassifyListInfo;
import com.collcloud.yaohe.api.info.ClassifyListInfo.Classify;
import com.collcloud.yaohe.api.info.DistrictListInfo;
import com.collcloud.yaohe.api.info.DistrictListInfo.DistrictList;
import com.collcloud.yaohe.api.info.NearByListInfo;
import com.collcloud.yaohe.api.info.NearByListInfo.NearBy;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.FuJinShopAdapter;
import com.collcloud.yaohe.ui.adapter.FuJinShopAdapter.OnNearItemClickListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.FuJinShopPopWindow;
import com.collcloud.yaohe.ui.view.FujinCategoryPopWindow;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;
import com.collcloud.yaohe.ui.view.SingleLayoutListView.OnRefreshListener;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 附近的商家
 * 
 * @ClassName FuJinActivity
 * @Description 附近的商家 （分类、 商圈）
 * @author CollCloud_小米
 */
public class FuJinActivity extends CommonActivity implements Callback,
		OnClickListener, AMapLocationListener {

	/** 【附近】分类 */
	private TextView mTvCategory;
	private TextView mTvCategoryArrow;
	private LinearLayout mLlCategory;

	/** 【附近】商圈 */
	private TextView mTvShop;
	private TextView mTvShopArrow;
	private LinearLayout mLlShop;

	/** 附近商圈显示内容的List */
	private SingleLayoutListView mLvPullToRefreshView;
	/** 附近商圈list列表适配器 */
	private FuJinShopAdapter mAdapter = null;

	private Button mBtnFujinShop = null;
	private Handler mHandler;

	// ********* 分类标签相关 开始********** //
	private FujinCategoryPopWindow mSelectCategoryPopWindow = null;
	/**
	 * 一级分类信息-显示的名称
	 */
	private List<String> mCategoryDatas = new ArrayList<String>();
	/**
	 * 一级分类信息-对应的名称ID
	 */
	private List<String> mCategoryIDs = new ArrayList<String>();
	/**
	 * 二级分类信息-显示的名称
	 */
	private List<String> mTwoClassfyData = new ArrayList<String>();
	/**
	 * 二级分类信息-对应的名称ID
	 */
	private List<String> mTwoClassfyID = new ArrayList<String>();
	// ********* 分类标签相关 结束********** //

	// ********* 商圈标签相关 开始********** //
	private FuJinShopPopWindow mSelectCityTownWindow = null;
	/**
	 * 当前城市对应的区域列表
	 */
	private List<String> mAreasList = new ArrayList<String>();
	/**
	 * 区域列表名称对应的ID信息
	 */
	private List<String> mAreaIDs = new ArrayList<String>();
	/**
	 * 当前区域对应的商圈列表
	 */
	private List<String> mDistrictsList = new ArrayList<String>();
	/**
	 * 区域对应名称对应的ID信息
	 */
	private List<String> mDistrictIDs = new ArrayList<String>();
	private String[] nearList = { "<500m", "<1km", "<3km", "<5km", "全城" };
	private String[] nearId = { "500", "1000", "3000", "5000", "" };
	// ********* 商圈标签相关 结束********** //

	/**
	 * 从服务器获取的附近商圈集
	 */
	private NearByListInfo mNearByListInfo;
	/**
	 * 附近商圈列表内容
	 */
	private List<NearBy> mNearList = new ArrayList<NearBy>();

	private double mLongitude;
	private double mLatitude;
	/**
	 * 数据项为空时，显示提示文字
	 */
	private LinearLayout mLlEmpty;
	private final String NO_DISTRICT = "暂无商圈入住";
	private final String DEFAULT_ALL_CLASSFY = "全部分类";
	
	private String tag = FuJinActivity.class.getSimpleName();

	private ArrayList<DistrictList> mAreaDistricts = new ArrayList<DistrictList>();
	private ArrayList<Classify> mOneClassfyInfos = new ArrayList<Classify>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_fujin);
		setFooterType(2);
		super.onCreate(savedInstanceState);
		init();
		if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) { // 如果从首页进入城市选择页面，切换城市
			getNearByList(GlobalVariable.sChoiceCityID, "1", null, null, null,
					null, null, null);
		} else {
			getNearByList(GlobalVariable.sChoiceCityID, "1", null, null, null,
					null, null, null);
		}
	}

	/**
	 * 初始化处理分类列表
	 */
	private void init() {
		// 一级分类列表- 程序启动时，已经预加载

		String _long = new String().valueOf(GlobalVariable.mLongitude);
		String lat   = new String().valueOf(GlobalVariable.mLatitude);
		CCLog.i("-------------------------------------------：", _long);
		CCLog.i("-------------------------------------------：", lat);


		if (AppApplacation.sOneClassFy != null
				&& AppApplacation.sOneClassFy.size() > 0) {
			mCategoryDatas.clear();
			mCategoryIDs.clear();

			mCategoryDatas.add("全部分类");
			mCategoryIDs.add("");
			mTwoClassfyData.add("全部分类");
			for (int i = 0; i < AppApplacation.sOneClassFy.size(); i++) {
				if (AppApplacation.sOneClassFy.get(i).title != null) {
					mCategoryDatas.add(AppApplacation.sOneClassFy.get(i).title);
					mCategoryIDs.add(AppApplacation.sOneClassFy.get(i).id);
				}
			}
		} else {
			mCategoryDatas.clear();
			mTwoClassfyData.clear();
			mCategoryDatas.add("全部分类");
			getOneClassifyList("0");
		}

		// 当前城市对应的区域列表
		if (AppApplacation.sAreaList != null
				&& AppApplacation.sAreaList.size() > 0) {

			mAreasList.clear();
			mAreaIDs.clear();
			mAreasList.add("附近");
			mDistrictsList.addAll(Arrays.asList(nearList));
			for (int i = 0; i < AppApplacation.sAreaList.size(); i++) {
				if (AppApplacation.sAreaList.get(i).title != null) {
					mAreasList.add(AppApplacation.sAreaList.get(i).title);
					mAreaIDs.add(AppApplacation.sAreaList.get(i).id);
				}
			}

		} else {
			mAreasList.clear();
			mAreaIDs.clear();
			mDistrictsList.clear();
			mAreasList.add("附近");
			mDistrictsList.addAll(Arrays.asList(nearList));
		}

		// 一级二级分类信息
		mSelectCategoryPopWindow = new FujinCategoryPopWindow(
				FuJinActivity.this, mCategoryDatas, mTwoClassfyData, mHandler,
				mTvCategory);
		mSelectCategoryPopWindow.popIsShowing = false;

		// 商圈分类信息
		mSelectCityTownWindow = new FuJinShopPopWindow(FuJinActivity.this,
				mAreasList, mDistrictsList, mHandler, mTvShop);
		mSelectCityTownWindow.popIsShowing = false;
	}

	private void initListener() {
		if (mAdapter != null) {
			mAdapter.setOnNearItemClickListener(new OnNearItemClickListener() {

				@Override
				public void onNearItemClick(int position, String shop_id,
						String member_id) {
					String faceUrl = mNearList.get(position).face;
					CCLog.d(tag, "faceUrl:"+faceUrl);
					
					Intent intent = new Intent(FuJinActivity.this,
							DetailsBusinessInfoActivity.class);
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shop_id);
					intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID,
							member_id);
					intent.putExtra("faceUrl",faceUrl);
					baseStartActivity(intent);
					
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置选中状态
		setFooterType(2);
		// initListener();
	}

	/**
	 * 获取附近商家列表
	 * 
	 * @Title getNearByList
	 * @param cityid
	 *            城市ID(post传参)
	 * @param page
	 *            分页数(1代表1页，以后类推)
	 * @param lat
	 *            纬度
	 * @param _long
	 *            经度
	 * @param one_id
	 *            第一级分类
	 * @param industry_class_id
	 *            第二级分类
	 * @param district_id
	 *            商圈ID
	 * @param rice
	 *            距离(多少米)500代表500米
	 */
	private void getNearByList(String cityid, String page, String lat,
			String _long, String one_id, String industry_class_id,
			String district_id, String rice) {
		_long = new String().valueOf(GlobalVariable.mLongitude);
		lat   = new String().valueOf(GlobalVariable.mLatitude);
		CCLog.i("-------------------------------------------：", _long);
		CCLog.i("-------------------------------------------：", lat);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("city_id", cityid);
		params.addBodyParameter("page", page);
		params.addBodyParameter("_long", _long);
		params.addBodyParameter("lat", lat);
		params.addBodyParameter("one_id", one_id);
		params.addBodyParameter("industry_class_id", industry_class_id);
		params.addBodyParameter("district_id", district_id);
		params.addBodyParameter("rice", rice);
		String url = ContantsValues.NEAR_BY_SHOP_LIST;
		url =  url + "&_long="+_long+ "&lat="+lat;
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							if (!Utils.isStringEmpty(responseInfo.result)) {
								CCLog.i("附近商圈data：", responseInfo.result);
							}
							if (jsonObject.has("data")) {
								mNearByListInfo = GsonUtils.json2Bean(
										responseInfo.result,
										NearByListInfo.class);
								if (mNearByListInfo != null) {
									if (mNearByListInfo.data != null
											&& mNearByListInfo.data.size() > 0) {
										mNearList.clear();
										if (mNearByListInfo.data.size() == 1) {
											if (Utils
													.isStringEmpty(mNearByListInfo.data
															.get(0).id)
													&& Utils.isStringEmpty(mNearByListInfo.data
															.get(0).member_id)) {
												mLlEmpty.setVisibility(View.VISIBLE);
												mLvPullToRefreshView
														.setVisibility(View.GONE);
												return;
											} else {
												mLlEmpty.setVisibility(View.GONE);
												mLvPullToRefreshView
														.setVisibility(View.VISIBLE);
											}
										}
										for (int j = 0; j < mNearByListInfo.data
												.size(); j++) {

											NearBy nearBy = new NearBy();
											if (mNearByListInfo.data.get(j).id != null) {
												nearBy.id = mNearByListInfo.data
														.get(j).id;
											}
											if (mNearByListInfo.data.get(j).member_id != null) {
												nearBy.member_id = mNearByListInfo.data
														.get(j).member_id;
											}
											if (mNearByListInfo.data.get(j).shop_id != null) {
												nearBy.shop_id = mNearByListInfo.data
														.get(j).shop_id;
											}
											if (!Utils
													.isStringEmpty(mNearByListInfo.data
															.get(j).face)) {
												nearBy.face = URLs.IMG_PRE
														+ mNearByListInfo.data
																.get(j).face;
											}
											if (mNearByListInfo.data.get(j).type != null) {
												nearBy.type = mNearByListInfo.data
														.get(j).type;
											}
											if (mNearByListInfo.data.get(j).full_name != null) {
												nearBy.full_name = mNearByListInfo.data
														.get(j).full_name;
											}
											if (mNearByListInfo.data.get(j).star != null) {
												nearBy.star = mNearByListInfo.data
														.get(j).star;
											}
											if (mNearByListInfo.data.get(j).content != null) {
												nearBy.content = mNearByListInfo.data
														.get(j).content;
											}
											if (mNearByListInfo.data.get(j).fans_num != null) {
												nearBy.fans_num = mNearByListInfo.data
														.get(j).fans_num;
											}
											if (mNearByListInfo.data.get(j).area_id != null) {
												nearBy.area_id = mNearByListInfo.data
														.get(j).area_id;
												nearBy.area_name = Utils
														.queryAreaName(nearBy.area_id);
											}
											if (mNearByListInfo.data.get(j).district_id != null) {
												nearBy.district_id = mNearByListInfo.data
														.get(j).district_id;
											}
											if (mNearByListInfo.data.get(j).range != null) {
												nearBy.range = mNearByListInfo.data
														.get(j).range;
											}
											mNearList.add(nearBy);
										}

										if (mNearList != null
												&& mNearList.size() > 0) {
											mLvPullToRefreshView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
											// 设定附近商圈内容
											setShopNearByInfo();

											// UIHelper.ToastMessage(FuJinActivity.this,
											// "数据加载完成。");
										}
									} else {
										mLlEmpty.setVisibility(View.VISIBLE);
										mLvPullToRefreshView
												.setVisibility(View.GONE);
									}
								}
							} else {
								mLvPullToRefreshView.setVisibility(View.GONE);
								mLlEmpty.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							mLvPullToRefreshView.setVisibility(View.GONE);
							mLlEmpty.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mLvPullToRefreshView.setVisibility(View.GONE);
						mLlEmpty.setVisibility(View.VISIBLE);
					}
				});

	}

	@Override
	public List<DistrictList> getDistrictList(String areaID) {
		return super.getDistrictList(areaID);
	}

	/**
	 * 设定附近推荐商圈显示内容
	 */
	private void setShopNearByInfo() {
		mAdapter = new FuJinShopAdapter(FuJinActivity.this, mNearList);
		mLvPullToRefreshView.setAdapter(mAdapter);
		Utils.resetListViewHeightBasedOnChildren(mLvPullToRefreshView);

		initListener();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 分类标签
		case R.id.ll_activity_fujin_fenlei:

			if (mSelectCityTownWindow.popIsShowing) {
				mSelectCityTownWindow.dismiss();
				mSelectCityTownWindow.popIsShowing = false;
				mTvShop.setTextColor(Color.rgb(85, 85, 85));
				mTvShopArrow
						.setBackgroundResource(R.drawable.icon_fujin_category_arrow_gray);
			}
			// 分类标签处理
			categoryControl();
			break;

		// 区域-商圈按钮选项
		case R.id.ll_activity_fujin_shangjia:
			if (mSelectCategoryPopWindow.popIsShowing) {
				mSelectCategoryPopWindow.dismiss();
				mSelectCategoryPopWindow.popIsShowing = false;
				mTvCategory.setTextColor(Color.rgb(85, 85, 85));
				mTvCategoryArrow
						.setBackgroundResource(R.drawable.icon_fujin_category_arrow_gray);
			}
			// 商圈标签处理
			shopControl();
			mSelectCityTownWindow.mAreaAdapter.setSelectedItem(0);
			mSelectCityTownWindow.mAreaAdapter.notifyDataSetChanged();
			mAreaIndex = 0;
			mDistrictsList.clear();
			mDistrictsList.addAll(Arrays.asList(nearList));
			mSelectCityTownWindow.mTownAdapter.refreshData(mDistrictsList);
			break;
		case R.id.btn_activity_fujin_location: // 标题栏附近图标
			Intent geocoderIntent = new Intent(FuJinActivity.this,
					ShowPoiSearchActivity.class);
			baseStartActivity(geocoderIntent);
			break;

		default:
			break;
		}

	}

	private void shopControl() {
		if (mSelectCityTownWindow.popIsShowing) {
			mTvShop.setTextColor(Color.rgb(85, 85, 85));
			mTvShopArrow
					.setBackgroundResource(R.drawable.icon_fujin_category_arrow_gray);
			mSelectCityTownWindow.dismiss();
			mSelectCityTownWindow.popIsShowing = false;
		} else {
			mTvShop.setTextColor(Color.rgb(252, 96, 71));
			mTvShopArrow
					.setBackgroundResource(R.drawable.icon_fujin_category_arrow_red);
			mSelectCityTownWindow.showPopUpWindow();
			mSelectCityTownWindow.popIsShowing = true;
			mSelectCityTownWindow.mAreaAdapter.setSelectedItem(0);
			mSelectCityTownWindow.mAreaAdapter.notifyDataSetChanged();
		}
	}

	private void categoryControl() {
		if (mSelectCategoryPopWindow.popIsShowing) {
			mTvCategory.setTextColor(Color.rgb(85, 85, 85));
			mTvCategoryArrow
					.setBackgroundResource(R.drawable.icon_fujin_category_arrow_gray);
			mSelectCategoryPopWindow.dismiss();
			mSelectCategoryPopWindow.popIsShowing = false;
		} else {

			mTvCategory.setTextColor(Color.rgb(252, 96, 71));
			mTvCategoryArrow
					.setBackgroundResource(R.drawable.icon_fujin_category_arrow_red);
			mSelectCategoryPopWindow.showPopUpWindow();
			mSelectCategoryPopWindow.popIsShowing = true;
			mSelectCategoryPopWindow.mFuJinCategoryAdapter.setSelectedItem(0);
			mSelectCategoryPopWindow.mFuJinCategoryAdapter
					.notifyDataSetChanged();
		}
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.rl_activity_fujin_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		mLvPullToRefreshView = (SingleLayoutListView) findViewById(R.id.lv_fujin_shagnquan_content);

		// 顶部切换标签组

		mTvCategory = (TextView) this
				.findViewById(R.id.tv_activity_fujin_fenlei);
		mTvCategoryArrow = (TextView) this
				.findViewById(R.id.tv_activity_fujin_fenlei_arrow);
		mLlCategory = (LinearLayout) this
				.findViewById(R.id.ll_activity_fujin_fenlei);
		mTvShop = (TextView) this.findViewById(R.id.tv_activity_fujin_shangjia);
		mTvShopArrow = (TextView) this
				.findViewById(R.id.tv_activity_fujin_shangjia_arrow);
		mLlShop = (LinearLayout) this
				.findViewById(R.id.ll_activity_fujin_shangjia);
		mLlCategory.setOnClickListener(this);
		mLlShop.setOnClickListener(this);

		// 附近地图显示商圈内容
		mBtnFujinShop = (Button) findViewById(R.id.btn_activity_fujin_location);
		mBtnFujinShop.setOnClickListener(this);

		// ******　＊＊＊＊＊＊ ******* //
		// 列表内容为空
		mLlEmpty = (LinearLayout) findViewById(R.id.ll_shop_nearby_fragment_empty);
		mHandler = new Handler(FuJinActivity.this);

		mLvPullToRefreshView.setCanLoadMore(false);
		mLvPullToRefreshView.setCanRefresh(true);
		mLvPullToRefreshView.setAutoLoadMore(false);
		mLvPullToRefreshView.setMoveToFirstItemAfterRefresh(false);
		mLvPullToRefreshView.setDoRefreshOnUIChanged(false);

		mLvPullToRefreshView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadData(0);
			}
		});
	}

	/**
	 * 加载数据啦~
	 */
	private void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					Message _Msg = mUiHandler.obtainMessage(0);
					mUiHandler.sendMessage(_Msg);
					CCLog.i("附近商家  —— 可以加载数据了。。");
					break;
				}

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
					Message _Msg = mUiHandler.obtainMessage(1);
					mUiHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// TODO 获取网络数据
				getNearByList(GlobalVariable.sChoiceCityID, "1",
						String.valueOf(mLatitude), String.valueOf(mLongitude),
						null, null, null, null);
				break;
			case 1:
				// UIHelper.ToastMessage(mBaseActivity, "数据加载完成。");
				mLvPullToRefreshView.onRefreshComplete(); // 下拉刷新完成
				break;
			default:
				break;
			}
		};
	};

	private int mAreaIndex;
	private int mOneClassIndex;
	private String mOne_id = "";
	int districtSelectedMark = 0;
	@Override
	public boolean handleMessage(Message msg) {
		Bundle data = msg.getData();

		switch (msg.what) {
		case 1:
			int categoryIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_INDEX, 0);
			mSelectCategoryPopWindow.mFuJinCategoryAdapter
					.setSelectedItem(categoryIndex);
			mSelectCategoryPopWindow.mFuJinCategoryAdapter
					.notifyDataSetChanged();
			mOneClassIndex = categoryIndex;

			// if (mCategoryDatas.size() > 0) {
			// mTvCategory.setText(mCategoryDatas.get(categoryIndex));
			// }
			// categoryControl();
			// assessNetTips();
			if (categoryIndex == 0) {
				mTwoClassfyData.clear();
				mTwoClassfyData.add("全部分类");
				mSelectCategoryPopWindow.mFuJinCategoryAdapter
						.refreshData(mTwoClassfyData);
			} else if (categoryIndex > 0) {
				if (AppApplacation.sOneClassFy.size() > 0) {
					mTwoClassfyData.clear();
					// 获取二级分类
					mOneClassfyInfos = getTwoClassifyList(AppApplacation.sOneClassFy
							.get(categoryIndex - 1).id);
				}
			}

			if (categoryIndex == 0) {
				mOne_id = "";
			} else {
				if (AppApplacation.sOneClassFy != null
						&& AppApplacation.sOneClassFy.size() > 0) {
					mOne_id = AppApplacation.sOneClassFy.get(categoryIndex - 1).id;
				}
			}

			break;
		case 2: // 点击商圈-区域列表子项事件
			int areaIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_CITY_INDEX, 0);

			mSelectCityTownWindow.mAreaAdapter.setSelectedItem(areaIndex);
			mSelectCityTownWindow.mAreaAdapter.notifyDataSetChanged();
			mAreaIndex = areaIndex;
			// 选择区域对应下的商圈列表内容
			if (areaIndex == 0) {
				mDistrictsList.clear();
				mDistrictsList.addAll(Arrays.asList(nearList));
				mSelectCityTownWindow.mTownAdapter.refreshData(mDistrictsList);
			} else if (areaIndex > 0) {

				if (AppApplacation.sAreaList.size() > 0) {
					mAreaDistricts.clear();
					// 通过区域id获取该区域下的商圈列表
					mAreaDistricts = getShopDistrictLists(AppApplacation.sAreaList
							.get(areaIndex - 1).id);
				}
			}

			break;
		case 3:
			districtSelectedMark = 1;
			int districtIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_TOWN_INDEX, 0);
			shopControl();
			if (mDistrictsList.size() > 0) {
				if (mDistrictsList.get(districtIndex).equals(NO_DISTRICT)) {
					if (mAreasList != null && mAreasList.size() > 0) {
						mTvShop.setText(mAreasList.get(mAreaIndex));
					} else {
						mTvShop.setText(mDistrictsList.get(districtIndex));
					}
				} else {
					mTvShop.setText(mDistrictsList.get(districtIndex));
				}

				assessNetTips();

				// 请求数据分析
				if (mAreaIndex == 0) {
					getNearByList(GlobalVariable.sChoiceCityID, "1",
							String.valueOf(GlobalVariable.mLatitude),
							String.valueOf(GlobalVariable.mLongitude), mOne_id,
							null, null, nearId[districtIndex]);
				} else {
					if (mDistrictsList.get(districtIndex).equals(NO_DISTRICT)) {

						getNearByList(GlobalVariable.sChoiceCityID, "1",
								String.valueOf(mLatitude),
								String.valueOf(mLongitude), mOne_id,
								mAreasList.get(mAreaIndex), null, null);
					} else {
						getNearByList(GlobalVariable.sChoiceCityID, "1",
								String.valueOf(mLatitude),
								String.valueOf(mLongitude), mOne_id,
								mAreasList.get(mAreaIndex),
								mDistrictIDs.get(districtIndex), null);
					}
				}

			}

			break;
		case 4: // 通过区域id获取该区域下的商圈列表
			int index = 0;
			if (data.getInt("index") != -1) {
				index = data.getInt("index");
			}
			CCLog.i("点击区域列表第  " + index + "项");
			if (mAreaDistricts != null && mAreaDistricts.size() > 0) {
				mDistrictsList.clear();
				mDistrictIDs.clear();
				for (int i = 0; i < mAreaDistricts.size(); i++) {
					mDistrictsList.add(mAreaDistricts.get(i).title);
					mDistrictIDs.add(mAreaDistricts.get(i).id);
				}
				mSelectCityTownWindow.mTownAdapter.refreshData(mDistrictsList);
			} else {
				mDistrictsList.clear();
				mDistrictIDs.clear();
				if (index == 0) {
					mDistrictsList.addAll(Arrays.asList(nearList));
					mDistrictIDs.addAll(Arrays.asList(nearId));
				} else {
					// TODO 此区域暂无商圈
					mDistrictsList.add(NO_DISTRICT);
					mDistrictIDs.add("");
				}
				mSelectCityTownWindow.mTownAdapter.refreshData(mDistrictsList);

			}
			break;

		case 5:
			int twoClassfyIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_TWO_CLASSFY_INDEX,
					0);

			categoryControl();

			if (mTwoClassfyData.size() > 0) {
				if (mTwoClassfyData.get(twoClassfyIndex).equals(
						DEFAULT_ALL_CLASSFY)) {
					mTvCategory.setText(DEFAULT_ALL_CLASSFY);
				} else {
					mTvCategory.setText(mTwoClassfyData.get(twoClassfyIndex));
				}

				assessNetTips();

				String tmpDistrictId = null;
				if (districtSelectedMark == 1){  // 商圈被选择
					int twoDistrictIndex = data.getInt(
							IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_TOWN_INDEX, 0);
					tmpDistrictId = mDistrictIDs.get(twoDistrictIndex);
				}
				// 请求数据分析
				if (mOneClassIndex == 0) {
					getNearByList(GlobalVariable.sChoiceCityID, "1", null,
								null, mOne_id, null,tmpDistrictId, null);
				} else {
					if (mTwoClassfyData.get(twoClassfyIndex).equals(
							DEFAULT_ALL_CLASSFY)) {
						getNearByList(GlobalVariable.sChoiceCityID, "1", null,
								null, mOne_id, null, tmpDistrictId, null);
					} else {
						getNearByList(GlobalVariable.sChoiceCityID, "1", null,
								null, mOne_id,	mTwoClassfyID.get(twoClassfyIndex),tmpDistrictId, null);
					}
				}

			}
			break;

		case 6:
			int oneClassIndex = 0;
			if (data.getInt("OneClassIndex") != -1) {
				oneClassIndex = data.getInt("index");
			}
			CCLog.i("点击一级分类列表第  " + oneClassIndex + "项");
			if (mOneClassfyInfos != null && mOneClassfyInfos.size() > 0) {
				mTwoClassfyData.clear();
				mTwoClassfyID.clear();
				for (int i = 0; i < mOneClassfyInfos.size(); i++) {
					mTwoClassfyData.add(mOneClassfyInfos.get(i).title);
					mTwoClassfyID.add(mOneClassfyInfos.get(i).id);
				}
				mSelectCategoryPopWindow.mFujinTwoClassfyAdapter
						.refreshData(mTwoClassfyData);
			} else {
				mTwoClassfyData.clear();
				mTwoClassfyID.clear();
				mTwoClassfyData.add(DEFAULT_ALL_CLASSFY);
				mTwoClassfyID.add("");
				mSelectCategoryPopWindow.mFujinTwoClassfyAdapter
						.refreshData(mTwoClassfyData);

			}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * 进度对话框提示
	 * 
	 * @Title assessNetTips
	 */
	private void assessNetTips() {
		ApiAccess.showProgressDialog(FuJinActivity.this, "加载中..",
				R.style.progress_dialog);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ApiAccess.dismissProgressDialog();
			}
		}, 500);
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

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (ApiAccess.isProgressDialogShow(this)) {
			ApiAccess.dismissProgressDialog();
		}
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			if (amapLocation.getLongitude() != 0) {
				CCLog.i("onLocationChanged当前位置的 经度--------------->：", " " + amapLocation.getLongitude());
				mLongitude = amapLocation.getLongitude();
			} else {
			}
			if (amapLocation.getLatitude() != 0) {
				CCLog.i("onLocationChanged当前位置的 纬度--------------->：", " " + amapLocation.getLatitude());
				mLatitude = amapLocation.getLatitude();
			} else {
			}
		}

	}

	/**
	 * 通过区域id获取该区域下的商圈列表
	 * 
	 * @Title getShopDistrictLists
	 * @param 区域ID
	 * @return 商圈列表信息
	 */
	private ArrayList<DistrictList> getShopDistrictLists(String area_id) {

		// 用来封装参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("area_id", area_id);

		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, ContantsValues.SHANGQUAN, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						DistrictListInfo districtListInfo;
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("data")) {
								try {
									// 数据处理
									districtListInfo = GsonUtils.json2Bean(
											responseInfo.result,
											DistrictListInfo.class);
									if (districtListInfo.data != null
											&& districtListInfo.data.size() > 0) {
										for (int i = 0; i < districtListInfo.data
												.size(); i++) {
											DistrictList district = new DistrictList();
											if (districtListInfo.data.get(i).title != null) {
												district.title = districtListInfo.data
														.get(i).title;
												CCLog.i("区域内商圈列表",
														districtListInfo.data
																.get(i).title);
											}
											if (districtListInfo.data.get(i).id != null) {
												district.id = districtListInfo.data
														.get(i).id;
											}
											mAreaDistricts.add(district);
										}
									}

									Message district_Msg = mHandler
											.obtainMessage(64);
									Bundle bundle = new Bundle();
									bundle.putInt("index", mAreaIndex);
									district_Msg.setData(bundle);
									district_Msg.what = 4;
									mHandler.sendMessage(district_Msg);
								} catch (Exception e) {

								}

							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String msg) {
					}
				});
		return mAreaDistricts;

	}

	/**
	 * 获取二级分类列表信息
	 */
	public ArrayList<Classify> getTwoClassifyList(String parent_id) {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("parent_id", parent_id);
		http.send(HttpMethod.POST, ContantsValues.BUSINESSTYPE, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						ClassifyListInfo classifyListInfo;
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("data")) {
								try {
									// 数据处理
									classifyListInfo = GsonUtils.json2Bean(
											responseInfo.result,
											ClassifyListInfo.class);
									if (mOneClassfyInfos != null
											&& mOneClassfyInfos.size() > 0) {
										mOneClassfyInfos.clear();
									}
									mOneClassfyInfos = new ArrayList<Classify>();
									if (classifyListInfo.data != null
											&& classifyListInfo.data.size() > 0) {
										for (int i = 0; i < classifyListInfo.data
												.size(); i++) {
											Classify classifyInfo = new Classify();
											if (classifyListInfo.data.get(i).title != null) {
												classifyInfo.title = classifyListInfo.data
														.get(i).title;
												CCLog.i("二级分类： ",
														classifyListInfo.data
																.get(i).title);
											}
											if (classifyListInfo.data.get(i).id != null) {
												classifyInfo.id = classifyListInfo.data
														.get(i).id;
											}
											mOneClassfyInfos.add(classifyInfo);
										}
									}
									Message district_Msg = mHandler
											.obtainMessage(6);
									Bundle bundle = new Bundle();
									bundle.putInt("OneClassIndex",
											mOneClassIndex);
									district_Msg.setData(bundle);
									district_Msg.what = 6;
									mHandler.sendMessage(district_Msg);

								} catch (Exception e) {

								}

							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String msg) {
					}
				});

		return mOneClassfyInfos;
	}
}
