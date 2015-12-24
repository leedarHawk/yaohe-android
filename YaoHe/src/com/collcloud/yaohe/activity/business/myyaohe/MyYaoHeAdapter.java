package com.collcloud.yaohe.activity.business.myyaohe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.ui.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyYaoHeAdapter extends BaseAdapter {

	private Context mContext;
	// 上下文对象
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options; // 加载参数
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private LayoutInflater mLayoutInflater = null;

	/** 首页 活动消息类型， 0 ： 优惠券 */
	public static final String TYPE_YOUHUI = "0";
	/** 首页 活动消息类型， 1 ： 会员卡 */
	public static final String TYPE_CARD = "1";
	/** 首页 活动消息类型， 2 ： 活动 */
	public static final String TYPE_HUODONG = "2";
	/** 首页 活动消息类型， 3 ： 新品 */
	public static final String TYPE_XINPIN = "3";
	private int mRightWidth = 0;

	private List<FourService> mListData = new ArrayList<FourService>();

	public MyYaoHeAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public MyYaoHeAdapter(Context context, List<FourService> datas,
			DisplayImageOptions options, int rightWidth) {
		this.mContext = context;
		this.mListData = datas;
		mRightWidth = rightWidth;
		mLayoutInflater = LayoutInflater.from(mContext);
		this.options = options;
	}

	@Override
	public int getCount() {
		return mListData == null ? 0 : mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnMyYaoheListener {
		void onMyYaoheClick(int position, String type, String callID,
				String member_id, String serviceId);
	}

	private OnMyYaoheListener mOnMyYaoheItemListener = null;

	public void setOnMyYaoheListerner(OnMyYaoheListener listener) {
		this.mOnMyYaoheItemListener = listener;
	}

	public void refresh(List<FourService> data) {
		this.mListData = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		MyCallContent holder;
		FourService serviceInfo = null;
		if (mListData != null && mListData.size() > 0) {
			serviceInfo = mListData.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.item_business_fwgl_content, null);

			holder = new MyCallContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MyCallContent) convertView.getTag();
		}

		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
//		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
//				LayoutParams.MATCH_PARENT);
//		holder.item_right.setLayoutParams(lp2);
//
//		holder.item_right.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (mListener != null) {
//					mListener.onRightItemClick(v, position);
//				}
//			}
//		});

		if (serviceInfo != null && serviceInfo.zan_num != null) {
			holder.mTvZan.setText(serviceInfo.zan_num);
		}
		if (serviceInfo != null && serviceInfo.collection_num != null) {
			holder.mTvCollection.setText(serviceInfo.collection_num);
		}
		if (serviceInfo != null && serviceInfo.comment_num != null) {
			holder.mTvComment.setText(serviceInfo.comment_num);
		}
		if (serviceInfo != null && serviceInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			holder.mTvDate.setText(serviceInfo.addtime);
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (!Utils.isStringEmpty(serviceInfo.img)) {
			imageLoader.displayImage(serviceInfo.img, holder.mIvThum, options,
					animateFirstListener);
		}

		if (serviceInfo != null && serviceInfo.type != null) {
			String type = serviceInfo.type;

			if (type.equals(TYPE_YOUHUI)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mTvContent.setText(serviceInfo.title);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals(TYPE_CARD)) {
				holder.mTvContent.setText(serviceInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals(TYPE_XINPIN)) {
				holder.mTvContent.setText(serviceInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals(TYPE_HUODONG)) {
				holder.mTvContent.setText(serviceInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else {
				holder.mTvContent.setText(serviceInfo.content);
				holder.mIvTag.setVisibility(View.GONE);
			}
			holder.diviler.setVisibility(View.GONE);
			holder.ll_zan_pinlun.setVisibility(View.GONE);
		}

		final String type = serviceInfo.type;
		final String memberID = serviceInfo.member_id;
		final String callID = serviceInfo.id;
		final String service_id = serviceInfo.service_id;
		// 点击吆喝商家信息
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (mOnMyYaoheItemListener != null) {
//					mOnMyYaoheItemListener.onMyYaoheClick(position, type,
//							callID, memberID, service_id);
//				}
//			}
//		});

		return convertView;
	}

	private class MyCallContent {

		LinearLayout item_left;
		//LinearLayout item_right;

		/** 简介 */
		TextView mTvContent;
		RelativeLayout mRlBusinessContent;
		/** 日期时间 */
		TextView mTvDate;
		/** 列表简介图片 */
		ImageView mIvThum;
		/** 消息活动类型 */
		ImageView mIvTag;
		TextView mTvZan;
		TextView mTvCollection;
		TextView mTvComment;
		View diviler;
		LinearLayout ll_zan_pinlun;
	}

	private void resetLayout(MyCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.swipe_fwgl_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.item_left = (LinearLayout) view
				.findViewById(R.id.ll_service_left_contnet);
//		holder.item_right = (LinearLayout) view
//				.findViewById(R.id.ll_menu_fwgl_right);

		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_myfwgl_shop_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.ll_item_myfwgl_image_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_myfwgl_shop_time);
		holder.diviler = view.findViewById(R.id.item_myfwgl_content_diviler_2);
		holder.ll_zan_pinlun = (LinearLayout)view.findViewById(R.id.ll_item_myfwgl_zan_pinlun);
		holder.mTvZan = (TextView) view.findViewById(R.id.tv_item_myfwgl_zan);
		holder.mTvComment = (TextView) view
				.findViewById(R.id.tv_item_myfwgl_pinlun);
		holder.mTvCollection = (TextView) view
				.findViewById(R.id.tv_item_myfwgl_shoucang);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_myfwgl_shop_image);
		holder.mIvTag = (ImageView) view
				.findViewById(R.id.iv_item_myfwgl_shop_image_tags);
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
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