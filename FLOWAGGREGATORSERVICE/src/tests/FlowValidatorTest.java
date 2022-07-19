package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import utility.FlowValidator;

public class FlowValidatorTest {
	@Test
	void TestIsValidFlowLogPostInput() {
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
}