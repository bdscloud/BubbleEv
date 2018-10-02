package com.example.briceveyredesoras.bubble_ev.common.exceptions;


public class CommunicationException extends Exception{
	
private static final long serialVersionUID = 1L;
	
	public CommunicationException(String sent, String received){
		super("Sent: "+sent+", received: "+received);
	}
	
	public CommunicationException(String msgErr, String sent, String received){
		super("Msg: "+msgErr+", sent: "+sent+", received: "+received);
	}

	public CommunicationException(String received){
		super("Received: "+received);
	}
}
