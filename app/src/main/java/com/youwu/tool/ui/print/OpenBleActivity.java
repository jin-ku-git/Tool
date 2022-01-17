package com.youwu.tool.ui.print;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.youwu.tool.R;
import com.youwu.tool.ui.print.config.Constants;


public class OpenBleActivity extends Activity {

	private final static String TAG = "OpenBleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFinishOnTouchOutside(false);
		setContentView(R.layout.activity_open_ble);
		TextView tvOpen = (TextView) findViewById(R.id.tv_open);
		tvOpen.setOnClickListener(openListener);
		TextView tvCancel = (TextView) findViewById(R.id.tv_cancel);
		tvCancel.setOnClickListener(cancelListener);
	}

	OnClickListener openListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setResult(Constants.RESULTCODE_OPEN_BLE);
			finish();
		}
	};

	OnClickListener cancelListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(Constants.RESULTCODE_CLOSE_BLE);
			finish();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(Constants.RESULTCODE_CLOSE_BLE);
			finish();
		} else {
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

}