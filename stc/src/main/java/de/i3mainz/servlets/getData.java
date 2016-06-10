package de.i3mainz.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.i3mainz.config.Config;
import de.i3mainz.database.PostGIS;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class getData extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException, SQLException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");
		try {
			String req_db = ""; //schema.table
			String req_timeconcept = "";
			String req_opttype = "";
			String req_geomtype = ""; // ST_Point ST_LineString ST_Polygon
			if (request.getParameter("db") != null) {
				req_db = request.getParameter("db");
			}
			if (request.getParameter("timeconcept") != null) {
				req_timeconcept = request.getParameter("timeconcept");
			}
			if (request.getParameter("opttype") != null) {
				req_opttype = request.getParameter("opttype");
			}
			if (request.getParameter("geomtype") != null) {
				req_geomtype = request.getParameter("geomtype"); 
			}
			Config.init();
			List<String> data = new ArrayList<String>();
			StringBuffer geojson = new StringBuffer();
			geojson.append("{ \"type\": \"FeatureCollection\", \"features\": [ ");
			PostGIS db = new PostGIS();
			ResultSet rs;
			if (!req_db.equals("") && !req_timeconcept.equals("") && req_opttype.equals("")) {
				rs = db.getDataForGeoJSONfromTimeConceptWithGeomType(req_db, req_timeconcept,req_geomtype);
			} else {
				rs = db.getDataForGeoJSONfromTimeConceptAndOpttypeWithGeomType(req_db, req_timeconcept, req_opttype,req_geomtype);
			}
			System.out.println("got data from database");
			while (rs.next()) {
				//String feature = "{ \"type\": \"Feature\", \"geometry\": " + rs.getString("geojson") + ", \"properties\": {\"id\": \"" + rs.getString("id") + "\", \"timeconcept\": \"" + rs.getString("timeconcept") + "\", \"internalid\": \"" + rs.getString("internalid") + "\", \"importdate\": \"" + rs.getString("importdate") + "\"} },";
				String feature = "{ \"type\": \"Feature\", \"geometry\": " + rs.getString("geojson") + ", \"properties\": {} },";
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
			// pretty print JSON output (Gson)
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(geojson.toString()).getAsJsonObject();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			out.print(gson.toJson(json));
			// set outputformat
			response.setContentType("application/json;charset=UTF-8");
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(getHeatmapData.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(getHeatmapData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(getHeatmapData.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(getHeatmapData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
