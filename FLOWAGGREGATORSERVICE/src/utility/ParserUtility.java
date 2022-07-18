package utility;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

public class ParserUtility {
public static StringBuffer GetJsonPayload(HttpServletRequest request) throws Exception {
	StringBuffer json_buffer = new StringBuffer();
	String content = null;
	BufferedReader reader = request.getReader();
	while ((content = reader.readLine()) != null) {
		json_buffer.append(content);
	}
	return json_buffer;
}
	
}