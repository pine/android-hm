package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import net.pinemz.hm.api.HmApi;
import net.pinemz.hm.api.MenuCollection;
import net.pinemz.hm.api.MenuTab;
import net.pinemz.hm.api.MenuItem;
import net.pinemz.hm.storage.CommonSettings;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

/**
 * メインのアクティビティ
 * @author MIZUNE Pine
 *
 */
public class MainActivity
	extends BasicActivity
	implements TabListener, AdapterView.OnItemClickListener
{
	public final String TAG = "MainActivity";
	
	private RequestQueue requestQueue;
	private HmApi hmApi;
	private CommonSettings settings;
	private int prefectureId;
	private MenuCollection menus;
	private BitmapCache cache;
	
	private TextView textViewTabName;
	private GridView gridViewMenuItems;
	private int gridViewMenuItemsFirstPosition;
	private MenuListAdapter menuAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
    	super.onCreate(savedInstanceState);
        
    	final ActionBar actionBar = this.getActionBar();
    	
        setContentView(R.layout.activity_main);
        
        // アクションバーのモードをタブに変更する
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        this.textViewTabName = (TextView)this.findViewById(R.id.textViewTabName);
        this.gridViewMenuItems = (GridView)this.findViewById(R.id.gridViewMenuItems);
        
        this.gridViewMenuItems.setOnItemClickListener(this);
        
        // 画像キャッシュ
        this.cache = new BitmapCache();
        this.menuAdapter = new MenuListAdapter(
        		this.getApplicationContext(),
        		R.layout.item_menu_item,
        		this.cache
        		);
        
        // アダプターを設定
        this.gridViewMenuItems.setAdapter(this.menuAdapter);
        
        // スクロール開始位置を保存
        this.gridViewMenuItemsFirstPosition =
        		this.gridViewMenuItems.getFirstVisiblePosition();
        
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
    	
    	// 画像キャッシュをクリア
    	this.cache.clear();
    	this.cache = null;
    	
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
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
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
    	
    	this.gridViewMenuItems.smoothScrollToPosition(this.gridViewMenuItemsFirstPosition);
    	
    	if (this.menus != null && position < this.menus.size()) {
    		this.textViewTabName.setText(menus.get(position).getTabName());
    	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    

    /**
     * メニューを選択したときの処理
     */
	@Override
	public void onItemClick(
			AdapterView<?> parent,
			View view,
			int position,
			long id) 
	{
		Log.d(TAG, "onItemClick");
		
		// 選択されたものを取得
		MenuItem item = (MenuItem)this.menuAdapter.getItem(position);
		if (item == null) { return; }
		
		// メニューを表示するダイアログを生成
		Log.d(TAG, "position = " + position + ", name = " + item.getMenuName());
		Dialog d = this.createMenuItemDialog(item);
		
		this.showDialog(d);
	}
	
    /**
     * 設定メニューが押されたときの処理
     */
    public void menuSettingsClicked(android.view.MenuItem item) {
    	Log.d(TAG, "menuSettingsClicked");
    	assert item != null;
    	
    	this.startSettingsActivity();
    }
    
    /**
     * メニューを読み込む
     */
    private void loadMenuts() {
    	// 既にデータが存在する場合
    	if (this.menus != null &&
    			this.menus.getPrefectureId() == this.prefectureId) {
    		this.setMenus(this.menus);
    		return;
    	}
    	
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
    
    /**
     * ロードしたメニューを反映させる
     * @param menus メニュー情報
     */
    private void setMenus(MenuCollection menus) {
    	this.menus = menus;
    	this.menuAdapter.setMenus(menus);
    	
    	ActionBar actionBar = this.getActionBar();
    	
    	// タブを追加する
    	actionBar.removeAllTabs();
    	
    	for (int i = 0; i < this.menus.size(); ++i) {
    		MenuTab menuTab = this.menus.get(i);
    		Tab appTab = actionBar.newTab()
    				.setText(menuTab.getTabName())
    				.setTabListener(this);
    		
    		actionBar.addTab(appTab);
    	}
    	
    	if (menus.size() > 0) {
    		// はじめのタブを選択
    		this.menuAdapter.setSelectedTabIndex(0);
    		this.menuAdapter.update();
    	}
    }
    
	/**
	 * メニューを表示するダイアログを生成する
	 * @param item 表示するメニュー
	 * @return 生成したダイアログ
	 */
	private Dialog createMenuItemDialog(MenuItem item) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.menu_dialog, null);
		
		// メニュー名
		TextView menuName =
				(TextView)dialogView.findViewById(R.id.textViewDialogMenuName);
		menuName.setText(item.getMenuName());
		
		// 金額
		TextView itemMoney =
				(TextView)dialogView.findViewById(R.id.textViewMenuItemMoney);
		itemMoney.setText(Integer.toString(item.getMoney()));
		
		// 画像を設定
		NetworkImageView imageView =
				(NetworkImageView)dialogView.findViewById(R.id.imageViewItemImage);
		imageView.setImageUrl(
				item.getImage().getUrl(),
				new ImageLoader(this.requestQueue, this.cache)
				);
		imageView.getLayoutParams().width = item.getImage().getWidth();
		imageView.getLayoutParams().height = item.getImage().getHeight();
		
		// ダイアログを生成
		Dialog d = new AlertDialog.Builder(this)
			.setTitle(R.string.menu_info_title)
			.setView(dialogView)
			.setPositiveButton(R.string.close_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.closeDialog();
				}
			})
			.create();
	
		return d;
	}
}
