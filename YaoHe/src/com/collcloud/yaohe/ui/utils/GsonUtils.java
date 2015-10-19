package com.collcloud.yaohe.ui.utils;

import com.google.gson.Gson;

public class GsonUtils {
	
	public static <T> T json2Bean(String result, Class<T> clz) {
		Gson gson = new Gson();
		T t = gson.fromJson(result, clz);
		return t;
	}
}
