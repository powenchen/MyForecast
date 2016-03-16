package com.example.powen.myforecast;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.hamweather.aeris.communication.AerisCallback;
import com.hamweather.aeris.communication.AerisEngine;
import com.hamweather.aeris.communication.EndpointType;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.MapViewFragment;
import com.hamweather.aeris.maps.interfaces.OnAerisMapLongClickListener;
import com.hamweather.aeris.model.AerisResponse;
import com.hamweather.aeris.tiles.AerisTile;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends MapViewFragment implements OnAerisMapLongClickListener, AerisCallback {


    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AerisEngine.initWithKeys("alqaVSIc0m387XbuedDpp", "chmKHvkxRGflTx1dgw9YYwWY4kXPIwjueQUKaMzP", "com.example.powen.myforecast");
        View view = inflater.inflate(R.layout.fragment_interactive_maps, container, false);
        mapView = (AerisMapView) view.findViewById(R.id.aerisfragment_map);
        mapView.init(savedInstanceState, AerisMapView.AerisMapType.GOOGLE);
        Bundle bundle = getArguments();
        String lat = bundle.getString("lat");
        String lng = bundle.getString("lon");


        Location location = new Location("");
        location.setLatitude(Double.valueOf(lat));
        location.setLongitude(Double.valueOf(lng));

        LatLng latlng = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        mapView.moveToLocation(location, (float) 9.0);
        //
        Log.d("LonLat",String.valueOf(latlng.latitude)+String.valueOf(latlng.longitude));
        // show the radar tile overlay
        mapView.addLayer(AerisTile.RADSAT);
        mapView.setOnAerisMapLongClickListener(this);
        return view;

    }

    @Override
    public void onResult(EndpointType endpointType, AerisResponse aerisResponse) {

    }

    @Override
    public void onMapLongClick(double v, double v1) {

    }
}
