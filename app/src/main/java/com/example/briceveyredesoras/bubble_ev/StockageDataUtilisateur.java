package com.example.briceveyredesoras.bubble_ev;
import com.example.briceveyredesoras.bubble_ev.common.CompteUser;

import android.content.Context;
import android.content.SharedPreferences;

public class StockageDataUtilisateur {
    public static final String sharedPNAme = "fichierdataUtilsateur";
    SharedPreferences userLocalDatabase;

    //constructeur qui permet d'avoir acces a userStockageLocal de l'exterieur de la class (autre activité)

    public StockageDataUtilisateur(Context context){
        userLocalDatabase = context.getSharedPreferences(sharedPNAme, context.MODE_PRIVATE);
    }

    //methode pour stocker les donnes de l'utilisateur sur le telephone

    public void StockerUserData(CompteUser compteUser, boolean booleanRegistered){
        SharedPreferences.Editor sharedPEditor = userLocalDatabase.edit();
        sharedPEditor.putString("Pseudo", compteUser.getPseudo());
        sharedPEditor.putString("Mail", compteUser.getMail());
        sharedPEditor.putString("Password", compteUser.getPassword());
        sharedPEditor.putInt("Premium", compteUser.getPremium());
        sharedPEditor.putBoolean("booleanRegistered", booleanRegistered);
        sharedPEditor.commit();
    }
    public String getLoggedInUserPseudo(){

        String pseudo = userLocalDatabase.getString("Pseudo", "");
        return pseudo;
    }


}
