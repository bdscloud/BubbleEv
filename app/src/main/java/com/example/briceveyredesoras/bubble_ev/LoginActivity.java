package com.example.briceveyredesoras.bubble_ev;


import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Client;
import com.example.briceveyredesoras.bubble_ev.common.CompteUser;
import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class LoginActivity extends Activity {

    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent intent = getIntent();

        setContentView(R.layout.activity_login);

    }

    public void onClickConnection(View view) throws InterruptedException {
        //---------Verifier si le mot de pass et l'email corresponde avec le serveur---------------

        new MyAsyncTask().execute();


    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        EditText pseudo1 = (EditText) findViewById(R.id.ETloginPseudo);
        EditText password1 = (EditText) findViewById(R.id.ETloginPassword);
        final String pseudo = pseudo1.getText().toString();
        final String password = password1.getText().toString();
        boolean bool = false;
        boolean bool2 = false;
        @Override
        protected void onPreExecute() {
            findViewById(R.id.layoutLogin).setVisibility(View.GONE);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            final Position position = new Position(0, 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    appLocationService = new AppLocationService(LoginActivity.this);
                    final Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER,
                            getApplicationContext(), LoginActivity.this);


                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        position.setLatitude(latitude);
                        position.setLongitude(longitude);
                        MainActivity.myPosition = position;
                        System.out.println(position);
                        bool2 = true;

                    } else {
                        showSettingsAlert();
                    }
                }
            });

            try {

                //-----------------code de cryptage du mot de passe -------------------------------------

                final int shift_key = 4; //it is the shift key to move character, like if i have 'a' then a=97+4=101 which =e and thus it changes
                char character;
                char ch[]=new char[password.length()];//for storing encrypt char

                for(int iteration = 0; iteration < password.length(); iteration++)
                {
                    character = password.charAt(iteration); //get characters
                    character = (char) (character + shift_key); //perform shift
                    ch[iteration]=character;//assign char to char array
                }
                String encryptstr = String.valueOf(ch);//converting char array to string

                //----------------------------fin du code de cryptage ----------------------------------------

                bool = MainActivity.cli.login(pseudo,encryptstr);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                Toast.makeText(getApplicationContext(), "Connexion réussie", Toast.LENGTH_SHORT).show();

            }

            if(bool ==false){
                Toast.makeText(getApplicationContext(),"Le pseudo et le mot de passe ne correspondent pas",Toast.LENGTH_SHORT).show();

            }
        }

    }
    public void showSettingsAlert() {
        Toast.makeText(getApplicationContext(), "impossible de Localiser", Toast.LENGTH_SHORT).show();
        showMessage3("Allez dans :" + "\n" + "-> Paramètres" + "\n" + "-> Localisation" + "\n" + "-> Activer" + "\n" + "Attendez 20 secondes puis réessayez", "Avez vous activé la localisation ?");
    }

    private void showMessage3(String message, String title) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


}
