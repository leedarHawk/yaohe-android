package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.HYCardList;
import com.collcloud.yaohe.ui.utils.Utils;

public class PersonHYKJAdapter extends BaseAdapter {

	Context context;
	List<HYCardList> mDatas;
	LayoutInflater layoutInflater;

	// 构造传参
	public PersonHYKJAdapter(Context context, List<HYCardList> data) {

		this.context = context;
		this.mDatas = data;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {

		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void refresh(List<HYCardList> data) {
		this.mDatas = data;
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// 获取布局
		View view = convertView;
		PersonCard holder = null;
		HYCardList cardInfo = mDatas.get(position);
		if (view == null) {
			holder = new PersonCard();
			view = layoutInflater.inflate(R.layout.item_person_hyk_content,
					null);
			RelativeLayout rlLayout = (RelativeLayout) view
					.findViewById(R.id.rl_person_huiyanka_root);
			SupportDisplay.resetAllChildViewParam(rlLayout);
			holder.mTvCardTitle = (TextView) view
					.findViewById(R.id.tv_item_hyk_shop_name);
			holder.mTvCardSale = (TextView) view
					.findViewById(R.id.tv_item_hyk_shop_zk);

			view.setTag(holder);
		} else {
			holder = (PersonCard) view.getTag();
		}

		if (!Utils.isStringEmpty(cardInfo.title)) {
			holder.mTvCardTitle.setText(cardInfo.title);
		}
		if (!Utils.isStringEmpty(cardInfo.discount)) {
			holder.mTvCardSale.setText(cardInfo.discount);
		}
		return view;
	}

	class PersonCard {

		private TextView mTvCardTitle;
		private TextView mTvCardSale;
	}
}
