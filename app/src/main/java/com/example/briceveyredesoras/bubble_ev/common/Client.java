package com.example.briceveyredesoras.bubble_ev.common;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



import com.example.briceveyredesoras.bubble_ev.common.exceptions.CommunicationException;
import com.example.briceveyredesoras.bubble_ev.common.interfaces.ClientInterface;
import com.example.briceveyredesoras.bubble_ev.common.interfaces.CompteUserInterface;
import com.example.briceveyredesoras.bubble_ev.common.interfaces.EvenementInterface;


public class Client implements ClientInterface {
	Socket socket;
	DataOutputStream os;
	DataInputStream is;
	public static final String PARSAGE = "<SPLIT HERE>";
	public static final String PARSAGE_EV = "<SPLIT EVENT HERE>";

	public Client(String addrServ, int port) throws IOException{
		print("Début de connexion");
		socket = new Socket(addrServ, port);
		print("Connexion réussie !");
		os = new DataOutputStream(socket.getOutputStream());
		is = new DataInputStream(socket.getInputStream()) ;
	}


	private void print(String msg){
		System.out.println("*** Client : \n\t"+msg);
	}


	private void sendText(String msg) throws IOException{
		//On envoie :
		print("Envoi d'une requête texte au serveur « "+msg+" ».");
		try {
			os.writeUTF(msg); 
		} catch (IOException e) {
			print("Serveur injoignable.");
			e.printStackTrace();
		} 
		
		//Signal de fin de message
		print("J'ai fini de parler au serveur.");

	}
	
	private String receiveText() throws IOException{
		//On récupère les données :
		String reponse = is.readUTF();
		print("Le serveur a fini de me parler. Réponse reçue : "+reponse);

		//Affichage du message reçu
		String[] reponseCut = reponse.split(PARSAGE);
		String argsMsg = "" ;
		for(String arg:reponseCut){
			argsMsg += "["+arg+"]";
		}
		print("Parsage : "+argsMsg);
		
		
				return reponse;
	}
	
	private byte[] receiveImage() throws IOException{
		/* On lit jusqu'à l'octet 29 (octet de séparation), on aura "Image taille"
		 *  on récupère la taille
		 *  on lit [taille] octets
		 */
		
		
		//On récupère les données :
		Byte lecture;
		ArrayList<Byte> bytesList = new ArrayList<Byte>();
		try {
			lecture = (byte)is.read();	
			while(lecture != 29){
				bytesList.add(lecture);;
				lecture = (byte)is.read();
				}
			} catch (IOException e) {
			print("Impossible de lire des données du serveur.");
			e.printStackTrace();
			throw e;
			}
		byte[] bytesArray = new byte[bytesList.size()];
		for(int i = 2; i<bytesList.size(); i++) {
			bytesArray[i-2] = (byte)bytesList.get(i);
		}
		String header = new String(bytesArray, "UTF-8");
		print("Réception du header terminée : « "+header+" ».");
		
		String[] parsage = header.split(Client.PARSAGE);
		if(! parsage[0].equals("Image")){
			print("Erreur lors de la réception de l'image");
			throw new IOException("Problème lors de la réception de l'image");
		}
		
		int imgLength = Integer.parseInt(parsage[1]);
		byte[] img = new byte[imgLength] ;
		is.read(img);
		return img;
}

	@Override
	public void stopServer(){
		//Fin de la connexion :
		try {
			sendText("StopServer");
			is.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			print("Le serveur est injoignable, peut-être est-il éteint ou vous n'êtes pas connecté au réseau ?");
			e.printStackTrace();
		}
		print("Fin de connexion");
	}
	
	@Override
	public void disconnect(){
		//Fin de la connexion :
		try {
			sendText("Disconnect");
			is.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			print("Le serveur est injoignable, peut-être est-il éteint ou vous n'êtes pas connecté au réseau ?");
			e.printStackTrace();
		}
		print("Fin de connexion");
	}

