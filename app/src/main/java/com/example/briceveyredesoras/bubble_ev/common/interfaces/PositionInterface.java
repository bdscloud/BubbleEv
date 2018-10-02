package com.example.briceveyredesoras.bubble_ev.common.interfaces;


public interface PositionInterface 
{
	public double getLongitude();
	public void setLongitude(double longitude);

	public double getLatitude();
	public void setLatitude(double latitude);
	
	/* renvoit vrai si la distance entre la position actuelle
	 * et celle entrée en paramètre (quelle unité ?) est inférieur au rayon */
	public boolean isNear(PositionInterface p, double rayon);
}
