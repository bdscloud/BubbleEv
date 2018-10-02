package com.example.briceveyredesoras.bubble_ev;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;
import com.example.briceveyredesoras.bubble_ev.gpslocation.LocationAddress;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.briceveyredesoras.bubble_ev.R.drawable.map_marker;

public class MapsActivity extends FragmentActivity {

    final SimpleDateFormat dayFormatter = new SimpleDateFormat("dd"); // output day
    final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM"); // output month
    final SimpleDateFormat hourFormatter = new SimpleDateFormat("HH"); // output hour
    final SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm"); // output hour

    AppLocationService appLocationService;
    Bitmap bitmap;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        appLocationService = new AppLocationService(MapsActivity.this);
        setUpMapIfNeeded();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Je verifie que la localisation est activée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSettingsAlert();
            return;
        }
        LatLng latLng;
        //je recupere les données gps pour localiser le telephone
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER, getApplicationContext(), MapsActivity.this);
        if (gpsLocation != null) {
            mMap.setMyLocationEnabled(true);
            MainActivity.myPosition.setLatitude(gpsLocation.getLatitude());
            MainActivity.myPosition.setLongitude(gpsLocation.getLongitude());
            latLng = new LatLng(gpsLocation.getLatitude(),gpsLocation.getLongitude());
        } else {
            showSettingsAlert();
            latLng = new LatLng(0,0);
        }
        //Je redimensionne le marker qui est customisé
        bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.map_marker);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,100,100,true);

        //Je dispose tous les markers qui representent les evenements sur la carte en mettant titre + mots clefs
        for(Evenement event : ListOfEventsActivity.eventArrayList){



            MarkerOptions options = new MarkerOptions()
                    .title(event.getName())
                    .position(new LatLng(event.getPosition().getLatitude(), event.getPosition().getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(resized));
            options.snippet(event.getDescription());

            mMap.addMarker(options);
            float zoomLevel = (float) 13.0; //goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            //Je rend chaque titre de marker clickable pour avoir une fenetre d'infos supplementaires.
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    Evenement evenement = null;
                    for(Evenement event : ListOfEventsActivity.eventArrayList){
                        if (event.getName().equals(marker.getTitle())){
                            evenement = event;
                        }
                    }

                    Intent intent = new Intent(MapsActivity.this,PopUpEvent.class);
                    ArrayList<String> descriptif = new ArrayList<String>();
                    String nom = evenement.getName();
                    String description = evenement.getDescription();
                    Date debut = evenement.getDateEVStart();
                    Date fin = evenement.getDateEVEnd();
                    String strdebut = dayFormatter.format(evenement.getDateEVStart()) + " " + monthFormatter.format(evenement.getDateEVStart()) + " " +
                            hourFormatter.format(evenement.getDateEVStart()) + ":" + minuteFormatter.format(evenement.getDateEVStart());
                    String strfin = dayFormatter.format(evenement.getDateEVEnd()) + " " + monthFormatter.format(evenement.getDateEVEnd()) + " " +
                            hourFormatter.format(evenement.getDateEVEnd()) + ":" + minuteFormatter.format(evenement.getDateEVEnd());
                    String posteur = evenement.getPosteur();
                    String distance = String.valueOf(getdistance(MainActivity.myPosition, (Position) evenement.getPosition()));
                    //ListOfEvents.eventbitmap = evenement.getBitmap();
                    descriptif.add(nom);
                    descriptif.add(description);
                    descriptif.add(strdebut);
                    descriptif.add(strfin);
                    descriptif.add(posteur);
                    descriptif.add(distance);
                    intent.putStringArrayListExtra("descriptif", descriptif);
                    startActivity(intent);
                } });


        }


    }

    public void showSettingsAlert() {
        Toast.makeText(getApplicationContext(),"impossible de Localiser",Toast.LENGTH_SHORT).show();
        showMessage3("Allez dans :" + "\n" + "-> Paramètres" + "\n" + "-> Localisation" + "\n" + "-> Activer" + "\n" + "Attendez 20 secondes puis réessayez", "Avez vous activé la localisation ?");
    }

    private void showMessage3(String message, String title){
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


    private float getdistance(Position position1 , Position position2) {
        double lat1 = position1.getLatitude();
        double lng1 = position1.getLongitude();
        double lat2 = position2.getLatitude();
        double lng2 = position2.getLongitude();
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist/1000;
    }
}