package tn.esprit.lssafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Heartbeat extends AppCompatActivity {
    private com.github.mikephil.charting.charts.LineChart mChart;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartbeat);
        getSupportActionBar().setTitle("Heartbeat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mChart = findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        // customize chart x-axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // customize chart y-axis
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        // customize chart legend
        Legend legend = mChart.getLegend();
        legend.setEnabled(false);

        // initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("LSPI").child("78:21:84:C6:87:10");

        // add database listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get latest heartbeats
                List<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chocstr = snapshot.child("Heartbeat").getValue(String.class);
                    long choc = Long.parseLong(chocstr);
                    entries.add(new Entry(entries.size(), choc));
                }

                // update chart data
                LineData data = mChart.getData();
                if (data == null) {
                    data = new LineData();
                    mChart.setData(data);
                }

                LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                if (set == null) {
                    set = new LineDataSet(entries, "Heartbeats");
                    set.setColors(ColorTemplate.MATERIAL_COLORS);
                    set.setCircleColors(ColorTemplate.MATERIAL_COLORS);
                    set.setLineWidth(2f);
                    set.setCircleRadius(4f);
                    set.setDrawValues(false);

                    data.addDataSet(set);
                } else {
                    set.setValues(entries);
                    data.notifyDataChanged();
                }

                // refresh chart
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });
    }
}








