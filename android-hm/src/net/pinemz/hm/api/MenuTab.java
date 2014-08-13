package net.pinemz.hm.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ForwardingList;

public class MenuTab extends ForwardingList<MenuItem> {
	public static final String TAG = "MenuTab";
	
	private int prefectureId;
	private String prefectureName;
	private String tabName;
	
	public MenuTab(
			int prefectureId,
			String prefectureName,
			JSONObject tabJson)
		throws JSONException
	{
		// 都道府県の基本情報
		this.prefectureId = prefectureId;
		this.prefectureName = prefectureName;
		
		// タブの基本情報
		this.tabName = tabJson.getString("tabName");
		
		// リストの解析
		JSONArray lists = tabJson.getJSONArray("lists");
		int listLength = lists.length();
		
		for (int i = 0; i < listLength; ++i) {
			JSONObject listJson = lists.getJSONObject(i);
			
			String listName = listJson.getString("listName");
			JSONArray itemsJson = listJson.getJSONArray("items");
			int itemsLength = itemsJson.length();
			
			for (int j = 0; j < itemsLength; ++j) {
				JSONObject itemJson = itemsJson.getJSONObject(j);
				MenuItem item = new MenuItem(
						this.prefectureId,
						this.prefectureName,
						this.tabName,
						listName,
						itemJson
						);
				
				this.add(item);
			}
		}
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

	/**
	 * ForwardingList の実装
	 */
	private List<MenuItem> delegate = new ArrayList<>();
	
	/**
	 * ForwardingList の実装
	 */
	@Override
	protected List<MenuItem> delegate() {
		return this.delegate;
	}
	
}
