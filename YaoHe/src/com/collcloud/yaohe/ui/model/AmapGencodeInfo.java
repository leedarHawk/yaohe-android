package com.collcloud.yaohe.ui.model;

import java.io.Serializable;

public class AmapGencodeInfo implements Serializable {

	private static final long serialVersionUID = -6348005520001692486L;
	private String mStrQueryName;
	private String mStrQueryAddress;
	private String mStrCityName;

	public AmapGencodeInfo() {
		// TODO Auto-generated constructor stub
	}

	public AmapGencodeInfo(String queryName, String queryAddress) {
		super();
		this.mStrQueryName = queryName;
		this.mStrQueryAddress = queryAddress;
	}

	public String getmStrQueryName() {
		return mStrQueryName;
	}

	public void setmStrQueryName(String mStrQueryName) {
		this.mStrQueryName = mStrQueryName;
	}

	public String getmStrCityName() {
		return mStrCityName;
	}

	public void setmStrCityName(String mStrCityName) {
		this.mStrCityName = mStrCityName;
	}

	public String getmStrQueryAddress() {
		return mStrQueryAddress;
	}

	public void setmStrQueryAddress(String mStrQueryAddress) {
		this.mStrQueryAddress = mStrQueryAddress;
	}

}
