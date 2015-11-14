package com.collcloud.yaohe.url;

public interface ContantsValues {
	public static final String xxx = "testtest.htcheng.com";
	//public String HOST="http://www.17yaohe.com/";
	public String HOST="http://www.17yaohe.com/";
	/** 个人注册接口url */
	String REGISTERURL = "http://www.17yaohe.com/?c=Api&a=register&partent=ht&key=ht";
	String USER_BASE_URL = "http://www.17yaohe.com/?c=Api&a=getUserBase&partent=ht&key=ht";
	String SHOP_BASE_URL = "http://www.17yaohe.com/?c=Api&a=getShopBase&partent=ht&key=ht";

	/** 获取验证码接口url */
	String GETVCODE = "http://www.17yaohe.com/?c=Api&a=register_sms&partent=ht&key=ht";

	/** 用户登录接口url */
	String LOGIN = "http://www.17yaohe.com/?c=Api&a=login&partent=ht&key=ht";

	/** 获取一二级分类接口url */
	String BUSINESSTYPE = "http://www.17yaohe.com/?c=Api&a=getClassifyList&partent=ht&key=ht";

	/** 获取区域所在商圈接口url */
	String SHANGQUAN = "http://www.17yaohe.com/?c=Api&a=getDistrictList&partent=ht&key=ht";

	/** 获取城市的区域列表接口url */
	String CITYAREA = "http://www.17yaohe.com/?c=Api&a=getAreaList&partent=ht&key=ht";

	/** 申请为商家接口url */
	String SHENGQINGSHANGJIA = "http://www.17yaohe.com/?c=Api&a=shopbase&partent=ht&key=ht";

	/** 商家发布吆喝接口url */
	String FAYAOHE = "http://www.17yaohe.com/?c=Api&a=addcall&partent=ht&key=ht";

	/** 商家发布优惠券接口url */
	String FAYHQ = "http://www.17yaohe.com/?c=Api&a=addcoupon&partent=ht&key=ht";

	/** 商家发布会员卡接口url */
	String FAHYK = "http://www.17yaohe.com/?c=Api&a=addcard&partent=ht&key=ht";

	/** 商家发布活动接口url */
	String FAHD = "http://www.17yaohe.com/?c=Api&a=addactivity&partent=ht&key=ht";

	/** 商家发布新品接口url */
	String FAXP = "http://www.17yaohe.com/?c=Api&a=addproduct&partent=ht&key=ht";

	/** 上传头像接口url */
	String FACE = "http://www.17yaohe.com/?c=Api&a=uploadFace&partent=ht&key=ht";

	/** 我的（个人收藏）接口url */
	String SC = "http://www.17yaohe.com/?c=Api&a=myCollection&partent=ht&key=ht";
	/** 删除（个人收藏）接口url */
	String DELETE_SHOUCANG = "http://www.17yaohe.com/?c=Api&a=delMyCollection&partent=ht&key=ht";

	/** 我的（个人优惠）接口url */
	String YH = "http://www.17yaohe.com/?c=Api&a=myDiscount&partent=ht&key=ht";
	/** 我的消息接口url */
	String PERSON_MY_MSG = "http://www.17yaohe.com/?c=Api&a=mySms&partent=ht&key=ht";

	/** 我的（可使用优惠）接口url */
	String KSYYH = "http://www.17yaohe.com/?c=Api&a=plyCoupon&partent=ht&key=ht";
	/** 我的吆喝评论接口url */
	String MY_YAOHE_COMMENT = "http://www.17yaohe.com/?c=Api&a=getMyCallCommentList&partent=ht&key=ht";
	/** 获取我的点赞列表接口url */
	String MY_ZAN_URL = "http://www.17yaohe.com/?c=Api&a=getMyZanList&partent=ht&key=ht";
	/** 获取我的店铺评论url */
	String MY_SHOP_COMMENT = "http://www.17yaohe.com/?c=Api&a=getMyShopCommentList&partent=ht&key=ht";

	/** 我的（不可使用优惠）接口url */
	String BKSYYH = "http://www.17yaohe.com/?c=Api&a=plyNoCoupon&partent=ht&key=ht";

	/** 我的（个人）优惠券详情接口url */
	String YHQDETAIL = "http://www.17yaohe.com/?c=Api&a=plyCouponDetail&partent=ht&key=ht";
//test
	/** 我的（个人）会员卡接口url */
	String MYCARD = "http://www.17yaohe.com/?c=Api&a=getMyMemberCardList&partent=ht&key=ht";

