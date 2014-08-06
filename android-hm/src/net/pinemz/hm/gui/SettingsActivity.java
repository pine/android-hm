package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import net.pinemz.hm.api.HmApi;
import net.pinemz.hm.api.Prefecture;
import net.pinemz.hm.api.PrefectureCollection;
import net.pinemz.hm.storage.CommonSettings;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.common.base.Optional;

public class SettingsActivity
	extends BasicActivity
{
	public static final String TAG = "SettingsActivity";
	
	private RequestQueue requestQueue;
	private HmApi hmApi;
	private PrefectureCollection prefectures;
	private CommonSettings settings;
	private ListView listViewPrefectures;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.activity_settings);
        
        this.requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        this.hmApi = new HmApi(this.getApplicationContext(), this.requestQueue);
        
        this.listViewPrefectures = (ListView)this.findViewById(R.id.listViewPrefectures);
        assert this.listViewPrefectures != null;
        
        // 設定クラスを準備
     	this.settings = new CommonSettings(this.getApplicationContext());
    }
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
				
		// 都道府県を読み込む
		this.loadPrefectures();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		
		
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		
		this.requestQueue = null;
		this.hmApi = null;
		
		// 設定クラスを解放
		this.settings = null;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        
        super.onCreateOptionsMenu(menu);
        return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "onOptionsItemSelected");
    	
    	return super.onOptionsItemSelected(item);
	}
	
	public void okButtonClicked(View view) {
		Log.d(TAG, "okButtonClicked");
		assert view instanceof Button;
		
		// 現在選択されている項目を取得
		int checkedPosition = this.listViewPrefectures.getCheckedItemPosition();
		Log.v(TAG, "onButtonClicked>getCheckedItemPosition = " + checkedPosition);
		if (checkedPosition == ListView.INVALID_POSITION) { return; }
		
		// 選択されている都道府県名を取得
		String checkedPrefectureName =
				(String)this.listViewPrefectures.getItemAtPosition(checkedPosition);
		assert checkedPrefectureName != null;
		
		// 都道府県のデータを取得
		Optional<Prefecture> prefecture =
				this.prefectures.getByName(checkedPrefectureName);
		
		// データが正常に存在する場合
		if (prefecture.isPresent()) {
			Log.i(TAG, "Prefecture.id = " + prefecture.get().getId());
			Log.i(TAG, "Prefecture.name = " + prefecture.get().getName());
			
			this.saveSettings(prefecture.get());
		}
	}
	
	public void cancelButtonClicked(View view) {
		Log.d(TAG, "cancelButtonClicked");
		assert view instanceof Button;
		
		this.cancelSettings();
	}
	
	private void setPrefectures(PrefectureCollection prefectures) {
		Log.d(TAG, "setPrefectures");
		
		this.prefectures = prefectures;
		assert prefectures != null;
		
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
				this.getApplicationContext(),
				android.R.layout.simple_list_item_single_choice,
				prefectures.getNames()
				);
		
		this.listViewPrefectures.setAdapter(adapter);
		this.listViewPrefectures.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		// 先頭を初期状態で選択
		if (adapter.getCount() > 0) {
			int prefectureId = this.settings.loadPrefectureId();
			
			// データが保存されてない場合は、最初の都道府県を選択
			if (prefectureId < 0) {
				prefectureId = prefectures.getIds()[0];
			}
			
			// 都道府県 ID の一覧を取得
			Integer[] ids = prefectures.getIds();
			
			// 一致した場合、選択
			for (int i = 0; i < ids.length; ++i) {
				if (ids[i] == prefectureId) {
					this.listViewPrefectures.setItemChecked(i, true);
					break;
				}
			}
		}
	}
	
	/**
	 * 設定を保存する
	 * @param prefecture 保存する都道府県
	 */
	private void saveSettings(Prefecture prefecture) {
		Log.d(TAG, "saveSettings");
		assert prefecture != null;
		
		// 値を保存
		this.settings.savePrefectureId(prefecture.getId());
		
		// メッセージを表示
		Toast.makeText(
				this.getApplicationContext(),
				R.string.setting_save_toast,
				Toast.LENGTH_SHORT
				).show();
		
		this.finish();
	}
	
	/**
	 * 設定の保存をキャンセルする
	 */
	private void cancelSettings() {
		Toast.makeText(
				this.getApplicationContext(),
				R.string.setting_cancel_toast,
				Toast.LENGTH_SHORT
				).show();
		
		this.finish();
	}
	
	private void loadPrefectures() {
		// ローディングメッセージを表示
		this.showProgressDialog(R.string.loading_msg_prefectures);
		
		// データを読み込む
		this.hmApi.getPrefectures(new HmApi.Listener<PrefectureCollection>() {
			
			@Override
			public void onSuccess(HmApi api, PrefectureCollection data) {
				Log.d(TAG, "HmApi.Listener#onSuccess");
				
				SettingsActivity.this.closeDialog();
				SettingsActivity.this.setPrefectures(data);
			}
			
			@Override
			public void onFailure() {
				Log.e(TAG, "HmApi.Listener#onFailure");
				
				SettingsActivity.this.showFinishAlertDialog(
						R.string.network_failed_title,
						R.string.network_failed_msg_prefectures
						);
			}
			
			@Override
			public void onException(Exception exception) {
				Log.e(TAG, "HmApi.Listener#onException", exception);
				
				SettingsActivity.this.showFinishAlertDialog(
						R.string.network_error_title,
						R.string.network_error_msg_prefectures
						);
			}
		});
	}
}
