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
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.PersonPinglunAdapter;
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

public class PersonPingLunActivity extends BaseActivity implements
		OnClickListener {

	/** 页面标题 */
	private TextView tv_actionbartitle;

	/** 店铺点评 */
	private TextView mTvShopComment;
	/** 数据加载进度条 */
	private Dialog mDialog;

	private XListView mXListView;
	private int mTotalCount = 0;

	private ArrayList<CallCommentInfo> mDatas = new ArrayList<CallCommentInfo>();
	private PersonPinglunAdapter mAdapter;
	private DetailsCallCommentInfo mDetailsCallCommentInfo;

	// ******** Tips *******//
	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_ping_lun);
		// 初始化页面资源
		intialSource();
		getYaoeDianping();
	}

	/**
	 * 获取吆喝点评信息
	 */
	private void getYaoeDianping() {
		progressbar(PersonPingLunActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(GlobalConstant.TAG,
				"当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.MY_YAOHE_COMMENT,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						mDialog.dismiss();
						showToast("数据加载失败了。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						mDialog.dismiss();
						CCLog.v(GlobalConstant.TAG, "网络获取吆喝点评数据成功");
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
												setYaoheCommentData();
											} else {
												mXListView
														.setVisibility(View.VISIBLE);
												mLlEmpty.setVisibility(View.GONE);
											}

										}
									}
									setYaoheCommentData();
								} else {
									showToast("您还没有点评吆喝");
								}
							} else {
								showToast("吆喝评论信息加载失败了，返回重试!");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});
	}

	private void setYaoheCommentData() {
		mAdapter = new PersonPinglunAdapter(PersonPingLunActivity.this, mDatas);
		mXListView.setAdapter(mAdapter);

	}

	private void intialSource() {
		LinearLayout topBack = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		topBack.setOnClickListener(this);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("评论");

		this.mTvShopComment = (TextView) findViewById(R.id.tv_person_shop_pinlun);
		mTvShopComment.setOnClickListener(this);

		// Xlistview
		this.mXListView = (XListView) findViewById(R.id.xlv_person_yaohe_pinlun);
		handler = new Handler();
		// 设置xlistview可以加载、刷新
		mXListView.setPullLoadEnable(false);
		// 设置xlistview可以刷新
		mXListView.setPullRefreshEnable(true);
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				CCLog.i("吆喝点评Item Pos:", position + " ");
//				Intent intent = new Intent();
				// intent.setClass(UsableYouHuiQuanActivity.this,
				// YouHuiQuanDetailActivity.class);
//				intent.setClass(PersonPingLunActivity.this,
//						YouHuiDetailsActivity.class);
//				baseStartActivity(intent);

			}
		});
		mXListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				getYaoeDianping();
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
				getYaoeDianping();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						mXListView.stopLoadMore();
						mXListView.setPullLoadEnable(false);
						showToast("数据全部加载完毕，没有更多了。");
					}
				}, 1000);
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_tv_actionbarback:
			finish();
			break;
		case R.id.tv_person_shop_pinlun:
			baseStartActivity(new Intent(PersonPingLunActivity.this,
					PersonShopCommentActivity.class));
			//finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void resetLayout() {

		LinearLayout ll_myyhq_usable = (LinearLayout) findViewById(R.id.ll_psg_yaohe_dianping_root);
		SupportDisplay.resetAllChildViewParam(ll_myyhq_usable);

		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_person_pinlun_empty);
		mLlEmpty.setVisibility(View.GONE);

		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("没有相关吆喝的点评\n\n去首页推荐看看吧");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(PersonPingLunActivity.this,
						MainActivity.class));
				finish();
			}
		});

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
}
