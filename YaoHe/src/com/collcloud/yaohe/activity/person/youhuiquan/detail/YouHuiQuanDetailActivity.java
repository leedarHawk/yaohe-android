package com.collcloud.yaohe.activity.person.youhuiquan.detail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.UseTimeList;
import com.collcloud.yaohe.ui.adapter.YHQDetailUseTimeAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

/**
 * @类说明 我的(个人)优惠券详情页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class YouHuiQuanDetailActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "MYH";
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	/** 返回按钮 */
	ImageView iv_top_menu_back;
	/** 标题信息 */
	TextView tv_commonn_title_menu_text;
	/** 分享按钮 */
	ImageView iv_top_title_menu_share;
	/** 消费按钮 */
	// 消费写陈了shoucang
	ImageView iv_top_title_menu_shoucang;
	/** 数据加载进度条 */
	private Dialog mDialog;
	/** 使用时间数据 */
	private UseTimeList usertimeBean;
	/** 获取的优惠券标题 */
	private String title;// 优惠券标题
	/** 获取的优惠券内容 */
	private String content;// 优惠券内容
	/** 获取的优惠券图片地址1 */
	private String img1;// 优惠券图片地址1
	/** 获取的优惠券卡号 */
	private String card_number;// 优惠券卡号
	/** 获取的优惠券店铺名 */
	private String shop_name = "";// 开始日期
	/** 获取的优惠券结束日期 */
	private String valid_end = "";// 结束日期
	/** 店铺名称 */
	private TextView tv_yhq_shop_name;
	/** 优惠信息 */
	private TextView tv_yhq_info;
	/** 优惠码 */
	private TextView tv_yhq_yhm_num;
	/** 有效期 */
	private TextView tv_yhq_yxq_times;
	/** 优惠券图片 */
	private ImageView im_yhq_shop_photo;
	private String mStrCouponID;
	private String mStrMemberCouponID;

	/** 底部使用次数listview */
	private ListView lv_usetime_list;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions Detail_options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_you_hui_quan_detail);
		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		progressbar(YouHuiQuanDetailActivity.this, R.layout.loading_progress);

		mStrCouponID = getStringExtra("useableCouponID");
		mStrMemberCouponID = getStringExtra("useableMemberCouponID");

		intialSource();
		accessNetGetYhqDetail(mStrCouponID, mStrMemberCouponID);

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
	 * 获取不可使用的优惠券数据
	 */
	private void accessNetGetYhqDetail(String coupon_id, String member_coupon_id) {

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 选中优惠券ID
		CCLog.v(TAG, "选中优惠券ID>>>>>" + coupon_id);
		params.addBodyParameter("coupon_id", coupon_id);
		// 选中优惠券对应商家ID
		CCLog.v(TAG, "该优惠券的发放者ID>>>>>" + member_coupon_id);
		params.addBodyParameter("member_coupon_id", member_coupon_id);

		http.send(HttpRequest.HttpMethod.GET, ContantsValues.BKSYYH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						mDialog.dismiss();
						showToast("获取优惠信息失败，请返回重试。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						mDialog.dismiss();
						CCLog.v(TAG, "网络获取优惠券数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;

						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络返回data数据
						String responseData = "";

						// 网络访问商圈状态码
						String code = "";
						// 网络商圈返回消息
						String responseMsg = "";
						try {

							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {
								String responseCoupon = null;
								responseData = object.getString("data");// 包括："coupon":
																		// "list":
								JSONObject object3, object4;
								object3 = new JSONObject(responseData);
								responseCoupon = object3.getString("coupon");
								object4 = new JSONObject(responseCoupon);

								title = object4.optString("title");
								tv_yhq_info.setText(title);

								shop_name = object4.optString("shop_name");
								tv_yhq_shop_name.setText(shop_name);

								if (!Utils.isStringEmpty(object4
										.optString("img1"))) {
									img1 = object4.optString("img1");
									imageLoader.displayImage(URLs.IMG_PRE
											+ img1, im_yhq_shop_photo,
											Detail_options,
											animateFirstListener);
								}

								valid_end = object4.optString("valid_end");
								tv_yhq_yxq_times.setText(valid_end);

								card_number = object4.optString("card_number");
								tv_yhq_yhm_num.setText(card_number);

								JSONArray jsoArray = object3
										.optJSONArray("list");
								final int itemCount = jsoArray.length();

								if (itemCount > 1) {
									/** 关注商家数据集合 */
									ArrayList<UseTimeList> usetimeList = new ArrayList<UseTimeList>();
									for (int i = 0; i < itemCount; i++) {
										UseTimeList timeItem = new UseTimeList();
										JSONObject scObject = jsoArray
												.getJSONObject(i);

										timeItem.id = scObject.optString("id");

										timeItem.ply_time = scObject
												.optString("ply_time");

										usetimeList.add(timeItem);
									}

									// 底部使用次数适配器
									lv_usetime_list
											.setAdapter(new YHQDetailUseTimeAdapter(
													YouHuiQuanDetailActivity.this,
													usetimeList));

								} else {
									showToast("该优惠券没有使用记录");
								}

							} else {
								showToast(responseMsg);
							}

						} catch (JSONException e) {

							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 页面资源初始化
	 */
	private void intialSource() {
		// 返回
		this.iv_top_menu_back = (ImageView) findViewById(R.id.iv_top_menu_back);
		LinearLayout llTopBack = (LinearLayout) findViewById(R.id.ll_top_back);
		llTopBack.setOnClickListener(this);

		// 标题
		this.tv_commonn_title_menu_text = (TextView) findViewById(R.id.tv_commonn_title_menu_text);
		tv_commonn_title_menu_text.setText("优惠券详情");
		// 分享
		// this.iv_top_title_menu_share = (ImageView)
		// findViewById(R.id.iv_top_title_menu_share);
		// iv_top_title_menu_share.setVisibility(View.GONE);
		// // 消费写陈了shoucang
		// this.iv_top_title_menu_shoucang = (ImageView)
		// findViewById(R.id.iv_top_title_menu_shoucang);
		// iv_top_title_menu_shoucang.setOnClickListener(this);

		this.lv_usetime_list = (ListView) findViewById(R.id.lv_usetime_list);
		this.tv_yhq_shop_name = (TextView) findViewById(R.id.tv_yhq_shop_name);
		this.tv_yhq_info = (TextView) findViewById(R.id.tv_yhq_info);
		this.tv_yhq_yhm_num = (TextView) findViewById(R.id.tv_yhq_yhm_num);
		this.tv_yhq_yxq_times = (TextView) findViewById(R.id.tv_yhq_yxq_times);
		this.im_yhq_shop_photo = (ImageView) findViewById(R.id.im_yhq_shop_photo);

		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		Detail_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_yaohe_loading_default)
				.showImageForEmptyUri(R.drawable.icon_yaohe_loading_default)
				.showImageOnFail(R.drawable.icon_yaohe_loading_default)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();

	}

	/**
	 * @类说明 图片加载
	 * @author 赵洋洋
	 * 
	 */
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	/**
	 * 页面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.ll_top_back:
			YouHuiQuanDetailActivity.this.finish();
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
		RelativeLayout rel_myyhq_detail = (RelativeLayout) findViewById(R.id.rl_yhq_detail_root);
		SupportDisplay.resetAllChildViewParam(rel_myyhq_detail);
	}

	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(
							testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}
}
