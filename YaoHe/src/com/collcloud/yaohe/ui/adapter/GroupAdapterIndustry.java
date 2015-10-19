package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.IndustryType;

/**
 * @类说明 行业popup适配器
 * @author Administrator
 * 
 */
public class GroupAdapterIndustry extends BaseAdapter {
	private Context context;
	private List<IndustryType> list;

	public GroupAdapterIndustry(Context context, List<IndustryType> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		ViewHolder holder1;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_popup_area, null);

			holder1 = new ViewHolder();
			convertView.setTag(holder1);
			LinearLayout linearLayout = (LinearLayout) convertView
					.findViewById(R.id.ll_item_adapter_group);
			SupportDisplay.resetAllChildViewParam(linearLayout);
			holder1.groupItemindustry = (TextView) convertView
					.findViewById(R.id.groupItem_area);
		} else {
			holder1 = (ViewHolder) convertView.getTag();
		}
		holder1.groupItemindustry.setText(list.get(position).getTitle());

		return convertView;
	}

	static class ViewHolder {
		TextView groupItemindustry;
	}

}
