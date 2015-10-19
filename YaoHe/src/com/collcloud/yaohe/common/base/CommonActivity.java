package com.collcloud.yaohe.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.fayaohe.BusinessFaYaoHeActivity;
import com.collcloud.yaohe.activity.friend.HaoWanActivity;
import com.collcloud.yaohe.activity.fujin.FuJinActivity;
import com.collcloud.yaohe.activity.my.MineActivity;

/**
 * 包含底部菜单的共通处理
 * 
 * @ClassName CommonActivity
 * @Description
 * @author CollCloud_小米
 */
public class CommonActivity extends BaseActivity {
	private LayoutInflater mInflater;
	// ******************　底部切换　************* //
	// 首页
	public static final int FOOTER_TYPE_HOME = 1;
	// 附近
	public static final int FOOTER_TYPE_FUJIN = 2;
	// 好玩
	public static final int FOOTER_TYPE_HAOWAN = 3;
	// 我的
	public static final int FOOTER_TYPE_MINE = 4;
	/**
	 * footer类型
	 */
	private int mFooterType = -1;
	/**
	 * footer_按钮_我的
	 */
	private ImageView mIvFooterHome;
	/**
	 * footer_按钮_附近
	 */
	private ImageView mIvFooterFujin;
	/**
	 * footer_按钮_好玩
	 */
	private ImageView mIvFooterHaowan;
	/**
	 * footer_按钮_我的
	 */
	private ImageView mIvFooterMine;
	/**
	 * footer_文本_首页
	 */
	private TextView mTvFooterHome;
	/**
	 * footer_文本_附近
	 */
	private TextView mTvFooterFujin;
	/**
	 * footer_文本_好玩
	 */
	private TextView mTvFooterHaowan;
	/**
	 * footer_文本_我的
	 */
	private TextView mTvFooterMine;
	protected LinearLayout mLlfooterCommon;
	private LinearLayout mLlHomeLayout;
	private LinearLayout mLlFujinLayout;
	private LinearLayout mLlHaoWanLayout;
	private RelativeLayout mRlMineLayout;

