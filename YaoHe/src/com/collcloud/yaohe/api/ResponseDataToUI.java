package com.collcloud.yaohe.api;

import java.util.ArrayList;
import java.util.List;

public class ResponseDataToUI {

	public String apiId = "";
	public Object responseData = null;
	public String result = "";
	public List<Object> responseDataList;

	public int netWorkErrorCode = ApiAccessErrorManager.OK;

	public ResponseDataToUI() {
		responseDataList = new ArrayList<Object>();
	}
}
