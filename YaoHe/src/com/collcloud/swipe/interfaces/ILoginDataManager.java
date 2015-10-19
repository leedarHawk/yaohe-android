package com.collcloud.swipe.interfaces;

/**
 * 吆喝APP接口，（个人/商家，登陆前后）所涉及的一些内部信息
 * 
 * @ClassName ILoginDataManager
 * @author CollCloud_小米
 */
public interface ILoginDataManager {

	/**
	 * 设定个人登陆状态（ 已登录设定为 loginStatus = "1" ）
	 * 
	 * @Title setLoginState
	 * @Description 保存登陆状态的值
	 * @return true 保存成功 ； false 未保存成功
	 */
	boolean setLoginState(String loginStatus);

	/**
	 * 获取个人信息，是否已经成功登陆？
	 * 
	 * @Title getLoginState
	 * @Description 个人登陆状态查询
	 * @return 状态： "0" 未登陆 ，"1" 已登陆
	 */
	String getLoginState();

	/**
	 * 设定商家的状态
	 * 
	 * @Description 当个人【申请为商家】后，设定成功的状态值
	 * @return true 保存成功 ； false 未保存成功
	 */
	boolean setBusinessState(String businessState);

	/**
	 * 获取商家的状态,是否已经通过商家审核
	 * 
	 * @Title getBusinessState
	 * @Description 商家申请状态查询
	 * @return 状态： "0" 未申请为商家 ，"1" 已申请为商家
	 */
	String getBusinessState();

	boolean setMemberId(String memberID);

	String getMemberId();

	boolean setUserId(String userID);

	String getUserID();

	boolean setUserPassword(String password);

	String getUserPassword();

	boolean setUserType(String userType);

	String getUserType();

	boolean setUserPhone(String userPhone);

	String getUserPhone();
	boolean setUserNickname(String nickname);
	
	String getUserNickName();

}
