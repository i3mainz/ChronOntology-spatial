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

public class CO_ArachneToDatabase {

	private static final Logger log = Logger.getLogger(CO_PleiadesToDatabase.class.getName());
	private static int tokensCOUNT = 5;
	private static List<String> sql = new ArrayList<String>();
	private static int writeLine = 5000;
	private static int i = -1;
	private static int geomNumber = -1;
	private static int line_geometry_lon = -1;
	private static int line_geometry_lat = -1;
	private static int line_period = -1;
	private static int line_id = -1;
	private static int line_type = -1;

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		try {
			// LOAD CONFIG VALUES
			Config.init();
			// PLACES
			System.out.println("start read places...");
			File fileDir = new File(Config.fileInArachne);
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
			String str;
			i = 0;
			boolean warning = false;
			sql.add("DELETE FROM " + Config.databaseArachne + ";");
			sql.add("ALTER SEQUENCE dumpapi.arachne_id_seq RESTART WITH 1;");
			while ((str = in.readLine()) != null) {
				warning = false;
				String[] split = str.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); //String[] split = str.split(",");
				int tokens = split.length;
				if (tokens != tokensCOUNT) {
					log.log(Level.WARNING, "wrong count in line " + i, "wrong count in line " + i);
					warning = true;
				}
				line_geometry_lon = 2;
				line_geometry_lat = 3;
				line_period = 1;
				line_id = 0;
				line_type = 4;
				// output for first line
				if (i == 0) {
					int ii = 0;
					for (String t : split) {
						System.out.println("> [" + ii + "]" + t);
						ii++;
					}
				}
				// database input write sql file and load it to postgis db
				Calendar cal = Calendar.getInstance();
				Date time = cal.getTime();
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				String d = formatter.format(time);
				String sqlstatement = "INSERT INTO " + Config.databaseArachne + " (timeconcept, geom, internalid, opttype, optdesc, importdate) "
						+ "VALUES ('" + split[line_period].replace("\"", "")
						+ "', ST_SetSRID(ST_MakePoint(" + split[line_geometry_lon].replace("\"", "")
						+ ", " + split[line_geometry_lat].replace("\"", "") + "),4326), '" + split[line_id].replace("\"", "")
						+ "', '" + split[line_type].replace("\"", "") + "', '" + "" + "', '" + d + "');";
				sql.add(sqlstatement);
				// console output
				if (i % writeLine == 0 && i != 0) {
					System.out.println("read line " + i + " ");
				}
				i++;
			}
			in.close();
			System.out.println(i + " lines read...");
			System.out.println(sql.size() + " statements created...");

			// WRITE SQL FILE
			System.out.println("write sql file...");
			PrintWriter writer = new PrintWriter(Config.fileOutArachne, "UTF-8");
			for (String sqlline : sql) {
				writer.println(sqlline);
			}
			writer.close();
			System.out.println("write sql file finished...");
			// send to database
			PostGIS db = new PostGIS();
			boolean res = db.executeSQLFile(Config.fileOutArachne);
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
