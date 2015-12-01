package com.collcloud.yaohe.activity.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DeatilsSearchInfo.SearchShopInfo;
import com.collcloud.yaohe.api.info.DeatilsSearchInfo.SearchYaoheInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.MyListView;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 搜索页面 -
 * 
 * @ClassName SearchActivity
 * @Description 搜索商家及相关吆喝
 * @author CollCloud_小米
 */
public class SearchActivity extends BaseActivity implements OnClickListener {
	private TextView mTvSearch = null;
	private EditText mEtSearch = null;
	private String mStrKeywords;
	// ********** 搜索相关吆喝展示 ********** //
	/**
	 * 相关吆喝 - 搜索结果为空的提示信息
	 */
	private LinearLayout mLlYaoHeNone = null;
	/**
	 * 搜索商家发布的吆喝 - 有搜索结果
	 */
	private LinearLayout mLlServiceCall;
	private MyListView mLvCalls = null;
	private SearchCallAdapter mSearchCallAdapter;
	/**
	 * 搜索商家的列表内容
	 */
	private List<SearchShopInfo> mListSearchShop = new ArrayList<SearchShopInfo>();

	// ********** 搜索商家展示 ********** //
	/**
	 * 搜索商家- 搜索结果为空的提示信息
	 */
	private LinearLayout mLlShopNone = null;
	/**
	 * 搜索商家展示 - 有搜索结果
	 */
	private LinearLayout mLlBusiness = null;
	private MyListView mLvShop = null;
	private SearchShopAdapter mSearchShopAdapter;
	/**
	 * 搜索吆喝的列表内容
	 */
	private List<SearchYaoheInfo> mListSearchYaohe = new ArrayList<SearchYaoheInfo>();

