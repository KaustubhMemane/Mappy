package com.kmema.android.mappy;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    GoogleMap mGoogleMap;
    EditText editText;
    FloatingActionButton searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkGoogleApiAvailability()){
            Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            editText = findViewById(R.id.editTextSearch);
            searchButton = findViewById(R.id.floatingActionButton);
            searchButton.setOnClickListener(this);
            initMap();
        }else {
            // No Google Maps Layout
        }
    }

    private void initMap() {
        MapFragment  mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean checkGoogleApiAvailability(){

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(googleApiAvailability.isUserResolvableError(isAvailable)){
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else{
            Toast.makeText(this, "Can not Connect to services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getToLocationZoom(37.4275, -122.1697, 10);
    }

    private void getToLocationZoom(double latitude, double longitude, float zoom) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    public void getLocation(View view) throws IOException {
        String location = editText.getText().toString();
        if (location.equals("")){
            Toast.makeText(this, "Enter Some Location", Toast.LENGTH_SHORT).show();
            return;
        }
        Geocoder gc = new Geocoder(this);
        List<Address> addressList = gc.getFromLocationName(location, 1);
        Address address = addressList.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        getToLocationZoom(lat, lng, 12);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.floatingActionButton:
                try {
                    getLocation(view);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Try Again Later", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.mapTypeNone:
               mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
               break;

           case R.id.mapTypeNormal:
               mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
               break;

           case R.id.mapTypeSatellite:
               mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               break;

           case R.id.mapTypeTerrain:
               mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
               break;

           case R.id.mapTypeHybrid:
               mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
               break;

               default:
                   break;
       }

        return super.onOptionsItemSelected(item);
    }
}
