package com.example.Task9_1P;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase sqlDB; //SQLite database integrated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlDB = openOrCreateDatabase("LocationsDB",MODE_PRIVATE,null);
        sqlDB.execSQL("CREATE TABLE IF NOT EXISTS locations" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR, lat REAL, lon REAL);");
        //create structure: location,id,name.lat,lon
    }

     public void buttonClick(View v) {
        if (v.getId() == R.id.Add_New_Location) {
            Intent i = new Intent(this, Add_New_Location.class);
            startActivity(i);
        }
         //if button add new location clicked, run addnewlocation

        if (v.getId() == R.id.Show_Saved_Location) {
            Intent i = new Intent(this, Show_Saved_Location.class);
            i.putExtra("showAll", true);
            startActivity(i);
        }
        //if button showsavedlocation clicked, run showsavedlocation

        else{
            return;
        }
        // do nothing
     }
}