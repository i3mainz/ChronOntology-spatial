package de.i3mainz.functions.gazetteer;

import de.i3mainz.classes.GazetteerData;
import de.i3mainz.classes.PeripleoSmall;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Peripleo {
	
	public static StringBuffer getResultsFromPeripleo(String types, String from, String to,String limit) throws Exception {
		String url = "http://pelagios.org/peripleo/search";
		String urlParameters = "?";
		if (limit.equals("")) { 
			urlParameters += "from=" + from + "&to=" + to + "&types=" + types;
		} else {
			urlParameters += "from=" + from + "&to=" + to + "&types=" + types + "&limit=" + limit;
		}
		url += urlParameters;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("Peripleo Response Code : " + responseCode);
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response;
		} else {
			return null;
		}
	}

	public static List<PeripleoSmall> ParseDAIJSON(StringBuffer JSON) throws JDOMException, IOException, ParseException {
		List<PeripleoSmall> resultList = new ArrayList<PeripleoSmall>();
		if (JSON != null && !JSON.toString().equals("")) {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON.toString());
			JSONArray items = (JSONArray) jsonObject.get("items");
			for (Object element : items) {
				JSONObject peripleoEntry = (JSONObject) element;
				String HOMEPAGE = (String) peripleoEntry.get("homepage");
				if (HOMEPAGE==null) {
					HOMEPAGE = (String) peripleoEntry.get("identifier");
				}
				String TITLE = (String) peripleoEntry.get("title");
				JSONObject temporal_bounds = (JSONObject) peripleoEntry.get("temporal_bounds");
				String START = "0";
				String END = "0";
				if (temporal_bounds!=null) {
					START = temporal_bounds.get("start").toString();
					END = temporal_bounds.get("end").toString();
				}
				JSONObject geo_bounds = (JSONObject) peripleoEntry.get("geo_bounds");
				String MIN_LON = geo_bounds.get("min_lon").toString();
				String MAX_LON = geo_bounds.get("max_lon").toString();
				String MIN_LAT = geo_bounds.get("min_lat").toString();
				String MAX_LAT = geo_bounds.get("max_lat").toString();
				resultList.add(new PeripleoSmall(Double.parseDouble(MIN_LON), Double.parseDouble(MAX_LON), Double.parseDouble(MIN_LAT), Double.parseDouble(MAX_LAT),Integer.parseInt(START),Integer.parseInt(END),HOMEPAGE,TITLE));
			}
		} else {
			resultList.add(new PeripleoSmall(0.0, 0.0, 0.0, 0.0,0,0,"",""));
		}
		return resultList;
	}
	
	public static List<PeripleoSmall> ResultsFromPeripleo(String types, String from, String to, String limit) throws Exception {
		return ParseDAIJSON(getResultsFromPeripleo(types,from,to,limit));
	}
	
}
