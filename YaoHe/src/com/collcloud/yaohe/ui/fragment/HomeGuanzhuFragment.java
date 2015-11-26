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
import com.collcloud.yaohe.activity.fujin.FuJinActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.HomeFollowShopInfo;
import com.collcloud.yaohe.api.info.HomeCallInfo.CallInfo;
import com.collcloud.yaohe.api.info.HomeFollowShopInfo.FollowShop;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseFragment;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.adapter.HomeGuanZhuAdapter;
import com.collcloud.yaohe.ui.adapter.HomeGuanZhuAdapter.OnGuanZhuListener;
import com.collcloud.yaohe.ui.fragment.HomeTuijianFragment.StatusBroadCastReceiver;
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
 * 吆喝APP 主页部分-关注商家
 * 
 * @ClassName HomeGuanzhuFragment
 * @Description 主页关注商家内容
 * @author CollCloud_小米
 */
@SuppressLint("ValidFragment")
public class HomeGuanzhuFragment extends BaseFragment {
	/**
	 * 主页面显示内容的List
	 */
	//private SingleLayoutListView mLvPullToRefreshView;
	/**
	 * 主页面显示内容的List
	 */
	private XListView mLvPullToRefreshView;
	/**
	 * 主页list列表适配器
	 */
	private HomeGuanZhuAdapter mAdapter = null;

	/**
	 * 从服务器获取的吆喝关注信息集
	 */
	private HomeFollowShopInfo mHomeFollowShopInfo;
	/**
	 * 吆喝关注列表内容
	 */
	private List<FollowShop> mFollowShops = new ArrayList<FollowShop>();

	// ************************ //
	/**
	 * 列表内容为空，显示提示内容
	 */
	private LinearLayout mLlEmpty = null;
	/**
	 * 提示文字
	 */
	private TextView mTvTips = null;
	/**
	 * 网络异常提示文字
	 */
	private TextView mTvNetError = null;
	/**
	 * 城市id
	 */
	private String mStrCityID;
	
	//总页数
	private int mTotalPage = 0;
	//当前页
	private int currentPage = 1;


	/**
	 * 构造方法
	 */
	public HomeGuanzhuFragment() {
		super();
	}

	public HomeGuanzhuFragment(String city_id) {
		this.mStrCityID = city_id;

	}
	
	private static String tag = HomeGuanzhuFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home_guanzhu, container,
				false);
		CCLog.i("关注商家Fragment", "onCreateView");

		mLvPullToRefreshView = (XListView) v
				.findViewById(R.id.lv_home_guanzhu_shop);

		mLlEmpty = (LinearLayout) v
				.findViewById(R.id.ll_home_guanzhu_shop_empty);
		ApiAccess.showProgressDialog(getActivity(), "数据加载中..",
				R.style.progress_dialog);
		// 获取首页关注吆喝内容列表
		//getFollowShopList(mStrCityID, mLoginDataManager.getMemberId());
		//getData(mStrCityID);
		doResetData();
		initControlerListenner();
		registBroadCast();
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CCLog.i("关注商家Fragment", "onResume");
		//在其他页面是否 点击了关注按钮 取消 或者 添加关注 检测是否需要刷新数据