	// ***　分割线 && 以下是底部包含【吆喝】按钮切换的组件　*** //
	protected LinearLayout mLlfooterCommonHasYaohe;
	private LinearLayout mLlHomeLayoutHasYaohe;
	private LinearLayout mLlFujinLayoutHasYaohe;
	private LinearLayout mLlHaoWanLayoutHasYaohe;
	private RelativeLayout mRlMineLayoutHasYaohe;
	private ImageView mIvFooterHomeHasYaohe;
	private ImageView mIvFooterFujinHasYaohe;
	private ImageView mIvFooterHaowanHasYaohe;
	private ImageView mIvFooterMineHasYaohe;
	private TextView mTvFooterHomeHasYaohe;
	private TextView mTvFooterFujinHasYaohe;
	private TextView mTvFooterHaowanHasYaohe;
	private TextView mTvFooterMineHasYaohe;
	private Button mBtnFooterYaoHe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(this);
		getViewById();
		if (mFooterType != -1) {

			if (mLoginDataManager.getUserType() != null) {
				if (mLoginDataManager.getUserType().equals(
						GlobalConstant.VALID_VALUE)) {
					setYaoHeSelectedFooter();
				} else {
					setSelectedFooter();
				}
			} else {
				setSelectedFooter();
			}

		}
	}

	// ********** 改版 底部导航切换 ************* //
	protected void getViewById() {
		if (findViewById(R.id.ll_yaohe_footer_no_yaohe) != null) {
			mLlfooterCommon = (LinearLayout) findViewById(R.id.ll_yaohe_footer_no_yaohe);
			mIvFooterHome = (ImageView) findViewById(R.id.iv_yaohe_bottom_home);
			mIvFooterFujin = (ImageView) findViewById(R.id.iv_yaohe_bottom_fujin);
			mIvFooterHaowan = (ImageView) findViewById(R.id.iv_yaohe_bottom_haowan);
			mIvFooterMine = (ImageView) findViewById(R.id.iv_yaohe_bottom_mine);
			mTvFooterHome = (TextView) findViewById(R.id.tv_yaohe_bottom_home);
			mTvFooterFujin = (TextView) findViewById(R.id.tv_yaohe_bottom_fujin);
			mTvFooterHaowan = (TextView) findViewById(R.id.tv_yaohe_bottom_haowan);
			mTvFooterMine = (TextView) findViewById(R.id.tv_yaohe_bottom_mine);
			// ******** 底部切换监听事件 ********* //
			mLlHomeLayout = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_home);
			mLlFujinLayout = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_fujin);
			mLlHaoWanLayout = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_haowan);
			mRlMineLayout = (RelativeLayout) findViewById(R.id.rl_yaohe_bottom_mine);
			mLlHomeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHome();
				}
			});
			mLlFujinLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnFujin();
				}
			});
			mLlHaoWanLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHaoWan();
				}
			});
			mRlMineLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnMine();
				}
			});
		}
		// ***　分割线 && 以下是底部包含【吆喝】按钮切换的组件　*** //
		if (findViewById(R.id.ll_yaohe_footer_has_yaohe) != null) {
			mLlfooterCommonHasYaohe = (LinearLayout) findViewById(R.id.ll_yaohe_footer_has_yaohe);
			mIvFooterHomeHasYaohe = (ImageView) findViewById(R.id.iv_yaohe_bottom_home_has_yaohe);
			mIvFooterFujinHasYaohe = (ImageView) findViewById(R.id.iv_yaohe_bottom_fujin_has_yaohe);
			mIvFooterHaowanHasYaohe = (ImageView) findViewById(R.id.iv_yaohe_bottom_haowan_has_yaohe);
			mIvFooterMineHasYaohe = (ImageView) findViewById(R.id.iv_yaohe_bottom_mine_has_yaohe);
			mTvFooterHomeHasYaohe = (TextView) findViewById(R.id.tv_yaohe_bottom_home_has_yaohe);
			mTvFooterFujinHasYaohe = (TextView) findViewById(R.id.tv_yaohe_bottom_fujin_has_yaohe);
			mTvFooterHaowanHasYaohe = (TextView) findViewById(R.id.tv_yaohe_bottom_haowan_has_yaohe);
			mTvFooterMineHasYaohe = (TextView) findViewById(R.id.tv_yaohe_bottom_mine_has_yaohe);
			// ******** 底部切换监听事件 ********* //
			mLlHomeLayoutHasYaohe = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_home_has_yaohe);
			mLlFujinLayoutHasYaohe = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_fujin_has_yaohe);
			mLlHaoWanLayoutHasYaohe = (LinearLayout) findViewById(R.id.ll_yaohe_bottom_haowan_has_yaohe);
			mRlMineLayoutHasYaohe = (RelativeLayout) findViewById(R.id.rl_yaohe_bottom_mine_has_yaohe);
			mBtnFooterYaoHe = (Button) findViewById(R.id.btn_footer_bottom_yaohe);
			mBtnFooterYaoHe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickOnYaoHe();

				}
			});
			mLlHomeLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHome();
				}
			});
			mLlFujinLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnFujin();
				}
			});
			mLlHaoWanLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnHaoWan();
				}
			});
			mRlMineLayoutHasYaohe.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickOnMine();
				}
			});
		}

		// 底部切换布局改变
		setFooterChanged();
	}

	/**
	 * 底部切换布局改变
	 */
	private void setFooterChanged() {
		if (mLoginDataManager.getUserType() != null) {
			if (mLoginDataManager.getUserType().equals(
					GlobalConstant.VALID_VALUE)) {
				mLlfooterCommon.setVisibility(View.GONE);
				mLlfooterCommonHasYaohe.setVisibility(View.VISIBLE);

			} else {
				mLlfooterCommon.setVisibility(View.VISIBLE);
				mLlfooterCommonHasYaohe.setVisibility(View.GONE);

			}
		} else {
			mLlfooterCommon.setVisibility(View.VISIBLE);
			mLlfooterCommonHasYaohe.setVisibility(View.GONE);
		}

	}

	/**
	 * 设置footer类型
	 * 
	 * @param footerType
	 */
	protected void setFooterType(int footerType) {
		mFooterType = footerType;
	}

	/**
	 * 设置被选择的footer
	 * 
	 * @param footerType
	 */
	protected void setSelectedFooter() {
		if (mFooterType == FOOTER_TYPE_HOME) {
			mIvFooterHome.setImageResource(R.drawable.yaohe_footer_home_on);
			mTvFooterHome.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FUJIN) {
			mIvFooterFujin.setImageResource(R.drawable.yaohe_footer_fujin_on);
			mTvFooterFujin.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_HAOWAN) {
			mIvFooterHaowan.setImageResource(R.drawable.yaohe_footer_haowan_on);
			mTvFooterHaowan.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_MINE) {
			mIvFooterMine.setImageResource(R.drawable.yaohe_footer_mine_no_message_on);
			mTvFooterMine.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		}
	}

	protected void setYaoHeSelectedFooter() {
		if (mFooterType == FOOTER_TYPE_HOME) {
			mIvFooterHomeHasYaohe
					.setImageResource(R.drawable.yaohe_footer_home_on);
			mTvFooterHomeHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_FUJIN) {
			mIvFooterFujinHasYaohe
					.setImageResource(R.drawable.yaohe_footer_fujin_on);
			mTvFooterFujinHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_HAOWAN) {
			mIvFooterHaowanHasYaohe
					.setImageResource(R.drawable.yaohe_footer_haowan_on);
			mTvFooterHaowanHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		} else if (mFooterType == FOOTER_TYPE_MINE) {
			mIvFooterMineHasYaohe
					.setImageResource(R.drawable.yaohe_footer_mine_no_message_on);
			mTvFooterMineHasYaohe.setTextColor(getResources().getColor(
					R.color.common_bw_dialog_btn_chengse));
		}
	}

	/**
	 * 点击【吆喝】监听事件
	 * 
	 * @Title clickOnYaoHe
	 */
	public void clickOnYaoHe() {

		Intent yaoheIntent = new Intent(this, BusinessFaYaoHeActivity.class);
		mBaseActivity.baseStartActivity(yaoheIntent);
	}

	/**
	 * 点击【主页】监听事件
	 * 
	 * @Title clickOnHome
	 */
	public void clickOnHome() {

		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		baseStartActivity(intent);
	}

	/**
	 * 点击【附近】监听事件
	 * 
	 * @Title clickOnFujin
	 */
	public void clickOnFujin() {

		Intent intent = new Intent();
		intent.setClass(this, FuJinActivity.class);
		baseStartActivity(intent);
	}

	/**
	 * 点击【好玩】监听事件
	 * 
	 * @Title clickOnHaoWan
	 */
	public void clickOnHaoWan() {

		Intent intent = new Intent();
		intent.setClass(this, HaoWanActivity.class);
		baseStartActivity(intent);
	}

	/**
	 * 点击【我的】监听事件
	 * 
	 * @Title clickOnMine
	 */
	public void clickOnMine() {
		// UserType : （0 个人 ； 1：商家）
		if (mLoginDataManager.getUserType().equals(GlobalConstant.VALID_VALUE)) {
			Intent myIntent = new Intent();
			myIntent.setClass(mBaseActivity, BusinessActivity.class);
			mBaseActivity.baseStartActivity(myIntent);
		} else { // 个人页面
			Intent myIntent = new Intent();
			myIntent.setClass(mBaseActivity, MineActivity.class);
			mBaseActivity.baseStartActivity(myIntent);
		}
	}

	@Override
	protected void resetLayout() {

	}

}
