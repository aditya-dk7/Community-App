package com.example.csdfcommunityapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class SosFragment extends Fragment {



    ImageView sendSOS;
    LocationManager locationManager;
    LocationListener locationListener;
    double latitude,longitude;


    // Required empty public constructor
    public SosFragment() {

    }

    // As soon as the permission is granted, the location is promptly saved in the variables.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    latitude = lastKnownLocation.getLatitude();
                    longitude = lastKnownLocation.getLongitude();
                }
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //setTheme(R.style.RedTheme);
        //getContext().setTheme().applyStyle(R.style.RedTheme, true);

        // Fragments cannot use views unless you specifically do the following. REMEMBER TO USE "view" to access the usual commands
        View view = inflater.inflate(R.layout.fragment_sos, container, false);

        sendSOS = view.findViewById(R.id.buttonLogIn);

        //The location manger and listener work in sync to determine the latitude and longitude
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        sendSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSOSEmail();

            }
        });

        return view;
    }

    private void sendSOSEmail(){
        String subjectSOSEmail = "SOS Alert";
        String bodySOS;
        if(latitude!=0 && longitude !=0){
            bodySOS = "I am in a dire situation. Please help me. My location is : https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude;

        }else{
            bodySOS = "I am in a dire situation. Please help me. My location is unavailable at the moment, but this is a genuine distress call sent from the SOS app";

        }
        Intent intent= new Intent(Intent.ACTION_SEND);

        /*
         Note : email intent would send the email to my address....you require to change it
         I am finding a way to use the G-MAIL APi.....But until then this ought to work
         */

        intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "csdfgroup53@gmail.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT,subjectSOSEmail);
        intent.putExtra(Intent.EXTRA_TEXT,bodySOS);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an e-mail client"));
    }
}
