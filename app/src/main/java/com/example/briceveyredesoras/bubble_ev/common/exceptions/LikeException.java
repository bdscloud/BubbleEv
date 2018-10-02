package com.example.briceveyredesoras.bubble_ev.common.exceptions;

import com.example.briceveyredesoras.bubble_ev.common.CompteUser;


public class LikeException extends Exception{
	
	public LikeException(CompteUser user, String msg){
		super(msg + "l utilisateur " + user.getPseudo() + "echec du like");
	}
}
