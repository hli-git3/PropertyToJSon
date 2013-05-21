package org.sky.util.json;

import java.util.Properties;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PropertyConverter {
	private static Pattern floatPattern = Pattern
			.compile("^[0-9]+(\\.[0-9]+)?$");
	private static Pattern intPattern = Pattern.compile("^[0-9]+$");

	/**
	 * Converts the properties object into a JSON string.
	 * 
	 * @param properties
	 *            The properties object to convert.
	 * @return A JSON string representing the object.
	 */
	public static String convertToJson(Properties properties) {
		JsonObject json = new JsonObject();
		for (Object key : properties.keySet()) {
			json.addProperty(key.toString(), properties.getProperty((String)key));

		}
		return new Gson().toJson(json);
	}
}