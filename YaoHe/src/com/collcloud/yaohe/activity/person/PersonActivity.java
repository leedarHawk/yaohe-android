package com.collcloud.yaohe.activity.person;

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
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.activity.updatepassword.UpdatePasswordActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SelectPicPopupWindow;
import com.collcloud.yaohe.upnickname.UpNicknameActivity;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.meg7.widget.CustomShapeImageView;

/**
 * @类说明 个人信息页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class PersonActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "PERINFO";
	/** 页面标题 */
	private TextView tv_actionbartitle;
	/** 页面返回 */
	private ImageView tv_actionbarback;
	/** 退出按钮 */
	private Button bt_person_exit;
	/** 账户名 */
	private TextView tv_person_accountnum;
	/** 昵称 */
	private TextView tv_person_nickname;
	/** 头像点击区域 */
	private RelativeLayout rl_img_person_face;
	/** 头像按钮 */
	private CustomShapeImageView im_per_photo;
	/** 头像uri */
	private File Aface;
	/** 上传头像进度条 */
	private Dialog per_mDialog;
	/** 修改密码区域 */
	private RelativeLayout rl_per_upPassword;
	/** 性别 */
	private RelativeLayout rl_img_person_sex;
	/** 性别显示 */
	private TextView tv_person_sex;
	/** 性别复选框 */
	boolean[] selected = new boolean[] { false, false };
	/** 性别 */
	String[] sex = new String[] { "男", "女" };
	/** 昵称 */
	private RelativeLayout rl_upnickname;
	private static ImageLoader mImageLoader;

	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		// 页面资源初始化
		intialSource();
		getUserBaseInfo();

	}

	/**
	 * 拍照图片处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		// 代表拍照
		case 1:
			switch (resultCode) {
			case Activity.RESULT_OK:// 照相完成点击确定
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {// 检测sd是否可用
					CCLog.v(TAG, "SD card is not avaiable/writeable right now");
					return;
				}
				Bundle bundle = data.getExtras();
				Bitmap bitmap = null;
				bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

				im_per_photo.setImageBitmap(bitmap);

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
					Aface = file;
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
			switch (resultCode) {
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
				im_per_photo.setImageBitmap(bitmap);// 当点击选择某张照片时进行显示
				CCLog.i("从相册选取图片路径",imgPath);
				CCLog.i("从相册选取图片文件名",imgName);
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

	/**
	 * 头像上传
	 */
	private void accessNetSend(File facePath) {

		progressbar(PersonActivity.this, R.layout.loading_progress);

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
						per_mDialog.dismiss();
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
							//AppApplacation.isReloadPersonDate = true;

						} else {// 返回提示信息

							CCLog.v(TAG, "头像上传时时出错啦");

							showToast(responseMsg);
						}
						per_mDialog.dismiss();
					}
				});
	}

	/**
	 * 页面上传
	 * */
	private void progressbar(Context context, int layout) {
		per_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		per_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		per_mDialog.setContentView(layout);
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		this.tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("个人信息");

		this.bt_person_exit = (Button) findViewById(R.id.bt_person_exit);
		bt_person_exit.setOnClickListener(this);

		this.tv_person_accountnum = (TextView) findViewById(R.id.tv_person_accountnum);

		this.tv_person_nickname = (TextView) findViewById(R.id.tv_person_nickname);

		rl_img_person_face = (RelativeLayout) findViewById(R.id.rl_img_person_face);
		rl_img_person_face.setOnClickListener(this);

		rl_per_upPassword = (RelativeLayout) findViewById(R.id.rl_per_upPassword);
		rl_per_upPassword.setOnClickListener(this);

		rl_img_person_sex = (RelativeLayout) findViewById(R.id.rl_img_person_sex);
		rl_img_person_sex.setOnClickListener(this);

		this.tv_person_sex = (TextView) findViewById(R.id.tv_person_sex);

		this.im_per_photo = (CustomShapeImageView) findViewById(R.id.im_person_photo);

		this.rl_upnickname = (RelativeLayout) findViewById(R.id.rl_upnickname);
		rl_upnickname.setOnClickListener(this);
		LinearLayout topCancel = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		topCancel.setOnClickListener(this);

	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			PersonActivity.this.finish();
			break;

		case R.id.rl_img_person_face:
		case R.id.im_person_photo:
			// 实例化SelectPicPopupWindow
			faceWindow = new SelectPicPopupWindow(PersonActivity.this,
					itemsOnClick);
			// 显示窗口
			faceWindow.showAtLocation(
					PersonActivity.this.findViewById(R.id.rl_img_person_face),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.bt_person_exit:
			showAlterExit();
			break;

		case R.id.rl_upnickname:
			baseStartActivity(new Intent(PersonActivity.this,
					UpNicknameActivity.class));
			break;

		case R.id.rl_img_person_sex:
			Intent intent = new Intent(PersonActivity.this,
					UpdataSexActivity.class);
			intent.putExtra(IntentKeyNames.KEY_NICKNAME,
					getSexType(Utils.strFromView(tv_person_sex)));
			baseStartActivity(intent);
			break;

		case R.id.rl_per_upPassword:
			baseStartActivity(new Intent(PersonActivity.this,
					UpdatePasswordActivity.class));
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

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		getUserBaseInfo();
		CCLog.i("PersonActivity onNewIntent");
	}

	/**
	 * 显示性别
	 * */
	private void showSex() {
		new AlertDialog.Builder(this).setTitle("性别")
				.setMultiChoiceItems(sex, selected, new OnMultiChoiceListen())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						String selectedStr = "";
						for (int i = 0; i < selected.length; i++) {

							if (selected[i] == true) {

								selectedStr = sex[i];
								break;
							}

						}

						tv_person_sex.setText(selectedStr);

						arg0.dismiss();
					}
				}).show();

	}

	/**
	 * 复选框监听器
	 * */
	class OnMultiChoiceListen implements
			DialogInterface.OnMultiChoiceClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1, boolean arg2) {

			selected[arg1] = arg2;
		}

	}

	/**
	 * 调用系统相机的方法
	 * */
	public void photo() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 1);
		} else {
			showToast("SD卡不可用，无法拍照。");
		}

	}

	/**
	 * 页面重新计算
	 */
	@Override
	protected void resetLayout() {
		// 页面适配
		RelativeLayout rel_perinfo = (RelativeLayout) findViewById(R.id.rl_perinfo_root);
		SupportDisplay.resetAllChildViewParam(rel_perinfo);
	}

	/**
	 * 退出提示
	 */
	private void showAlterExit() {

		AlertDialog.Builder builder = new Builder(PersonActivity.this);
		builder.setMessage("是否退出登录");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// 在杀死进程之前将登陆状态设置为未登录 &清除密码
				mLoginDataManager.setUserPassword("");// 清除密码
				mLoginDataManager.setLoginState("0");// 登录状态设置为 未登录
				mLoginDataManager.setUserType("0"); // 清除默认状态为个人
				mLoginDataManager.setBusinessState("0");// 清除默认状态,底部导航判断为个人显示
				mLoginDataManager.setMemberId(""); //清除memberid号
				// mApplication.finishAll();
				Intent intent = new Intent();
				intent.setClass(PersonActivity.this, MineActivity.class);
				baseStartActivity(intent);
				PersonActivity.this.finish();
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

	/**
	 * 获取个人基本信息
	 */
	private void getUserBaseInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.USER_BASE_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("个人基本详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject.has("login_user")) {
									tv_person_accountnum.setText(dataObject
											.optString("login_user"));
								}
								if (dataObject.has("nickname")) {
									String nickName = dataObject
											.optString("nickname");
									if (Utils.isStringEmpty(nickName)
											|| nickName.equals("null")) {
										tv_person_nickname.setText("无昵称");
									} else {
										tv_person_nickname.setText(nickName);
									}
									Staticvalue.sNickName = Utils
											.strFromView(tv_person_nickname);

								}
								if (dataObject.has("face")) {
									if (!Utils.isStringEmpty(dataObject
											.optString("face"))) {
										String url = URLs.IMG_PRE
												+ dataObject.optString("face");
										ImageListener listener = ImageLoader
												.getImageListener(
														im_per_photo,
														R.drawable.icon_yaohe_default_logo,
														R.drawable.icon_yaohe_default_logo);
										mImageLoader.get(url, listener, getResources().getDimensionPixelSize(R.dimen.photo_width), getResources().getDimensionPixelSize(R.dimen.photo_height));
									}
								}
								if (dataObject.has("sex")) {
									tv_person_sex.setText(setSexInfo(dataObject
											.optString("sex")));
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

	private String setSexInfo(String sex) {
		String sexStr = "男";
		if (!Utils.isStringEmpty(sex)) {
			if (sex.equals(GlobalConstant.VALID_VALUE)) {
				sexStr = "女";
			} else if (sex.equals(GlobalConstant.INVALID_VALUE)) {
				sexStr = "男";
			}
		}
		return sexStr;
	}

	private String getSexType(String sex) {

		String sexType = GlobalConstant.INVALID_VALUE;
		if (!Utils.isStringEmpty(sex)) {
			if (sex.equals("男")) {
				sexType = GlobalConstant.INVALID_VALUE;
			} else if (sex.equals("女")) {
				sexType = GlobalConstant.VALID_VALUE;
			}
		}
		return sexType;
	}
}
