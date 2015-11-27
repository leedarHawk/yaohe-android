package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.SCInfoList;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class ShouCangBusinessAdapter extends BaseAdapter {

	Context context;
	List<SCInfoList> data_sc;
	private static ImageLoader mImageLoader;

	// 构造传参
	public ShouCangBusinessAdapter(Context context, List<SCInfoList> data) {
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());

		this.context = context;
		this.data_sc = data;
	}

	@Override
	public int getCount() {
		return data_sc == null ? 0 : data_sc.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data_sc.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void refreshData(List<SCInfoList> data_sc) {
		this.data_sc = data_sc;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {

		ViewHolder vHolder = null;

		if (arg1 == null) {
			vHolder = new ViewHolder();
			arg1 = View.inflate(context, R.layout.item_shoucang_list, null);
			LinearLayout llLayout = (LinearLayout) arg1
					.findViewById(R.id.ll_person_shoucang_root);
			SupportDisplay.resetAllChildViewParam(llLayout);

			// 获取布局中的组件
			vHolder.tv_who_ = (TextView) arg1.findViewById(R.id.tv_who_);
			vHolder.tv_show_shoucang_time = (TextView) arg1
					.findViewById(R.id.tv_show_shoucang_time);
			vHolder.tv_item_zhekou_content = (TextView) arg1
					.findViewById(R.id.tv_item_zhekou_content);
			vHolder.iv_item_shop_img_tags = (ImageView) arg1
					.findViewById(R.id.iv_item_shop_img_tags);
			vHolder.iv_item_shop_img = (ImageView) arg1
					.findViewById(R.id.iv_item_shop_img);
			vHolder.mRlContentLayout = (RelativeLayout) arg1
					.findViewById(R.id.rl_item_zhekou_content);
			
			vHolder.fl_item_home_shop_img = (FrameLayout) arg1
					.findViewById(R.id.fl_item_home_shop_img);

			// 绑定数据
			arg1.setTag(vHolder);

		} else {
			vHolder = (ViewHolder) arg1.getTag();
		}

		SCInfoList sc = data_sc.get(position);
		if (!Utils.isStringEmpty(sc.shop_name)) {
			vHolder.tv_who_.setText(sc.shop_name);// 改成店铺名
		}
		if (!Utils.isStringEmpty(sc.addtime)) {
			vHolder.tv_show_shoucang_time.setText(sc.addtime);
		}

		ImageListener listener = ImageLoader.getImageListener(
				vHolder.iv_item_shop_img,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (!Utils.isStringEmpty(sc.img1)) {
			vHolder.fl_item_home_shop_img.setVisibility(View.VISIBLE);
			mImageLoader.get(sc.img1, listener,context.getResources().getDimensionPixelSize(R.dimen.max_list_width),context.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		} else {
			vHolder.fl_item_home_shop_img.setVisibility(View.GONE);
		}

		if (!Utils.isStringEmpty(sc.type)) {
			if (sc.type.equals("0")) {

				vHolder.tv_item_zhekou_content.setText(sc.title);
				vHolder.iv_item_shop_img_tags.setVisibility(View.VISIBLE);
				vHolder.iv_item_shop_img_tags
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (sc.type.equals("1")) {
				vHolder.tv_item_zhekou_content.setText(sc.title);
				vHolder.iv_item_shop_img_tags.setVisibility(View.VISIBLE);
				vHolder.iv_item_shop_img_tags
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);

			} else if (sc.type.equals("2")) {
				vHolder.iv_item_shop_img_tags.setVisibility(View.VISIBLE);
				vHolder.iv_item_shop_img_tags
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
				vHolder.tv_item_zhekou_content.setText(sc.title);

			} else if (sc.type.equals("3")) {

				vHolder.iv_item_shop_img_tags.setVisibility(View.VISIBLE);
				vHolder.iv_item_shop_img_tags
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
				vHolder.tv_item_zhekou_content.setText(sc.title);
			} else if (sc.type.equals("4")) {

				vHolder.tv_item_zhekou_content.setText(sc.title);
				vHolder.iv_item_shop_img_tags.setVisibility(View.GONE);
			} else {
				vHolder.iv_item_shop_img_tags.setVisibility(View.GONE);
			}
		}
//		final String deleteID = sc.id ;
//
//		vHolder.iv_item_shop_img
//				.setOnLongClickListener(new OnLongClickListener() {
//
//					@Override
//					public boolean onLongClick(View v) {
//						if (mOnCollectionLongListener != null) {
//							mOnCollectionLongListener.onLongCollectionClick(
//									position, deleteID);
//						}
//						return false;
//					}
//				});

		return arg1;
	}

	class ViewHolder {

		private TextView tv_who_;
		private TextView tv_show_shoucang_time;
		private TextView tv_item_zhekou_content;
		private ImageView iv_item_shop_img_tags;
		private ImageView iv_item_shop_img;
		private FrameLayout fl_item_home_shop_img;
		RelativeLayout mRlContentLayout;

	}

	public interface OnPersonCollectionListener {
		void onLongCollectionClick(int position, String deleteID);
	}

	private OnPersonCollectionListener mOnCollectionLongListener = null;

	public void setOnCollectionItemListerner(OnPersonCollectionListener listener) {
		this.mOnCollectionLongListener = listener;
	}
}
