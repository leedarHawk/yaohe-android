package com.collcloud.yaohe.common.base;

import java.util.ArrayList;
import java.util.List;

import com.collcloud.yaohe.activity.friend.FriendHaowanActivity;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.swipe.interfaces.ILoginDataManager;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.fayaohe.BusinessFaYaoHeActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.ShopCommentActivity;
//import com.collcloud.yaohe.activity.friend.HaoWanActivity;
import com.collcloud.yaohe.activity.fujin.FuJinActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.ResponseDataToUI;
import com.collcloud.yaohe.common.data.LoginDataManagerSPImpl;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
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
 * Fragment共通基类
 * 
 * @ClassName BaseFragment
 * @Description 自定义Fragment的基类
 * @author CollCloud_小米
 */
public abstract class BaseFragment extends Fragment {
	protected BaseActivity mBaseActivity = null;

	protected ViewPager mPager;
	protected HomePageAdapter mPagerAdapter;
	private ViewGroup mCircleView = null;

	private ImageButton preBtn = null;
	private List<String> mCircleData = new ArrayList<String>();

	private boolean timeFlag = false;
	private boolean theadFlag = true;
	private Handler myHandler = null;

	// *** 底部菜单导航设置 *** //
	// ******************　底部切换　************* //
	// 首页
	private static final int FOOTER_TYPE_FRAGMENT_HOME = 1;
	// 附近
	private static final int FOOTER_TYPE_FRAGMENT_FUJIN = 2;
	// 好玩
	private static final int FOOTER_TYPE_FRAGMENT_HAOWAN = 3;
	// 我的
	private static final int FOOTER_TYPE_FRAGMENT_MINE = 4;
	/**
	 * footer类型
	 */
	protected int mFooterType = -1;
	/**
	 * footer_按钮_我的
	 */
	private ImageView mIvFooterHome;
	/**
	 * footer_按钮_附近
	 */
	private ImageView mIvFooterFujin;
	/**
	 * footer_按钮_好玩
	 */
	private ImageView mIvFooterHaowan;
	/**
	 * footer_按钮_我的
	 */
	private ImageView mIvFooterMine;
	/**
	 * footer_文本_首页
	 */
	private TextView mTvFooterHome;
	/**
	 * footer_文本_附近
	 */
	private TextView mTvFooterFujin;
	/**
	 * footer_文本_好玩
	 */
	private TextView mTvFooterHaowan;
	/**
	 * footer_文本_我的
	 */
	private TextView mTvFooterMine;
	protected LinearLayout mLlfooterCommon;
	private LinearLayout mLlHomeLayout;
	private LinearLayout mLlFujinLayout;
	private LinearLayout mLlHaoWanLayout;
	private RelativeLayout mRlMineLayout;

	// ***　分割线 && 以下是底部包含【吆喝】按钮切换的组件　*** //
	protected LinearLayout mLlfooterCommonHasYaohe;
	private LinearLayout mLlHomeLayoutHasYaohe;
	private LinearLayout mLlFujinLayoutHasYaohe;
	private LinearLayout mLlHaoWanLayoutHasYaohe;
	private RelativeLayout mRlMineLayoutHasYaohe;
	private ImageView mIvFooterHomeHasYaohe;
	private ImageView mIvFooterFujinHasYaohe;
	private ImageView mIvFooterHaowanHasYaohe;
	private ImageView mIvFooterMineHasYaohe;
	private TextView mTvFooterHomeHasYaohe;
	private TextView mTvFooterFujinHasYaohe;
	private TextView mTvFooterHaowanHasYaohe;
	private TextView mTvFooterMineHasYaohe;
	private Button mBtnFooterYaoHe;
	
	//显示加载提示框
	 public ApiAccess ApiAccess = new ApiAccess();

	protected ILoginDataManager mLoginDataManager = LoginDataManagerSPImpl
			.getInstace(getActivity());

