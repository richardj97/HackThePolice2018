package uk.ac.lincoln.a16629926students.community_anpr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportCrime extends AppCompatActivity {
    private Spinner spinner;
    private EditText regPlateTb;
    private double Longitude = 0, Latitude = 0;
    private Intent viewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_crime);
        Intent intent = getIntent();
        String foundNumberPlate = intent.getStringExtra("foundNumberPlate");
        spinner = (Spinner)findViewById(R.id.spinner);
        regPlateTb = (EditText)findViewById(R.id.regPlateTb);
        regPlateTb.setText(foundNumberPlate);
        UpdateSpinner();
    }
    private void UpdateSpinner(){
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(ReportCrime.this,
                android.R.layout.simple_spinner_item, Crimes.GetCrimeList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void viewMap(View view){
        String url = "https://scoop-cinema.codio.io/Map.html?lat=-" + Latitude + "&long=" + Longitude;
        viewMap = new Intent(Intent.ACTION_VIEW);
        viewMap.setData(Uri.parse(url));
        startActivity(viewMap);
    }

    public void submitCrime(View view){
        if (!spinner.getSelectedItem().toString().isEmpty() && !regPlateTb.getText().toString().isEmpty()){
            ActivityCompat.requestPermissions(ReportCrime.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            Toast.makeText(ReportCrime.this, "One or more fields are empty", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // acquire a reference to the system Location Manager
                    String locationProvider = LocationManager.GPS_PROVIDER;
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                    if (lastKnownLocation == null) {
                        // if no last location is available set lat/long to zero
                        Latitude = 53.228029; // Default lat
                        Longitude = -0.546055; // Default lon
                        Toast.makeText(ReportCrime.this, "Could not find location, using default", Toast.LENGTH_SHORT).show();
                    } else {
                        // if last location exists then get/set the lat/long
                        Latitude = lastKnownLocation.getLatitude();
                        Longitude = lastKnownLocation.getLongitude();
                        Toast.makeText(ReportCrime.this, "Found your location successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportCrime.this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    finishActivity(0);
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String crimetype = spinner.getSelectedItem().toString();
                String vehiclereg = regPlateTb.getText().toString();

                Map<String, Object> crime = new HashMap<>();
                crime.put("crimetype", crimetype);
                crime.put("vehreg", vehiclereg);
                crime.put("locationlat", Longitude);
                crime.put("locationlon", Latitude);

                db.collection("crimes")
                        .add(crime)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(ReportCrime.this, "Crime was submitted successfully", Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error adding document", e);
                                Toast.makeText(ReportCrime.this, "Unable to submit the crime", Toast.LENGTH_SHORT).show();
                            }
                        });
                return;
            }
        }
    }
}
