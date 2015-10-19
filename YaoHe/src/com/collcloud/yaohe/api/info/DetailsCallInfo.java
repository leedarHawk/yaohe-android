package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 吆喝详情
 * 
 * @ClassName DetailsCallInfo
 */
public class DetailsCallInfo implements Serializable {

	private static final long serialVersionUID = -2350612906252782880L;
	public CallInfo data;

	public static class CallInfo implements Serializable {

		private static final long serialVersionUID = -1154719917953945104L;
		public String id;
		public String member_id;
		public String service_id;
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
		
		public String c_id;//引用内容ID
		public String s_img;
		public String s_title;
		public String shop_service_id; //点赞等或评论调用这个ID

		public String collection_num;// 收藏数量
		public String zan_num;// 点赞数量
		public String comment_num;// 评论数量
		
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;
		
	}

}
