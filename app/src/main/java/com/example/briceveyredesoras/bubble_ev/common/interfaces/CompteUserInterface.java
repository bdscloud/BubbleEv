package com.example.briceveyredesoras.bubble_ev.common.interfaces;

public interface CompteUserInterface
{	
	public int getId();
	
	public String getPseudo();
	public void setPseudo(String pseudo);

	public String getMail();
	public void setMail(String mail);
	
	public String getPassword();
	public void setPassword(String password);

	public int getPremium();
	public void setPremium(int premium);

}