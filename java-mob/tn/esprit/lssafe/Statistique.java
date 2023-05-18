package tn.esprit.lssafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Statistique extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView temp, batt, dist, Lalt, Longi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);
        getSupportActionBar().setTitle("État de drone de khalil.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        temp = findViewById(R.id.longitude);
        batt = findViewById(R.id.laltitude);
        dist = findViewById(R.id.distance);
        Lalt = findViewById(R.id.maposition);
        Longi = findViewById(R.id.dronita);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }


        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Geocoder geocoder = new Geocoder(Statistique.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Lalt.setText(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        });

        DatabaseReference gpsRef = FirebaseDatabase.getInstance().getReference("Drone");


        gpsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dronitaSnapshot : dataSnapshot.getChildren()) {
                    String tempe = dronitaSnapshot.child("Temperature").getValue(String.class);
                    String batte = dronitaSnapshot.child("Battery").getValue(String.class);
                    String diqto = dronitaSnapshot.child("Range").getValue(String.class);
                    String latitude = dronitaSnapshot.child("Laltitude").getValue(String.class);
                    String longitude = dronitaSnapshot.child("Longitude").getValue(String.class);
                    temp.setText(tempe);
                    batt.setText(batte);
                    dist.setText(diqto);
                    //Lalt.setText(placeName);
                    //Longi.setText(longitude + " " + latitude);
                    Geocoder geocoder = new Geocoder(Statistique.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses != null && addresses.size() > 0) {
                        String placeName = addresses.get(0).getAddressLine(0);
                        Longi.setText(placeName);
                        Longi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + placeName);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Toast.makeText(Statistique.this, "Google Maps not available", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    //Longi.setText(longitude + " " + latitude);
                }

                /*
                    Double latitude = dronitaSnapshot.child("Laltitude").getValue(Double.class);
                    Double longitude = dronitaSnapshot.child("Longitude").getValue(Double.class);
                    if (latitude != null | longitude != null) {
                    String latitudeStr = latitude.toString();
                    String longitudeStr = longitude.toString();
                     Longi.setText(latitudeStr + " " + longitudeStr);}
                */


                }


            @Override
            public void onCancelled(DatabaseError error) {

            }

        });


    }


}
