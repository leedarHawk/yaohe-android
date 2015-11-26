package com.collcloud.yaohe.activity.businessinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.activity.updatepassword.UpdatePasswordActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SelectPicPopupWindow;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.meg7.widget.CustomShapeImageView;

/**
 * @类说明 我的(店铺信息)页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class BusinessInfoActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "SHOPINFO";
	/** 标题信息 */
	private TextView tv_business_info;
	/** 隐藏的按钮 */
	private TextView tv_business_info_hide;
	/** 返回按钮 */
	ImageView im_business_info_back;
	/** 头像按钮 */
	CustomShapeImageView im_business_photo;
	/** 头像uri */
	// private File Aface;
	/** 头像点击区域 */
	private RelativeLayout rl_img_business_face;
	/** 退出按钮 */
	private Button bt_business_exit;
	/** 更改密码区域 */
	private RelativeLayout rl_business_password;
	/** 商家信息 */
	private RelativeLayout rl_activity_shop_info;
	/** 上传头像进度条 */
	private Dialog busin_mDialog;
	/** 用户手机号 */
	private TextView tv_business_accountnum;
	/** 商家名称 */
	private TextView mTvBusinessName;
	private static ImageLoader mImageLoader;
	private String mStrShopID;
	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_info);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());

		intialSource();
		getShopBaseInfo();
	}

	/**
	 * 获取个人基本信息
	 */
	private void getShopBaseInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SHOP_BASE_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("商家基本详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject.has("title")) {
									mTvBusinessName.setText(dataObject
											.optString("title"));
								}
								if (dataObject.has("shop_id")) {
									mStrShopID = dataObject
											.optString("shop_id");
								}
								if (dataObject.has("face")) {
									if (!Utils.isStringEmpty(dataObject
											.optString("face"))) {

										String url = URLs.IMG_PRE
												+ dataObject.optString("face");
										ImageListener listener = ImageLoader
												.getImageListener(
														im_business_photo,
														R.drawable.icon_yaohe_default_logo,
														R.drawable.icon_yaohe_default_logo);
										mImageLoader.get(url, listener, getResources().getDimensionPixelSize(R.dimen.photo_width), getResources().getDimensionPixelSize(R.dimen.photo_height));
									}
								}

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (msg != null) {
							CCLog.e("个人信息获取异常");
						}
					}
				});

	}

	/**
	 * 照片处理
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {

		// 拍照&相册
		super.onActivityResult(arg0, arg1, data);

		switch (arg0) {
		// 代表拍照
		case 1:
			switch (arg1) {
			case Activity.RESULT_OK:// 照相完成点击确定
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {// 检测sd是否可用
					CCLog.v(TAG, "SD card is not avaiable/writeable right now");
					return;
				}
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

				im_business_photo.setImageBitmap(bitmap);
				FileOutputStream fos = null;
				File appDir = new File(
						Environment.getExternalStorageDirectory(), "yaohe");
				if (!appDir.exists()) {
					appDir.mkdir();
				}
				String fileName = System.currentTimeMillis() + ".jpg";
				File file = new File(appDir, fileName);
				try {

					CCLog.v(TAG, "图片还没有写入到yaohe文件夹");
					fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件
					CCLog.v(TAG, "图片已经写入到yaohe文件夹");

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					CCLog.v(TAG, "图片已经写入到yaohe文件夹遇到异常");

				} finally {
					try {
						fos.flush();
						fos.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// 将图片插入到系统图库
				try {
					MediaStore.Images.Media.insertImage(
							this.getContentResolver(), file.getAbsolutePath(),
							fileName, null);

					// 给图片地址赋值
					// Aface = file;
					// 将照片上传
					accessNetSend(file);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// 通知图库进行更新
				this.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(new File(fileName))));
				break;

			case Activity.RESULT_CANCELED:// 取消按钮
				break;

			default:
				break;
			}
			break;

		// 代表从图册中进行选择图片
		case 2:
			switch (arg1) {

			case Activity.RESULT_OK:
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null,
						null, null);
				cursor.moveToFirst();
				String imgNo = cursor.getString(0); // 图片编号
				String imgPath = cursor.getString(1); // 图片文件路径
				String imgSize = cursor.getString(2); // 图片大小
				String imgName = cursor.getString(3); // 图片文件名
				cursor.close();
				Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
				im_business_photo.setImageBitmap(bitmap);// 当点击选择某张照片时进行显示
				CCLog.i("从相册选取图片路径", imgPath);
				CCLog.i("从相册选取图片文件名", imgName);
				// 将照片上传
				if (!Utils.isStringEmpty(imgPath)) {
					accessNetSend(new File(imgPath));
				}
				break;
			case Activity.RESULT_CANCELED:// 取消
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		getShopBaseInfo();
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {
		tv_business_info_hide = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		tv_business_info_hide.setText("");

		tv_business_accountnum = (TextView) findViewById(R.id.tv_business_accountnum);
		mTvBusinessName = (TextView) findViewById(R.id.tv_business_shop_name);
		tv_business_accountnum.setText(mLoginDataManager.getUserPhone());

		tv_business_info = (TextView) findViewById(R.id.my_login_tv_top_title);
		tv_business_info.setText("商家信息");

		im_business_info_back = (ImageView) findViewById(R.id.my_login_im_top_back);
		im_business_info_back.setOnClickListener(this);

		im_business_photo = (CustomShapeImageView) findViewById(R.id.im_business_photo);
		im_business_photo.setOnClickListener(this);

		rl_img_business_face = (RelativeLayout) findViewById(R.id.rl_img_business_face);
		rl_img_business_face.setOnClickListener(this);

		bt_business_exit = (Button) findViewById(R.id.bt_business_exit);
		bt_business_exit.setOnClickListener(this);

		rl_business_password = (RelativeLayout) findViewById(R.id.rl_business_password);
		rl_business_password.setOnClickListener(this);
		rl_activity_shop_info = (RelativeLayout) findViewById(R.id.rl_activity_shop_info);
		rl_activity_shop_info.setOnClickListener(this);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_top_back);
		layout.setOnClickListener(this);
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_top_back:
			BusinessInfoActivity.this.finish();
			break;
		case R.id.rl_img_business_face:
		case R.id.im_business_photo:
			// 实例化SelectPicPopupWindow
			faceWindow = new SelectPicPopupWindow(BusinessInfoActivity.this,
					itemsOnClick);
			// 显示窗口
			faceWindow.showAtLocation(BusinessInfoActivity.this
					.findViewById(R.id.rl_img_business_face), Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.bt_business_exit:
			showAlterExit();
			break;
		case R.id.rl_business_password:
			baseStartActivity(new Intent(BusinessInfoActivity.this,
					UpdatePasswordActivity.class));
			break;
		case R.id.rl_activity_shop_info: // 商家信息
			Intent intent = new Intent(BusinessInfoActivity.this,
					DetailsBusinessInfoActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID,
					mLoginDataManager.getMemberId());
			baseStartActivity(intent);
			break;
		default:
			break;
		}
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			faceWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				photo();
				break;
			case R.id.btn_pick_photo:
				choiceImg();
				break;
			default:
				break;
			}
		}
	};

	private void choiceImg() {
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType("image/*");
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		/* 取得相片后返回本画面 */
		startActivityForResult(intent, 2);
	}

	/**
	 * 头像上传
	 */
	private void accessNetSend(File facePath) {

		progressbar(BusinessInfoActivity.this, R.layout.loading_progress);

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 用户头像
		CCLog.v(TAG, "用户头像>>>>>" + facePath.toString());
		params.addBodyParameter("face", facePath);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.FACE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络发送失败");
						busin_mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络上传头像数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
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

						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (code.equals("0")) {// 请求成功

							showToast("您的头像已上传成功");

						} else {// 返回提示信息

							CCLog.v(TAG, "头像上传时时出错啦");

							showToast(responseMsg);
						}
						busin_mDialog.dismiss();
					}
				});
	}

	/**
	 * 调用系统相机的方法
	 * */
	public void photo() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		startActivityForResult(intent, 1);
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		busin_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		busin_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		busin_mDialog.setContentView(layout);
	}

	/**
	 * UI界面适配
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_mydpxx = (RelativeLayout) findViewById(R.id.rl_shopinfo_root);
		SupportDisplay.resetAllChildViewParam(rel_mydpxx);
	}

	/**
	 * 用户退出按钮
	 */
	private void showAlterExit() {

		AlertDialog.Builder builder = new Builder(BusinessInfoActivity.this);
		builder.setMessage("是否退出登录");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// SaveLoginstate.clearPasswd(getApplicationContext());
				// 在杀死进程之前将登陆状态设置为未登录 &清除密码
				mLoginDataManager.setLoginState("0");
				mLoginDataManager.setUserPassword("");
				mLoginDataManager.setUserType("0");
				mLoginDataManager.setBusinessState("0");
				mLoginDataManager.setMemberId("");

				// mApplication.finishAll();
				Intent intent = new Intent();
				intent.setClass(BusinessInfoActivity.this, MineActivity.class);
				baseStartActivity(intent);
				BusinessInfoActivity.this.finish();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();

	}
}
