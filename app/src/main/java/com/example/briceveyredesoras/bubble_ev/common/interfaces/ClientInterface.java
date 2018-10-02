package com.example.briceveyredesoras.bubble_ev.common.interfaces;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.example.briceveyredesoras.bubble_ev.common.*;
import com.example.briceveyredesoras.bubble_ev.common.interfaces.*;

public interface ClientInterface
{

	public boolean createUser(CompteUserInterface user);
	public boolean login(String NomDeCompte, String motDePasse) throws IOException; //ajouter exception si erreur de login

	public ArrayList<Evenement> getNearEvents(Position position);
	
//	public boolean logout();
//	public boolean modifyUser(CompteUserInterface user);
//	
	public Boolean createEvent(EvenementInterface ev);
//	
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p);
//	public ArrayList<EvenementInterface> getNearEv(String instruction, PositionInterface p, String pref);
//	public void displayList(ArrayList<EvenementInterface> listEv);
//	public void displayMap(ArrayList<EvenementInterface> listEv);
//	
//	public PositionInterface getPositionActuel();
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
	public void disconnect() throws IOException;
	public void stopServer() throws IOException;
	String fibo(int n) throws IOException;


	byte[] getThumbnail(String NomEvent) throws IOException;
}