	@Override
	public void onAttach(Activity activity) {
		mBaseActivity = (BaseActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		resetLayout(getView());
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		theadFlag = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd("Fragment");
	}

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart("Fragment"); //统计页面
	}

	protected void baseAddFragment(int container, Fragment fragment) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
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

	public void measureListView(SingleLayoutListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	protected abstract void resetLayout(View view);

//	protected void doOnApiAccessResponsedFragment(ResponseDataToUI responseData) {
//	}

	protected void doOnErrorResponseAction() {
	}

	/*
	protected ApiAccessResponseListener mApiResponseFragmentListener = new ApiAccessResponseListener() {

		@Override
		public void onApiAccessResponse(ResponseDataToUI responseData) {
			if (responseData == null) {
				String errorMsg = ApiAccessErrorManager.getMessage(
						ApiAccessErrorManager.RESPONSE_DATA_INVALID,
						mBaseActivity);
				UIHelper.ToastMessage(mBaseActivity, errorMsg);
				doOnErrorResponseAction();
				return;
			}
			if (ApiAccessErrorManager.isNotOK(responseData.netWorkErrorCode)) {
				if (!mBaseActivity.mApplication.isNetworkConnected()) {
					String errorMsg = ApiAccessErrorManager.getMessage(
							ApiAccessErrorManager.NETWORK_DISABLED,
							mBaseActivity);
					UIHelper.ToastMessage(mBaseActivity, errorMsg);
				} else {
					String errorMsg = ApiAccessErrorManager.getMessage(
							responseData.netWorkErrorCode, mBaseActivity);
					UIHelper.ToastMessage(mBaseActivity, errorMsg);
				}
				doOnErrorResponseAction();
				return;
			} else {
				if (!Utils.isStringEmpty(responseData.result)) {
					if (responseData.result.contains("status")) {
						try {
							// 数据处理
							JSONObject errorJsonObject = new JSONObject(
									responseData.result);
							if (errorJsonObject.has("status")) {
								int code = errorJsonObject.optInt("code");
								if (code == 1) {
									String strErrorMsg = errorJsonObject
											.getString("message");
									CCLog.i("BaseFragment 结果:", "错误信息是： "
											+ strErrorMsg);
									UIHelper.ToastMessage(mBaseActivity,
											strErrorMsg);
								} else {
									CCLog.i("BaseFragment 结果:",
											"status ：0 正常。 ");
								}
							}
						} catch (Exception e) {
							String errorMsg = ApiAccessErrorManager.getMessage(
									5, mBaseActivity);
							UIHelper.ToastMessage(mBaseActivity, errorMsg);
						}
						doOnErrorResponseAction();

					}
				}
			}
			doOnApiAccessResponsedFragment(responseData);
		};
	};
	
	*/

	// ==== 华丽的分割线 下面内容涉及viewpager底部圆点处理 ==== //
	/**
	 * 初始化viewpager共通调用的组件信息等
	 * 
	 * @return void
	 */
	protected void initViewPagerCircleView() {
		setTopImageCircleView();
		mPager.setCurrentItem(0);
		if(myHandler == null) {
			myHandler = new myHandler();
		}
		
		// Thread thread = new Thread(new MyThread());
		// thread.start();
	}

	/**
	 * 画面初期化时，相关处理
	 */
	private void setTopImageCircleView() {
		if (mCircleView != null) {
			mCircleView.removeAllViews();
		}
		for (int i = 0; i < mCircleData.size(); i++) {
			ImageButton btn = new ImageButton(mBaseActivity);
			LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams(15, 15);
			vl.setMargins(2, 0, 2, 0);
			btn.setLayoutParams(vl);
			if (i == 0) {
				btn.setBackgroundResource(R.drawable.icon_home_pager_circle_on);
			} else {
				btn.setBackgroundResource(R.drawable.icon_home_pager_circle_off);
			}
			mCircleView.addView(btn);
		}
		preBtn = null;
		if (mPager.getChildCount() > 0) {
			mPager.setCurrentItem(0);
		}
	}

	protected class ImageViewPagerChangeListener implements
			OnPageChangeListener {
		public ImageViewPagerChangeListener() {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 == 1) {
				timeFlag = true;
			}
		}

		@Override
		public void onPageScrolled(int position, float offset, int offsetPixel) {

		}

		@Override
		public void onPageSelected(int index) {
			if (preBtn == null) {
				preBtn = (ImageButton) mCircleView.getChildAt(0);
			}
			if (preBtn != null) {
				preBtn.setBackgroundResource(R.drawable.icon_home_pager_circle_off);
			}
			ImageButton currentBt = (ImageButton) mCircleView.getChildAt(index
					% mCircleData.size());

			currentBt
					.setBackgroundResource(R.drawable.icon_home_pager_circle_on);
			preBtn = currentBt;
		}
	}