	// ********** 业务相关 ********** //
	private String mStrCityID = null;
	private TextView mTvDefaultTips = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 搜索商家及相关吆喝
	 * 
	 * @Title getSearchShopCallList
	 */
	private void getSearchShopCallList(String keywords) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("keywords", keywords);
		params.addBodyParameter("city_id", mStrCityID);
		//当搜索内容有空格时，报错，所以用post提交当搜索内容有空格时，报错，所以用post提交
		String url = ContantsValues.HOME_SEARCH_URL  + "&city_id=" + mStrCityID; //+ "&keywords=" + keywords
		CCLog.i("搜索参数信息： ", "city_id= " + mStrCityID + " keywords= " + keywords);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							CCLog.i("搜索结果详情：", responseInfo.result);
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject != null
										&& dataObject.has("shoplist")) {
									// 如果搜索结果中有对应的商家
									// JSONObject businessObject = dataObject
									// .optJSONObject("shoplist");
									JSONArray shopArray = dataObject
											.optJSONArray("shoplist");
									// 商家个人商铺介绍
									parseShopArray(shopArray);
									setSearchShopData();
								}
								if (dataObject != null
										&& dataObject.optJSONArray("calllist") != null) {
									// 如果搜索结果中有对应的吆喝信息
									JSONArray callArray = dataObject
											.optJSONArray("calllist");
									parseYaoHeArray(callArray);
									setSearchCallData();

								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(SearchActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 搜索结果中包含相关的商家
	 */
	private void parseShopArray(JSONArray shopArray) {

		if (shopArray != null && shopArray.length() > 0) {
			if (mListSearchShop != null) {
				mListSearchShop.clear();
			}
			mListSearchShop = new ArrayList<SearchShopInfo>();
			// 服务器接口的搜索结果为空时，总是返回一条空白信息，所以得处理
			if (shopArray.length() == 1) {
				JSONObject shopObject = shopArray.optJSONObject(0);
				if (Utils.isStringEmpty(shopObject.optString("id"))) {
					mLlShopNone.setVisibility(View.VISIBLE);
					mLvShop.setVisibility(View.GONE);
					return;
				}
			}
			mLlShopNone.setVisibility(View.GONE);
			mLvShop.setVisibility(View.VISIBLE);

			for (int i = 0; i < shopArray.length(); i++) {
				JSONObject shoObject = shopArray.optJSONObject(i);
				SearchShopInfo shopInfo = new SearchShopInfo();

				if (shoObject.has("id")) {// 店铺的ID
					shopInfo.id = shoObject.optString("id");
				}
				if (shoObject.has("title")) {// 标题
					shopInfo.title = shoObject.optString("title");
				}
				if (shoObject.has("member_id")) {// 会员ID
					shopInfo.member_id = shoObject.optString("member_id");
				}
				if (!Utils.isStringEmpty(shoObject.optString("face"))) {// 图片地址
					shopInfo.face = URLs.IMG_PRE + shoObject.optString("face");
				}
				mListSearchShop.add(shopInfo);
			}
		}

	}

	/**
	 * 搜索结果中包含相关的商家
	 */
	private void parseYaoHeArray(JSONArray yaoheArray) {

		if (yaoheArray != null && yaoheArray.length() > 0) {
			if (mListSearchYaohe != null) {
				mListSearchYaohe.clear();
			}
			mListSearchYaohe = new ArrayList<SearchYaoheInfo>();
			// 服务器接口的搜索结果为空时，总是返回一条空白信息，所以得处理
			if (yaoheArray.length() == 1) {
				JSONObject shopObject = yaoheArray.optJSONObject(0);
				if (Utils.isStringEmpty(shopObject.optString("id"))) {
					mLlYaoHeNone.setVisibility(View.VISIBLE);
					mLvCalls.setVisibility(View.GONE);
					return;
				}
			}

			mLlYaoHeNone.setVisibility(View.GONE);
			mLvCalls.setVisibility(View.VISIBLE);

			for (int i = 0; i < yaoheArray.length(); i++) {
				JSONObject callObject = yaoheArray.optJSONObject(i);
				SearchYaoheInfo callInfo = new SearchYaoheInfo();

				if (callObject.has("id")) {// 店铺的ID
					callInfo.id = callObject.optString("id");
				}
				if (callObject.has("title")) {// 标题
					callInfo.title = callObject.optString("title");
				}
				if (callObject.has("service_id")) {// 标题
					callInfo.service_id = callObject.optString("service_id");
				}
				if (callObject.has("content")) {
					callInfo.content = callObject.optString("content");
				}
				if (callObject.has("type")) {
					callInfo.type = callObject.optString("type");
				}
				if (callObject.has("member_id")) {// 会员ID
					callInfo.member_id = callObject.optString("member_id");
				}
				if (!Utils.isStringEmpty(callObject.optString("img"))) {// 图片地址
					callInfo.img = URLs.IMG_PRE + callObject.optString("img");
				}
				if (callObject.has("shop_id")) {// 店铺ID
					callInfo.shop_id = callObject.optString("shop_id");
				}
				if (callObject.has("shop_title")) {// 店铺名称
					callInfo.shop_title = callObject.optString("shop_title");
				}
				mListSearchYaohe.add(callInfo);
			}
		}

	}

	private void setSearchShopData() {
		mSearchShopAdapter = new SearchShopAdapter(SearchActivity.this,
				mListSearchShop);
		mLvShop.setAdapter(mSearchShopAdapter);
		mLvShop.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				if (arg0 != null && mListSearchShop.size() > 0) {
					String shopID = mListSearchShop.get(position).id;
					String memberID = mListSearchShop.get(position).member_id;
					String faceString = mListSearchShop.get(position).face;
					Intent intent = new Intent();
					intent.setClass(SearchActivity.this,
							DetailsBusinessInfoActivity.class);
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopID);
					intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberID);
					intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_FACE,
							faceString);
					baseStartActivity(intent);
				}

			}
		});

	}

	private void setSearchCallData() {
		mSearchCallAdapter = new SearchCallAdapter(SearchActivity.this,
				mListSearchYaohe);
		mLvCalls.setAdapter(mSearchCallAdapter);
		mLvCalls.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				if (arg0 != null && mListSearchYaohe.size() > 0) {
					String shopID = mListSearchYaohe.get(position).shop_id;
					String memberID = mListSearchYaohe.get(position).member_id;
					String serviceID = mListSearchYaohe.get(position).service_id;
					String callID = mListSearchYaohe.get(position).id;
					String type = mListSearchYaohe.get(position).type;
					Intent intent = new Intent();
					intent.setClass(SearchActivity.this,
							YaoHeLaDetailsActivity.class);
					intent.putExtra(IntentKeyNames.KEY_CALL_ID, callID);
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, shopID);
					intent.putExtra(IntentKeyNames.KEY_CALL_TYPE, type);
					intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberID);
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, serviceID);
					baseStartActivity(intent);
				}

			}
		});

	}

	@Override
	protected void resetLayout() {
		LinearLayout llLayout = (LinearLayout) this
				.findViewById(R.id.ll_activity_search_root_);
		SupportDisplay.resetAllChildViewParam(llLayout);

		mTvSearch = (TextView) findViewById(R.id.tv_search_cancel_text);
		mEtSearch = (EditText) findViewById(R.id.et_activity_search_view);
		mTvDefaultTips = (TextView) findViewById(R.id.tv_search_default_tips);
		mTvSearch.setOnClickListener(this);

		// ********** 搜索吆喝结果显示 ********* //
		mLlYaoHeNone = (LinearLayout) findViewById(R.id.ll_search_yaohe_none);
		mLlServiceCall = (LinearLayout) findViewById(R.id.ll_search_business_yaohe_viewgroup);
		mLvCalls = (MyListView) findViewById(R.id.lv_search_type_call_details);

		// ********** 搜索商家结果显示 ********* //
		mLlShopNone = (LinearLayout) findViewById(R.id.ll_search_shop_none);
		mLvShop = (MyListView) findViewById(R.id.lv_search_type_shop_details);
		mLlBusiness = (LinearLayout) findViewById(R.id.ll_search_business_shop_viewgroup);

		// ******　　页面初始化时，默认隐藏商家和吆喝的列表内容　******//
		mLlBusiness.setVisibility(View.GONE);
		mLlServiceCall.setVisibility(View.GONE);

		mLlYaoHeNone.setVisibility(View.GONE);
		mLlShopNone.setVisibility(View.GONE);
		mTvDefaultTips.setVisibility(View.VISIBLE);

		if (!Utils.isStringEmpty(GlobalVariable.sChoiceCityID)) {
			mStrCityID = GlobalVariable.sChoiceCityID;
		} else {
			mStrCityID = GlobalConstant.DEFAULT_CITY_ID;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_search_cancel_text:
			mStrKeywords = Utils.strFromView(mEtSearch);
			if(mStrKeywords != null && !"".equals(mStrKeywords) && !"null".equals(mStrKeywords)) {
				ApiAccess.showProgressDialog(SearchActivity.this, "搜索中...",
						R.style.progress_dialog);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();
						getSearchShopCallList(mStrKeywords);

						// mLlYaoHeNone.setVisibility(View.VISIBLE);
						// mLlShopNone.setVisibility(View.VISIBLE);
						mLlBusiness.setVisibility(View.VISIBLE);
						mLlServiceCall.setVisibility(View.VISIBLE);
						mTvDefaultTips.setVisibility(View.GONE);

					}
				}, 1000);
			} else {
				Toast.makeText(SearchActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
	}

}
