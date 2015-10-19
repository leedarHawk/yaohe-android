package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.GuanZhunBusiList.GuanZhunBusi;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

public class PersonGuanzhuAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;
	private static ImageLoader mImageLoader;
	private List<GuanZhunBusi> mFollowData = new ArrayList<GuanZhunBusi>();

	// 构造传参
	public PersonGuanzhuAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	// 构造传参
	public PersonGuanzhuAdapter(Context context, List<GuanZhunBusi> data) {
		this.mContext = context;
		this.mFollowData = data;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {
		return mFollowData == null ? 0 : mFollowData.size();
	}

	@Override
	public Object getItem(int position) {
		return mFollowData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refresh(List<GuanZhunBusi> data) {
		this.mFollowData = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		PersonFollowHolder holder;
		GuanZhunBusi guanzhu = null;
		guanzhu = mFollowData.get(position);
		if (convertView == null) {
			holder = new PersonFollowHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_guanzhu_list,
					null);
			LinearLayout rlLayout = (LinearLayout) convertView
					.findViewById(R.id.rl_mine_guanzhu_root);
			SupportDisplay.resetAllChildViewParam(rlLayout);

			// 获取布局中的组件
			holder.mIvFace = (ImageView) convertView
					.findViewById(R.id.im_per_guanzhu_item_shop_photo);
			holder.mTvFollowTitle = (TextView) convertView
					.findViewById(R.id.tv_per_item_guanzhu_shop_name);
			holder.mTvFollowType = (TextView) convertView
					.findViewById(R.id.tv_per_item_guanzhu_shop_type);
			holder.rlFollowContent = (RelativeLayout) convertView
					.findViewById(R.id.rl_per_guanzhu_item_shop_con);
			// 绑定数据
			convertView.setTag(holder);
		} else {
			holder = (PersonFollowHolder) convertView.getTag();
		}

		ImageListener listener = ImageLoader.getImageListener(holder.mIvFace,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (guanzhu != null && !Utils.isStringEmpty(guanzhu.face)) {
			mImageLoader.get(guanzhu.face, listener);
		}
		String shopID = null;
		String member_id = null;
		if (!Utils.isStringEmpty(guanzhu.id)) {
			shopID = guanzhu.id;
		}
		if (!Utils.isStringEmpty(guanzhu.member_id)) {
			member_id = guanzhu.member_id;
		}
		CCLog.i("face adapter", " " + guanzhu.face);

		if (!Utils.isStringEmpty(guanzhu.class_title)) {
			holder.mTvFollowType.setText(guanzhu.class_title);
		} else {
			holder.mTvFollowType.setText("美食");
		}

		if (!Utils.isStringEmpty(guanzhu.title)) {
			holder.mTvFollowTitle.setText(guanzhu.title);
		} else {
			holder.mTvFollowTitle.setText("");
		}
		// vHolder.rlFollowContent.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (mOnFollowItemListener != null) {
		// mOnFollowItemListener.onFollowItemClick(position,
		// member_id, shopID);
		// }
		//
		// }
		// });
		// vHolder.rlFollowContent
		// .setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// Toast.makeText(mContext, "长按事件。", 1000).show();
		// return false;
		// }
		// });

		return convertView;
	}

	private class PersonFollowHolder {

		TextView mTvFollowTitle;
		TextView mTvFollowType;
		ImageView mIvFace;
		RelativeLayout rlFollowContent;

	}

	public interface OnPerFollowListener {
		void onFollowItemClick(int position, String member_id, String shopID);
	}

	private OnPerFollowListener mOnFollowItemListener = null;

	public void setOnFollowItemListerner(OnPerFollowListener listener) {
		this.mOnFollowItemListener = listener;
	}
}
