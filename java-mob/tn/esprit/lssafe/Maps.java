package tn.esprit.lssafe;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.osmdroid.util.BoundingBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Maps extends AppCompatActivity  {
    private static final String TAG = "Maps";
    private MapView mMapView;
    private DatabaseReference mDatabase;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference("LSPI").child("78:21:84:C6:87:10") ;

        // Initialize map view
        mMapView = findViewById(R.id.mapView);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Retrieve latest location from Firebase Realtime Database
        mDatabase.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get latitude and longitude values
                double latitude = 0;
                double longitude = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String latitudestr = snapshot.child("Laltitude").getValue(String.class);
                    latitude = Double.parseDouble(latitudestr);
                    String  longitudestr = snapshot.child("Longitude").getValue(String.class);
                    longitude = Double.parseDouble(longitudestr);
                    Geocoder geocoder = new Geocoder(Maps.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

// Check if the geocoder was successful
                    if (addresses != null && addresses.size() > 0) {
                        // Get the location name
                        String locationName = addresses.get(0).getAddressLine(0);
                        TextView locationTextView = findViewById(R.id.Textview);

                        // Set the text of the TextView to the location name
                        locationTextView.setText(locationName);
                    }


                    Log.d("FirebaseExample", "Data changed: " + dataSnapshot.getValue());

                }

                // Create new GeoPoint object
                GeoPoint location = new GeoPoint(latitude, longitude);

                // Add marker to map and set title
                mMarker = new Marker(mMapView);
                mMarker.setPosition(location);

                mMapView.getOverlays().add(mMarker);

                // Move map view to location
                mMapView.getController().setCenter(location);

                mMapView.scrollTo(0, 0);
                BoundingBox boundingBox = BoundingBox.fromGeoPoints(Collections.singletonList(location));
                mMapView.zoomToBoundingBox(boundingBox, false);

                int zoomLevel = mMapView.getZoomLevel() - 17; // decrease the zoom level by 1
                mMapView.getController().setZoom(zoomLevel);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDetach();
    }
}