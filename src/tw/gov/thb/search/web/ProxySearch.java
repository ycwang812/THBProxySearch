package tw.gov.thb.search.web;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tw.gov.thb.search.model.SearchProcess;

public class ProxySearch extends HttpServlet {

	private static final long serialVersionUID = -3182286499638470895L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		HashMap<String, String[]> params = (HashMap<String, String[]>) request.getParameterMap();

		SearchProcess search = new SearchProcess(getServletConfig().getInitParameter("searchHost"));
		search.setParamsMap(params);
		search.setCollection(getServletConfig().getInitParameter("collection"));
		search.setFilter(getServletConfig().getInitParameter("filter"));
		search.setFrontend(getServletConfig().getInitParameter("frontend"));

		ServletOutputStream out = response.getOutputStream();
		out.write(search.getSearchResults());
		out.flush();
	}
	
}
