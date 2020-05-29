package com.example.csdfcommunityapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    /*
    This fragment literally needs huge updates.
    The idea is to open the Map and show nearby police stations or Hospitals......We will be implementing it soon
     */
    LocationManager locationManager;
    LocationListener locationListener;

    TextView textViewLat1,textViewLon1,textViewAlt1,textViewAcc1,textViewAdd1;
    TextView textViewLat,textViewLon,textViewAlt,textViewAcc,textViewAdd;


    // Required empty public constructor
    public MapFragment() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    if (lastKnownLocation.getLongitude() != 0) {
                        textViewLat.setText("Latitude:");
                        textViewLat1.setText(String.valueOf(lastKnownLocation.getLatitude()));
                        textViewLon.setText("Longitude:");
                        textViewLat1.setText(String.valueOf(lastKnownLocation.getLongitude()));
                        textViewAlt.setText("Altitude:");
                        textViewAlt1.setText(String.valueOf(lastKnownLocation.getAltitude()));
                        textViewAcc.setText("Accuracy:");
                        textViewAcc1.setText(String.valueOf(lastKnownLocation.getAccuracy()));



                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                            if (!addressList.get(0).getAddressLine(0).isEmpty()) {
                                textViewAdd.setText("Address:");
                                textViewAdd1.setText(addressList.get(0).getAddressLine(0));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { textViewLat1.setTextSize(10);
                        textViewLat1.setText("Information unavailable");

                        textViewLat.setText("");
                    }

                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        textViewLat1 = view.findViewById(R.id.latStatus);
        textViewLon1 = view.findViewById(R.id.lonStatus);
        textViewAcc1 = view.findViewById(R.id.accStatus);
        textViewAlt1 = view.findViewById(R.id.altStatus);
        textViewAdd1 = view.findViewById(R.id.addStatus);
        textViewLat = view.findViewById(R.id.latStatus2);
        textViewLon = view.findViewById(R.id.lonStatus2);
        textViewAcc = view.findViewById(R.id.accStatus2);
        textViewAlt = view.findViewById(R.id.altStatus2);
        textViewAdd = view.findViewById(R.id.addStatus2);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    textViewLat1.setText("Latitude:");
                    textViewLat.setText(String.valueOf(location.getLatitude()));
                    textViewLon1.setText("Longitude:");
                    textViewLon.setText(String.valueOf(location.getLongitude()));
                    textViewAlt1.setText("Altitude:");
                    textViewAlt.setText(String.valueOf(location.getAltitude()));
                    textViewAcc1.setText("Accuracy:");
                    textViewAcc.setText(String.valueOf(location.getAccuracy()));



                    Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (!addressList.get(0).getAddressLine(0).isEmpty()) {
                            textViewAdd1.setText("Address:");
                            textViewAdd.setText(addressList.get(0).getAddressLine(0));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    textViewLat1.setTextSize(10);
                    textViewLat1.setText("Information unavailable");

                    textViewLat.setText("");
                }
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
                textViewLat.setText("Latitude:");
                textViewLat1.setText(String.valueOf(lastKnownLocation.getLatitude()));
                textViewLon.setText("Longitude:");
                textViewLat1.setText(String.valueOf(lastKnownLocation.getLongitude()));
                textViewAlt.setText("Altitude:");
                textViewAlt1.setText(String.valueOf(lastKnownLocation.getAltitude()));
                textViewAcc.setText("Accuracy:");
                textViewAcc1.setText(String.valueOf(lastKnownLocation.getAccuracy()));



                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                    if (!addressList.get(0).getAddressLine(0).isEmpty()) {
                        textViewAdd.setText("Address:");
                        textViewAdd1.setText(addressList.get(0).getAddressLine(0));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else { textViewLat.setTextSize(10);
                textViewLat1.setText("Information unavailable");

                textViewLat.setText("");
            }

            } else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},4);
        }
        return view;
    }
}
