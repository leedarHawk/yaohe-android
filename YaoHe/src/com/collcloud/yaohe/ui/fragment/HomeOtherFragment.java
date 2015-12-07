package com.collcloud.yaohe.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.frame.xlistview.XListView;
import com.collcloud.frame.xlistview.XListView.IXListViewListener;
import com.collcloud.frame.xlistview.XListView.OnSlidingDirectionListen;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeCommentActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.ShopCommentActivity;
import com.collcloud.yaohe.activity.fujin.FuJinActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.HomeTypeCallInfo;
import com.collcloud.yaohe.api.info.HomeCallInfo.CallInfo;
import com.collcloud.yaohe.api.info.HomeFollowShopInfo.FollowShop;
import com.collcloud.yaohe.api.info.HomeTypeCallInfo.TypeCall;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseFragment;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.adapter.HomeTypeCallAdapter;
import com.collcloud.yaohe.ui.adapter.HomeTypeCallAdapter.OnOtherListener;
import com.collcloud.yaohe.ui.fragment.HomeTuijianFragment.StatusBroadCastReceiver;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 获取某个分类的吆喝列表
 * 
 * @ClassName HomeOtherFragment
 * @Description 选定城市（eg:天津）下的具体分类内容
 * @author CollCloud_小米
 */
@SuppressLint("ValidFragment")
public class HomeOtherFragment extends BaseFragment {

	/**
	 * 主页面显示内容的List
	 */
	//private SingleLayoutListView mLvPullToRefreshView;
	private XListView 			 mLvPullToRefreshView;
	/**
	 * 主页list列表适配器
	 */
	private HomeTypeCallAdapter mAdapter = null;

	/**
	 * 从服务器获取的吆喝分类信息集
	 */
	private HomeTypeCallInfo mHomeTypeCallInfo;
	/**
	 * 吆喝分类列表内容
	 */
	private List<TypeCall> mTypeCalls = new ArrayList<TypeCall>();

	// ********** 异常提示 *********** //
	/**
	 * 列表内容为空，显示提示内容
	 */
	private LinearLayout mLlEmpty = null;
	/**
	 * 提示文字
	 */
	private TextView mTvTips = null;
	/**
	 * fragment切换时，不同标签对应的分类ID
	 */
	private String mStrTypeID = null;
	/**
	 * 城市id
	 */
	private String mStrCityID;
	/**
	 * 网络异常提示文字
	 */
	private TextView mTvNetError = null;
	
	private static String tag = HomeOtherFragment.class.getSimpleName();
	
	//总页数
	private int mTotalPage = 0;
	//当前页
	private int currentPage = 1;
		

	/**
	 * 构造方法
	 */
	public HomeOtherFragment() {
		super();
	}

	public HomeOtherFragment(String city_id) {
		super();
		this.mStrCityID = city_id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home_other, container,
				false);
		CCLog.i("分类内容Fragment", "onCreateView");
		mLvPullToRefreshView = (XListView) v
				.findViewById(R.id.lv_home_other_list);
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

