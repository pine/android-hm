package net.pinemz.hm.api;

import java.text.ParseException;

import net.pinemz.hm.R;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

public class HmApi {
	public static final String TAG = "HmApi";
	
	private static final String API_BASE = "http://node-hm.herokuapp.com/";
	private static final String API_INDEX = "prefectures";
	private static final String API_SHOW = "prefecture/:id";
	private static final String API_MENU = "prefecture/:prefecture_id/:menu_id";
	
	private Context context;
	private RequestQueue requestQueue;
	
	public HmApi(
			Context context,
			RequestQueue requestQueue
			)
	{
		Log.d(TAG, "HmApi");
		
		Validate.notNull(context);
		Validate.notNull(requestQueue);
		
		this.context = context;
		this.requestQueue = requestQueue;
	}
	
	/**
	 * リクエストをキューに追加する
	 * @param request 追加するリクエスト
	 */
	protected <T> void addRequest(Request<T> request) {
		Log.d(TAG, "addRequest");
		
		Validate.notNull(request);
		
		request.setRetryPolicy(new DefaultRetryPolicy(
				context.getResources().getInteger(R.integer.network_timeout),
				context.getResources().getInteger(R.integer.network_max_retries),
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
				));
		
		this.requestQueue.add(request);
		this.requestQueue.start();
	}
	
	public void getPrefectures(final HmApi.Listener<PrefectureCollection> listener){
		this.addRequest(new JsonArrayRequest(this.getIndexUrl(),
				new Response.Listener<JSONArray>() {
			        @Override
			        public void onResponse(JSONArray response){			        	
			        	try {
			        		PrefectureCollection prefectures =
			        				new PrefectureCollection(response);
			        		
			        		if(prefectures.isEmpty()){
			        			Log.d(TAG, "Response.Listener#onResponse onFailure");
			        			listener.onFailure();
			        		}
			        		
			        		else {
			        			Log.d(TAG, "Response.Listener#onResponse onSuccess");
			        			listener.onSuccess(HmApi.this, prefectures);
			        		}
			        	}
			        	
			        	catch(JSONException e){
			        		Log.e(TAG, "Response.Listener#onResponse", e);
			        		listener.onException(e);
			        	}
			        	
			        	catch(ParseException e){
			        		Log.e(TAG, "Response.Listener#onResponse", e);
			        		listener.onException(e);
			        	}
			        }
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Response.ErrorListener#onErrorResponse", error);
						listener.onException(error);
					}
				}));
	}
	
	/**
	 * メニュー一覧を取得する
	 * @param prefectureId 都道府県 ID
	 * @param listener イベントリスナ
	 */
	public void getMenus(
			int prefectureId,
			final HmApi.Listener<MenuCollection> listener)
	{
		Log.d(TAG, "getMenus");
		
		if (prefectureId < 0) {
			throw new IllegalArgumentException("prefectureId cannot be negative number");
		}
		
		this.addRequest(new JsonObjectRequest(
				Method.GET,
				this.getShowUrl(prefectureId),
				null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d(TAG, "getMenus>#onResponse");
						
						try {
							MenuCollection menus =
									new MenuCollection(response);
							
							if (menus.isEmpty()) {
								listener.onFailure();
							}
							
							else {
								listener.onSuccess(HmApi.this, menus);
							}
						}
						
						catch(JSONException e) {
							Log.d(TAG, "getMenus>#onResponse", e);
							listener.onException(e);
						}
						
						catch(ParseException e) {
							Log.d(TAG, "getMenus>#onResponse", e);
							listener.onException(e);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						listener.onException(error);
					}
				}));
	}
	
	protected String getIndexUrl(){
		return API_BASE + API_INDEX;
	}
	
	protected String getShowUrl(int id){
		String url = API_BASE + API_SHOW;
		url = url.replace(":id", Integer.toString(id));
		
		return url;
	}
	
	protected String getMenuUrl(int prefecture_id, int menu_id){
		String url = API_BASE + API_MENU;
		url = url.replace(":prefecture_id", Integer.toString(prefecture_id));
		url = url.replace(":menu_id", Integer.toString(menu_id));
		
		return url;
	}
	
	/**
	 * API 実行時のイベントリスナ
	 * @author MIZUNE Pine
	 * @param <T> API の戻り値
	 */
	public interface Listener<T> {
		void onSuccess(HmApi api, T data);
		void onFailure();
		void onException(Exception exception);
	}
}
