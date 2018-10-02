package com.example.briceveyredesoras.bubble_ev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Arnaud on 01/04/2016.
 */
public class ChangeMailActivity extends Activity {
    //Client cli = null ;
    Button bChangeMail;
    EditText etNewMail, etNewMailAgain, etPassword;
    StockageDataUtilisateur stockageDataUtilisateur;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemail);



        bChangeMail = (Button) findViewById(R.id.bChangeMail);
        etPassword = (EditText) findViewById(R.id.etMPassword);
        etNewMail = (EditText) findViewById(R.id.etNewMail);
        etNewMailAgain = (EditText) findViewById(R.id.etNewMailAgain);

        stockageDataUtilisateur = new StockageDataUtilisateur(this);

    }

    public void onClickChangePassword(View view) {

        final String newMail = etNewMail.getText().toString();
        final String password = etPassword.getText().toString();
        String newMailAgain = etNewMailAgain.getText().toString();

        final String pseudo = stockageDataUtilisateur.getLoggedInUserPseudo(); //On devra récuperer le nom du client en cours. Quand le stock t-on ? à la connection ?


        if (isValidEmail(newMail)){



            if (newMail.equals(newMailAgain)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*try {

                            cli = new Client("192.168.43.87", 1234);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                        if (MainActivity.cli.changeMail(pseudo, password, newMail)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("mail changé");
                                    Toast.makeText(getApplicationContext(), "Adresse email modifiée avec succès", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ParametersActivity.class);
                                    //Intent intent = new Intent(this, ParametersActivity.class); // ne fonctionne pas
                                    startActivity(intent);
                                }
                            });

                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("c'est pas bon");
                                    etNewMail.setText("");
                                    Toast.makeText(getApplicationContext(), "Erreur, le mot de passe entré est incorrect", Toast.LENGTH_SHORT).show();
                                    etNewMailAgain.setText("");
                                    etPassword.setText("");
                                }
                            });

                        }
                    }
                }).start();
            }

            else {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer deux adresses mail identiques", Toast.LENGTH_SHORT).show();
                etNewMailAgain.setText("");
                etNewMail.setText("");
                etPassword.setText("");
            }

        }
        else {
            Toast.makeText(getApplicationContext(), "adresse email non valide", Toast.LENGTH_SHORT).show();
            etNewMailAgain.setText("");
            etNewMail.setText("");
            etPassword.setText("");
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
