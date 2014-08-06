package net.pinemz.hm.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageData {
	public static final String TAG = "ImageData";
	
	private String url;
	private int width;
	private int height;
	
	public ImageData(JSONObject imageJson) throws JSONException {
		
		this.url = imageJson.getString("url");
		this.width = imageJson.getInt("width");
		this.height = imageJson.getInt("height");
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
