package com.example.Task9_1P;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Show_Saved_Location extends AppCompatActivity implements OnMapReadyCallback {


    Intent i; //new intent, Y E S.
    private GoogleMap googleMap;
    Boolean alllocationrecorded; // this is for calling SQLite. if yes then get whats in the sqlite.
    SQLiteDatabase sqlDB; //I enjoy SQLite.

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap; // Y E S, google map is google map.
        if (alllocationrecorded) {
            Cursor cursor = sqlDB.rawQuery("SELECT * FROM locations",null);
            while (cursor.moveToNext()) {
                // calling name lat and lon, there could be a place for ID but i dont think its necessary.
                //indexing from id, places,Latitude and Longtitude
                String id = cursor.getString(0);
                String Places = cursor.getString(1);
                Double Latitude = cursor.getDouble(2);
                Double Longititude = cursor.getDouble(3);

                Log.i("",Places);
                Log.i("", Latitude.toString());
                Log.i("", Longititude.toString());

                LatLng newPoint = new LatLng(Latitude, Longititude);
                this.googleMap.addMarker(new MarkerOptions().position(newPoint).title(""));
            }// yolo, saved location found  i will put a marker on top. N I C E.
            cursor.close();// close.
        }
        else {
            Bundle bundle = i.getExtras();
            String Place = bundle.getString("name");

            Double Latitudes = i.getDoubleExtra("lat", 0);
            Double Longitutde = i.getDoubleExtra("lon", 0);

            // Add to map
            LatLng newPoint = new LatLng(Latitudes, Longitutde);
            googleMap.addMarker(new MarkerOptions().position(newPoint).title("name"));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Mapinshowmap);
        mapFragment.getMapAsync(this);

        //get things ready.
        i = getIntent(); // yes, go.
        alllocationrecorded = i.getBooleanExtra("showAll", true); //uhm, set to ture and reecall to SQLite.
        sqlDB = openOrCreateDatabase("LocationsDB",MODE_PRIVATE,null); //Editing time.
    }

}
