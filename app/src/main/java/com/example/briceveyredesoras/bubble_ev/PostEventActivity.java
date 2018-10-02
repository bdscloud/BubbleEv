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
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.sax.StartElementListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.example.briceveyredesoras.bubble_ev.common.Client;
import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;
import com.example.briceveyredesoras.bubble_ev.gpslocation.LocationAddress;

import org.w3c.dom.Text;


public class PostEventActivity extends FragmentActivity {
    private static final int CAMERA_REQUEST = 10;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int GALLERY_REQUEST = 20;
    private static String logtag = "CameraBubbleEV";
    private Uri imageUri;
    private Button boutonPhoto;
    private Button boutonChoisirDebut;
    private Button boutonChoisirFin;
    private Button boutonCreerEvenement;

    RadioButton radioboutonOui;



    AppLocationService appLocationService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent i = getIntent();
        setContentView(R.layout.activity_post);

        boutonPhoto = (Button) findViewById(R.id.boutonPhoto);
        boutonChoisirDebut = (Button) findViewById(R.id.boutonChoisirDebut);
        boutonChoisirFin = (Button) findViewById(R.id.boutonChoisirFin);
        boutonCreerEvenement = (Button) findViewById(R.id.boutonCreerEvenement);

        radioboutonOui = (RadioButton) findViewById(R.id.radioButtonOui);




        boutonPhoto.setOnClickListener(cameraListener);
        boutonChoisirDebut.setOnClickListener(ChoisirDebutListener);
        boutonChoisirFin.setOnClickListener(ChoisirFinListener);
        boutonCreerEvenement.setOnClickListener(CreerEvenementListener);

        appLocationService = new AppLocationService(PostEventActivity.this);


    }


    private OnClickListener cameraListener = new OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view
            selectImage();
        }
    };

    private OnClickListener ChoisirDebutListener = new OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePickerDebut");
        }
    };

    private OnClickListener ChoisirFinListener = new OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePickerFin");
        }
    };

    private OnClickListener CreerEvenementListener = new OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(PostEventActivity.this,"Vous n'avez pas rentré d'adresse",Toast.LENGTH_SHORT).show();

        }
    };

    static String strNomEv;
    static String strMotsclefs;
    static String strDescriptionEv;
    static String strheureDebut;
    static String strheureFin;
    static  Position position;


    public void onClickRadioButton(View view){
        //On recupere les champs qui ont étés remplis pour les remettre dans la nouvelle activité.
        EditText nomEV = (EditText) findViewById(R.id.ETnomEV);
        EditText motsclefs = (EditText) findViewById(R.id.ETmotsclefsEV);
        EditText descriptionEV = (EditText) findViewById(R.id.ETdescriptionEV);
        EditText heureDebut = (EditText) findViewById(R.id.ETdateDebut);
        EditText heureFin = (EditText) findViewById(R.id.ETdateFin);
        strNomEv = nomEV.getText().toString();
        strMotsclefs = motsclefs.getText().toString();
        strDescriptionEv = descriptionEV.getText().toString();
        strheureDebut = heureDebut.getText().toString();
        strheureFin = heureFin.getText().toString();

        if(view.getId()==R.id.radioButtonOui) {

            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER, getApplicationContext(), PostEventActivity.this);
            if (gpsLocation != null) {

                Position pos = new Position(0,0);
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                pos.setLatitude(latitude);
                pos.setLongitude(longitude);
                position = pos;
                System.out.println(position);
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());

            } else {
                showSettingsAlert();
            }

        }

        if(view.getId()==R.id.radioButtonNon){

            Intent intent = new Intent(getApplicationContext(),PostEventActivity3.class);
            intent.putExtra("activity",1);
            startActivity(intent);
        }



    }


    private void selectImage() {

        final CharSequence[] options = {"Prendre une Photo", "Choisir une Photo", "Retour"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(PostEventActivity.this);
        builder.setTitle("Ajouter une Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (ActivityCompat.checkSelfPermission(PostEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                            PostEventActivity.this,
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
                    Toast.makeText(PostEventActivity.this, selectedImage.toString(), Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    // notify the user
                    Toast.makeText(PostEventActivity.this, "impossible de charger la photo", Toast.LENGTH_LONG).show();
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
                c.close();
                Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                ImageView imageview = (ImageView)findViewById(R.id.imagetest);
                imageview.setImageBitmap(bitmap);

            }

        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PostEventActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Refuser", null)
                .create()
                .show();
    }
    private void showMessage2(String message){
        new AlertDialog.Builder(PostEventActivity.this)
                .setMessage(message)
                .show();
    }

    private void showMessage3(String message, String title){
        new AlertDialog.Builder(PostEventActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static int hdebut=0;
        public static int mindebut=0;
        public static int hfin=0;
        public static int minfin=0;

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
            intent.putExtra("activity",1);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

        }
    }

}
