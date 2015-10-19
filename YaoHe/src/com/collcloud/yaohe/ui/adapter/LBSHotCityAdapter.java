package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;

public class LBSHotCityAdapter extends BaseAdapter {

	private List<String> mHotCitys;
	private Context mContext;
	private LayoutInflater mInflater;

	public LBSHotCityAdapter(Context context, List<String> citys) {
		this.mHotCitys = citys;
		mContext = context;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {

		return mHotCitys.size();
	}

	@Override
	public Object getItem(int position) {

		return mHotCitys.get(position);
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
					R.layout.item_home_lbs_gridview_content, null);
			RelativeLayout rlLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_activity_lbs_hot_city_root);
			holder.mTvHotCity = (TextView) convertView
					.findViewById(R.id.tv_item_lbs_hot_city);
			SupportDisplay.resetAllChildViewParam(rlLayout);
			convertView.setTag(holder);
		} else {
			holder = (ImageHolder) convertView.getTag();
		}
		// TODO 文字赋值
		holder.mTvHotCity.setText(mHotCitys.get(position));
		return convertView;
	}

	class ImageHolder {
		TextView mTvHotCity = null;
	}

	public List<String> getHotCitys() {
		return mHotCitys;
	}

	public void setHotCitys(List<String> citys) {
		this.mHotCitys = citys;
	}

}
