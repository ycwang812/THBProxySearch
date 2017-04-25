package tw.gov.thb.search.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class AutoSearch extends HttpServlet {

	private static final long serialVersionUID = 5214488573492641685L;
	
	private String host = "http://10.122.100.52/";
	private String site = "default_collection";
	private String client = "default_frontend";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String query = new String(request.getParameter("query").getBytes("iso8859-1"), "utf-8");		
		
		if (query.trim().isEmpty()) {
			return;
		}
		
		JSONObject object = new JSONObject();
		
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(host + "search?q=" + URLEncoder.encode(query, "utf-8") + "&site=" + site + "&client=" + client + "&output=xml&filter=0");
			
			List<Node> tList = document.selectNodes("//T");
			List<Node> uList = document.selectNodes("//U");
			
			if (tList.size() == 0) {
				return;
			}
			
			object.put("query", query);
			
			JSONArray array = new JSONArray();
			
			for (int i = 0; i < tList.size() && i < 10; i++) {
				Node tNode = tList.get(i);
				Node uNode = uList.get(i);				
				
				JSONObject result = new JSONObject();
				
				result.put("name", tNode.getText());
				result.put("type", "People");
				result.put("content", "<h1>" + tNode.getText() + "</h1><br /><span class='type'></span>");
				result.put("moreDetailsUrl", uNode.getText().startsWith("googledb://") ? uNode.getText().replaceFirst("googledb://", host + "db/") : uNode.getText());
				result.put("style", "normal");
				
				array.put(result);
			}
			
			object.put("results", array);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("searchAsYouType.handleAjaxResponse(" + object.toString() + ");");
		response.getWriter().close();
	}

}