	@Override
	public String fibo(int n) throws IOException{
		sendText("Fibo"+PARSAGE+n);
		String received = receiveText();
		String[] cut = received.split(PARSAGE);
		print("Résultat de fibo("+n+") : "+cut[1]);
		return cut[1];
	}

	public boolean login(String NomDeCompte, String motDePasse) throws IOException{
		String message = "Login" +PARSAGE + NomDeCompte +PARSAGE + motDePasse ;
		sendText(message) ;
		String reponse = receiveText();
		String[] cut = reponse.split(PARSAGE);
		if(cut[0].equals("Error")){
			return false;
		}
		else if(cut[0].equals("LoginOK")){
			return true;
		}
		return false;
	}


	public boolean createUser(CompteUserInterface user) {
		String message="NewUser";

		message += PARSAGE + user.getPseudo() + PARSAGE + user.getMail() + PARSAGE ;
		message += user.getPassword() + PARSAGE +user.getPremium() ;
		try {
			sendText(message);
			String rep = receiveText();
			switch(rep) {
			case "NewUserOK": 
				return true ;
			case "NewUserError" :
				return false;
			default :
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	

	public Boolean createEvent(EvenementInterface ev) {
		String message="NewEvent";

		message=message + PARSAGE + ev.getName() + PARSAGE + ev.getDescription() + PARSAGE ;
		String strPosition = ev.getPosition().toString();
		message = message + strPosition + PARSAGE ;
		String strDatePoste = dateToString(ev.getDatePoste());
		String strDateEVStart = dateToString(ev.getDateEVStart());
		String strDateEVEnd = dateToString(ev.getDateEVEnd());
		message=message+ strDatePoste +PARSAGE+ strDateEVStart +PARSAGE+ strDateEVEnd +PARSAGE;
		String pseudo = ev.getPosteur() ;
		message += pseudo +PARSAGE;
		
		String rep = null ;
		try {
			 sendText(message);
			 rep = receiveText();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		switch(rep) {
		case "NewEventOK": 
			return true ;
		case "NewEventError" :
			return false;
		default :
			return false;
		}
	}
	
	public ArrayList<Evenement> getNearEvents(Position position){
		String message = "GetNearEvent" + PARSAGE + position.toString() ;
		String rep = null ;
		try {
			sendText(message);
			rep = receiveText();			
		} catch (IOException e) {
			e.printStackTrace();
			print("Impossible d'envoyer des données au serveur.");
		}
		ArrayList<Evenement> reponse = new ArrayList<Evenement>() ;
		String[] reponseCut = rep.split(PARSAGE);
		for (int i=1 ; i<reponseCut.length ; i++ ){
			String[] strEv = reponseCut[i].split(PARSAGE_EV);
			String name = strEv[0];
			String description = strEv[1];
			String strPosition = strEv[2];
			String strDatePoste = strEv[3];
			String strDateEVStart = strEv[4];
			String strDateEVEnd = strEv[5];
			String note = strEv[6];
			String pseudo = strEv[7];
			
			Position pos = null;
			try {
				pos = new Position(strPosition);
			} catch (CommunicationException e) {
				print(" Erreur de format pour Position !");
				e.printStackTrace();
			}
			Date datePoste = null;
			try {
				datePoste = stringToDate(strDatePoste);
			} catch (ParseException e1) {
				print("Impossible de parser la date de poste : "+strDatePoste+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DatePoste mal formattée : "+strDatePoste);
				
			}
			Date dateEVStart = null;
			try {
				dateEVStart = stringToDate(strDateEVStart);
			} catch (ParseException e1) {
				print("Impossible de parser la date de début : "+strDateEVStart+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DateEVStart mal formattée : "+strDateEVStart);	
			}
			Date dateEVEnd = null;
			try {
				dateEVEnd = stringToDate(strDateEVEnd);
			} catch (ParseException e1) {
				print("Impossible de parser la date de fin : "+strDateEVEnd+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DateEVEnd mal formattée : "+strDateEVEnd);	
			}
			int nbLike = Integer.parseInt(note);
			
			Evenement event = new Evenement(name, description, pos, datePoste, dateEVStart, dateEVEnd,nbLike, pseudo);
			reponse.add(event);
		}
		
		
		
		return reponse;
	}
	
	
	@Override
	public byte[] getThumbnail(String NomEvent) throws IOException {
		
		//Envoi de la requête
		try {
			sendText("GetThumbnail"+PARSAGE+NomEvent);
		} catch (IOException e) {
			print("Impossible d'envoyer des données au serveur");
			e.printStackTrace();
			throw e;
		}
		
		//Réception des données
		byte[] imageBytes = null;
		try {
			imageBytes = receiveImage(); //changer ça
		} catch (IOException e) {
			print("Impossible de recevoir des données du serveur");
			e.printStackTrace();
			throw e;
		}
		
		return imageBytes;
	}
	
	
	public Boolean sendImage(String nomEvent, String chemin) throws IOException{
		sendText("Image"+ PARSAGE + nomEvent); 
		String repTraitement = receiveText();		
		FileInputStream fis = null;
		String filePath = chemin;
		try {
			long length = new File(filePath).length();
			fis = new FileInputStream(filePath);
			
			byte[] b = new byte[(int) length];
			fis.read(b);
			String message = "Image"+Client.PARSAGE+ nomEvent + Client.PARSAGE+length+Client.PARSAGE;
			byte[] bytesArray = message.getBytes("UTF-8");
			String bytesString = bytesArray[0] + " " + bytesArray[1] + " " + bytesArray[2] + " ";
			print("Premiers octets du header : "+bytesString);
			os.writeUTF(message); 
			os.write((byte)29);
			os.write(b);
			os.flush();
		} catch (FileNotFoundException e) {
			print("Fichier "+filePath+".jpg introuvable");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			print("Problème d'entrée/sortie.");
			e.printStackTrace();
			throw e;
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				print("Impossible de fermer le fichier "+nomEvent+".jpg");
				e.printStackTrace();
				throw e;
			}
		}
		String rep = receiveText();	
		switch(rep) {
		case "ImageOK": 
			return true ;
		case "ImageError" :
			return false;
		default :
			return false;
		}
		
		
	}
	
	
	public boolean setLike(String NomEvent, String pseudo){
		String message = "setLike" + PARSAGE + NomEvent + PARSAGE + pseudo ;
		String rep = null ;
		try {
			sendText(message);
			rep = receiveText();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(rep=="setLikeOK"){
			return true;
		}
		if(rep=="setLikeError"){
			return false;
		}
		return false;
	}
	
	//fonction qui permet d'obtenir les mots clés à partir de la description d'un événement
	public ArrayList<String> getKeyWords(String description){
    	ArrayList<String> keyWords = new ArrayList<String>() ;
    	for(int i=0 ; i<description.length(); i++){
    		if(description.charAt(i)=='#'){
    			String keyWord = new String();
    			int compte = 1 ;
    			while((i+compte<description.length())&&(description.charAt(i+compte)!=' ')&&
    					(description.charAt(i+compte)!='.')&&(description.charAt(i+compte)!=',')&&(description.charAt(i+compte)!='!')&&
    					(description.charAt(i+compte)!='?')){
    				keyWord=keyWord+description.charAt(i+compte);
    				compte= compte+1 ;

    			}
    			keyWords.add(keyWord);
    			i=i+compte ;
    		}
    	}
    	return keyWords ;
    }
	
	public ArrayList<String> KeyWordCompletion(String debutMot){
		String message = "KeyWordCompletion" + PARSAGE + debutMot ;
		String rep = null ;
		try {
			 sendText(message);	
			 rep = receiveText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] cutRep = rep.split(PARSAGE);
		ArrayList<String> reponse = new ArrayList<String>();
		for (int i=1 ; i< cutRep.length ; i++ ){
			reponse.add(cutRep[i]);
		}
		return reponse;
	}
	
	public boolean changePassword(String Name ,String oldPassword, String newPassword) {
		String message="changePassword";

		message += PARSAGE + Name +PARSAGE + oldPassword + PARSAGE +  newPassword ;
		try {
			sendText(message);
			String rep = receiveText();
			switch(rep) {
			case "changePasswordOK": 
				return true ;
			case "changePasswordError" :
				return false;
			default :
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public boolean changeMail(String Name ,String password, String newMail) {
		String message="changeMail";

		message += PARSAGE + Name +PARSAGE + password + PARSAGE +  newMail ;
		try {
			sendText(message);
			String rep = receiveText();
			switch(rep) {
			case "changeMailOK": 
				return true ;
			case "changeMailError" :
				return false;
			default :
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean changePremium(String Name ,String password, int premium) {
		String message="changePremium";

		message += PARSAGE + Name +PARSAGE + password + PARSAGE +  premium ;
		try {
			sendText(message);
			String rep = receiveText();
			switch(rep) {
			case "changePremiumOK": 
				return true ;
			case "changePremiumError" :
				return false;
			default :
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public ArrayList<Evenement> getNearEvents(Position position, ArrayList<String> keyWords){
		String message = "GetNearEventKeyWords" + PARSAGE + position.toString() ;
		int taille = keyWords.size() ;
		for (int i=0; i<taille ;i++){
			message = message +PARSAGE + keyWords.get(i);
		}
		String rep = null ;
		try {
			sendText(message);
			rep = receiveText();			
		} catch (IOException e) {
			e.printStackTrace();
			print("Impossible d'envoyer des données au serveur.");
		}
		ArrayList<Evenement> reponse = new ArrayList<Evenement>() ;
		String[] reponseCut = rep.split(PARSAGE);
		for (int i=1 ; i<reponseCut.length ; i++ ){
			String[] strEv = reponseCut[i].split(PARSAGE_EV);
			String name = strEv[0];
			String description = strEv[1];
			String strPosition = strEv[2];
			String strDatePoste = strEv[3];
			String strDateEVStart = strEv[4];
			String strDateEVEnd = strEv[5];
			String note = strEv[6];
			String pseudo = strEv[7];
			
			Position pos = null;
			try {
				pos = new Position(strPosition);
			} catch (CommunicationException e) {
				print(" Erreur de format pour Position !");
				e.printStackTrace();
			}
			Date datePoste = null;
			try {
				datePoste = stringToDate(strDatePoste);
			} catch (ParseException e1) {
				print("Impossible de parser la date de poste : "+strDatePoste+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DatePoste mal formattée : "+strDatePoste);
				
			}
			Date dateEVStart = null;
			try {
				dateEVStart = stringToDate(strDateEVStart);
			} catch (ParseException e1) {
				print("Impossible de parser la date de début : "+strDateEVStart+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DateEVStart mal formattée : "+strDateEVStart);	
			}
			Date dateEVEnd = null;
			try {
				dateEVEnd = stringToDate(strDateEVEnd);
			} catch (ParseException e1) {
				print("Impossible de parser la date de fin : "+strDateEVEnd+". Message : "+e1.getMessage());
				e1.printStackTrace();
				//throw new CommunicationException("DateEVEnd mal formattée : "+strDateEVEnd);	
			}
			int nbLike = Integer.parseInt(note);
			
			Evenement event = new Evenement(name, description, pos, datePoste, dateEVStart, dateEVEnd,nbLike, pseudo);
			reponse.add(event);
		}
		
		
		
		return reponse;
	}
	
	
	public static String dateToString(Date d) {
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		return formatter.format(d);
	}
	
	public static Date stringToDate(String str) throws ParseException {
		//Format : "Tue Jul 10 19:50:23 2014", résultat de date.toString()
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		return formatter.parse(str);
	}
	
}
