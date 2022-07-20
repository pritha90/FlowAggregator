package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import model.FlowRecord;
import utility.FlowValidator;

public class FlowValidatorTest {
	@Test
	void IsValidFlowLogPostInput() {
		try {
			JSONObject obj = new JSONObject("{\"src_app\": \"foo\", \"dest_app\": \"bar\","
					+ " \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 2}");
			assertTrue(FlowValidator.IsValidFlowLogPostInput(obj));
			obj = new JSONObject("{\"src_app\": \"foo\", \"dest_app\": \"bar\","
					+ " \"vpc_id\": \"vpc-1\", \"bytes_rx\": 800, \"hour\": 2}");
			assertFalse(FlowValidator.IsValidFlowLogPostInput(obj));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	void GetHourParameterAsStr() {
		assertEquals(FlowValidator.GetHourParameterAsStr("foo;bar;vpc-100;100"), "100");
		assertEquals(FlowValidator.GetHourParameterAsStr("foo;bar;vpc-100;100;100"), "100");
		assertEquals(FlowValidator.GetHourParameterAsStr("foo;bar;vpc-"), "vpc-");
		assertEquals(FlowValidator.GetHourParameterAsStr("foo-"), "foo-");
		assertEquals(FlowValidator.GetHourParameterAsStr(""), "");
	}
	
	@Test
	void MakeKey() {
		JSONObject json_obj = new JSONObject();
		try {		
			json_obj.put("src_app", "spp-1");
			json_obj.put("dest_app", "dpp-1");
			json_obj.put("vpc_id", "vpc");
			json_obj.put("hour" , 1);
			json_obj.put("bytes" , 19);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			assertEquals(FlowValidator.MakeKey(json_obj), "spp-1;dpp-1;vpc;1");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void MakeKeyThrowsException() {
		JSONObject json_obj = new JSONObject();
		try {		
			json_obj.put("src_pp", "spp-1");
			json_obj.put("dest_app", "dpp-1");
			json_obj.put("vpc_id", "vpc");
			json_obj.put("hour" , 1);
			json_obj.put("bytes" , 19);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Exception e = assertThrows(JSONException.class, () -> {FlowValidator.MakeKey(json_obj);});
		assertTrue(e.getMessage().contains("JSONObject[\"src_app\"] not found."));
	}
	
	@Test
	void MakeFlowRecordThrowsException() {
		JSONObject json_obj = new JSONObject();
		try {		
			json_obj.put("src_app", "spp-1");
			json_obj.put("dest_app", "dpp-1");
			json_obj.put("vpc_id", "vpc");
			json_obj.put("hour" , 1);
			json_obj.put("bytes" , 19);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Exception e = assertThrows(Exception.class, () -> {FlowValidator.MakeFlowRecord(json_obj);});
		assertTrue(e.getMessage().contains("Not the expected Flow-log"));
	}
	
	@Test
	void MakeFlowRecordSuccess() {
		JSONObject json_obj = new JSONObject();
		try {		
			json_obj.put("src_app", "spp-1");
			json_obj.put("dest_app", "dpp-1");
			json_obj.put("vpc_id", "vpc");
			json_obj.put("hour" , 1);
			json_obj.put("bytes_tx" , 19);
			json_obj.put("bytes_rx" , 1900);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			FlowRecord record = FlowValidator.MakeFlowRecord(json_obj);
			assertEquals(record.getKey(),  "spp-1;dpp-1;vpc;1");
			assertTrue(record.getFlowStatsRecord().getTxCount() == 19);
			assertTrue(record.getFlowStatsRecord().getRxCount() == 1900);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}