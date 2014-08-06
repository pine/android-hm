package net.pinemz.hm.storage;

import org.apache.commons.lang3.Validate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class CommonSettings {
	public static final String TAG = "CommonSettings";
	
	private final String PREFERENCES_NAME = "commons";
	private final int PREFERENCES_MODE = Context.MODE_PRIVATE;
	private final String PREFECTURE_ID_KEY = "prefectureId";
	
	private Context context;
	
	/**
	 * 設定クラスを構築する
	 * @param context コンテキスト
	 */
	public CommonSettings(Context context) {
		Validate.notNull(context);
		
		this.context = context;
	}
	
	/**
	 * 保存された都道府県 ID を取得する
	 * @return 保存された値がない場合は -1 を返す
	 */
	public int loadPrefectureId() {
		return this.getPreferences().getInt(PREFECTURE_ID_KEY, -1);
	}
	
	/**
	 * 都道府県 ID を保存する
	 * @param prefectureId 保存する ID
	 */
	public void savePrefectureId(int prefectureId) {
		Editor editor = this.getPreferences().edit();
		editor.putInt(PREFECTURE_ID_KEY, prefectureId);
		editor.commit();
	}
	
	private SharedPreferences getPreferences() {
		return this.context.getSharedPreferences(
				PREFERENCES_NAME, PREFERENCES_MODE);
	}
}
