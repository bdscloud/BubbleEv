package com.example.briceveyredesoras.bubble_ev.common;

import com.example.briceveyredesoras.bubble_ev.common.interfaces.CompteUserInterface;

public class CompteUser implements CompteUserInterface {
	private int id;
	private String pseudo;
	private String mail;
	private String password;
	private int premium;
	
	public CompteUser(String pseudo, String mail, String pass, int premium){
		this.pseudo = pseudo ;
		this.mail = mail;
		this.password = pass;
		this.premium=premium;
	}
	
	public CompteUser(int id, String pseudo, String mail, String pass, int premium){
		this.id = id;
		this.pseudo = pseudo ;
		this.mail = mail;
		this.password = pass;
		this.premium=premium;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPremium() {
		return premium;
	}
	public  void setPremium(int premium) {
		this.premium = premium;
	}

	public String toString(){
		return (pseudo+" "+mail+" "+password+" "+premium+ " "+id);
	}


}
