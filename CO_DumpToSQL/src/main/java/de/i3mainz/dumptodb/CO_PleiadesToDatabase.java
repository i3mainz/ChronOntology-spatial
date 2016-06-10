package de.i3mainz.dumptodb;

import de.i3mainz.config.Config;
import de.i3mainz.database.PostGIS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CO_PleiadesToDatabase {

	private static final Logger log = Logger.getLogger(CO_PleiadesToDatabase.class.getName());
	private static int tokensCOUNT = 26;
	private static List<String> sql = new ArrayList<String>();
	private static int writeLine = 2000;
	private static int i = -1;
	private static int geomNumber = -1;
	private static int line_geometry = -1;
	private static int line_timePeriodsKeys = -1;
	private static int line_id = -1;
	private static int line_desc = -1;

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		try {
			// LOAD CONFIG VALUES
			Config.init();
			// PLACES
			System.out.println("start read places...");
			File fileDir = new File(Config.fileInPleiadesPlaces);
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
			String str;
			i = 0;
			boolean warning = false;
			sql.add("DELETE FROM " + Config.databasePleiades + ";");
			sql.add("ALTER SEQUENCE dumpapi.pleiades_id_seq RESTART WITH 1;");
			while ((str = in.readLine()) != null) {
				warning = false;
				String[] split = str.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); //String[] split = str.split(",");
				int tokens = split.length;
				if (tokens != tokensCOUNT) {
					log.log(Level.WARNING, "wrong count in line " + i, "wrong count in line " + i);
					warning = true;
				}
				// write and parse header for column headings
				if (i == 0) {
					int ii = 0;
					for (String t : split) {
						System.out.println("> [" + ii + "]" + t);
						if (t.equals("extent")) {
							line_geometry = ii;
						} else if (t.equals("timePeriodsKeys")) {
							line_timePeriodsKeys = ii;
						} else if (t.equals("id")) {
							line_id = ii;
						}
						ii++;
					}
					System.out.println(">> " + "[geometry:" + line_geometry + "] " + "[timePeriodsKeys:" + line_timePeriodsKeys + "] " + "[id:" + line_id + "] ");
				}
				// output for first line
				if (i == 1) {
					int ii = 0;
					for (String t : split) {
						System.out.println("> [" + ii + "]" + t);
						ii++;
					}
				}
				if (i > 0) {
					// parse periods
					String periodLine = split[line_timePeriodsKeys].replace("\"", "");
					String[] periods = periodLine.split(","); // for each period import new line in database
					for (String period : periods) {
						// parse geom
						String geoJSON = split[line_geometry];
						if (!geoJSON.equals("null")) {
							geoJSON = geoJSON.substring(1, geoJSON.length() - 1);
							geoJSON = geoJSON.replace("\"\"", "\"");
							//JSONParser parser = new JSONParser();
							//Object obj = parser.parse(geoJSON); // ST_GeomFromGeoJSON(geoJSON)
							//JSONObject jsonObject = (JSONObject) obj;
							//String type = (String) jsonObject.get("type");
							// database input write sql file and load it to postgis db
							Calendar cal = Calendar.getInstance();
							Date time = cal.getTime();
							DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
							String d = formatter.format(time);
							//String sqlstatement = "INSERT INTO pleiades (timeconcept, geom, internalid, opttype, optdesc, importdate) VALUES ('" + periods[j] + "',ST_GeomFromGeoJSON('" + geoJSON + "'), '" + split[11] + "', '" + split[8] + "', '" + split[24] + "', '" + d + "');";
							String sqlstatement = "INSERT INTO " + Config.databasePleiades + " (timeconcept, geom, internalid, opttype, optdesc, importdate) VALUES ('" + period + "',ST_SetSRID(ST_GeomFromGeoJSON('" + geoJSON + "'),4326), '" + split[line_id] + "', '" + "place" + "', '" + "" + "', '" + d + "');";
							sql.add(sqlstatement);
						}
					}
					// console output
					if (i % writeLine == 0 && i != 0) {
						System.out.println("read line " + i + " ");
					}
				}
				i++;
			}
			in.close();
			System.out.println(i + " lines read...");
			System.out.println(sql.size() + " statements created...");

			// LOCATIONS
			System.out.println("start read locations...");
			File fileDir2 = new File(Config.fileInPleiadesLocations);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir2), "UTF8"));
			String str2;
			i = 0;
			warning = false;
			while ((str2 = in2.readLine()) != null) {
				warning = false;
				//String[] split = str.split(",");
				String[] split = str2.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				int tokens = split.length;
				if (tokens != tokensCOUNT) {
					log.log(Level.WARNING, "wrong count in line " + i, "wrong count in line " + i);
					warning = true;
				}
				// read header
				if (i == 0) {
					int ii = 0;
					for (String t : split) {
						System.out.println("> [" + ii + "]" + t);
						if (t.equals("geometry")) {
							line_geometry = ii;
						} else if (t.equals("timePeriodsKeys")) {
							line_timePeriodsKeys = ii;
						} else if (t.equals("pid")) {
							line_id = ii;
						}
						ii++;
					}
					System.out.println(">> " + "[geometry:" + line_geometry + "] " + "[timePeriodsKeys:" + line_timePeriodsKeys + "] " + "[id:" + line_id + "] ");
				}
				if (i == 1) {
					int ii = 0;
					for (String t : split) {
						System.out.println("> [" + ii + "]" + t);
						ii++;
					}
				}
				if (i > 0 && !warning) {
					// parse periods
					String periodLine = split[line_timePeriodsKeys].replace("\"", "");
					String[] periods = periodLine.split(","); // for each period import new line in database
					for (String period : periods) {
						// parse geom
						String geoJSON = split[line_geometry];
						if (!geoJSON.equals("null") && !geoJSON.equals("")) {
							geoJSON = geoJSON.substring(1, geoJSON.length() - 1);
							geoJSON = geoJSON.replace("\"\"", "\"");
							//JSONParser parser = new JSONParser();
							//Object obj = parser.parse(geoJSON); // ST_GeomFromGeoJSON(geoJSON)
							//JSONObject jsonObject = (JSONObject) obj;
							//String type = (String) jsonObject.get("type");
							// database input write sql file and load it to postgis db
							Calendar cal = Calendar.getInstance();
							Date time = cal.getTime();
							DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
							String d = formatter.format(time);
							String sqlstatement = "INSERT INTO " + Config.databasePleiades + " (timeconcept, geom, internalid, opttype, optdesc, importdate) VALUES ('" + period + "',ST_SetSRID(ST_GeomFromGeoJSON('" + geoJSON + "'),4326), '" + split[line_id] + "', '" + "location" + "', '" + "" + "', '" + d + "');";
							sql.add(sqlstatement);
						}
					}
					// console output
					if (i % writeLine == 0 && i != 0) {
						System.out.println("read line " + i + " ");
					}
				}
				i++;
			}
			in2.close();
			System.out.println(i + " lines read...");
			System.out.println(sql.size() + " statements created...");

			// WRITE SQL FILE
			System.out.println("write sql file...");
			PrintWriter writer = new PrintWriter(Config.fileOutPleiades, "UTF-8");
			for (String sqlline : sql) {
				writer.println(sqlline);
			}
			writer.close();
			System.out.println("write sql file finished...");
			// send to database
			PostGIS db = new PostGIS();
			boolean res = db.executeSQLFile(Config.fileOutPleiades);
			System.out.println("result: " + res);
			db.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString() + " | " + i, e.toString() + " | " + i);
			for (StackTraceElement element : e.getStackTrace()) {
				String message = "element: \"" + element.getClassName() + " / " + element.getMethodName() + "() / " + element.getLineNumber() + "\"";
				log.log(Level.SEVERE, message, message);
			}
		}
	}

}
