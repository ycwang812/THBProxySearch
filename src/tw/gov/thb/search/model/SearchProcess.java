package tw.gov.thb.search.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchProcess {

	private static Logger logger = LoggerFactory.getLogger("file");

	private String searchHost = "";
	private HashMap<String, String[]> paramsMap = null;
	private String frontend = "website_frontend";
	private String filter = "0";
	private String collection = "website_collection";
	
	public SearchProcess(String searchHost) {
		this.searchHost = searchHost;
	}

	public void setParamsMap(HashMap<String, String[]> paramsMap) {
		this.paramsMap = paramsMap;
	}
	
	public void setFrontend(String frontend){
		this.frontend = frontend;
	}
	
	public void setFilter(String filter){
		this.filter = filter;
	}
	
	public void setCollection(String collection){
		this.collection = collection;
	}
	
	public byte[] getSearchResults() {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;

		try {
			int byteSize = 4096;
			int count = -1;
			byte[] byteArray = new byte[byteSize];
			String searchURL = this.searchHost + getRequestParames();

			logger.info("searchURL:" + searchURL);
		 	
			URL url = new URL(searchURL);
			bis = new BufferedInputStream(url.openStream());
			baos = new ByteArrayOutputStream();

			while ((count = bis.read(byteArray, 0, byteSize)) != -1) {
				baos.write(byteArray, 0, count);
			}

			return baos.toByteArray();
		} catch (MalformedURLException murle) {
			murle.printStackTrace();
			logger.error(murle.getMessage());
			return murle.getMessage().getBytes();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
			logger.error(uee.getMessage());
			return uee.getMessage().getBytes();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.error(ioe.getMessage());
			return ioe.getMessage().getBytes();
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (baos != null)
					baos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				logger.error(ioe.getMessage());
			}
		}
	}
	 
	private String getRequestParames() throws UnsupportedEncodingException {
		Set<String> keyset = this.paramsMap.keySet();
		StringBuffer queryString = new StringBuffer();
		
		for (String key : keyset) {
			queryString.append(key + ":" + paramsMap.get(key)[0] + "&");
		}
		
		logger.debug("querystring:" + queryString.toString());

		ArrayList<String> paramsList = new ArrayList<String>();

		/*
		 * query term, need process for string, value is uncertain
		 */
		if (this.paramsMap.containsKey(Params.q.toString())) {
			String value = this.paramsMap.get(Params.q.toString())[0];
			
			if (value.equals("")) {
				value = " ";
			}
			
			paramsList.add(Params.q.toString() + "=" + stringProcess(value));
		}
		if (this.paramsMap.containsKey(Params.as_q.toString())) {
			String value = this.paramsMap.get(Params.as_q.toString())[0];
			
			if (value.equals("")) {
				value = " ";
			}
			
			paramsList.add(Params.as_q.toString() + "=" + stringProcess(value));
		}
		if (this.paramsMap.containsKey(Params.as_epq.toString())) {
			paramsList.add(Params.as_epq.toString() + "=" + stringProcess(this.paramsMap.get(Params.as_epq.toString())[0]));
		}
		if (this.paramsMap.containsKey(Params.as_oq.toString())) {
			paramsList.add(Params.as_oq.toString() + "=" + stringProcess(this.paramsMap.get(Params.as_oq.toString())[0]));
		}
		if (this.paramsMap.containsKey(Params.as_eq.toString())) {
			paramsList.add(Params.as_eq.toString() + "="+ stringProcess(this.paramsMap.get(Params.as_eq.toString())[0]));
		}

		/*
		 * 由 GSA 去處理不合法的值，use otherwise default value
		 */
		/* Advanced search parameters */
		if (this.paramsMap.containsKey(Params.as_dt.toString())) {
			paramsList.add(Params.as_dt.toString() + "=" + this.paramsMap.get(Params.as_dt.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.as_lq.toString())) {
			paramsList.add(Params.as_lq.toString() + "=" + this.paramsMap.get(Params.as_lq.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.as_occt.toString())) {
			paramsList.add(Params.as_occt.toString() + "=" + this.paramsMap.get(Params.as_occt.toString())[0]);
		}
		// extra process
		if (this.paramsMap.containsKey(Params.as_sitesearch.toString())) {
			paramsList.add(Params.as_sitesearch.toString() + "=" + this.paramsMap.get(Params.as_sitesearch.toString())[0]);
		}
		/* General search parameters */
		if (this.paramsMap.containsKey(Params.numgm.toString())) {
			paramsList.add(Params.numgm.toString() + "=" + this.paramsMap.get(Params.numgm.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.access.toString())) {
			paramsList.add(Params.access.toString() + "=" + this.paramsMap.get(Params.access.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.entqr.toString())) {
			paramsList.add(Params.entqr.toString() + "=" + this.paramsMap.get(Params.entqr.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.entsp.toString())) {
			paramsList.add(Params.entsp.toString() + "=" + this.paramsMap.get(Params.entsp.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.ip.toString())) {
			paramsList.add(Params.ip.toString() + "=" + this.paramsMap.get(Params.ip.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.lr.toString())) {
			paramsList.add(Params.lr.toString() + "=" + this.paramsMap.get(Params.lr.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.num.toString())) {
			paramsList.add(Params.num.toString() + "=" + this.paramsMap.get(Params.num.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.sitesearch.toString())) {
			paramsList.add(Params.sitesearch.toString() + "=" + this.paramsMap.get(Params.sitesearch.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.sort.toString())) {
			paramsList.add(Params.sort.toString() + "=" + this.paramsMap.get(Params.sort.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.start.toString())) {
			paramsList.add(Params.start.toString() + "=" + this.paramsMap.get(Params.start.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.ud.toString())) {
			paramsList.add(Params.ud.toString() + "=" + this.paramsMap.get(Params.ud.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.proxycustom.toString())) {
			paramsList.add(Params.proxycustom.toString() + "=" + this.paramsMap.get(Params.proxycustom.toString())[0]);
		}
		/*
		 * other, enocde
		 */
		if (this.paramsMap.containsKey(Params.ie.toString())) {
			paramsList.add(Params.ie.toString() + "=" + this.paramsMap.get(Params.ie.toString())[0]);
		}
		if (this.paramsMap.containsKey(Params.oe.toString())) {
			paramsList.add(Params.oe.toString() + "=" + this.paramsMap.get(Params.oe.toString())[0]);
		}

		/*
		 * fixed value, value put to initail parameter
		 */
		paramsList.add(Params.site.toString() + "=" + this.collection);
		paramsList.add(Params.client.toString() + "=" + this.frontend);
		paramsList.add(Params.proxystylesheet.toString() + "=" + this.frontend);
		paramsList.add(Params.filter.toString() + "=" + this.filter);
		paramsList.add(Params.proxyreload.toString() + "=1");
		paramsList.add(Params.output.toString() + "=xml_no_dtd");

		/*
		 * Combination
		 */
		StringBuffer paramesBuffer = new StringBuffer();
		ListIterator<String> iterator = paramsList.listIterator();
		
		while (iterator.hasNext()) {
			paramesBuffer.append(iterator.next());
			
			if (iterator.hasNext()) {
				paramesBuffer.append("&");
			}
		}

		logger.debug("requestParames:" + paramesBuffer.toString());
		
		return paramesBuffer.toString();
	}

	private String stringProcess(String term) throws UnsupportedEncodingException {
		logger.debug("[original term:" + term + "]");
		term = URLEncoder.encode(new String(term.getBytes("iso8859-1"), "utf-8"), "utf-8");
		logger.debug("[term:" + term + "]");
		return term;
	}

	/*
	 * 32 google search parameters
	 * 
	 */
	private enum Params {
		access, as_dt, as_epq, as_eq, as_lq, as_occt, as_oq, as_q, 
		as_sitesearch, client, entqr, entsp, filter, getfields, ie, 
		ip, lr, num, numgm, oe, output, partialfields, proxycustom, 
		proxyreload, proxystylesheet, q, requiredfields, site, 
		sitesearch, sort, start, ud
	}
	
}