//		if(isRefreshHomeGuanzhuFragment()) {
//			doResetData();
//		} else {
//			//do nothing....
//		}
		// 接收从城市定位页面传递的城市ID
		/**
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			mStrCityID = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			CCLog.i("关注商家Fragment", "onResume \n获取的城市ID ：" + mStrCityID);
			//getFollowShopList(mStrCityID, mLoginDataManager.getMemberId());
			getData(mStrCityID);

		} else {

			if (mLoginDataManager.getLoginState().equals("1")) {
				//loadData(1);
				getData(mStrCityID);
			} else {
				ApiAccess.dismissProgressDialog();
				if (mLvPullToRefreshView != null) {
					mLvPullToRefreshView.setVisibility(View.GONE);
				}
				if (mLlEmpty != null) {
					mLlEmpty.setVisibility(View.VISIBLE);
				}
			}
		}*/
	}
	/**
	 * 更换城市 以及 发布新的产品 重新设置数据 代替onresume
	 */
	public void doResetData() {
		// 接收从城市定位页面传递的城市ID
		if (getActivity().getIntent().getStringExtra(
				IntentKeyNames.KEY_LBS_CURRENT_CITY_ID) != null) {
			mStrCityID = getActivity().getIntent().getStringExtra(
					IntentKeyNames.KEY_LBS_CURRENT_CITY_ID);
			CCLog.i("关注商家Fragment", "onResume \n获取的城市ID ：" + mStrCityID);
			//getFollowShopList(mStrCityID, mLoginDataManager.getMemberId());
			getData(mStrCityID);

		} else {

			if (mLoginDataManager.getLoginState().equals("1")) {
				//loadData(1);
				getData(mStrCityID);
			} else {
				ApiAccess.dismissProgressDialog();
				if (mLvPullToRefreshView != null) {
					mLvPullToRefreshView.setVisibility(View.GONE);
				}
				if (mLlEmpty != null) {
					mLlEmpty.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	//临时保存城市id 为了与当前的进行比较 。如果一样 就不进行数据加载，如果不一致则进行加载
	private String tmpStrCityId;
	public void getData(String mStrCityId) {
		CCLog.i("关注页面开始加载数据 ==================================");
		currentPage = 1;
		this.mStrCityID = mStrCityId;
		//此参数 从 MainActivity来 为了表示 是否刷新数据
		boolean refreshData = this.getActivity().getIntent().getBooleanExtra("refreshData", false);
		if(tmpStrCityId != null) {
			if(tmpStrCityId.equals(mStrCityID) &&!refreshData) {
				CCLog.d(tag, "tmpStrCityId is the same to the old id so not load data.......");
				ApiAccess.dismissProgressDialog();
				if(isFromGuanzhuStatusChanged) {
					CCLog.d(tag, "tmpStrCityId is the same to the old id but guanzhu status changed so load data...");
					getFollowShopList(mStrCityID, mLoginDataManager.getMemberId(),true);
				}
			} else {
				tmpStrCityId = mStrCityID;
				CCLog.d(tag, "tmpStrCityId is not null but different old id so load data.......");
				getFollowShopList(mStrCityID, mLoginDataManager.getMemberId(),true);
			}
			
		} else {
			tmpStrCityId = mStrCityID;
			CCLog.d(tag, "tmpStrCityId is null load data.......");
			getFollowShopList(mStrCityID, mLoginDataManager.getMemberId(),true);
		}
	}

//	@SuppressLint("HandlerLeak")
//	Handler mUiHandler = new Handler() {
//		public void handleMessage(Message msg) {
//
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 0:
//				// 获取首页关注吆喝内容列表
//				getFollowShopList(mStrCityID, mLoginDataManager.getMemberId());
//				break;
//
//			case 1:
//				UIHelper.ToastMessage(mBaseActivity, "数据已全部加载，没有更多了。");
//				mLvPullToRefreshView.onRefreshComplete(); // 下拉刷新完成
//				break;
//			case 2:
//				ApiAccess.dismissProgressDialog();
//				if (mFollowShops != null && mFollowShops.size() > 0) {
//					mLvPullToRefreshView.setVisibility(View.VISIBLE);
//					mLlEmpty.setVisibility(View.GONE);
//					// 设定首页吆喝内容
//					mAdapter = new HomeGuanZhuAdapter(mBaseActivity,
//							mFollowShops);
//					mLvPullToRefreshView.setAdapter(mAdapter);
//					initControlerListenner();
//				}
//				break;
//			default:
//				break;
//			}
//		};
//	};

	/**
	 * 加载数据啦~
	 */
//	public void loadData(final int type) {
//		new Thread() {
//			@Override
//			public void run() {
//				switch (type) {
//				case 0:
//					Message _Msg = mUiHandler.obtainMessage(0);
//					mUiHandler.sendMessage(_Msg);
//					CCLog.i("关注商家  —— 可以加载数据了。。");
//					break;
//				case 1:
//					getFollowShopList(mStrCityID,
//							mLoginDataManager.getMemberId());
//					break;
//				}
//
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (type == 0) { // 下拉刷新
//					// Collections.reverse(mList); //逆序
//					Message _Msg = mUiHandler.obtainMessage(1);
//					mUiHandler.sendMessage(_Msg);
//				}
//			}
//		}.start();
//	}
//
//	class myRunnable implements Runnable {
//		@Override
//		public void run() {
//
//			// 获取首页关注吆喝内容列表
//			getFollowShopList(GlobalConstant.DEFAULT_CITY_ID,
//					mLoginDataManager.getMemberId());
//		}
//	}

	

	/**
	 * 获取首页吆喝列表
	 * 
	 * @Title getFollowShopList
	 */
	private void getFollowShopList(String cityid, String memberID,final boolean refreshData) {
		if (mLoginDataManager.getLoginState().equals("1")) {

			HttpUtils http = new HttpUtils();
			String url = ContantsValues.HOME_FOLLOW_SHOP_LIST_URL + "&city_id="
					+ cityid + "&member_id=" + memberID+"&page="+currentPage;
			CCLog.d(tag, "home guanzhu shangjia url:"+url);
			http.send(HttpRequest.HttpMethod.GET, url, null,
					new RequestCallBack<String>() {
				
				

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							CCLog.d(tag, "refresh data:"+refreshData);
							CCLog.d(tag, "refresh data:"+responseInfo.toString());
							if(refreshData) {
								currentPage = 1;
								mFollowShops.clear();
							}
							
							ApiAccess.dismissProgressDialog();
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(responseInfo.result);
								if (responseInfo.result != null) {

									CCLog.e("关注商家列表：", responseInfo.result
											+ " ");
								}

								if (jsonObject.has("data")) {
									mHomeFollowShopInfo = GsonUtils.json2Bean(
											responseInfo.result,
											HomeFollowShopInfo.class);
									if (mHomeFollowShopInfo != null) {

										if (mHomeFollowShopInfo.data != null
												&& mHomeFollowShopInfo.data
														.size() > 0) {
											
											try {
												mTotalPage = jsonObject.optInt("pageNumber");
											} catch(Exception e) {
												e.printStackTrace();
											}
											CCLog.d(tag, "mTotalPage:"+mTotalPage);
											
											//mFollowShops.clear();
//											currentPage = currentPage + 1;
											if (mHomeFollowShopInfo.data.size() == 1) {
												if (Utils
														.isStringEmpty(mHomeFollowShopInfo.data
																.get(0).id)
														&& Utils.isStringEmpty(mHomeFollowShopInfo.data
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
											
											ArrayList<FollowShop> mFollowShopsTmp = new ArrayList<FollowShop>();
											int dataSize = mHomeFollowShopInfo.data.size();
											
											for (int j = 0; j < dataSize; j++) {
												FollowShop followShopInfo = new FollowShop();
												if (mHomeFollowShopInfo.data
														.get(j).id != null) {
													followShopInfo.id = mHomeFollowShopInfo.data
															.get(j).id;
												}
												if (mHomeFollowShopInfo.data
														.get(j).member_id != null) {
													followShopInfo.member_id = mHomeFollowShopInfo.data
															.get(j).member_id;
												}
												if (mHomeFollowShopInfo.data
														.get(j).service_id != null) {
													followShopInfo.service_id = mHomeFollowShopInfo.data
															.get(j).service_id;
												}
												if (mHomeFollowShopInfo.data
														.get(j).shop_id != null) {
													followShopInfo.shop_id = mHomeFollowShopInfo.data
															.get(j).shop_id;
												}
												if (mHomeFollowShopInfo.data
														.get(j).shop_name != null) {
													followShopInfo.shop_name = mHomeFollowShopInfo.data
															.get(j).shop_name;
												}
												if (mHomeFollowShopInfo.data
														.get(j).shop_fans_num != null) {
													followShopInfo.shop_fans_num = mHomeFollowShopInfo.data
															.get(j).shop_fans_num;
												}
												if (mHomeFollowShopInfo.data
														.get(j).shop_star != null) {
													followShopInfo.shop_star = mHomeFollowShopInfo.data
															.get(j).shop_star;
												}
												if (mHomeFollowShopInfo.data
														.get(j).type != null) {
													followShopInfo.type = mHomeFollowShopInfo.data
															.get(j).type;
												}
												if (mHomeFollowShopInfo.data
														.get(j).title != null) {
													followShopInfo.title = mHomeFollowShopInfo.data
															.get(j).title;
												}
												if (mHomeFollowShopInfo.data
														.get(j).content != null) {
													followShopInfo.content = mHomeFollowShopInfo.data
															.get(j).content;
												}
												if (mHomeFollowShopInfo.data
														.get(j).addtime != null) {
													followShopInfo.addtime = mHomeFollowShopInfo.data
															.get(j).addtime;
												}
												if (!Utils
														.isStringEmpty(mHomeFollowShopInfo.data
																.get(j).img1)) {
													followShopInfo.img1 = URLs.IMG_PRE
															+ mHomeFollowShopInfo.data
																	.get(j).img1;
												}
												if (mHomeFollowShopInfo.data
														.get(j).collection_num != null) {
													followShopInfo.collection_num = mHomeFollowShopInfo.data
															.get(j).collection_num;
												}
												if (mHomeFollowShopInfo.data
														.get(j).zan_num != null) {
													followShopInfo.zan_num = mHomeFollowShopInfo.data
															.get(j).zan_num;
												}
												if (mHomeFollowShopInfo.data
														.get(j).comment_num != null) {
													followShopInfo.comment_num = mHomeFollowShopInfo.data
															.get(j).comment_num;
												}
												if (mHomeFollowShopInfo.data
														.get(j).fans_num != null) {
													followShopInfo.fans_num = mHomeFollowShopInfo.data
															.get(j).fans_num;
												}
												////////////////引用
												if (mHomeFollowShopInfo.data.get(j).s_content != null) {
													followShopInfo.s_content = mHomeFollowShopInfo.data
															.get(j).s_content;
												}
												if (!Utils
														.isStringEmpty(mHomeFollowShopInfo.data
																.get(j).s_img)) {
													followShopInfo.s_img = URLs.IMG_PRE
															+ mHomeFollowShopInfo.data
																	.get(j).s_img;
												}
												if (!Utils
														.isStringEmpty(mHomeFollowShopInfo.data
																.get(j).img)) {
													followShopInfo.img = URLs.IMG_PRE
															+ mHomeFollowShopInfo.data
																	.get(j).img;
												}
												
												followShopInfo.c_id = mHomeFollowShopInfo.data
														.get(j).c_id;
												
												
												mFollowShopsTmp.add(followShopInfo);
											}
											
											//刷新数据
											if(refreshData) {
												mFollowShops.addAll(mFollowShopsTmp);
											} else {//加载更多数据
												mFollowShops.addAll(mFollowShopsTmp);
											}
											

											if (mFollowShops != null
													&& mFollowShops.size() > 0) {
												mLvPullToRefreshView
														.setVisibility(View.VISIBLE);
												mLlEmpty.setVisibility(View.GONE);
												// 设定首页吆喝内容
												 refreshFollow(mFollowShops);
//												if (mUiHandler != null) {
//													mUiHandler.obtainMessage(2);
//												}
											}
										} else {
											mLlEmpty.setVisibility(View.VISIBLE);
											mLvPullToRefreshView
													.setVisibility(View.GONE);
										}
									}
								} else {
									mLvPullToRefreshView
											.setVisibility(View.GONE);
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
							onLoad();
							ApiAccess.dismissProgressDialog();
							mLvPullToRefreshView.setVisibility(View.GONE);
							mLlEmpty.setVisibility(View.VISIBLE);
						}
					});
		} else {
			ApiAccess.dismissProgressDialog();
			if (mLvPullToRefreshView != null) {
				mLvPullToRefreshView.setVisibility(View.GONE);
			}
			if (mLlEmpty != null) {
				mLlEmpty.setVisibility(View.VISIBLE);
			}

		}

	}

	/**
	 * 设定首页关注显示内容
	 */
	private void setFollowShopList() {
		mAdapter = new HomeGuanZhuAdapter(mBaseActivity, mFollowShops);
		mLvPullToRefreshView.setAdapter(mAdapter);
		// measureListView(mLvPullToRefreshView);
		initControlerListenner();
	}

	private void refreshFollow(List<FollowShop> followShop) {
		this.mFollowShops = followShop;
		if (mAdapter != null) {
			mAdapter.refresh(followShop);
		} else {
			mAdapter = new HomeGuanZhuAdapter(mBaseActivity, mFollowShops);
			mLvPullToRefreshView.setAdapter(mAdapter);
		}
		initControlerListenner();
	}

	/**
	 * 关注，收藏，点评相关监听事件
	 * 
	 * @Title initControlerListenner
	 */
	private void initControlerListenner() {

		if (mAdapter != null) {
			mAdapter.setOnHomeItemClickListener(new OnGuanZhuListener() {

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
						TextView tvPinLunImg, TextView tvPinLun, String callID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});
			// 点击查看商家信息
			mAdapter.setOnBusinessShopListener(new OnGuanZhuListener() {
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
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

			});

			// 首页关注按钮点击监听事件处理
			mAdapter.setOnGuanZhuClickListener(new OnGuanZhuListener() {
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
//							new Handler().postDelayed(new Runnable() {
//								@Override
//								public void run() {
//									ApiAccess.dismissProgressDialog();
//									if (!mIsAllowFollow) {
//										if (Utils
//												.strFromView(tvGuanZhu)
//												.equals(GlobalConstant.INVALID_VALUE)) {
//											tvGuanZhu
//													.setText(GlobalConstant.VALID_VALUE);
//											tvGuanZhu
//													.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
//
//											new Thread(new Runnable() {
//
//												@Override
//												public void run() {
//													currentPage = 1;
//													getFollowShopList(
//															mStrCityID,
//															mLoginDataManager
//																	.getMemberId(),true);
//												}
//											}).start();
//
//										} else {
//											tvGuanZhu
//													.setText(GlobalConstant.INVALID_VALUE);
//											tvGuanZhu
//													.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
//										}
//									}
//								}
//							}, 1000);
						} else if (Utils.strFromView(tvGuanZhu).equals(
								GlobalConstant.VALID_VALUE)) {

							ApiAccess.showProgressDialog(getActivity(),
									"取消关注中...");
							// 关注
							cancelFollows(shopID);
							//TODO 需要加刷新

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
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnShouCangClickListenerClickListener(new OnGuanZhuListener() {

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
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnZanClickListenerClickListener(new OnGuanZhuListener() {

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
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});

			mAdapter.setOnPinLunClickListenerClickListener(new OnGuanZhuListener() {

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
				public void onCallItemClick(int position, String callId,
						String serviceId, String shopId, String memberID,
						String type) {
				}

				@Override
				public void onBusinessShopClick(int position, String shopId,
						String memberId) {

				}
			});
		}

	}

	/**
	 * 吆喝收藏
	 */
	private void callCollection(String callID) {
		String url = ContantsValues.CALL_FOLLOW_URL +"&member_id="+mLoginDataManager.getMemberId()+"&id="+callID;
		shopActionApi(mLoginDataManager.getMemberId(), callID, url, "收藏成功。");
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

	@Override
	protected void resetLayout(View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_home_fragment_guanzhu);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		mLvPullToRefreshView = (XListView) view
				.findViewById(R.id.lv_home_guanzhu_shop);
		mLvPullToRefreshView.setVisibility(View.VISIBLE);

		CCLog.i("关注商家Fragment", "resetLayout");

		// 空内容提示
		mLlEmpty = (LinearLayout) view
				.findViewById(R.id.ll_home_guanzhu_shop_empty);
		mTvTips = (TextView) view
				.findViewById(R.id.tv_fragment_extra_text_link);
		mTvNetError = (TextView) view
				.findViewById(R.id.tv_home_guanzhu_net_error);
		mTvTips.setText(Html.fromHtml("去"
				+ "<font size=\"14\" color=\"#23a3fc\">附近商家</font>" + "看看吧"));

		mTvTips.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 点击 “还没关注任何商家”提示文字。
				Intent fujinIntent = new Intent();
				fujinIntent.setClass(getActivity(), FuJinActivity.class);
				mBaseActivity.baseStartActivity(fujinIntent);
			}
		});

		if (!AppApplacation.getInstance().isNetworkConnected()) {
			mTvNetError.setVisibility(View.VISIBLE);
			mLvPullToRefreshView.setVisibility(View.GONE);
			return;
		} else {
			mTvNetError.setVisibility(View.GONE);
			mLvPullToRefreshView.setVisibility(View.VISIBLE);
		}

		// 设定首页关注显示内容
		setFollowShopList();

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

	}
	
	public void refresh() {
		currentPage = 1;
		getFollowShopList(mStrCityID, mLoginDataManager.getMemberId(),true);
	}
	
	public void loadMore() {
		currentPage = currentPage + 1;
		if (currentPage>mTotalPage) {
			mLvPullToRefreshView.forbiddenLoadMore();
			UIHelper.ToastMessage(getActivity(), "数据已全部加载，没有更多了。");
		} else {
			getFollowShopList(mStrCityID, mLoginDataManager.getMemberId(),false);
		}

	}
	
	private void onLoad() {
		mLvPullToRefreshView.stopRefresh();
		mLvPullToRefreshView.stopLoadMore();
		mLvPullToRefreshView.setRefreshTime("");
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

	/**
	 * 取消关注
	 */
	private void cancelFollows(final String shopID) {
		String url = ContantsValues.CANCEL_FOLLOWS;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", shopID);
		CCLog.i("取消关注参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + shopID);
		//首页关注 取消关注
		url = url + "&member_id=" + mLoginDataManager.getMemberId()
				+ "&id=" + shopID;
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
												
												
												if(mFollowShops !=null){
													ArrayList<FollowShop> list = new ArrayList<FollowShop>();
													for(FollowShop fs :mFollowShops) {
														if(shopID.equals(fs.shop_id)) {
															list.add(fs);
														}
													}
													mFollowShops.removeAll(list);
												}
												refreshFollow(mFollowShops);
//												new Thread(new Runnable() {
//
//													@Override
//													public void run() {
//														getFollowShopList(
//																mStrCityID,
//																mLoginDataManager
//																		.getMemberId(),true);
//													}
//												}).start();
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

	/**
	 * 吆喝点赞
	 */
	private void callPraise(String callID) {
		shopZanActionApi(mLoginDataManager.getMemberId(), callID,
				ContantsValues.CALL_PRAISE_URL, "点赞成功。");
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

	/**
	 * 吆喝点评
	 */
	private void callComment(String callID, String type) {
		Intent intent = new Intent(getActivity(), YaoHeCommentActivity.class);
		intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, callID);
		intent.putExtra("YaoHeType", type);
		mBaseActivity.baseStartActivity(intent);

	}
	//是否来自关注状态的变化
	boolean isFromGuanzhuStatusChanged = false;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden) {
			//在其他页面是否 点击了关注按钮 取消 或者 添加关注 检测是否需要刷新数据
			if(isRefreshHomeGuanzhuFragment()) {
				setRefreshHomeGuanzhuFragmentStatus(false);
				isFromGuanzhuStatusChanged = true;
				CCLog.d(tag, "refresh data beasure guanzhu status is changed");
				doResetData();
			} else {
				//do nothing....
			}
			CCLog.d(tag, "show guanzhu fragment.....");
		}
	}
	
	/**
	 * 
	 * @author LEE
	 * 关注状态 收藏状态 关注广播
	 *
	 */
	 class StatusBroadCastReceiver extends BroadcastReceiver {   
	    @Override  
	    public void onReceive(Context context, Intent intent) {
	    	try {
	    		CCLog.d(tag, "broadcast..........");
		    	int doWhat = intent.getIntExtra("doWhat",-1);
		    	String shopId = intent.getStringExtra("shopId");
		    	boolean isCanceFollow  =intent.getBooleanExtra("isCanceFollow",false);
		    	ArrayList<FollowShop> tmp = null;
		    	switch(doWhat) {
			    	case CommonConstant.doWhat_change_followStatus:
			    		if(isCanceFollow) {
			    			tmp = new ArrayList<FollowShop>();
			    		}
			    		for(FollowShop followShop : mFollowShops) {
			    			if(followShop.shop_id.equals(shopId)) {
			    				if(isCanceFollow) {
			    					tmp.add(followShop);
			    				} else {
			    					doResetData();
			    				}
			    			}
			    		}
			    		if(tmp != null) {
			    			mFollowShops.removeAll(tmp);
			    			refreshFollow(mFollowShops);
			    		}
			    		
			    		CCLog.d(tag, "change guanzhu status... notify adapter");
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
