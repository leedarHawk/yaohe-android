package com.collcloud.yaohe.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.frame.xlistview.XListView;
import com.collcloud.frame.xlistview.XListView.IXListViewListener;
import com.collcloud.frame.xlistview.XListView.OnSlidingDirectionListen;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeCommentActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.activity.webview.WebViewActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.HomeCallInfo;
import com.collcloud.yaohe.api.info.HomeCallInfo.CallInfo;
import com.collcloud.yaohe.api.info.HomeRotate;
import com.collcloud.yaohe.api.info.HomeRotate.Rotate;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseFragment;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.ui.adapter.HomeAdapter;
import com.collcloud.yaohe.ui.adapter.HomeAdapter.OnButtonClickListener;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter.OnPagerItemClickListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 吆喝APP 主页部分-推荐
 * 
 * @ClassName HomeTuijianFragment
 * @Description 主页推荐内容
 * @author CollCloud_小米
 */
@SuppressLint("ValidFragment")
public class HomeTuijianFragment extends BaseFragment  {

	// ********* 首页轮换图 ******** //
	/**
	 * 从服务器获取的首页轮换图数据集
	 */
	private HomeRotate mHomeRotateInfo;
	/**
	 * 从服务器获取的首页轮换图数据List
	 */
	private List<Rotate> mRotates = new ArrayList<HomeRotate.Rotate>();
	/**
	 * 图片显示viewpager容器
	 */
	private ViewPager mPager;
	/**
	 * 底部小圆点
	 */
	private ViewGroup mCirleVGroup;
	/**
	 * 图片轮播适配器
	 */
	private HomePageAdapter mPagerAdapter;
	/**
	 * 图片数据源
	 */
	private List<String> mDataPath = new ArrayList<String>();

	// ********* 首页推荐内容- 吆喝列表 ******** //
	/**
	 * 主页面显示内容的List
	 */
	private XListView mLvPullToRefreshView;
	/**
	 * 主页list列表适配器
	 */
	public HomeAdapter mAdapter = null;
	/**
	 * 从服务器获取的吆喝信息集
	 */
	private HomeCallInfo mHomeCallInfo;
	/**
	 * 吆喝列表内容
	 */
	private List<CallInfo> mCallInfos = new ArrayList<CallInfo>();

	// ********* 首页推荐内容- 吆喝列表 ******** //

	/**
	 * 城市id
	 */
	private String mStrCityId;

	/**
	 * 数据项为空时，显示提示文字
	 */
	private LinearLayout mLlEmpty;
	/**
	 * 提示文字
	 */
	private TextView mTvTips = null;
	/**
	 * 网络异常提示文字
	 */
	private TextView mTvNetError = null;
	/**
	 * 首页轮换图视图
	 */
	private RelativeLayout mRlRotate;

	//private PullScrollView pullScrollView;
	//private LinearLayout contentLayout;
	//总页数
	private int mTotalPage = 0;
	//当前页
	private int currentPage = 1;
	private static final int NUM = 15;
	
	private static String tag = HomeTuijianFragment.class.getSimpleName();
	
	//RelativeLayout pptViewLayout;
	
	//临时保存城市id 为了与当前的进行比较 。如果一样 就不进行数据加载，如果不一致则进行加载
	private String tmpStrCityId;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tuijian_root, container,
				false);
		CCLog.i("推荐Fragment", "onCreateView");
		//pullScrollView = (PullScrollView) v.findViewById(R.id.scroll);
		//LEE 轮转图片部件
		mRlRotate = (RelativeLayout)inflater.inflate(R.layout.home_tuijian_ppt_layout, null);
		
		ApiAccess.showProgressDialog(getActivity(), "数据加载中..",
				R.style.progress_dialog);

//		contentLayout = (LinearLayout) inflater.inflate(
//				R.layout.layout_content, null);
		//initView(v);

		// 获取首页轮换图列表
		//getHomeRotateListInfo(mStrCityId);
		// // 获取首页吆喝内容列表
		// getHomeCallList(mStrCityId, mLoginDataManager.getMemberId());
		return v;
	}

