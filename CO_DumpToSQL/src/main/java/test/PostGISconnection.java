package test;

import de.i3mainz.config.Config;
import de.i3mainz.database.PostGIS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostGISconnection {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, ClassNotFoundException, SQLException {
		try {
			List<String> data = new ArrayList<>();
			Config.init();
			StringBuffer geojson = new StringBuffer(); http://geojson.org/geojson-spec.html 
			geojson.append("{ \"type\": \"FeatureCollection\", \"features\": [ ");
			PostGIS db = new PostGIS();
			//ResultSet rs = db.getDataForGeoJSONfromTimeConceptAndOpttype(Config.databaseArachne, "römisch", "Fundort");
			ResultSet rs = db.getDataForGeoJSONfromTimeConcept(Config.databasePleiades, "roman");
			System.out.println("got data from database");
			while (rs.next()) {
				//String a = "{ \"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [102.0, 0.5]}, \"properties\": {\"prop0\": \""+rs.getString("internalid")+"\"} },";
				String feature = "{ \"type\": \"Feature\", \"geometry\": " + rs.getString("geojson") + ", \"properties\": {\"id\": \"" + rs.getString("id") + "\", \"timeconcept\": \"" + rs.getString("timeconcept") + "\", \"internalid\": \"" + rs.getString("internalid") + "\", \"importdate\": \"" + rs.getString("importdate") + "\"} },";
				data.add(feature);
			}
			/*boolean rs = db.executeSQLFile("C:\\Users\\florian.thiery\\Desktop\\arachne_periods.sql");
			 System.out.println("result: " + rs);*/
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
