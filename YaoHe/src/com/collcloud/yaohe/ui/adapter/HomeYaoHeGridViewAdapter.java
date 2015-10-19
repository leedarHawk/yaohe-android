package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;

/**
 * 主页-吆喝啦详情页面适配器
 * 
 * @ClassName HomeYaoHeGridViewAdapter
 * @author CollCloud_小米
 */
public class HomeYaoHeGridViewAdapter extends BaseAdapter {

	private List<String> mImgPaths;
	private Context mContext;
	private LayoutInflater mInflater;

	public HomeYaoHeGridViewAdapter(Context context, List<String> mImgPaths) {
		this.mImgPaths = mImgPaths;
		mContext = context;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {

		return mImgPaths.size();
	}

	@Override
	public Object getItem(int position) {

		return mImgPaths.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageHolder holder = null;
		if (null == convertView) {
			holder = new ImageHolder();
			convertView = mInflater.inflate(
					R.layout.item_home_yaohe_gridview_img, null);
			RelativeLayout rlLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_activity_home_gv_img);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.rl_activity_home_gv_yaohe_img);
			SupportDisplay.resetAllChildViewParam(rlLayout);
			convertView.setTag(holder);
		} else {
			holder = (ImageHolder) convertView.getTag();
		}

		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
		// TODO
		ImageListener listener = ImageLoader.getImageListener(holder.imageView,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		imageLoader.get(mImgPaths.get(position), listener,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		return convertView;
	}

	class ImageHolder {
		ImageView imageView = null;
	}

	public List<String> getImgPaths() {
		return mImgPaths;
	}

	public void setImgPaths(List<String> imgPaths) {
		this.mImgPaths = imgPaths;
	}

}
