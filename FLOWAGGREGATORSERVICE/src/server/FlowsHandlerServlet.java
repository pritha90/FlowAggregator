package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Constants;
import model.DatabaseWriterInterface;
import model.FlowListCacheInterface;
import model.FlowLongStatsRecord;
import model.StatsCacheInterface;
import utility.FlowValidator;
import utility.ParserUtility;

public class FlowsHandlerServlet extends HttpServlet
{
	DatabaseWriterInterface db_writer;
	StatsCacheInterface stats_global_cache;
	
    public FlowsHandlerServlet(StatsCacheInterface global_cache,
    		FlowListCacheInterface flow_list_global_cache,
    		DatabaseWriterInterface db_writer) {
			this.db_writer = db_writer;
			this.stats_global_cache = global_cache;
    }
    
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {		
		try {
			StringBuffer json_buffer = ParserUtility.GetJsonPayload(request);
			JSONArray obj = new JSONArray(json_buffer.toString());
			this.db_writer.Write(obj);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String hour = request.getParameter(Constants.HOUR);
		JSONArray json_array = new JSONArray();
		this.stats_global_cache.InitConnection();
		Set<String> cached_set = (Set<String>) this.stats_global_cache.getMembers(hour);
		for (String flow_str : cached_set) {
			try {
			JSONObject obj = FlowValidator.GetJsonFromKeysStr(flow_str);
			FlowLongStatsRecord record = this.stats_global_cache.get(flow_str);
			obj.put(Constants.BYTES_TX, record.getTxCount());
			obj.put(Constants.BYTES_RX, record.getRxCount());
			json_array.put(obj);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}		
		}
		
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(json_array);
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}
}
