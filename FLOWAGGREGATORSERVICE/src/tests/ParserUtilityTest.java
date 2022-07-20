package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import utility.ParserUtility;

public class ParserUtilityTest {
	@Test
	public void GetJsonPayloadWithJsonArray() {
		HttpServletRequest request = mock(HttpServletRequest.class); 
		String post_data_json = "[{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 2},"+
				"{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 1}]";
		try {
			when(request.getReader()).thenReturn(new BufferedReader(new StringReader(post_data_json)));
			when(request.getContentType()).thenReturn("application/json");
			String buffered = ParserUtility.GetJsonPayload(request).toString();
			assertEquals(buffered, post_data_json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void GetJsonPayloadWithNonJsonArray() {
		HttpServletRequest request = mock(HttpServletRequest.class); 
		String post_data_json = "trial----trial";
		try {
			when(request.getReader()).thenReturn(new BufferedReader(new StringReader(post_data_json)));
			when(request.getContentType()).thenReturn("application/json");
			assertEquals(ParserUtility.GetJsonPayload(request).toString(), post_data_json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}