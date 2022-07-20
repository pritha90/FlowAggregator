package tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import model.Constants;
import model.DatabaseWriterInterface;
import model.FlowListCacheInterface;
import model.FlowLongStatsRecord;
import model.StatsCacheInterface;
import server.FlowsHandlerServlet;

//@RunWith(MockitoJUnitRunner.class)
public class FlowsHandlerServletTest extends Mockito {
	FlowsHandlerServlet servlet;

	@Mock
	DatabaseWriterInterface db_writer;
	StatsCacheInterface global_cache;
	HttpServletRequest request;
	HttpServletResponse response;
	
	public static final String kFlow1 = "foo;bar;vpc-1;1";
	public static final String kFlow2 = "foo;bar;vpc-2;1";
	
	@BeforeEach
    public void SetUp() throws Exception {
        request = mock(HttpServletRequest.class);       
        response = mock(HttpServletResponse.class); 
        db_writer = mock(DatabaseWriterInterface.class);
        global_cache = mock(StatsCacheInterface.class);
        servlet = spy(new FlowsHandlerServlet(global_cache,
        		null,db_writer));
    }
	
	@Test
    public void DoPostWithGoodInputReturnsOk() {
		String post_data_json = 
				"[{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 2}]";
		try {
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(post_data_json)));
		when(request.getContentType()).thenReturn("application/json");
		
		servlet.doPost(request, response);
		verify(db_writer, times(1)).Write(any(JSONArray.class));
		verify(response).setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void DoPostWithGoodMultipleObjInputReturnsOk() {
		String post_data_json = "[{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 2},"+
				"{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-1\", \"bytes_tx\":300, \"bytes_rx\": 800, \"hour\": 1}]";
		try {
			when(request.getReader()).thenReturn(new BufferedReader(new StringReader(post_data_json)));
			when(request.getContentType()).thenReturn("application/json");
			
			servlet.doPost(request, response);
			verify(db_writer, times(1)).Write(any(JSONArray.class));
			verify(response).setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void DoPostWithBadInputReturnsError() {
		String post_data_json = 
				"[{\"src_app\": \"foo\", \"dest_app\": \"bar\"\"bytes_rx\": 800, \"hour\": 2}]";
		try {
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(post_data_json)));
		when(request.getContentType()).thenReturn("application/json");
		servlet.doPost(request, response);
		verify(db_writer, times(0)).Write(any(JSONArray.class));
		verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void DoGetReturnsJsonAndOk() {
		String data_json = "[{\"src_app\":\"foo\",\"dest_app\":\"bar\""
				+ ",\"hour\":\"1\",\"bytes_tx\":1000,\"vpc_id\":\"vpc-1\",\"bytes_rx\":300}]";
		try {
			when(request.getParameter(Constants.HOUR)).thenReturn("1");
			
			Set<String> ret_val = new HashSet<String>();
			ret_val.add(kFlow1);
			when(global_cache.getMembers("1")).thenReturn(ret_val);
			when(global_cache.get(kFlow1)).thenReturn(new FlowLongStatsRecord(1000L,300L));
			StringWriter str_writer = new StringWriter();
			PrintWriter writer = new PrintWriter(str_writer);
			when(response.getWriter()).thenReturn(writer);
			servlet.doGet(request, response);
			verify(db_writer, times(0)).Write(any(JSONArray.class));
			verify(response).setStatus(HttpServletResponse.SC_OK);
			verify(response).setContentType("application/json");
			writer.flush();
			assertTrue(str_writer.toString().contains(data_json));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void DoGetCannotContructOutputReturnsError() {
		try {
			when(request.getParameter(Constants.HOUR)).thenReturn("1");
			Set<String> ret_val = new HashSet<String>();
			ret_val.add(kFlow1);
			when(global_cache.getMembers("1")).thenReturn(ret_val);
			when(global_cache.get(kFlow1)).thenReturn(new FlowLongStatsRecord(1000L,300L));
			servlet.doGet(request, response);
			verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}