package com.collcloud.yaohe.activity.person.pinglun;

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
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.PersonShopCommentAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class PersonShopCommentActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "USEDYHQ";
	/** 页面标题 */
	private TextView tv_actionbartitle;
	/** 数据加载进度条 */
	private Dialog mDialog;

	private XListView mXListView;
	private PersonShopCommentAdapter mAdapter;
	private ArrayList<CallCommentInfo> mDatas = new ArrayList<CallCommentInfo>();
	private DetailsCallCommentInfo mDetailsCallCommentInfo;
	private int mTotalCount = 0;

	// ******** Tips *******//
	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_shop_pinglun);

		intialSource();

		progressbar(PersonShopCommentActivity.this, R.layout.loading_progress);

		getShopCommentInfo();
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
	 * 获取店铺点评的数据信息
	 */
	private void getShopCommentInfo() {

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.MY_SHOP_COMMENT,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						mDialog.dismiss();
						CCLog.v(GlobalConstant.TAG, "网络获取店铺点评数据成功");
						CCLog.v(GlobalConstant.TAG, responseInfo.result);
						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String response = "";
						// 网络访问商圈状态码
						String code = "";
						try {

							object = new JSONObject(responseInfo.result);
							response = object.getString("status");
							object2 = new JSONObject(response);

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

									if (itemusableYHQCount >9) {
										mXListView.setPullLoadEnable(false);
									}else {
										mXListView.setPullLoadEnable(true);
									}
									if (mDatas != null && mDatas.size() > 0) {
										mDatas.clear();
									}
									mDatas = new ArrayList<CallCommentInfo>();
									if (object != null && object.has("data")) {
										mDetailsCallCommentInfo = GsonUtils
												.json2Bean(
														responseInfo.result,
														DetailsCallCommentInfo.class);
										if (mDetailsCallCommentInfo.data != null
												&& mDetailsCallCommentInfo.data
														.size() > 0) {

											mTotalCount = mDetailsCallCommentInfo.data
													.size();
											if (mTotalCount > 9) {
												mXListView
														.setPullLoadEnable(true);
											} else {
												mXListView
														.setPullLoadEnable(false);
											}
											for (int j = 0; j < mDetailsCallCommentInfo.data
													.size(); j++) {

												CallCommentInfo commentInfo = new CallCommentInfo();
												if (mDetailsCallCommentInfo.data
														.get(j).id != null) {
													commentInfo.id = mDetailsCallCommentInfo.data
															.get(j).id;
												}
												if (mDetailsCallCommentInfo.data
														.get(j).member_id != null) {
													commentInfo.member_id = mDetailsCallCommentInfo.data
															.get(j).member_id;
												}
												if (mDetailsCallCommentInfo.data
														.get(j).content != null) {
													commentInfo.content = mDetailsCallCommentInfo.data
															.get(j).content;
												}
												if (mDetailsCallCommentInfo.data
														.get(j).nickname != null) {
													commentInfo.nickname = mDetailsCallCommentInfo.data
															.get(j).nickname;
												}
												if (mDetailsCallCommentInfo.data
														.get(j).is_anonymous != null) {
													commentInfo.is_anonymous = mDetailsCallCommentInfo.data
															.get(j).is_anonymous;
												}
												if (mDetailsCallCommentInfo.data
														.get(j).addtime != null) {
													commentInfo.addtime = mDetailsCallCommentInfo.data
															.get(j).addtime;
												}
												if (!Utils
														.isStringEmpty(mDetailsCallCommentInfo.data
																.get(j).face)) {
													commentInfo.face = URLs.IMG_PRE
															+ mDetailsCallCommentInfo.data
																	.get(j).face;
												}

												mDatas.add(commentInfo);

											}

											if (mDatas.size() > 0) {
												mXListView
														.setVisibility(View.VISIBLE);
												mLlEmpty.setVisibility(View.GONE);
												// 设定评论列表信息
												setShopCommentData();
											} else {
												mXListView
														.setVisibility(View.VISIBLE);
												mLlEmpty.setVisibility(View.GONE);
											}

										}
									}
									setShopCommentData();
								} else {
									showToast("您还没有点评店铺");
								}
							} else {
								showToast("店铺评论信息加载失败了，返回重试!");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void setShopCommentData() {
		mAdapter = new PersonShopCommentAdapter(PersonShopCommentActivity.this,
				mDatas);
		mXListView.setAdapter(mAdapter);

	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {
		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("评论");

		handler = new Handler();
		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_person_shop_comment_empty);
		mLlEmpty.setVisibility(View.GONE);
		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("你还没有点评商家\n\n去首页推荐看看吧");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(PersonShopCommentActivity.this,
						MainActivity.class));
				finish();
			}
		});

		// Xlistview
		this.mXListView = (XListView) findViewById(R.id.xlv_person_shop_comment);
		handler = new Handler();
		// 设置xlistview可以加载、刷新
		mXListView.setPullLoadEnable(false);
		// 设置xlistview可以刷新
		mXListView.setPullRefreshEnable(true);
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				CCLog.i("商铺点评Item Pos:", position + " ");
//				Intent intent = new Intent();
//				intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID,
//						mDatas.get(position - 1).id);
//				intent.setClass(PersonShopCommentActivity.this,
//						YouHuiDetailsActivity.class);
//				baseStartActivity(intent);

			}
		});
		mXListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				getShopCommentInfo();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mXListView.stopRefresh();
						showToast("刷新成功,数据已是最新");
					}
				}, 1000);
			}

			@Override
			public void onLoadMore() {
				getShopCommentInfo();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mXListView.stopLoadMore();
						mXListView.setPullLoadEnable(false);
						showToast("数据全部加载完毕，没有更多了");
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
			finish();
			break;
		case R.id.tv_usable_yaohe_comment:
			finish();
			break;
			
		default:
			break;
		}
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		LinearLayout ll_myyhq_used = (LinearLayout) findViewById(R.id.ll_shop_comment_root_);
		SupportDisplay.resetAllChildViewParam(ll_myyhq_used);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);
		TextView useable = (TextView) findViewById(R.id.tv_usable_yaohe_comment);
		useable.setOnClickListener(this);

	}
}