	/** 我的（个人）会员卡详情接口url */
	String MYCARDDETAIL = "http://www.17yaohe.com/?c=Api&a=getMyMemberCardDetail&partent=ht&key=ht";

	/** 我的（个人）关注接口url */
	String MYGZ = "http://www.17yaohe.com/?c=Api&a=myFollowList&partent=ht&key=ht";

	/** 我的（发吆喝）引用服务接口url */
	String YYFW = "http://www.17yaohe.com/?c=Api&a=getMyServiceList&partent=ht&key=ht";

	/** 修改密码接口url */
	String UPPASSWORD = "http://www.17yaohe.com/?c=Api&a=upPass&partent=ht&key=ht";
	String UPPSEX = "http://www.17yaohe.com/?c=Api&a=upSex&partent=ht&key=ht";

	/** 我的吆喝接口url */
	String MYYH = "http://www.17yaohe.com/?c=Api&a=getMyCallList&partent=ht&key=ht";

	/** 我的粉丝接口url */
	String MYFS = "http://www.17yaohe.com/?c=Api&a=getMyFansList&partent=ht&key=ht";

	/** 意見反饋接口url */
	String FEEDBACK = "http://www.17yaohe.com/?c=Api&a=addOpinion&partent=ht&key=ht";

	/** 修改昵称接口url */
	String UPNICKNAME = "http://www.17yaohe.com/?c=Api&a=upNickname&partent=ht&key=ht";

	/** 用户中奖接口url */
	String ZHONGJIANG = "http://www.17yaohe.com/?c=Api&a=getWinnig&partent=ht&key=ht";

	/** 用户中奖纪录接口url */
	String ZHONGJIANGJILU = "http://www.17yaohe.com/?c=Api&a=getWinGoodsList&partent=ht&key=ht";

	/** 更改密码接口url */
	String CHANGPASSWORD = "http://www.17yaohe.com/?c=Api&a=forgetpass&partent=ht&key=ht";

	/** 重置密码接口url */
	String RESETPASSWORD = "http://www.17yaohe.com/?c=Api&a=validatecode&partent=ht&key=ht";

	/** 重置密码时验证码接口url */
	String RESETPASSWORDCODE = "http://www.17yaohe.com/?c=Api&a=forgetpass&partent=ht&key=ht";
	/** 忘记密码 重新设定新密码 */
	String RESET_NEW_PASS = "http://www.17yaohe.com/?c=Api&a=resetpass&partent=ht&key=ht";

	// ******************* 首页接口描述 ****************** //
	/** 获取首页轮换图列表url */
	String HOME_BANNER_ROTATE_URL = "http://www.17yaohe.com/?c=Api&a=getHomeRotateList&partent=ht&key=ht";
	/** 获取首页分类列表url */
	String HOME_TYPE_LIST_URL = "http://www.17yaohe.com/?c=Api&a=getHomeTypeList&partent=ht&key=ht";
	/** 获取首页吆喝列表url */
	String HOME_CALL_LIST_URL = "http://www.17yaohe.com/?c=Api&a=getHomeCallList&partent=ht&key=ht";
	/** 获取首页关注列表url */
	String HOME_FOLLOW_SHOP_LIST_URL = "http://www.17yaohe.com/?c=Api&a=getFollowShopList&partent=ht&key=ht";
	/** 获取某个分类的吆喝列表url */
	String HOME_TYPE_CALL_LIST_URL = "http://www.17yaohe.com/?c=Api&a=getTypeCallList&partent=ht&key=ht";
	/** 获取某个分类的吆喝列表url */
	String HOME_SEARCH_URL = "http://www.17yaohe.com/?c=Api&a=getSearchShopCallList&partent=ht&key=ht";
	// ****************　首页接口描述　****************** //

	/** 获取优惠券详情url */
	String DETAILS_GET_COUPON = "http://www.17yaohe.com/?c=Api&a=getCoupon&partent=ht&key=ht";
	/** 领取优惠券url */
	String SHOP_GET_COUPON = "http://www.17yaohe.com/?c=Api&a=giveCoupon&partent=ht&key=ht";
	/** 获取会员卡详情url */
	String DETAILS_GET_CARD = "http://www.17yaohe.com/?c=Api&a=getCard&partent=ht&key=ht";
	/** 领取会员卡url */
	String SHOP_GET_CARD = "http://www.17yaohe.com/?c=Api&a=giveCard&partent=ht&key=ht";
	/** 获取活动详情url */
	String DETAILS_GET_ACTIVITY = "http://www.17yaohe.com/?c=Api&a=getActivity&partent=ht&key=ht";
	/** 获取新品详情url */
	String DETAILS_GET_NEW_PRODUCT = "http://www.17yaohe.com/?c=Api&a=getNewProduct&partent=ht&key=ht";
	/** 获取商家个人详情url */
	String DETAILS_GET_BUSINESS_SHOP_INFO = "http://www.17yaohe.com/?c=Api&a=getShopInfo&partent=ht&key=ht";
	/** 获取商家某类型服务列表详情url */
	String DETAILS_GET_BUSINESS_SEVICE_INFO = "http://www.17yaohe.com/?c=Api&a=getShopServiceList&partent=ht&key=ht";
	/** 获取吆喝详情url */
	String DETAILS_GET_CALL_INFO = "http://www.17yaohe.com/?c=Api&a=getCall&partent=ht&key=ht";

