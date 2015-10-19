package com.collcloud.yaohe.activity.chat;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.ChatInfo;
import com.collcloud.yaohe.ui.adapter.ChatAdapter;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECDevice.OnECDeviceConnectListener;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

/**
 * @类说明 聊天界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
@SuppressLint("ShowToast")
public class ChattingActivity extends BaseActivity implements OnClickListener {
	private String gittest;
	private ListView lv_chatting;
	private Button btn_send;
	private EditText et_message;
	private String message_content;
	private static String Tag = "IM_DEMO_TEXT.ChattingActivity";
	@SuppressWarnings("rawtypes")
	private ArrayList arrayList;// 用来存放两个聊天对象所说的内容
	private BaseAdapter baseAdapter;
	private String account;
	private String mAccountTo;
	private String mNickname;
	public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";
	private TextView tv_title;
	private LinearLayout tv_logout;
	private ECInitParams params;
	public static final String ACTION_LOGOUT = "com.yuntongxun.ECDemo_logout";
	/** 是否是同步消息 */
	private boolean isFirstSync = false;
    // test git
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		lv_chatting = (ListView) findViewById(R.id.lv_chatting);
		btn_send = (Button) findViewById(R.id.btn_send);
		et_message = (EditText) findViewById(R.id.ed_message);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_logout = (LinearLayout) findViewById(R.id.tv_logout);
		tv_logout.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		// 其他页面传过来的账户号码&昵称
		mAccountTo = getStringExtra("ACCOUNTTO");

		account = mLoginDataManager.getUserPhone();
		mNickname = getIntent().getStringExtra("NICKNAME");// 对方的昵称

		// 聊天界面标题
		tv_title.setText(mNickname);

		InitSDK();
		initDate();

	}

	private void InitSDK() {
		if (!ECDevice.isInitialized()) {
			ECDevice.initial(this, new ECDevice.InitListener() {
				@Override
				public void onInitialized() {
					// SDK已经初始化成功
					Toast.makeText(ChattingActivity.this, "SDK初始化成功", 1).show();
					// SDK已经初始化成功
					// 第二步：设置注册参数、设置通知掉监听
					// 构建注册所需要的参数信息
					if (params == null || params.getInitParams() == null
							|| params.getInitParams().isEmpty()) {
						params = new ECInitParams();
					}
					// 重置
					params.reset();
					params.setUserid(account);
					params.setAppKey("aaf98f894f06f288014f0788d72901c8");
					params.setToken("34cea21364846c65119fc23913912df3");
					// 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
					// 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
					// 3 LoginMode（FORCE_LOGIN AUTO）
					params.setMode(ECInitParams.LoginMode.AUTO);
					// 设置登陆验证模式（是否验证密码）
					params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);

					// 第三步：验证参数是否正确，注册SDK
					Log.i(Tag, "validate=" + params.validate());
					if (!params.validate()) {
						Log.i(Tag, "注册参数错误，请检查。。");
						Intent intent = new Intent(ACTION_SDK_CONNECT);
						intent.putExtra("error", -1);
						sendBroadcast(intent);
						return;
					}
					params.setOnDeviceConnectListener(new OnECDeviceConnectListener() {

						@Override
						public void onDisconnect(ECError arg0) {

						}

						@Override
						public void onConnectState(
								ECDevice.ECConnectState state, ECError error) {
							// TODO Auto-generated method stub
							if (state == ECDevice.ECConnectState.CONNECT_FAILED
									&& error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
								Toast.makeText(getApplicationContext(), "连接成功",
										1).show();
								Log.i(Tag, "连接成功");
							}

							Intent intent = new Intent(ACTION_SDK_CONNECT);
							intent.putExtra("error", error.errorCode);
							sendBroadcast(intent);
						}

						@Override
						public void onConnect() {
						}
					});// 登录后的接口回调

					params.setOnChatReceiveListener(new OnChatReceiveListener() {

						@Override
						public void OnReceiveGroupNoticeMessage(
								ECGroupNoticeMessage arg0) {
							Log.i(Tag, "OnReceiveGroupNoticeMessage:" + arg0);
						}

						@SuppressWarnings("unchecked")
						@Override
						public void OnReceivedMessage(ECMessage arg0) {
							Log.i(Tag, "OnReceivedMessage:" + arg0);

							Toast.makeText(getApplicationContext(), "接受成功",
									1000).show();

							if (arg0 != null) {
								ECTextMessageBody msg = (ECTextMessageBody) arg0
										.getBody();

								Log.i(Tag, "接受到的消息1" + msg.getMessage());

								ChatInfo cc = new ChatInfo(account, msg
										.getMessage(), 0);

								arrayList.add(cc);
								baseAdapter.notifyDataSetChanged();
							}
						}

						@Override
						public int onGetOfflineMessage() {
							Log.i(Tag, "onGetOfflineMessage");
							// 获取全部的离线历史消息
							return ECDevice.SYNC_OFFLINE_MSG_ALL;
						}

						@Override
						public void onOfflineMessageCount(int arg0) {
							// 离线消息的总数
						}

						@Override
						public void onReceiveDeskMessage(ECMessage arg0) {
							Log.i(Tag, "onReceiveDeskMessage:" + arg0);

						}

						@SuppressWarnings("unchecked")
						@Override
						public void onReceiveOfflineMessage(List<ECMessage> msgs) {
							Log.i(Tag,
									"[onReceiveOfflineMessage] show notice false");
							if (msgs != null && !msgs.isEmpty() && !isFirstSync)
								isFirstSync = true;
							for (ECMessage msg : msgs) {
								ECTextMessageBody msg1 = (ECTextMessageBody) msg
										.getBody();
								ChatInfo ddd = new ChatInfo(account, msg1
										.getMessage(), 0);
								arrayList.add(ddd);
								baseAdapter.notifyDataSetChanged();

							}

						}

						@Override
						public void onReceiveOfflineMessageCompletion() {
							Log.i(Tag, "onReceiveOfflineMessageCompletion");
						}

						@Override
						public void onServicePersonVersion(int arg0) {
						}

						@Override
						public void onSoftVersion(String arg0, int arg1) {
						}

					});
					ECDevice.login(params);
				}

				@Override
				public void onError(Exception exception) {
					// SDK 初始化失败,可能有如下原因造成
					// 1、可能SDK已经处于初始化状态
					// 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
					// 或者未配置服务属性android:exported="false";
					// 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
					// Android Build.VERSION.SDK_INT 以及以上版本）

				}
			});
		}
	}

	@SuppressWarnings("rawtypes")
	private void initDate() {
		// TODO Auto-generated method stub
		arrayList = new ArrayList();
		baseAdapter = new ChatAdapter(this, arrayList);
		lv_chatting.setAdapter(baseAdapter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_logout:
			ECDevice.logout(new ECDevice.OnLogoutListener() {

				@Override
				public void onLogout() {
					// TODO Auto-generated method stub
					if (params != null && params.getInitParams() != null) {
						params.getInitParams().clear();
					}
					params = null;
					sendBroadcast(new Intent(ACTION_LOGOUT));
					Log.i(Tag, "点击退出，调用登出");
				}
			});
			ChattingActivity.this.finish();
			break;

		case R.id.btn_send:
			message_content = et_message.getText().toString().trim();
			if(null==message_content || "".equals(message_content)) {
				Toast.makeText(ChattingActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				// 组建一个待发送的ECMessage
				ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
				// 设置消息的属性：发出者，接受者，发送时间等
				msg.setForm(account);
				msg.setMsgTime(System.currentTimeMillis());
				// if (account.equals("13389902527")) {
				// // 设置消息接收者
				// msg.setTo("13519386403");
				// msg.setSessionId("13519386403");
				// } else {
				// // 设置消息接收者
				// msg.setTo("13389902527");
				// }

				if (account.equals(mLoginDataManager.getUserPhone().toString()
						.trim())) {
					// 设置消息接收者
					msg.setTo(mAccountTo);
					msg.setSessionId(mAccountTo);
				} else {
					// 设置消息接收者
					msg.setTo(account);
				}
				msg.setUserData("");
				// 设置消息发送类型（发送或者接收）
				msg.setDirection(ECMessage.Direction.SEND);
				// 创建一个文本消息体，并添加到消息对象中
				ECTextMessageBody msgBody = new ECTextMessageBody(
						message_content);
				Log.i(Tag, "ECTextMessageBody:" + msgBody);
				// 将消息体存放到ECMessage中
				msg.setBody(msgBody);
				// 调用SDK发送接口发送消息到服务器
				ECChatManager manager = ECDevice.getECChatManager();
				manager.sendMessage(msg,
						new ECChatManager.OnSendMessageListener() {
							@Override
							public void onSendMessageComplete(ECError error,
									ECMessage message) {
								// 处理消息发送结果
								if (message == null) {
									return;
								}
								// 将发送的消息更新到本地数据库并刷新UI

							}

							@Override
							public void onProgress(String msgId, int totalByte,
									int progressByte) {
								// 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
							}

							@Override
							public void onComplete(ECError error) {
								// 忽略
								Log.i(Tag, "onComplete:" + error);
							}
						});
			} catch (Exception e) {
				// 处理发送异常
				Log.e(Tag, "send message fail , e=" + e.getMessage());
			}
			ChatInfo ci2 = new ChatInfo(account, message_content, 1);
			arrayList.add(ci2);
			baseAdapter.notifyDataSetChanged();
			et_message.setText("");
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void OnReceivedMessage(ECMessage msg) {
		if (msg == null) {
			return;
		}
		// 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
		ECMessage.Type type = msg.getType();
		if (type == ECMessage.Type.TXT) {
			// 在这里处理文本消息
			ECTextMessageBody textMessageBody = (ECTextMessageBody) msg
					.getBody();
			Log.i(Tag, "获取到的文本消息：" + textMessageBody);
			String message = textMessageBody.getMessage();
			Log.i(Tag, "获取到的getUserData：" + msg.getUserData());
			ChatInfo ci3 = new ChatInfo(null, message, 0);
			arrayList.add(ci3);

			baseAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void resetLayout() {

		LinearLayout ll_chat_root = (LinearLayout) findViewById(R.id.ll_chat_root);
		SupportDisplay.resetAllChildViewParam(ll_chat_root);
	}

}
