package net.pinemz.hm.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ForwardingList;

public class MenuTab extends ForwardingList<MenuList> {
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
		// �s���{���̊�{���
		this.prefectureId = prefectureId;
		this.prefectureName = prefectureName;
		
		// �^�u�̊�{���
		this.tabName = tabJson.getString("tabName");
		
		// ���X�g�̉��
		JSONArray lists = tabJson.getJSONArray("lists");
		int length = lists.length();
		
		for (int i = 0; i < length; ++i) {
			JSONObject listJson = lists.getJSONObject(i);
			MenuList list = new MenuList(
					this.prefectureId,
					this.prefectureName,
					this.tabName,
					listJson
					);
			
			this.add(list);
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
	 * ForwardingList �̎���
	 */
	private List<MenuList> delegate = new ArrayList<>();
	
	/**
	 * ForwardingList �̎���
	 */
	@Override
	protected List<MenuList> delegate() {
		return this.delegate;
	}
	
}
