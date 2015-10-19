package com.collcloud.yaohe.activity.search;

import java.util.ArrayList;
import java.util.List;

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
import com.collcloud.yaohe.api.info.DeatilsSearchInfo.SearchYaoheInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class SearchCallAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private static ImageLoader mImageLoader;

	private List<SearchYaoheInfo> mShopList = new ArrayList<SearchYaoheInfo>();

	public SearchCallAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public SearchCallAdapter(Context context, List<SearchYaoheInfo> datas) {
		this.mContext = context;
		this.mShopList = datas;
		mLayoutInflater = LayoutInflater.from(mContext);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {
		return mShopList == null ? 0 : mShopList.size();
	}

	@Override
	public Object getItem(int position) {
		return mShopList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// public interface OnBusinessInfoListener {
	// void onBusinessInfoClick(int position, String type, String callId,
	// String serviceId, String memberId);
	// }
	// private OnBusinessInfoListener mOnItemClickListener = null;
	// public void setOnHomeItemClickListener(OnBusinessInfoListener listener) {
	// this.mOnItemClickListener = listener;
	// }

	public void refresh(List<SearchYaoheInfo> data) {
		this.mShopList = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		SearchCllContent holder;
		SearchYaoheInfo callInfo = null;
		if (mShopList != null && mShopList.size() > 0) {
			callInfo = mShopList.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.item_search_call_content, null);

			holder = new SearchCllContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (SearchCllContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (callInfo != null && callInfo.content != null) {
			holder.mTvTitle.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.shop_title != null) {
			holder.mTvShopName.setText(callInfo.shop_title);
		}

		if (!Utils.isStringEmpty(callInfo.img)) {
			mImageLoader.get(callInfo.img, listener);
		}
		String type = callInfo.type;
		if (!Utils.isStringEmpty(type)) {
			if (type.equals("0")) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals("1")) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals("2")) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else if (type.equals("3")) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals("4")) {
				holder.mIvTag.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	private class SearchCllContent {

		TextView mTvTitle;
		TextView mTvShopName;
		RelativeLayout mRlBusinessContent;
		ImageView mIvTag;
		ImageView mIvThum;

	}

	private void resetLayout(SearchCllContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_search_call_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvTitle = (TextView) view
				.findViewById(R.id.tv_details_search_call_title);
		holder.mTvShopName = (TextView) view
				.findViewById(R.id.tv_details_search_call_shop);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_search_call_image);
		holder.mIvTag = (ImageView) view
				.findViewById(R.id.iv_item_search_call_image_tags);
	}

}