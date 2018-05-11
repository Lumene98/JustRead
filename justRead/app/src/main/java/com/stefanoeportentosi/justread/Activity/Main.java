package com.stefanoeportentosi.justread.Activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stefanoeportentosi.justread.Database.DBSTORE;
import com.stefanoeportentosi.justread.GeofenceTransitionService;
import com.stefanoeportentosi.justread.Luoghi;
import com.stefanoeportentosi.justread.R;

import java.util.ArrayList;
import java.util.Random;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;



public class Main extends AppCompatActivity implements OnMapReadyCallback,LocationListener,ResultCallback<Status>,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerDragListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    private static final String TAG = "TAG";
    private static final int REQ_PERMISSION = 1;
    private TextView textcoordinate;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private static ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private Marker locationMarker;
    public Marker geoFenceMarker;
    public Marker markercliccato;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  3000;
    private final int FASTEST_INTERVAL = 2000;
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private static final float GEOFENCE_RADIUS = 500.0f; // in meters

    private CardView Bottomsheet;
    private CardView newPlace;
    public Circle cerchi;
    public Marker markers;

    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior bottomSheetBehaviornew;
    ArrayList<Luoghi> luoghi = new ArrayList<>();



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //.setTitle("Luoghi");

        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_third);
        createGoogleApi();
        initGMaps();
        // get the bottom sheet view
        Bottomsheet = (CardView)findViewById(R.id.bottom_sheet);
        newPlace = (CardView)findViewById(R.id.newPlace);


         //init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(Bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior = BottomSheetBehavior.from(Bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehaviornew = BottomSheetBehavior.from(newPlace);
        bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_HIDDEN);



        bottomSheetBehaviornew.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
           @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_EXPANDED)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((geoFenceMarker.getPosition().latitude)-0.004,geoFenceMarker.getPosition().longitude), 15f));
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //Log.d("Slide", String.valueOf(slideOffset));

            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_EXPANDED)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((markercliccato.getPosition().latitude)-0.004,markercliccato.getPosition().longitude), 15f));
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("Slide", String.valueOf(slideOffset));
            }
        });
        EditText campotitolo=(EditText)findViewById(R.id.titolonew);
                campotitolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //textcoordinate = (TextView)findViewById(R.id.textView);
        //textLong = (TextView) findViewById(R.id.lon);

        Button aggiungi=(Button)findViewById(R.id.aggiungiluogo);//+
        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome= ((EditText)findViewById(R.id.titolonew)).getText().toString();
                String note= ((EditText)findViewById(R.id.notenew)).getText().toString();

                aggiungiLuogo(nome,note);
            }
        });
        try{
        }catch (NullPointerException d){}

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });

    }

    public void aggiungiLuogo(String titolo,String autore) {
        LatLng position=geoFenceMarker.getPosition();
        Double lat= position.latitude;
        Double lng=position.longitude;
        Random r = new Random();
        int id=r.nextInt(100 - 1) + 1;
        drawMarkers(lat,lng,500f,id);
        luoghi.add(new Luoghi(id,titolo,autore,"",lat,lng,500f));
        bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_HIDDEN);
    }





    private void initGMaps(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( getApplicationContext() )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();

    }
    // Start location Updates
    private void startLocationUpdates(){
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        //textLong.setText();
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));


    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQ_PERMISSION
        );
    }
    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }
    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request, createGeofencePendingIntent()).setResultCallback(this);
    }
    private void markerForGeofence(LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.book2))
                .draggable(true)
                .title(title);
        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();
            markers.setTag(Math.random());
            geoFenceMarker = map.addMarker(markerOptions);
        }
    }
    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( getApplicationContext(), GeofenceTransitionService.class);
        int tag=(new Random()).nextInt(100 - 1) + 1;
        PendingIntent pendingintent =PendingIntent.getService(getApplicationContext(), tag, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //DBHandler db = new DBHandler(getContext());
        //db.addPromemoria(new Promemoria(titolo[0],note[0],tag));
        return pendingintent;
    }
    // Start Geofence creation process
    private void startGeofence() {
        Log.i(TAG, "startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = createGeofence( geoFenceMarker.getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }
    private void markerLocation(LatLng latLng) {
        //Log.i(TAG, "markerLocation("+latLng+")");
        String title = "Mia posizione";
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.poiner))
                .title(title);

        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            locationMarker.setTag(0);
            float zoom = 14f;
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            //map.animateCamera(cameraUpdate);
        }
    }

    private void drawMarkers(Double lat,Double lng,Float radius,Integer id) {
        Log.d(TAG, "drawingcustomGeofence()");
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.book2));
        markers = map.addMarker(markerOptions);
        markers.setTag(id);
    }

    private void writeMarkers(LatLng latLng) {
        //Log.i(TAG, "markerLocation("+latLng+")");
        String title = "Mia posizione";
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .title(title);

        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            locationMarker.setTag(0);
            float zoom = 14f;
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            //map.animateCamera(cameraUpdate);
        }
    }
    // Initialize GoogleMaps

    private Circle geoFenceLimits;

    private void drawsingleGeofence() {
        if (geoFenceLimits!=null)geoFenceLimits.remove();
        Log.d(TAG, "drawGeofence()");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            geoFenceLimits.setRadius(progress);
        }catch (NullPointerException x)
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Seleziona un punto", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
        Log.i(TAG, "onConnected()");
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()), 15f);
        map.moveCamera(cameraUpdate);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if ( status.isSuccess() ) {
            drawsingleGeofence();
        } else {
            // inform about fail
        }
    }
    private Geofence createGeofence( LatLng latLng, float radius ) { //metodo da chiamare quando si vuole creare una geofence
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId("")
                .setCircularRegion( latLng.latitude, latLng.longitude,GEOFENCE_RADIUS )
                .setExpirationDuration( NEVER_EXPIRE )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }
    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) { //metodo da chiamare per iniziare a monitorare una geofence
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence( geofence )
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
        markerForGeofence(latLng);
        drawsingleGeofence();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        bottomSheetBehaviornew.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        markercliccato=marker;
        //CardViewContent cardView=new CardViewContent(getApplicationContext());
        //cardView.getCardViewproperties(Integer.valueOf((Integer) marker.getTag()));
        DBSTORE db = new DBSTORE(this);
        TextView titolo = (TextView) findViewById(R.id.titololibro);
        TextView autore = (TextView) findViewById(R.id.autorelibro);
        TextView stelle = (TextView) findViewById(R.id.stellelibro);
        Integer tag= (Integer) marker.getTag();
        Luoghi luogo = luoghi.get((Integer) marker.getTag());
        titolo.setText(luogo.getTitolo());
        autore.setText((luogo.getAutore()));
        stelle.setText((luogo.getStelle()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((marker.getPosition().latitude)-0.004,marker.getPosition().longitude), 15f));
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG,"pausa");
    }

    @Override
    public void onResume() {
        googleApiClient.connect();
        super.onResume();

    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"destroy");
    }


    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        DBSTORE db = new DBSTORE(getApplicationContext());
        //List<Luoghi> luoghi = db.getAllLuogdb.getAllLuoghi();hi();

        luoghi.add(new Luoghi(0, "Storia di una gabbianella e del gatto che le insegnò a volare", "Luis Sepúlveda",  "★★★★★", 45.520934, 9.213022 , 500f));
        luoghi.add(new Luoghi(1, "1984", "George Orwell", "★★☆☆☆", 45.518424, 9.208065, 500f));
        luoghi.add(new Luoghi(2, "I promessi Sposi", "Alessandro Manzoni", "★★★★★",  45.517221, 9.216841, 500f));
        luoghi.add(new Luoghi(3, "Decamerone", "Giovanni Boccaccio", "★★★★☆",  45.513038, 9.213999  , 500f));
        luoghi.add(new Luoghi(4, "Divina Commedia", "Dante Alighieri", "★★★★★",  45.521016, 9.210430, 500f));
        luoghi.add(new Luoghi(5, "Uno, nessuno, centomila", "Luigi Pirandello", "★★★★★",  45.512935, 9.207720  , 500f));


        for (Luoghi luogo : luoghi) {
            drawMarkers(luogo.getLat(),luogo.getLng(),luogo.getRadius(),luogo.getId());
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
