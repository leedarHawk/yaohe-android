package com.collcloud.yaohe.activity.business.myservicemanager;

import java.util.ArrayList;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessServiceInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class BusinessServiceManagerAdapter extends BaseAdapter {

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

	private static ImageLoader mImageLoader;
	private int mRightWidth = 0;

	private List<BusinessServiceInfo> mBusinessServiceList = new ArrayList<BusinessServiceInfo>();

	public BusinessServiceManagerAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public BusinessServiceManagerAdapter(Context context,
			List<BusinessServiceInfo> datas, int rightWidth) {
		this.mContext = context;
		this.mBusinessServiceList = datas;
		mRightWidth = rightWidth;
		mLayoutInflater = LayoutInflater.from(mContext);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {
		return mBusinessServiceList == null ? 0 : mBusinessServiceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBusinessServiceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnBusinessServiceManagerListener {
		void onServiceClick(int position, String type, String member_id,
				String serviceId);
	}

	private OnBusinessServiceManagerListener mOnItemClickListener = null;

	public void setOnServiceListerner(OnBusinessServiceManagerListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void refresh(List<BusinessServiceInfo> data) {
		this.mBusinessServiceList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ServiceContent holder;
		BusinessServiceInfo serviceInfo = null;
		if (mBusinessServiceList != null && mBusinessServiceList.size() > 0) {
			serviceInfo = mBusinessServiceList.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.item_business_service_manager, null);

			holder = new ServiceContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ServiceContent) convertView.getTag();
		}

		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		holder.item_right.setLayoutParams(lp2);

		holder.item_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		
		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (serviceInfo != null && serviceInfo.title != null) {
			holder.mTvContent.setText(serviceInfo.title);
		}
		if (serviceInfo != null && serviceInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
			// if (serviceInfo.addtime.length() == 10) {
			// serviceInfo.addtime = serviceInfo.addtime + "000";
			// }
			// SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
			// Date date = new Date(Long.valueOf(serviceInfo.addtime));
			// holder.mTvDate.setText(df.format(date));
			holder.mTvDate.setText(serviceInfo.addtime);
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (!Utils.isStringEmpty(serviceInfo.img)) {
			//mImageLoader.get(serviceInfo.img, listener);
			mImageLoader.get(serviceInfo.img, listener,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		}

		if (serviceInfo != null && serviceInfo.type != null) {
			String type = serviceInfo.type;
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
			} else {
				holder.mIvTag.setVisibility(View.GONE);
			}
		}

		final String type = serviceInfo.type;
		final String memberID = serviceInfo.member_id;
		final String service_id = serviceInfo.id;
		// 点击吆喝商家信息
		holder.mRlBusinessContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onServiceClick(position, type,
							memberID, service_id);
				}
			}
		});

		return convertView;
	}

	private class ServiceContent {

		RelativeLayout item_left;
		LinearLayout item_right;

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

	private void resetLayout(ServiceContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_home_business_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.item_left = (RelativeLayout) view
				.findViewById(R.id.rl_business_service_manager_content);
		holder.item_right = (LinearLayout) view
				.findViewById(R.id.ll_business_service_manager_delete);
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

	/**
	 * 单击事件监听器
	 */
	private OnRightClickListener mListener = null;

	public void setOnRightItemClickListener(OnRightClickListener listener) {
		mListener = listener;
	}

	public interface OnRightClickListener {
		void onRightItemClick(View v, int position);
	}
}