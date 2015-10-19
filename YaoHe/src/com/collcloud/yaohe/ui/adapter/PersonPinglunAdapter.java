package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class PersonPinglunAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private static ImageLoader mImageLoader;

	private List<CallCommentInfo> mCallCommentList = new ArrayList<CallCommentInfo>();

	public PersonPinglunAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public PersonPinglunAdapter(Context context, List<CallCommentInfo> datas) {
		this.mContext = context;
		this.mCallCommentList = datas;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
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

	public void refresh(List<CallCommentInfo> data) {
		this.mCallCommentList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DetailsCallContent holder;
		CallCommentInfo callInfo = null;
		if (mCallCommentList != null && mCallCommentList.size() > 0) {
			callInfo = mCallCommentList.get(position);
		}
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_home_call_comment_details, null);

			holder = new DetailsCallContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (DetailsCallContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (callInfo != null && callInfo.content != null) {
			holder.mTvContent.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.is_anonymous != null) {
			if (callInfo.is_anonymous.equals("1")) {
				holder.mTvName.setText("匿名用户");
			}else {
				if (!Utils.isStringEmpty(callInfo.nickname)) {
					holder.mTvName.setText(callInfo.nickname);
				}else {
					holder.mTvName.setText("吆喝用户");
				}
				
			}
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			holder.mTvDate.setText(callInfo.addtime);
			
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (callInfo != null && callInfo.face != null) {
			mImageLoader.get(callInfo.face, listener);
		}

		return convertView;
	}

	private class DetailsCallContent {

		TextView mTvName;
		TextView mTvContent;
		RelativeLayout mRlBusinessContent;
		/** 日期时间 */
		TextView mTvDate;
		/** 列表简介图片 */
		ImageView mIvThum;

	}

	private void resetLayout(DetailsCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_home_call_comment_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_name);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.rl_item_home_call_comment_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_time);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_home_call_comment_img);
	}

}