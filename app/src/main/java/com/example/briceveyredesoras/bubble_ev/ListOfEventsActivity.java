package com.example.briceveyredesoras.bubble_ev;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.briceveyredesoras.bubble_ev.common.Evenement;
import com.example.briceveyredesoras.bubble_ev.common.Position;
import com.example.briceveyredesoras.bubble_ev.gpslocation.AppLocationService;
import com.example.briceveyredesoras.bubble_ev.gpslocation.LocationAddress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ListOfEventsActivity extends ListActivity {

    View myFragmentView;
    ListView lv;
    RelativeLayout bardubas;
    AppLocationService appLocationService;
    static ArrayList<Evenement> eventArrayList = new ArrayList<>();
    static Bitmap eventBitmap ;
    static String adresseEventClick;
    static Boolean bool =false;


    final SimpleDateFormat dayFormatter = new SimpleDateFormat("dd"); // output day
    final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM"); // output month
    final SimpleDateFormat hourFormatter = new SimpleDateFormat("HH"); // output hour
    final SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm"); // output hour


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_listofevents);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 23);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 00);
        Date dateEVStart = cal.getTime();
        Date dateEVend = new Date(116, 3, 23, 10, 40);
        //--------------------------------------------------------------------------------------------------
 /*       eventArrayList.add(new Evenement("Table ronde", "Big data #data", new Position(40, 8) , dateEVStart , dateEVend ,new Date(), "pseudo"));
        eventArrayList.add(new Evenement("Concert de Rock", "Rock et musique #chill", new Position(10, 20) , dateEVStart , dateEVend ,new Date(), "pseudo"));
        eventArrayList.add(new Evenement("Demonstration skate park", "Tony Hawk #skate", new Position(8, 40.8) , dateEVStart , dateEVend ,new Date(), "pseudo"));
        eventArrayList.add(new Evenement("Cours de Pact", "groupe 1.4 #nonponctuel", new Position(5, 0) , dateEVStart , dateEVend ,new Date(), "pseudo"));
        eventArrayList.add(new Evenement("Tournoi Babyfoot", "Tournoi organisé par le bds #baby", new Position(18, 24), dateEVStart , dateEVend ,new Date(), "pseudo1"));
        eventArrayList.add(new Evenement("Tournoi ping pong", "Tournoi organisé par le bds #pingpong",new Position(0, 40.6), dateEVStart , dateEVend ,new Date(), "pseudo"));
 */       //-------------------------------------------------------------------------------------------------


        eventArrayList = MainActivity.listOfEvents;
        bardubas = (RelativeLayout) findViewById(R.id.bardubas);

        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(new custom_list(getApplicationContext(), eventArrayList));

    }
    public void onClickSynchronize(View view) {
        new SyncAsyncTask().execute();
    }


    public void onClickMap(View view) {
        Intent intent = new Intent(ListOfEventsActivity.this,MapsActivity.class);
        startActivity(intent);
    }

    public void onClickList(View view) {
        new SyncAsyncTask().execute();
    }

    public void onClickPost(View view) {
        Intent intent = new Intent(this, PostEventActivity.class);
        startActivity(intent);

    }

    public void onClickParameters(View view) {
        Intent intent = new Intent(this, ParametersActivity.class);
        startActivity(intent);
    }

    public View onClickSearch(final View view) {
        RelativeLayout searchView = (RelativeLayout) findViewById(R.id.searchView1);
        searchView.setVisibility(View.VISIBLE);
        lv.setVisibility(View.INVISIBLE);
        bardubas.setVisibility(View.INVISIBLE);
        SearchView search=(SearchView) findViewById(R.id.search_bar);
        search.setQueryHint("Commencez à taper pour chercher...");

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                //---------------------------------------recuperation serveur -------------------------------------

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList array = MainActivity.cli.KeyWordCompletion(query);
                        MainActivity.listOfEvents = new ArrayList<Evenement>();
                        MainActivity.listOfEvents = MainActivity.cli.getNearEvents(MainActivity.myPosition, array);
                        eventArrayList = MainActivity.listOfEvents;

                        bool = true;
                    }
                }).start();

                while (bool == false) {

                }

                //---------------------------------------------------------------------------------------------------

                lv.setVisibility(view.VISIBLE);
                bardubas.setVisibility(View.GONE);

                Intent intent = new Intent (getApplicationContext(),ListOfEventsActivity.class);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                return false;
            }



        });
        return myFragmentView;



    }




    //le viewHolder stock temporairement des donnés relatives a la mise en page d'un evenement : layout_event
    public class ViewHolder {

        TextView nomEV;
        TextView motsClefs;
        TextView dateEVStart;
        TextView dateEVEnd;
        TextView posteur;
        ImageView photoEv;
        TextView cptlikeEv;
        CheckBox star;
        TextView distanceEv;

    }

    //On crée une classe custom_list, cad qu'on crée une liste deroulante d'objets customisés. Ici ce sont des Evenement
    //representées de la maniere de layout_event. On insere les layout_event dans une ListView (activity_listOfEvents)
    public class custom_list extends BaseAdapter {

        private LayoutInflater layoutInflater;
        ViewHolder holder = new ViewHolder();
        private ArrayList<Evenement> arrayList = new ArrayList<Evenement>();
        Context context;

        public custom_list(Context context, ArrayList<Evenement> arrayList) {
            layoutInflater = LayoutInflater.from(context);
            this.arrayList = arrayList;
            this.context = context;
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {

            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.layout_event, null);
                holder = new ViewHolder();
                holder.photoEv = (ImageView) convertView.findViewById(R.id.photoEV);
                holder.nomEV = (TextView) convertView.findViewById(R.id.nomEV);
                holder.motsClefs = (TextView) convertView.findViewById(R.id.motsClefsEV);
                holder.dateEVStart = (TextView) convertView.findViewById(R.id.dateEVStart);
                holder.dateEVEnd = (TextView) convertView.findViewById(R.id.dateEVEnd);
                holder.posteur = (TextView) convertView.findViewById(R.id.posteurEV);
                holder.cptlikeEv = (TextView) convertView.findViewById(R.id.noteEV);
                holder.star = (CheckBox) convertView.findViewById(R.id.buttonStar);
                holder.distanceEv = (TextView) convertView.findViewById(R.id.distanceEvenement);
                convertView.setTag(holder);
                //jusqu'ici les evenements visibles a l'ecran seront tous identiques et de la meme mamiere que layout_event
            } else {

                holder = (ViewHolder) convertView.getTag();
            }


            //Mais ici on modifie les evenements, qui doivent etre chargés d'une ArrayList fournie par la BDD

            final Evenement event = (Evenement) getItem(position);

            holder.nomEV.setText(event.getName());
            //il faut que je fasse la methode qui recupere les mots clefs dans la description
            holder.motsClefs.setText(event.getDescription());
            holder.dateEVStart.setText(dayFormatter.format(event.getDateEVStart()) + " " + monthFormatter.format(event.getDateEVStart()) + " " + hourFormatter.format(event.getDateEVStart()) + ":" + minuteFormatter.format(event.getDateEVStart()));
            holder.dateEVEnd.setText(dayFormatter.format(event.getDateEVEnd()) + " " + monthFormatter.format(event.getDateEVEnd()) + " " + hourFormatter.format(event.getDateEVEnd()) + ":" + minuteFormatter.format(event.getDateEVEnd()));
            holder.posteur.setText(event.getPosteur());
            //On calacule la distance entre nous et l'evenement
            Float distance = getdistance(MainActivity.myPosition,(Position) event.getPosition());
            holder.distanceEv.setText(String.valueOf(distance));
            holder.star.setOnCheckedChangeListener(StarCheckedChangeListener);
            holder.star.setTag(position);
            holder.cptlikeEv.setText(String.valueOf(event.nbLike()));
            //holder.photoEv.setImageBitmap(event.getBitmap());


            //permet de cliquer sur un evenement pour avoir accès a la description et aux eventuelles videos
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ListOfEventsActivity.this,PopUpEvent.class);
                    ArrayList<String> descriptif = new ArrayList<String>();
                    //-------------------------------recuperation de l'adresse----------------------------------------------------
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(event.getPosition().getLatitude(), event.getPosition().getLongitude(), getApplicationContext(), new GeocoderHandler());
                    //-------------------------------fin de recuperation de l'adresse---------------------------------------------
                    String nom = eventArrayList.get(position).getName();
                    String description = eventArrayList.get(position).getDescription();
                    Date debut = eventArrayList.get(position).getDateEVStart();
                    Date fin = eventArrayList.get(position).getDateEVEnd();
                    String strdebut = dayFormatter.format(event.getDateEVStart()) + " " + monthFormatter.format(event.getDateEVStart()) + " " +
                            hourFormatter.format(event.getDateEVStart()) + ":" + minuteFormatter.format(event.getDateEVStart());
                    String strfin = dayFormatter.format(event.getDateEVEnd()) + " " + monthFormatter.format(event.getDateEVEnd()) + " " +
                            hourFormatter.format(event.getDateEVEnd()) + ":" + minuteFormatter.format(event.getDateEVEnd());
                    String posteur = eventArrayList.get(position).getPosteur();
                    String distance = String.valueOf(getdistance(MainActivity.myPosition, (Position) event.getPosition()));

                    //eventBitmap = eventArrayList.get(position).getBitmap();
                    //ensuite je crée une variable static eventBitmap dans cette activitée que je recupere dans PopupEvent
                    descriptif.add(nom);
                    descriptif.add(description);
                    descriptif.add(strdebut);
                    descriptif.add(strfin);
                    descriptif.add(posteur);
                    descriptif.add(distance);
                    descriptif.add(adresseEventClick);
                    intent.putStringArrayListExtra("descriptif", descriptif);
                    startActivity(intent);
                }
            });

            return convertView;
        }


        //cette methode permet de mettre a jour la note a chaque etoile créé.
        private CompoundButton.OnCheckedChangeListener StarCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final int position = getListView().getPositionForView(buttonView);
                final Evenement event = (Evenement) getItem(position);
                if (position != ListView.INVALID_POSITION) {
                    if (isChecked)
                        event.addLike();
                    if (!isChecked)
                        event.removeLike();

                    notifyDataSetChanged();
                    //------------------------------ envoie du like en plus au serveur --------------------------------
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences preferences = getSharedPreferences("fichierdataUtilsateur", Context.MODE_PRIVATE);
                            final String pseudoUser = preferences.getString("Pseudo", "");
                            if (MainActivity.cli.setLike(event.getName(), pseudoUser)) {
                                System.out.println("c'est bon");
                            }

                        }
                    }).start();
                    //--------------------------------fin d'envoi du like au serveur------------------------------------
                }

            }

        };
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


    private class SyncAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean bool = false;
        Position position = new Position(0,0);
        @Override
        protected void onPreExecute() {
            findViewById(R.id.relativeListItems).setVisibility(View.GONE);
            findViewById(R.id.loadingPanelListItems).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    appLocationService = new AppLocationService(ListOfEventsActivity.this);
                    final Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER,
                            getApplicationContext(), ListOfEventsActivity.this);


                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        position.setLatitude(latitude);
                        position.setLongitude(longitude);
                        MainActivity.myPosition.setLatitude(latitude);
                        MainActivity.myPosition.setLongitude(longitude);
                        System.out.println(position);
                        bool = true;

                    } else {
                        showSettingsAlert();
                    }
                }
            });

            while (bool == false){

            }


            Intent intent = new Intent(getApplicationContext(),ListOfEventsActivity.class);
            MainActivity.listOfEvents = MainActivity.cli.getNearEvents(position);
            startActivity(intent);



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            findViewById(R.id.loadingPanelListItems).setVisibility(View.GONE);
            findViewById(R.id.relativeListItems).setVisibility(View.VISIBLE);

            if(bool == true){
                Toast.makeText(getApplicationContext(), "Synchronisation réussie", Toast.LENGTH_SHORT).show();

            }

            if(bool ==false){
                Toast.makeText(getApplicationContext(),"problème de localisation",Toast.LENGTH_SHORT).show();

            }
        }

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
                    locationAddress = "impossible de trouver l'adresse";
            }
            ListOfEventsActivity.adresseEventClick = locationAddress ;


        }
    }

}


