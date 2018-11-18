package schnitzel_web;

import java.lang.Math; 

public class Checkpoint
{
	
	public double lat;
	public double lon;
	public double threshold;
	public double progress;
	public String hint;
	
	public Checkpoint(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
		this.threshold = 80;
	}
	
	public Checkpoint()
	{
		this.threshold = 80;
	}
	
	public double updateProgress(double user_lat, double user_lon)
	{
		progress = euclidDist(user_lat, user_lon, lat, lon);
		
		if(progress > threshold)
		{
			//item found, move to next	
			return (double)-1;
		}			
		else
		{
			return progress;			
		}
	}

	private double euclidDist(double user_lat, double user_lon, double lat2, double lon2)
	{
		return Math.sqrt(Math.pow((user_lat - lat), 2) + Math.pow((user_lon - lon), 2));
		//TODO: perform some sort of scaling to return sensible number)
	}
	
	public String receiveCheckpointInfo()
	{
		return lat + ", " + lon;
	}
	
	public void setLat(double newlat) {
		lat = newlat;
	}
	
	public void setLon(double newlon) {
		lon = newlon;
	}
	
	public void setThreshold(double newthreshold) {
		threshold = newthreshold;
	}
	
	public void setProgress(double newprogress) {
		progress = newprogress;
	}
	
	public void setHint(String newhint) {
		hint = newhint;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public String getHint() {
		return hint;
	}
}
