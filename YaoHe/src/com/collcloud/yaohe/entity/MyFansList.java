package com.collcloud.yaohe.entity;

import java.io.Serializable;

public class MyFansList implements Serializable {

	/**
	 * @类说明：收藏信息类
	 * @author 赵洋洋
	 */
	private static final long serialVersionUID = 1L;

	/** 店铺ID */
	public String id;
	public String member_id;
	/** 图片地址1 */
	public String face;
	/** 1券 2卡 3新品 4活动 0无 */
	public String nickname;
	/** 用户名 */
	public String login_user;
	

}
