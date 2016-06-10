package de.i3mainz.functions.gazetteer;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.i3mainz.classes.GazetteerData;
import de.i3mainz.classes.PeripleoSmall;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jdom.JDOMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class Periodo {

	public static StringBuffer getResultsFromPeriodoByURI(String uri) throws Exception {

		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// read PELAGIOS URI - Dataset (with API)
		String url = "https://test.perio.do/d.json";
		InputStream in = new URL(url).openStream();

		// read the RDF/XML file
		model.read(in, null); // null base URI, since model URIs are absolute
		in.close();

		// Create a new query
		//String queryString = "SELECT * WHERE { ?s ?p ?o }";
		String queryString
				= "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "SELECT ?dataset WHERE { "
				+ "?dataset a skos:Concept."
				+ "}";

		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();

		// Output query results	
		String datasetXML = ResultSetFormatter.asXMLString(results);
		System.out.println(datasetXML); //consolelog

		// Important - free up resources used running the query
		qe.close();

		/*String url = "http://pelagios.org/peripleo/search";
		 String urlParameters = "?";
		 if (limit.equals("")) { 
		 urlParameters += "from=" + from + "&to=" + to + "&types=" + uri;
		 } else {
		 urlParameters += "from=" + from + "&to=" + to + "&types=" + uri + "&limit=" + limit;
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
		 }*/
		return null;
	}

	public static StringBuffer getPeriodoJSON() throws Exception {
		String https_url = "https://google.com/";
		URL url;
		url = new URL(https_url);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		//dumpl all cert info
		print_https_cert(con);
		//dump all the content
		return print_content(con);
	}

	public static List<PeripleoSmall> ParsePeriodoJSON(StringBuffer JSON) throws JDOMException, IOException, ParseException {
		List<PeripleoSmall> resultList = new ArrayList<PeripleoSmall>();
		if (JSON != null && !JSON.toString().equals("")) {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSON.toString());
			JSONArray items = (JSONArray) jsonObject.get("items");
			for (Object element : items) {
				JSONObject peripleoEntry = (JSONObject) element;
				String HOMEPAGE = (String) peripleoEntry.get("homepage");
				if (HOMEPAGE == null) {
					HOMEPAGE = (String) peripleoEntry.get("identifier");
				}
				String TITLE = (String) peripleoEntry.get("title");
				JSONObject temporal_bounds = (JSONObject) peripleoEntry.get("temporal_bounds");
				String START = "0";
				String END = "0";
				if (temporal_bounds != null) {
					START = temporal_bounds.get("start").toString();
					END = temporal_bounds.get("end").toString();
				}
				JSONObject geo_bounds = (JSONObject) peripleoEntry.get("geo_bounds");
				String MIN_LON = geo_bounds.get("min_lon").toString();
				String MAX_LON = geo_bounds.get("max_lon").toString();
				String MIN_LAT = geo_bounds.get("min_lat").toString();
				String MAX_LAT = geo_bounds.get("max_lat").toString();
				resultList.add(new PeripleoSmall(Double.parseDouble(MIN_LON), Double.parseDouble(MAX_LON), Double.parseDouble(MIN_LAT), Double.parseDouble(MAX_LAT), Integer.parseInt(START), Integer.parseInt(END), HOMEPAGE, TITLE));
			}
		} else {
			resultList.add(new PeripleoSmall(0.0, 0.0, 0.0, 0.0, 0, 0, "", ""));
		}
		return resultList;
	}

	public static List<PeripleoSmall> ResultsFromPeriodo(String uri) throws Exception {
		return ParsePeriodoJSON(getResultsFromPeriodoByURI(uri));
	}

	private static void print_https_cert(HttpsURLConnection con) {
		if (con != null) {
			try {
				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");
				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					System.out.println("\n");
				}
			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static StringBuffer print_content(HttpsURLConnection con) {
		if (con != null) {
			try {
				System.out.println("****** Content of the URL ********");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return response;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

}
