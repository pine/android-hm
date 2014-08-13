package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import net.pinemz.hm.api.MenuCollection;
import net.pinemz.hm.api.MenuItem;
import net.pinemz.hm.api.MenuTab;

import org.apache.commons.lang3.Validate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

public class MenuListAdapter extends BaseAdapter {
	public static final String TAG = MenuListAdapter.class.getName();
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private LayoutInflater inflater;
    private int layoutId;
    
    private MenuCollection menus;
    private int selectedTabIndex;
    
    public MenuListAdapter(
			Context context,
			int layoutId,
			ImageCache cache
			)
	{
		super();
		Log.d(TAG, TAG);
		
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		
		this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		this.imageLoader = new ImageLoader(requestQueue, cache);
	}
    
    /**
     * 個数を返す。添字が間違っており、数が特定できない場合はゼロを返す。
     */
	@Override
	public int getCount() {
		Log.d(TAG, "getCount");
		
		// タブが取得できるか
		if (this.menus != null &&
				this.selectedTabIndex >= 0 &&
				this.selectedTabIndex < this.menus.size())
		{
			MenuTab tab = this.menus.get(this.selectedTabIndex);
			
			if (tab != null)
			{
				return tab.size();
			}
		}
		
		return 0;
	}
	
	@Override
	public Object getItem(int position) {
		if (this.getCount() > 0) {
			return this.menus
					.get(this.selectedTabIndex)
					.get(position);
		}
		
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView(position = " + position + ")");
		
		MenuItemViewHolder holder;
		
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, parent, false);
			
			holder = new MenuItemViewHolder();
//			holder.textViewMenuName = (TextView)convertView.findViewById(R.id.textViewMenuName);
			holder.imageViewMenu = (ImageView)convertView.findViewById(R.id.imageViewMenu);
			convertView.setTag(holder);
			
			if (this.getCount() > 0) {
				((GridView)parent).setColumnWidth(this.getItemMaxWidth());
			}
		}
		
		else {
			holder = (MenuItemViewHolder)convertView.getTag();
		}

		MenuItem item = (MenuItem)this.getItem(position);
	
		ImageView imageViewMenu = holder.imageViewMenu;

		imageViewMenu.setImageDrawable(null);
		ImageListener listener = ImageLoader.getImageListener(
				holder.imageViewMenu, 0, 0);
		
		imageLoader.get(item.getImage().getUrl(), listener);
			
		imageViewMenu.getLayoutParams().height = item.getImage().getHeight();
		imageViewMenu.getLayoutParams().width = item.getImage().getWidth();
		
		return convertView;
	}
    

	public MenuCollection getMenus() {
		return this.menus;
	}
	
	public void setMenus(MenuCollection menus) {
		Log.d(TAG, "setMenus");
		Validate.notNull(menus);
		
		this.menus = menus;
	}
	
	public int getSelectedTabIndex() {
		return this.selectedTabIndex;
	}
	
	public void setSelectedTabIndex(int tabIndex) {
		Log.d(TAG, "setSelectedTabIndex");
		
		this.selectedTabIndex = tabIndex;
	}
	
	/**
	 * データ変更を通知し UI に反映させる
	 */
	public void update() {
		Log.d(TAG, "update()");
		
		if (this.menus != null &&
				this.selectedTabIndex >= 0 &&
				this.selectedTabIndex >= 0)
		{
			this.notifyDataSetChanged();
		}
		
		else {
			this.notifyDataSetInvalidated();
		}
	}
	
	private int getItemMaxWidth() {
		int maxWidth = 0;
		
		for (int i = 0; i < this.getCount(); ++i) {
			int width = ((MenuItem)this.getItem(i)).getImage().getWidth();
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
}
