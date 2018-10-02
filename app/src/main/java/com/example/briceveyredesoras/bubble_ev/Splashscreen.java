package com.example.briceveyredesoras.bubble_ev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

//On crée des animations dans le dossier anim
//Ensuite on execute ses animations en meme temps puis on lance l'activité main qui redirigera vers ListOfEvents

public class Splashscreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final LinearLayout l1 =(LinearLayout) findViewById(R.id.linear1);
        final LinearLayout l2 =(LinearLayout) findViewById(R.id.linear2);
        final ImageView iv = (ImageView) findViewById(R.id.splash);


        final Animation anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotation);
        final Animation anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zomein);
        final Animation anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.alpha);


        l1.startAnimation(anim1);
        l2.startAnimation(anim2);
        iv.startAnimation(anim3);

        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



}