package de.i3mainz.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.i3mainz.classes.GazetteerData;
import de.i3mainz.functions.gazetteer.ChronOntology;
import de.i3mainz.functions.gazetteer.GazetteerDAI;
import de.i3mainz.functions.gazetteer.Periodo;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class getChronOntologyGeo extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String req_id = "";
			if (request.getParameter("id") != null) {
				req_id = request.getParameter("id");
			}
			req_id = URLDecoder.decode(req_id, "UTF-8");
			List<String> geoitems = ChronOntology.ResultsFromChronOntology(req_id);
			GazetteerData gd;
			List<GazetteerData> resultList = new ArrayList<GazetteerData>();
			for (String element : geoitems) {
				String[] tmp = element.split("%");
				gd = GazetteerDAI.getgeoJSONFromDaiGazetteer(tmp[0]);
				gd.setOPTIONAL(tmp[1]);
				if (gd != null && !gd.getGEOMETRY().equals("")) {
					resultList.add(gd);
				}
			}
			// output
			String geojson = "";
			geojson += "{";
			geojson += "\"type\": \"FeatureCollection\",";
			geojson += "\"features\": [";
			for (GazetteerData element : resultList) {
				geojson += "{ \"type\": \"Feature\",";
				geojson += "\"geometry\": ";
				geojson += element.getGEOMETRY();
				geojson += ",";
				geojson += "\"properties\": {";
				geojson += "\"homepage\": \""+element.getURI()+"\",";
				geojson += "\"name\": \""+element.getNAME()+"\",";
				geojson += "\"relation\": \""+element.getOPTIONAL()+"\"";
				geojson += "}";
				geojson += "},";
			}
			geojson = geojson.substring(0, geojson.length()-1);
			geojson += "]";
			geojson += "}";
			// pretty print JSON output (Gson)
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(geojson.toString()).getAsJsonObject();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			out.print(gson.toJson(json));
		} catch (Exception e) {
			System.out.println(e.toString());
			out.println(e.toString());
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
