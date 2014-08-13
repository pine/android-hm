package net.pinemz.hm.api;

import java.text.ParseException;
import java.util.Date;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * “s“¹•{Œ§‚ð•\‚·
 * @author MIZUNE Pine
 *
 */
public class Prefecture {
	public static String TAG = "Prefecture";
	
	private int id;
	private String name;
	private Date updatedAt;
	
	public Prefecture(JSONObject obj) throws JSONException, ParseException {
		this.id = obj.getInt("id");
		this.name = obj.getString("name");
		this.updatedAt = JsonParserUtils.parseDate(obj.getString("updatedAt"));
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Date getUpdatedAt(){
		return this.updatedAt;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
