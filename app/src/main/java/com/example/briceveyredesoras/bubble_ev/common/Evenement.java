package com.example.briceveyredesoras.bubble_ev.common;

import android.graphics.Bitmap;

import com.example.briceveyredesoras.bubble_ev.common.interfaces.EvenementInterface;
import com.example.briceveyredesoras.bubble_ev.common.interfaces.PositionInterface;

import java.util.ArrayList;
import java.util.Date;

public class Evenement implements EvenementInterface{

	private String name;
	private String description;
	private int id;
	private PositionInterface position;
	private Date datePoste;
	private Date dateEVStart ;
	private Date dateEVEnd;
	private String posteur ;
	private int cptLike = 0;
	private ArrayList<String> listMultimedia = null;
	private int note;


	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public String getPosteur() {
		return posteur;
	}
	@Override
	public void setPosteur(String pseudo) {
		posteur = pseudo;	
	}

	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public void setDescription(String msg) {
		description = msg;
	}
	
	public PositionInterface getPosition() {
		return position;
	}

	public void setPosition(PositionInterface position) {
		this.position = position;
	}

	@Override
	public Date getDatePoste() {
		return datePoste;
	}
	@Override
	public void setDatePoste(Date dPoste) {
		datePoste = dPoste;		
	}
	@Override
	public Date getDateEVStart() {
		return dateEVStart;
	}
	@Override
	public void setDateEVStart(Date dEVStart) {
		this.dateEVStart = dEVStart ;
	}
	@Override
	public Date getDateEVEnd() {
		return dateEVEnd;
	}
	@Override
	public void setDateEVEnd(Date dEVEnd) {
		this.dateEVEnd = dEVEnd;
	}

	@Override
	public boolean addMultimedia(String fileName) {
		listMultimedia.add(fileName);
		return true;
	}
	
	public ArrayList<String> getAllMultimedia(){
		return listMultimedia;
	}

	@Override
	public boolean addLike() {
		cptLike++;
		return true;
	}
	@Override
	public boolean removeLike() {
		cptLike--;
		return true;
	}
	@Override
	public int nbLike() {
		return cptLike;
	}

	public Evenement(String name, String description, Position position, Date dateEVStart, Date dateEVEnd, Date poste, String posteur) {
		this.name = name;
		this.description = description;
		this.datePoste = poste;
		this.dateEVStart = dateEVStart;
		this.dateEVEnd = dateEVEnd;
		this.position = (PositionInterface) position;
		this.datePoste = poste;
		this.posteur = posteur;

	}

	public Evenement(String name, String description, Position position, Date dateEVStart, Date dateEVEnd, Date poste, int nbLike, String posteur) {
		this.name = name;
		this.description = description;
		this.datePoste = poste;
		this.dateEVStart = dateEVStart;
		this.dateEVEnd = dateEVEnd;
		this.position = (PositionInterface) position;
		this.datePoste = poste;
		this.cptLike = nbLike;
		this.posteur = posteur;

	}



}
