package com.example.csdfcommunityapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    double latitude, longitude;
    String hospital = "hospital", police = "police";
    private int proximityRadius = 5000;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)
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
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BlueTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},4);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onButtonPressed (View view){

        final Object transferData[] = new Object[2];
        final GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        LatLng yourLocation = new LatLng(latitude,longitude);

        String url;
        switch(view.getId()){
            case R.id.button2:
                Intent i = new Intent(MapsActivity.this, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.button4:
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 15));

                url = getUrl(latitude, longitude, hospital);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(MapsActivity.this, "Refreshing with nearby Hospitals", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button5:
                mMap.clear();

                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 15));
                url = getUrl(latitude, longitude, police);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(MapsActivity.this, "Refreshing with nearby Police Stations", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=").append(latitude).append(",").append(longitude);
        googleUrl.append("&radius=").append(proximityRadius);
        googleUrl.append("&type=").append(nearbyPlace);
        googleUrl.append("&sensor=true");
        googleUrl.append("&key=" + "");
        Log.d("GoogleMapsActivity", "url = " + googleUrl.toString());
        return googleUrl.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng yourLocation = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 15));
    }
}
