package com.example.briceveyredesoras.bubble_ev.common;

import com.example.briceveyredesoras.bubble_ev.common.interfaces.PositionInterface;
import com.example.briceveyredesoras.bubble_ev.common.exceptions.*;

public class Position implements PositionInterface {
	private double longitude;
	private double latitude;
	
	public Position(double longitude, double latitude){
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public Position(String str) throws CommunicationException{
		String[] parse = str.split(",");
		if(parse.length != 2){
			throw new CommunicationException("Impossible de déduire une position à partir de « "+str+" ».");
		}
		this.longitude = Double.parseDouble(parse[0]);
		this.latitude = Double.parseDouble(parse[1]);
		
	}
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longit) {
		this.longitude = longit;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latit) {
		this.latitude = latit;
	}
	
	@Override
	public boolean isNear(PositionInterface p, double rayon) {
		 int R = 6371; // Radius of the earth in km
		  double dLat = deg2rad(this.latitude-p.getLatitude());  // deg2rad below
		  double dLon = deg2rad(this.longitude-p.getLongitude()); 
		  double a = 
		   (Math.sin(dLat/2) * Math.sin(dLat/2) +
		    Math.cos(deg2rad(this.latitude)) * Math.cos(deg2rad(p.getLatitude())) * 
		    Math.sin(dLon/2) * Math.sin(dLon/2)); 
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		  double d = R * c; // Distance in km
		  return (d<rayon) ; 
	}
	
	public boolean isNear2(PositionInterface p, double rayon){
		double PI = 3.14159265358979323846;
				double Lat1 = latitude * PI / 180;
				double Lon1 = longitude * PI / 180;
				double Lat2 = p.getLatitude() * PI / 180;
				double Lon2 = p.getLongitude() * PI / 180;
				double D = 6378.137 * Math.acos(Math.cos( Lat1 ) * Math.cos( Lat2 ) * Math.cos( Lon2 - Lon1 ) + Math.sin( Lat1 ) * Math.sin( Lat2 ) );
				return(D<rayon); 
	}

	public double deg2rad(double d) {
		return (float) (d * (Math.PI/180)); 
	}	
	
	public String toString(){
		return longitude+","+latitude;
	}
	


}
