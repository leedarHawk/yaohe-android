package com.collcloud.yaohe.ui.model;

import java.io.Serializable;

/**
 * 主页面列表内容的对象模型
 * 
 * @ClassName HomeItemInfo
 * @author CollCloud_小米
 */
public class HomeItemInfo implements Serializable {

	private static final long serialVersionUID = 8903778245034228890L;

	/**
	 * 首页商品/店铺名称
	 */
	private String mStrName = null;
	/**
	 * 店铺简介
	 */
	private String mStrContent = null;
	/**
	 * 粉丝数
	 */
	private String mStrFans = null;
	/**
	 * 点赞数
	 */
	private String mStrZan = null;
	/**
	 * 评论数
	 */
	private String mStrPingLun = null;
	/**
	 * 收藏数
	 */
	private String mStrShouCang = null;
	/**
	 * 列表简介图片路径
	 */
	private String mStrShopImgPath = null;
	/**
	 * 消息活动类型（优惠券、会员卡、新品、活动。。）
	 */
	private String mStrShopTag = null;
	/**
	 * 评价几颗星
	 */
	private String mStrPingJiaXing = null;
	/**
	 * 是否点击关注按钮
	 */
	private boolean isGuanzhu = false;
	/**
	 * 关注按钮状态
	 */
	private String mStrGuanZhuStatus = null;

	public String getmStrGuanZhuStatus() {
		return mStrGuanZhuStatus;
	}

	public void setmStrGuanZhuStatus(String mStrGuanZhuStatus) {
		this.mStrGuanZhuStatus = mStrGuanZhuStatus;
	}

	public String getmStrName() {
		return mStrName;
	}

	public void setmStrName(String mStrName) {
		this.mStrName = mStrName;
	}

	public String getmStrContent() {
		return mStrContent;
	}

	public void setmStrContent(String mStrContent) {
		this.mStrContent = mStrContent;
	}

	public String getmStrFans() {
		return mStrFans;
	}

	public void setmStrFans(String mStrFans) {
		this.mStrFans = mStrFans;
	}

	public String getmStrZan() {
		return mStrZan;
	}

	public void setmStrZan(String mStrZan) {
		this.mStrZan = mStrZan;
	}

	public String getmStrPingLun() {
		return mStrPingLun;
	}

	public void setmStrPingLun(String mStrPingLun) {
		this.mStrPingLun = mStrPingLun;
	}

	public String getmStrShouCang() {
		return mStrShouCang;
	}

	public void setmStrShouCang(String mStrShouCang) {
		this.mStrShouCang = mStrShouCang;
	}

	public String getmStrShopImgPath() {
		return mStrShopImgPath;
	}

	public void setmStrShopImgPath(String mStrShopImgPath) {
		this.mStrShopImgPath = mStrShopImgPath;
	}

	public String getmStrShopTag() {
		return mStrShopTag;
	}

	public void setmStrShopTag(String mStrShopTag) {
		this.mStrShopTag = mStrShopTag;
	}

	public String getmStrPingJiaXing() {
		return mStrPingJiaXing;
	}

	public void setmStrPingJiaXing(String mStrPingJiaXing) {
		this.mStrPingJiaXing = mStrPingJiaXing;
	}

	public boolean isGuanzhu() {
		return isGuanzhu;
	}

	public void setGuanzhu(boolean isGuanzhu) {
		this.isGuanzhu = isGuanzhu;
	}
}
