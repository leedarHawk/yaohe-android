package com.collcloud.yaohe.api;

import java.io.Serializable;

/**
 * 实体基类：实现序列化
 * 
 * @ClassName Base
 * @Description 处理服务器返回的数据实体类
 * @author CollCloud_小米
 */
public abstract class Base implements Serializable {

	private static final long serialVersionUID = -7893898313397571579L;
	public final static String UTF8 = "UTF-8";
	public final static String RESULT_ROOT = "17yaohe";
	/**
	 * 返回JSON数据的data模块，包含各机能的业务数据
	 */
	public final static String RESULT_DATA = "data";
	/**
	 * 返回JSON数据的status模块，主要区分返回数据是否正常
	 */
	public final static String RESULT_STATUS = "status";

}
