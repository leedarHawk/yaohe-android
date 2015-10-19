package com.collcloud.yaohe.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DetailBusinessCommentInfo.BusinessCommentInfo;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.Utils;

public class HomeBusinessCommentAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	// private static ImageLoader mImageLoader;

	private List<BusinessCommentInfo> mCallCommentList = new ArrayList<BusinessCommentInfo>();

	public HomeBusinessCommentAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		// mImageLoader = new ImageLoader(AppApplacation.requestQueue,
		// new BitmapCache());
	}

	public HomeBusinessCommentAdapter(Context context,
			List<BusinessCommentInfo> datas) {
		this.mContext = context;
		this.mCallCommentList = datas;
		mLayoutInflater = LayoutInflater.from(mContext);
		// mImageLoader = new ImageLoader(AppApplacation.requestQueue,
		// new BitmapCache());
	}

	@Override
	public int getCount() {
		return mCallCommentList == null ? 0 : mCallCommentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCallCommentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// public interface OnBusinessInfoListener {
	// void onBusinessInfoClick(int position,String type, String serviceId);
	// }
	//
	// private OnBusinessInfoListener mOnItemClickListener = null;
	//
	// public void setOnHomeItemClickListener(OnBusinessInfoListener listener) {
	// this.mOnItemClickListener = listener;
	// }

	public void refresh(List<BusinessCommentInfo> data) {
		this.mCallCommentList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DetailsCallContent holder;
		BusinessCommentInfo callInfo = null;
		if (mCallCommentList != null && mCallCommentList.size() > 0) {
			callInfo = mCallCommentList.get(position);
		}
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_home_business_comment_details, null);

			holder = new DetailsCallContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (DetailsCallContent) convertView.getTag();
		}

		if (callInfo != null && callInfo.content != null) {
			holder.mTvContent.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.is_anonymous != null) {
			if (callInfo.is_anonymous.equals("1")) {
				holder.mTvName.setText("匿名用户");
			} else {
				if (callInfo != null && callInfo.nickname != null) {
					holder.mTvName.setText(callInfo.nickname);
				} else {
					holder.mTvName.setText("吆喝用户");
				}
			}
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			holder.mTvDate.setText(callInfo.addtime);
			// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
			try {
				if (callInfo.addtime.length() == 10) {
					callInfo.addtime = callInfo.addtime + "000";
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = new Date(Long.valueOf(callInfo.addtime));
				holder.mTvDate.setText(df.format(date));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				holder.mTvDate.setText(callInfo.addtime);
			}
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (callInfo != null && callInfo.star != null) {
			String shopStar = callInfo.star;
			if (shopStar.equals("1")) {
				holder.mTvStar1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("2")) {
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("3")) {
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("4")) {
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("5")) {
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_on);
			} else {
				holder.mTvStar2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvStar3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvStar5
						.setBackgroundResource(R.drawable.icon_rate_star_off);

			}
		}
		if (position == 0) {
			holder.mLlShopInfo.setVisibility(View.VISIBLE);
			if (!Utils.isStringEmpty(callInfo.title)) {
				holder.mTvShopName.setText(callInfo.title);
			}
			if (!Utils.isStringEmpty(GlobalVariable.sShopStar)) {
				String shopStar = GlobalVariable.sShopStar;
				holder.mTvShopStar.setText(shopStar);
				if (shopStar.equals("1")) {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_off);
				} else if (shopStar.equals("2")) {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_off);
				} else if (shopStar.equals("3")) {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_off);
				} else if (shopStar.equals("4")) {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_off);
				} else if (shopStar.equals("5")) {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_on);
				} else {
					holder.mTvShopStar1
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar2
							.setBackgroundResource(R.drawable.icon_rate_star_on);
					holder.mTvShopStar3
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar4
							.setBackgroundResource(R.drawable.icon_rate_star_off);
					holder.mTvShopStar5
							.setBackgroundResource(R.drawable.icon_rate_star_off);
				}

			}
		} else {
			holder.mLlShopInfo.setVisibility(View.GONE);

		}

		return convertView;
	}

	private class DetailsCallContent {
		// 店铺基本信息
		TextView mTvShopName;
		TextView mTvShopStar;
		TextView mTvShopStar1;
		TextView mTvShopStar2;
		TextView mTvShopStar3;
		TextView mTvShopStar4;
		TextView mTvShopStar5;
		LinearLayout mLlShopInfo;

		TextView mTvName;
		TextView mTvContent;
		/** 日期时间 */
		TextView mTvDate;
		TextView mTvStar1;
		TextView mTvStar2;
		TextView mTvStar3;
		TextView mTvStar4;
		TextView mTvStar5;

	}

	private void resetLayout(DetailsCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_home_business_comment_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mLlShopInfo = (LinearLayout) view
				.findViewById(R.id.ll_item_shop_zongti);
		holder.mTvShopName = (TextView) view
				.findViewById(R.id.tv_item_shop_business_name);
		holder.mTvShopStar = (TextView) view
				.findViewById(R.id.tv_shop_comment_total_star);
		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_item_home_business_comment_name);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_business_comment_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_business_comment_time);

		holder.mTvStar1 = (TextView) view
				.findViewById(R.id.tv_item_shop_comment_stars_1);
		holder.mTvStar2 = (TextView) view
				.findViewById(R.id.tv_item_shop_comment_stars_2);
		holder.mTvStar3 = (TextView) view
				.findViewById(R.id.tv_item_shop_comment_stars_3);
		holder.mTvStar4 = (TextView) view
				.findViewById(R.id.tv_item_shop_comment_stars_4);
		holder.mTvStar5 = (TextView) view
				.findViewById(R.id.tv_item_shop_comment_stars_5);

		holder.mTvShopStar1 = (TextView) view
				.findViewById(R.id.tv_item_shop_stars_1);
		holder.mTvShopStar2 = (TextView) view
				.findViewById(R.id.tv_item_shop_stars_2);
		holder.mTvShopStar3 = (TextView) view
				.findViewById(R.id.tv_item_shop_stars_3);
		holder.mTvShopStar4 = (TextView) view
				.findViewById(R.id.tv_item_shop_stars_4);
		holder.mTvShopStar5 = (TextView) view
				.findViewById(R.id.tv_item_shop_stars_5);

	}

}