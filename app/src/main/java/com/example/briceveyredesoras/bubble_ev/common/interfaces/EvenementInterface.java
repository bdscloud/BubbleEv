package com.example.briceveyredesoras.bubble_ev.common.interfaces;

import java.util.ArrayList;
import java.util.Date;

public interface EvenementInterface 
{	
	public int getId();
	public void setId(int id);
	
	public String getName();
	public void setName(String name);
	
	public String getPosteur();
	public void setPosteur(String pseudo);
	
	public String getDescription();
	public void setDescription(String msg);
	
	public PositionInterface getPosition();
	public void setPosition(PositionInterface p);
	
	/* Date de la publication de l'evenement */
	public Date getDatePoste();
	public void setDatePoste(Date dPoste);
	
	/* Date à laquelle débute l'evenement */
	public Date getDateEVStart();
	public void setDateEVStart(Date dEVStart);
	
	/* Date à laquelle finit l'evenement */
	public Date getDateEVEnd();
	public void setDateEVEnd(Date dEVEnd);

	/* Contenu multimedia. */
	public boolean addMultimedia(String fileName);
	public ArrayList<String> getAllMultimedia();

	/* Nombre de "j'aime" sur l'evenement */
	public boolean addLike();
	public boolean removeLike();
	public int nbLike();
}
