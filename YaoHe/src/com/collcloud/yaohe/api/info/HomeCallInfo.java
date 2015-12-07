package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 获取首页吆喝列表
 * 
 * @ClassName HomeCallInfo
 */
public class HomeCallInfo implements Serializable {

	private static final long serialVersionUID = -3887642287274813188L;
	public List<CallInfo> data;

	public static class CallInfo implements Serializable {
		private static final long serialVersionUID = -2903651451889821658L;

		public String id; // 吆喝ID
		public String city_id; // 城市ＩＤ
		public String type;// 引用内容 1券 2卡 3新品 4活动 0无
		public String member_id;// 会员ID
		public String content; // 内容
		public String img;// 图片地址1
		public String title; // 标题
		public String collection_num;// 收藏数量
		public String zan_num;// 点赞数量
		public String comment_num;// 评论数量
		
		public String shop_id;// 评论数量
		public String shop_star;// 评价星级
		public String shop_name;// 店铺名称
		// ***********
		public String service_id;// 服务ＩＤ
		public String addtime;// 时间
		
		public String guanzhu ;
		public String shop_fans_num ;//粉丝
		//引用图片
		public String s_img;
		//引用内容
		public String s_content;
		// 为0的时候 标志 没有引用服务 ，而是纯吆喝
		public String c_id;
		
		public String is_yinyong;
	}
}
