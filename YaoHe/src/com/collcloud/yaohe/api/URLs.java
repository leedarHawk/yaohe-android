package com.collcloud.yaohe.api;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 接口URL实体类
 */
public class URLs implements Serializable {

	private static final long serialVersionUID = 620554142157243130L;
	public final static String HTTP = "http://";
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	/**
	 * 吆喝API调用共通前缀
	 */
	public final static String HOST = "http://www.17yaohe.com/?c=Api&a=";
	public final static String IMG_PRE = "http://static.htcheng.com/imgs/";
	/**
	 * 获取开通的城市及热门城市
	 */
	public final static String CITY_LIST = "getcitylist";
	/**
	 * 获取城市列表
	 */
	public final static String ALL_CITY = "getallcity";
	/**
	 * 个人注册
	 */
	public final static String REGISTER = "register";
	/**
	 * 商户注册
	 */
	public final static String SHOP_REGISTTER = "shopreg";

	private int objId;
	private String objKey = "";
	private int objType;

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getObjKey() {
		return objKey;
	}

	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	private final static String parseObjId(String path, String url_type) {
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains(URL_SPLITTER)) {
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		} else {
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objId
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type) {
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains("?")) {
			tmp = str.split("?");
			objKey = tmp[0];
		} else {
			objKey = str;
		}
		return objKey;
	}

	/**
	 * 解析url获得objKey
	 * 
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if (path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}
}
