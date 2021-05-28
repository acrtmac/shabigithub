package com.example.Task9_1P;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Objects;

public class Add_New_Location extends AppCompatActivity implements OnMapReadyCallback {

    Double Latitude;
    Double Longitudes;
    Location Location;
    SQLiteDatabase sqlDB;
    SupportMapFragment MapFrag;
    AutocompleteSupportFragment AutocompleteEdittext;
    public Place place;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    //getting permission
    private final ActivityResultLauncher<String> activityResultGPSPermission=  registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            getLocation();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);
        String apiKey = getString(R.string.api_key);
        //calling the apikey for the default map interface.

        MapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newlocationmap);
        assert MapFrag != null;
        MapFrag.getMapAsync(this);

        //Map fragment setting
        sqlDB = openOrCreateDatabase("LocationsDB", MODE_PRIVATE, null);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //PC setting
        if (!Places.isInitialized()) { Places.initialize(getApplicationContext(), apiKey); }
        AutocompleteEdittext = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert AutocompleteEdittext != null;
        AutocompleteEdittext.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        AutocompleteEdittext.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        //AC search bar setting.
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Add_New_Location.this.place = place;
                Latitude = Objects.requireNonNull(place.getLatLng()).latitude;
                Longitudes = place.getLatLng().longitude;
                //once a location is selected, get Lon and Lat
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i("", "An error occurred: " + status);
            } //auto created for handling error.
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //google map ready. ready player one.
        this.googleMap = googleMap;
        findViewById(R.id.map1).setVisibility(View.GONE);
    }

    public void savedPlaces(View v) {
        EditText edittext = findViewById(R.id.editTextTextPersonName);
        if ( Latitude == null || Longitudes == null) {
            edittext.requestFocus(); //if no location, request focus.
        }
        else { // if there are places:
            sqlDB.execSQL("INSERT INTO locations (name, lat, lon) VALUES (\"" + edittext.getText().toString() + "\", " + Latitude + ", " + Longitudes + ");");

            Intent i = new Intent(this, Show_Saved_Location.class);
            i.putExtra("showAll", false);
            i.putExtra("name", edittext.getText().toString());
            i.putExtra("Latitude", Latitude);
            i.putExtra("Longitude", Longitudes);// get id, name, lat, lon

            startActivity(i);
            Latitude = null;
            Longitudes = null;
        }
    }
    public void getLocation(View v) {
        //test if the permission is greanted or not
        String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            activityResultGPSPermission.launch(permission);
        }
        else {
            getLocation();
        }
    }
    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        }// if permission not granted

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override //im lazy, its set in default as permission granted.
            public void onSuccess(Location location) {
                if (location != null) {
                    Location = location;
                    Latitude = Location.getLatitude();
                    Longitudes = Location.getLongitude();
                }
                else {
                    Location = null;
                }
            }
        });

        if (Location == null) {
            LatLng location = new LatLng(0, 0);
            Latitude = location.latitude;
            Longitudes = location.longitude;
        }

        AutocompleteEdittext.setText("Current Location");
        place = null; // get current location. kinda what it is.

    }

    public void cancel(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i); // goes back.
    }

    public void hideMap(View v) { // hides map.
        findViewById(R.id.map1).setVisibility(View.GONE);
    }

    public void showMap(View v) { // shows map. Lord I hate commenting.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        }// if not granted

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Location = location;
            }
        });

        EditText et = findViewById(R.id.editTextTextPersonName);
        LatLng location = new LatLng(0, 0);
        // now at this stage, there are lots of exceptions to perform the "try and catch";
        // at first try is a general call for getting location detail for lat and lon
        // the second tr is creating new LatLng for new location and based on the thrid try to add marker and move camera.
        // tries again for the third time and get Exceptions to catch.
        //make markers visiable.
        try {
            try {location = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude); }
            catch (Exception e) {
                try {location = new LatLng(Location.getLatitude(), Location.getLongitude()); }
                catch (Exception ignored) {}
            }
            googleMap.addMarker(new MarkerOptions().position(location).title(et.getText().toString()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));// show marker based on location added.
        }
        catch (Exception ignored) {}
        finally {
            findViewById(R.id.map1).setVisibility(View.VISIBLE);
        }
    }
}
