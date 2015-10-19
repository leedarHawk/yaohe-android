package com.collcloud.yaohe.activity.person.guanzhubusiness;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.GuanZhunBusiList.GuanZhunBusi;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class PersonFollowAdapter extends BaseAdapter {

	// 上下文对象
	private Context mContext;
	// 数据
	private List<GuanZhunBusi> mListData;

	private static ImageLoader mImageLoader;
	private int mRightWidth = 0;

	// 构造函数
	public PersonFollowAdapter(Context mContext,
			List<GuanZhunBusi> data_mService, int rightWidth) {
		this.mContext = mContext;
		this.mListData = data_mService;
		mRightWidth = rightWidth;
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		GuanZhunBusi followInfo = mListData.get(position);
		PersonFollowHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_person_follow, parent, false);
			LinearLayout layout = (LinearLayout) convertView
					.findViewById(R.id.ll_mine_guanzhu_root);
			SupportDisplay.resetAllChildViewParam(layout);
			holder = new PersonFollowHolder();
			holder.item_left = (LinearLayout) convertView
					.findViewById(R.id.ll_person_follow_content);
			holder.item_right = (LinearLayout) convertView
					.findViewById(R.id.ll_menu_follow);
			holder.mTvType = (TextView) convertView
					.findViewById(R.id.tv_person_item_guanzhu_shop_type);
			holder.mTvTitlte = (TextView) convertView
					.findViewById(R.id.tv_person_item_guanzhu_shop_name);
			holder.mIvFace = (ImageView) convertView
					.findViewById(R.id.im_person_guanzhu_item_shop_photo);

			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (PersonFollowHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.item_left.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		holder.item_right.setLayoutParams(lp2);

		final String faceString = followInfo.face;

		if (!Utils.isStringEmpty(followInfo.title)) {
			holder.mTvTitlte.setText(followInfo.title);
		}
		if (!Utils.isStringEmpty(followInfo.class_title)) {
			holder.mTvType.setText(followInfo.class_title);
		}
		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvFace,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		if (!Utils.isStringEmpty(faceString)) {
			mImageLoader.get(faceString, listener);
		}

		holder.item_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		return convertView;
	}

	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}

	public void refreshData(List<GuanZhunBusi> data) {
		mListData = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mListData == null ? 0 : mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static  class PersonFollowHolder {

		LinearLayout item_left;
		LinearLayout item_right;
		TextView mTvType;
		TextView mTvTitlte;
		ImageView mIvFace;
	}

}