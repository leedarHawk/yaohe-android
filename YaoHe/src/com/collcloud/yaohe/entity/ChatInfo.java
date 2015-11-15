package com.collcloud.yaohe.entity;

/**
 *要显示在聊天界面中的信息：消息的发送时间，聊天者的姓名，聊天内容
 */
public class ChatInfo {
	
	public String name;
	public String content;
	public String nickname;
	public String addtime;
	public String face;
	public String member_id;
	public int num;
	
	public ChatInfo() {
		
	}
	
	public ChatInfo(String name,String content,int num){
		this.name = name;
		this.content = content;
		this.num = num;
	}
	
	
	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