	// ************　店铺点评、店铺收藏、店铺点赞 　************** //
	/** 店铺点赞url */
	String SHOP_PRAISE_URL = "http://www.17yaohe.com/?c=Api&a=shopPraise&partent=ht&key=ht";
	/** 店铺点评url */
	String SHOP_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=shopComment&partent=ht&key=ht";
	/** 店铺收藏url */
	String SHOP_COLLECTION_URL = "http://www.17yaohe.com/?c=Api&a=shopCollection&partent=ht&key=ht";
	/** 店铺关注url */
	String SHOP_FOLLOW_URL = "http://www.17yaohe.com/?c=Api&a=shopFollow&partent=ht&key=ht";
	/** 取消关注url */
	String CANCEL_FOLLOW_URL = "http://www.17yaohe.com/?c=Api&a=cancelFollow&partent=ht&key=ht";
	/** 检测店铺关注状态url */
	String SHOP_FOLLOW_STATUS_URL = "http://www.17yaohe.com/?c=Api&a=isFollowed&partent=ht&key=ht";

	// ************　吆喝点评、吆喝收藏、吆喝点赞 　************** //
	/** 吆喝点赞url */
	String CALL_PRAISE_URL = "http://www.17yaohe.com/?c=Api&a=callPraise&partent=ht&key=ht";
	/** 吆喝点评url */
	String CALL_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=callComment&partent=ht&key=ht";
	/** 吆喝收藏url */
	String CALL_FOLLOW_URL = "http://www.17yaohe.com/?c=Api&a=callCollection&partent=ht&key=ht";
	/** 获取吆喝的评论列表url */
	String CALL_DETAILS_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=getCallCommentList&partent=ht&key=ht";
	/** 获取店铺的评论列表url */
	String BUSINESS_DETAILS_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=getShopCommentList&partent=ht&key=ht";

	/** 写入举报url */
	String ADD_REPORT_URL = "http://www.17yaohe.com/?c=Api&a=addReport&partent=ht&key=ht";

	// ****************　附近商圈类接口描述　****************** //
	/** 附近url */
	String NEAR_BY_SHOP_LIST = "http://www.17yaohe.com/?c=Api&a=getNearbyList&partent=ht&key=ht";
	// ****************　附近商圈类接口描述　****************** //   

	// ****************　商家信息接口描述　****************** //
	/** 删除优惠券url */
	String BUSINESS_DELETE_COUPON = "http://www.17yaohe.com/?c=Api&a=delCoupon&partent=ht&key=ht";
	/** 删除会员卡url */
	String BUSINESS_DELETE_CARD = "http://www.17yaohe.com/?c=Api&a=delCard&partent=ht&key=ht";
	/** 删除新品url */
	String BUSINESS_DELETE_NEW = "http://www.17yaohe.com/?c=Api&a=delNewProduct&partent=ht&key=ht";
	/** 删除活动url */
	String BUSINESS_DELETE_ACTTIVITY = "http://www.17yaohe.com/?c=Api&a=delActivity&partent=ht&key=ht";
	/** 删除吆喝url */
	String BUSINESS_DELETE_CALL = "http://www.17yaohe.com/?c=Api&a=delcall&partent=ht&key=ht";
	/** 取消关注url */
	String CANCEL_FOLLOWS = "http://www.17yaohe.com/?c=Api&a=cancelFollow&partent=ht&key=ht";
	
	/**发送消息 chatting */
	String SEND_CHATTING_URL = "http://www.17yaohe.com/?c=Api&a=sms&partent=ht&key=ht";
	/**删除吆喝评论*/
	String DELYAOHE_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=delShopServiceComment&partent=ht&key=ht";
	/**删除店铺评论*/
	String DELSHOP_COMMENT_URL = "http://www.17yaohe.com/?c=Api&a=delShopComment&partent=ht&key=ht";
	
	

}
