package com.collcloud.yaohe.activity.business.myfans;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.MyFansList;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class BusinessFansAdapter extends BaseAdapter {

	Context context;
	List<MyFansList> mDatas;
	private static ImageLoader mImageLoader;

	public BusinessFansAdapter(Context context){
		this.context = context;
	}
	// 构造传参
	public BusinessFansAdapter(Context context, List<MyFansList> data) {
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());

		this.context = context;
		this.mDatas = data;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void refreshData(List<MyFansList> data_sc) {
		this.mDatas = data_sc;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {

		ViewHolder vHolder = null;

		if (view == null) {
			vHolder = new ViewHolder();
			view = View.inflate(context, R.layout.item_business_my_fans, null);
			LinearLayout llLayout = (LinearLayout) view
					.findViewById(R.id.ll_item_business_fans);
			SupportDisplay.resetAllChildViewParam(llLayout);

			// 获取布局中的组件
			vHolder.mTvName = (TextView) view
					.findViewById(R.id.tv_bus_item_fans_name);
			vHolder.mIvFace = (ImageView) view
					.findViewById(R.id.im_item_per_fans_img);
			vHolder.mBtnSendMSG = (Button) view
					.findViewById(R.id.bt_bus_item_fans_fxx);
			vHolder.mRlContentLayout = (RelativeLayout) view
					.findViewById(R.id.rl_bus_fans_item_content);

			// 绑定数据
			view.setTag(vHolder);

		} else {
			vHolder = (ViewHolder) view.getTag();
		}

		MyFansList fansInfo = mDatas.get(position);
		if (!Utils.isStringEmpty(fansInfo.nickname)) {
			vHolder.mTvName.setText(fansInfo.nickname);
		} else {
			vHolder.mTvName.setText("吆喝用户");
		}

		ImageListener listener = ImageLoader.getImageListener(vHolder.mIvFace,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (!Utils.isStringEmpty(fansInfo.face)) {
			mImageLoader.get(fansInfo.face, listener,context.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_width),context.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_height));
		}

		final String member_id = fansInfo.member_id;
		if(null==fansInfo.nickname || "".equals(fansInfo.nickname) || "null".equals(fansInfo.nickname)) {
			fansInfo.nickname="吆喝用户";
		}
		final String nickname = fansInfo.nickname;

		vHolder.mBtnSendMSG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnMsgListener != null) {
					mOnMsgListener.onSendMsgListener(position, member_id,
							nickname);
				}
			}
		});

		return view;
	}

	class ViewHolder {

		TextView mTvName;
		ImageView mIvFace;
		RelativeLayout mRlContentLayout;
		Button mBtnSendMSG;

	}

	public interface OnSendMsgListener {
		void onSendMsgListener(int position, String member_id, String nickname);
	}

	private OnSendMsgListener mOnMsgListener = null;

	public void setOnCollectionItemListerner(OnSendMsgListener listener) {
		this.mOnMsgListener = listener;
	}
}