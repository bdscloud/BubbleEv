package com.example.briceveyredesoras.bubble_ev.common.interfaces;

import com.example.briceveyredesoras.bubble_ev.common.exceptions.NewEventException;
import com.example.briceveyredesoras.bubble_ev.common.exceptions.NewUserException;

import java.util.ArrayList;
import java.util.Date;

import com.example.briceveyredesoras.bubble_ev.common.CompteUser;
import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.exceptions.*;

public interface ServeurInterface {
	
	public void quit();
	
	public boolean login(String compte, String passe);
	public void newUser(CompteUser user) throws NewUserException;
	public void newEvent(Evenement ev) throws NewEventException;

	
//	/*
//	 * Appelle de la fonction modération
//	 */
//	public boolean imageConforme(char[][][] ima);
//	
//	/*
//	 * Appelle de la fonction détection de doublon
//	 */
//	public boolean existe(EvenementInterface ev);
//	
//	/*
//	 * Appelle de la fonction fusion de doublon
//	 */
//	public boolean fusionEv(EvenementInterface ev1, EvenementInterface ev2);
//	
//	public ArrayList<EvenementInterface> getAllEventNear(PositionInterface p);
//	
//	/***************************************
//	 * Retourne une valeurs de l'evenement *
//	 ***************************************/
//	
//	public int getId(EvenementInterface ev);
//	
//	public String getName(EvenementInterface ev);
//
//	public CompteUserInterface getUser(EvenementInterface ev);
//	
//	public String getDescription(EvenementInterface ev);
//	
//	public PositionInterface getPosition(EvenementInterface ev);
//	
//	/* Date du poste de l'evenement */
//	public Date getDatePoste(EvenementInterface ev);
//	
//	/* Date à laquel débute l'evenement */
//	public Date getDateEVStart(EvenementInterface ev);
//	
//	/* Date à laquel fini l'evenement */
//	public Date getDateEVEnd(EvenementInterface ev);	
//
//	/* renvoie le contenue multimedia associé a un Evenement */
//	public ArrayList<String> getAllPicture(EvenementInterface ev);
//	public ArrayList<String> getAllMusic(EvenementInterface ev);
//	public ArrayList<String> getAllVideo(EvenementInterface ev);
//
//	public int nbLike(EvenementInterface ev);
//	
//	
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p);
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p, String pref);

}

