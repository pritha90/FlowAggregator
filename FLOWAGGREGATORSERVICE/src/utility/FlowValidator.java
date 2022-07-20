package utility;
import org.json.JSONException;
import org.json.JSONObject;

import model.FlowRecord;
import model.FlowStatsRecord;

public class FlowValidator {
	
public static Boolean IsValidFlowLogPostInput(JSONObject json_obj) {
	if (!json_obj.has("src_app") || !json_obj.has("dest_app")
			|| !json_obj.has("vpc_id")
			|| !json_obj.has("bytes_tx")
			|| !json_obj.has("bytes_rx")
			|| !json_obj.has("hour")) {
	return false;
	}
	return true;
}

public static FlowRecord MakeFlowRecord(JSONObject json_obj) throws Exception {
	if (!IsValidFlowLogPostInput(json_obj)) {
		throw new Exception("Malformed JSON input. Not the expected Flow-log");
	}
    String key = MakeKey(json_obj);
    FlowRecord record = new FlowRecord(key, 
    		new FlowStatsRecord(json_obj.getInt("bytes_tx"),json_obj.getInt("bytes_rx")));
    return record;
}

public static String MakeKey(JSONObject json_obj) throws JSONException {
	return json_obj.getString("src_app") + ";"
			+ json_obj.getString("dest_app") + ";" 
			+ json_obj.getString("vpc_id") + ";"
			+json_obj.getString("hour");
}

public static Integer GetHourParameter(String input) {
	String [] result = input.split(";");
	if (result.length > 0) {
		return Integer.parseInt(result[result.length - 1]);
	}
	return 0;
}

public static String GetHourParameterAsStr(String input) {
	String [] result = input.split(";");
	if (result.length > 0) {
		return result[result.length - 1];
	}
	return "0";
}

public static String[] SplitKeys(String input) {
	return input.split(";");
}

public static JSONObject GetJsonFromKeysStr(String input) throws Exception {
	String [] result = input.split(";");
	JSONObject json_obj = new JSONObject();
	
	if (result.length != 4) {
		throw new Exception("Invalid string received: " + input);
	}
	json_obj.put("src_app", result[0]);
	json_obj.put("dest_app", result[1]);
	json_obj.put("vpc_id", result[2]);
	json_obj.put("hour" ,result[3]);

	return json_obj;	
}


}