package com.example.briceveyredesoras.bubble_ev.common.interfaces;

import com.example.briceveyredesoras.bubble_ev.common.exceptions.NewUserException;

import java.util.ArrayList;

import com.example.briceveyredesoras.bubble_ev.common.*;

public interface BDDInterface {

	
	public Boolean login(String mail, String pass); //needed by Serveur.java
	public void newUser(CompteUserInterface user) throws NewUserException; //needed by Serveur.java
	public void newEvent(EvenementInterface ev); //needed by Serveur.java
	public CompteUser getThisUser(String posteurPseudo);
	public ArrayList<Evenement> getNearEvents(PositionInterface pos);
	
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p);
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p, String pref);
//	
//	public boolean newEV(EvenementInterface EvtName);
//	
//	/* renvoie le contenu multimedia associé a un Evenement : chemin décrivant le fichier */
//	public ArrayList<String> getAllPicture(EvenementInterface ev);
//	public ArrayList<String> getAllMusic(EvenementInterface ev);
//	public ArrayList<String> getAllVideo(EvenementInterface ev);
}
