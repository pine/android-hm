package net.pinemz.hm.api;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuItem {
	public static final String TAG = "MenuItem";
	
	private int prefectureId;
	private String prefectureName;
	private String tabName;
	private String listName;
	
	private int menuId;
	private String menuName;
	private int money;
	private ImageData image;
	
	public MenuItem(
		int prefectureId,
		String prefectureName,
		String tabName,
		String listName,
		JSONObject item
		)
		throws JSONException
	{
		// 都道府県の基本情報
		this.prefectureId = prefectureId;
		this.prefectureName = prefectureName;
		
		// タブの基本情報
		this.tabName = tabName;
		
		// リストの基本情報
		this.listName = listName;
		
		// メニュー項目の解析
		this.menuId = item.getInt("id");
		this.menuName = item.getString("name");
		this.money = item.getInt("money");
		
		// 画像の解析
		this.image = new ImageData(item.getJSONObject("image"));
	}
	
	public int getPrefectureId() {
		return this.prefectureId;
	}
	
	public String getPrefectureName() {
		return this.prefectureName;
	}
	
	public String getTabName() {
		return this.tabName;
	}
	
	public String getListName() {
		return this.listName;
	}
	
	public int getMenuId() {
		return this.menuId;
	}
	
	public String getMenuName() {
		return this.menuName;
	}
	
	public int getMoney() {
		return this.money;
	}
	
	public ImageData getImage() {
		return this.image;				
	}
}
