package com.collcloud.yaohe.activity.person.shenqingweishangjia;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.IndustryType;
import com.collcloud.yaohe.ui.adapter.FuJinShopCityAdapter;
import com.collcloud.yaohe.ui.adapter.FujinShopTownAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 申请商家-选择行业分类
 * 
 * @ClassName ChoiceClassifyActivity
 */
public class ChoiceClassifyActivity extends BaseActivity implements Callback {
	private static final String TAG = "选择行业分类Activity";
	private ListView mLvLeft = null;
	private ListView mLvRight = null;

	public FuJinShopCityAdapter mAreaAdapter = null;
	public FujinShopTownAdapter mTownAdapter = null;

	private List<String> mLeftData = new ArrayList<String>();
	private List<String> mRightData = new ArrayList<String>();

	/** 一级行业大分类 */
	private List<IndustryType> mOneClassList;
	private List<IndustryType> mTwoClassList;
	private int mLeftIndex = 0;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_classfy);
		getOneClassfyList(GlobalConstant.INVALID_VALUE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mAreaAdapter != null) {
					mAreaAdapter.setSelectedItem(0);
					mAreaAdapter.notifyDataSetChanged();
				}
				getOneClassfyList("23");
			}
		}, 500);
	}

	/**
	 * 获取商家行业分类
	 * 
	 * @Title getOneClassfyList
	 * @Description
	 * @param parent_id
	 *            当为0时是调取大分类列表
	 */
	private void getOneClassfyList(final String parent_id) {
		ApiAccess.showProgressDialog(ChoiceClassifyActivity.this, "获取分类中...",
				R.style.progress_dialog);

		CCLog.v(TAG, "正在获取行业分类数据");
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("parent_id", parent_id);
		http.send(HttpRequest.HttpMethod.POST, ContantsValues.BUSINESSTYPE,
				params, new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						 ApiAccess.dismissProgressDialog();
						CCLog.v(TAG, "网络获取行业分类失败");
						showToast("商家行业分类获取失败。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						 ApiAccess.dismissProgressDialog();
						CCLog.v(TAG, "网络获取行业分类成功");
						JSONObject object, object2;
						// 网络返回结果状态信息
						String responseInfo = "";
						// 网络返回数据信息
						JSONArray responseData = null;
						// 网络访问状态码
						String code = "";
						// 网络返回消息
						String responseMsg = "";

						try {
							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							responseData = object.getJSONArray("data");

							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (code.equals("0")) {// 请求成功
							if (responseData != null
									&& responseData.length() > 0) {
								CCLog.v("行业分类：", responseData.toString());
							}
							if (parent_id.equals("0")) {
								mOneClassList = parseJSONObject(responseData,
										"0");
								if (mLeftData != null && mLeftData.size() > 0) {
									setOneClassData();
								}
							} else {
								if (mTwoClassList != null) {
									mTwoClassList.clear();
								}
								mTwoClassList = new ArrayList<IndustryType>();
								mTwoClassList = parseJSONObject(responseData,
										parent_id);
								if (mRightData != null && mRightData.size() > 0) {
									// setTwoClassData();
									refreshTwoClassData();
								}
							}

						} else {// 返回提示信息
							CCLog.v(TAG, "网络返回了错误信息");
							showToast(responseMsg);
						}
					}
				});

	}

	/**
	 * 行业分类解析
	 */
	public ArrayList<IndustryType> parseJSONObject(JSONArray response,
			String parent_id) {
		if (mRightData != null) {
			mRightData.clear();
			mRightData = new ArrayList<String>();
		}
		ArrayList<IndustryType> industrytypes = new ArrayList<IndustryType>();
		IndustryType industrytype = null;

		for (int i = 0; i < response.length(); i++) {
			industrytype = new IndustryType();
			JSONObject object = null;
			try {
				object = response.getJSONObject(i);
				industrytype.setId(object.getString("id"));
				industrytype.setTitle(object.getString("title"));
				if (parent_id.equals("0")) {
					mLeftData.add(object.getString("title"));
				} else {
					mRightData.add(object.getString("title"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			industrytypes.add(industrytype);
		}
		return industrytypes;
	}

	private void setOneClassData() {
		mAreaAdapter = new FuJinShopCityAdapter(ChoiceClassifyActivity.this,
				mHandler, mLeftData);
		mLvLeft.setAdapter(mAreaAdapter);
	}

	@SuppressWarnings("unused")
	private void setTwoClassData() {
		mTownAdapter = new FujinShopTownAdapter(ChoiceClassifyActivity.this,
				mHandler, mRightData);
		mLvRight.setAdapter(mTownAdapter);
	}

	private void refreshTwoClassData() {
		if (mTownAdapter != null) {
			mTownAdapter.refreshData(mRightData);
		} else {
			mTownAdapter = new FujinShopTownAdapter(
					ChoiceClassifyActivity.this, mHandler, mRightData);
			mLvRight.setAdapter(mTownAdapter);
		}
	}

	@Override
	protected void resetLayout() {

		LinearLayout rlLayout = (LinearLayout) this
				.findViewById(R.id.ll_shenqing_choice_classfy);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		initTopOnlyBackTitle();
		setTopOnlyBackTitle("申请为商家");

		mLvLeft = (ListView) findViewById(R.id.lv_shenqing_shop_left_list);
		mLvRight = (ListView) findViewById(R.id.lv_shenqing_shop_right_list);
		mLvLeft.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mLvLeft.setSelection(0);
		mLvRight.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mHandler = new Handler(ChoiceClassifyActivity.this);
		mBtnIsCancelButton = true;
		mTopCancel = true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		Bundle data = msg.getData();

		switch (msg.what) {
		case 2: // 点击商圈-区域列表子项事件
			int areaIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_CITY_INDEX, 0);
			mAreaAdapter.setSelectedItem(areaIndex);
			mAreaAdapter.notifyDataSetChanged();
			mLeftIndex = areaIndex;
			// 选择区域对应下的商圈列表内容
			if (mOneClassList.size() > 0) {
				mRightData.clear();
				getOneClassfyList(mOneClassList.get(areaIndex).getId());
			}

			break;
		case 3: // 小分类列表子项点击事件
			final int districtIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_TOWN_INDEX, 0);
			mTownAdapter.setSelectedItem(districtIndex);
			mTownAdapter.notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					String oneClassID = "23";
					String twoClassID = "24";
					String oneClassName = "";
					String twoClassName = "";
					if (mOneClassList != null && mOneClassList.size() > 0) {
						oneClassID = mOneClassList.get(mLeftIndex).getId();
						oneClassName = mOneClassList.get(mLeftIndex).getTitle();
						CCLog.v("oneClassID :" + oneClassID + "  一级分类名称："
								+ oneClassName);
					}
					if (mTwoClassList != null && mTwoClassList.size() > 0) {
						twoClassID = mTwoClassList.get(districtIndex).getId();
						twoClassName = mTwoClassList.get(districtIndex).getTitle();
						CCLog.v("twoClassID :" + twoClassID + "  二级分类名称："
								+ twoClassName);
					}
					cancelDefinedAction(oneClassID, oneClassName, twoClassID,
							twoClassName);
				}
			} , 500);
	

			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void topCancelDefined() {
		super.topCancelDefined();
		cancelDefinedAction("23", "美食", "24", "");
	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		cancelDefinedAction("23", "美食", "24", "");
	}

	/**
	 * 返回前个页面传递的信息
	 * 
	 * @Title cancelDefinedAction
	 */
	private void cancelDefinedAction(String oneClassId, String oneClassName,
			String twoClassId, String twoClassName) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.putExtra("oneClassId", oneClassId);
		intent.putExtra("oneClassName", oneClassName);
		intent.putExtra("twoClassId", twoClassId);
		intent.putExtra("twoClassName", twoClassName);
		intent.putExtras(bundle);
		setResult(11, intent);
		ChoiceClassifyActivity.this.finish();
	}
}