	private void sleep() throws InterruptedException {
		Thread.sleep(3000);
		if (timeFlag) {
			timeFlag = false;
			sleep();
		}
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mPager != null) {
				int currentItem = mPager.getCurrentItem();
				mPager.setCurrentItem(currentItem + 1);
			}
		}
	}

	private class MyThread implements Runnable {

		@Override
		public void run() {
			while (theadFlag) {
				try {
					sleep();
					Message message = new Message();
					message.what = 1;
					myHandler.sendMessage(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public List<String> getmCircleData() {
		return mCircleData;
	}

	public void setmCircleData(List<String> mCircleData) {
		this.mCircleData = mCircleData;
	}

	public ViewGroup getmCircleView() {
		return mCircleView;
	}

	public void setmCircleView(ViewGroup mCircleView) {
		this.mCircleView = mCircleView;
	}

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}

	// ******************** 底部相关 ******************** //
	// ********** 改版 底部导航切换 ************* //
	protected void getViewById(View view) {
		if (view.findViewById(R.id.ll_yaohe_footer_no_yaohe) != null) {
			mLlfooterCommon = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_footer_no_yaohe);
			mIvFooterHome = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_home);
			mIvFooterFujin = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_fujin);
			mIvFooterHaowan = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_haowan);
			mIvFooterMine = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_mine);
			mTvFooterHome = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_home);
			mTvFooterFujin = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_fujin);
			mTvFooterHaowan = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_haowan);
			mTvFooterMine = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_mine);
			// ******** 底部切换监听事件 ********* //
			mLlHomeLayout = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_home);
			mLlFujinLayout = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_fujin);
			mLlHaoWanLayout = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_haowan);
			mRlMineLayout = (RelativeLayout) view
					.findViewById(R.id.rl_yaohe_bottom_mine);
			mLlHomeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHome();
				}
			});
			mLlFujinLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnFujin();
				}
			});
			mLlHaoWanLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHaoWan();
				}
			});
			mRlMineLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnMine();
				}
			});
		}
		// ***　分割线 && 以下是底部包含【吆喝】按钮切换的组件　*** //
		if (view.findViewById(R.id.ll_yaohe_footer_has_yaohe) != null) {
			mLlfooterCommonHasYaohe = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_footer_has_yaohe);
			mIvFooterHomeHasYaohe = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_home_has_yaohe);
			mIvFooterFujinHasYaohe = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_fujin_has_yaohe);
			mIvFooterHaowanHasYaohe = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_haowan_has_yaohe);
			mIvFooterMineHasYaohe = (ImageView) view
					.findViewById(R.id.iv_yaohe_bottom_mine_has_yaohe);
			mTvFooterHomeHasYaohe = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_home_has_yaohe);
			mTvFooterFujinHasYaohe = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_fujin_has_yaohe);
			mTvFooterHaowanHasYaohe = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_haowan_has_yaohe);
			mTvFooterMineHasYaohe = (TextView) view
					.findViewById(R.id.tv_yaohe_bottom_mine_has_yaohe);
			// ******** 底部切换监听事件 ********* //
			mLlHomeLayoutHasYaohe = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_home_has_yaohe);
			mLlFujinLayoutHasYaohe = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_fujin_has_yaohe);
			mLlHaoWanLayoutHasYaohe = (LinearLayout) view
					.findViewById(R.id.ll_yaohe_bottom_haowan_has_yaohe);
			mRlMineLayoutHasYaohe = (RelativeLayout) view
					.findViewById(R.id.rl_yaohe_bottom_mine_has_yaohe);
			mBtnFooterYaoHe = (Button) view
					.findViewById(R.id.btn_footer_bottom_yaohe);
			mBtnFooterYaoHe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickOnYaoHe();

				}
			});
			mLlHomeLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHome();
				}
			});
			mLlFujinLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnFujin();
				}
			});
			mLlHaoWanLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHaoWan();
				}
			});
			mRlMineLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnMine();
				}
			});
		}

		// 底部切换布局改变
		setFooterChanged();
	}

	/**
	 * 底部切换布局改变
	 */
	private void setFooterChanged() {
		if (mLoginDataManager.getUserType() != null) {
			if (mLoginDataManager.getUserType().equals(
					GlobalConstant.VALID_VALUE)) {
				mLlfooterCommon.setVisibility(View.GONE);
				mLlfooterCommonHasYaohe.setVisibility(View.VISIBLE);

			} else {
				mLlfooterCommon.setVisibility(View.VISIBLE);
				mLlfooterCommonHasYaohe.setVisibility(View.GONE);

			}
		} else {
			mLlfooterCommon.setVisibility(View.VISIBLE);
			mLlfooterCommonHasYaohe.setVisibility(View.GONE);
		}

	}

	/**
	 * 设置footer类型
	 * 
	 * @param footerType
	 */
	protected void setFooterType(int footerType) {
		mFooterType = footerType;
	}

	/**
	 * 设定底部标签选中
	 * 
	 * @Title setFooterChanged
	 */
	protected void setFooterChanged(int type) {
		mFooterType = type;
		if (mFooterType != -1) {
			if (mLoginDataManager.getUserType() != null) {
				if (mLoginDataManager.getUserType().equals(
						GlobalConstant.VALID_VALUE)) {
					setYaoHeSelectedFooter();
				} else {
					setSelectedFooter();
				}
			} else {
				setSelectedFooter();
			}

		}
	}

	/**
	 * 设置被选择的footer
	 * 
	 * @param footerType
	 */
	protected void setSelectedFooter() {
		if (mFooterType == FOOTER_TYPE_FRAGMENT_HOME) {
			mIvFooterHome.setImageResource(R.drawable.yaohe_footer_home_on);
			mTvFooterHome.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_FUJIN) {
			mIvFooterFujin.setImageResource(R.drawable.yaohe_footer_fujin_on);
			mTvFooterFujin.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_HAOWAN) {
			mIvFooterHaowan.setImageResource(R.drawable.yaohe_footer_haowan_on);
			mTvFooterHaowan.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_MINE) {
			mIvFooterMine.setImageResource(R.drawable.yaohe_footer_mine_on);
			mTvFooterMine.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		}
	}

	protected void setYaoHeSelectedFooter() {
		if (mFooterType == FOOTER_TYPE_FRAGMENT_HOME) {
			mIvFooterHomeHasYaohe
					.setImageResource(R.drawable.yaohe_footer_home_on);
			mTvFooterHomeHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_FUJIN) {
			mIvFooterFujinHasYaohe
					.setImageResource(R.drawable.yaohe_footer_fujin_on);
			mTvFooterFujinHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_HAOWAN) {
			mIvFooterHaowanHasYaohe
					.setImageResource(R.drawable.yaohe_footer_haowan_on);
			mTvFooterHaowanHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FRAGMENT_MINE) {
			mIvFooterMineHasYaohe
					.setImageResource(R.drawable.yaohe_footer_mine_on);
			mTvFooterMineHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		}
	}

	/**
	 * 点击【吆喝】监听事件
	 * 
	 * @Title clickOnYaoHe
	 */
	public void clickOnYaoHe() {

		Intent yaoheIntent = new Intent(mBaseActivity,
				BusinessFaYaoHeActivity.class);
		mBaseActivity.baseStartActivity(yaoheIntent);
	}

	/**
	 * 点击【主页】监听事件
	 * 
	 * @Title clickOnHome
	 */
	public void clickOnHome() {

		Intent intent = new Intent();
		intent.setClass(mBaseActivity, MainActivity.class);
		mBaseActivity.baseStartActivity(intent);
	}

	/**
	 * 点击【附近】监听事件
	 * 
	 * @Title clickOnFujin
	 */
	public void clickOnFujin() {

		Intent intent = new Intent();
		intent.setClass(mBaseActivity, FuJinActivity.class);
		mBaseActivity.baseStartActivity(intent);
	}

	/**
	 * 点击【好玩】监听事件
	 * 
	 * @Title clickOnHaoWan
	 */
	public void clickOnHaoWan() {

		Intent intent = new Intent();
		intent.setClass(mBaseActivity, FriendHaowanActivity.class);
		mBaseActivity.baseStartActivity(intent);
	}

	/**
	 * 点击【我的】监听事件
	 * 
	 * @Title clickOnMine
	 */
	public void clickOnMine() {
		// UserType : （0 个人 ； 1：商家）
		if (mLoginDataManager.getUserType().equals(GlobalConstant.VALID_VALUE)) {
			Intent myIntent = new Intent();
			myIntent.setClass(mBaseActivity, BusinessActivity.class);
			mBaseActivity.baseStartActivity(myIntent);
		} else { // 个人页面
			Intent myIntent = new Intent();
			myIntent.setClass(mBaseActivity, MineActivity.class);
			mBaseActivity.baseStartActivity(myIntent);
		}
	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	// 首页相关的关注，收藏，点赞
	public boolean mFragmetnIsFollow = false;

	/**
	 * 会员加店铺关注
	 */
	public void baseShopFollow(String shopID) {
		mFragmetnIsFollow = false;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", shopID);

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
										CCLog.i("Fragment关注状态信息：",
												responseInfo.result + " ");
									}
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												mFragmetnIsFollow = true;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												mFragmetnIsFollow = false;
											}
										}
									}
								} catch (Exception e) {
									mFragmetnIsFollow = true;
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
	 * 吆喝收藏、吆喝点赞 共通API调用
	 * 
	 * @Title baseShopActionApi
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
	public void baseShopActionApi(String memberID, String callID, String url,
			final String message) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝收藏点赞状态信息：", responseInfo.result
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
												String strErrorMsg = errorJsonObject
														.getString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);

											} else {
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
	 * 吆喝点评
	 */
	public void baseCallComment(String callID) {
		Intent intent = new Intent(getActivity(), ShopCommentActivity.class);
		intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, callID);
		mBaseActivity.baseStartActivity(intent);
	}
	/**
	 * 吆喝点赞
	 */
	public void baseCallPraise(String callID) {
		baseShopActionApi(mLoginDataManager.getMemberId(), callID,
				ContantsValues.CALL_PRAISE_URL, "点赞成功。");
	}

	/**
	 * 吆喝收藏
	 */
	public void baseCallCollection(String callID) {
		String url = ContantsValues.CALL_FOLLOW_URL +"&member_id="+mLoginDataManager.getMemberId()+"&id="+callID;
		baseShopActionApi(mLoginDataManager.getMemberId(), callID,url, "收藏成功。");
	}
}
