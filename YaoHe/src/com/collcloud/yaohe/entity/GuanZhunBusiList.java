package com.collcloud.yaohe.entity;

import java.io.Serializable;
import java.util.List;

public class GuanZhunBusiList implements Serializable {
	private static final long serialVersionUID = 1802192161514940844L;
	/**
	 * @类说明：关注商家列表
	 * @author 赵洋洋
	 */
	public List<GuanZhunBusi> GuanZhunBusis;

	public static class GuanZhunBusi implements Serializable {

		private static final long serialVersionUID = 2193102600317351784L;
		// 店铺ID
		public String id;
		// 会员ID
		public String member_id;
		// 用户手机号
		public String username;
		// 店铺名称
		public String title;
		// 头像地址
		public String face;
		// 一级分类id
		public String one_id;
		// 二级分类
		public String industry_class_id;
		// 商家类型
		public String class_title;

	}
}
