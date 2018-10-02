package com.example.briceveyredesoras.bubble_ev;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Client;
import com.example.briceveyredesoras.bubble_ev.common.CompteUser;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;

import java.io.IOException;

public class RegisterActivity extends Activity{
    EditText ETpseudo , ETmail , ETpassword , ETconfirmpassword;
    Button boutonCreerCompte;
    StockageDataUtilisateur userLocalDatabase;
    int premium;

    RadioGroup radio_g;
    RadioButton radio_b;

    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent i = getIntent();
        setContentView(R.layout.activity_register);


        ETpseudo = (EditText) findViewById(R.id.ETpseudo);
        ETmail = (EditText) findViewById(R.id.ETmail);
        ETpassword = (EditText) findViewById(R.id.ETpassword);
        ETconfirmpassword = (EditText) findViewById(R.id.ETconfirmpassword);
        boutonCreerCompte = (Button) findViewById(R.id.boutonCreerCompte);
        radio_g = (RadioGroup) findViewById(R.id.premium);


    }


    //Quand on click sur le bouton creer les informations utilisateur sont stockés dans un SharedPreference
    //On stocke aussi un boolean pour savoir si il est enregistré ou non.
    public void onClickCreer(View view) {
        //-----------------code de cryptage du mot de passe -------------------------------------
        /*
        EditText password1 = (EditText) findViewById(R.id.ETloginPassword);
        String password = password1.getText().toString();
        final int shift_key = 4; //it is the shift key to move character, like if i have 'a' then a=97+4=101 which =e and thus it changes
        char character;
        char ch[]=new char[password.length()];//for storing encrypt char
        for (int iteration = 0; iteration < password.length(); iteration++)
        {
            character = password.charAt(iteration); //get characters
            character = (char) (character + shift_key); //perform shift
            ch[iteration]=character;//assign char to char array
        }
        String encryptstr = String.valueOf(ch);//converting char array to string
        System.out.println(encryptstr);

        for(int i=0;i<encryptstr.length();i++)
        {
            character=encryptstr.charAt(i);
            character = (char) (character -shift_key); //perform shift
            ch[i]=character;
        }
        String decryptstr = String.valueOf(ch);
        System.out.println("Resultat : "+ decryptstr);
        //----------------------------fin du code de cryptage ----------------------------------------
        */

        final String pseudo = ETpseudo.getText().toString();

        int selected_id = radio_g.getCheckedRadioButtonId();
        radio_b = (RadioButton) findViewById(selected_id);
        String rep = radio_b.getText().toString();

        if (rep.equals("Oui")) {
            premium = 1;
        }
        if (rep.equals("Non")) {
            premium = 0;
        } else {
            premium = 0;
        }

        if (5 < pseudo.length() && pseudo.length() < 15) {
            final String mail = ETmail.getText().toString();
            if (isValidEmail(mail)) {
                final String password = ETpassword.getText().toString();
                String confirmpassword = ETconfirmpassword.getText().toString();

                //-----------------code de cryptage du mot de passe -------------------------------------

                final int shift_key = 4; //it is the shift key to move character, like if i have 'a' then a=97+4=101 which =e and thus it changes
                char character;
                char ch[] = new char[password.length()];//for storing encrypt char
                for (int iteration = 0; iteration < password.length(); iteration++) {
                    character = password.charAt(iteration); //get characters
                    character = (char) (character + shift_key); //perform shift
                    ch[iteration] = character;//assign char to char array
                }
                String encryptstr = String.valueOf(ch);//converting char array to string

                //----------------------------fin du code de cryptage ----------------------------------------

                if (password.equals(confirmpassword)) {
                    if (5 < pseudo.length() && pseudo.length() < 15) {
                        final CompteUser compteUser = new CompteUser(pseudo, mail, encryptstr, premium);
                        //serveur
                        userLocalDatabase = new StockageDataUtilisateur(this);
                        userLocalDatabase.StockerUserData(compteUser, true);
                        //-----------serveur-------------------------------
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                if (MainActivity.cli.createUser(compteUser)) {
                                    System.out.println("c'est bon");
                                }

                            }
                        }).start();

                        Intent i = new Intent(this, ListOfEventsActivity.class);
                        startActivity(i);
                    } else {
                        ETpassword.setText("");
                        ETconfirmpassword.setText("");
                        Toast.makeText(getApplicationContext(), "Votre mot de passe doit etre d'une longueur entre 5 et 15 caractères", Toast.LENGTH_SHORT).show();


                    }


                } else {
                    ETpassword.setText("");
                    ETconfirmpassword.setText("");
                    Toast.makeText(getApplicationContext(), "Mots de passe différents", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getApplicationContext(), "Adresse email non valide", Toast.LENGTH_SHORT).show();
                ETmail.setText("");
            }


        } else {
            ETpseudo.setText("");
            Toast.makeText(getApplicationContext(), "Votre pseudo doit etre d'une longueur entre 5 et 15 caractères", Toast.LENGTH_SHORT).show();
        }

        new MyAsyncTask().execute();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        boolean bool = false;
        @Override
        protected void onPreExecute() {
            findViewById(R.id.layoutRegister).setVisibility(View.GONE);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            final Position position = new Position(0, 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    appLocationService = new AppLocationService(RegisterActivity.this);
                    final Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER,
                            getApplicationContext(), RegisterActivity.this);


                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        position.setLatitude(latitude);
                        position.setLongitude(longitude);
                        MainActivity.myPosition = position;
                        System.out.println(position);
                        bool = true;

                    } else {
                        showSettingsAlert();
                    }
                }
            });

            while(bool == false){

            }

            if(bool){
                Intent intent = new Intent(getApplicationContext(),ListOfEventsActivity.class);
                MainActivity.listOfEvents = MainActivity.cli.getNearEvents(position);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(bool == true){
                Toast.makeText(getApplicationContext(), "Compte Crée", Toast.LENGTH_SHORT).show();

            }

        }

    }
    public void showSettingsAlert() {
        Toast.makeText(getApplicationContext(), "impossible de Localiser", Toast.LENGTH_SHORT).show();
        showMessage3("Allez dans :" + "\n" + "-> Paramètres" + "\n" + "-> Localisation" + "\n" + "-> Activer" + "\n" + "Attendez 20 secondes puis réessayez", "Avez vous activé la localisation ?");
    }

    private void showMessage3(String message, String title) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
