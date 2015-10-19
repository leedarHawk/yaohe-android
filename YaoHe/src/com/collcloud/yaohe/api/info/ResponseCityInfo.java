package com.collcloud.yaohe.api.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.collcloud.yaohe.api.BaseResponseInfo;
import com.collcloud.yaohe.ui.utils.CCLog;

/**
 * 解析服务器返回的城市信息
 * 
 * @ClassName ResponseCityInfo
 */
public class ResponseCityInfo extends BaseResponseInfo {

	private static final long serialVersionUID = 8626221661759368692L;
	/**
	 * 字母
	 */
	@SuppressWarnings("unused")
	private String mStrLetter;
	/**
	 * 是否热门
	 */
	@SuppressWarnings("unused")
	private String mIsHot;
	/**
	 * 返回所有的城市信息集合
	 */
	private List<BaseResponseInfo> mCityInfos = new ArrayList<BaseResponseInfo>();
	/**
	 * 热门城市列表
	 */
	private List<BaseResponseInfo> mHotCitys = new ArrayList<BaseResponseInfo>();

	/**
	 * 已开通城市列表
	 */
	private List<BaseResponseInfo> mOpenCitys = new ArrayList<BaseResponseInfo>();

	/**
	 * 解析所有服务器返回的所有城市信息集
	 * 
	 * @Title parse 解析数据
	 * @Description 包括热门城市列表，已开通所有城市
	 * @return ResponseCityInfo
	 * @param jsonData
	 *            城市信息数据源
	 */
	public ResponseCityInfo parseAllCity(JSONObject jsonData)
			throws IOException, JSONException {
		ResponseCityInfo cityInfo = new ResponseCityInfo();
		// 正常、异常返回code
		if (jsonData.has(RESULT_STATUS)) {
			JSONObject jsonObject = jsonData.getJSONObject(RESULT_STATUS);
			if (jsonObject.has(KEY_RESULT_CODE)) {
				cityInfo.setmCode(jsonData.optInt(KEY_RESULT_CODE, 0));
				CCLog.e("code is ", " " + jsonData.optInt(KEY_RESULT_CODE, 0));
			}
			if (jsonObject.has(KEY_RESULT_MSG)) {
				cityInfo.setmStrMsg(jsonData.optString(KEY_RESULT_MSG));
				CCLog.e("MSG is ", " " + jsonData.optString(KEY_RESULT_MSG));
			}

		}
		// 数据集合结点
		if (jsonData.has(RESULT_DATA)) {

			JSONArray resultJsonArray = jsonData.getJSONArray(RESULT_DATA);

			for (int i = 0; i < resultJsonArray.length(); i++) {
				JSONObject resultJsonObject = resultJsonArray.getJSONObject(i);
				BaseResponseInfo allCity = new BaseResponseInfo();
				if (resultJsonObject.has(KEY_RESULT_ID)) {
					allCity.setmStrId(resultJsonObject.optString(KEY_RESULT_ID));
				}
				if (resultJsonObject.has(KEY_RESULT_TITLE)) {
					allCity.setmStrTitle(resultJsonObject
							.optString(KEY_RESULT_TITLE));
				}
				cityInfo.mCityInfos.add(allCity);
			}

		}

		return cityInfo;
	}

	/**
	 * 解析所有热门城市
	 * 
	 * @Title parse 解析数据
	 * @Description 热门城市列表
	 * @return ResponseCityInfo
	 * @param jsonData
	 *            城市信息数据源
	 */
	public ResponseCityInfo parseHotCity(JSONObject jsonData)
			throws IOException, JSONException {
		ResponseCityInfo hotCityInfo = new ResponseCityInfo();
		// 正常、异常返回code
		if (jsonData.has(RESULT_STATUS)) {

			JSONObject jsonObject = jsonData.getJSONObject(RESULT_STATUS);
			if (jsonObject.has(KEY_RESULT_CODE)) {
				hotCityInfo.setmCode(jsonData.optInt(KEY_RESULT_CODE, 0));
			}
			if (jsonObject.has(KEY_RESULT_MSG)) {
				hotCityInfo.setmStrMsg(jsonData.optString(KEY_RESULT_MSG));
			}
		}
		// 数据集合结点
		if (jsonData.has(RESULT_DATA)) {
			JSONObject hJsonObject = jsonData.getJSONObject(RESULT_DATA);

			if (hJsonObject.getJSONArray("hotcity") != null) {
				JSONArray hotJsonArray = hJsonObject.getJSONArray("hotcity");
				for (int i = 0; i < hotJsonArray.length(); i++) {
					JSONObject hotJsonObject = hotJsonArray.getJSONObject(i);
					BaseResponseInfo hotInfo = new BaseResponseInfo();
					// 热门城市
					if (hotJsonObject.has(KEY_RESULT_ID)) {
						hotInfo.setmStrId(hotJsonObject
								.optString(KEY_RESULT_ID));
					}
					if (hotJsonObject.has(KEY_RESULT_TITLE)) {
						hotInfo.setmStrTitle(hotJsonObject
								.optString(KEY_RESULT_TITLE));
					}
					hotCityInfo.mHotCitys.add(hotInfo);
				}
			}
		}

		return hotCityInfo;
	}

