package tn.esprit.lssafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BarChar extends AppCompatActivity {
    private static final String TAG = "BarChar";

    private DatabaseReference mDatabase;
    private BarChart mChart;
    private HashMap<String, Integer> locationCountMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_char);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference("LSPI").child("78:21:84:C6:87:10");
        locationCountMap = new HashMap<>();

        mChart = findViewById(R.id.chart);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setFitBars(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        mChart.getAxisRight().setEnabled(false);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String altitude = snapshot.child("Laltitude").getValue(String.class);
                String longitude = snapshot.child("Longitude").getValue(String.class);

                Geocoder geocoder = new Geocoder(BarChar.this, Locale.getDefault());
                List<Address> addresses;
                String locationName = "";
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(altitude), Double.parseDouble(longitude), 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        locationName = address.getLocality();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!locationCountMap.containsKey(locationName)) {
                    locationCountMap.put(locationName, 1);
                } else {
                    locationCountMap.put(locationName, locationCountMap.get(locationName) + 1);
                }

                updateChart();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle data changes
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle data removal
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle data movement
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
            }
        });
    }
    private void updateChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Integer> entry : locationCountMap.entrySet()) {
            entries.add(new BarEntry(i++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Locations");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setDrawValues(true);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(500);
        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
    }

}

