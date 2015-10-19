package com.collcloud.yaohe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseFragment;

/**
 * 吆喝APP 主页部分-动态加载预留扩展页面
 * 
 * @ClassName HomeExtraFragment
 * @Description 预留扩展页面内容
 * @author CollCloud_小米
 */
public class HomeExtraFragment extends BaseFragment {

	public HomeExtraFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_home_extra, container, false);
		return v;
	}

	@Override
	protected void resetLayout(View view) {
		// TODO Auto-generated method stub

	}

}
