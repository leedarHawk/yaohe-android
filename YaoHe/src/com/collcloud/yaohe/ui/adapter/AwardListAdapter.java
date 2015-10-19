package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.Award;

public class AwardListAdapter extends BaseAdapter {

	Context context;
	List<Award> data_award;

	// 构造传参
	public AwardListAdapter(Context context, List<Award> data) {

		this.context = context;
		this.data_award = data;
	}

	@Override
	public int getCount() {

		return data_award.size();
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (arg1 == null) {
			viewHolder = new ViewHolder();
			arg1 = View.inflate(context, R.layout.item_award_list, null);
			viewHolder.award_nickname = (TextView) arg1
					.findViewById(R.id.tv_award_nickname);
			viewHolder.award_ptitle = (TextView) arg1
					.findViewById(R.id.tv_award_info);
			viewHolder.card_number = (TextView) arg1
					.findViewById(R.id.tv_award_num);

			arg1.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) arg1.getTag();
		}
		
		Award award=data_award.get(arg0);
		
		viewHolder.award_nickname.setText(award.username);
		viewHolder.card_number.setText(award.card_number);
		viewHolder.award_ptitle.setText(award.ptitle);

		return arg1;
	}

	/**
	 * @类说明 奖品类
	 * @author 赵洋洋
	 * 
	 */
	private static class ViewHolder {

		private TextView award_nickname;
		private TextView award_ptitle;
		private TextView card_number;
	}

}
