package usp.pad.lumesi.smartcampussp.util;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IReader {
	public boolean isSensorEnable();
	public JSONObject inserValues(JSONObject jobj);
	public JSONObject insertFIWAREValues(JSONObject jobj);
	public JSONArray insertFIWAREValues(JSONArray jobj);
	public void printDebug();
	public void setTime(int time);
	public void stop();
}
