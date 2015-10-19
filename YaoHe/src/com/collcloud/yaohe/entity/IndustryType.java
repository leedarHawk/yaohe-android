package com.collcloud.yaohe.entity;

import java.io.Serializable;

public class IndustryType implements Serializable{

	/**
	 * @类说明：行业类型
	 * @author 赵洋洋
	 */
	private static final long serialVersionUID = 1L;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String id;
	public String title;

}
