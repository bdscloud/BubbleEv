package com.example.briceveyredesoras.bubble_ev;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.briceveyredesoras.bubble_ev.common.Client;
import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static Client cli;
    static ArrayList<Evenement> listOfEvents = new ArrayList<Evenement>();
    static Position myPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //En fonction du booleanRegistered l'utilisateur tombe directement sur la page login ou sur la page Register.
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("fichierdataUtilsateur" , Context.MODE_PRIVATE);
        //preferences.edit().clear().commit();
        Boolean restoredBoolean = preferences.getBoolean("booleanRegistered",false );

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cli = new Client("37.162.134.200", 1234);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();


        if(restoredBoolean==false) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


}
