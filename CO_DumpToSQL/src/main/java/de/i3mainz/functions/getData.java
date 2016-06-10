package de.i3mainz.functions;

import de.i3mainz.config.Config;
import de.i3mainz.database.PostGIS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class getData {
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, ClassNotFoundException, SQLException {
		try {
			Config.init();
			List<String> data = new ArrayList<>();
			StringBuffer geojson = new StringBuffer();
			geojson.append("{ \"type\": \"FeatureCollection\", \"features\": [ ");
			PostGIS db = new PostGIS();
			//ResultSet rs = db.getDataForGeoJSONfromTimeConceptAndOpttype(Config.databaseArachne, "römisch", "Fundort");
			ResultSet rs = db.getDataForGeoJSONfromTimeConcept(Config.databasePleiades, "roman");
			System.out.println("got data from database");
			while (rs.next()) {
				String feature = "{ \"type\": \"Feature\", \"geometry\": " + rs.getString("geojson") + ", \"properties\": {\"id\": \"" + rs.getString("id") + "\", \"timeconcept\": \"" + rs.getString("timeconcept") + "\", \"internalid\": \"" + rs.getString("internalid") + "\", \"importdate\": \"" + rs.getString("importdate") + "\"} },";
				data.add(feature);
			}
			db.close();
			String lastline = data.get(data.size()-1).substring(0, data.get(data.size()-1).length()-1);
			data.set(data.size()-1, lastline);
			System.out.println("write geojson");
			for (String dataelement : data) {
				geojson.append(dataelement);
			}
			geojson.append("] }");
			System.out.println(geojson);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
}
