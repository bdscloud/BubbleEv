package com.example.briceveyredesoras.bubble_ev;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;

import java.util.Date;


public class ParametersActivity extends Activity{
    View myFragmentView;
    ListView lv;
    RelativeLayout bardubas;
    AppLocationService appLocationService;

    RelativeLayout changePassword;
    RelativeLayout changeMail;
    RelativeLayout logOut;
    RelativeLayout notifications;
    Button boutonListofEvents;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);


        Intent i = getIntent();

        changePassword = (RelativeLayout) findViewById(R.id.reChangePassword);
        changeMail = (RelativeLayout) findViewById(R.id.reChangeMail);
        logOut = (RelativeLayout) findViewById(R.id.reLogOut);
        notifications = (RelativeLayout) findViewById(R.id.reNotifications);
        boutonListofEvents = (Button) findViewById(R.id.boutonListOfEvents);
        boutonListofEvents.setOnClickListener(ListOfEventsListener);

    }

    public void onClickCP(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    public  void onClickLogOut(View view){
        //TODO
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Se déconnecter ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("Non", null)
                .show();
    }

    public void onClickChangeMail(View view){
        Intent intent = new Intent(this,ChangeMailActivity.class);
        startActivity(intent);
    }



    public void onClickNotifications(View view){

    }

    private View.OnClickListener ListOfEventsListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),ListOfEventsActivity.class);
            startActivity(intent);
        }
    };

    public void onClickSynchronize(View view) {
        new SyncAsyncTask().execute();
    }


    public void onClickMap(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

    public void onClickList(View view) {
        Intent intent = new Intent(this, ListOfEventsActivity.class);
        startActivity(intent);

    }

    public void onClickPost(View view) {
        Intent intent = new Intent(this, PostEventActivity.class);
        startActivity(intent);

    }

    public void onClickParameters(View view) {
        Intent intent = new Intent(this, ParametersActivity.class);
        startActivity(intent);
    }

    public void onClickSearch(View view) {
        Intent intent = new Intent(this, ListOfEventsActivity.class);
        startActivity(intent);
    }

    public void onClickPremiumAccount(View view) {
        Intent intent = new Intent(this,ChangePremiumActivity.class);
        startActivity(intent);
    }

    private class SyncAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean bool = false;
        @Override
        protected void onPreExecute() {
            findViewById(R.id.relativeListItems).setVisibility(View.GONE);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            final Position position = new Position(0, 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    appLocationService = new AppLocationService(ParametersActivity.this);
                    final Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER,
                            getApplicationContext(), ParametersActivity.this);


                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        position.setLatitude(latitude);
                        position.setLongitude(longitude);
                        System.out.println(position);
                        bool = true;

                    } else {
                        showSettingsAlert();
                    }
                }
            });

            while (bool == false)
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            Intent intent = new Intent(getApplicationContext(),ListOfEventsActivity.class);
            MainActivity.listOfEvents = MainActivity.cli.getNearEvents(position);
            startActivity(intent);



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(bool == true){
                Toast.makeText(getApplicationContext(), "Synchronisation réussie", Toast.LENGTH_SHORT).show();

            }

            if(bool ==false){
                Toast.makeText(getApplicationContext(),"problème de localisation",Toast.LENGTH_SHORT).show();

            }
        }

    }

    public void showSettingsAlert() {
        Toast.makeText(getApplicationContext(), "impossible de Localiser", Toast.LENGTH_SHORT).show();
        showMessage3("Allez dans :" + "\n" + "-> Paramètres" + "\n" + "-> Localisation" + "\n" + "-> Activer" + "\n" + "Attendez 20 secondes puis réessayez", "Avez vous activé la localisation ?");
    }

    private void showMessage3(String message, String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
