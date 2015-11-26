package com.collcloud.yaohe.activity.search;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DeatilsSearchInfo.SearchShopInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;
import com.meg7.widget.CustomShapeImageView;

public class SearchShopAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private static ImageLoader mImageLoader;

	private List<SearchShopInfo> mShopList = new ArrayList<SearchShopInfo>();

	public SearchShopAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public SearchShopAdapter(Context context, List<SearchShopInfo> datas) {
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

	public void refresh(List<SearchShopInfo> data) {
		this.mShopList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		SearchShopContent holder;
		SearchShopInfo callInfo = null;
		if (mShopList != null && mShopList.size() > 0) {
			callInfo = mShopList.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(
					R.layout.item_search_shop_content, null);

			holder = new SearchShopContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (SearchShopContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_default_logo,
				R.drawable.icon_yaohe_default_logo);

		if (callInfo != null && callInfo.title != null) {
			holder.mTvTitle.setText(callInfo.title);
		}

		if (callInfo != null && !Utils.isStringEmpty(callInfo.face)) { 
			mImageLoader.get(callInfo.face, listener,mContext.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_width),mContext.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_height));
		} else {
			holder.mIvThum.setImageResource(R.drawable.icon_yaohe_default_logo);
		}
		
		final String shopID = callInfo.id;
		final String memberID = callInfo.member_id;

		return convertView;
	}

	private class SearchShopContent {

		TextView mTvTitle;
		RelativeLayout mRlBusinessContent;
		CustomShapeImageView mIvThum;

	}

	private void resetLayout(SearchShopContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_search_shop_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvTitle = (TextView) view
				.findViewById(R.id.tv_search_shop_title);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.ll_item_search_shhopcontent);

		holder.mIvThum = (CustomShapeImageView) view.findViewById(R.id.iv_search_shop_img);
	}

}