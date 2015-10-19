package com.collcloud.yaohe.entity;

import java.io.Serializable;

public class MyYhList implements Serializable {
	private static final long serialVersionUID = 4245775330564644681L;
	/** 吆喝ID */
	public String id;
	/** 吆喝内容 */
	public String content;
	public String title;
	/** 图片地址1 */
	public String img1;
	/** 1券 2卡 3新品 4活动 0无 */
	public String type;
	/** 引用内容ID */
	public String c_id;
	/** 点赞次数 */
	public String zan_num;
	/** 评论次数 */
	public String comment_num;
	/** 收藏次数 */
	public String collection_num;

}
