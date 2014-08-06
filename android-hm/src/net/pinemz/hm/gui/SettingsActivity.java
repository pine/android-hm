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
        
        // �ݒ�N���X������
     	this.settings = new CommonSettings(this.getApplicationContext());
    }
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
				
		// �s���{����ǂݍ���
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
		
		// �ݒ�N���X�����
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
		
		// ���ݑI������Ă��鍀�ڂ��擾
		int checkedPosition = this.listViewPrefectures.getCheckedItemPosition();
		Log.v(TAG, "onButtonClicked>getCheckedItemPosition = " + checkedPosition);
		if (checkedPosition == ListView.INVALID_POSITION) { return; }
		
		// �I������Ă���s���{�������擾
		String checkedPrefectureName =
				(String)this.listViewPrefectures.getItemAtPosition(checkedPosition);
		assert checkedPrefectureName != null;
		
		// �s���{���̃f�[�^���擾
		Optional<Prefecture> prefecture =
				this.prefectures.getByName(checkedPrefectureName);
		
		// �f�[�^������ɑ��݂���ꍇ
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
		
		// �擪��������ԂőI��
		if (adapter.getCount() > 0) {
			int prefectureId = this.settings.loadPrefectureId();
			
			// �f�[�^���ۑ�����ĂȂ��ꍇ�́A�ŏ��̓s���{����I��
			if (prefectureId < 0) {
				prefectureId = prefectures.getIds()[0];
			}
			
			// �s���{�� ID �̈ꗗ���擾
			Integer[] ids = prefectures.getIds();
			
			// ��v�����ꍇ�A�I��
			for (int i = 0; i < ids.length; ++i) {
				if (ids[i] == prefectureId) {
					this.listViewPrefectures.setItemChecked(i, true);
					break;
				}
			}
		}
	}
	
	/**
	 * �ݒ��ۑ�����
	 * @param prefecture �ۑ�����s���{��
	 */
	private void saveSettings(Prefecture prefecture) {
		Log.d(TAG, "saveSettings");
		assert prefecture != null;
		
		// �l��ۑ�
		this.settings.savePrefectureId(prefecture.getId());
		
		// ���b�Z�[�W��\��
		Toast.makeText(
				this.getApplicationContext(),
				R.string.setting_save_toast,
				Toast.LENGTH_SHORT
				).show();
		
		this.finish();
	}
	
	/**
	 * �ݒ�̕ۑ����L�����Z������
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
		// ���[�f�B���O���b�Z�[�W��\��
		this.showProgressDialog(R.string.loading_msg_prefectures);
		
		// �f�[�^��ǂݍ���
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
