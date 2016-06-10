package de.i3mainz.classes;

public class PeripleoSmall {
	
	private double MIN_LON = -9999.0;
	private double MAX_LON = -9999.0;
	private double MIN_LAT = -9999.0;
	private double MAX_LAT = -9999.0;
	private int START = -9999;
	private int END = -9999;
	private String HOMEPAGE = "";
	private String TITLE = "";

	public PeripleoSmall() {
	}
	
	public PeripleoSmall(double min_lon, double max_lon, double min_lat, double max_lat, int start, int end, String homepage, String title) {
		this.MIN_LON = min_lon;
		this.MAX_LON = max_lon;
		this.MIN_LAT = min_lat;
		this.MAX_LAT = max_lat;
		this.START = start;
		this.END = end;
		this.HOMEPAGE = homepage;
		this.TITLE = title;
	}

	public double getMIN_LON() {
		return MIN_LON;
	}

	public void setMIN_LON(double MIN_LON) {
		this.MIN_LON = MIN_LON;
	}

	public double getMAX_LON() {
		return MAX_LON;
	}

	public void setMAX_LON(double MAX_LON) {
		this.MAX_LON = MAX_LON;
	}

	public double getMIN_LAT() {
		return MIN_LAT;
	}

	public void setMIN_LAT(double MIN_LAT) {
		this.MIN_LAT = MIN_LAT;
	}

	public double getMAX_LAT() {
		return MAX_LAT;
	}

	public void setMAX_LAT(double MAX_LAT) {
		this.MAX_LAT = MAX_LAT;
	}

	public int getSTART() {
		return START;
	}

	public void setSTART(int START) {
		this.START = START;
	}

	public int getEND() {
		return END;
	}

	public void setEND(int END) {
		this.END = END;
	}

	public String getHOMEPAGE() {
		return HOMEPAGE;
	}

	public void setHOMEPAGE(String HOMEPAGE) {
		this.HOMEPAGE = HOMEPAGE;
	}

	public String getTITLE() {
		return TITLE;
	}

	public void setTITLE(String TITLE) {
		this.TITLE = TITLE;
	}
	
}
