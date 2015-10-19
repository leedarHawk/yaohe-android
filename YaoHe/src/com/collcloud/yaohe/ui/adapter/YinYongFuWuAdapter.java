package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

public class YinYongFuWuAdapter extends BaseAdapter {

	Context context;
	List<FourService> mDatas;
	LayoutInflater layout;
	private static ImageLoader mImageLoader;

	SparseBooleanArray selected;
	boolean isSingle = true;
	int old = -1;

	// 构造传参
	public YinYongFuWuAdapter(Context context, List<FourService> data) {

		this.context = context;
		this.mDatas = data;
		this.layout = LayoutInflater.from(context);
		selected = new SparseBooleanArray();
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());

	}

	@Override
	public int getCount() {
		if (mDatas == null) {
			return 0;
		}
		if (mDatas.size() > 20) {
			return 20;
		}
		return mDatas.size();
	}

	@Override
	public Object getItem(int arg0) {

		return mDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	public void setSelectedItem(int selected) {
		if (isSingle = true && old != -1) {
			this.selected.put(old, false);
		}
		this.selected.put(selected, true);
		old = selected;
	}

	public void refresh(List<FourService> data) {
		this.mDatas = data;
		notifyDataSetChanged();
	}

	public interface OnYaoHeItemListener {
		void onYaoHeItemClick(int position, ImageView ivCheck);
	}

	private OnYaoHeItemListener mOnYaoHeItemListener = null;

	public void setOnYaoHeItemClickListener(OnYaoHeItemListener listener) {
		this.mOnYaoHeItemListener = listener;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {

		// 获取布局
		View view = arg1;
		if (view == null) {

			view = layout.inflate(R.layout.item_fyh_service_list, null);
			LinearLayout rlLayout = (LinearLayout) view
					.findViewById(R.id.rl_item_fujin_shop_viewgroup);
			SupportDisplay.resetAllChildViewParam(rlLayout);
		}

		FourService fourService = (FourService) mDatas.get(position);
		// 获取布局中的组件
		TextView tv_service_list_shop_name = (TextView) view
				.findViewById(R.id.tv_item_fyh_shop_name);
		TextView tv_service_list_type = (TextView) view
				.findViewById(R.id.tv_item_fyh_type_);
		ImageView ivServiceImg = (ImageView) view
				.findViewById(R.id.iv_fyh_yinyong_img);

		ImageListener listener = ImageLoader.getImageListener(ivServiceImg,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		TextView tv_service_list_yhcontent = (TextView) view
				.findViewById(R.id.tv_item_fyh_service_yhtext);
		final ImageView iv_service_list_check = (ImageView) view
				.findViewById(R.id.im_item_fyh_sel);
		RelativeLayout rl_service_list_check = (RelativeLayout) view
				.findViewById(R.id.rl_fyh_shop_name_type_info);
		rl_service_list_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (mOnYaoHeItemListener != null) {
					mOnYaoHeItemListener.onYaoHeItemClick(position,
							iv_service_list_check);
				}

			}
		});
		iv_service_list_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnYaoHeItemListener != null) {
					mOnYaoHeItemListener.onYaoHeItemClick(position,
							iv_service_list_check);
				}
			}
		});
		if (position != old) {
			iv_service_list_check
					.setBackgroundResource(R.drawable.icon_fujin_niming_off);
		} else {
			iv_service_list_check
					.setBackgroundResource(R.drawable.icon_fujin_niming_on);
		}

		// 引用服务的标题
		tv_service_list_shop_name.setText(fourService.title);

		if (!Utils.isStringEmpty(fourService.img)) {
			CCLog.d("YinYongFuWuAdapter", "img url:"+fourService.img);
			mImageLoader.get(fourService.img, listener,160,200);
		}

		// 0券 1卡 3新品 2活动 4无
		if (!Utils.isStringEmpty(fourService.type)) {
			String type = fourService.type;
			if (type.equals("0")) {
				tv_service_list_yhcontent.setText("优惠券");
			} else if (type.equals("1")) {
				tv_service_list_yhcontent.setText("会员卡");
			} else if (type.equals("2")) {
				tv_service_list_yhcontent.setText("活动");
			} else if (type.equals("3")) {
				tv_service_list_yhcontent.setText("新品");
			}
		} else {
			tv_service_list_type.setText("");
		}

		// tv_service_list_yhcontent.setText(fourService.content);

		return view;
	}
}
