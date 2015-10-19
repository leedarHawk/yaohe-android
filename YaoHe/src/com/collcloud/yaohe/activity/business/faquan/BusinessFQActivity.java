package com.collcloud.yaohe.activity.business.faquan;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.service.BusinessServiceActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.photo.activity.AlbumActivity;
import com.collcloud.yaohe.photo.activity.GalleryActivity;
import com.collcloud.yaohe.photo.util.Bimp;
import com.collcloud.yaohe.photo.util.FileUtils;
import com.collcloud.yaohe.photo.util.ImageItem;
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
 * @类说明 发券界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
@SuppressLint("SimpleDateFormat")
public class BusinessFQActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "FaQuan";
	/** 返回按钮 */
	private LinearLayout ll_common_top_back;
	/** 标题信息 */
	private TextView tv_title;
	/** 发送按钮 */
	private TextView tv_do;
	/** 满钱数 */
	private EditText edt_fq_tj_money1;
	/** 减钱数 */
	private EditText edt_fq_tj_money2;
	/** 标题 */
	private EditText edt_fq_bt_title;
	/** 优惠内容 */
	private EditText edt_fq_bt;
	/** 从 */
	private TextView edt_fq_cong;
	/** 到 */
	private TextView edt_fq_dao;
	/** 发行量 */
	private EditText edt_fq_fxl;// 发行量为空默认为数量不计
	/** 单次按钮 */
	private Button bt_fq_dc;// 单次&多次按钮传递一个值 0：单次 1：多次
	/** 多次按钮 */
	private Button bt_fq_doc;
	/** 使用次数 */
	private String num = "0";// 默认为单次
	/** 发优惠券进度条 */
	private Dialog fyhq_mDialog;
	/** 0满减券 1满赠券 2代金券 3折扣券 */
	private static final String[] m = { "满减券", "满赠券", "代金券", "折扣券" };
	/** 类型adapter */
	private ArrayAdapter<String> adapter;
	/** 类型下拉列表 */
	private Spinner spinner;
	/** 优惠券类型 */
	private String yhqType = "0";
	/** 日期选择 */
	private DatePicker datePicker;
	/** 日期选择 */
	private Button btn_date_sure;
	/** 满减券 */
	private LinearLayout ll_mjq;
	/** 满赠券 */
	private LinearLayout ll_mzq;
	/** 代金券 */
	private LinearLayout ll_djq;
	/** 折扣券 */
	private LinearLayout ll_zkq;

	/** 满赠数 */
	private EditText edt_fq_mzq_money1;
	/** 满赠数 */
	private EditText edt_fq_mzq_money2;
	/** 代金券 */
	private EditText edt_fq_djq_money1;
	/** 折扣券 */
	private EditText edt_fq_zkq_money1;

	private int selected = 0;// 默认是满减劵

	private String mTj1 = "";
	private String mTj2 = "";
	private Date mValidStart;
	private Date mValidEnd;

	// 选择图片
	private GridView noScrollgridview;
	private GridAdapter mGridAdapter;
	private String mStrImgPath1, mStrImgPath2, mStrImgPath3, mStrImgPath4,
			mStrImgPath5, mStrImgPath6;
	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;
	private View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_fq);
		view = this.findViewById(R.id.rl_fq_root);
		spinner = (Spinner) findViewById(R.id.Spinner01);
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spinner.setAdapter(adapter);

		// 添加事件Spinner事件监听
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		// 设置默认值
		spinner.setVisibility(View.VISIBLE);

		intialSource();
	}

	/**
	 * 发布优惠券
	 */
	@SuppressLint("SimpleDateFormat")
	private void accessNet() {
		resetImgPath();
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 优惠券类型
		CCLog.v(TAG, "优惠券类型>>>>>" + yhqType);
		params.addBodyParameter("type", yhqType);
		// 优惠券标题
		CCLog.v(TAG, "优惠券标题>>>>>" + edt_fq_bt_title.getText().toString());
		params.addBodyParameter("title", edt_fq_bt_title.getText().toString());
		// 优惠券内容
		CCLog.v(TAG, "优惠券内容>>>>>" + edt_fq_bt.getText().toString());
		params.addBodyParameter("content", edt_fq_bt.getText().toString());
		// 优惠券满额度
		CCLog.v(TAG, "条件1>>>>>" + mTj1);
		params.addBodyParameter("term_start", mTj1);
		// 优惠券减额度
		CCLog.v(TAG, "条件2>>>>>" + mTj2);
		params.addBodyParameter("term_end", mTj2);
		if (mValidStart != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
			String valid_start = format.format(mValidStart);
			params.addBodyParameter("valid_start", valid_start);
			// 优惠券开始时间
			CCLog.v(TAG, "优惠券开始时间>>>>>" + valid_start);

		} else {
			params.addBodyParameter("valid_start", "2015-01-01");
		}
		if (mValidEnd != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
			String valid_end = format.format(mValidEnd);
			params.addBodyParameter("valid_end", valid_end);
			// 优惠券结束时间
			CCLog.v(TAG, "优惠券结束时间>>>>>" + valid_end);
		} else {
			params.addBodyParameter("valid_end", "2015-12-31");
		}
		// 优惠券发行量
		CCLog.v(TAG, "优惠券发行量>>>>>" + edt_fq_fxl.getText().toString());
		params.addBodyParameter("num", edt_fq_fxl.getText().toString());
		// 优惠券使用次数
		CCLog.v(TAG, "优惠券使用次数>>>>>" + num);
		params.addBodyParameter("use", num);

		// 图片1
		if (Bimp.tempSelectBitmap != null && mStrImgPath1 != null) {
			CCLog.v(TAG, "图片1>>>>>" + mStrImgPath1);
			params.addBodyParameter("img1", new File(mStrImgPath1));
		}
		// 图片2
		if (Bimp.tempSelectBitmap != null && mStrImgPath2 != null) {
			CCLog.v(TAG, "图片2>>>>>" + mStrImgPath2);
			params.addBodyParameter("img2", new File(mStrImgPath2));
		}
		// 图片3
		if (Bimp.tempSelectBitmap != null && mStrImgPath3 != null) {
			CCLog.v(TAG, "图片3>>>>>" + mStrImgPath3);
			params.addBodyParameter("img3", new File(mStrImgPath3));
		}
		// 图片4
		if (Bimp.tempSelectBitmap != null && mStrImgPath4 != null) {
			CCLog.v(TAG, "图片4>>>>>" + mStrImgPath4);
			params.addBodyParameter("img4", new File(mStrImgPath4));
		}
		// 图片5
		if (Bimp.tempSelectBitmap != null && mStrImgPath5 != null) {
			CCLog.v(TAG, "图片5>>>>>" + mStrImgPath5);
			params.addBodyParameter("img5", new File(mStrImgPath5));
		}
		// 图片6
		if (Bimp.tempSelectBitmap != null && mStrImgPath6 != null) {
			CCLog.v(TAG, "图片6>>>>>" + mStrImgPath6);
			params.addBodyParameter("img6", new File(mStrImgPath6));
		}

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.FAYHQ, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络发送优惠券失败");
						fyhq_mDialog.dismiss();
						showToast("网络访问失败,检查网络设置");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络发送优惠券数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回发布优惠券状态信息
						String responseInfo = "";
						// 网络发布优惠券状态码
						String code = "";
						// 网络发布优惠券返回消息
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
							dialog();
							Bimp.tempSelectBitmap.clear();
							CCLog.v(TAG, "一条优惠券发布成功.....");

						} else {// 返回提示信息
							CCLog.v(TAG, "发布优惠券时出错啦");
							showToast(responseMsg);
						}
						fyhq_mDialog.dismiss();
					}

				});
	}

	private void resetImgPath() {
		if (Bimp.tempSelectBitmap.size() == 1) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
		} else if (Bimp.tempSelectBitmap.size() == 2) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
			mStrImgPath2 = Bimp.tempSelectBitmap.get(1).getImagePath();

		} else if (Bimp.tempSelectBitmap.size() == 3) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
			mStrImgPath2 = Bimp.tempSelectBitmap.get(1).getImagePath();
			mStrImgPath3 = Bimp.tempSelectBitmap.get(2).getImagePath();

		} else if (Bimp.tempSelectBitmap.size() == 4) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
			mStrImgPath2 = Bimp.tempSelectBitmap.get(1).getImagePath();
			mStrImgPath3 = Bimp.tempSelectBitmap.get(2).getImagePath();
			mStrImgPath4 = Bimp.tempSelectBitmap.get(3).getImagePath();

		} else if (Bimp.tempSelectBitmap.size() == 5) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
			mStrImgPath2 = Bimp.tempSelectBitmap.get(1).getImagePath();
			mStrImgPath3 = Bimp.tempSelectBitmap.get(2).getImagePath();
			mStrImgPath4 = Bimp.tempSelectBitmap.get(3).getImagePath();
			mStrImgPath5 = Bimp.tempSelectBitmap.get(4).getImagePath();

		} else if (Bimp.tempSelectBitmap.size() == 6) {
			mStrImgPath1 = Bimp.tempSelectBitmap.get(0).getImagePath();
			mStrImgPath2 = Bimp.tempSelectBitmap.get(1).getImagePath();
			mStrImgPath3 = Bimp.tempSelectBitmap.get(2).getImagePath();
			mStrImgPath4 = Bimp.tempSelectBitmap.get(3).getImagePath();
			mStrImgPath5 = Bimp.tempSelectBitmap.get(4).getImagePath();
			mStrImgPath6 = Bimp.tempSelectBitmap.get(5).getImagePath();

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
	}

	/**
	 * UI界面输入检验
	 */
	private void verifyInput() {

		if (selected == 0) {// 满减券
			mTj1 = edt_fq_tj_money1.getText().toString();
			mTj2 = edt_fq_tj_money2.getText().toString();
			if (Utils.isStringEmpty(Utils.strFromView(edt_fq_tj_money1))
					|| Utils.isStringEmpty(Utils.strFromView(edt_fq_tj_money2))) {
				showToast("请输入满减券优惠条件!");
				return;
			}
		} else if (selected == 1) {// 满赠券
			mTj1 = edt_fq_mzq_money1.getText().toString();
			mTj2 = edt_fq_mzq_money2.getText().toString();
			if (Utils.isStringEmpty(Utils.strFromView(edt_fq_mzq_money1))
					|| Utils.isStringEmpty(Utils.strFromView(edt_fq_mzq_money2))) {
				showToast("请输入满赠券优惠条件!");
				return;
			}
		} else if (selected == 2) {// 代金券
			mTj1 = edt_fq_djq_money1.getText().toString();
			mTj2 = "0";
			if (Utils.isStringEmpty(Utils.strFromView(edt_fq_djq_money1))) {
				showToast("请输入代金券优惠条件!");
				return;
			}
		} else if (selected == 3) {
			mTj1 = edt_fq_zkq_money1.getText().toString();
			mTj2 = "0";
			if (Utils.isStringEmpty(Utils.strFromView(edt_fq_zkq_money1))) {
				showToast("请输入折扣券优惠条件!");
				return;
			}
		}

		if (TextUtils.isEmpty(edt_fq_bt_title.getText())) {
			showToast("请输入优惠券标题!");
			return;
		}

		if (TextUtils.isEmpty(edt_fq_bt.getText())) {
			showToast("请输入优惠券范围!");
			return;
		}
		if (Bimp.tempSelectBitmap == null || Bimp.tempSelectBitmap.size() == 0) {
			showToast("请至少拍摄一张图片");
			return;
		}

		if (TextUtils.isEmpty(edt_fq_cong.getText())) {
			showToast("请输入开始日期!");
			return;
		}

		if (TextUtils.isEmpty(edt_fq_dao.getText())) {
			showToast("请输入结束日期!");
			return;
		}
		if (TextUtils.isEmpty(edt_fq_fxl.getText())) {
			showToast("发行量不能为空!");
			return;
		}

		progressbar(getApplicationContext(), R.layout.loading_progress);

		accessNet();
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		this.ll_common_top_back = (LinearLayout) findViewById(R.id.ll_common_top_back);
		ll_common_top_back.setOnClickListener(this);

		this.tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("发券");

		this.tv_do = (TextView) findViewById(R.id.tv_do);
		LinearLayout doLayout = (LinearLayout) findViewById(R.id.ll_tv_do);
		tv_do.setText("发布");
		doLayout.setOnClickListener(this);

		this.edt_fq_tj_money1 = (EditText) findViewById(R.id.edt_fq_tj_money1);

		this.edt_fq_tj_money2 = (EditText) findViewById(R.id.edt_fq_tj_money2);

		this.edt_fq_bt_title = (EditText) findViewById(R.id.edt_fq_bt_title);

		this.edt_fq_bt = (EditText) findViewById(R.id.edt_fq_bt);

		this.edt_fq_cong = (TextView) findViewById(R.id.edt_fq_cong);
		edt_fq_cong.setOnClickListener(this);

		this.edt_fq_dao = (TextView) findViewById(R.id.edt_fq_dao);
		edt_fq_dao.setOnClickListener(this);

		this.edt_fq_fxl = (EditText) findViewById(R.id.edt_fq_fxl);

		this.bt_fq_dc = (Button) findViewById(R.id.bt_fq_dc);
		bt_fq_dc.setOnClickListener(this);

		this.bt_fq_doc = (Button) findViewById(R.id.bt_fq_doc);
		bt_fq_doc.setOnClickListener(this);

		// 默认为单次
		bt_fq_dc.setSelected(true);

		ll_mjq = (LinearLayout) findViewById(R.id.ll_mjq);
		ll_mzq = (LinearLayout) findViewById(R.id.ll_mzq);
		ll_djq = (LinearLayout) findViewById(R.id.ll_djq);
		ll_zkq = (LinearLayout) findViewById(R.id.ll_zkq);

		edt_fq_mzq_money1 = (EditText) findViewById(R.id.edt_fq_mzq_money1);
		edt_fq_mzq_money2 = (EditText) findViewById(R.id.edt_fq_mzq_money2);

		edt_fq_djq_money1 = (EditText) findViewById(R.id.edt_fq_djq_money1);
		edt_fq_zkq_money1 = (EditText) findViewById(R.id.edt_fq_zkq_money1);

		// 发优惠券时间，开始时间默认是当天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		edt_fq_cong.setHint("从 : " + format.format(new Date()));

	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		// 点击返回按钮
		case R.id.ll_common_top_back:
			BusinessFQActivity.this.finish();
			break;

		// 点击发布按钮
		case R.id.ll_tv_do:
			verifyInput();
			break;
		// 点击单次按钮
		case R.id.bt_fq_dc:
			bt_fq_dc.setSelected(true);
			bt_fq_doc.setSelected(false);
			num = "0";
			break;

		// 点击多次按钮
		case R.id.bt_fq_doc:
			bt_fq_dc.setSelected(false);
			bt_fq_doc.setSelected(true);
			num = "1";
			break;

		// 点击riqi
		case R.id.edt_fq_cong:
			dialogDatePicker1();
			break;

		// 点击riqi
		case R.id.edt_fq_dao:
			if (Utils.isStringEmpty(Utils.strFromView(edt_fq_cong))) {
				UIHelper.ToastMessage(BusinessFQActivity.this, "请您先设定开始时间。");
				return;
			}
			dialogDatePicker2();
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
		// Intent intent = new Intent();
		// /* 开启Pictures画面Type设定为image */
		// intent.setType("image/*");
		// /* 使用Intent.ACTION_GET_CONTENT这个Action */
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// intent.addCategory(Intent.CATEGORY_OPENABLE);
		// /* 取得相片后返回本画面 */
		// startActivityForResult(intent, 2);
		Intent intent = new Intent(BusinessFQActivity.this, AlbumActivity.class);
		startActivity(intent);
	}

	/**
	 * 调用系统相机的方法
	 * */
	public void photo() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		startActivityForResult(intent, 1);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		RelativeLayout rel_fq = (RelativeLayout) findViewById(R.id.rl_fq_root);
		SupportDisplay.resetAllChildViewParam(rel_fq);

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridAdapter = new GridAdapter(this);
		mGridAdapter.update();
		noScrollgridview.setAdapter(mGridAdapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				hideInputBoard();
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					faceWindow = new SelectPicPopupWindow(
							BusinessFQActivity.this, itemsOnClick);
					// 显示窗口
					faceWindow.showAtLocation(BusinessFQActivity.this
							.findViewById(R.id.rl_fq_root), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					Intent intent = new Intent(BusinessFQActivity.this,
							GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}
	
	/**

	* 

	* 

	* @description 隐藏键盘

	* @version 1.0

	* @author LEE

	* @date 2015年10月11日 下午4:10:23 

	* @update 2015年10月11日 下午4:10:23

	*/

	private void hideInputBoard() {

	InputMethodManager imm = (InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);  

	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {

		fyhq_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		fyhq_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		fyhq_mDialog.setContentView(layout);
	}

	/**
	 * 提示对话框
	 */
	private void dialog() {

		AlertDialog.Builder builder = new Builder(BusinessFQActivity.this);
		builder.setTitle("提示");
		builder.setMessage("您的优惠券已经发布成功啦");
		builder.setPositiveButton("去首页看看",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						startActivity(new Intent(BusinessFQActivity.this,
								MainActivity.class));
						finish();
					}
				});

		builder.setNegativeButton("我还要发布",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						baseStartActivity(new Intent(BusinessFQActivity.this,
								BusinessServiceActivity.class));
						finish();
					}
				});
		builder.create().show();
	}

	/**
	 * 弹出日期选择对话框
	 */
	protected void dialogDatePicker2() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.datepicker);
		dialog.setCancelable(false);

		datePicker = (DatePicker) dialog.findViewById(R.id.dpPicker);

		btn_date_sure = (Button) dialog.findViewById(R.id.btn_date_sure);

		btn_date_sure.setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View arg0) {

				if (mValidEnd != null) {
					long endTime = mValidEnd.getTime();
					long startTime = 0L;
					if (mValidStart != null) {
						startTime = mValidStart.getTime();
					}
					if (startTime >= endTime) {
						UIHelper.ToastMessage(BusinessFQActivity.this,
								"结束日期必须大于开始日期。");
						return;
					}
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd ");
					String valid_end = format.format(mValidEnd);
					edt_fq_dao.setText(valid_end);
				} else {
					edt_fq_dao.setText("2015-12-31");
				}
				dialog.dismiss();
			}
		});

		datePicker.init(2015, 11, 31, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// 获取一个日历对象，并初始化为当前选中的时间
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));

				edt_fq_dao.setText(format.format(calendar.getTime()).toString());
				mValidEnd = calendar.getTime();
			}
		});

		dialog.show();
	}

	/**
	 * 弹出日期选择对话框
	 */
	@SuppressWarnings("deprecation")
	protected void dialogDatePicker1() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.datepicker);
		dialog.setCancelable(false);

		datePicker = (DatePicker) dialog.findViewById(R.id.dpPicker);
		btn_date_sure = (Button) dialog.findViewById(R.id.btn_date_sure);

		btn_date_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mValidStart != null) {
					long startTime = mValidStart.getTime();
					
					//当前时间
					Calendar c = Calendar.getInstance();
					c.setTime(new Date(System.currentTimeMillis()));
					int year = c.get(Calendar.YEAR);  
					int month = c.get(Calendar.MONTH); 
					int day = c.get(Calendar.DAY_OF_MONTH);  
					
					//选择的时间
					Calendar c1 = Calendar.getInstance();
					c1.setTime(mValidStart);
					int year1 = c1.get(Calendar.YEAR);  
					int month1 = c1.get(Calendar.MONTH); 
					int day1 = c1.get(Calendar.DAY_OF_MONTH);  
					
					boolean showWarning = false;
					if(startTime < System.currentTimeMillis()) {
						if(year1<year) {
							showWarning = true;
						} else if(year1==year) {
							if(month1<month) {
								showWarning = true;
							} else if(month1==month) {
								if(day1<day) {
									showWarning = true;
								}
							}
						}
						if(showWarning) {
							UIHelper.ToastMessage(BusinessFQActivity.this,
									"开始日期不能小于当前日期。");
						} else {
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd ");
							String valid_start = format.format(mValidStart);
							edt_fq_cong.setText(valid_start);
							dialog.dismiss();
						}
						
					} else {
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd ");
						String valid_start = format.format(mValidStart);
						edt_fq_cong.setText(valid_start);
						dialog.dismiss();
					}
					
				} else {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					edt_fq_cong.setText(format.format(new Date()));
					dialog.dismiss();
				}
				
			}
		});
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.setTime(date);
		int cyear = cal.get(Calendar.YEAR);
		int cmonth = cal.get(Calendar.MONTH);
		int cday = cal.get(Calendar.DAY_OF_MONTH);
		datePicker.init(cyear, cmonth, cday, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// 获取一个日历对象，并初始化为当前选中的时间
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setTimeZone(TimeZone.getTimeZone("GMT"));

				edt_fq_cong.setText(format.format(calendar.getTime())
						.toString());
				mValidStart = calendar.getTime();
			}
		});

		dialog.show();
	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1,
				final int position, long arg3) {
			yhqType = position + "";
			CCLog.v(TAG, "您选了" + position);
			selected = position;

			switch (position) {
			case 0:
				ll_mjq.setVisibility(View.VISIBLE);
				ll_mzq.setVisibility(View.GONE);
				ll_djq.setVisibility(View.GONE);
				ll_zkq.setVisibility(View.GONE);
				break;
			case 1:
				ll_mjq.setVisibility(View.GONE);
				ll_mzq.setVisibility(View.VISIBLE);
				ll_djq.setVisibility(View.GONE);
				ll_zkq.setVisibility(View.GONE);
				break;
			case 2:
				ll_mjq.setVisibility(View.GONE);
				ll_mzq.setVisibility(View.GONE);
				ll_djq.setVisibility(View.VISIBLE);
				ll_zkq.setVisibility(View.GONE);
				break;
			case 3:
				ll_mjq.setVisibility(View.GONE);
				ll_mzq.setVisibility(View.GONE);
				ll_djq.setVisibility(View.GONE);
				ll_zkq.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 6) {
				return 6;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				LinearLayout layout = (LinearLayout) convertView
						.findViewById(R.id.ll_item_gridview_view);
				SupportDisplay.resetAllChildViewParam(layout);
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 6) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					mGridAdapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	// protected void onRestart() {
	// mGridAdapter.update();
	// super.onRestart();
	// }

	@Override
	protected void onResume() {
		super.onResume();

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) noScrollgridview
				.getLayoutParams();
		if (Bimp.tempSelectBitmap != null && Bimp.tempSelectBitmap.size() > 2) {
			layoutParams.height = SupportDisplay
					.calculateActualControlerSize(280.0f);
		} else {
			layoutParams.height = SupportDisplay
					.calculateActualControlerSize(140.0f);
		}
		mGridAdapter.update();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 拍照&相册
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		// 代表拍照
		case 1:
			if (Bimp.tempSelectBitmap.size() < 6 && resultCode == RESULT_OK) {// 照相完成点击确定
				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				FileUtils.saveBitmap(bm, fileName);

				File f = new File(FileUtils.SDPATH, fileName + ".JPEG");

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				takePhoto.setImagePath(f.toString());
				Bimp.tempSelectBitmap.add(takePhoto);

				// 通知图库进行更新
				this.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(new File(fileName))));
			}

			break;

		default:
			break;
		}
	}

}
