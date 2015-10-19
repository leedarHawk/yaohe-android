package com.collcloud.yaohe.activity.person.zan;

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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.PersonZanInfo.ZANInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;

public class PersonZanAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private static ImageLoader mImageLoader;

	private List<ZANInfo> mCallCommentList = new ArrayList<ZANInfo>();

	public PersonZanAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public PersonZanAdapter(Context context, List<ZANInfo> datas) {
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

	public void refresh(List<ZANInfo> data) {
		this.mCallCommentList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DetailsCallContent holder;
		ZANInfo callInfo = null;
		if (mCallCommentList != null && mCallCommentList.size() > 0) {
			callInfo = mCallCommentList.get(position);
		}
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_person_zan_content, null);

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
		ImageListener listener1 = ImageLoader.getImageListener(
				holder.mIvServiceImg, R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (callInfo != null && callInfo.content != null) {
			holder.mTvContent.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.nickname != null) {
			holder.mTvName.setText(callInfo.nickname);
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setText(callInfo.addtime);
		}

		if (callInfo != null && callInfo.face != null) {
			mImageLoader.get(callInfo.face, listener);
		}
		if (callInfo != null && callInfo.img != null) {
			mImageLoader.get(callInfo.img, listener1);
		}

		return convertView;
	}

	private class DetailsCallContent {

		TextView mTvName;
		TextView mTvContent;
		LinearLayout mLlContent;
		TextView mTvDate;
		ImageView mIvThum;
		ImageView mIvServiceImg;

	}

	private void resetLayout(DetailsCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_person_zan_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_yaohe_zan_face_name);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_yaohe_zan_content_service);
		holder.mLlContent = (LinearLayout) view
				.findViewById(R.id.ll_yaohe_zan_con_service);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_yaohe_zan_addtime);

		holder.mIvThum = (ImageView) view.findViewById(R.id.iv_yaohe_zan_face);
		holder.mIvServiceImg = (ImageView) view
				.findViewById(R.id.iv_yaohe_zan_service_img);
	}
}