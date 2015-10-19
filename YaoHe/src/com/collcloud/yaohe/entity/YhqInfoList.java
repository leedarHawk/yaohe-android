package com.collcloud.yaohe.entity;

import java.io.Serializable;

public class YhqInfoList implements Serializable {

	/**
	 * @类说明：优惠券信息类
	 * @author 赵洋洋
	 */
	private static final long serialVersionUID = 1L;
	/** 优惠券ID */
	public String id;
	/** 优惠信息 */
	public String title;
	/** 会员ID */
	public String member_id;
	/** 优惠券内容 */
	public String content;
	/** 优惠券图片 */
	public String img1;
	/** 优惠券类型 */
	public String type;
	/** 获取发该优惠券的会员ID */
	public String member_coupon_id;
	/** 使用次数 */
	public String use_number;
	/** 优惠券开始时间 */
	public String valid_start;
	/** 优惠券结束时间 */
	public String valid_end;
	/** 优惠券店铺ID */
	public String shop_id;
	/** 优惠券店铺名 */
	public String shop_name;

}
