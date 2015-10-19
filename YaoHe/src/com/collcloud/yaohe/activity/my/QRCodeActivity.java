package com.collcloud.yaohe.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;
import com.dtr.zbar.scan.CaptureActivity;

/**
 * 二维码展示
 * 
 * @ClassName QRCodeActivity
 * @Description
 * @author CollCloud_小米
 */
public class QRCodeActivity extends BaseActivity implements OnClickListener {
	/**
	 * 返回
	 */
	private LinearLayout mLlBack = null;
	/**
	 * 二维码扫描
	 */
	private LinearLayout mLlScan = null;
	/**
	 * 用户头像
	 */
	private ImageView mIvUserFace = null;
	/**
	 * 二维码图片
	 */
	private ImageView mIvQrcode = null;
	/**
	 * 用户性别
	 */
	private ImageView mIvSex = null;
	/**
	 * 用户昵称
	 */
	private TextView mTvUserName = null;
	/**
	 * 类型
	 */
	private TextView mTvType = null;
	private static ImageLoader mImageLoader;

	private String mStrShopName;
	private String mStrShopType;
	private String mStrFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_qrcode);
		mStrShopName = getStringExtra("shopName");
		mStrShopType = getStringExtra("shopType");
		mStrFace = getStringExtra("userface");

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(mIvUserFace,
				R.drawable.ic_launcher, R.drawable.ic_launcher);
		if (!Utils.isStringEmpty(mStrFace)) {
			mImageLoader.get(mStrFace, listener);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.isStringEmpty(mStrShopName)) {
			mTvUserName.setText(mStrShopName);
		}
		if (!Utils.isStringEmpty(mStrShopType)) {
			mTvType.setText(mStrShopType);
		}
	}

	@Override
	protected void resetLayout() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_mine_qrcode_root);
		SupportDisplay.resetAllChildViewParam(layout);
		mLlBack = (LinearLayout) findViewById(R.id.ll_mine_qrcode_root_title_back);
		mLlBack.setOnClickListener(this);
		mLlScan = (LinearLayout) findViewById(R.id.ll_mine_qrcode_root_title_scan);
		mLlScan.setOnClickListener(this);
		mIvUserFace = (ImageView) findViewById(R.id.iv_qrcode_user_photo);
		mIvQrcode = (ImageView) findViewById(R.id.iv_qrcode_user_info);
		mIvSex = (ImageView) findViewById(R.id.iv_qrcode_user_sex);
		mTvUserName = (TextView) findViewById(R.id.tv_qrcode_user_name);
		mTvType = (TextView) findViewById(R.id.tv_qrcode_user_type);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_mine_qrcode_root_title_back:
			this.finish();
			break;
		case R.id.ll_mine_qrcode_root_title_scan:
			baseStartActivity(new Intent(QRCodeActivity.this,
					CaptureActivity.class));
			break;

		default:
			break;
		}

	}

}
