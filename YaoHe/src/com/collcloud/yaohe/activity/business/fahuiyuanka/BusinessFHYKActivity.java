package com.collcloud.yaohe.activity.business.fahuiyuanka;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * @类说明 发会员卡界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
@SuppressLint("SdCardPath")
public class BusinessFHYKActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "FaHuiYuanKa ";
	/** 返回按钮 */
	private LinearLayout ll_common_top_back;
	/** 标题信息 */
	private TextView tv_title;
	/** 发送按钮 */
	private TextView tv_do;
	/** 标题 */
	private EditText edt_fhyk_bt;
	/** 折扣 */
	private EditText edt_fhyk_zk;
	/** 内容 */
	private EditText edt_fhyk_fw;
	/** 发行量 */
	private EditText edt_fhyk_fxl;
	/** 发活动进度条 */
	private Dialog fhyk_mDialog;

	private TextView mTvDateFrom;
	private TextView mTvDateTo;
	/** 日期选择 */
	private DatePicker datePicker;
	private Button btn_date_sure;
	private Date mValidStart;
	private Date mValidEnd;
	// 选择图片
	private GridView noScrollgridview;
	private GridAdapter mGridAdapter;
	private String mStrImgPath1, mStrImgPath2, mStrImgPath3, mStrImgPath4,
			mStrImgPath5, mStrImgPath6;
	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;
	//整个布局view
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_fhyk);
		view = this.findViewById(R.id.rl_fhyk_root);
		intialSource();
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
	 * UI界面资源初始化
	 */
	@SuppressLint("SimpleDateFormat")
	private void intialSource() {
		this.ll_common_top_back = (LinearLayout) findViewById(R.id.ll_common_top_back);
		ll_common_top_back.setOnClickListener(this);
		LinearLayout topDo = (LinearLayout) findViewById(R.id.ll_tv_do);
		topDo.setOnClickListener(this);

		this.tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("发会员卡");

		this.tv_do = (TextView) findViewById(R.id.tv_do);
		tv_do.setText("发布");

		this.edt_fhyk_bt = (EditText) findViewById(R.id.edt_fhyk_bt);

		this.edt_fhyk_zk = (EditText) findViewById(R.id.edt_fhyk_zk);

		this.edt_fhyk_fw = (EditText) findViewById(R.id.edt_fhyk_fw);

		this.edt_fhyk_fxl = (EditText) findViewById(R.id.edt_fhyk_fxl);

		mTvDateFrom = (TextView) findViewById(R.id.edt_huiyuanka_cong);
		mTvDateFrom.setOnClickListener(this);

		mTvDateTo = (TextView) findViewById(R.id.edt_huiyuanka_dao);
		mTvDateTo.setOnClickListener(this);
		// 发优惠券时间，开始时间默认是当天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		mTvDateFrom.setHint("从 : " + format.format(new Date()));
	}

	/**
	 * UI界面输入检验
	 */
	private void verifyInput() {

		if (TextUtils.isEmpty(edt_fhyk_bt.getText().toString())) {
			showToast("请输入标题!");
			return;
		}
		if (TextUtils.isEmpty(edt_fhyk_zk.getText().toString())) {
			showToast("请输入折扣!");
			return;
		}
		if (TextUtils.isEmpty(edt_fhyk_fw.getText().toString())) {
			showToast("请输入会员卡范围内容!");
			return;
		}
		if (TextUtils.isEmpty(mTvDateFrom.getText())) {
			showToast("请输入开始日期!");
			return;
		}

		if (TextUtils.isEmpty(mTvDateTo.getText())) {
			showToast("请输入结束日期!");
			return;
		}
		if (Bimp.tempSelectBitmap == null || Bimp.tempSelectBitmap.size() == 0) {
			showToast("请至少拍摄一张图片");
			return;
		}
		if (TextUtils.isEmpty(edt_fhyk_fxl.getText().toString())) {
			showToast("请输入发行量!");
			return;
		}

		progressbar(getApplicationContext(), R.layout.loading_progress);
		accessNet();
	}

	/**
	 * 发布会员卡
	 */
	private void accessNet() {
		resetImgPath();
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 会员卡标题
		CCLog.v(TAG, "会员卡标题>>>>>" + edt_fhyk_bt.getText().toString());
		params.addBodyParameter("title", edt_fhyk_bt.getText().toString());
		// 会员卡折扣
		CCLog.v(TAG, "会员卡折扣>>>>>" + edt_fhyk_zk.getText().toString());
		params.addBodyParameter("discount", edt_fhyk_zk.getText().toString());
		// 会员卡内容
		CCLog.v(TAG, "会员卡内容>>>>>" + edt_fhyk_fw.getText().toString());
		params.addBodyParameter("content", edt_fhyk_fw.getText().toString());
		// 会员卡发行量
		CCLog.v(TAG, "会员卡发行量>>>>>" + edt_fhyk_fxl.getText().toString());
		params.addBodyParameter("num", edt_fhyk_fxl.getText().toString());
		// 会员卡开始时间
		CCLog.v(TAG, "会员卡开始时间>>>>>" + Utils.strFromView(mTvDateFrom));
		params.addBodyParameter("valid_start", Utils.strFromView(mTvDateFrom));
		// 会员卡结束时间
		CCLog.v(TAG, "会员卡结束时间>>>>>" + Utils.strFromView(mTvDateTo));
		params.addBodyParameter("valid_end", Utils.strFromView(mTvDateTo));
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

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.FAHYK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络发送会员卡失败");
						fhyk_mDialog.dismiss();
						showToast("发布会员卡信息失败了。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络发送会员卡数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回发布会员卡状态信息
						String responseInfo = "";
						// 网络发布会员卡状态码
						String code = "";
						// 网络发布会员卡返回消息
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
							CCLog.v(TAG, "一条会员卡发布成功.....");

						} else {// 返回提示信息

							showToast(responseMsg);
						}
						fhyk_mDialog.dismiss();
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

	/**
	 * 提示对话框
	 */
	private void dialog() {

		AlertDialog.Builder builder = new Builder(BusinessFHYKActivity.this);
		builder.setTitle("提示");
		builder.setMessage("您的会员卡已经发布成功啦");
		builder.setPositiveButton("去首页看看",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						startActivity(new Intent(BusinessFHYKActivity.this,
								MainActivity.class));
						finish();
					}
				});

		builder.setNegativeButton("我还要发布",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						baseStartActivity(new Intent(BusinessFHYKActivity.this,
								BusinessServiceActivity.class));
						finish();
					}
				});
		builder.create().show();
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		fhyk_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		fhyk_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		fhyk_mDialog.setContentView(layout);
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		// 返回按钮
		case R.id.ll_common_top_back:
			BusinessFHYKActivity.this.finish();
			break;
		// 发送按钮
		case R.id.ll_tv_do:
			verifyInput();
			break;

		// 会员卡开始时间
		case R.id.edt_huiyuanka_cong:
			dialogDatePicker1();
			break;
		// 会员卡结束时间
		case R.id.edt_huiyuanka_dao:

			if (Utils.isStringEmpty(Utils.strFromView(mTvDateFrom))) {
				UIHelper.ToastMessage(BusinessFHYKActivity.this, "请您先设定开始时间。");
				return;
			}
			dialogDatePicker2();
			break;

		default:
			break;
		}
	}

	/**
	 * 弹出日期选择对话框
	 */
	@SuppressLint("SimpleDateFormat")
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
						UIHelper.ToastMessage(BusinessFHYKActivity.this,
								"结束日期必须大于开始日期。");
						return;
					}
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd ");
					String valid_end = format.format(mValidEnd);
					mTvDateTo.setText(valid_end);
				} else {
					mTvDateTo.setText("2015-12-31");
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

				mTvDateTo.setText(format.format(calendar.getTime()).toString());
				mValidEnd = calendar.getTime();
			}
		});

		dialog.show();
	}

	/**
	 * 弹出日期选择对话框
	 */
	@SuppressLint("SimpleDateFormat")
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
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd ");
					String valid_start = format.format(mValidStart);
					mTvDateFrom.setText(valid_start);
				} else {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					mTvDateFrom.setText(format.format(new Date()));
				}
				dialog.dismiss();
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

				mTvDateFrom.setText(format.format(calendar.getTime())
						.toString());
				mValidStart = calendar.getTime();
			}
		});

		dialog.show();
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
		Intent intent = new Intent(BusinessFHYKActivity.this,
				AlbumActivity.class);
		startActivity(intent);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		RelativeLayout rel_fhyk = (RelativeLayout) findViewById(R.id.rl_fhyk_root);
		SupportDisplay.resetAllChildViewParam(rel_fhyk);
		noScrollgridview = (GridView) findViewById(R.id.huiyuanka_noScrollgridview);
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
							BusinessFHYKActivity.this, itemsOnClick);
					// 显示窗口
					faceWindow.showAtLocation(BusinessFHYKActivity.this
							.findViewById(R.id.rl_fhyk_root), Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					Intent intent = new Intent(BusinessFHYKActivity.this,
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
	}

	@Override
	protected void onResume() {
		super.onResume();

		//noScrollgridview = (GridView) findViewById(R.id.huiyuanka_noScrollgridview);
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

				// 将图片插入到系统图库
				try {
					MediaStore.Images.Media.insertImage(
							this.getContentResolver(), f.getAbsolutePath(),
							fileName, null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
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
