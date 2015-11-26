package com.collcloud.yaohe.activity.person.huiyuanka;

import java.util.ArrayList;

import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.person.huiyuanka.detail.PersonHYKDetailActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.swipe.view.XListView;
import com.collcloud.swipe.view.XListView.IXListViewListener;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.activity.person.youhui.PersonYouhuiActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.HYCardList;
import com.collcloud.yaohe.ui.adapter.PersonHYKJAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 * @类说明 我的(个人)会员卡页面
 */
public class PersonHYKActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "HYK";
    /**
     * 返回按钮
     */
    private ImageView tv_actionbarback;
    /**
     * 标题信息
     */
    private TextView tv_actionbartitle;
    /**
     * 数据加载进度条
     */
    private Dialog mDialog;

    /**
     * 优惠券数据集合
     */
    private ArrayList<HYCardList> mDatas;
    private XListView mXListView;
    private PersonHYKJAdapter mAdapter;

    // ******** Tips *******//
    private LinearLayout mLlEmpty;
    private TextView mTvEmptyTips;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_hyk);

        progressbar(PersonHYKActivity.this, R.layout.loading_progress);

        intialSource();

        accessNetGetHYK();

        // 绑定适配器PersonHYKJAdapter
    }

    /**
     * 数据加载
     */
    private void progressbar(Context context, int layout) {
        mDialog = new AlertDialog.Builder(this).create();
        // mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(layout);
    }

    /**
     * 获取会员卡
     */
    private void accessNetGetHYK() {

        HttpUtils http = new HttpUtils();
        // 用来封装参数
        RequestParams params = new RequestParams();
        // 用户ID
        CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
        params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

        http.send(HttpRequest.HttpMethod.POST, ContantsValues.MYCARD, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                        mDialog.dismiss();
                        showToast("网络访问失败,检查网络设置");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {

                        mDialog.dismiss();
                        CCLog.v(TAG, "网络获取优惠券数据成功");
                        CCLog.v(TAG, arg0.result);
                        JSONObject object, object2;
                        // 网络返回商圈结果状态信息
                        String responseInfo = "";
                        // 网络访问商圈状态码
                        String code = "";
                        // 网络商圈返回消息
                        String responseMsg = "";
                        try {

                            object = new JSONObject(arg0.result);
                            responseInfo = object.getString("status");
                            object2 = new JSONObject(responseInfo);

                            code = object2.getString("code");
                            responseMsg = object2.getString("message");

                            if (code.equals("0")) {

                                JSONArray jsoArray = object
                                        .optJSONArray("data");
                                final int itemHYKCount = jsoArray.length();

                                if (itemHYKCount > 0) {
                                    if (itemHYKCount == 1) {
                                        JSONObject yhObject = jsoArray
                                                .getJSONObject(0);
                                        if (Utils.isStringEmpty(yhObject
                                                .optString("id"))) {
                                            mLlEmpty.setVisibility(View.VISIBLE);
                                            mXListView.setVisibility(View.GONE);
                                            return;
                                        } else {
                                            mXListView
                                                    .setVisibility(View.VISIBLE);
                                            mLlEmpty.setVisibility(View.GONE);
                                        }
                                    }
                                    /** 可使用的会员卡数据集合 */
                                    mDatas = new ArrayList<HYCardList>();

                                    for (int i = 0; i < itemHYKCount; i++) {

                                        HYCardList hykItem = new HYCardList();

                                        JSONObject hykObject = jsoArray
                                                .getJSONObject(i);

                                        hykItem.id = hykObject.optString("id");

                                        hykItem.member_card_id = hykObject
                                                .optString("member_card_id");

                                        hykItem.discount = hykObject
                                                .optString("discount");

                                        hykItem.member_id = hykObject
                                                .optString("member_id");

                                        if (!Utils.isStringEmpty(hykObject
                                                .optString("img1"))) {
                                            hykItem.img1 = URLs.IMG_PRE
                                                    + hykObject
                                                    .optString("img1");
                                        }

                                        hykItem.title = hykObject
                                                .optString("title");

                                        mDatas.add(hykItem);

                                    }
                                    setCardData(mDatas);

                                } else {
                                    showToast("您还没有可使用的会员卡");
                                }

                            } else {
                                showToast("会员卡信息加载失败了，返回重试!");
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setCardData(ArrayList<HYCardList> usedList) {

        if (mAdapter == null) {
            mAdapter = new PersonHYKJAdapter(PersonHYKActivity.this, usedList);
            mXListView.setAdapter(mAdapter);
        } else {
            mAdapter.refresh(usedList);
        }
    }

    /**
     * UI资源初始化
     */
    private void intialSource() {

        // 刷新相关
        this.tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
        tv_actionbarback.setOnClickListener(this);

        this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
        tv_actionbartitle.setText("会员卡");

        handler = new Handler();
        mLlEmpty = (LinearLayout) this
                .findViewById(R.id.ll_person_usable_card_empty);
        mLlEmpty.setVisibility(View.GONE);
        mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
        mTvEmptyTips.setText("没有可使用的会员卡\n\n去首页推荐看看吧");
        mTvEmptyTips.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                baseStartActivity(new Intent(PersonHYKActivity.this,
                        MainActivity.class));
                finish();
            }
        });

        // Xlistview
        this.mXListView = (XListView) findViewById(R.id.xlv_person_useable_card);
        handler = new Handler();
        // 设置xlistview可以加载、刷新
        mXListView.setPullLoadEnable(false);
        // 设置xlistview可以刷新
        mXListView.setPullRefreshEnable(true);
        mXListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                CCLog.i("优惠券Item Pos:", position + " ");
                Intent intent = new Intent();
                intent.putExtra("selectedhykid", mDatas.get(position - 1).id);
                intent.putExtra("selectedsourid", mDatas.get(position - 1).member_card_id);
                intent.setClass(PersonHYKActivity.this, PersonHYKDetailActivity.class);
                baseStartActivity(intent);

            }
        });
        mXListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                accessNetGetHYK();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mXListView.stopRefresh();
                        showToast("刷新成功");
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                accessNetGetHYK();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        mXListView.stopLoadMore();
                        showToast("数据加载成功");
                    }
                }, 1000);
            }
        });

    }

    /**
     * UI界面动作事件
     */
    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {

            case R.id.ll_tv_actionbarback:
                baseStartActivity(new Intent(PersonHYKActivity.this,
                        PersonYouhuiActivity.class));
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    protected void resetLayout() {

        LinearLayout rel_myperhyk = (LinearLayout) findViewById(R.id.rl_perhyk_root);
        SupportDisplay.resetAllChildViewParam(rel_myperhyk);
        LinearLayout topCancelLayout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
        topCancelLayout.setOnClickListener(this);
    }
}
