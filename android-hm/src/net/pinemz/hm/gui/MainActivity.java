package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import net.pinemz.hm.api.HmApi;
import net.pinemz.hm.api.MenuCollection;
import net.pinemz.hm.api.MenuTab;
import net.pinemz.hm.storage.CommonSettings;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class MainActivity
	extends BasicActivity
	implements TabListener
{
	public final String TAG = "MainActivity";
	
	private RequestQueue requestQueue;
	private HmApi hmApi;
	private CommonSettings settings;
	private int prefectureId;
	private MenuCollection menus;
	private ImageCache cache;
	
	private TextView textViewTabName;
	private MenuTabHelper menuAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
    	super.onCreate(savedInstanceState);
        
    	final ActionBar actionBar = this.getActionBar();
    	
        setContentView(R.layout.activity_main);
        
        // アクションバーのモードをタブに変更する
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        this.cache = new BitmapCache();
        this.menuAdapter = new MenuTabHelper(
        		this.getApplicationContext(),
//        		(ViewGroup)this.findViewById(R.id.linearLayoutMenuLists),
        		(ViewGroup)this.findViewById(R.id.viewFlipper),
        		R.layout.item_menu_list,
        		this.cache
        		);
        
        this.textViewTabName = (TextView)this.findViewById(R.id.textViewTabName);
        
        // API 関係
    	this.requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        this.hmApi = new HmApi(this.getApplicationContext(), this.requestQueue);
        
    }
    
    /**
     * データの読み込み処理
     */
    @Override
    protected void onResume() {
    	Log.d(TAG, "onResume");
    	super.onResume();
    	
    	// 全般設定
    	this.settings = new CommonSettings(this.getApplicationContext());
    	this.prefectureId = this.settings.loadPrefectureId();
    	
    	assert this.settings != null;
    	Log.i(TAG, "PrefectureId = " + this.prefectureId);
    	
    	// 都道府県 ID が設定されていない場合
    	if (this.prefectureId < 0) {
    		this.startSettingsActivity();
    		return;
    	}
    	
    	// メニューをロードする
    	this.loadMenuts();
    }
    
    /**
     * 開放処理
     */
    @Override
    protected void onPause() {
    	Log.d(TAG, "onPause");
    	super.onPause();
    	
    	// 設定を開放
    	this.settings = null;
    }
    
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "onDestroy");
    	super.onDestroy();
    	
    	this.menuAdapter = null;
    	
    	// API 関係を開放
    	this.hmApi = null;
    	this.requestQueue = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "onOptionsItemSelected");
    	
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	Log.d(TAG, "onTabSelected");
    	
    	int position = tab.getPosition();
    	this.menuAdapter.setSelectedTabIndex(position);
    	this.menuAdapter.update();
    	
    	if (this.menus != null && position < this.menus.size()) {
    		this.textViewTabName.setText(menus.get(position).getTabName());
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    /**
     * 設定メニューが押されたときの処理
     */
    public void menuSettingsClicked(MenuItem item) {
    	Log.d(TAG, "menuSettingsClicked");
    	assert item != null;
    	
    	this.startSettingsActivity();
    }
    
    /**
     * メニューを読み込む
     */
    private void loadMenuts(){
    	// ローディングダイアログを表示
    	this.showProgressDialog(R.string.loading_msg_menus);
    	
    	// API を用いてメニューを取得
    	this.hmApi.getMenus(this.prefectureId, new HmApi.Listener<MenuCollection>() {

			@Override
			public void onSuccess(HmApi api, MenuCollection data) {
				Log.d(TAG, "HmApi.Listener#onSuccess");
				
				MainActivity.this.closeDialog();
				MainActivity.this.setMenus(data);
			}

			@Override
			public void onFailure() {
				Log.e(TAG, "HmApi.Listener#onFailure");
				
				MainActivity.this.showFinishAlertDialog(
						R.string.network_failed_title,
						R.string.network_failed_msg_menus
						);
			}

			@Override
			public void onException(Exception exception) {
				Log.e(TAG, "HmApi.Listener#onException", exception);
				
				MainActivity.this.showFinishAlertDialog(
						R.string.network_error_title,
						R.string.network_error_msg_menus
						);
			}
    		
		});
    }
    
    private void setMenus(MenuCollection menus) {
    	this.menus = menus;
    	this.menuAdapter.setMenus(menus);
    	
    	ActionBar actionBar = this.getActionBar();
    	
    	// タブを追加する
    	actionBar.removeAllTabs();
    	
    	for (MenuTab tab: menus) {
    		actionBar.addTab(actionBar.newTab().setText(tab.getTabName()).setTabListener(this));
    	}
    	
    	if (menus.size() > 0) {
    		// はじめのタブを選択
    		this.menuAdapter.setSelectedTabIndex(0);
    		this.menuAdapter.update();
    		actionBar.selectTab(actionBar.getTabAt(0));
    	}
    }
}
