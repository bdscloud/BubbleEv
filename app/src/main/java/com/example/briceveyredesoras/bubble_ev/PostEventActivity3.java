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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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


public class PostEventActivity3 extends FragmentActivity {
    private static final int CAMERA_REQUEST = 10;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String path;

    private static final int GALLERY_REQUEST = 20;
    private static String logtag = "CameraBubbleEV";
    private Uri imageUri;

    EditText nom;
    EditText motsclefs;
    EditText description;
    EditText heureDebut;
    EditText heureFin;

    private Button boutonPhoto;
    private Button boutonChoisirDebut;
    private Button boutonChoisirFin;
    private Button boutonCreerEvenement;

    private LinearLayout mLayout;

    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_post3);

        mLayout = (LinearLayout) findViewById(R.id.mlayout);

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
        if(activity_number==2){
            nom.setText(PostEventActivity2.strNomEv2);
            motsclefs.setText(PostEventActivity2.strMotsclefs2);
            description.setText(PostEventActivity2.strDescriptionEv2);
            heureDebut.setText(PostEventActivity2.strheureDebut2);
            heureFin.setText(PostEventActivity2.strheureFin2);
        }




        boutonPhoto = (Button) findViewById(R.id.boutonPhoto);
        boutonChoisirDebut = (Button) findViewById(R.id.boutonChoisirDebut);
        boutonChoisirFin = (Button) findViewById(R.id.boutonChoisirFin);
        boutonCreerEvenement = (Button) findViewById(R.id.boutonCreerEvenement);

        boutonPhoto.setOnClickListener(cameraListener);
        boutonChoisirDebut.setOnClickListener(ChoisirDebutListener);
        boutonChoisirFin.setOnClickListener(ChoisirFinListener);
        boutonCreerEvenement.setOnClickListener(CreerEvenementListener);

        appLocationService = new AppLocationService(PostEventActivity3.this);

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
            EditText nomEV = (EditText) findViewById(R.id.ETnomEV);
            EditText motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
            EditText descriptionEV = (EditText) findViewById(R.id.ETdescriptionEV);
            final String nomEv = nomEV.getText().toString();
            final String descriptionEv = descriptionEV.getText().toString()+" "+"#"+motsclefs.getText().toString();
            if(positionNonNull==false){
                Toast.makeText(getApplicationContext(),"Vous n'avez pas validé d'adresse",Toast.LENGTH_SHORT).show();
                return;
            }
            final Position pos = position;

            Calendar caldebut = Calendar.getInstance();
            caldebut.set(Calendar.HOUR_OF_DAY, TimePickerFragment.hdebut);
            caldebut.set(Calendar.MINUTE, TimePickerFragment.mindebut);
            final Date dateDebut = caldebut.getTime();
            Calendar calfin = Calendar.getInstance();
            calfin.set(Calendar.HOUR_OF_DAY, TimePickerFragment.hfin);
            calfin.set(Calendar.MINUTE, TimePickerFragment.minfin);
            final Date dateFin = calfin.getTime();
            final Date datePoste = new Date();



            //--------------- On recupere le pseudo de l'utilisateur qui est enregistré sur le telephone----------------
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


    static Position position = new Position(0,0);
    static boolean positionNonNull = false;
    public void onClickValiderAdresse(View view) {

        EditText lieuEV = (EditText) findViewById(R.id.ETlieuEV);
        String lieuEv = lieuEV.getText().toString();
        LocationAddress locationAddress = new LocationAddress();
        Position pos = locationAddress.getLocationFromAddress(getApplicationContext(), lieuEv, new GeocoderHandler2());
        double lat = pos.getLatitude();
        double lng = pos.getLongitude();
        if(lat !=0 || lng !=0){
            position.setLatitude(pos.getLatitude());
            position.setLongitude(pos.getLongitude());
            System.out.println(position);
            positionNonNull=true;
        }
        else
            positionNonNull=false;


    }

    static String strNomEv3;
    static String strMotsclefs3;
    static String strDescriptionEv3;
    static String strheureDebut3;
    static String strheureFin3;

    public void onClickRadioButton(View view){


        if(view.getId()==R.id.radioButtonOui) {

            EditText nomEV = (EditText) findViewById(R.id.ETnomEV);
            EditText motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
            EditText descriptionEV = (EditText) findViewById(R.id.ETdescriptionEV);
            EditText heureDebut = (EditText) findViewById(R.id.ETdateDebut);
            EditText heureFin = (EditText) findViewById(R.id.ETdateFin);
            strNomEv3 = nomEV.getText().toString();
            strMotsclefs3 = motsclefs.getText().toString();
            strDescriptionEv3 = descriptionEV.getText().toString();
            strheureDebut3 = heureDebut.getText().toString();
            strheureFin3 = heureFin.getText().toString();

            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER, getApplicationContext(), PostEventActivity3.this);
            if (gpsLocation != null) {


                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

            } else {
                showSettingsAlert();
            }

        }

        if(view.getId()==R.id.radioButtonNon){
            return;
        }



    }


    private void selectImage() {

        final CharSequence[] options = {"Prendre une Photo", "Choisir une Photo", "Retour"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostEventActivity3.this);
        builder.setTitle("Ajouter une Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (ActivityCompat.checkSelfPermission(PostEventActivity3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                            PostEventActivity3.this,
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
                    Toast.makeText(PostEventActivity3.this, selectedImage.toString(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    // notify the user
                    Toast.makeText(PostEventActivity3.this, "impossible de charger la photo", Toast.LENGTH_LONG).show();
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
        new AlertDialog.Builder(PostEventActivity3.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Refuser", null)
                .create()
                .show();
    }
    private void showMessage2(String message){
        new AlertDialog.Builder(PostEventActivity3.this)
                .setMessage(message)
                .show();
    }

    private void showMessage3(String message, String title){
        new AlertDialog.Builder(PostEventActivity3.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static int hdebut = PostEventActivity.TimePickerFragment.hdebut;
        public static int mindebut = PostEventActivity.TimePickerFragment.mindebut;
        public static int hfin = PostEventActivity.TimePickerFragment.hfin;
        public static int minfin = PostEventActivity.TimePickerFragment.minfin;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
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
                text.setText("    "+format(hourOfDay)+":"+format(minute));
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

            Intent intent = new Intent(getApplicationContext(),PostEventActivity2.class);
            intent.putExtra("endroit", locationAddress);
            intent.putExtra("activity",3);
            startActivity(intent);

        }
    }

    //ce Geocoder est identique au precedent mais permet de gerer la validation (recuperation) de l'adresse EditText et
    //le stockage de la nouvelle adresse validée. Contrairement a l'autre qui traite le cas RadioBoutonOui et renvoie sur PostActivity2

    static boolean already_written = false;//ce boolean permet de remplacer le textview créé si besoin
    private class GeocoderHandler2 extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = "impossible de trouver l'adresse";
            }

            TextView add = createNewTextView(locationAddress,16);
            if(already_written==false){
                mLayout.setVisibility(View.VISIBLE);
                mLayout.addView(add);
                already_written = true;
            }
            else{
                add = (TextView) mLayout.getChildAt(0);
                add.setText(" "+locationAddress);
            }


        }
    }
    private TextView createNewTextView(String text,int size) {
        final AbsListView.LayoutParams lparams = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(" " + text);
        textView.setTextSize(size);
        textView.setTypeface(null,2);
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.bordergrey);
        rel.setVisibility(View.VISIBLE);
        return textView;

    }

}