		mLlEmpty = (LinearLayout) v.findViewById(R.id.ll_home_other_empty);
		// 获取首页某个分类下的吆喝列表
		mHandler.sendEmptyMessageDelayed(0, 200);
		//getTypeCallList(mStrCityID, mStrTypeID);
		registBroadCast();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		CCLog.i("分类内容Fragment", "onResume");
		// 接收从城市定位页面传递的城市ID
		/**
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			mStrCityID = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			CCLog.i(tag, "onresume lbs.......mStrCityID:"+mStrCityID);
			CCLog.i("分类内容Fragment", "onResume \n获取的城市ID ：" + mStrCityID);
			mHandler.sendEmptyMessageDelayed(0, 200);
			//getTypeCallList(mStrCityID, mStrTypeID);

		}*/
	}
	
	@Override
	public void doResetData() {
		super.doResetData();
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			mStrCityID = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			CCLog.i(tag, "onresume lbs.......mStrCityID:"+mStrCityID);
			CCLog.i("分类内容Fragment", "onResume \n获取的城市ID ：" + mStrCityID);
			mHandler.sendEmptyMessageDelayed(0, 200);
			//getTypeCallList(mStrCityID, mStrTypeID);

		}
	}
	
	/**
	 * 刷新数据
	 */
	public void refresh() {
		currentPage = 1;
		getTypeCallList(mStrCityID, mStrTypeID,true);
	}
	
	public void loadMore() {
		currentPage = currentPage + 1;
		if (currentPage>mTotalPage) {
			mLvPullToRefreshView.forbiddenLoadMore();
			UIHelper.ToastMessage(getActivity(), "数据已全部加载，没有更多了。");
		} else {
			getTypeCallList(mStrCityID, mStrTypeID,false);
		}

	}
	
	private void onLoad() {
		mLvPullToRefreshView.stopRefresh();
		mLvPullToRefreshView.stopLoadMore();
		mLvPullToRefreshView.setRefreshTime("");
	}
	
	//临时保存城市id 为了与当前的进行比较 。如果一样 就不进行数据加载，如果不一致则进行加载
	private String tmpStrCityId;
	private void getData() {
		if(tmpStrCityId != null) {
			if(tmpStrCityId.equals(mStrCityID)) {
				CCLog.d(tag, "1111tmpStrCityId is the same to the old id so not load data.......");
			} else {
				tmpStrCityId = mStrCityID;
				CCLog.d(tag, "111122tmpStrCityId is not null but different old id so load data.......");
				getTypeCallList(mStrCityID, mStrTypeID,true);
			}
		} else {
			tmpStrCityId = mStrCityID;
			CCLog.d(tag, "tmpStrCityId is null load data.......");
			getTypeCallList(mStrCityID, mStrTypeID,true);
		}
	}

	/**
	 * 更新数据内容
	 * 
	 * @Title refreshData
	 * @Description 首页标签切换时，更新显示内容
	 * @param typeID
	 *            分类ID
	 */
	public void refreshData(String cityID, String typeID) {
		// TODO 服务器接口地址，数据处理
		if (!Utils.isStringEmpty(cityID)) {
			mStrCityID = cityID;
			CCLog.i("刷新分类Fragment-城市ID:  " + cityID);
		}
		if (!Utils.isStringEmpty(typeID)) {
			mStrTypeID = typeID;
			CCLog.i("刷新分类Fragment-分类ID: " + typeID);
		}
		
		//getTypeCallList(mStrCityID, mStrTypeID);
		mHandler.sendEmptyMessageDelayed(0, 200);
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// 获取首页某个分类下的吆喝列表
				//getData();
				CCLog.d(tag, "getTypeCallList>>"+"mStrCityID:"+mStrCityID+"--mStrTypeID:"+mStrTypeID); 
				currentPage = 1;
				getTypeCallList(mStrCityID, mStrTypeID,true);
				break;

			case 1:
				UIHelper.ToastMessage(mBaseActivity, "数据已全部加载，没有更多了。");
				//mLvPullToRefreshView.onRefreshComplete(); // 下拉刷新完成

				break;
			default:
				break;
			}
		};
	};

	/**
	 * 获取首页某个分类下的吆喝列表
	 * 
	 * @Title getFollowShopList
	 */
	private void getTypeCallList(String cityid, String typeID,final boolean refreshData) {

		HttpUtils http = new HttpUtils();
		String url = ContantsValues.HOME_TYPE_CALL_LIST_URL + "&city_id="
				+ cityid + "&one_id=" + typeID+"&page="+currentPage+"&member_id="+mLoginDataManager.getMemberId();
		CCLog.d(tag, "getTypeCallList url:---"+url);
		CCLog.d(tag, "refreshData:"+refreshData);
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							if (responseInfo.result != null) {
								CCLog.e("首页分类id " + mStrTypeID + " 列表内容：",
										responseInfo.result + " ");
							}
							
							if(refreshData) {
								currentPage = 1;
								mTypeCalls.clear();
							}
							
							
							try {
								mTotalPage = jsonObject.optInt("pageNumber");
							} catch(Exception e) {
								e.printStackTrace();
							}
							
							if (jsonObject.has("data")) {
								mHomeTypeCallInfo = GsonUtils.json2Bean(
										responseInfo.result,
										HomeTypeCallInfo.class);
								if (mHomeTypeCallInfo != null) {
									if (mHomeTypeCallInfo.data != null
											&& mHomeTypeCallInfo.data.size() > 0) {

										if (mHomeTypeCallInfo.data.size() == 1) {
											if (Utils
													.isStringEmpty(mHomeTypeCallInfo.data
															.get(0).id)
													&& Utils.isStringEmpty(mHomeTypeCallInfo.data
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
										ArrayList<TypeCall> mCallInfosTmp = new ArrayList<TypeCall>();
										int dataSize = mHomeTypeCallInfo.data.size();
										for (int j = 0; j < dataSize; j++) {
											TypeCall typeCallInfo = new TypeCall();
											if (mHomeTypeCallInfo.data.get(j).id != null) {
												typeCallInfo.id = mHomeTypeCallInfo.data
														.get(j).id;
											}
											if (mHomeTypeCallInfo.data.get(j).service_id != null) {
												typeCallInfo.service_id = mHomeTypeCallInfo.data
														.get(j).service_id;
											}
											if (mHomeTypeCallInfo.data.get(j).shop_id != null) {
												typeCallInfo.shop_id = mHomeTypeCallInfo.data
														.get(j).shop_id;
											}
											if (mHomeTypeCallInfo.data.get(j).member_id != null) {
												typeCallInfo.member_id = mHomeTypeCallInfo.data
														.get(j).member_id;
											}
											if (mHomeTypeCallInfo.data.get(j).shop_id != null) {
												typeCallInfo.shop_id = mHomeTypeCallInfo.data
														.get(j).shop_id;
											}
											if (mHomeTypeCallInfo.data.get(j).shop_name != null) {
												typeCallInfo.shop_name = mHomeTypeCallInfo.data
														.get(j).shop_name;
											}
											if (mHomeTypeCallInfo.data.get(j).shop_star != null) {
												typeCallInfo.shop_star = mHomeTypeCallInfo.data
														.get(j).shop_star;
											}
											if (mHomeTypeCallInfo.data.get(j).shop_fans_num != null) {
												typeCallInfo.shop_fans_num = mHomeTypeCallInfo.data
														.get(j).shop_fans_num;
											}
											if (mHomeTypeCallInfo.data.get(j).type != null) {
												typeCallInfo.type = mHomeTypeCallInfo.data
														.get(j).type;
											}
											if (mHomeTypeCallInfo.data.get(j).title != null) {
												typeCallInfo.title = mHomeTypeCallInfo.data
														.get(j).title;
											}
											if (mHomeTypeCallInfo.data.get(j).content != null) {
												typeCallInfo.content = mHomeTypeCallInfo.data
														.get(j).content;
											}
											if (!Utils
													.isStringEmpty(mHomeTypeCallInfo.data
															.get(j).img1)) {
												typeCallInfo.img1 = URLs.IMG_PRE
														+ mHomeTypeCallInfo.data
																.get(j).img1;
											}
											if (mHomeTypeCallInfo.data.get(j).addtime != null) {
												typeCallInfo.addtime = mHomeTypeCallInfo.data
														.get(j).addtime;
											}
											if (mHomeTypeCallInfo.data.get(j).collection_num != null) {
												typeCallInfo.collection_num = mHomeTypeCallInfo.data
														.get(j).collection_num;
											}
											if (mHomeTypeCallInfo.data.get(j).zan_num != null) {
												typeCallInfo.zan_num = mHomeTypeCallInfo.data
														.get(j).zan_num;
											}
											if (mHomeTypeCallInfo.data.get(j).comment_num != null) {
												typeCallInfo.comment_num = mHomeTypeCallInfo.data
														.get(j).comment_num;
											}
											if (mHomeTypeCallInfo.data.get(j).fans_num != null) {
												typeCallInfo.fans_num = mHomeTypeCallInfo.data
														.get(j).fans_num;
											}
											if (mHomeTypeCallInfo.data.get(j).guanzhu != null) {
												typeCallInfo.guanzhu = mHomeTypeCallInfo.data
														.get(j).guanzhu;
											}
											
											
											
									    ////////////////引用
										if (mHomeTypeCallInfo.data.get(j).s_content != null) {
											typeCallInfo.s_content = mHomeTypeCallInfo.data
													.get(j).s_content;
										}
										if (!Utils
												.isStringEmpty(mHomeTypeCallInfo.data
														.get(j).s_img)) {
											typeCallInfo.s_img = URLs.IMG_PRE
													+ mHomeTypeCallInfo.data
															.get(j).s_img;
										}
										if (!Utils
												.isStringEmpty(mHomeTypeCallInfo.data
														.get(j).img)) {
											typeCallInfo.img = URLs.IMG_PRE
													+ mHomeTypeCallInfo.data
															.get(j).img;
										}
										
										typeCallInfo.c_id = mHomeTypeCallInfo.data
												.get(j).c_id;
																	
										typeCallInfo.is_yinyong = mHomeTypeCallInfo.data
												.get(j).is_yinyong;
											mCallInfosTmp.add(typeCallInfo);
										}
										
										//刷新数据
										if(refreshData) {
											//mCallInfos = mCallInfosTmp;
											mTypeCalls.addAll(mCallInfosTmp);
										} else {//加载更多数据
											mTypeCalls.addAll(mCallInfosTmp);
										}
										

										if (mTypeCalls != null
												&& mTypeCalls.size() > 0) {
											mLvPullToRefreshView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
											// 设定首页吆喝内容
											refreshTypeCall(mTypeCalls);
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
							onLoad();
						} catch (JSONException e) {
							mLvPullToRefreshView.setVisibility(View.GONE);
							mLlEmpty.setVisibility(View.VISIBLE);
							onLoad();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mLvPullToRefreshView.setVisibility(View.GONE);
						mLlEmpty.setVisibility(View.VISIBLE);
						onLoad();
					}
				});

	}



	/**
	 * 加载数据啦~
	 */
//	public void loadData(final int type) {
//		new Thread() {
//			@Override
//			public void run() {
//				switch (type) {
//				case 0:
//					Message _Msg = mHandler.obtainMessage(0);
//					mHandler.sendMessage(_Msg);
//					CCLog.i("分类吆喝信息  —— 可以加载数据了。。");
//					break;
//				}
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (type == 0) { // 下拉刷新
//					// Collections.reverse(mList); //逆序
//					Message _Msg = mHandler.obtainMessage(1);
//					mHandler.sendMessage(_Msg);
//				}
//			}
//		}.start();
//	}

	/**
	 * 设定首页某个分类下的显示内容
	 */
	private void setTypeCallList() {

		mAdapter = new HomeTypeCallAdapter(mBaseActivity, mTypeCalls);
		mLvPullToRefreshView.setAdapter(mAdapter);
		initControlerListenner();
	}

	/**
	 * 刷新数据信息
	 * 
	 * @Title refreshTypeCall
	 */
	private void refreshTypeCall(List<TypeCall> typeCalls) {
		this.mTypeCalls = typeCalls;
		if (mAdapter != null) {
			mAdapter.refresh(mTypeCalls);
		} else {
			mAdapter = new HomeTypeCallAdapter(mBaseActivity, mTypeCalls);
			mLvPullToRefreshView.setAdapter(mAdapter);
			initControlerListenner();
		}
		
	}

	/**
	 * 关注，收藏，点评相关监听事件
	 * 
	 * @Title initControlerListenner
	 */
	private void initControlerListenner() {

		if (mAdapter != null) {
			mAdapter.setOnHomeItemClickListener(new OnOtherListener() {

				@Override
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopID, String memberID,
						String type) {
					CCLog.e("点击关注位置信息：", position + " ");
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
						TextView tvPinLunImg, TextView tvPinLun, String callID,String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});
			// 点击查看商家信息
			mAdapter.setOnBusinessShopListener(new OnOtherListener() {
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
						TextView tvPinLunImg, TextView tvPinLun, String callID,String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onCallItemClick(int position, String callId, String serviceId,
						String shopId, String memberID, String type) {
				}

			});

			// 首页关注按钮点击监听事件处理
			mAdapter.setOnGuanZhuClickListener(new OnOtherListener() {
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
							cancelFollows(shopID,tvGuanZhu);
						

						}
						
//						ApiAccess.showProgressDialog(getActivity(), "卖力关注中...");
//						// 关注
//						shopFollow(shopID);
//						new Handler().postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								ApiAccess.dismissProgressDialog();
//								if (!mIsAllowFollow) {
//									if (Utils.strFromView(tvGuanZhu).equals(
//											GlobalConstant.INVALID_VALUE)) {
//										tvGuanZhu
//												.setText(GlobalConstant.VALID_VALUE);
//										tvGuanZhu
//												.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
//
//									} else {
//										tvGuanZhu
//												.setText(GlobalConstant.INVALID_VALUE);
//										tvGuanZhu
//												.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
//									}
//								}
//							}
//						}, 1000);

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
						TextView tvPinLunImg, TextView tvPinLun, String callID,String type) {
				}

				@Override
				public void onCallItemClick(int position,  String callId,String serviceId,
						String shopId, String memberID, String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnShouCangClickListenerClickListener(new OnOtherListener() {

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {
				}

				@Override
				public void onShouCangButtonClick(int position,
						final TextView tvShouCangImg,
						final TextView tvShouCang, String callID) {
					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {

						ApiAccess.showProgressDialog(getActivity(), "卖力收藏中...");
						// 收藏
						callCollection(callID);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								ApiAccess.dismissProgressDialog();
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
//										tvShouCang
//												.setText(String.valueOf(Integer.valueOf(Utils
//														.strFromView(tvShouCang)) + 1));
									} else {
										tvShouCangImg
												.setText(GlobalConstant.INVALID_VALUE);
										tvShouCang.setTextColor(getResources()
												.getColor(R.color.text_gray));
//										tvShouCang
//												.setText(String.valueOf(Integer.valueOf(Utils
//														.strFromView(tvShouCang)) - 1));
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
						TextView tvPinLunImg, TextView tvPinLun, String callID,String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onCallItemClick(int position,  String callId,String serviceId,
						String shopId, String memberID, String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnZanClickListenerClickListener(new OnOtherListener() {

				@Override
				public void onZanButtonClick(int position, TextView tvZanImg,
						TextView tvZan, String callID) {

					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {
						callPraise_zan(callID);
						/**
						if (!Utils.isStringEmpty(Utils.strFromView(tvZanImg))) {
							if (Utils.strFromView(tvZanImg).equals(
									GlobalConstant.INVALID_VALUE)) {
								tvZanImg.setText(GlobalConstant.VALID_VALUE);
								tvZan.setTextColor(getResources().getColor(
										R.color.common_home_title_bg));
								tvZanImg.setBackgroundResource(R.drawable.icon_home_item_zan_on);
								tvZan.setText(String.valueOf(Integer
										.valueOf(Utils.strFromView(tvZan)) + 1));
								// 吆喝点赞
								baseCallPraise(callID);
							} else {
//								tvZanImg.setText(GlobalConstant.INVALID_VALUE);
//								tvZan.setTextColor(getResources().getColor(
//										R.color.text_gray));
//								tvZan.setText(String.valueOf(Integer
//										.valueOf(Utils.strFromView(tvZan)) - 1));
//								tvZanImg.setBackgroundResource(R.drawable.icon_home_item_zan_off);
							}
						} else {
							tvZanImg.setText(GlobalConstant.INVALID_VALUE);
							tvZan.setTextColor(getResources().getColor(
									R.color.text_gray));
							tvZanImg.setBackgroundResource(R.drawable.icon_home_item_zan_off);
						} */
						
						
						
					}

				}

				@Override
				public void onShouCangButtonClick(int position,
						TextView tvShouCangImg, TextView tvShouCang,
						String callID) {
				}

				@Override
				public void onPinLunButtonClick(int position,
						TextView tvPinLunImg, TextView tvPinLun, String callID,String type) {
				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onCallItemClick(int position, String callId, String serviceId,
						String shopId, String memberID, String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnPinLunClickListenerClickListener(new OnOtherListener() {

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
						String callID,String type) {

					if (!mLoginDataManager.getLoginState().equals("1")) {
						UIHelper.ToastMessage(getActivity(), "您还没登录，请先登录。");
						Intent intent = new Intent(mBaseActivity,
								LoginActivity.class);
						mBaseActivity.baseStartActivity(intent);
					} else {
						/**
						if (Utils.strFromView(tvPinLunImg).equals(
								GlobalConstant.INVALID_VALUE)) {
							// 点评
							baseCallComment(callID);
							tvPinLunImg.setText(GlobalConstant.VALID_VALUE);
							tvPinLun.setTextColor(getResources().getColor(
									R.color.common_home_title_bg));
							tvPinLunImg
									.setBackgroundResource(R.drawable.icon_home_item_liuyan_on);
							// tvPinLun.setText(String.valueOf(Integer
							// .valueOf(Utils.strFromView(tvPinLun)) + 1));
						} else {
							tvPinLunImg.setText(GlobalConstant.INVALID_VALUE);
							tvPinLun.setTextColor(getResources().getColor(
									R.color.text_gray));
							tvPinLunImg
									.setBackgroundResource(R.drawable.icon_home_item_liuyan_off);
							// tvPinLun.setText(String.valueOf(Integer
							// .valueOf(Utils.strFromView(tvPinLun)) - 1));
						} */
						callComment(callID,type);
					}

				}

				@Override
				public void onGuanZhuClick(TextView tvGuanzhu, String callID) {
				}

				@Override
				public void onCallItemClick(int position,  String callId,String serviceId,
						String shopId, String memberID, String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

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
	private void onItemSelectAction(String callId,String serviceId, String shopId,
			String memberId, String type) {
		Intent intent = new Intent();
		if (!Utils.isStringEmpty(type)) {
			intent.setClass(mBaseActivity, YaoHeLaDetailsActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID,
					serviceId);
			intent.putExtra(IntentKeyNames.KEY_CALL_ID, callId);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopId);
			intent.putExtra(IntentKeyNames.KEY_CALL_TYPE, type);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberId);
			mBaseActivity.baseStartActivity(intent);
			}
	
	}

	@Override
	protected void resetLayout(View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_home_fragment_other_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		CCLog.i("分类内容Fragment", "resetLayout");
		// 列表内容
		mLvPullToRefreshView = (XListView) view
				.findViewById(R.id.lv_home_other_list);

		// 空内容提示
		mLlEmpty = (LinearLayout) view.findViewById(R.id.ll_home_other_empty);

		// 提示文字
		mTvTips = (TextView) view
				.findViewById(R.id.tv_fragment_extra_text_link);
		mTvTips.setText(Html.fromHtml("去"
				+ "<font size=\"14\" color=\"#23a3fc\">附近商家</font>" + "看看吧"));

		// 提示文字监听事件
		mTvTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent fujinIntent = new Intent();
				fujinIntent.setClass(getActivity(), FuJinActivity.class);
				mBaseActivity.baseStartActivity(fujinIntent);
			}
		});

		// 网络错误
		mTvNetError = (TextView) view
				.findViewById(R.id.tv_home_other_net_error);

		if (!AppApplacation.getInstance().isNetworkConnected()) {
			mTvNetError.setVisibility(View.VISIBLE);
			mLvPullToRefreshView.setVisibility(View.GONE);
			return;
		} else {
			mTvNetError.setVisibility(View.GONE);
			mLvPullToRefreshView.setVisibility(View.VISIBLE);
		}

		// 设定首页某个分类下的显示内容
		setTypeCallList();
	}

//	/**
//	 * 吆喝点评
//	 */
//	private void callComment(String callID) {
//		Intent intent = new Intent(getActivity(), ShopCommentActivity.class);
//		intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, callID);
//		mBaseActivity.baseStartActivity(intent);
//	}
//
//	/**
//	 * 吆喝点赞
//	 */
//	private void callPraise(String callID) {
//		shopActionApi(mLoginDataManager.getMemberId(), callID,
//				ContantsValues.CALL_PRAISE_URL, "点赞成功。");
//	}

	/**
	 * 吆喝收藏
	 */
	private void callCollection(String callID) {
		String url = ContantsValues.CALL_FOLLOW_URL +"&member_id="+mLoginDataManager.getMemberId()+"&id="+callID;
		shopActionApi(mLoginDataManager.getMemberId(), callID,
				url, "收藏成功。");
	}

	// 是否可以关注
	private boolean mIsAllowFollow = false;

	/**
	 * 会员加店铺关注
	 */
	private void shopFollow(final String shopID) {
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
												setRefreshHomeGuanzhuFragmentStatus(true);
											} else {
												mIsAllowFollow = false;
											}
											sendBroadCast(shopID, false, CommonConstant.doWhat_change_followStatus);
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
	
	/**
	 * 取消关注
	 */
	private void cancelFollows(final String shopID,final TextView tvGuanZhu) {
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
												tvGuanZhu.setText(GlobalConstant.INVALID_VALUE);
												tvGuanZhu.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
												//setRefreshHomeGuanzhuFragmentStatus(true);
												sendBroadCast(shopID, true, CommonConstant.doWhat_change_followStatus);
											}
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
									tvGuanZhu.setText(GlobalConstant.VALID_VALUE);
									tvGuanZhu.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
									
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
	
	/**
	 * 吆喝点赞
	 */
	private void callPraise_zan(String callID) {
		shopZanActionApi(mLoginDataManager.getMemberId(), callID,
				ContantsValues.CALL_PRAISE_URL, "点赞成功。");
	}
	
	// 是否可以点赞
		private boolean mIsZanAllow = true;

		protected boolean shopZanActionApi(String memberID, final String callID,
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
													sendBroadCastForZan(callID);
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
	protected boolean shopActionApi(String memberID, final String callID, String url,
			final String message) {
		mIsAllow = true;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		CCLog.i("吆喝收藏点赞参数：", "member_id=" + memberID + " call_id=" + callID);

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
												sendBroadCastForShouCang(callID,true);
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
	
	/**
	 * 
	 * @return
	 * @description 检测是否刷新
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月5日 上午1:02:57 
	 * @update 2015年10月5日 上午1:02:57
	 */
	public boolean isNeedRefreshData() {
		boolean istheSameCity = true;
		if(tmpStrCityId != null) {
			if(tmpStrCityId.equals(mStrCityID)) {
				CCLog.d(tag, "tmpStrCityId is the same to the old id so not load data.......");
			} else {
				tmpStrCityId = mStrCityID;
				CCLog.d(tag, "tmpStrCityId is not null but different old id so load data.......");
				//getTypeCallList(mStrCityID, mStrTypeID);
				istheSameCity = false;
			}
		} else {
			istheSameCity = false;
			tmpStrCityId = mStrCityID;
			CCLog.d(tag, "tmpStrCityId is null load data.......");
			//getTypeCallList(mStrCityID, mStrTypeID);
		}
		
		if(mTypeCalls != null && mTypeCalls.size()>0 && istheSameCity) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 
	 * @author LEE
	 * 关注状态 收藏状态 关注广播
	 *private List<TypeCall> mTypeCalls = new ArrayList<TypeCall>();
	 */
	 class StatusBroadCastReceiver extends BroadcastReceiver {   
	    @Override  
	    public void onReceive(Context context, Intent intent) {
	    	try {
	    		CCLog.d(tag, "broadcast..........");
		    	int doWhat = intent.getIntExtra("doWhat",-1);
		    	String shopId = intent.getStringExtra("shopId");
		    	boolean isCanceFollow  =intent.getBooleanExtra("isCanceFollow",false);
		    	switch(doWhat) {
			    	case CommonConstant.doWhat_change_followStatus:
			    		for(TypeCall typeCall : mTypeCalls) {
			    			if(typeCall.shop_id.equals(shopId)) {
			    				if(isCanceFollow) {
			    					int fansCount = 0;
			    					try {
			    						fansCount = Integer.parseInt(typeCall.shop_fans_num);
			    					} catch(Exception e) {
			    						e.printStackTrace();
			    						fansCount = 0;
			    					}
			    					fansCount = fansCount-1;
			    					if(fansCount<0) {
			    						fansCount =0;
			    					}
			    					typeCall.shop_fans_num = String.valueOf(fansCount);
			    					
			    					typeCall.guanzhu = GlobalConstant.INVALID_VALUE;
			    					
			    					
			    				} else {
			    					CCLog.d(tag, "add guanzhu.....");
			    					int fansCount = 0;
			    					try {
			    						fansCount = Integer.parseInt(typeCall.shop_fans_num);
			    					} catch(Exception e) {
			    						e.printStackTrace();
			    						fansCount = 0;
			    					}
			    					fansCount = fansCount+1;
			    					typeCall.shop_fans_num = String.valueOf(fansCount);
			    					
			    					typeCall.guanzhu = GlobalConstant.VALID_VALUE;
			    				}
			    			}
			    		}
			    		CCLog.d(tag, "change guanzhu status... notify adapter");
			    		refreshTypeCall(mTypeCalls);
			    		break;
			    		//评论个数
			    	case CommonConstant.doWhat_change_comment_count:
			    		boolean isAddCommentCount=intent.getBooleanExtra("isAddCommentCount",false);
			    		String callId = intent.getStringExtra("callId");
			    		for(TypeCall callInfo : mTypeCalls) {
			    			if(callInfo.id.equals(callId)) {
			    				if(!isAddCommentCount) {//删除评论
			    					
			    					
			    					int commentCount = 0;
			    					try {
			    						commentCount = Integer.parseInt(callInfo.comment_num);
			    					} catch(Exception e) {
			    						e.printStackTrace();
			    						commentCount = 0;
			    					}
			    					commentCount = commentCount-1;
			    					if(commentCount<0) {
			    						commentCount =0;
			    					}
			    					callInfo.comment_num = String.valueOf(commentCount);
			    				} else {//添加评论
			    					int commentCount = 0;
			    					try {
			    						commentCount = Integer.parseInt(callInfo.comment_num);
			    					} catch(Exception e) {
			    						e.printStackTrace();
			    						commentCount = 0;
			    					}
			    					commentCount = commentCount+1;
			    					callInfo.comment_num = String.valueOf(commentCount);
			    				}
			    			}
			    		}
			    		CCLog.d(tag, "change pinglun count ... notify adapter");
			    		refreshTypeCall(mTypeCalls);
			    		break;
			    		//点赞个数改变
			    	case CommonConstant.doWhat_change_zan_count:
			    		String callId_zan = intent.getStringExtra("callId");
			    		for(TypeCall callInfo : mTypeCalls) {
			    			if(callInfo.id.equals(callId_zan)) {
			    				int zanCount = 0;
		    					try {
		    						zanCount = Integer.parseInt(callInfo.zan_num);
		    					} catch(Exception e) {
		    						e.printStackTrace();
		    						zanCount = 0;
		    					}
		    					zanCount = zanCount+1;
		    					callInfo.zan_num = String.valueOf(zanCount);
			    			}
			    		}
			    		refreshTypeCall(mTypeCalls);
			    		break;
			    		//收藏个数改变
			    	case CommonConstant.doWhat_change_shoucang_count:
			    		String callId_shoucang = intent.getStringExtra("callId");
			    		//是否为添加收藏
			    		boolean isAddShoucang = intent.getBooleanExtra("isAddShoucang",false);
			    		for(TypeCall callInfo : mTypeCalls) {
			    			if(callInfo.id.equals(callId_shoucang)) {
			    				int shoucangCount = 0;
		    					try {
		    						shoucangCount = Integer.parseInt(callInfo.collection_num);
		    					} catch(Exception e) {
		    						e.printStackTrace();
		    						shoucangCount = 0;
		    					}
		    					if(isAddShoucang) {
		    						shoucangCount = shoucangCount+1;
		    					} else {
		    						shoucangCount = shoucangCount-1;
		    					}
		    					
		    					callInfo.collection_num = String.valueOf(shoucangCount);
			    			}
			    		}
			    		CCLog.d(tag, "change shoucang status... notify adapter");
			    		refreshTypeCall(mTypeCalls);
			    		//星星个数改变
			    	case CommonConstant.doWhat_change_shop_start_count:
			    		String starCount = intent.getStringExtra("starCount");
			    		String shopId_star = intent.getStringExtra("shopId");
			    		for(TypeCall callInfo : mTypeCalls) {
			    			if(callInfo.shop_id.equals(shopId_star)) {
			    				callInfo.shop_star = starCount;
			    			}
			    		}
			    		CCLog.d(tag, "change shop star count ... notify adapter");
			    		refreshTypeCall(mTypeCalls);
			    		break;
		    	}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    }   
	       
	}
	private void registBroadCast() {
		//生成广播处理   
		StatusBroadCastReceiver  bc = new StatusBroadCastReceiver();   
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CommonConstant.STATUS_BROADCAST_ACTION);
		this.getActivity().registerReceiver(bc, intentFilter);
	}
	
	
	
}
