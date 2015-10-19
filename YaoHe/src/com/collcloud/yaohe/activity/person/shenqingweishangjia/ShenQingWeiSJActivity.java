package com.collcloud.yaohe.activity.person.shenqingweishangjia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.lbs.LBSCityActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.IndustryType;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.adapter.GroupAdapterIndustry;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SelectPicPopupWindow;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 个人申请为商家
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
@SuppressLint("InflateParams")
public class ShenQingWeiSJActivity extends BaseActivity implements
		OnClickListener, AMapLocationListener {

	private static final String TAG = "申请商家Activity";
	/** 商家所在城市 */
	private TextView edt_sqwsj_city;
	/** 返回按钮 */
	private ImageView tv_actionbarback;
	/** 标题信息 */
	private TextView tv_actionbartitle;
	/** 上下滑动 */
	private ScrollView sv_sqwsj_info;
	/** 商家名称 */
	private EditText edt_sqwsj_shop_name;
	/** 行业分类 */
	private TextView edt_sqwsj_shop_hy;
	/** 所属商圈 */
	private TextView edt_sqwsj_shop_sq;
	/** 详细地址 */
	private EditText edt_sqwsj_shop_address;
	/** 营业时间 */
	// private EditText edt_sqwsj_shop_yytime;
	/** 预约电话 */
	private EditText edt_sqwsj_shop_yyphone;
	/** 店铺介绍 */
	private EditText edt_sqwsj_shop_js;
	/** 店主名 */
	private EditText edt_sqwsj_shop_host_name;
	/** 法人信息 */
	private ImageView im_sqwsj_frxx;
	/** 营业执照 */
	private ImageView im_sqwsj_yyzz;
	/** 提交按钮 */
	private Button my_sqwsj_businessinfo_submit;
	/** 上传进度条 */
	private Dialog submit_mDialog;
	/** 营业执照赋值区分标志 */
	private Boolean imFlagYingYeZhiZhao = true;
	/** 法人信息赋值区分标志 */
	private Boolean imFlagFaRenInfo = true;
	/** 法人信息赋值file区分标志 */
	private Boolean imFlagFaRenInfoFile = true;
	/** 营业执照赋值file区分标志 */
	private Boolean imFlagYingYeZhiZhaoFile = true;
	/** 法人图片uri */
	private File img_Fr;
	/** 营业执照uri */
	private File img_Yy;
	/** popup区域 */
	private PopupWindow popupWindowIndustryType;
	/** popup区域列表 */
	private ListView lv_group_industry;
	/** popup区域视图 */
	private View view_indusry;
	/** 选中行业一级分类id */
	private String selectedIndusOneId;
	private LocationManagerProxy mLocationManagerProxy;
	/** 时间选择器 */
	private TimePicker mTimePicker;
	/** 时间选择器确认按钮 */
	private Button mBtnTimerOK;
	/** 开始日期 */
	private TextView tv_time1;
	/** 结束日期 */
	private TextView tv_time2;

	RelativeLayout layout_;

	private String mStrCity;
	private String mStrCityId;
	private String mStrDistrict;
	private double mLatitude;
	private double mLongitude;

	// ****** 选择分类 传回的信息 *********//
	private String oneClassID = "23";
	private String twoClassID = "24";
	private String oneClassName = "";
	private String twoClassName = "";
	// ****** 选择分类 传回的信息 *********//
	private String areaID = "";
	private String districtID = "";
	private String areaName = "";
	private String districtName = "";
	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shen_qing_wei_sj);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		intialSource();
		lbsCity();

		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 屏幕宽度
		Staticvalue.WIDTH = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		Staticvalue.HEIGHT = windowManager.getDefaultDisplay().getHeight();

	}

	private void lbsCity() {
		// 初始化定位，采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(true);
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	/**
	 * UI界面输入检验
	 */
	public void verifyInput() {

		if (TextUtils.isEmpty(edt_sqwsj_city.getText().toString())) {
			showToast("店铺所在城市不能为空");
			return;
		}
		if (TextUtils.isEmpty(edt_sqwsj_shop_name.getText().toString())) {
			showToast("店铺名称不能为空");
			return;
		}
		if (edt_sqwsj_shop_name.getText().toString().length() > 36) {
			showToast("店铺名称不能多于18个字");
			return;
		}

		if (TextUtils.isEmpty(edt_sqwsj_shop_hy.getText().toString())) {
			showToast("行业分类不能为空");
			return;
		}
		if (TextUtils.isEmpty(edt_sqwsj_shop_sq.getText().toString())) {
			showToast("所属商圈不能为空");
			return;
		}
		if (TextUtils.isEmpty(edt_sqwsj_shop_address.getText().toString())) {
			showToast("详细地址不能为空");
			return;
		}
		if (TextUtils.isEmpty(edt_sqwsj_shop_yyphone.getText().toString())) {
			showToast("预约电话不能为空");
			return;
		}
//		if (TextUtils.isEmpty(edt_sqwsj_shop_js.getText().toString())) {
//			showToast("店铺介绍不能为空");
//			return;
//		}
//		if (img_Fr == null) {
//			showToast("请拍摄法人照片!");
//			return;
//		}
//		if (img_Yy == null) {
//			showToast("请拍摄营业执照!");
//			return;
//		}

		progressbar(this, R.layout.loading_progress);
		accessNet();
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

		// 返回按钮
		case R.id.ll_tv_actionbarback:
			dialog();
			break;
		// 选择城市
		case R.id.edt_sqwsj_city:
			Intent lbsIntent = new Intent(ShenQingWeiSJActivity.this,
					LBSCityActivity.class);
			lbsIntent.putExtra("shenqing", "shenqing");
			startActivityForResult(lbsIntent, 10);
			break;

		// 点击法人信息进行拍照
		case R.id.im_sqwsj_frxx:

			// 实例化SelectPicPopupWindow
			faceWindow = new SelectPicPopupWindow(ShenQingWeiSJActivity.this,
					itemsOnClick);
			// 显示窗口
			faceWindow
					.showAtLocation(ShenQingWeiSJActivity.this
							.findViewById(R.id.rl_sqwsj_root), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
			imFlagFaRenInfo = false;
			imFlagFaRenInfoFile = false;
			// photo();
			break;

		// 点击营业执照进行拍照
		case R.id.im_sqwsj_yyzz:
			// 实例化SelectPicPopupWindow
			faceWindow = new SelectPicPopupWindow(ShenQingWeiSJActivity.this,
					itemsOnClick);
			// 显示窗口
			faceWindow
					.showAtLocation(ShenQingWeiSJActivity.this
							.findViewById(R.id.rl_sqwsj_root), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
			imFlagYingYeZhiZhao = false;
			imFlagYingYeZhiZhaoFile = false;
			// photo();
			break;

		// 点击提交按钮
		case R.id.my_sqwsj_businessinfo_submit:
			verifyInput();
			break;

		// 点击选择商圈
		case R.id.edt_sqwsj_shop_sq_:

			if (Utils.isStringEmpty(Utils.strFromView(edt_sqwsj_city))) {
				showToast("请您先选择城市，然后可查看所在商圈列表");
				return;
			}

			Intent districtIntent = new Intent(ShenQingWeiSJActivity.this,
					ChoiceDistrictActivity.class);
			districtIntent.putExtra("current_city_id", mStrCityId);
			startActivityForResult(districtIntent, 10);
			break;

		// 点击选择行业分类
		case R.id.edt_sqwsj_shop_hy:
			Intent intent = new Intent(ShenQingWeiSJActivity.this,
					ChoiceClassifyActivity.class);
			startActivityForResult(intent, 10);
			break;

		// 点击开始时间
		case R.id.tv_time1:
			dialogTimePicker1();
			break;

		// 点击结束时间
		case R.id.tv_time2:
			dialogTimePicker2();
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
	 * 访问网络申请为商家
	 */
	private void accessNet() {

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 商铺所在城市ID
		CCLog.v(TAG, "所在城市的ID>>>>>" + mStrCityId);
		params.addBodyParameter("city_id", mStrCityId);
		// 店铺名
		CCLog.v(TAG, "店铺名>>>>>" + edt_sqwsj_shop_name.getText().toString());
		params.addBodyParameter("title", edt_sqwsj_shop_name.getText()
				.toString());
		// 一级分类
		CCLog.v(TAG, "一级分类>>>>>" + oneClassID);
		params.addBodyParameter("one_id", oneClassID);
		// 二级分类
		CCLog.v(TAG, "二级分类>>>>>" + twoClassID);
		params.addBodyParameter("industry_class_id", twoClassID);
		// 区域id
		CCLog.v(TAG, "区域ID>>>>>" + areaID);
		params.addBodyParameter("area_id", areaID);
		// 商圈id
		CCLog.v(TAG, "商圈ID>>>>>" + districtID);
		params.addBodyParameter("district_id", districtID);
		// 详细地址
		CCLog.v(TAG, "详细地址>>>>>" + edt_sqwsj_shop_address.getText().toString());
		params.addBodyParameter("address", edt_sqwsj_shop_address.getText()
				.toString());
		// 经度
		CCLog.v(TAG, "经度>>>>>" + String.valueOf(mLongitude));
		params.addBodyParameter("_long", String.valueOf(mLongitude));
		// 纬度
		CCLog.v(TAG, "纬度>>>>>" + String.valueOf(mLatitude));
		params.addBodyParameter("lat", String.valueOf(mLatitude));
		// 营业时间
		params.addBodyParameter("business_time", tv_time1.getText().toString()
				+ " — " + tv_time2.getText());
		// 预约电话
		CCLog.v(TAG, "预约电话>>>>>" + edt_sqwsj_shop_yyphone.getText().toString());
		params.addBodyParameter("subscribe_tel", edt_sqwsj_shop_yyphone
				.getText().toString());
		// 店铺介绍
		if(edt_sqwsj_shop_js.getText() !=null) {
			CCLog.v(TAG, "店铺介绍>>>>>" + edt_sqwsj_shop_js.getText().toString());
			params.addBodyParameter("content", edt_sqwsj_shop_js.getText()
					.toString());
		}
		
		// 店主名
		if(edt_sqwsj_shop_host_name.getText() != null) {
			CCLog.v(TAG, "店主名>>>>>" + edt_sqwsj_shop_host_name.getText().toString());
			params.addBodyParameter("shopkeeper_name", edt_sqwsj_shop_host_name
					.getText().toString());
		}
		
		// 法人信息
		if(img_Fr !=null) {
			CCLog.v(TAG, "法人信息>>>>>" + img_Fr.toString());
			params.addBodyParameter("legal_person_card", img_Fr);
		}
		
		// 营业执照
		if(img_Yy !=null) {
			CCLog.v(TAG, "营业执照>>>>>" + img_Yy.toString());
			params.addBodyParameter("business_licence", img_Yy);
		}
		
		

		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.SHENGQINGSHANGJIA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络申请商家失败!");
						showToast("网络申请商家失败");
						submit_mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						submit_mDialog.dismiss();

						CCLog.v(TAG, "网络申请商家成功!");
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

							mLoginDataManager.setBusinessState("1");
							mLoginDataManager.setUserType("1");
							dialogSuccess();

							CCLog.v(TAG, "申请为商家成功.....");

						} else {// 返回提示信息

							CCLog.v(TAG, "申请为商家时出错啦");
							showToast(responseMsg);
						}

					}
				});
	}

	/**
	 * 弹出对话框
	 */
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(ShenQingWeiSJActivity.this);
		builder.setMessage("返回后本页提交信息将不会保存？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				baseStartActivity(new Intent(ShenQingWeiSJActivity.this,
						MineActivity.class));
				finish();
				arg0.dismiss();
			}

		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 弹出对话框
	 */
	protected void dialogSuccess() {

		AlertDialog.Builder builder = new Builder(ShenQingWeiSJActivity.this);
		builder.setMessage("您已成功申请为商家!");
		builder.setTitle("提示");

		builder.setNeutralButton("商家登录", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				baseStartActivity(new Intent(ShenQingWeiSJActivity.this,
						LoginActivity.class));
				finish();
				arg0.dismiss();
			}

		});

		builder.create().show();
	}

	/**
	 * 弹出时间选择对话框
	 */
	protected void dialogTimePicker1() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.timerpick);
		dialog.setCancelable(false);

		RelativeLayout dialogBackground = (RelativeLayout) dialog
				.findViewById(R.id.rl_timer_pick);
		SupportDisplay.resetAllChildViewParam(dialogBackground);

		mTimePicker = (TimePicker) dialog.findViewById(R.id.timePic_yysj);

		mBtnTimerOK = (Button) dialog.findViewById(R.id.btn_time_sure);
		// 是否使用24小时制
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(9);
		mTimePicker.setCurrentMinute(0);

		mTimePicker.setOnTimeChangedListener(mNullTimeChangedListener);

		mBtnTimerOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTimePicker.clearFocus();
				int h = mTimePicker.getCurrentHour();
				int m = mTimePicker.getCurrentMinute();
				String hour = "00";
				String minute = "00";
				if (h == 0) {
					hour = "00";
				} else {
					hour = format(h);
				}
				minute = format(m);
				tv_time1.setText(hour + ":" + minute);

				dialog.dismiss();

			}
		});

		dialog.show();

	}

	protected void dialogTimePicker2() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.timerpick);
		dialog.setCancelable(false);

		RelativeLayout dialogBackground = (RelativeLayout) dialog
				.findViewById(R.id.rl_timer_pick);
		SupportDisplay.resetAllChildViewParam(dialogBackground);

		mTimePicker = (TimePicker) dialog.findViewById(R.id.timePic_yysj);

		mBtnTimerOK = (Button) dialog.findViewById(R.id.btn_time_sure);
		// 是否使用24小时制
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(23);
		mTimePicker.setCurrentMinute(0);

		mTimePicker.setOnTimeChangedListener(mNullTimeChangedListener);

		mBtnTimerOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTimePicker.clearFocus();
				int h = mTimePicker.getCurrentHour();
				int m = mTimePicker.getCurrentMinute();
				String hour = "23";
				String minute = "00";
				if (h == 0) {
					hour = "24";
				} else {
					hour = format(h);
				}
				minute = format(m);
				tv_time2.setText(hour + ":" + minute);
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/* 日期时间显示两位数的方法 */
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private TimePicker.OnTimeChangedListener mStartTimeChangedListener = new TimePicker.OnTimeChangedListener() {

		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			updateDisplay(view, new Date(), hourOfDay, minute);
		}
	};
	private TimePicker.OnTimeChangedListener mNullTimeChangedListener = new TimePicker.OnTimeChangedListener() {

		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

		}
	};

	private void updateDisplay(TimePicker timePicker, Date date, int hourOfDay,
			int minute) {

		int nextMinute = 0;
		if (minute >= 45 && minute <= 59)
			nextMinute = 45;
		else if (minute >= 30)
			nextMinute = 30;
		else if (minute >= 15)
			nextMinute = 15;
		else if (minute > 0)
			nextMinute = 0;
		else {
			nextMinute = 45;
		}

		// remove ontimechangedlistener to prevent stackoverflow/infinite loop
		timePicker.setOnTimeChangedListener(mNullTimeChangedListener);

		// set minute
		timePicker.setCurrentMinute(nextMinute);

		// hook up ontimechangedlistener again
		timePicker.setOnTimeChangedListener(mStartTimeChangedListener);

		// update the date variable for use elsewhere in code
		date.setMinutes(nextMinute);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_sqwsj = (RelativeLayout) findViewById(R.id.rl_sqwsj_root);
		SupportDisplay.resetAllChildViewParam(rel_sqwsj);
		mBtnIsCancelButton = true;
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		submit_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		submit_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		submit_mDialog.setContentView(layout);
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
	 * 调用系统图册的方法
	 */
	public void picture(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 2);
	}

	/**
	 * popup显示行业分类
	 */
	@SuppressWarnings("deprecation")
	public void showWindowIndustryType(View v2, final List<IndustryType> data) {

		if (popupWindowIndustryType == null) {

			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view_indusry = layoutInflater.inflate(R.layout.popup_area_list,
					null);// 内部的主体view

			lv_group_industry = (ListView) view_indusry
					.findViewById(R.id.lvGroup_area);
			TextView title = (TextView) view_indusry
					.findViewById(R.id.groupAll_title);
			title.setText("选择行业分类");

			GroupAdapterIndustry groupAdapter2 = new GroupAdapterIndustry(
					ShenQingWeiSJActivity.this, data);
			lv_group_industry.setAdapter(groupAdapter2);// listview绑定适配器

			// 创建一个PopuWidow对象
			popupWindowIndustryType = new PopupWindow(view_indusry,
					Staticvalue.WIDTH * 2 / 3, Staticvalue.HEIGHT / 2);

		}
		// 使其聚集
		popupWindowIndustryType.setFocusable(true);

		// 设置允许在外点击消失
		popupWindowIndustryType.setOutsideTouchable(true);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindowIndustryType.setBackgroundDrawable(new BitmapDrawable());

		popupWindowIndustryType.showAsDropDown(v2, 0, 0);

		lv_group_industry.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (popupWindowIndustryType != null) {
					// 需要根据id找出内容
					selectedIndusOneId = data.get(position).getId();
					// findTextById(data.get(position).getId());
					edt_sqwsj_shop_hy.setText(data.get(position).getTitle());
					CCLog.v("选择行业分类", "选中分类ID ：" + selectedIndusOneId);
					popupWindowIndustryType.dismiss();
				}
			}
		});
	}

	/**
	 * popup显示商圈
	 */

	/**
	 * 行业分类解析
	 */
	public ArrayList<IndustryType> parseJSONObject(JSONArray response) {

		ArrayList<IndustryType> industrytypes = new ArrayList<IndustryType>();

		IndustryType industrytype = null;

		for (int i = 0; i < response.length(); i++) {
			industrytype = new IndustryType();
			JSONObject object = null;
			try {
				object = response.getJSONObject(i);
				industrytype.setId(object.getString("id"));
				industrytype.setTitle(object.getString("title"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			industrytypes.add(industrytype);
		}
		return industrytypes;
	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			String desc = "";
			Bundle locBundle = amapLocation.getExtras();
			// 定位成功回调信息，设置相关消息
			if (amapLocation.getCity() == null) {
			} else {
				edt_sqwsj_city.setText(amapLocation.getCity());
				// TODO 第二种从本地db处理反转城市id
				if (!Utils
						.isStringEmpty(Utils.resetCity(amapLocation.getCity()))) {
					mStrCityId = AppApplacation.queryNativeCityID(Utils
							.resetCity(amapLocation.getCity()));
					// getsAreaList(mStrCityId);
				}
			}

			if (amapLocation.getLongitude() != 0) {
				mLongitude = amapLocation.getLongitude();
				CCLog.i("当前位置的 经度：", " " + amapLocation.getLongitude());
			} else {
				mLongitude = 117.191584;
			}

			if (amapLocation.getLatitude() != 0) {
				mLatitude = amapLocation.getLatitude();
				CCLog.i("当前位置的 纬度：", " " + amapLocation.getLatitude());
			} else {
				mLatitude = 39.067154;
			}
			if (amapLocation.getDistrict() != null) {
				mStrDistrict = Utils.resetDistrict(amapLocation.getDistrict());
				CCLog.i("当前位置对应的区域：", " " + amapLocation.getDistrict());
			}
			if (locBundle != null) {
				desc = locBundle.getString("desc");
				CCLog.i("定位详细信息：" + desc);
			}
		} else {
			CCLog.i("定位失败:" + amapLocation.getAMapException().getErrorCode());
		}

	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		dialog();
	}

	/**
	 * UI界面资源初始化
	 */
	public void intialSource() {

		this.edt_sqwsj_city = (TextView) findViewById(R.id.edt_sqwsj_city);
		edt_sqwsj_city.setOnClickListener(this);

		this.tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("申请为商家");

		this.sv_sqwsj_info = (ScrollView) findViewById(R.id.sv_sqwsj_info);
		sv_sqwsj_info.setOnClickListener(this);

		this.edt_sqwsj_shop_name = (EditText) findViewById(R.id.edt_sqwsj_shop_name);
		edt_sqwsj_shop_name
				.setOnFocusChangeListener(UIHelper.etOnFocusListener);

		this.edt_sqwsj_shop_hy = (TextView) findViewById(R.id.edt_sqwsj_shop_hy);
		edt_sqwsj_shop_hy.setOnClickListener(this);

		this.edt_sqwsj_shop_sq = (TextView) findViewById(R.id.edt_sqwsj_shop_sq_);
		edt_sqwsj_shop_sq.setOnClickListener(this);

		this.edt_sqwsj_shop_address = (EditText) findViewById(R.id.edt_sqwsj_shop_address);
		edt_sqwsj_shop_address
				.setOnFocusChangeListener(UIHelper.etOnFocusListener);
		this.edt_sqwsj_shop_yyphone = (EditText) findViewById(R.id.edt_sqwsj_shop_yyphone);
		edt_sqwsj_shop_yyphone
				.setOnFocusChangeListener(UIHelper.etOnFocusListener);
		this.edt_sqwsj_shop_js = (EditText) findViewById(R.id.edt_sqwsj_shop_js);
		edt_sqwsj_shop_js.setOnFocusChangeListener(UIHelper.etOnFocusListener);
		this.edt_sqwsj_shop_host_name = (EditText) findViewById(R.id.edt_sqwsj_shop_host_name);
		edt_sqwsj_shop_host_name
				.setOnFocusChangeListener(UIHelper.etOnFocusListener);
		this.im_sqwsj_frxx = (ImageView) findViewById(R.id.im_sqwsj_frxx);
		im_sqwsj_frxx.setOnClickListener(this);

		this.im_sqwsj_yyzz = (ImageView) findViewById(R.id.im_sqwsj_yyzz);
		im_sqwsj_yyzz.setOnClickListener(this);

		this.my_sqwsj_businessinfo_submit = (Button) findViewById(R.id.my_sqwsj_businessinfo_submit);
		my_sqwsj_businessinfo_submit.setOnClickListener(this);

		this.tv_time1 = (TextView) findViewById(R.id.tv_time1);
		this.tv_time1.setOnClickListener(this);

		this.tv_time2 = (TextView) findViewById(R.id.tv_time2);
		this.tv_time2.setOnClickListener(this);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);

	}

	/**
	 * 拍照上传图片用的方法
	 */
	@SuppressLint("SdCardPath")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 拍照&将图片保存到图册
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
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

				// 将图片赋值给？
				if (!imFlagFaRenInfo) {
					im_sqwsj_frxx.setImageBitmap(bitmap);
					imFlagFaRenInfo = true;
				} else if (!imFlagYingYeZhiZhao) {
					im_sqwsj_yyzz.setImageBitmap(bitmap);
					imFlagYingYeZhiZhao = true;
				}

				FileOutputStream fos = null;
				File appDir = new File(
						Environment.getExternalStorageDirectory(), "yaohe");
				if (!appDir.exists()) {
					appDir.mkdir();
				}
				String fileName = System.currentTimeMillis() + ".jpg";
				File file = new File(appDir, fileName);
				// 扫描sd卡中的特定文件
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.fromFile(new File("/sdcard/Boohee/" + fileName))));
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
					if (!imFlagFaRenInfoFile) {
						img_Fr = file;
						CCLog.v(TAG, "法人信息的图片" + img_Fr.toString());
						imFlagFaRenInfoFile = true;
					} else if (!imFlagYingYeZhiZhaoFile) {
						img_Yy = file;
						CCLog.v(TAG, "营业只照的图片" + img_Yy.toString());
						imFlagYingYeZhiZhaoFile = true;
					}

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
				// Cursor cursor = MainActivity.getContentResolver().query(uri,
				// null, null, null, null);
				Cursor cursor = getContentResolver().query(uri, null, null,
						null, null);
				cursor.moveToFirst();
				String imgNo = cursor.getString(0); // 图片编号
				String imgPath = cursor.getString(1); // 图片文件路径
				String imgSize = cursor.getString(2); // 图片大小
				String imgName = cursor.getString(3); // 图片文件名
				cursor.close();
				CCLog.i("申请商家-相册图片； ", imgPath + " ");
				Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
				// 将图片赋值给？
				if (!imFlagFaRenInfo) {
					im_sqwsj_frxx.setImageBitmap(bitmap);
					imFlagFaRenInfo = true;

					File file = new File(imgPath);
					img_Fr = file;
					CCLog.v(TAG, "相册图片-法人信息" + img_Fr.toString());
					imFlagFaRenInfoFile = true;
				} else if (!imFlagYingYeZhiZhao) {
					im_sqwsj_yyzz.setImageBitmap(bitmap);
					imFlagYingYeZhiZhao = true;
					File file = new File(imgPath);
					img_Yy = file;
					CCLog.v(TAG, "相册图片-营业执照" + img_Yy.toString());
					imFlagYingYeZhiZhaoFile = true;

				}
				break;
			case Activity.RESULT_CANCELED:// 取消
				break;

			default:
				break;
			}
			break;
		case 10:
			Bundle b = null;
			if (resultCode == 11) {
				if (data.getExtras() != null) {
					b = data.getExtras();
					oneClassID = b.getString("oneClassId");
					twoClassID = b.getString("twoClassId");
					oneClassName = b.getString("oneClassName");
					twoClassName = b.getString("twoClassName");
					edt_sqwsj_shop_hy.setText(oneClassName + "   "
							+ twoClassName);
				}
			} else if (resultCode == 22) {

				if (data.getExtras() != null) {
					b = data.getExtras();
					areaID = b.getString("areaID");
					areaName = b.getString("areaName");

					districtID = b.getString("districtID");
					districtName = b.getString("districtName");
					if (Utils.isStringEmpty(districtName)) {
						edt_sqwsj_shop_sq.setText(areaName);
					} else {
						edt_sqwsj_shop_sq.setText(areaName + "   "
								+ districtName);
					}
				}

			} else if (resultCode == 33) {

				if (data.getExtras() != null) {
					b = data.getExtras();
					mStrCity = b.getString("lbsCity");
					mStrCityId = b.getString("lbsCityID");
					edt_sqwsj_city.setText(mStrCity);
				}
			}
			break;

		default:
			break;

		}
	}

}
