package de.i3mainz.config;

public class Config {
	
	public static String path = "C:\\Users\\florian.thiery\\Desktop\\";
	
	public static String fileInArachne = "arachne_periods.csv";
	public static String fileOutArachne = "arachne_periods.sql";
	
	public static String fileInPleiadesPlaces = "pleiades-places-20151012.csv";
	public static String fileInPleiadesLocations = "pleiades-locations-20151012.csv";
	public static String fileOutPleiades = "pleiades-placeslocations-20151012.sql";
	
	public static String databaseArachne = "dumpapi.arachne";
	public static String databasePleiades = "dumpapi.pleiades";
	
	// POSTGIS
	public static final String POSTGIS_HOST = "143.93.113.157";
	public static final String POSTGIS_PORT = "5432";
	public static final String POSTGIS_DATABASE = "Chronontology";
	public static final String POSTGIS_USER = "postgis";
	public static final String POSTGIS_PWD = "postgis";
	
	public static void init() {
		fileInArachne = path + fileInArachne;
		fileOutArachne = path + fileOutArachne;
		fileInPleiadesPlaces = path + fileInPleiadesPlaces;
		fileInPleiadesLocations = path + fileInPleiadesLocations;
		fileOutPleiades = path + fileOutPleiades;
	}
	
}
