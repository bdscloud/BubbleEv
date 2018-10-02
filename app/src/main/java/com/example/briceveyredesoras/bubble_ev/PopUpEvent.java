package com.example.briceveyredesoras.bubble_ev;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PopUpEvent extends Activity {

    TextView nom ;
    TextView debut ;
    TextView fin ;
    TextView description;
    TextView pseudo ;
    TextView distance;
    TextView adresse;
    ArrayList<String> descriptif;
    //ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.7) ,(int) (height*.7));

        Intent i = getIntent();
        descriptif = i.getStringArrayListExtra("descriptif");

        nom = (TextView) findViewById(R.id.SETnom);
        debut = (TextView) findViewById(R.id.SETdateDebut);
        fin = (TextView) findViewById(R.id.SETdateFin);
        description = (TextView) findViewById(R.id.SETdescription);
        pseudo = (TextView) findViewById(R.id.SETpseudo);
        distance = (TextView) findViewById(R.id.SETkm);
        adresse = (TextView) findViewById(R.id.SETadresse);
        //image = (ImageView) findViewById(R.id.SETphoto);

        nom.setText(descriptif.get(0));
        description.setText(descriptif.get(1));
        debut.setText(descriptif.get(2));
        fin.setText(descriptif.get(3));
        pseudo.setText(descriptif.get(4));
        distance.setText(descriptif.get(5));
        adresse.setText(descriptif.get(6));
        //image.setImageBitmap(ListOfEventsActivity.eventBitmap);

    }
}
