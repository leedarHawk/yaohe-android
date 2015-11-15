package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 
 * @author sms消息
 *
 */
public class MsgInfo implements Serializable{
	private static final long serialVersionUID = -6619378351641240773L;
	
	//发送消息者
	public String member_id;
	// 发送消息者名称，如果是会员，则是昵称，如果是商家，则是商铺名称
	public String nickname;
	// 送送者头像，如果是会员，则是会员头像，如果是商家，则是商铺头像
	public String face;
	//最后一次发送消息时间
	public String lastSendtime;
	//未读条数
	public String noReadCount;
	

}
