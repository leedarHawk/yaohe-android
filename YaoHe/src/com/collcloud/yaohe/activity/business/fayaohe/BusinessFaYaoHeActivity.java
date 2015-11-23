package com.collcloud.yaohe.activity.business.fayaohe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.frame.xlistview.XListView;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.fahuiyuanka.BusinessFHYKActivity;
import com.collcloud.yaohe.activity.business.fahuodong.BusinessFHDActivity;
import com.collcloud.yaohe.activity.business.faquan.BusinessFQActivity;
import com.collcloud.yaohe.activity.business.faxinpin.BusinessFXPActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.photo.activity.AlbumActivity;
import com.collcloud.yaohe.photo.util.Bimp;
import com.collcloud.yaohe.photo.util.ImageItem;
import com.collcloud.yaohe.ui.adapter.YinYongFuWuAdapter;
import com.collcloud.yaohe.ui.adapter.YinYongFuWuAdapter.OnYaoHeItemListener;
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
 * @类说明 发吆喝界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
public class BusinessFaYaoHeActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = BusinessFaYaoHeActivity.class.getSimpleName();
	/** 发吆喝进度条 */
	private Dialog fyh_mDialog;
	/** 返回按钮 */
	private LinearLayout ll_common_top_back;
	/** 标题信息 */
	private TextView tv_title;
	/** 发送按钮 */
	private TextView tv_do;
	/** 吆喝文字 */
	private EditText edt_input_yhmsg;
	/** 图片1 */
	private ImageView im_fyh_pic_1;
	/** 图片2 */
	private ImageView im_fyh_pic_2;
	/** 图片3 */
	private ImageView im_fyh_pic_3;
	/** 图片4 */
	private ImageView im_fyh_pic_4;
	/** 图片5 */
	private ImageView im_fyh_pic_5;
	/** 图片6 */
	private ImageView im_fyh_pic_6;
	/** 发券 */
	RelativeLayout rl_fyh_fq;
	/** 发会员卡 */
	RelativeLayout rl_fyh_fhyk;
	/** 发新品 */
	RelativeLayout rl_fyh_xp;
	/** 发活动 */
	RelativeLayout rl_fyh_fhd;
	/** 图片1uri */
	private File img_1;
	/** 图片2uri */
	private File img_2;
	/** 图片3uri */
	private File img_3;
	/** 图片4uri */
	private File img_4;
	/** 图片5uri */
	private File img_5;
	/** 图片6uri */
	private File img_6;
	/** 底部服务 */
	//LinearLayout ll_fyh_yy_service;
	/** 底部没有服务 */
	RelativeLayout rl_no_service;
	/** 服务列表 */
	XListView fyh_service_list;
	/** 6张图片序列化 */
	private int pic[] = { 0, 1, 2, 3, 4, 5 };
	/** 应用内容类型 */
	private String selectedType = "4";
	/** 应用内容id */
	private String selectedId = "";
	/** 四大服务 */
	List<FourService> mFourServiceListView;
	YinYongFuWuAdapter mYinYongAdapter;
	
	ImageView [] imageViews = new ImageView[6];
	
	// 自定义的弹出框类
	private SelectPicPopupWindow faceWindow;
	
	private String mStrImgPath1, mStrImgPath2, mStrImgPath3, mStrImgPath4,
	mStrImgPath5, mStrImgPath6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_fa_yao_he);

		intialSource();

		//ll_fyh_yy_service.setVisibility(View.VISIBLE);
		rl_no_service.setVisibility(View.GONE);

		progressbar(BusinessFaYaoHeActivity.this, R.layout.loading_progress);

		accessNetGetService();
		fyh_service_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.doXiangeCeSelPicsShow();
	}

	/**
	 * UI界面资源初始化
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
//				int selIndex=-1;
//				for (int i = 0; i < 6; i++) {
//
//					if (pic[i] == 6) {
//						selIndex = i;
//						if (i == 0) {
//
//							CCLog.v(TAG, "im_fyh_pic_1被设置数据");
//							im_fyh_pic_1.setImageBitmap(bitmap);
//							break;
//						} else if (i == 1) {
//
//							CCLog.v(TAG, "im_fyh_pic_2被设置数据");
//							im_fyh_pic_2.setImageBitmap(bitmap);
//							break;
//						} else if (i == 2) {
//
//							CCLog.v(TAG, "im_fyh_pic_3被设置数据");
//							im_fyh_pic_3.setImageBitmap(bitmap);
//							break;
//						} else if (i == 3) {
//
//							CCLog.v(TAG, "im_fyh_pic_4被设置数据");
//							im_fyh_pic_4.setImageBitmap(bitmap);
//							break;
//						} else if (i == 4) {
//
//							CCLog.v(TAG, "im_fyh_pic_5被设置数据");
//							im_fyh_pic_5.setImageBitmap(bitmap);
//							break;
//						} else if (i == 5) {
//
//							CCLog.v(TAG, "im_fyh_pic_6被设置数据");
//							im_fyh_pic_6.setImageBitmap(bitmap);
//							break;
//						}
//					}
//				}

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

//					for (int i = 0; i < 6; i++) {
//
//						if (pic[i] == 6) {
//							if (i == 0) {
//
//								CCLog.v(TAG, "img_1被赋值");
//								img_1 = file;
//								pic[i] = 0;
//								break;
//
//							} else if (i == 1) {
//
//								CCLog.v(TAG, "img_2被赋值");
//								img_2 = file;
//								pic[i] = 0;
//								break;
//
//							} else if (i == 2) {
//
//								CCLog.v(TAG, "img_3被赋值");
//								img_3 = file;
//								pic[i] = 0;
//								break;
//
//							} else if (i == 3) {
//
//								CCLog.v(TAG, "img_4被赋值");
//								img_4 = file;
//								pic[i] = 0;
//								break;
//
//							} else if (i == 4) {
//
//								CCLog.v(TAG, "img_5被赋值");
//								img_5 = file;
//								pic[i] = 0;
//								break;
//
//							} else if (i == 5) {
//
//								CCLog.v(TAG, "img_6被赋值");
//								img_6 = file;
//								pic[i] = 0;
//								break;
//
//							}
//						}
//					}
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
				// 通知图库进行更新
				this.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(new File(fileName))));
				
				//保存到缓存中
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bitmap);
				takePhoto.setImagePath(file.toString());
//				if(Bimp.tempSelectBitmap.size()==0) {
//					for(int i=0;i<6;i++) {
//						Bimp.tempSelectBitmap.add(null);
//					}
//				}
				Bimp.tempSelectBitmap.add(takePhoto);

				
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
				Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 10;
				Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
				// iv_show.setImageBitmap(bitmap);当点击选择某张照片时进行显示
				// iv_show.setImageURI(uri);
				CCLog.v(TAG, uri.toString());
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
	 * 处理相册选择图片
	 */
	private void doXiangeCeSelPicsShow() {
		
//		if(size==0) {
//			for(int i=0;i<6;i++) {
//				Bimp.tempSelectBitmap.add(null);
//			}
//		}
		
		try {
			int size = Bimp.tempSelectBitmap.size();
			for(int i=0;i<size;i++) {
				imageViews[i].setImageBitmap(Bimp.tempSelectBitmap.get(i).getBitmap());
			}
			
			for(int i=size;i<6;i++) {
				imageViews[i].setImageBitmap(null);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
//		for(int i=0;i<6;i++) {
//			try {
//				ImageItem imageItem = Bimp.tempSelectBitmap.get(i);
//				if(imageItem !=null) {
//					if (i == 0) {
//
//						CCLog.v(TAG, "im_fyh_pic_1被设置数据");
//						im_fyh_pic_1.setImageBitmap(imageItem.getBitmap());
//					} else if (i == 1) {
//
//						CCLog.v(TAG, "im_fyh_pic_2被设置数据");
//						im_fyh_pic_2.setImageBitmap(imageItem.getBitmap());
//					} else if (i == 2) {
//
//						CCLog.v(TAG, "im_fyh_pic_3被设置数据");
//						im_fyh_pic_3.setImageBitmap(imageItem.getBitmap());
//					} else if (i == 3) {
//
//						CCLog.v(TAG, "im_fyh_pic_4被设置数据");
//						im_fyh_pic_4.setImageBitmap(imageItem.getBitmap());
//					} else if (i == 4) {
//
//						CCLog.v(TAG, "im_fyh_pic_5被设置数据");
//						im_fyh_pic_5.setImageBitmap(imageItem.getBitmap());
//					} else if (i == 5) {
//
//						CCLog.v(TAG, "im_fyh_pic_6被设置数据");
//						im_fyh_pic_6.setImageBitmap(imageItem.getBitmap());
//					}
//				} else {
//					if (i == 0) {
//						im_fyh_pic_1.setImageBitmap(null);
//					} else if (i == 1) {
//						im_fyh_pic_2.setImageBitmap(null);
//					} else if (i == 2) {
//						im_fyh_pic_3.setImageBitmap(null);
//					} else if (i == 3) {
//						im_fyh_pic_4.setImageBitmap(null);
//					} else if (i == 4) {
//						im_fyh_pic_5.setImageBitmap(null);
//					} else if (i == 5) {
//						im_fyh_pic_6.setImageBitmap(null);
//					}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//
//		}
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
		if(mStrImgPath1 != null && !"".equals(mStrImgPath1)) {
			if(img_1 == null) {
				img_1 = new File(mStrImgPath1);
			}
		}
		if(mStrImgPath2 != null && !"".equals(mStrImgPath2)) {
			if(img_2 == null) {
				img_2 = new File(mStrImgPath2);
			}
		}
		if(mStrImgPath3 != null && !"".equals(mStrImgPath3)) {
			if(img_3 == null) {
				img_3 = new File(mStrImgPath3);
			}
		}
		if(mStrImgPath4 != null && !"".equals(mStrImgPath4)) {
			if(img_4 == null) {
				img_4 = new File(mStrImgPath4);
			}
		}
		if(mStrImgPath5 != null && !"".equals(mStrImgPath5)) {
			if(img_5 == null) {
				img_5 = new File(mStrImgPath5);
			}
		}
		if(mStrImgPath6 != null && !"".equals(mStrImgPath6)) {
			if(img_6 == null) {
				img_6 = new File(mStrImgPath6);
			}
		}
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
	}
	
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		accessNetGetService();
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		View view = LayoutInflater.from(this).inflate(R.layout.activity_business_fayaohe_header, null);
		this.ll_common_top_back = (LinearLayout) findViewById(R.id.ll_common_top_back);
		ll_common_top_back.setOnClickListener(this);

		this.tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("发吆喝");

		this.tv_do = (TextView) findViewById(R.id.tv_do);
		LinearLayout ll_do = (LinearLayout) findViewById(R.id.ll_tv_do);
		tv_do.setText("发送");
		ll_do.setOnClickListener(this);

		this.edt_input_yhmsg = (EditText) view.findViewById(R.id.edt_input_yhmsg);

		this.im_fyh_pic_1 = (ImageView) view.findViewById(R.id.im_fyh_pic_1);
		im_fyh_pic_1.setOnClickListener(this);

		this.im_fyh_pic_2 = (ImageView) view.findViewById(R.id.im_fyh_pic_2);
		im_fyh_pic_2.setOnClickListener(this);

		this.im_fyh_pic_3 = (ImageView) view.findViewById(R.id.im_fyh_pic_3);
		im_fyh_pic_3.setOnClickListener(this);

		this.im_fyh_pic_4 = (ImageView) view.findViewById(R.id.im_fyh_pic_4);
		im_fyh_pic_4.setOnClickListener(this);

		this.im_fyh_pic_5 = (ImageView) view.findViewById(R.id.im_fyh_pic_5);
		im_fyh_pic_5.setOnClickListener(this);

		this.im_fyh_pic_6 = (ImageView) view.findViewById(R.id.im_fyh_pic_6);
		im_fyh_pic_6.setOnClickListener(this);

		this.rl_fyh_fq = (RelativeLayout) view.findViewById(R.id.rl_fyh_fq);
		rl_fyh_fq.setOnClickListener(this);

		this.rl_fyh_fhyk = (RelativeLayout) view.findViewById(R.id.rl_fyh_fhyk);
		rl_fyh_fhyk.setOnClickListener(this);

		this.rl_fyh_xp = (RelativeLayout) view.findViewById(R.id.rl_fyh_xp);
		rl_fyh_xp.setOnClickListener(this);

		this.rl_fyh_fhd = (RelativeLayout) view.findViewById(R.id.rl_fyh_fhd);
		rl_fyh_fhd.setOnClickListener(this);

		//this.ll_fyh_yy_service = (LinearLayout) view.findViewById(R.id.ll_fyh_yy_service);

		this.rl_no_service = (RelativeLayout) view.findViewById(R.id.rl_no_service);

		this.fyh_service_list = (XListView) findViewById(R.id.listview);
		fyh_service_list.setPullLoadEnable(false);
		fyh_service_list.setPullRefreshEnable(false);
		fyh_service_list.addHeaderView(view);
		
		imageViews[0]=im_fyh_pic_1;
		imageViews[1]=im_fyh_pic_2;
		imageViews[2]=im_fyh_pic_3;
		imageViews[3]=im_fyh_pic_4;
		imageViews[4]=im_fyh_pic_5;
		imageViews[5]=im_fyh_pic_6;
		

	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		RelativeLayout rel_fyh = (RelativeLayout) findViewById(R.id.rl_fyh_root);
		SupportDisplay.resetAllChildViewParam(rel_fyh);
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {

		// 返回按钮
		case R.id.ll_common_top_back:
			BusinessFaYaoHeActivity.this.finish();
			break;

		// 发送按钮
		case R.id.ll_tv_do:
			verifyInput();
			break;

		// 第一张图
		case R.id.im_fyh_pic_1:
			for (int i = 0; i < 6; i++) {
				if (i == 0) {
					pic[i] = 6;
					CCLog.v(TAG, pic[i] + "");
				} else {
					pic[i] = 0;
					CCLog.v(TAG, pic[i] + "");
				}
			}
			//photo();
			showPopWindow();
			break;

		// 第二张图
		case R.id.im_fyh_pic_2:
			for (int i = 0; i < 6; i++) {
				if (i == 1) {
					pic[i] = 6;
				} else {
					pic[i] = 0;
				}
			}
			//photo();
			showPopWindow();
			break;

		// 第三张图
		case R.id.im_fyh_pic_3:
			for (int i = 0; i < 6; i++) {
				if (i == 2) {
					pic[i] = 6;
				} else {
					pic[i] = 0;
				}
			}
			//photo();
			showPopWindow();
			break;

		// 第四张图
		case R.id.im_fyh_pic_4:
			for (int i = 0; i < 6; i++) {
				if (i == 3) {
					pic[i] = 6;
					CCLog.v(TAG, pic[i] + "");
				} else {
					pic[i] = 0;
					CCLog.v(TAG, pic[i] + "");
				}
			}
			//photo();
			showPopWindow();
			break;

		// 第五张图
		case R.id.im_fyh_pic_5:
			for (int i = 0; i < 6; i++) {
				if (i == 4) {
					pic[i] = 6;
				} else {
					pic[i] = 0;
				}
			}
			//photo();
			showPopWindow();
			break;

		// 第六张图
		case R.id.im_fyh_pic_6:
			for (int i = 0; i < 6; i++) {
				if (i == 5) {
					pic[i] = 6;
				} else {
					pic[i] = 0;
				}
			}
			//photo();
			showPopWindow();
			break;

		// 发券
		case R.id.rl_fyh_fq:
			baseStartActivity(new Intent(BusinessFaYaoHeActivity.this,
					BusinessFQActivity.class));
			break;

		// 发新品
		case R.id.rl_fyh_xp:
			baseStartActivity(new Intent(BusinessFaYaoHeActivity.this,
					BusinessFXPActivity.class));
			break;

		// 发活动
		case R.id.rl_fyh_fhd:
			baseStartActivity(new Intent(BusinessFaYaoHeActivity.this,
					BusinessFHDActivity.class));
			break;

		// 发会员卡
		case R.id.rl_fyh_fhyk:
			baseStartActivity(new Intent(BusinessFaYaoHeActivity.this,
					BusinessFHYKActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		fyh_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		fyh_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		fyh_mDialog.setContentView(layout);
	}

	/**
	 * UI界面输入检验
	 */
	private void verifyInput() {
		if (!AppApplacation.getInstance().isNetworkConnected()) { // 网络检查
			UIHelper.ToastMessage(BusinessFaYaoHeActivity.this, getResources()
					.getString(R.string.network_disabled));
			return;
		}
		if (!mLoginDataManager.getLoginState().equals("1")) {
			toastNotLogin(BusinessFaYaoHeActivity.this);
		} else if (TextUtils.isEmpty(edt_input_yhmsg.getText())) {
			showToast("请填写吆喝具体信息");
		}
		// else if (im_fyh_pic_1.getDrawable() == null
		// && im_fyh_pic_2.getDrawable() == null
		// && im_fyh_pic_3.getDrawable() == null
		// && im_fyh_pic_4.getDrawable() == null
		// && im_fyh_pic_5.getDrawable() == null
		// && im_fyh_pic_6.getDrawable() == null) {
		//
		// showToast("请至少拍摄一张图片");
		//
		// }
		else {
			progressbar(BusinessFaYaoHeActivity.this, R.layout.loading_progress);
			accessNetSend();
		}

	}

	/**
	 * 访问网络发吆喝
	 */
	private void accessNetSend() {
		resetImgPath();
		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 所在城市ID
		if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) {
			params.addBodyParameter("city_id", GlobalVariable.sChoiceCityID);
			CCLog.v(TAG, "城市ID>>>>>" + GlobalVariable.sChoiceCityID);
		} else {
			params.addBodyParameter("city_id", GlobalConstant.DEFAULT_CITY_ID);
			CCLog.v(TAG, "城市ID>>>>>" + GlobalConstant.DEFAULT_CITY_ID);
		}
		// 吆喝内容
		CCLog.v(TAG, "吆喝内容>>>>>" + edt_input_yhmsg.getText().toString());
		params.addBodyParameter("content", edt_input_yhmsg.getText().toString());
		// 图片1
		if (img_1 != null && img_1.getPath() != null) {
			params.addBodyParameter("img1", img_1);
			CCLog.v(TAG, "图片1>>>>>" + new File(img_1.toString()));
		}
		// 图片2
		if (img_2 != null && img_2.getPath() != null) {
			params.addBodyParameter("img2", img_2);
			CCLog.v(TAG, "图片2>>>>>" + new File(img_2.toString()));
		}
		// 图片3
		if (img_3 != null && img_3.getPath() != null) {
			params.addBodyParameter("img3", img_3);
			CCLog.v(TAG, "图片3>>>>>" + new File(img_3.toString()));
		}
		// 图片4
		if (img_4 != null && img_4.getPath() != null) {
			params.addBodyParameter("img4", img_4);
			CCLog.v(TAG, "图片4>>>>>" + new File(img_4.toString()));
		}
		// 图片5
		if (img_5 != null && img_5.getPath() != null) {
			params.addBodyParameter("img5", img_5);
			CCLog.v(TAG, "图片5>>>>>" + new File(img_5.toString()));
		}
		// 图片6
		if (img_6 != null && img_6.getPath() != null) {
			params.addBodyParameter("img6", img_6);
			CCLog.v(TAG, "图片6>>>>>" + new File(img_6.toString()));
		}
		// 引用内容类型
		CCLog.v(TAG, "引用内容类型>>>>>" + selectedType);
		params.addBodyParameter("type", selectedType);// 引用内容 0券 1卡 2活动 3新品
		// 引用内容id
		CCLog.v(TAG, "引用内容类型id>>>>>" + selectedId);
		params.addBodyParameter("c_id", selectedId);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.FAYAOHE, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "网络发送失败");
						fyh_mDialog.dismiss();
						showToast("发布吆喝不成功，请重新尝试。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Bimp.tempSelectBitmap.clear();
						fyh_mDialog.dismiss();
						CCLog.v(TAG, "网络发送吆喝数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回四大服务结果状态信息
						String responseInfo = "";
						// 网络访问四大服务状态码
						String code = "";
						// 网络四大服务返回消息
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
							CCLog.v(TAG, "一条吆喝发布成功.....");
						} else {// 返回提示信息
							CCLog.v(TAG, "发布吆喝时出错啦");
							showToast(responseMsg);
						}
					}
				});
	}

	/**
	 * 访问网络获取四大类服务
	 */
	private void accessNetGetService() {

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		String url = ContantsValues.YYFW+"&isFaYaohe=Y";
		CCLog.d(TAG, "get service url :"+url);
		http.send(HttpRequest.HttpMethod.POST,url , params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络发送请求服务失败");
						fyh_mDialog.dismiss();
						showToast("获取引用服务数据失败。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						fyh_mDialog.dismiss();
						CCLog.v(TAG, "网络请求服务数据成功");
						JSONObject object, object2;
						// 网络返回四大服务结果状态信息
						String responseInfo = "";
						// 网络访问四大服务状态码
						String code = "";
						// 网络四大服务返回消息
						String responseMsg = "";
						// 网络四大服务返回数据
						String jsonObject1_data = "";

						try {

							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							jsonObject1_data = object.optString("data");

							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {// 请求成功

								if (jsonObject1_data != null) {
									JSONArray jsonArray = null;
									try {
										jsonArray = object.getJSONArray("data");
										CCLog.v(TAG, "获取到的四大服务" + jsonArray);

									} catch (JSONException e) {
										CCLog.v(TAG, "+" + "获取四大服务异常信息" + e);
										e.printStackTrace();
									}

									CCLog.v(TAG, "正在解析四大服务数据");
									mFourServiceListView = parseJSONArray(jsonArray);
									CCLog.v(TAG, "四大服务数据解析完毕");

									CCLog.v(TAG, "四大服务数据 总数 ："
											+ mFourServiceListView.size());
									if (mFourServiceListView.size() > 0) {
										if (mFourServiceListView.size() == 1) {
											FourService service = mFourServiceListView
													.get(0);
											if (Utils.isStringEmpty(service.id)) {
												mFourServiceListView.clear();
												//ll_fyh_yy_service.setVisibility(View.GONE);
												rl_no_service
														.setVisibility(View.VISIBLE);
//												setYinYongData();
//												return;
											} else {
												//ll_fyh_yy_service.setVisibility(View.VISIBLE);
												rl_no_service
														.setVisibility(View.GONE);
											}
										}
										// 设定引用服务数据
										setYinYongData();

									} else {
										//ll_fyh_yy_service.setVisibility(View.GONE);
										rl_no_service
												.setVisibility(View.VISIBLE);
										setYinYongData();
									}

								} else {

									//ll_fyh_yy_service.setVisibility(View.GONE);
									rl_no_service.setVisibility(View.VISIBLE);
									setYinYongData();

								}

							} else {
								CCLog.v(TAG, "获取四大服务出错啦");
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
						fyh_mDialog.dismiss();
					}
				});
	}

	private void setYinYongData() {
		if(mYinYongAdapter != null) {
			mYinYongAdapter.notifyDataSetChanged();
		} else {
			mYinYongAdapter = new YinYongFuWuAdapter(getApplicationContext(),
					mFourServiceListView);
			fyh_service_list.setAdapter(mYinYongAdapter);
			fyh_service_list.forbiddenLoadMore();
			mYinYongAdapter.setOnYaoHeItemClickListener(new OnYaoHeItemListener() {
				@Override
				public void onYaoHeItemClick(int position,
						ImageView ivCheck) {
					// TODO Auto-generated method stub

					String Type = mFourServiceListView.get(position).type;
					if (!Utils.isStringEmpty(Type)) {
						selectedType = Type;
					} else {
						selectedType = "4";
					}
					CCLog.e(TAG, "选中引用类型 type = " + selectedType);

					selectedId = mFourServiceListView.get(position).service_id;
					CCLog.e(TAG, "选中引用内容 id = " + selectedId);
					ivCheck.setBackgroundResource(R.drawable.icon_fujin_niming_on);
					mYinYongAdapter.setSelectedItem(position);
					mYinYongAdapter.notifyDataSetChanged();

				}
			});
		}
		
		

	}

	/**
	 * 服务Gson解析
	 */
	public ArrayList<FourService> parseJSONArray(JSONArray response) {

		ArrayList<FourService> fourservices = new ArrayList<FourService>();

		FourService fourservice = null;

		for (int i = 0; i < response.length(); i++) {
			fourservice = new FourService();
			JSONObject object = null;
			try {

				object = response.getJSONObject(i);

				fourservice.id = object.optString("id");
				fourservice.addtime = object.optString("addtime");
				fourservice.content = object.getString("content");
				fourservice.city_id = object.getString("city_id");
				fourservice.title = object.getString("title");
				fourservice.type = object.getString("type");
				fourservice.member_id = object.getString("member_id");
				fourservice.service_id = object.getString("service_id");

				fourservice.comment_num = object.getString("comment_num");
				fourservice.zan_num = object.getString("zan_num");
				fourservice.collection_num = object.getString("collection_num");
				if (!Utils.isStringEmpty(object.optString("img"))) {
					fourservice.img = URLs.IMG_PRE + object.optString("img");
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}
			fourservices.add(fourservice);
		}
		return fourservices;
	}

	/**
	 * 提示对话框
	 */
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(BusinessFaYaoHeActivity.this);
		builder.setTitle("提示");
		builder.setMessage("您的吆喝已经发布成功啦");
		builder.setPositiveButton("去首页看看",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						baseStartActivity(new Intent(
								BusinessFaYaoHeActivity.this,
								MainActivity.class));
						finish();
					}
				});

		builder.setNegativeButton("我还要发布",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						BusinessFaYaoHeActivity.this.finish();
						startActivity(new Intent(BusinessFaYaoHeActivity.this,
								BusinessFaYaoHeActivity.class));
					}
				});
		builder.create().show();
	}

	/**
	 * 调用系统相机的方法
	 * */
	public void photo() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
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
	
	private void showPopWindow() {
		faceWindow = new SelectPicPopupWindow(
				BusinessFaYaoHeActivity.this, itemsOnClick);
		// 显示窗口
		faceWindow.showAtLocation(BusinessFaYaoHeActivity.this
				.findViewById(R.id.rl_fyh_root), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
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
		Intent intent = new Intent(BusinessFaYaoHeActivity.this, AlbumActivity.class);
		startActivity(intent);
	}
	

}
