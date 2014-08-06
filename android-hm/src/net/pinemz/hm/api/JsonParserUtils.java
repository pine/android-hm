package net.pinemz.hm.api;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

class JsonParserUtils {
	/**
	 * JSON Œ`®‚Ì“ú‚ğ‰ğÍ‚·‚é
	 * @param dateText ‰ğÍ‚·‚é“ú
	 * @return ‰ğÍŒ‹‰Ê
	 * @throws ParseException ‰ğÍ¸”s
	 */
	public static Date parseDate(String dateText) throws ParseException {
		final FastDateFormat fastDateFormat =
				FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		final String[] patterns = new String[]{ fastDateFormat.getPattern() };
		
		return DateUtils.parseDate(dateText, patterns);
	}
}
