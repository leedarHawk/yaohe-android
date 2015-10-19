package com.collcloud.yaohe.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessCallInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class HomeBusinessCallAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	/** 首页 活动消息类型， 0 ： 优惠券 */
	public static final String TYPE_YOUHUI = "0";
	/** 首页 活动消息类型， 1 ： 会员卡 */
	public static final String TYPE_CARD = "1";
	/** 首页 活动消息类型， 2 ： 活动 */
	public static final String TYPE_HUODONG = "2";
	/** 首页 活动消息类型， 3 ： 新品 */
	public static final String TYPE_XINPIN = "3";
	/** 首页 活动消息类型， 4 ： 吆喝啦 */
	public static final String TYPE_DEFAULT = "4";

	private static ImageLoader mImageLoader;

	private List<BusinessCallInfo> mBusinessCallList = new ArrayList<BusinessCallInfo>();

	public HomeBusinessCallAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public HomeBusinessCallAdapter(Context context, List<BusinessCallInfo> datas) {
		this.mContext = context;
		this.mBusinessCallList = datas;
		mLayoutInflater = LayoutInflater.from(mContext);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {
		return mBusinessCallList == null ? 0 : mBusinessCallList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBusinessCallList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnBusinessInfoListener {
		void onBusinessInfoClick(int position, String type, String callId,
				String serviceId,String memberId);
	}

	private OnBusinessInfoListener mOnItemClickListener = null;

	public void setOnHomeItemClickListener(OnBusinessInfoListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void refresh(List<BusinessCallInfo> data) {
		this.mBusinessCallList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		BusinessCallContent holder;
		BusinessCallInfo callInfo = null;
		if (mBusinessCallList != null && mBusinessCallList.size() > 0) {
			callInfo = mBusinessCallList.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.item_home_business_info_content, null);

			holder = new BusinessCallContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (BusinessCallContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (callInfo != null && callInfo.content != null) {
			holder.mTvContent.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
			try {
				if (callInfo.addtime.length() == 10) {
					callInfo.addtime = callInfo.addtime + "000";
				}
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
				Date date = new Date(Long.valueOf(callInfo.addtime));
				holder.mTvDate.setText(df.format(date));
			} catch (NumberFormatException e) {
				holder.mTvDate.setText(callInfo.addtime);
			}
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (!Utils.isStringEmpty(callInfo.img)) {
			mImageLoader.get(callInfo.img, listener,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		}

		if (callInfo != null && callInfo.type != null) {
			String type = callInfo.type;
			if (type.equals(TYPE_YOUHUI)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals(TYPE_CARD)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals(TYPE_XINPIN)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals(TYPE_HUODONG)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else if (type.equals(TYPE_DEFAULT)) {
				holder.mIvTag.setVisibility(View.GONE);
			} else {
				holder.mIvTag.setVisibility(View.GONE);
			}
		}

		final String callID = callInfo.id;
		final String type = callInfo.type;
		final String memberID = callInfo.member_id;
		final String service_id = callInfo.service_id;
		// 点击吆喝商家信息
		holder.mRlBusinessContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onBusinessInfoClick(position, type,
							callID, service_id,memberID);
				}
			}
		});

		return convertView;
	}

	private class BusinessCallContent {

		/** 简介 */
		TextView mTvContent;
		RelativeLayout mRlBusinessContent;
		/** 日期时间 */
		TextView mTvDate;
		/** 列表简介图片 */
		ImageView mIvThum;
		/** 消息活动类型 */
		ImageView mIvTag;

	}

	private void resetLayout(BusinessCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_home_business_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_business_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.ll_item_business_image_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_business_time);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_home_business_image);
		holder.mIvTag = (ImageView) view
				.findViewById(R.id.iv_item_home_business_image_tags);
	}

}