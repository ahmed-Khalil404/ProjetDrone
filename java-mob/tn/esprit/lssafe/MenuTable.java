package tn.esprit.lssafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MenuTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_table);



        CardView profile =findViewById(R.id.card1);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,Profil.class));            }
        });

        CardView etatDrone =findViewById(R.id.card2);
        etatDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,Statistique.class));            }
        });

        CardView Cameratyha =findViewById(R.id.card3);
        Cameratyha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,Cameratyha.class));            }
        });

        CardView Maps =findViewById(R.id.card4);
        Maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,Maps.class));            }
        });

        CardView Statistique  =findViewById(R.id.card5);
        Statistique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,LineChart.class));            }
        });

        CardView AboutUs  =findViewById(R.id.card6);
        AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,Heartbeat.class));            }
        });
        CardView Barchart  =findViewById(R.id.card7);
        Barchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuTable.this,BarChar.class));            }
        });
        CardView Logout  =findViewById(R.id.card8);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {

    }
}