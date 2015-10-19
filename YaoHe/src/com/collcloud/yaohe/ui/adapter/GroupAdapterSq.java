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
import com.collcloud.yaohe.entity.BusinessArea;

/**
 * @类说明 商圈popup适配器
 * @author Administrator
 *
 */
public class GroupAdapterSq extends BaseAdapter {

	private Context context;

	private List<BusinessArea> list;

	public GroupAdapterSq(Context context, List<BusinessArea> list) {

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

		
		ViewHolder holder;
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.item_popup_sq, null);
			
			holder=new ViewHolder();
			convertView.setTag(holder);
			LinearLayout linearLayout = (LinearLayout) convertView
					.findViewById(R.id.ll_shangquan_viewgroup);
			SupportDisplay.resetAllChildViewParam(linearLayout);
			holder.groupItemsq=(TextView) convertView.findViewById(R.id.groupItem_sq);
		}
		else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.groupItemsq.setText(list.get(position).getTitle());

		return convertView;
	}

	static class ViewHolder {
		TextView groupItemsq;
	}

}
