package com.collcloud.yaohe.activity.person.youhui.usableyouhuiquan;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.swipe.view.XListView;
import com.collcloud.swipe.view.XListView.IXListViewListener;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.activity.person.youhui.PersonYouhuiActivity;
import com.collcloud.yaohe.activity.person.youhui.usedyouhuiquan.UsedYouHuiQuanActivity;
import com.collcloud.yaohe.activity.person.youhuiquan.detail.YouHuiQuanDetailActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.YhqInfoList;
import com.collcloud.yaohe.ui.adapter.PersonUsableYouHuiQuanAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 我的(个人)可用优惠券页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class UsableYouHuiQuanActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "USABLEYHQ";
	/** 页面标题 */
	private TextView tv_actionbartitle;
	/** 可用 */
	private TextView mTvUseable;
	/** 不可用 */
	private TextView tv_usedyouhuiquan_;
	/** 数据加载进度条 */
	private Dialog mDialog;

	private XListView mXListView;
	private PersonUsableYouHuiQuanAdapter mAdapter;
	private ArrayList<YhqInfoList> mDatas = new ArrayList<YhqInfoList>();

	// ******** Tips *******//
	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_usable_you_hui_quan);

		intialSource();
		accessNetGetUsableYHQ();
	}

	/**
	 * 获取可使用的优惠券数据
	 */
	private void accessNetGetUsableYHQ() {
		progressbar(UsableYouHuiQuanActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.KSYYH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						mDialog.dismiss();
						showToast("数据加载失败了。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						mDialog.dismiss();
						CCLog.v(TAG, "网络获取优惠券数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络访问商圈状态码
						String code = "";
						try {

							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							if (code.equals("0")) {

								JSONArray jsoArray = object
										.optJSONArray("data");
								final int itemusableYHQCount = jsoArray
										.length();
								if (itemusableYHQCount > 0) {
									if (itemusableYHQCount == 1) {
										JSONObject yhObject = jsoArray
												.getJSONObject(0);
										if (Utils.isStringEmpty(yhObject
												.optString("id"))) {
											mLlEmpty.setVisibility(View.VISIBLE);
											mXListView.setVisibility(View.GONE);
											return;
										} else {
											mXListView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
										}
									}
									if (mDatas != null && mDatas.size()>0) {
										mDatas.clear();
									}
									mDatas = new ArrayList<YhqInfoList>();
									/** 可使用的会员卡数据集合 */
									for (int i = 0; i < itemusableYHQCount; i++) {
										

										YhqInfoList yhqItem = new YhqInfoList();

										JSONObject yhqObject = jsoArray
												.getJSONObject(i);

										yhqItem.id = yhqObject.optString("id");
										yhqItem.content = yhqObject
												.optString("content");
										yhqItem.member_id = yhqObject
												.optString("member_id");
										yhqItem.member_coupon_id = yhqObject
												.optString("member_coupon_id");
										if (!Utils.isStringEmpty(yhqObject
												.optString("img1"))) {
											yhqItem.img1 = URLs.IMG_PRE
													+ yhqObject
															.optString("img1");
										}
										yhqItem.shop_id = yhqObject
												.optString("shop_id");
										yhqItem.shop_name = yhqObject
												.optString("shop_name");
										yhqItem.title = yhqObject
												.optString("title");
										yhqItem.type = yhqObject
												.optString("type");
										yhqItem.use_number = yhqObject
												.optString("use_number");
										yhqItem.valid_end = yhqObject
												.optString("valid_end");
										yhqItem.valid_start = yhqObject
												.optString("valid_start");
										mDatas.add(yhqItem);

									}
									
									mTvUseable.setText("可使用(" + mDatas.size() + ")");
									setYouhuiData(mDatas);
								} else {
									showToast("您还没有可使用的优惠券。");
								}
							} else {
								showToast("优惠券信息加载失败了，返回重试!");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});
	}

	private void setYouhuiData(ArrayList<YhqInfoList> usableYHQList) {
		mAdapter = new PersonUsableYouHuiQuanAdapter(
				UsableYouHuiQuanActivity.this, usableYHQList);
		mXListView.setAdapter(mAdapter);
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {
		// 刷新相关
		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("我的优惠券");

		mTvUseable = (TextView) findViewById(R.id.tv_usableyouhuiquan);
		this.tv_usedyouhuiquan_ = (TextView) findViewById(R.id.tv_usedyouhuiquan_);
		tv_usedyouhuiquan_.setOnClickListener(this);

		// Xlistview
		this.mXListView = (XListView) findViewById(R.id.xlv_person_useable_coupon);
		handler = new Handler();
		// 设置xlistview可以加载、刷新
		mXListView.setPullLoadEnable(false);
		// 设置xlistview可以刷新
		mXListView.setPullRefreshEnable(true);
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				CCLog.i("优惠券Item Pos:", position + " ");
				Intent intent = new Intent();
				intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID,
						mDatas.get(position - 1).id);
				intent.putExtra("useableCouponID", mDatas.get(position - 1).id);
				intent.putExtra("useableMemberCouponID",
						mDatas.get(position - 1).member_coupon_id);
//				intent.setClass(UsableYouHuiQuanActivity.this,
//						YouHuiQuanDetailActivity.class);
				intent.setClass(UsableYouHuiQuanActivity.this,
						YouHuiDetailsActivity.class);
				baseStartActivity(intent);

			}
		});
		mXListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				accessNetGetUsableYHQ();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mXListView.stopRefresh();
						showToast("刷新成功");
					}
				}, 1000);
			}

			@Override
			public void onLoadMore() {
				accessNetGetUsableYHQ();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						mXListView.stopLoadMore();
						showToast("数据加载成功");
					}
				}, 1000);
			}
		});
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.ll_tv_actionbarback:
			baseStartActivity(new Intent(UsableYouHuiQuanActivity.this,
					PersonYouhuiActivity.class));
			finish();
			break;

		case R.id.tv_usedyouhuiquan_:
			baseStartActivity(new Intent(UsableYouHuiQuanActivity.this,
					UsedYouHuiQuanActivity.class));
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 数据加载
	 * */
	private void progressbar(Context context, int layout) {
		mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		mDialog.setContentView(layout);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		LinearLayout ll_myyhq_usable = (LinearLayout) findViewById(R.id.ll_usable_yhq_root);
		SupportDisplay.resetAllChildViewParam(ll_myyhq_usable);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);

		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_person_youhui_empty);
		mLlEmpty.setVisibility(View.GONE);
		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("没有可使用的优惠券\n\n去首页推荐看看吧");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(UsableYouHuiQuanActivity.this,
						MainActivity.class));
				finish();
			}
		});

	}

}
