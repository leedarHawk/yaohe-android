package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 商家基本信息 - getShopInfo
 * 
 * @ClassName DetailsBusinessShopInfo
 * @Description
 * @author CollCloud_小米
 */
public class DetailsBusinessShopInfo implements Serializable {
	private static final long serialVersionUID = -3411250792461456509L;
	public BusinessShopInfo row;
	public List<BusinessServiceInfo> service;
	public List<BusinessCallInfo> call;

	public static class BusinessShopInfo implements Serializable {
		private static final long serialVersionUID = 3512448776421096331L;
		public String id;// 店铺ID
		public String full_name; // 商店全称
		public String fans_num;// 粉丝数量
		public String star;// 星数
		public String content;// 店铺简介
		public String type;
		public String address;// 详细地址
		public String img1;
		public String subscribe_tel;// 联系电话
		public String business_time;// 营业时间
		public String follow;// 是否关注 0未关注 1已关注

	}

	public static class BusinessServiceInfo implements Serializable {
		private static final long serialVersionUID = -5770704075365777380L;
		public String id;// 店铺ID
		public String member_id; // 会员ID
		public String service_id;
		public String title;// 服务标题
		public String type;// 0优惠券 1会员卡 2活动 3新品
		public String addtime;
		public String city_id;
		public String zan_num;
		public String comment_num;
		public String collection_num;
		public String one_id;
		public String industry_class_id;
		public String province_id;
		public String area_id;
		public String district_id;
		public String img;
		public String content;

	}

	public static class BusinessCallInfo implements Serializable {
		private static final long serialVersionUID = 737838634013424123L;
		public String id;// 店铺ID
		public String member_id; // 会员ID
		public String service_id;
		public String type;// 0优惠券 1会员卡 2活动 3新品
		public String img;// 图片地址1
		public String title;// 服务标题
		public String addtime;// 添加时间
		public String city_id;
		public String zan_num;
		public String comment_num;
		public String collection_num;
		public String one_id;
		public String industry_class_id;
		public String province_id;
		public String area_id;
		public String district_id;
		public String content;
		
		public String counponnum; //优惠券数量
		public String cardnum; //会员卡数量
		public String activitynum;//活动数量
		public String newproductnum;//新品数量

	}

}
