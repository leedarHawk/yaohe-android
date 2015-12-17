package com.collcloud.yaohe.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.HomeFollowShopInfo.FollowShop;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * 首页关注标签下的内容适配器
 * 
 * @ClassName HomeGuanZhuAdapter
 * @Description 首页关注商家-内容适配器
 * @author CollCloud_小米
 */
public class HomeGuanZhuAdapter extends BaseAdapter {

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
	
	private String tag = HomeGuanZhuAdapter.class.getSimpleName();
	/**
	 * 吆喝关注列表内容
	 */
	private List<FollowShop> mFollowShops = new ArrayList<FollowShop>();

	public HomeGuanZhuAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public HomeGuanZhuAdapter(Context context, List<FollowShop> data) {
		this.mContext = context;
		this.mFollowShops = data;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public void refresh(List<FollowShop> data) {
		this.mFollowShops = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mFollowShops == null) {
			return 0;
		}
		return mFollowShops.size();
		// return mFollowShops == null ? 0 : mFollowShops.size();
	}

	@Override
	public Object getItem(int position) {
		return mFollowShops.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final FollowContent holder;
		FollowShop followShop = null;
		if (mFollowShops != null && mFollowShops.size() > 0) {
			followShop = mFollowShops.get(position);
		}
		if (convertView == null) {
			holder = new FollowContent();
			convertView = mLayoutInflater.inflate(
					R.layout.item_home_guanzhu_content, null);

			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (FollowContent) convertView.getTag();
		}

		// 设定关注吆喝信息
		setHomeFollowInfo(holder, followShop);

		final String callID = followShop.id;
		final String type = followShop.type;
		final String shopID = followShop.shop_id;
		final String memberID = followShop.member_id;
		final String service_id = followShop.service_id;
		// *********** 点击List列表的关注按钮 ***********
		holder.mTvGuanzhu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnGuanZhuClickListener != null) {
					mOnGuanZhuClickListener.onGuanZhuClick(holder.mTvGuanzhu,
							shopID);
				}
			}
		});
		// 点击吆喝商家信息
		holder.mRlBusinessContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnBusinessShopListener != null) {
					mOnBusinessShopListener.onBusinessShopClick(position,
							shopID, memberID);
				}
			}
		});
		// 点击List列表项的文字说明或者图片
		holder.mRlContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onCallItemClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		holder.mTvContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onCallItemClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		holder.ll_item_hoe_image_content_yaohe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onCallItemClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		// 收藏
		holder.mTvShoucang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnShouCangClickListener != null) {
					mOnShouCangClickListener.onShouCangButtonClick(position,
							holder.mTvShoucangImg, holder.mTvShoucang, callID);
					CCLog.e("关注商家item点击的位置", "position = " + position);
					CCLog.e("关注商家：", "吆喝id：" + callID + "service_id："
							+ service_id + " member_id: " + memberID);
				}

			}
		});
		holder.mTvShoucangImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnShouCangClickListener != null) {
					mOnShouCangClickListener.onShouCangButtonClick(position,
							holder.mTvShoucangImg, holder.mTvShoucang, callID);
					CCLog.e("关注商家：", "吆喝id：" + callID + "service_id："
							+ service_id + " member_id: " + memberID);
				}
			}
		});

		// 点赞
		holder.mTvZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnZanClickListener != null) {
					mOnZanClickListener.onZanButtonClick(position,
							holder.mTvZanImg, holder.mTvZan, callID);
				}

			}
		});
		holder.mTvZanImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnZanClickListener != null) {
					mOnZanClickListener.onZanButtonClick(position,
							holder.mTvZanImg, holder.mTvZan, callID);
				}

			}
		});
		// 评论
		holder.mTvPinlun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnPinlunClickListener != null) {
					mOnPinlunClickListener.onPinLunButtonClick(position,
							holder.mTvPinlunImg, holder.mTvPinlun, callID,type);
				}

			}
		});
		holder.mTvPinlunImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnPinlunClickListener != null) {
					mOnPinlunClickListener.onPinLunButtonClick(position,
							holder.mTvPinlunImg, holder.mTvPinlun, callID,type);
				}

			}
		});
		return convertView;
	}

	/**
	 * 设定关注吆喝信息
	 * 
	 * @Title setHomeFollowInfo
	 */
	private void setHomeFollowInfo(FollowContent holder, FollowShop followShop) {
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		holder.mTvZanImg.setText(GlobalConstant.INVALID_VALUE);
		holder.mTvShoucangImg.setText(GlobalConstant.INVALID_VALUE);
		holder.mTvPinlunImg.setText(GlobalConstant.INVALID_VALUE);

		// 关注
		holder.mTvGuanzhu.setText(GlobalConstant.VALID_VALUE);

		if (followShop != null && followShop.shop_name != null) {
			holder.mTvName.setText(followShop.shop_name);
		}
		if (followShop != null && followShop.shop_fans_num != null) {
			holder.mTvFans.setText(followShop.shop_fans_num + "粉丝");
		}
		if (followShop != null && followShop.zan_num != null) {
			holder.mTvZan.setText(followShop.zan_num);
		}
		if (followShop != null && followShop.comment_num != null) {
			holder.mTvPinlun.setText(followShop.comment_num);
		}
		if (followShop != null && followShop.collection_num != null) {
			holder.mTvShoucang.setText(followShop.collection_num);
		}
		if (followShop != null && followShop.s_content != null) {
			holder.mTvContent.setText(followShop.s_content);
		}
		
		

		if (followShop != null && !Utils.isStringEmpty(followShop.s_img)) {
			//mImageLoader.get(followShop.img1, listener);
			holder.fv_item_home_guanzhu_shop_image.setVisibility(View.VISIBLE);
			mImageLoader.get(followShop.s_img, listener,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		} else {
			holder.mIvThum.setImageResource(R.drawable.icon_yaohe_loading_default);
			holder.fv_item_home_guanzhu_shop_image.setVisibility(View.GONE);
		}
		
		ImageListener listener2 = ImageLoader.getImageListener(holder.iv_item_hme_shop_image_yaohe,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		if (followShop != null && !Utils.isStringEmpty(followShop.img)) {
			CCLog.d(tag, "yaohe img url:"+followShop.img);
			holder.fv_item_home_shop_image_yaohe.setVisibility(View.VISIBLE);
			mImageLoader.get(followShop.img, listener2,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		} else {
			CCLog.d(tag, "yaohe img url:"+followShop.img);
			holder.iv_item_hme_shop_image_yaohe.setImageResource(R.drawable.icon_yaohe_loading_default);
			holder.fv_item_home_shop_image_yaohe.setVisibility(View.GONE);
		}
		
		
//		if("0".equals(followShop.c_id) || Utils.isStringEmpty(followShop.c_id)) {
//			holder.mRlContent.setVisibility(View.GONE);
//		} else {
//			holder.mRlContent.setVisibility(View.VISIBLE);
//		}
		
		//吆喝本身内容
		holder.tv_item_home_content_yaohe.setText(followShop.content); 
		//LEE
		if("1".equals(followShop.is_yinyong)) {
			holder.mTvContent.setText(followShop.s_content);
		} else {
			holder.mTvContent.setText(followShop.title);
		}
		
		//不是吆喝
//		if(followShop != null &&  !TYPE_DEFAULT.equals(followShop.type)) {
//			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.GONE);
//			holder.mRlContent.setVisibility(View.VISIBLE);
//			holder.mRlContent.setBackgroundResource(R.color.transparent);
//		} else {//是吆喝
//			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
//			if("0".equals(followShop.c_id) || Utils.isStringEmpty(followShop.c_id)) {//纯吆喝
//				holder.mRlContent.setVisibility(View.GONE);
//			} else {//有引用
//				holder.mRlContent.setVisibility(View.VISIBLE);
//				holder.mRlContent.setBackgroundResource(R.color.diliver_in_gray);
//			}
//		}
//		
		
		//不纯吆喝 引用了某一个服务 则显示 两行
		if(followShop != null && "1".equals(followShop.is_yinyong)) {
			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
			holder.mRlContent.setVisibility(View.VISIBLE);
			holder.mRlContent.setBackgroundResource(R.color.diliver_in_gray);
		} else if(followShop != null && !"1".equals(followShop.is_yinyong)) {//不是引用 就显示一样
			if(!TYPE_DEFAULT.equals(followShop.type)) {//不是纯吆喝 也不是 发吆喝引用了服务 而是纯服务
				holder.ll_item_hoe_image_content_yaohe.setVisibility(View.GONE);
				holder.mRlContent.setVisibility(View.VISIBLE);
				holder.mRlContent.setBackgroundResource(R.color.transparent);
			} else {//纯吆喝
				holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
				holder.mRlContent.setVisibility(View.GONE);
			}
			
		}
		
		
		if (followShop != null && followShop.addtime != null) {
			try {
				holder.mTvDate.setVisibility(View.VISIBLE);
				// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
				if (followShop.addtime.length() == 10) {
					followShop.addtime = followShop.addtime + "000";
				}
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
				Date date = new Date(Long.valueOf(followShop.addtime));
				holder.mTvDate.setText(df.format(date));
			} catch(Exception e) {
				e.printStackTrace();
				holder.mTvDate.setVisibility(View.GONE);
			}
			
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}
		if (followShop != null && followShop.type != null) {
			String type = followShop.type;
			CCLog.e("type:", " " + type);
			if (type.equals(TYPE_YOUHUI)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals(TYPE_CARD)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals(TYPE_XINPIN)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals(TYPE_HUODONG)) {
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else {
				holder.mIvTag.setVisibility(View.GONE);
			}
		}
		if (followShop != null && followShop.shop_star != null) {
			String shopStar = followShop.shop_star;
			if (shopStar.equals("2")) {
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("3")) {
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("4")) {
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("5")) {
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_on);
			} else {
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);

			}
		}
	}

	private void resetLayout(FollowContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.rl_item_home_guanzhu_content_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_name);
		holder.mTvFans = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_fans);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_shop_content);
		holder.mRlContent = (RelativeLayout) view
				.findViewById(R.id.ll_item_home_guanzhu_image_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.rl_item_home_guanzhu_shop);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_shop_guanzhu_time);
		holder.mTvZan = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_zan);
		holder.mTvZanImg = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_zan_img);
		holder.mTvPinlun = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pinlun);
		holder.mTvPinlunImg = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pinlun_img);
		holder.mTvShoucang = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_shoucang);
		holder.mTvShoucangImg = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_shoucang_img);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_home_guanzhu_shop_image);
		holder.mIvTag = (ImageView) view
				.findViewById(R.id.iv_item_home_guanzhu_shop_image_tags);
		holder.mIvxing1 = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pingjia1);
		holder.mIvxing2 = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pingjia2);
		holder.mIvxing3 = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pingjia3);
		holder.mIvxing4 = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pingjia4);
		holder.mIvxing5 = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_pingjia5);
		holder.mTvGuanzhu = (TextView) view
				.findViewById(R.id.tv_item_home_guanzhu_guanzhu);
		
		//吆喝图片 以及内容 LEE
		holder.iv_item_hme_shop_image_yaohe = (ImageView)view.findViewById(R.id.iv_item_hme_shop_image_yaohe);
		holder.tv_item_home_content_yaohe = (TextView)view.findViewById(R.id.tv_item_home_content_yaohe);
		holder.ll_item_hoe_image_content_yaohe = view.findViewById(R.id.ll_item_hoe_image_content_yaohe);
		
		holder.fv_item_home_guanzhu_shop_image = (FrameLayout)view.findViewById(R.id.fv_item_home_guanzhu_shop_image);
		holder.fv_item_home_shop_image_yaohe = (FrameLayout)view.findViewById(R.id.fv_item_home_shop_image_yaohe);

	}

	class FollowContent {

		/** 店铺名称 */
		TextView mTvName;
		/** 店铺简介 */
		TextView mTvContent;
		RelativeLayout mRlContent;
		RelativeLayout mRlBusinessContent;
		/** 粉丝数 */
		TextView mTvFans;
		/** 日期时间 */
		TextView mTvDate;
		/** 点赞数 */
		TextView mTvZan;
		/** 点赞 小图标 */
		TextView mTvZanImg;
		/** 评论数 */
		TextView mTvPinlun;
		/** 评论 的小图标 */
		TextView mTvPinlunImg;
		/** 收藏数 */
		TextView mTvShoucang;
		/** 收藏 小图标 */
		TextView mTvShoucangImg;
		/** 列表简介图片 */
		ImageView mIvThum;
		/** 消息活动类型 */
		ImageView mIvTag;
		/** 关注按钮 */
		TextView mTvGuanzhu;
		/** 评价星星1 */
		TextView mIvxing1;
		/** 评价星星2 */
		TextView mIvxing2;
		/** 评价星星3 */
		TextView mIvxing3;
		/** 评价星星4 */
		TextView mIvxing4;
		/** 评价星星5 */
		TextView mIvxing5;
		
		//吆喝本身图片
		ImageView iv_item_hme_shop_image_yaohe;
		//吆喝本身内容
		TextView tv_item_home_content_yaohe;
		
		View ll_item_hoe_image_content_yaohe;
		
		// 引用
		FrameLayout fv_item_home_guanzhu_shop_image;
		//吆喝本身 frameLayout
		FrameLayout fv_item_home_shop_image_yaohe;
	}

	public interface OnGuanZhuListener {
		void onCallItemClick(int position, String callId, String service_id,
				String shopId, String memberID, String type);

		void onBusinessShopClick(int position, String shopId, String memberID);

		// 关注按钮点击
		void onGuanZhuClick(TextView tvGuanzhu, String callID);

		// 点赞按钮点击
		void onZanButtonClick(int position, TextView tvZanImg, TextView tvZan,
				String callID);

		// 收藏按钮点击
		void onShouCangButtonClick(final int position,
				final TextView tvShouCangImg, final TextView tvShouCang,
				final String callID);

		// 评论按钮点击
		void onPinLunButtonClick(int position, TextView tvPinLunImg,
				TextView tvPinLun, String callID,String type);
	}

	private OnGuanZhuListener mOnBusinessShopListener = null;

	public void setOnBusinessShopListener(OnGuanZhuListener listener) {
		this.mOnBusinessShopListener = listener;
	}

	private OnGuanZhuListener mOnItemClickListener = null;

	public void setOnHomeItemClickListener(OnGuanZhuListener listener) {
		this.mOnItemClickListener = listener;
	}

	private OnGuanZhuListener mOnGuanZhuClickListener = null;

	public void setOnGuanZhuClickListener(OnGuanZhuListener listener) {
		this.mOnGuanZhuClickListener = listener;
	}

	private OnGuanZhuListener mOnZanClickListener = null;

	public void setOnZanClickListenerClickListener(OnGuanZhuListener listener) {
		this.mOnZanClickListener = listener;
	}

	private OnGuanZhuListener mOnPinlunClickListener = null;

	public void setOnPinLunClickListenerClickListener(OnGuanZhuListener listener) {
		this.mOnPinlunClickListener = listener;
	}

	private OnGuanZhuListener mOnShouCangClickListener = null;

	public void setOnShouCangClickListenerClickListener(
			OnGuanZhuListener listener) {
		this.mOnShouCangClickListener = listener;
	}
}