	/**
	 * 解析所有已开通的城市
	 * 
	 * @Title parse 解析数据
	 * @Description 已开通的城市列表
	 * @return ResponseCityInfo
	 * @param jsonData
	 *            城市信息数据源
	 */
	public ResponseCityInfo parseOpenCity(JSONObject jsonData)
			throws IOException, JSONException {
		ResponseCityInfo openCityInfo = new ResponseCityInfo();
		// 正常、异常返回code
		if (jsonData.has(RESULT_STATUS)) {

			JSONObject jsonObject = jsonData.getJSONObject(RESULT_STATUS);
			if (jsonObject.has(KEY_RESULT_CODE)) {
				openCityInfo.setmCode(jsonData.optInt(KEY_RESULT_CODE, 0));
			}
			if (jsonObject.has(KEY_RESULT_MSG)) {
				openCityInfo.setmStrMsg(jsonData.optString(KEY_RESULT_MSG));
			}
		}
		// 数据集合结点
		if (jsonData.has(RESULT_DATA)) {

			JSONObject openJsonObject = jsonData.getJSONObject(RESULT_DATA);
			if (openJsonObject.has("citylist")) {
				JSONObject cityListObject = openJsonObject
						.getJSONObject("citylist");
				if (cityListObject.getJSONArray("a") != null) {
					JSONArray aLetterArray = cityListObject.getJSONArray("a");
					addOpenCity(openCityInfo, aLetterArray);
				}
				if (cityListObject.getJSONArray("b") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("b");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("c") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("c");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("d") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("d");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("e") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("e");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("f") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("f");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("g") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("g");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("h") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("h");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("j") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("j");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("k") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("k");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("l") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("l");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("m") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("m");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("n") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("n");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("p") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("p");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("q") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("q");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("r") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("r");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("s") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("s");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("t") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("t");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("w") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("w");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("x") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("x");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("y") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("y");
					addOpenCity(openCityInfo, bLetterArray);
				}
				if (cityListObject.getJSONArray("z") != null) {
					JSONArray bLetterArray = cityListObject.getJSONArray("z");
					addOpenCity(openCityInfo, bLetterArray);
				}

			}

		}

		return openCityInfo;
	}

	private void addOpenCity(ResponseCityInfo openCityInfo,
			JSONArray bLetterArray) throws JSONException {
		for (int i = 0; i < bLetterArray.length(); i++) {
			JSONObject aObject = bLetterArray.getJSONObject(i);
			BaseResponseInfo bLetterInfo = new BaseResponseInfo();
			if (aObject.has(KEY_RESULT_ID)) {
				bLetterInfo.setmStrId(aObject.optString(KEY_RESULT_ID));
			}
			if (aObject.has(KEY_RESULT_TITLE)) {
				bLetterInfo.setmStrTitle(aObject.optString(KEY_RESULT_TITLE));
			}
			openCityInfo.mOpenCitys.add(bLetterInfo);

		}
	}

	public List<BaseResponseInfo> getmAllCity() {
		return mCityInfos;
	}

	public void setmCityInfos(List<BaseResponseInfo> mCityInfos) {
		this.mCityInfos = mCityInfos;
	}

	public List<BaseResponseInfo> getmHotCity() {
		return mHotCitys;
	}

	public void setmHotCitys(List<BaseResponseInfo> mHotCitys) {
		this.mHotCitys = mHotCitys;
	}

	public List<BaseResponseInfo> getmOpenCity() {
		return mOpenCitys;
	}

	public void setmOpenCitys(List<BaseResponseInfo> mOpenCitys) {
		this.mOpenCitys = mOpenCitys;
	}
}
