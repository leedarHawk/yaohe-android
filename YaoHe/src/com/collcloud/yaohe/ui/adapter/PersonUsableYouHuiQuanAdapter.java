package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.YhqInfoList;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * @类说明：优惠券列表适配器
 * @author 赵洋洋
 */
public class PersonUsableYouHuiQuanAdapter extends BaseAdapter {

	Context context;
	List<YhqInfoList> mListData;
	LayoutInflater layoutInflater;
	private static ImageLoader mImageLoader;

	// 构造传参
	public PersonUsableYouHuiQuanAdapter(Context context, List<YhqInfoList> data) {

		this.context = context;
		this.mListData = data;
		layoutInflater = LayoutInflater.from(context);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {

		return mListData.size();
	}

	@Override
	public Object getItem(int position) {

		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void refresh(List<YhqInfoList> data) {
		this.mListData = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		PersonUserCoupon vHolder = null;
		// 获取布局
		View view = arg1;
		if (view == null) {
			vHolder = new PersonUserCoupon();
			view = layoutInflater.inflate(R.layout.item_usableyouhuiquan_list,
					null);
			// 获取布局中的组件
			RelativeLayout rlLayout = (RelativeLayout) view
					.findViewById(R.id.rl_mine_youhuiquan_root);
			SupportDisplay.resetAllChildViewParam(rlLayout);
			vHolder.im_usableyouhuiquan_shop_photo = (ImageView) view
					.findViewById(R.id.im_usableyouhuiquan_shop_photo);
			vHolder.tv_usableyouhuiquan_shop_name = (TextView) view
					.findViewById(R.id.tv_usableyouhuiquan_shop_name);
			vHolder.tv_usableyouhuiquan_info = (TextView) view
					.findViewById(R.id.tv_usableyouhuiquan_info);
			vHolder.tv_usableyouhuiquan_totime = (TextView) view
					.findViewById(R.id.tv_usableyouhuiquan_totime);
			vHolder.tv_usableyouhuiquan_num = (TextView) view
					.findViewById(R.id.tv_usableyouhuiquan_num);
			// 绑定数据
			view.setTag(vHolder);

		} else {
			vHolder = (PersonUserCoupon) view.getTag();
		}
		ImageListener listener = ImageLoader.getImageListener(
				vHolder.im_usableyouhuiquan_shop_photo,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		YhqInfoList yhq = mListData.get(arg0);
		vHolder.tv_usableyouhuiquan_num.setText("已使用" + yhq.use_number + "次");
		vHolder.tv_usableyouhuiquan_totime.setText("有效期至 " + yhq.valid_end);
		vHolder.tv_usableyouhuiquan_info.setText(yhq.content);
		vHolder.tv_usableyouhuiquan_shop_name.setText(yhq.title);
		if (yhq.img1 != null && !Utils.isStringEmpty(yhq.img1)) {
			mImageLoader.get(yhq.img1, listener);
		}
		return view;
	}

	class PersonUserCoupon {

		private TextView tv_usableyouhuiquan_num;
		private TextView tv_usableyouhuiquan_totime;
		private TextView tv_usableyouhuiquan_info;
		private TextView tv_usableyouhuiquan_shop_name;
		private ImageView im_usableyouhuiquan_shop_photo;// 相片
	}

}
