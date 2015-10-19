package com.collcloud.yaohe.activity.person.setting;

import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 我的设置页面
 * 
 * @ClassName: MainActivity
 * @author 赵洋洋
 */
public class PersonSettingActivity extends BaseActivity implements
		OnClickListener {
	/** 页面标题 */
	private TextView tv_actionbartitle;
	/** 页面返回 */
	private ImageView tv_actionbarback;
	private LinearLayout ll_actionbarback;
	/** 设置wifi加载图片 */
	CheckBox cb_image_Control = null;
	/** 设置消息显示 */
	CheckBox cb_message_Control = null;
	/** 清理缓存 */
	RelativeLayout cachLayout;
	/** 版本检查 */
	RelativeLayout version_layout;
	/** 拨打官方电话 */
	RelativeLayout telLayout;
	/** 内存大小 */
	private TextView cach_size;
	/** 电话号码 */
	private TextView tv_callphone;
	private TextView mTvVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_setting);
		initalSource();
	}

	/**
	 * UI页面资源初始化
	 */
	private void initalSource() {
		// 其他
		tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		ll_actionbarback = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);
		ll_actionbarback.setOnClickListener(this);

		tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);

		tv_actionbartitle.setText("设置");

		cb_image_Control = (CheckBox) findViewById(R.id.cb_image_Control);

		cb_image_Control.setOnClickListener(this);

		cb_message_Control = (CheckBox) findViewById(R.id.cb_message_Control);

		cb_message_Control.setOnClickListener(this);

		cachLayout = (RelativeLayout) findViewById(R.id.cachLayout);

		cachLayout.setOnClickListener(this);

		version_layout = (RelativeLayout) findViewById(R.id.version_layout);

		version_layout.setOnClickListener(this);

		telLayout = (RelativeLayout) findViewById(R.id.telLayout);

		telLayout.setOnClickListener(this);

		cach_size = (TextView) findViewById(R.id.cach_size);

		tv_callphone = (TextView) findViewById(R.id.tv_callphone);
		mTvVersionName = (TextView) findViewById(R.id.version_View);
		mTvVersionName.setText("当前版本："
				+ Utils.getVersionName(PersonSettingActivity.this));
		Random random1 = new Random();
		int i = Math.abs(random1.nextInt() % 5);
		Random random2 = new Random();
		int j = Math.abs(random2.nextInt() % 5);
		Random random3 = new Random();
		int k = Math.abs(random3.nextInt() % 5);
		cach_size.setText(i + "." + j + k + "M");

	}

	/**
	 * UI页面点击事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		// 返回按钮
		case R.id.ll_tv_actionbarback:
			if (mLoginDataManager.getUserType().equals("1")) {
				startActivity(new Intent(PersonSettingActivity.this,
						BusinessActivity.class));
				finish();
			} else {
				startActivity(new Intent(PersonSettingActivity.this,
						MineActivity.class));
				finish();
			}

			break;
		// 图片控制
		case R.id.cb_image_Control:

			break;
		// 消息控制
		case R.id.cb_message_Control:

			break;
		// 版本检查
		case R.id.version_layout:
			ApiAccess.showProgressDialog(PersonSettingActivity.this, "检测中...",
					R.style.progress_dialog);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					ApiAccess.dismissProgressDialog();
				}
			}, 1000);

			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

				@Override
				public void onUpdateReturned(int updateStatus,
						UpdateResponse updateInfo) {
					switch (updateStatus) {
					case UpdateStatus.Yes:
						UmengUpdateAgent.showUpdateDialog(
								PersonSettingActivity.this, updateInfo);
						break;
					case UpdateStatus.No: 
						showToast("当前为最新版本");
						break;

					default:
						break;
					}
				}
			});

			UmengUpdateAgent.update(this);
			// UmengUpdateAgent.forceUpdate(PersonSettingActivity.this);
			break;
		// 清理缓存
		case R.id.cachLayout:
			ApiAccess.showProgressDialog(PersonSettingActivity.this, "清理中...",
					R.style.progress_dialog);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					ApiAccess.dismissProgressDialog();
					cach_size.setText("0.0M");
					UIHelper.ToastMessage(PersonSettingActivity.this, "清理成功。");
				}
			}, 1000);
			break;
		// 拨打电话
		case R.id.telLayout:
			dialog();
			break;

		default:
			break;
		}
	}

	/**
	 * 提示对话框
	 */
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(PersonSettingActivity.this);
		builder.setTitle("提示");
		builder.setMessage("您将拨通吆喝官方电话\n"
				+ tv_callphone.getText().toString().trim());
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				callPhone(tv_callphone.getText().toString().trim());
				arg0.dismiss();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				arg0.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void resetLayout() {

		ScrollView sv_setting = (ScrollView) findViewById(R.id.sv_setting_root);
		SupportDisplay.resetAllChildViewParam(sv_setting);
	}

}
