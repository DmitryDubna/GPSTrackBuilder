package gps.track;

import java.util.Date;

public class GPSTrackPoint {
	
	private double latitude;
	private double longitude;
	private double height;
	private Date date;
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public long getTime()
	{
		return date.getTime();
	}
	
	public GPSTrackPoint clone()
	{
		return new GPSTrackPoint(latitude, longitude, height, date);
	}
	
	
	// constructor
	public GPSTrackPoint(double latitude, double longitude, double height, Date date)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
		this.date = date;
	}
}
