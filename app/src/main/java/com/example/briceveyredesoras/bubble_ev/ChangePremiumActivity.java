package com.example.briceveyredesoras.bubble_ev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Arnaud on 12/04/2016.
 */
public class ChangePremiumActivity extends Activity {
    Button bChangePremium;
    EditText etPassword3;
    StockageDataUtilisateur stockageDataUtilisateur;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepremium);

        bChangePremium = (Button) findViewById(R.id.bChangePremium);
        etPassword3 = (EditText) findViewById(R.id.etPassword3);

        stockageDataUtilisateur = new StockageDataUtilisateur(this);

    }

    public void onClickPassPremium(View view) {
        final String pseudo = stockageDataUtilisateur.getLoggedInUserPseudo();
        final String pw = etPassword3.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Boolean b = MainActivity.cli.changePremium(pseudo,pw,1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (b) {
                            Toast.makeText(getApplicationContext(), "Vous êtes bien en train de passer compte premium", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ParametersActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Désolé, une erreur est survenue lors du passage au compte premium", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).start();
    }
}

