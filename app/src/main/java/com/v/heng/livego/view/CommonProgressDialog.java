/**
 * 
 */
package com.v.heng.livego.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.v.heng.livego.R;


/**
 * @author Administrator
 *
 */
public class CommonProgressDialog extends Dialog {

//	private ProgressBar progressBar;
	private TextView contentTv;
	private boolean onBackCancel = false;  //后退取消
	

	public CommonProgressDialog(Context context, String message, boolean onBackCancel) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		this.setContentView(R.layout.dialog_progress);
		
		this.onBackCancel = onBackCancel;
		contentTv = (TextView) findViewById(R.id.contentTv);
		contentTv.setText(message);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(!onBackCancel && keyCode==KeyEvent.KEYCODE_BACK)
			return true;
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	
	
	
}