//	private void initView(View view) {
//		initContentView(view);
//
////		pullScrollView.addBodyView(contentLayout);
////		pullScrollView.setOnPullListener(this);
//	}

	@Override
	public void onResume() {
		super.onResume();
		CCLog.i("推荐Fragment", "onResume");
		// 接收从城市定位页面传递的城市ID
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			ApiAccess.showProgressDialog(getActivity(), "数据加载中..",
					R.style.progress_dialog);
			mStrCityId = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			CCLog.i("推荐Fragment", "onResume \n获取的城市ID ：" + mStrCityId);
			getData(mStrCityId);

		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					currentPage = 1;
					getData(mStrCityId);
				}
			}).start();

		}
	}
	/**
	 * 
	 * 
	 * @description 通过http请求数据
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月5日 上午11:00:46 
	 * @update 2015年10月5日 上午11:00:46
	 */
	public void getData(String mStrCityId) {
		this.mStrCityId = mStrCityId;
		//此参数 从 MainActivity来 为了表示 是否刷新数据
		boolean refreshData = this.getActivity().getIntent().getBooleanExtra("refreshData", false);
		if(tmpStrCityId != null) {
			if(tmpStrCityId.equals(mStrCityId) && !refreshData) {
				CCLog.d(tag, "tmpStrCityId is the same to the old id so not load data.......");
				ApiAccess.dismissProgressDialog();
			} else {
				tmpStrCityId = mStrCityId;
				CCLog.d(tag, "tmpStrCityId is not null but different old id so load data.......");
				getHomeRotateListInfo(mStrCityId);
				getHomeCallList(mStrCityId, mLoginDataManager.getMemberId(),true);
			}
		} else {
			tmpStrCityId = mStrCityId;
			CCLog.d(tag, "tmpStrCityId is null load data.......");
			getHomeRotateListInfo(mStrCityId);
			getHomeCallList(mStrCityId, mLoginDataManager.getMemberId(),true);
		}
		
	}

	/**
	 * 获取首页轮换图列表
	 */
	private void getHomeRotateListInfo(String cityid) {

		HttpUtils http = new HttpUtils();
		String url = ContantsValues.HOME_BANNER_ROTATE_URL + "&city_id="
				+ cityid;

		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);

							if (jsonObject.has("data")) {
								mHomeRotateInfo = GsonUtils.json2Bean(
										responseInfo.result, HomeRotate.class);
								if (mHomeRotateInfo != null) {
									if (mHomeRotateInfo.data != null
											&& mHomeRotateInfo.data.size() > 0) {
										mRlRotate.setVisibility(View.VISIBLE);
										mDataPath.clear();
										mRotates.clear();

										for (int j = 0; j < mHomeRotateInfo.data
												.size(); j++) {
											Rotate rotateInfo = new Rotate();

											if (!Utils
													.isStringEmpty(mHomeRotateInfo.data
															.get(j).img)) {
												rotateInfo.img = URLs.IMG_PRE
														+ mHomeRotateInfo.data
																.get(j).img;
												mDataPath.add(rotateInfo.img);
											}
											if (mHomeRotateInfo.data.get(j).type != null) {
												rotateInfo.type = mHomeRotateInfo.data
														.get(j).type;
											}
											if (mHomeRotateInfo.data.get(j).content_id != null) {
												rotateInfo.content_id = mHomeRotateInfo.data
														.get(j).content_id;
											}
											if (mHomeRotateInfo.data.get(j).title != null) {
												rotateInfo.title = mHomeRotateInfo.data
														.get(j).title;
											}
											if (mHomeRotateInfo.data.get(j).link_url != null) {
												rotateInfo.link_url = mHomeRotateInfo.data
														.get(j).link_url;
											}
											if (mHomeRotateInfo.data.get(j).id != null) {
												rotateInfo.id = mHomeRotateInfo.data
														.get(j).id;
											}
											if (mHomeRotateInfo.data.get(j).service_id != null) {
												rotateInfo.service_id = mHomeRotateInfo.data
														.get(j).service_id;
											}
											mRotates.add(rotateInfo);
										}

										if (mDataPath != null
												&& mDataPath.size() > 0) {
											// 设定首页轮播图
											setHomeRotateInfo();
										}
									} else {
										mRlRotate.setVisibility(View.GONE);
									}

								}
							} else {
								mRlRotate.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mRlRotate.setVisibility(View.GONE);
					}
				});

	}

	/**
	 * 获取首页吆喝列表
	 * 
	 * @Title getHomeCallList
	 */
