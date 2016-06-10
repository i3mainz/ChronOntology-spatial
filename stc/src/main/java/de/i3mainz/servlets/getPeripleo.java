package de.i3mainz.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.i3mainz.classes.GazetteerData;
import de.i3mainz.classes.PeripleoSmall;
import de.i3mainz.database.PostGIS;
import de.i3mainz.functions.gazetteer.GazetteerDAI;
import de.i3mainz.functions.gazetteer.Peripleo;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class getPeripleo extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		try {
			// params
			String req_from = "";
			if (request.getParameter("from") != null) {
				req_from = request.getParameter("from");
			}
			String req_to = "";
			if (request.getParameter("to") != null) {
				req_to = request.getParameter("to");
			}
			String req_types = "";
			if (request.getParameter("types") != null) {
				req_types = request.getParameter("types");
			}
			String req_prettyprint = "";
			if (request.getParameter("prettyprint") != null) {
				req_prettyprint = request.getParameter("prettyprint");
			}
			String req_limit = "";
			if (request.getParameter("limit") != null) {
				req_limit = request.getParameter("limit");
			}
			// get data
			List<PeripleoSmall> resultList = new ArrayList<PeripleoSmall>();
			resultList = Peripleo.ResultsFromPeripleo(req_types, req_from, req_to, req_limit);
			// output
			StringBuffer geojson = new StringBuffer();
			geojson.append("{ \"type\": \"FeatureCollection\", \"features\": [ ");
			for (PeripleoSmall element : resultList) {
				geojson.append("{ \"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": ["+element.getMAX_LON()+", "+element.getMAX_LAT()+"]}, \"properties\": {\"title\":\""+element.getTITLE()+"\",\"homepage\":\""+element.getHOMEPAGE()+"\"} },");
			}
			String geojson2 = geojson.toString();
			geojson2 = geojson2.substring(0, geojson2.length() - 1);
			geojson2 += "]}";
			// pretty print JSON output (Gson)
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(geojson2).getAsJsonObject();
			if (req_prettyprint.equals("true")) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				out.print(gson.toJson(json));
			} else {
				out.print(json);
			}
		} catch (Exception e) {
			String ex = e.toString();
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
		processRequest(request, response);
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
		processRequest(request, response);
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
