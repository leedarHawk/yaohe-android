package com.collcloud.yaohe.constants;

/**
 * 
 * @author LEE
 * 通用常量类
 *
 */
public class CommonConstant {
	
	//关注状态 相关
	public static final int RESULTCODE_GUANZHU=100;
	/**
	 * 关注 状态广播action
	 */
	public static final String STATUS_BROADCAST_ACTION="statusAction";
	/**
	 * 登出广播action
	 */
	public static final String BUSINESS_LOGINOUT_BROADCAST_ACTION="business_loginoutAction";
	/**
	 * 登出广播action
	 */
	public static final String PERSON_LOGINOUT_BROADCAST_ACTION="person_loginoutAction";
	/**
	 * 改变关注状态
	 */
	public static final int doWhat_change_followStatus=1;
	/**
	 * 改变评论个数
	 */
	public static final int doWhat_change_comment_count=2;
	/**
	 * 改变赞个数
	 */
	public static final int doWhat_change_zan_count=3;
	/**
	 * 改变收藏个数
	 */
	public static final int doWhat_change_shoucang_count=4;
	/**
	 * 改变商铺星星个数
	 */
	public static final int doWhat_change_shop_start_count=5;

}