//	private void getHomeCallList1(String cityid, String memberId) {
//
//		HttpUtils http = new HttpUtils();
//		String url = ContantsValues.HOME_CALL_LIST_URL + "&city_id=" + cityid
//				+ "&member_id=" + memberId + "&time="
//				+ System.currentTimeMillis();
//		CCLog.d(tag, "data list url:"+url); 
//
//		http.send(HttpRequest.HttpMethod.GET, url, null,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						JSONObject jsonObject;
//						try {
//							jsonObject = new JSONObject(responseInfo.result);
//							if (responseInfo.result != null) {
//								CCLog.e("推荐Fragment获取的吆喝信息：",
//										responseInfo.result.toString());
//							}
//
//							if (jsonObject.has("data")) {
//								mHomeCallInfo = new HomeCallInfo();
//								mHomeCallInfo = GsonUtils
//										.json2Bean(responseInfo.result,
//												HomeCallInfo.class);
//								if (mHomeCallInfo != null) {
//									if (mHomeCallInfo.data != null
//											&& mHomeCallInfo.data.size() > 0) {
//										mCallInfos.clear();
//										mPage = mPage + 1;
//										if (mHomeCallInfo.data.size() <= NUM) {
//											//pullScrollView.setfooterViewGone();
//										}
//										if (mHomeCallInfo.data.size() == 1) {
//											if (Utils
//													.isStringEmpty(mHomeCallInfo.data
//															.get(0).id)
//													&& Utils.isStringEmpty(mHomeCallInfo.data
//															.get(0).shop_id)) {
//												mLlEmpty.setVisibility(View.VISIBLE);
//												mLvPullToRefreshView
//														.setVisibility(View.GONE);
//												return;
//											} else {
//												mLlEmpty.setVisibility(View.GONE);
//												mLvPullToRefreshView
//														.setVisibility(View.VISIBLE);
//											}
//										}
//										mCallInfos = new ArrayList<CallInfo>();
//										mTotal = mHomeCallInfo.data.size();
//										int requestCount = NUM;
//										CCLog.i("page = ", mPage + " ");
//										if (mTotal > mPage * NUM) {
//											requestCount = mPage * NUM;
//										} else {
//											requestCount = mTotal;
//										}
//										CCLog.i("requestCount = ", requestCount
//												+ " ");
//										for (int j = 0; j < requestCount; j++) {
//											CallInfo callInfo = new CallInfo();
//											if (mHomeCallInfo.data.get(j).shop_id != null) {
//												callInfo.shop_id = mHomeCallInfo.data
//														.get(j).shop_id;
//											}
//											if (mHomeCallInfo.data.get(j).shop_name != null) {
//												callInfo.shop_name = mHomeCallInfo.data
//														.get(j).shop_name;
//											}
//											if (mHomeCallInfo.data.get(j).shop_star != null) {
//												callInfo.shop_star = mHomeCallInfo.data
//														.get(j).shop_star;
//											}
//											if (mHomeCallInfo.data.get(j).shop_fans_num != null) {
//												callInfo.shop_fans_num = mHomeCallInfo.data
//														.get(j).shop_fans_num;
//											}
//											if (mHomeCallInfo.data.get(j).id != null) {
//												callInfo.id = mHomeCallInfo.data
//														.get(j).id;
//											}
//											if (mHomeCallInfo.data.get(j).guanzhu != null) {
//												callInfo.guanzhu = mHomeCallInfo.data
//														.get(j).guanzhu;
//											}
//											if (mHomeCallInfo.data.get(j).member_id != null) {
//												callInfo.member_id = mHomeCallInfo.data
//														.get(j).member_id;
//											}
//											if (mHomeCallInfo.data.get(j).service_id != null) {
//												callInfo.service_id = mHomeCallInfo.data
//														.get(j).service_id;
//											}
//											if (mHomeCallInfo.data.get(j).type != null) {
//												callInfo.type = mHomeCallInfo.data
//														.get(j).type;
//											}
//											if (mHomeCallInfo.data.get(j).title != null) {
//												callInfo.title = mHomeCallInfo.data
//														.get(j).title;
//											}
//											if (mHomeCallInfo.data.get(j).addtime != null) {
//												callInfo.addtime = mHomeCallInfo.data
//														.get(j).addtime;
//											}
//											if (mHomeCallInfo.data.get(j).city_id != null) {
//												callInfo.city_id = mHomeCallInfo.data
//														.get(j).city_id;
//											}
//
//											if (mHomeCallInfo.data.get(j).content != null) {
//												callInfo.content = mHomeCallInfo.data
//														.get(j).content;
//											}
//											if (!Utils
//													.isStringEmpty(mHomeCallInfo.data
//															.get(j).img)) {
//												callInfo.img = URLs.IMG_PRE
//														+ mHomeCallInfo.data
//																.get(j).img;
//											}
//											if (mHomeCallInfo.data.get(j).collection_num != null) {
//												callInfo.collection_num = mHomeCallInfo.data
//														.get(j).collection_num;
//											}
//											if (mHomeCallInfo.data.get(j).zan_num != null) {
//												callInfo.zan_num = mHomeCallInfo.data
//														.get(j).zan_num;
//											}
//											if (mHomeCallInfo.data.get(j).comment_num != null) {
//												callInfo.comment_num = mHomeCallInfo.data
//														.get(j).comment_num;
//											}
//											mCallInfos.add(callInfo);
//										}
//
//										if (mCallInfos != null
//												&& mCallInfos.size() > 0) {
//											mLvPullToRefreshView
//													.setVisibility(View.VISIBLE);
//											mLlEmpty.setVisibility(View.GONE);
//
//											// 设定首页吆喝内容
//											setHomeRecommendInfo();
//										}
//									} else {
//										mLlEmpty.setVisibility(View.VISIBLE);
//										mLvPullToRefreshView
//												.setVisibility(View.GONE);
//										mRlRotate.setVisibility(View.GONE);
//									}
//								}
//							} else {
//								mLvPullToRefreshView.setVisibility(View.GONE);
//								mLlEmpty.setVisibility(View.VISIBLE);
//								mRlRotate.setVisibility(View.GONE);
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//							mLvPullToRefreshView.setVisibility(View.GONE);
//							mLlEmpty.setVisibility(View.VISIBLE);
//							mRlRotate.setVisibility(View.GONE);
//						}
//						onLoad();
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						mLvPullToRefreshView.setVisibility(View.GONE);
//						mLlEmpty.setVisibility(View.VISIBLE);
//						mRlRotate.setVisibility(View.GONE);
//						onLoad();
//					}
//				});
//
//	}
	
	/**
	 * 
	 * @param cityid
	 * @param memberId
	 * @param refreshData 是否是刷新数据  false：加载更多
	 */
	private void getHomeCallList(String cityid, String memberId,final boolean refreshData) {
		
		HttpUtils http = new HttpUtils();
		String url = ContantsValues.HOME_CALL_LIST_URL + "&city_id=" + cityid
				+ "&member_id=" + memberId + "&time="
				+ System.currentTimeMillis()+"&page="+currentPage;
		CCLog.d(tag, "data list url:"+url); 

		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						CCLog.d(tag, "refresh data:"+refreshData);
						if(refreshData) {
							currentPage = 1;
							mCallInfos.clear();
						}
						JSONObject jsonObject;
						ApiAccess.dismissProgressDialog();
						try {
							jsonObject = new JSONObject(responseInfo.result);
							if (responseInfo.result != null) {
								CCLog.e("推荐Fragment获取的吆喝信息：",
										responseInfo.result.toString());
							}
							
							try {
								mTotalPage = jsonObject.optInt("pageNumber");
							} catch(Exception e) {
								e.printStackTrace();
							}
							

							if (jsonObject.has("data")) {
								mHomeCallInfo = new HomeCallInfo();
								mHomeCallInfo = GsonUtils
										.json2Bean(responseInfo.result,
												HomeCallInfo.class);
								if (mHomeCallInfo != null) {
									if (mHomeCallInfo.data != null
											&& mHomeCallInfo.data.size() > 0) {
										//mCallInfos.clear();
										currentPage = currentPage + 1;
										if (mHomeCallInfo.data.size() <= NUM) {
											//pullScrollView.setfooterViewGone();
										}
										if (mHomeCallInfo.data.size() == 1) {
											if (Utils
													.isStringEmpty(mHomeCallInfo.data
															.get(0).id)
													&& Utils.isStringEmpty(mHomeCallInfo.data
															.get(0).shop_id)) {
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
										ArrayList<CallInfo> mCallInfosTmp = new ArrayList<CallInfo>();
										int dataSize = mHomeCallInfo.data.size();
										//LEE

										for (int j = 0; j < dataSize; j++) {
											CallInfo callInfo = new CallInfo();
											if (mHomeCallInfo.data.get(j).shop_id != null) {
												callInfo.shop_id = mHomeCallInfo.data
														.get(j).shop_id;
											}
											if (mHomeCallInfo.data.get(j).shop_name != null) {
												callInfo.shop_name = mHomeCallInfo.data
														.get(j).shop_name;
											}
											if (mHomeCallInfo.data.get(j).shop_star != null) {
												callInfo.shop_star = mHomeCallInfo.data
														.get(j).shop_star;
											}
											if (mHomeCallInfo.data.get(j).shop_fans_num != null) {
												callInfo.shop_fans_num = mHomeCallInfo.data
														.get(j).shop_fans_num;
											}
											if (mHomeCallInfo.data.get(j).id != null) {
												callInfo.id = mHomeCallInfo.data
														.get(j).id;
											}
											if (mHomeCallInfo.data.get(j).guanzhu != null) {
												callInfo.guanzhu = mHomeCallInfo.data
														.get(j).guanzhu;
											}
											if (mHomeCallInfo.data.get(j).member_id != null) {
												callInfo.member_id = mHomeCallInfo.data
														.get(j).member_id;
											}
											if (mHomeCallInfo.data.get(j).service_id != null) {
												callInfo.service_id = mHomeCallInfo.data
														.get(j).service_id;
											}
											if (mHomeCallInfo.data.get(j).type != null) {
												callInfo.type = mHomeCallInfo.data
														.get(j).type;
											}
											if (mHomeCallInfo.data.get(j).title != null) {
												callInfo.title = mHomeCallInfo.data
														.get(j).title;
											}
											if (mHomeCallInfo.data.get(j).addtime != null) {
												callInfo.addtime = mHomeCallInfo.data
														.get(j).addtime;
											}
											if (mHomeCallInfo.data.get(j).city_id != null) {
												callInfo.city_id = mHomeCallInfo.data
														.get(j).city_id;
											}

											if (mHomeCallInfo.data.get(j).content != null) {
												callInfo.content = mHomeCallInfo.data
														.get(j).content;
											}
											if (mHomeCallInfo.data.get(j).s_content != null) {
												callInfo.s_content = mHomeCallInfo.data
														.get(j).s_content;
											}
											
											if (!Utils
													.isStringEmpty(mHomeCallInfo.data
															.get(j).img)) {
												callInfo.img = URLs.IMG_PRE
														+ mHomeCallInfo.data
																.get(j).img;
											}
											if (!Utils
													.isStringEmpty(mHomeCallInfo.data
															.get(j).s_img)) {
												callInfo.s_img = URLs.IMG_PRE
														+ mHomeCallInfo.data
																.get(j).s_img;
											}
											
											callInfo.c_id = mHomeCallInfo.data
													.get(j).c_id;
											
											if (mHomeCallInfo.data.get(j).collection_num != null) {
												callInfo.collection_num = mHomeCallInfo.data
														.get(j).collection_num;
											}
											if (mHomeCallInfo.data.get(j).zan_num != null) {
												callInfo.zan_num = mHomeCallInfo.data
														.get(j).zan_num;
											}
											if (mHomeCallInfo.data.get(j).comment_num != null) {
												callInfo.comment_num = mHomeCallInfo.data
														.get(j).comment_num;
											}
											mCallInfosTmp.add(callInfo);
										}
										//刷新数据
										if(refreshData) {
											//mCallInfos = mCallInfosTmp;
											mCallInfos.addAll(mCallInfosTmp);
										} else {//加载更多数据
											mCallInfos.addAll(mCallInfosTmp);
										}

										if (mCallInfos != null
												&& mCallInfos.size() > 0) {
											mLvPullToRefreshView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);

											// 设定首页吆喝内容
											setHomeRecommendInfo();
										}
									} else {
										mLlEmpty.setVisibility(View.VISIBLE);
										mLvPullToRefreshView
												.setVisibility(View.GONE);
										mRlRotate.setVisibility(View.GONE);
									}
								}
							} else {
								mLvPullToRefreshView.setVisibility(View.GONE);
								mLlEmpty.setVisibility(View.VISIBLE);
								mRlRotate.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							mLvPullToRefreshView.setVisibility(View.GONE);
							mLlEmpty.setVisibility(View.VISIBLE);
							mRlRotate.setVisibility(View.GONE);
						}
						onLoad();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mLvPullToRefreshView.setVisibility(View.GONE);
						mLlEmpty.setVisibility(View.VISIBLE);
						mRlRotate.setVisibility(View.GONE);
						onLoad();
					}
				});

	}

	@Override
	protected void doOnErrorResponseAction() {
		super.doOnErrorResponseAction();
		ApiAccess.dismissProgressDialog();
		mLvPullToRefreshView.setVisibility(View.GONE);
		mLlEmpty.setVisibility(View.GONE);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void resetLayout(View view) {
		initContentView(view);
	}

	private void initContentView(View view) {
		CCLog.d(tag, "initContentView.........");
//		LinearLayout rlLayout = (LinearLayout) view
//				.findViewById(R.id.ll_home_fragment_tuijian);
//		SupportDisplay.resetAllChildViewParam(rlLayout);
		// 主页内容列表
		mLvPullToRefreshView = (XListView) view
				.findViewById(R.id.lv_home_content);
		mLvPullToRefreshView.addHeaderView(mRlRotate);
		mLvPullToRefreshView.setPullLoadEnable(true);
		mLvPullToRefreshView.setPullRefreshEnable(true);
		
		mLvPullToRefreshView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				refresh();
			}
			
			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
		mLvPullToRefreshView.setOnSlidingDirectionListen(new OnSlidingDirectionListen() {
			
			@Override
			public void onScrollUpWard(float value) {
			}
			
			@Override
			public void onScrollTop() {
			}
			
			@Override
			public void onScrollDownWard(float value) {
			}
			
			@Override
			public void onScrollBottom() {
				
			}
		});

		
		// 列表内容为空
		mLlEmpty = (LinearLayout) view
				.findViewById(R.id.ll_home_fragment_empty);
		// 提示文字
		mTvTips = (TextView) view
				.findViewById(R.id.tv_fragment_extra_text_link);
		mTvTips.setText(Html
				.fromHtml("<font size=\"14\" color=\"#23a3fc\">还没有商家发布吆喝</font>"
						+ ""));
		// 提示文字监听事件
		mTvTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent fujinIntent = new Intent();
				// fujinIntent.setClass(getActivity(), FuJinActivity.class);
				// mBaseActivity.baseStartActivity(fujinIntent);
			}
		});
		// 首页轮换图视图
		//mRlRotate = (RelativeLayout) view.findViewById(R.id.rl_home_viewpager);

		// 图片轮播视图
		mPager = (ViewPager) mRlRotate.findViewById(R.id.vp_top_slide_page);
		mCirleVGroup = (ViewGroup) mRlRotate.findViewById(R.id.top_circle_images);
		// 网络错误提示
		mTvNetError = (TextView) view.findViewById(R.id.tv_home_net_error);

		CCLog.i("推荐Fragment", "resetLayout");

		if (!AppApplacation.getInstance().isNetworkConnected()) {
			mTvNetError.setVisibility(View.VISIBLE);
			mLvPullToRefreshView.setVisibility(View.GONE);
			return;
		} else {
			mTvNetError.setVisibility(View.GONE);
			mLvPullToRefreshView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设定首页轮播图
	 */
	private void setHomeRotateInfo() {
		if(mPagerAdapter != null) {
			mPager.setAdapter(mPagerAdapter);
		} else {
			mPagerAdapter = new HomePageAdapter(mBaseActivity, mDataPath);
			mPager.setAdapter(mPagerAdapter);
			mPager.setOnPageChangeListener(new ImageViewPagerChangeListener());
		}
		refreshRotateData();
		
	}
	
	private void refreshRotateData() {
		// viewpager图片轮播的圆点处理
		if (mDataPath != null && mDataPath.size() > 0) {
			setmPager(mPager);
			setmCircleView(mCirleVGroup);
			setmCircleData(mDataPath);
			initViewPagerCircleView();
		}
	}

	/**
	 * 设定首页推荐显示内容
	 */
	public void setHomeRecommendInfo() {
		ApiAccess.dismissProgressDialog();
		if(mAdapter !=null) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new HomeAdapter(mBaseActivity, mCallInfos);
			mLvPullToRefreshView.setAdapter(mAdapter);
			//measureListView(mLvPullToRefreshView);
			initControlerListenner();
		}
		

		

	}

	public void refereshTuiJianData(List<CallInfo> callInfos) {
		this.mCallInfos = callInfos;

		if (mCallInfos != null && mCallInfos.size() > 0) {
			mLvPullToRefreshView.setVisibility(View.VISIBLE);
			if (mAdapter != null) {
				mAdapter.refresh(mCallInfos);
			} else {
				mAdapter = new HomeAdapter(getActivity(), mCallInfos);
				mLvPullToRefreshView.setAdapter(mAdapter);
			}
		} else {
			mLvPullToRefreshView.setVisibility(View.GONE);
		}
	}

	/**
	 * 刷新首页banner轮播图
	 */
	public void refreshRotateData(List<String> imgs) {
		this.mDataPath = imgs;
		if (mDataPath != null && mDataPath.size() > 0) {
			mRlRotate.setVisibility(View.VISIBLE);
			mPagerAdapter.refreshData(mDataPath);
		} else {
			mRlRotate.setVisibility(View.GONE);
		}
	}

	/** 构造函数 */
	public HomeTuijianFragment() {
		super();
	}

	public HomeTuijianFragment(String cityId) {
		this.mStrCityId = cityId;
	}

	/**
	 * 根据type类型，跳转至不同页面
	 * 
	 * @Title onItemSelectAction
	 * @Description
	 * @param serviceId
	 *            服务ID
	 * @param shopId
	 *            商铺ID
	 * @param memberId
	 *            会员ID
	 * @param type
	 *            类型 （ 0 ： 优惠券，1 ： 会员卡，2 ： 活动， 3 ： 新品，4 ： 吆喝啦）
	 */
	private void onItemSelectAction(String callId, String serviceId,
			String shopId, String memberId, String type) {
		Intent intent = new Intent();
		
		
		//商家
		if("0".equals(type)) {
			intent.setClass(getActivity(),
					DetailsBusinessInfoActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopId);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberId);
			mBaseActivity.startActivity(intent);
		} else {
			if (!Utils.isStringEmpty(type)) {
				intent.setClass(mBaseActivity, YaoHeLaDetailsActivity.class);
				intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, serviceId);
				intent.putExtra(IntentKeyNames.KEY_CALL_ID, callId);
				intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopId);
				intent.putExtra(IntentKeyNames.KEY_CALL_TYPE, type);
				intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberId);
				mBaseActivity.baseStartActivity(intent);
			}
		}
		
		
		
	}

	/**
	 * 关注，收藏，点评相关监听事件
	 * 
	 * @Title initControlerListenner
	 */
	private void initControlerListenner() {

		if (mAdapter != null) {
			mAdapter.setOnHomeItemClickListener(new OnButtonClickListener() {

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopID, String memberID,
						String type) {
					CCLog.e("点击位置信息：", position + " ");
					// 点击列表项，跳转对应页面
					onItemSelectAction(callId, serviceId, shopID, memberID,
							type);

				}

				@Override
				public void onGuanZhuClick(TextView tvGuanZhu, String callID) {
				}

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});
			// 点击查看商家信息
			mAdapter.setOnBusinessShopListener(new OnButtonClickListener() {
				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							DetailsBusinessInfoActivity.class);
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopId);
					intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberId);
					mBaseActivity.startActivity(intent);
				}

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

			});

			// 首页关注按钮点击监听事件处理
			mAdapter.setOnGuanZhuClickListener(new OnButtonClickListener() {
				@Override
				public void onGuanZhuClick(final TextView tvGuanZhu,
						String shopID) {
					if (!isNetworkConnected()) { // 网络检查
						UIHelper.ToastMessage(
								getActivity(),
								getActivity().getResources().getString(
										R.string.network_disabled));
						return;
					}
					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {
						if (shopID != null) {
							CCLog.i("点击关注 ，对应的shopID ：", " " + shopID);
							CCLog.i("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
						}

						if (Utils.strFromView(tvGuanZhu).equals(
								GlobalConstant.INVALID_VALUE)) {
							ApiAccess.showProgressDialog(getActivity(),
									"卖力关注中...");
							// 关注
							shopFollow(shopID);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									ApiAccess.dismissProgressDialog();
									if (!mIsAllowFollow) {
										if (Utils.strFromView(tvGuanZhu).equals(GlobalConstant.INVALID_VALUE)) {
											tvGuanZhu.setText(GlobalConstant.VALID_VALUE);
											tvGuanZhu.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);

											new Thread(new Runnable() {

												@Override
												public void run() {
													currentPage = 1;
													getHomeCallList(
															mStrCityId,
															mLoginDataManager
																	.getMemberId(),true);
												}
											}).start();

										} else {
											tvGuanZhu
													.setText(GlobalConstant.INVALID_VALUE);
											tvGuanZhu
													.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
										}
									}
								}
							}, 1000);
						} else if (Utils.strFromView(tvGuanZhu).equals(
								GlobalConstant.VALID_VALUE)) {

							ApiAccess.showProgressDialog(getActivity(),
									"取消关注中...");
							// 关注
							cancelFollows(shopID);
						

						}

					}

				}

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnShouCangClickListenerClickListener(new OnButtonClickListener() {

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						final TextView tvShouCangImg,
						final TextView tvShouCang, String callID) {

					if (!isNetworkConnected()) { // 网络检查
						UIHelper.ToastMessage(
								getActivity(),
								getActivity().getResources().getString(
										R.string.network_disabled));
						return;
					}
					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {
						// ApiAccess.showProgressDialog(getActivity(),
						// "卖力收藏中...");
						// 收藏
						callCollection(callID);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								// ApiAccess.dismissProgressDialog();
								if (mIsAllow) {
									if (Utils
											.strFromView(tvShouCangImg)
											.equals(GlobalConstant.INVALID_VALUE)) {
										tvShouCangImg
												.setText(GlobalConstant.VALID_VALUE);
										tvShouCang
												.setTextColor(getResources()
														.getColor(
																R.color.common_home_title_bg));
										tvShouCangImg
												.setBackgroundResource(R.drawable.icon_home_item_shoucang_on);
										tvShouCang
												.setText(String.valueOf(Integer.valueOf(Utils
														.strFromView(tvShouCang)) + 1));
									} else {
										tvShouCangImg
												.setText(GlobalConstant.INVALID_VALUE);
										tvShouCang.setTextColor(getResources()
												.getColor(R.color.text_gray));
										tvShouCang
												.setText(String.valueOf(Integer.valueOf(Utils
														.strFromView(tvShouCang)) - 1));
										tvShouCangImg
												.setBackgroundResource(R.drawable.icon_home_item_shoucang_off);
									}
								}
							}
						}, 1000);

					}
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnZanClickListenerClickListener(new OnButtonClickListener() {

				@Override
				public void onZanButtonClick(int position,
						final TextView tvZanImg, final TextView tvZan,
						String callID) {

					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {
						// ApiAccess.showProgressDialog(getActivity(),
						// "卖力点赞中...");
						// 吆喝点赞
						callPraise(callID);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								// ApiAccess.dismissProgressDialog();
								if (mIsZanAllow) {
									if (Utils.strFromView(tvZanImg).equals(
											GlobalConstant.INVALID_VALUE)) {

										tvZanImg.setText(GlobalConstant.VALID_VALUE);
										tvZan.setTextColor(getResources()
												.getColor(
														R.color.common_home_title_bg));
										tvZanImg.setBackgroundResource(R.drawable.icon_home_item_zan_on);
										tvZan.setText(String.valueOf(Integer
												.valueOf(Utils
														.strFromView(tvZan)) + 1));

									}
								}
							}
						}, 1000);
					}

				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnPinLunClickListenerClickListener(new OnButtonClickListener() {

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, final TextView tvPinLun,
						String callID, String type) {

					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {

						// 点评
						callComment(callID, type);
					}

				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onButtonClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});
		}

		// 首页轮播图图片点击
		if (mPagerAdapter != null) {
			mPagerAdapter.setOnPagerItemClick(new OnPagerItemClickListener() {

				@Override
				public void onPagerItemClick(final int position) {
					if (mRotates != null && mRotates.size() > 0) {
						Rotate rotate = mRotates.get(position);
						Intent intent = new Intent();
						if (rotate.type.equals("5")) {
							intent.setClass(mBaseActivity,
									WebViewActivity.class);
							if (mRotates != null && mRotates.size() > 0) {
								intent.putExtra(WebViewActivity.URL_TAG,
										rotate.link_url);
							}
							intent.putExtra(WebViewActivity.TITLE_TAG,
									rotate.title);
							mBaseActivity.baseStartActivity(intent);

						} else {
							onItemSelectAction(rotate.service_id, rotate.content_id, rotate.content_id, "",
									rotate.type);
						}
					}

				}
			});
		}

	}

	/**
	 * 吆喝点评
	 */
	private void callComment(String callID, String type) {
		// Intent intent = new Intent(getActivity(), ShopCommentActivity.class);
		Intent intent = new Intent(getActivity(), YaoHeCommentActivity.class);
		intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, callID);
		intent.putExtra("YaoHeType", type);
		mBaseActivity.baseStartActivity(intent);

	}

	/**
	 * 吆喝点赞
	 */
	private void callPraise(String callID) {
		shopZanActionApi(mLoginDataManager.getMemberId(), callID,
				ContantsValues.CALL_PRAISE_URL, "点赞成功。");
	}

	/**
	 * 吆喝收藏
	 */
	private void callCollection(String callID) {
		//String url = ContantsValues.CANCEL_FOLLOW_URL +"&member_id="+mLoginDataManager.getMemberId()+"&id="+callID;
		String url = ContantsValues.CALL_FOLLOW_URL +"&member_id="+mLoginDataManager.getMemberId()+"&id="+callID;
		shopActionApi(mLoginDataManager.getMemberId(), callID, url, "收藏成功。");
	}

	// 是否可以关注
	private boolean mIsAllowFollow = false;

	/**
	 * 会员加店铺关注
	 */
	private void shopFollow(String shopID) {
		mIsAllowFollow = false;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", shopID);
		CCLog.i("会员加店铺关注参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + shopID);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SHOP_FOLLOW_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (responseInfo.result != null) {
										CCLog.i("关注状态信息：", responseInfo.result
												+ " ");
									}
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												mIsAllowFollow = true;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												mIsAllowFollow = false;
											}
										}
									}
								} catch (Exception e) {
									mIsAllowFollow = true;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	// 是否可以关注
	private boolean mIsAllow = true;

	/**
	 * 吆喝收藏、吆喝点赞 共通API调用
	 * 
	 * @Title shopActionApi
	 * @Description 吆喝收藏、吆喝点赞 共通API调用
	 * @param memberID
	 *            会员ID
	 * @param callID
	 *            吆喝ID
	 * @param url
	 *            请求url地址
	 * @param message
	 *            自定义成功后的提示信息
	 */
	protected boolean shopActionApi(String memberID, String callID, String url,
			final String message) {
		mIsAllow = true;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		CCLog.i("吆喝收藏点赞参数：", "member_id=" + memberID + " call_id=" + callID);
		CCLog.i("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
		CCLog.i("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG        URL=", url);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝收藏点赞状态：", responseInfo.result
											+ " ");
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {

										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												mIsAllow = false;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);

											} else {
												mIsAllow = true;
												if (!Utils
														.isStringEmpty(message)) {
													UIHelper.ToastMessage(
															mBaseActivity,
															message);
												}
											}
										}
									}
								} catch (Exception e) {
									mIsAllow = false;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mIsAllow = false;
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
		return mIsAllow;
	}

	// 是否可以点赞
	private boolean mIsZanAllow = true;

	protected boolean shopZanActionApi(String memberID, String callID,
			String url, final String message) {
		mIsZanAllow = true;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		CCLog.i("吆喝点赞参数：", "member_id=" + memberID + " call_id=" + callID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝点赞状态：", responseInfo.result
											+ " ");
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {

										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												mIsZanAllow = false;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);

											} else {
												mIsZanAllow = true;
												if (!Utils
														.isStringEmpty(message)) {
													UIHelper.ToastMessage(
															mBaseActivity,
															message);
												}
											}
										}
									}
								} catch (Exception e) {
									mIsZanAllow = false;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mIsZanAllow = false;
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
		return mIsZanAllow;
	}


	public void refresh() {
		// 获取首页吆喝内容列表
		getHomeCallList(mStrCityId, mLoginDataManager.getMemberId(),true);
		getHomeRotateListInfo(mStrCityId);
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				if (mPage * NUM > mTotal) {
//					UIHelper.ToastMessage(getActivity(), "数据已全部加载，没有更多了。");
//				}
//				//pullScrollView.setheaderViewReset();
//			}
//
//		}, 100);
	}


	public void loadMore() {
		if (currentPage>mTotalPage) {
			mLvPullToRefreshView.forbiddenLoadMore();
			UIHelper.ToastMessage(getActivity(), "数据已全部加载，没有更多了。");
		} else {
			getHomeCallList(mStrCityId, mLoginDataManager.getMemberId(),false);
		}

	}
	
	private void onLoad() {
		mLvPullToRefreshView.stopRefresh();
		mLvPullToRefreshView.stopLoadMore();
		mLvPullToRefreshView.setRefreshTime("");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		currentPage = 1;
	}

	/**
	 * 取消关注
	 */
	private void cancelFollows(String shopID) {
		String url = ContantsValues.CANCEL_FOLLOWS + "&member_id=" + mLoginDataManager.getMemberId() + "&id=" + shopID;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", shopID);
		CCLog.i("取消关注参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + shopID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						ApiAccess.dismissProgressDialog();
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (responseInfo.result != null) {
										CCLog.i("取消关注状态信息：",
												responseInfo.result + " ");
									}
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												UIHelper.ToastMessage(
														mBaseActivity, "取消关注成功");
												new Thread(new Runnable() {

													@Override
													public void run() {
														currentPage = 1;
														getHomeCallList(
																mStrCityId,
																mLoginDataManager
																		.getMemberId(),true);
													}
												}).start();
											}
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ApiAccess.dismissProgressDialog();
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});

	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if(hidden) {
			CCLog.d(tag, "hidden");
		} else {
			CCLog.d(tag, "show");
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onPause() {
		CCLog.d(tag, "onPause");
		super.onPause();
	}
}
