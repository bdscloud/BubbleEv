package com.example.briceveyredesoras.bubble_ev.common.exceptions;

import com.example.briceveyredesoras.bubble_ev.common.interfaces.CompteUserInterface;

public class LoginException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LoginException(CompteUserInterface user, String msg){
		super(msg+"Compte : pseudo:"+user.getPseudo()+", id:"+user.getId());
	}

}
