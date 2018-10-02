package com.example.briceveyredesoras.bubble_ev.common.exceptions;

import com.example.briceveyredesoras.bubble_ev.common.Evenement;


public class NewEventException extends Exception{


	
	public NewEventException(Evenement ev, String msg){
		super(msg+" L'événement : titre:"+ev.getName()+", ajouté par : "+ev.getPosteur() );
	}

}