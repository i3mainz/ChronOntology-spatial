package de.i3mainz.functions.gazetteer;

import de.i3mainz.classes.BoundingBox;
import de.i3mainz.classes.GazetteerData;
import de.i3mainz.database.PostGIS;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import org.json.simple.parser.ParseException;

public class GazetteerArachneDB {
	
	public static ResultSet getResultsFromArachne(BoundingBox bb) throws Exception {
		PostGIS db = new PostGIS();
		ResultSet rs = db.getBoundingBoxDataPoints("dumpapi.arachne", String.valueOf(bb.getLowerleft_lon()), String.valueOf(bb.getLowerleft_lat()), String.valueOf(bb.getUpperright_lon()), String.valueOf(bb.getUpperright_lat()));
		db.close();
		return rs;
	}
	
	public static List<GazetteerData> ParseDatabaseResultSet(ResultSet rs) throws JDOMException, IOException, ParseException, SQLException {
		List<GazetteerData> resultList = new ArrayList<GazetteerData>();
		while (rs.next()) {
			resultList.add(new GazetteerData(rs.getString("uri"), rs.getString("name"), rs.getString("lat"), rs.getString("lon"), "arachne"));
		}
		return resultList;
	}
	
}
