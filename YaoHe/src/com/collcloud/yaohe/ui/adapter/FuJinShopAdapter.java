package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.collcloud.yaohe.api.info.NearByListInfo.NearBy;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.StringUtils;
import com.collcloud.yaohe.ui.utils.Utils;
import com.meg7.widget.CustomShapeImageView;

/**
 * 附近商圈-内容适配器
 * 
 * @ClassName FuJinShopAdapter
 * @author CollCloud_小米
 */
public class FuJinShopAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	/** 首页 活动消息类型， 0 ： 优惠券 */
	public static final String TYPE_YOUHUI = "0";
	/** 首页 活动消息类型， 1 ： 会员卡 */
	public static final String TYPE_CARD = "1";
	/** 首页 活动消息类型， 2 ： 活动 */
	public static final String TYPE_HUODONG = "2";
	/** 首页 活动消息类型， 3 ： 新品 */
	public static final String TYPE_XINPIN = "3";
	/** 首页 活动消息类型， 4 ： 吆喝啦 */
	public static final String TYPE_DEFAULT = "4";

	private static ImageLoader mImageLoader;
	private List<NearBy> mNearList = new ArrayList<NearBy>();

	public FuJinShopAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public FuJinShopAdapter(Context context, List<NearBy> nearList) {
		this.mContext = context;
		this.mNearList = nearList;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	@Override
	public int getCount() {
		return mNearList == null ? 0 : mNearList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public interface OnNearItemClickListener {
		void onNearItemClick(int position, String shop_id, String member_id);
	}

	private OnNearItemClickListener mOnItemClickListener = null;

	public void setOnNearItemClickListener(OnNearItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	public void refresh(List<NearBy> nearList) {
		this.mNearList = nearList;
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FuJinShopContent holder = null;
		NearBy nearByInfo = null;
		if (mNearList != null && mNearList.size() > 0) {
			nearByInfo = mNearList.get(position);
		}
		if (convertView == null) {
			holder = new FuJinShopContent();
			convertView = mLayoutInflater.inflate(
					R.layout.item_fujin_shop_content, null);

			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (FuJinShopContent) convertView.getTag();
		}

		// 设定附近商家信息
		setNearCallInfo(holder, nearByInfo);
		if (nearByInfo != null) {
			final String shopId = nearByInfo.id;
			final String memberId = nearByInfo.member_id;
			// 点击List列表项的文字说明或者图片
			holder.mRlShopContent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onNearItemClick(position, shopId,
								memberId);
					}
				}
			});
			holder.mIvShop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onNearItemClick(position, shopId,
								memberId);
					}
				}
			});

		}
		return convertView;
	}

	/**
	 * 设定首页吆喝信息
	 * 
	 * @Title setHomeCallInfo
	 */
	private void setNearCallInfo(FuJinShopContent holder, final NearBy nearInfo) {
		ImageListener listener = ImageLoader.getImageListener(holder.mIvShop,
				R.drawable.icon_yaohe_default_logo,
				R.drawable.icon_yaohe_default_logo);

		if (nearInfo != null && nearInfo.fans_num != null) {
			holder.mTvFans.setText(nearInfo.fans_num + "粉丝");
		}
		if (nearInfo != null && nearInfo.full_name != null) {
			holder.mTvName.setText(nearInfo.full_name);
		}
		if (nearInfo != null && nearInfo.area_name != null) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(nearInfo.area_name + "  ");
			holder.mTvLocation.setText(stringBuffer.toString());
		}
		if (nearInfo != null && nearInfo.range != null) {
			holder.mTvJuli.setText(StringUtils.friendly_Distance(Long
					.valueOf(nearInfo.range)));
		}

		if (nearInfo != null && !Utils.isStringEmpty(nearInfo.face)) { 
			//CCLog.d("fujin", "nearInfo.face url :"+nearInfo.face);
			mImageLoader.get(nearInfo.face, listener,mContext.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_width),mContext.getResources().getDimensionPixelSize(R.dimen.photo_max_middle_height));
		} else {
			holder.mIvShop.setImageResource(R.drawable.icon_yaohe_default_logo);
		}
		if (nearInfo != null && nearInfo.type != null) {
			String type = nearInfo.type;
			if (type.equals(TYPE_YOUHUI)) {
				holder.mTvTagQuan.setVisibility(View.VISIBLE);
				holder.mTvTagQuan
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals(TYPE_CARD)) {
				holder.mTvTagCard.setVisibility(View.VISIBLE);
				holder.mTvTagCard
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals(TYPE_XINPIN)) {
				holder.mTvTagXin.setVisibility(View.VISIBLE);
				holder.mTvTagXin
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals(TYPE_HUODONG)) {
				holder.mTvTagCuxiao.setVisibility(View.VISIBLE);
				holder.mTvTagCuxiao
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else {
				holder.mTvTagQuan.setVisibility(View.GONE);
				holder.mTvTagXin.setVisibility(View.GONE);
				holder.mTvTagCard.setVisibility(View.GONE);
				holder.mTvTagCuxiao.setVisibility(View.GONE);
			}
		}
		if (nearInfo != null && nearInfo.star != null) {
			String shopStar = nearInfo.star;
			if (shopStar.equals("2")) {
				holder.mTvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("3")) {
				holder.mTvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("4")) {
				holder.mTvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("5")) {
				holder.mTvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_on);
			} else {
				holder.mTvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mTvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mTvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);

			}
		}
	}

	private void resetLayout(FuJinShopContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.rl_item_fujin_business_shop_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_name);
		holder.mTvFans = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_fans);
		holder.mTvLocation = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_location_);
		holder.mTvJuli = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_location_juli);
		holder.mIvShop = (CustomShapeImageView) view
				.findViewById(R.id.iv_item_fujin_shop_image);

		holder.mTvTagQuan = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_type_quan);
		holder.mTvTagCard = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_type_ka);
		holder.mTvTagXin = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_type_xinpin);
		holder.mTvTagCuxiao = (TextView) view
				.findViewById(R.id.tv_item_fujin_shop_type_cu);

		holder.mTvxing1 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia1);
		holder.mTvxing2 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia2);
		holder.mTvxing3 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia3);
		holder.mTvxing4 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia4);
		holder.mTvxing5 = (TextView) view
				.findViewById(R.id.tv_item_fujin_pingjia5);
		holder.mRlShopContent = (RelativeLayout) view
				.findViewById(R.id.rl_fujin_item_text_info);

	}

	class FuJinShopContent {

		/** 商铺名称 */
		TextView mTvName;
		/** 粉丝数 */
		TextView mTvFans;
		/** 商铺地理位置 */
		TextView mTvLocation;
		/** 商铺附近距离 */
		TextView mTvJuli;
		/** 评价星数 */
		TextView mTvPinlunTotal;
		/** 列表简介图片 */
		CustomShapeImageView mIvShop;

		/** 商铺活动类型 */
		ImageView mIvTag;
		/** 商铺活动类型 - 优惠券 */
		TextView mTvTagQuan;
		/** 商铺活动类型 - 会员卡 */
		TextView mTvTagCard;
		/** 商铺活动类型 - 新品发布 */
		TextView mTvTagXin;
		/** 商铺活动类型 - 促销 */
		TextView mTvTagCuxiao;

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
		RelativeLayout mRlShopContent;

	}
}
