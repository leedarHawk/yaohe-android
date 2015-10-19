package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 会员卡详情
 * @ClassName DetailsCardInfo
 */
public class DetailsCardInfo implements Serializable {

	private static final long serialVersionUID = -7746858765254264288L;
	public Card data;

	public static class Card implements Serializable {

		private static final long serialVersionUID = 7574165301558631001L;
		public String id;
		public String member_id;
		public String title;
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
		public String draw_num;
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;
		public String give;

		public String zan_num;
		public String comment_num;
		public String collection_num;
	}

}
