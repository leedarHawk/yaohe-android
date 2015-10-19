package com.collcloud.yaohe.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.view.MyGridView;

/**
 * 【附近】-商圈详细评论适配器
 * 
 * @ClassName FuJinPinLunAdapter
 * @Description 评论列表内容
 * @author CollCloud_小米
 */
public class FuJinPinLunAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	public FuJinPinLunAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		PinLunHolder holder = null;
		if (convertView == null) {
			holder = new PinLunHolder();
			convertView = mLayoutInflater.inflate(
					R.layout.item_fujin_pinglun_content, null);

			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (PinLunHolder) convertView.getTag();
		}

		return convertView;
	}

	private void resetLayout(PinLunHolder holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.rl_item_fujin_shop_pinglun_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.mTvDelete = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinglun_delete);
		holder.mTvHuifu = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinglun_report);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia_content);
		holder.mTvTime = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia_time);
		holder.mTvPerson1 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinglun_person_1);
		holder.mTvPerson3 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinglun_person_3);
		holder.mLlPerson3 = (LinearLayout) view
				.findViewById(R.id.ll_item_fujin_pinglun_person_3_huifu);
		holder.mLlImgs = (LinearLayout) view
				.findViewById(R.id.ll_activity_fujin_pinglun_img);
		holder.mGvShowImgs = (MyGridView) view
				.findViewById(R.id.gv_activity_fujin_pinglun_img);

		holder.mTvxing1 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinlun_pingjia1);
		holder.mTvxing2 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinlun_pingjia2);
		holder.mTvxing3 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinlun_pingjia3);
		holder.mTvxing4 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinlun_pingjia4);
		holder.mTvxing5 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pinlun_pingjia5);

	}

	class PinLunHolder {

		/** 删除按钮 */
		TextView mTvDelete;
		/** 回复按钮 */
		TextView mTvHuifu;
		/** 评论内容 */
		TextView mTvContent;
		/** 评论时间 */
		TextView mTvTime;
		/** 第一人（自己） */
		TextView mTvPerson1;
		/** 第三方回复人 */
		TextView mTvPerson3;
		/** 第三方回复组件 */
		LinearLayout mLlPerson3;
		/** 评论图片 */
		LinearLayout mLlImgs;
		/** 评论图片显示 */
		MyGridView mGvShowImgs;

		/** 评价星星1 */
		TextView mTvxing1;
		/** 评价星星2 */
		TextView mTvxing2;
		/** 评价星星3 */
		TextView mTvxing3;
		/** 评价星星4 */
		TextView mTvxing4;
		/** 评价星星5 */
		TextView mTvxing5;

	}
}
