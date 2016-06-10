package de.i3mainz.database;

import de.i3mainz.config.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostGIS {

	private Connection connection;

	public PostGIS() throws IOException, ClassNotFoundException, SQLException {
		open();
	}

	public void open() throws SQLException {
		String url = "jdbc:postgresql://" + Config.POSTGIS_HOST + ":" + Config.POSTGIS_PORT + "/" + Config.POSTGIS_DATABASE;
		connection = DriverManager.getConnection(url, Config.POSTGIS_USER, Config.POSTGIS_PWD);
	}

	public void close() throws SQLException {
		connection.close();
	}

	public boolean executeSQLFile(String filename) throws FileNotFoundException, SQLException, IOException {
		List<String> lines = new ArrayList<String>();
		try {
			// store line in list
			File file = new File(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String line;
			System.out.println("start read lines...");
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			// parse array
			String sql = "";
			for (int j = 0; j < lines.size(); j++) {
				sql += lines.get(j);
				if ((j % 2000 == 0 && j != 0) || (j == lines.size() - 1)) {
					System.out.println("read line " + j + " ");
					System.out.println("open database...");
					connection.createStatement().execute(sql);
					sql = "";
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public ResultSet getDataForGeoJSONfromTimeConcept(String table, String timeconcept) throws SQLException {
		String sql = "SELECT *, ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		System.out.println(statement.toString());
		return statement.executeQuery();
	}
	
	public ResultSet getDataForGeoJSONfromTimeConceptAndOpttype(String table, String timeconcept, String opttype) throws SQLException {
		String sql = "SELECT *, ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ? AND opttype = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, opttype);
		return statement.executeQuery();
	}

	/*
	 * Examples
	 */
	/*public int insertFind(int nr, String location, double lon, double lat, Timestamp timestamp, String remark) throws SQLException {
	 String sql = "INSERT INTO Find(IDREF_balloon,find_timestamp,location,geom,remark) VALUES (?,?,?,ST_GeomFromText(?,4326),?)";
	 PreparedStatement statement = connection.prepareStatement(sql);
	 statement = connection.prepareStatement(sql);
	 statement.setInt(1, 0);
	 statement.setTimestamp(2, timestamp);
	 statement.setString(3, location);
	 statement.setString(4, "POINT(" + lon + " " + lat + ")");
	 statement.setString(5, remark);
	 return statement.executeUpdate();
	 }

	 public int deleteEvent(String name) throws SQLException {
	 String sql = "DELETE FROM Event WHERE event_name = ?";
	 PreparedStatement statement = connection.prepareStatement(sql);
	 statement.setString(1, name);
	 return statement.executeUpdate();
	 }*/
}
