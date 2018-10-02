package com.example.briceveyredesoras.bubble_ev;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Client;
import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;
import com.example.briceveyredesoras.bubble_ev.gpslocation.LocationAddress;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PostEventActivity2 extends FragmentActivity {
    private static final int CAMERA_REQUEST = 10;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int GALLERY_REQUEST = 20;
    private static String logtag = "CameraBubbleEV";
    private Uri imageUri;
    static String path;

    AppLocationService appLocationService;


    private Button boutonPhoto;
    private Button boutonChoisirDebut;
    private Button boutonChoisirFin;
    private Button boutonCreerEvenement;

    private EditText nom;
    private EditText motsclefs;
    private EditText description;
    private EditText heureDebut;
    private EditText heureFin;
    private TextView lieu;
    private RadioGroup radioGroup;
    private RadioButton radioboutonOui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_post2);

        nom = (EditText) findViewById(R.id.ETnomEV);
        motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
        description = (EditText) findViewById(R.id.ETdescriptionEV);
        heureDebut = (EditText) findViewById(R.id.ETdateDebut);
        heureFin = (EditText) findViewById(R.id.ETdateFin);

        Intent i = getIntent();

        int activity_number = i.getIntExtra("activity" , 0);
        if(activity_number==1){
            nom.setText(PostEventActivity.strNomEv);
            motsclefs.setText(PostEventActivity.strMotsclefs);
            description.setText(PostEventActivity.strDescriptionEv);
            heureDebut.setText(PostEventActivity.strheureDebut);
            heureFin.setText(PostEventActivity.strheureFin);
        }
        if(activity_number==3){
            nom.setText(PostEventActivity3.strNomEv3);
            motsclefs.setText(PostEventActivity3.strMotsclefs3);
            description.setText(PostEventActivity3.strDescriptionEv3);
            heureDebut.setText(PostEventActivity3.strheureDebut3);
            heureFin.setText(PostEventActivity3.strheureFin3);
        }

        lieu = (TextView) findViewById(R.id.lieuNewEv);
        String endroit = i.getStringExtra("endroit");
        System.out.println(endroit);
        lieu.setText(endroit);

        boutonPhoto = (Button) findViewById(R.id.boutonPhoto);
        boutonChoisirDebut = (Button) findViewById(R.id.boutonChoisirDebut);
        boutonChoisirFin = (Button) findViewById(R.id.boutonChoisirFin);
        boutonCreerEvenement = (Button) findViewById(R.id.boutonCreerEvenement);

        boutonPhoto.setOnClickListener(cameraListener);
        boutonChoisirDebut.setOnClickListener(ChoisirDebutListener);
        boutonChoisirFin.setOnClickListener(ChoisirFinListener);
        boutonCreerEvenement.setOnClickListener(CreerEvenementListener);

        appLocationService = new AppLocationService(PostEventActivity2.this);


    }


    private View.OnClickListener cameraListener = new View.OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view
            selectImage();
        }
    };

    private View.OnClickListener ChoisirDebutListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePickerDebut");
        }
    };

    private View.OnClickListener ChoisirFinListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePickerFin");
        }
    };

    private View.OnClickListener CreerEvenementListener = new View.OnClickListener() {
        public void onClick(View v) {
            //recuperation des données dans les champs textes pour creer l'evenement
            EditText nomEV = (EditText) findViewById(R.id.ETnomEV);
            EditText motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
            EditText descriptionEV = (EditText) findViewById(R.id.ETdescriptionEV);
            final String nomEv = nomEV.getText().toString();
            final String descriptionEv = descriptionEV.getText().toString()+" "+"#"+motsclefs.getText().toString();

            //recuperation des données horaires pour creer l'evenement.
            Calendar caldebut = Calendar.getInstance();
            caldebut.set(Calendar.HOUR_OF_DAY, PostEventActivity.TimePickerFragment.hdebut);
            caldebut.set(Calendar.MINUTE, TimePickerFragment.mindebut);
            final Date dateDebut = caldebut.getTime();

            Calendar calfin = Calendar.getInstance();
            calfin.set(Calendar.HOUR_OF_DAY, TimePickerFragment.hfin);
            calfin.set(Calendar.MINUTE, TimePickerFragment.minfin);
            final Date dateFin = calfin.getTime();
            final Date datePoste = new Date();

            //récuperation de la position de l'evenement.
            final Position pos = PostEventActivity.position;

            //recuperation du pseudo de l'utilisateur dans le SharedPreference pour creer l'evenement.

            SharedPreferences preferences = getSharedPreferences("fichierdataUtilsateur", Context.MODE_PRIVATE);
            final String pseudoUser = preferences.getString("Pseudo", "");

            //------------------------ envoi au serveur-----------------------------------------------------

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Evenement evenement = new Evenement(nomEv,descriptionEv,pos,dateDebut,dateFin,datePoste,pseudoUser);

                    if(MainActivity.cli.createEvent(evenement)){
                    }
                    try {
                        if(MainActivity.cli.sendImage(nomEv,path)){

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(getApplicationContext(),ListOfEventsActivity.class);
                    startActivity(intent);

                }
            }).start();

            //----------------------fin d'envoi au serveur--------------------------------------------------------------

        }
    };


    static String strNomEv2;
    static String strMotsclefs2;
    static String strDescriptionEv2;
    static String strheureDebut2;
    static String strheureFin2;

    public void onClickRadioButton(View view){


        if(view.getId()==R.id.radioButtonOui) {
            Position position = new Position(0,0);
            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER, getApplicationContext(), PostEventActivity2.this);

            if (gpsLocation != null) {

                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                position.setLatitude(latitude);
                position.setLongitude(longitude);
                MainActivity.myPosition.setLatitude(latitude);
                MainActivity.myPosition.setLongitude(longitude);
                System.out.println(position);
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

            } else {
                showSettingsAlert();
            }

        }

        if(view.getId()==R.id.radioButtonNon){

            EditText nomEV = (EditText) findViewById(R.id.ETnomEV);
            EditText motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
            EditText descriptionEV = (EditText) findViewById(R.id.ETdescriptionEV);
            EditText heureDebut = (EditText) findViewById(R.id.ETdateDebut);
            EditText heureFin = (EditText) findViewById(R.id.ETdateFin);
            strNomEv2 = nomEV.getText().toString();
            strMotsclefs2 = motsclefs.getText().toString();
            strDescriptionEv2 = descriptionEV.getText().toString();
            strheureDebut2 = heureDebut.getText().toString();
            strheureFin2 = heureFin.getText().toString();

            Intent intent = new Intent(getApplicationContext(),PostEventActivity3.class);
            intent.putExtra("activity",2);
            startActivity(intent);
        }

    }


    private void selectImage() {

        final CharSequence[] options = {"Prendre une Photo", "Choisir une Photo", "Retour"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostEventActivity2.this);
        builder.setTitle("Ajouter une Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (ActivityCompat.checkSelfPermission(PostEventActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("Vous devez autoriser l'accès au Stockage",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showMessage2("Allez dans Paramètres, applications, BubbleEv, autorisations");
                                        }
                                    });

                        }
                    }
                    ActivityCompat.requestPermissions(
                            PostEventActivity2.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );

                } else {

                    if (options[item].equals("Prendre une Photo")) {
                        // Dis au telephone qu'on veut utiliser la camera
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        // create a new temp file called pic.jpg in the "pictures" storage area of the phone
                        File photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "pic.jpg");
                        // take the return data and store it in the temp file "pic.jpg"
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                        // store the temp photo uri so we can find it later
                        imageUri = Uri.fromFile(photo);
                        path = "/storage/emulated/0/pic.jpg";
                        // start the camera
                        startActivityForResult(intent, CAMERA_REQUEST);

                    } else if (options[item].equals("Choisir une Photo")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY_REQUEST);

                    } else if (options[item].equals("Retour")) {
                        dialog.dismiss();
                    }
                }
            }


        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ){

            if (requestCode == CAMERA_REQUEST) {
                // get the image uri from earlier
                Uri selectedImage = imageUri;
                // notify any apps of any changes we make
                getContentResolver().notifyChange(selectedImage, null);
                // get the imageView
                ImageView imageView = (ImageView)findViewById(R.id.imagetest);
                // create a content resolver object which will allow us to access the image file at the uri above
                ContentResolver cr = getContentResolver();
                // create an empty bitmap object
                Bitmap bitmap;
                try {
                    // get the bitmap from the image uri using the content resolver api to get the image
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                    // set the bitmap to the image view
                    imageView.setImageBitmap(bitmap);
                    // notify the user
                    Toast.makeText(PostEventActivity2.this, selectedImage.toString(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    // notify the user
                    Toast.makeText(PostEventActivity2.this, "impossible de charger la photo", Toast.LENGTH_LONG).show();
                    Log.e(logtag, e.toString());
                }
            }

            else if(requestCode == GALLERY_REQUEST) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                path = picturePath;
                c.close();
                Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                ImageView imageview = (ImageView)findViewById(R.id.imagetest);
                imageview.setImageBitmap(bitmap);

            }

        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PostEventActivity2.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Refuser", null)
                .create()
                .show();
    }
    private void showMessage2(String message){
        new AlertDialog.Builder(PostEventActivity2.this)
                .setMessage(message)
                .show();
    }

    private void showMessage3(String message, String title){
        new AlertDialog.Builder(PostEventActivity2.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static int hdebut = PostEventActivity.TimePickerFragment.hdebut ;
        public static int mindebut = PostEventActivity.TimePickerFragment.mindebut;
        public static int hfin = PostEventActivity.TimePickerFragment.hfin;
        public static int minfin = PostEventActivity.TimePickerFragment.minfin;



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String tag = getTag();
            if(tag.equals("timePickerDebut")){
                TextView text = (TextView) getActivity().findViewById(R.id.ETdateDebut);
                text.setText("    " + format(hourOfDay) + ":" + format(minute));
                hdebut = hourOfDay;
                mindebut = minute;


            }
            else if(tag.equals("timePickerFin")){
                TextView text = (TextView) getActivity().findViewById(R.id.ETdateFin);
                text.setText("    "+format(hourOfDay)+":"+format(minute));
                hfin = hourOfDay;
                minfin = minute;
            }

        }

        public String format(int t){
            if(t<=9)
                return "0"+String.valueOf(t);
            else
                return String.valueOf(t);
        }
    }

    public void showSettingsAlert() {
        Toast.makeText(getApplicationContext(),"impossible de Localiser",Toast.LENGTH_SHORT).show();
        showMessage3("Allez dans :" + "\n" + "-> Paramètres" + "\n" + "-> Localisation" + "\n" + "-> Activer"+"\n"+"Attendez 20 secondes puis réessayez", "Avez vous activé la localisation ?");
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            lieu.setText(locationAddress);

        }
    }

}
