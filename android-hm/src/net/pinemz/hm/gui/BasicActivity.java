package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public abstract class BasicActivity
	extends Activity
{
	public static final String TAG = "BasicActivity";
	
	private Dialog currentDialog = null;
	
	protected synchronized void showDialog(Dialog dialog){
		Log.d(TAG, "showDialog");
		assert dialog != null;
		
		this.closeDialog();
		this.currentDialog = dialog;
		this.currentDialog.show();
	}

	protected synchronized void closeDialog(){
		Log.d(TAG, "closeDialog");
		
		if(this.currentDialog != null){
			this.currentDialog.dismiss();
			this.currentDialog = null;
		}
	}
	
	protected synchronized void showProgressDialog(int messageId) {
		this.showProgressDialog(
				R.string.loading_title,
				messageId
				);
	}
	
	protected synchronized void showProgressDialog(int titleId, int messageId){
		this.showProgressDialog(
			this.getResources().getString(titleId),
			this.getResources().getString(messageId)
			);
	}

	protected synchronized void showProgressDialog(String title, String message){
		ProgressDialog dialog = new ProgressDialog(this);
		
		dialog.setIndeterminate(true);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCanceledOnTouchOutside(false);
		
		this.showDialog(dialog);
	}
	
	protected void showFinishAlertDialog(int titleId, int msgId) {
		Dialog dialog = this.showAlertDialog(
				titleId,
				msgId
				);
		
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				BasicActivity.this.finish();
			}
		});
    }
    
	protected synchronized Dialog showAlertDialog(int titleId, int messageId) {
		return this.showAlertDialog(
				this.getResources().getString(titleId),
				this.getResources().getString(messageId)
				);
	}
	
	protected synchronized Dialog showAlertDialog(String title, String message) {
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(R.string.ok_button, null)
			.create();
		
		this.showDialog(dialog);
		
		return dialog;
	}
	
	private <T> void startActivity(Class<T> k) {
		// 現在が既にそのアクティビティである場合は何もしない
		if (k.isInstance(this)){ return; }
    	
    	Intent intent = new Intent(this.getApplicationContext(), k);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	
    	this.getApplicationContext().startActivity(intent);
	}
	
	/**
	 * メインアクティビティへ移動する
	 */
	protected void startMainActivity() {
		this.startActivity(MainActivity.class);
	}
	
	/**
	 * 設定アクティビティへ移動する  
	 */
    protected void startSettingsActivity() {
    	this.startActivity(SettingsActivity.class);
    }
}
