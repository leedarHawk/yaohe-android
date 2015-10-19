package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 优惠券详情内容
 * @ClassName DetailsCouponInfo
 */
public class DetailsCouponInfo implements Serializable {

	private static final long serialVersionUID = -6858983193364498918L;

	public Coupon data;

	public static class Coupon implements Serializable {

		private static final long serialVersionUID = 4831413041843988297L;
		public String id;
		public String member_id;
		public String type;
		public String title;
		public String addtime;
		public String content;
		public String img1;
		public String img2;
		public String img3;
		public String img4;
		public String img5;
		public String img6;
		public String valid_start;
		public String valid_end;
		public String num;
		public String use;
		public String draw_num;
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;
		public String give; // 0未领取 1领取
		
		public String zan_num;
		public String comment_num;
		public String collection_num;
	}

}
