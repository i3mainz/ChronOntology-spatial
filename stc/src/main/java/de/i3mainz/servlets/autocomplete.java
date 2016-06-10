package de.i3mainz.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.i3mainz.config.Config;
import de.i3mainz.database.PostGIS;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class autocomplete extends HttpServlet {

	private static JSONObject jsonobj_query = new JSONObject(); // {}
	private static JSONArray jsonarray_suggestions = new JSONArray(); // []

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int suggestions = 100;
		jsonarray_suggestions = new JSONArray();
		Config.init();
		try {
			String req_substring = "";
			if (request.getParameter("query") != null) {
				//req_substring = request.getParameter("query").toLowerCase();
				req_substring = request.getParameter("query");
				req_substring = URLDecoder.decode(req_substring, "UTF-8");
			}
			// FILTER
			String req_db = "";
			if (request.getParameter("db") != null) {
				req_db = request.getParameter("db");
			}
			if (req_substring.length() <= 1) {
				throw new IllegalArgumentException();
			} else {
				if (req_db.equals("dumpapi.arachne")) {
					setSuggestionByDatabaseQuery("dumpapi.arachne", req_substring, suggestions, "arachne");
				} else if (req_db.equals("dumpapi.pleiades")) {
					setSuggestionByDatabaseQuery("dumpapi.pleiades", req_substring, suggestions, "pleiades");
				} else {
					setSuggestionByDatabaseQuery("dumpapi.arachne", req_substring, suggestions, "arachne");
					setSuggestionByDatabaseQuery("dumpapi.pleiades", req_substring, suggestions, "pleiades");
				}
				jsonobj_query.put("suggestions", jsonarray_suggestions);
				jsonobj_query.put("query", req_substring);
				// pretty print JSON output (Gson)
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				out.print(gson.toJson(jsonobj_query));
				response.setStatus(200);
			}
		} catch (Exception e) {
			response.setStatus(500);
			//out.print(Logging.getMessageJSON(e, getClass().getName()));
		} finally {
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setCharacterEncoding("UTF-8");
			out.close();
		}
	}

	private static void setSuggestionByDatabaseQuery(String table, String substring, int suggestions, String provenance) throws IOException, ClassNotFoundException, SQLException {
		PostGIS db = new PostGIS();
		ResultSet rs = db.getAutocompleteTimeconceptBySubstring(table, substring, suggestions);
		while (rs.next()) {
			JSONObject jsonobj_suggestion = new JSONObject(); // {}
			// neccessary items
			jsonobj_suggestion.put("value", rs.getString("timeconcept"));
			jsonobj_suggestion.put("data", rs.getString("timeconcept"));
			// additional items
			jsonobj_suggestion.put("provenance", provenance);
			jsonarray_suggestions.add(jsonobj_suggestion);
		}
		db.close();
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
