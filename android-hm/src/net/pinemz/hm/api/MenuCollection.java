package net.pinemz.hm.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.collect.ForwardingList;

public class MenuCollection
	extends ForwardingList<MenuTab>
{
	public static final String TAG = "MenuCollection";

	private int prefectureId;
	private String prefectureName;
	private Date updatedAt;
	
	/**
	 * JSON �̉�͏���
	 * @param menuJson ��͑Ώۂ� JSON �f�[�^
	 * @throws JSONException �\����͎��s
	 * @throws ParseException �\����͎��s
	 */
	public MenuCollection(JSONObject menuJson)
			throws JSONException, ParseException
	{
		Log.d(TAG, "MenuCollection");
		
		// �s���{�������擾
		this.prefectureId = menuJson.getInt("id");
		this.prefectureName = menuJson.getString("name");
		this.updatedAt = JsonParserUtils.parseDate(menuJson.getString("updatedAt"));
		
		// ���j���[�̃^�u�ꗗ���擾
		JSONArray menuTabs = menuJson.getJSONArray("menu");
		int length = menuTabs.length();
		
		// �^�u�����
		for(int i = 0; i < length; ++i) {
			JSONObject tabJson = menuTabs.getJSONObject(i);
			MenuTab tab = new MenuTab(
					this.prefectureId,
					this.prefectureName,
					tabJson
					);
			
			this.add(tab);
		}
	}
	
	public int getTabCount() {
		return this.size();
	}
	
	public int getPrefectureId() {
		return this.prefectureId;
	}
	
	public String getPrefectureName() {
		return this.prefectureName;
	}
	
	public Date getUpdatedAt(){
		return this.updatedAt;
	}
	
	/**
	 * FowardingList �̎���
	 */
	private List<MenuTab> delegate = new ArrayList<>();
	
	/**
	 * FowardingList �̎���
	 */
	@Override
	protected List<MenuTab> delegate() {
		return this.delegate;
	}
}
