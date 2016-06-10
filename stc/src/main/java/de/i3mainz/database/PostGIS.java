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

	public void open() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
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
		String sql = "SELECT DISTINCT ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		return statement.executeQuery();
	}

	public ResultSet getDataForGeoJSONfromTimeConceptAndOpttype(String table, String timeconcept, String opttype) throws SQLException {
		String sql = "SELECT DISTINCT ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ? AND opttype = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, opttype);
		return statement.executeQuery();
	}

	public ResultSet getDataForGeoJSONfromTimeConceptWithGeomType(String table, String timeconcept, String geomtype) throws SQLException {
		String sql = "SELECT DISTINCT ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ? AND ST_GeometryType(geom) = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, geomtype);
		return statement.executeQuery();
	}

	public ResultSet getDataForGeoJSONfromTimeConceptAndOpttypeWithGeomType(String table, String timeconcept, String opttype, String geomtype) throws SQLException {
		String sql = "SELECT DISTINCT ST_asgeojson(geom) AS geojson FROM " + table + " WHERE timeconcept = ? AND opttype = ? AND ST_GeometryType(geom) = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, opttype);
		statement.setString(3, geomtype);
		return statement.executeQuery();
	}

	public ResultSet getBoundingBoxAndCentroidFromTimeConcept(String table, String timeconcept) throws SQLException {
		String sql = "SELECT ST_EXTENT(geom) AS extent, ST_ASTEXT(ST_CENTROID(ST_EXTENT(geom))) AS centroid FROM " + table + " WHERE timeconcept = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		return statement.executeQuery();
	}

	public ResultSet getBoundingBoxAndCentroidFromTimeConceptAndOpttype(String table, String timeconcept, String opttype) throws SQLException {
		String sql = "SELECT ST_EXTENT(geom) AS extent, ST_ASTEXT(ST_CENTROID(ST_EXTENT(geom))) AS centroid FROM " + table + " WHERE timeconcept = ? AND opttype = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, opttype);
		return statement.executeQuery();
	}

	public ResultSet getBoundingBoxAndCentroidFromTimeConceptWithGeomType(String table, String timeconcept, String geomtype) throws SQLException {
		String sql = "SELECT ST_EXTENT(geom) AS extent, ST_ASTEXT(ST_CENTROID(ST_EXTENT(geom))) AS centroid FROM " + table + " WHERE timeconcept = ? AND ST_GeometryType(geom) = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, geomtype);
		return statement.executeQuery();
	}

	public ResultSet getBoundingBoxAndCentroidFromTimeConceptAndOpttypeWithGeomType(String table, String timeconcept, String opttype, String geomtype) throws SQLException {
		String sql = "SELECT ST_EXTENT(geom) AS extent, ST_ASTEXT(ST_CENTROID(ST_EXTENT(geom))) AS centroid FROM " + table + " WHERE timeconcept = ? AND opttype = ? AND ST_GeometryType(geom) = ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, timeconcept);
		statement.setString(2, opttype);
		statement.setString(3, geomtype);
		return statement.executeQuery();
	}

	// boundingbox
	public ResultSet getBoundingBoxFromPoint4326(String lat, String lon, String radius) throws SQLException {
		String sql = "SELECT ST_EXTENT(ST_BUFFER(ST_SETSRID(ST_GEOMFROMTEXT('POINT(" + lon + " " + lat + ")'),4326)," + radius + ")) AS extent;";
		PreparedStatement statement = connection.prepareStatement(sql);
		return statement.executeQuery();
	}

	public ResultSet getBoundingBoxGeoJSONFromPoint4326(String lat, String lon, String radius) throws SQLException {
		String sql = "SELECT ST_ASGEOJSON(ST_EXTENT(ST_BUFFER(ST_SETSRID(ST_GEOMFROMTEXT('POINT(" + lon + " " + lat + ")'),4326)," + radius + "))) AS extent;";
		PreparedStatement statement = connection.prepareStatement(sql);
		return statement.executeQuery();
	}

	// autocomplete
	public ResultSet getAutocompleteTimeconceptBySubstring(String table, String substring, int limit) throws SQLException {
		String sql = "SELECT DISTINCT timeconcept FROM " + table + " WHERE timeconcept LIKE ? ORDER BY timeconcept LIMIT ?;";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, "%" + substring + "%");
		statement.setInt(2, limit);
		return statement.executeQuery();
	}

	// places from boundingbox
	public ResultSet getBoundingBoxDataPoints(String table, String left, String bottom, String right, String top) throws SQLException {
		String sql = "SELECT id AS uri, timeconcept AS name, ST_X(geom) AS lon, ST_Y(geom) AS lat FROM " + table + " WHERE ST_Intersects(geom, ST_MakeEnvelope(" + left + ", " + bottom + ", " + right + ", " + top + ", 4326)) AND ST_GeometryType(geom) = 'ST_Point';";
		PreparedStatement statement = connection.prepareStatement(sql);
		return statement.executeQuery();
	}
	
	// places from boundingbox
	public ResultSet getPointInsideGeom(String lat, String lon, String geom) throws SQLException {
		String sql = "SELECT ST_CONTAINS(ST_GeomFromgeoJSON('"+geom+"'),ST_GeomFromText('POINT("+lon+" "+lat+")'))";
		PreparedStatement statement = connection.prepareStatement(sql);
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
