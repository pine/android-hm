package net.pinemz.hm.gui;

import net.pinemz.hm.R;
import net.pinemz.hm.api.MenuCollection;
import net.pinemz.hm.api.MenuList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader.ImageCache;

class MenuTabHelper {
	public static final String TAG = MenuTabHelper.class.getName();
	
	private LayoutInflater inflater;
	private ViewGroup parentView;
    private int layoutId;
    private ImageCache cache;
    
    private MenuCollection menus;
    private int selectedTabIndex;
    
	public MenuTabHelper(
			Context context,
			ViewGroup parentView,
			int layoutId,
			ImageCache cache
			)
	{
		super();
		
		Log.d(TAG, "MenuAdapter");
		
		Validate.notNull(context);
		
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.parentView = parentView;
		this.layoutId = layoutId;
		this.cache = cache;
		
		this.menus = null;
		this.selectedTabIndex = -1;
		
	}
	
	public int size() {
		if (this.menus != null &&
				this.selectedTabIndex >= 0 &&
				this.selectedTabIndex < this.menus.size())
		{
			return this.menus.get(this.selectedTabIndex).size();
		}
		
		return 0;
	}
	public Object getItem(int position) {
		if (this.size() > 0) {
			return this.menus.get(this.selectedTabIndex).get(position);
		}
		
		return null;
	}

	private View createView(int position) {
		View view = this.inflater.inflate(this.layoutId, null);
		TextView textViewListName =
				(TextView)view.findViewById(R.id.textViewListName);
		GridView gridViewMenuItems =
				(GridView)view.findViewById(R.id.gridViewMenuItems);
		
		MenuListAdapter adapter = new MenuListAdapter(
				this.inflater.getContext().getApplicationContext(),
				R.layout.item_menu_item,
				this.cache
				);
		
		gridViewMenuItems.setAdapter(adapter);
		
		MenuList tab = (MenuList)this.getItem(position);
		String listName = tab.getListName();
		
		if (StringUtils.isEmpty(listName)) {
			textViewListName.setVisibility(View.GONE);
		}
		
		else {
			textViewListName.setText(tab.getListName());
		}
		
		adapter.setMenus(menus);
		adapter.setSelectedTabIndex(this.selectedTabIndex);
		adapter.setSelectedListIndex(position);
		adapter.update();
		
		return view;
		
		/*
		MenuListViewHolder holder;
		
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, parent, false);
			
			holder = new MenuListViewHolder();
			holder.textViewListName = (TextView)convertView.findViewById(R.id.textViewListName);
			holder.gridViewMenuItems = (GridView)convertView.findViewById(R.id.gridViewMenuItems);
			holder.menuListAdapter = new MenuListAdapter(
					this.inflater.getContext().getApplicationContext(),
					R.layout.item_menu_item
					);
			
			holder.gridViewMenuItems.setAdapter(holder.menuListAdapter);
			convertView.setTag(holder);
		}
		
		else {
			holder = (MenuListViewHolder)convertView.getTag();
		}
		
		MenuList tab = (MenuList)this.getItem(position);
		holder.textViewListName.setText(tab.getListName());
		
		holder.menuListAdapter.setMenus(menus);
		holder.menuListAdapter.setSelectedTabIndex(this.selectedTabIndex);
		holder.menuListAdapter.setSelectedListIndex(position);
		
		return convertView;*/
	}
	
	public void update() {
		this.parentView.removeAllViews();
		
		for (int i = 0; i < this.size(); ++i) {
			this.parentView.addView(this.createView(i));
		}
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
}
