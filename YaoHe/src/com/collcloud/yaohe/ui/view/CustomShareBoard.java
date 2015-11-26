package com.collcloud.yaohe.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

public class CustomShareBoard extends PopupWindow implements OnClickListener {

	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(GlobalConstant.DESCRIPTOR);
	private BaseActivity mActivity;
	public Button mBtnClose;

	public CustomShareBoard(BaseActivity activity) {
		super(activity);
		this.mActivity = activity;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.share_custom_board, null);
		rootView.findViewById(R.id.share_sina_weibo).setOnClickListener(this);
		rootView.findViewById(R.id.share_wechat).setOnClickListener(this);
		rootView.findViewById(R.id.share_wechat_circle)
				.setOnClickListener(this);
		rootView.findViewById(R.id.share_wechat_shoucang).setOnClickListener(
				this);
		rootView.findViewById(R.id.share_qq).setOnClickListener(this);
		rootView.findViewById(R.id.share_qq_zone).setOnClickListener(this);
		mBtnClose = (Button)rootView.findViewById(R.id.btn_share_close);
		mBtnClose.setOnClickListener(this);
		setContentView(rootView);
		RelativeLayout rlLayout = (RelativeLayout) rootView
				.findViewById(R.id.rl_share_content_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.share_sina_weibo:
			performShare(SHARE_MEDIA.SINA);
			break;
		case R.id.share_wechat:
			performShare(SHARE_MEDIA.WEIXIN);
			break;
		case R.id.share_wechat_circle:
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		case R.id.share_wechat_shoucang:
			performShare(SHARE_MEDIA.WEIXIN);
			break;
		case R.id.share_qq:
			performShare(SHARE_MEDIA.QQ);
			break;
		case R.id.share_qq_zone:
			performShare(SHARE_MEDIA.QZONE);
			break;
		default:
			break;
		}
	}

	private void performShare(SHARE_MEDIA platform) {
		mController.postShare(mActivity, platform, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				String showText = platform.toString();
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
//					showText += "分享成功";
				} else {
					String eMsg = "";
					// if (eCode == -101){
					// eMsg = "没有授权";
					// }
//					showText += "分享失败";
					Toast.makeText(mActivity, showText + "分享失败", Toast.LENGTH_SHORT).show();
				}
//				Toast.makeText(mActivity, showText, Toast.LENGTH_SHORT).show();
				dismiss();
			}
		});
	}

}
