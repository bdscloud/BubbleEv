package com.example.briceveyredesoras.bubble_ev.common.interfaces;

public interface ModerationInterface {

	/* -1 : on ne sait pas 
	 * 0 : ne contient pas
	 * 1 : contient */
	public int contientNudite(String cheminImg);
	public int contientPeau(String cheminImg);
}
