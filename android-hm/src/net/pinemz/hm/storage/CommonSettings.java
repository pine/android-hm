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
	 * �ݒ�N���X���\�z����
	 * @param context �R���e�L�X�g
	 */
	public CommonSettings(Context context) {
		Validate.notNull(context);
		
		this.context = context;
	}
	
	/**
	 * �ۑ����ꂽ�s���{�� ID ���擾����
	 * @return �ۑ����ꂽ�l���Ȃ��ꍇ�� -1 ��Ԃ�
	 */
	public int loadPrefectureId() {
		return this.getPreferences().getInt(PREFECTURE_ID_KEY, -1);
	}
	
	/**
	 * �s���{�� ID ��ۑ�����
	 * @param prefectureId �ۑ����� ID
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
