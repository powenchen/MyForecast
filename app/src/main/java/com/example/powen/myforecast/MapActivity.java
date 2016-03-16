package com.example.powen.myforecast;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


public class MapActivity extends AppCompatActivity {
    private String[] place ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
      //  AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);
        Bundle extras = getIntent().getExtras();

        place = extras.getStringArray("toMapPlace");

        Bundle bundle = new Bundle();
        bundle.putString("lon", place[1]);
        bundle.putString("lat", place[0]);

        Log.d("LonLat",  place[0] +  place[1]);
        FragmentTransaction fM = getFragmentManager().beginTransaction();
        BlankFragment myf = new BlankFragment();
        myf.setArguments(bundle);
        Log.d("myBug", "after constructor");
        fM.add(R.id.mapFrame, myf);
        Log.d("myBug", "afteradd");
        fM.commit();
        Log.d("myBug", "aftercommit");



    }

}
