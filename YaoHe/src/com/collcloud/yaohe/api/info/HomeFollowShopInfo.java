package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 获取会员关注的吆喝列表
 * 
 * @ClassName HomeFollowShopInfo
 */
public class HomeFollowShopInfo implements Serializable {

	private static final long serialVersionUID = 6123127326657209799L;
	public List<FollowShop> data;

	public static class FollowShop implements Serializable {
		private static final long serialVersionUID = 5754206117289903901L;
		public String id; // 吆喝ID
		public String member_id;// 会员ID
		public String service_id;// 服务ID
		
		public String title; // 
		public String type;// 引用内容 
		public String content; // 内容
		public String img1;// 图片地址1
		
		public String fans_num;
		public String collection_num;// 收藏数量
		public String zan_num;// 点赞数量
		public String comment_num;// 评论数量
		
		public String addtime;// 时间
		
		public String shop_id;// 评论数量
		public String shop_star;// 评价星级
		public String shop_name;// 店铺名称
		public String shop_fans_num;// 粉丝数
	}
}
