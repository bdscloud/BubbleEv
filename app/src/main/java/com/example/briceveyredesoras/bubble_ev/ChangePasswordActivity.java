package com.example.briceveyredesoras.bubble_ev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Arnaud on 06/03/2016.
 */
public class ChangePasswordActivity extends Activity {
    //Client cli = null ;  Peut on supprimer cette ligne ??? ( n'était pas en commentaire avant )
    Button bChangePassword;
    EditText etCurrentPassword, etNewPassword, etNewPasswordAgain;
    StockageDataUtilisateur stockageDataUtilisateur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);


        bChangePassword = (Button) findViewById(R.id.bChangePassword);
        etCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etNewPasswordAgain = (EditText) findViewById(R.id.etNewPasswordAgain);

        stockageDataUtilisateur = new StockageDataUtilisateur(this);

    }

    public void onClickChangePassword(View view) {
        final String currentPassword = etCurrentPassword.getText().toString();
        /*SharedPreferences preferences = getSharedPreferences("fichierdataUtilsateur" , Context.MODE_PRIVATE);
        Boolean restoredBoolean = preferences.getString("Password");*/
       /* if (currentPassword.equals(" mot de passe actuel à trouver")){*/
        final String newPassword = etNewPassword.getText().toString();
        String newPasswordAgain = etNewPasswordAgain.getText().toString();

        //-----------------code de cryptage du mot de passe -------------------------------------

        final int shift_key = 4; //it is the shift key to move character, like if i have 'a' then a=97+4=101 which =e and thus it changes
        char character;
        char ch[]=new char[newPassword.length()];//for storing encrypt char

        for(int iteration = 0; iteration < newPassword.length(); iteration++)
        {
            character = newPassword.charAt(iteration); //get characters
            character = (char) (character + shift_key); //perform shift
            ch[iteration]=character;//assign char to char array
        }
        final String newPasswordEncryptstr = String.valueOf(ch);//converting char array to string

        char ch2[]=new char[currentPassword.length()];//for storing encrypt char

        for(int iteration = 0; iteration < currentPassword.length(); iteration++)
        {
            character = currentPassword.charAt(iteration); //get characters
            character = (char) (character + shift_key); //perform shift
            ch[iteration]=character;//assign char to char array
        }
        final String currentPasswordEncryptstr = String.valueOf(ch2);//converting char array to string


        //----------------------------fin du code de cryptage ----------------------------------------

        Boolean success;

        final String pseudo = stockageDataUtilisateur.getLoggedInUserPseudo(); //On devra récuperer le nom du client en cours. Quand le stock t-on ? à la connection ?


        if (5 <newPassword.length() & newPassword.length() < 20){



            if (newPassword.equals(newPasswordAgain)){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (MainActivity.cli.changePassword(pseudo,currentPasswordEncryptstr, newPasswordEncryptstr)){
                        System.out.println("password changé");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ParametersActivity.class);
                                startActivity(intent);
                            }
                        });

                    }
                    else{
                        System.out.println("c'est pas bon");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etCurrentPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Erreur, ceci n'est pas votre ancien mot de passe", Toast.LENGTH_SHORT).show();
                                etNewPassword.setText("");
                                etNewPasswordAgain.setText("");
                            }
                        });
                    }
                }
            }).start();
        }

        else {
            Toast.makeText(getApplicationContext(), "Veuillez rentrer deux mot de passe identiques", Toast.LENGTH_SHORT).show();
            etNewPassword.setText("");
            etNewPasswordAgain.setText("");
        }

        }
        else {
            Toast.makeText(getApplicationContext(), "Rentrez un mot de passe compris entre 6 et 20 caractères", Toast.LENGTH_SHORT).show();
            etNewPassword.setText("");
            etNewPasswordAgain.setText("");}
    }

}
