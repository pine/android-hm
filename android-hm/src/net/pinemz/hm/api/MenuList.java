package net.pinemz.hm.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ForwardingList;

/**
 * ���j���[���X�g��\��
 * @author MIZUNE Pine
 */
public class MenuList extends ForwardingList<MenuItem> {
	public static final String TAG = "MenuList";
	
	private int prefectureId;
	private String prefectureName;
	private String tabName;
	private String listName;
	
	public MenuList(
			int prefectureId,
			String prefectureName,
			String tabName,
			JSONObject listJson
			)
			throws JSONException
	{
		// �s���{���̊�{���
		this.prefectureId = prefectureId;
		this.prefectureName = prefectureName;
		
		// �^�u�̊�{���
		this.tabName = tabName;
		
		// ���X�g�̊�{���
		if (listJson.isNull("listName")) {
			this.listName = null;
		}
		
		else {
			this.listName = listJson.getString("listName");
		}
		
		// ���j���[�̉��
		JSONArray itemsJson = listJson.getJSONArray("items");
		int length = itemsJson.length();
		
		for (int i = 0; i < length; ++i) {
			JSONObject itemJson = itemsJson.getJSONObject(i);
			MenuItem item = new MenuItem(
					this.prefectureId,
					this.prefectureName,
					this.tabName,
					this.listName,
					itemJson
					);
			
			this.add(item);
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
	
	public String getListName() {
		return this.listName;
	}
	
	/**
	 * FowardingList �̎���
	 */
	private List<MenuItem> delegate = new ArrayList<>();
	
	/**
	 * FowardingList �̎���
	 */
	@Override
	protected List<MenuItem> delegate() {
		return this.delegate;
	}
}
