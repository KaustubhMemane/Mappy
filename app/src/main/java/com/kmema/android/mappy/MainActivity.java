package com.kmema.android.mappy;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        DataAvailableListener.onDataAvailable{

    GoogleMap mGoogleMap;
    EditText editTextSearch;
    FloatingActionButton searchButton;
    FloatingActionButton fabOption, fabHotel, fabGas;
    Animation fabOpen, fabClose, rotateFoward, rotateBackward;
    boolean isOpen = false;


    GoogleApiClient mGoogleApiClient;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkGoogleApiAvailability()) {
            Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show();
            editTextSearch = findViewById(R.id.editTextSearch);
            searchButton = findViewById(R.id.floatingActionButton);

            fabOption = findViewById(R.id.floatingActionButtonOption);
            fabHotel = findViewById(R.id.floatingActionButtonHotel);
            fabGas = findViewById(R.id.floatingActionButtonGas);

            fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

            rotateFoward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
            rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_background);

            fabOption.setOnClickListener(this);
            fabGas.setOnClickListener(this);
            fabHotel.setOnClickListener(this);

            searchButton.setOnClickListener(this);
            initMap();

            recyclerView = findViewById(R.id.rvLocationData);

        } else {
            // No Google Maps Layout
        }
    }



    private void animationFab(){
        if(isOpen){
            fabOption.setAnimation(rotateBackward);
            fabGas.setAnimation(fabClose);
            fabHotel.setAnimation(fabClose);
            fabGas.setClickable(false);
            fabHotel.setClickable(false);
            isOpen = false;
            fabHotel.setVisibility(View.INVISIBLE);
            fabGas.setVisibility(View.INVISIBLE);
        }else
        {
            fabHotel.setVisibility(View.VISIBLE);
            fabGas.setVisibility(View.VISIBLE);
            fabOption.setAnimation(rotateFoward);
            fabGas.setAnimation(fabOpen);
            fabHotel.setAnimation(fabOpen);
            fabGas.setClickable(true);
            fabHotel.setClickable(true);
            isOpen = true;
        }
    }



    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean checkGoogleApiAvailability() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can not Connect to services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(mGoogleMap != null){
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    LatLng latLng = marker.getPosition();
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 12);
                        getGeoCoder(addresses);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    marker.showInfoWindow();
                }
            });




            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                        return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.local_info_window, null);

                    TextView tvLocality = v.findViewById(R.id.tvLocality);
                    TextView tvLatitude = v.findViewById(R.id.tvLatitude);
                    TextView tvLongitude = v.findViewById(R.id.tvLongitude);
                    TextView tvSnippet = v.findViewById(R.id.tvSnippet);
                    TextView tvDistanceMarker = v.findViewById(R.id.tvDistanceMarker);

                    LatLng ll = marker.getPosition();
                    float results[] = new float[10];
                    Location.distanceBetween(mGlobalLocation.getLatitude(),mGlobalLocation.getLongitude(),ll.latitude, ll.longitude, results);
                    tvLocality.setText(marker.getTitle());
                    tvLatitude.setText("Latitude: "+ll.latitude);
                    tvLongitude.setText("Longitude: "+ ll.longitude);
                    tvSnippet.setText(marker.getSnippet());
                    float intoMiles = (float) (results[0] * 0.00062137);
                    tvDistanceMarker.setText(Float.toString(intoMiles)+" Miles ");
                    return v;
                }
            });
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
//        getToLocationZoom(37.4275, -122.1697, 10);

    }

    private void getToLocationZoom(double latitude, double longitude, float zoom) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    public void getLocation(View view){
        String location = editTextSearch.getText().toString();
        if (location.equals("")) {
            Toast.makeText(this, "Enter Some Location", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Geocoder gc = new Geocoder(this);
            List<Address> addressList = gc.getFromLocationName(location, 1);
            getGeoCoder(addressList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getGeoCoder(List<Address> addressList) throws IOException {
        Address address = addressList.get(0);
        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        getToLocationZoom(lat, lng, 12);
        setMarkerToLocation(address, lat,lng);
    }




    Marker mMarker;
    Circle circle;
    private void setMarkerToLocation(Address address, double latitude, double longitude){
        if(mMarker != null){
            removePreviousMarks();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title(address.getLocality())
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(latitude, longitude))
                .snippet(address.getPostalCode());
        mMarker = mGoogleMap.addMarker(markerOptions);
        circle = drawCircle(new LatLng(latitude, longitude));
    }

    private Circle drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(1000)
                .fillColor(0x33FF0000)
                .strokeColor(Color.BLUE)
                .strokeWidth(1);
        return mGoogleMap.addCircle(circleOptions);
    }

    private void removePreviousMarks(){
        mMarker.remove();
        mMarker = null;
        circle.remove();
        circle = null;
    }


    Marker privateMarker;
    private void setMarkerToLocation(Location location, double latitude, double longitude){
        if(privateMarker != null){
            privateMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title("Here You are!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
                .position(new LatLng(latitude, longitude))
                .snippet(Long.toString(location.getTime()));
        privateMarker = mGoogleMap.addMarker(markerOptions);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.floatingActionButton:
                getLocation(view);
                break;
            case R.id.floatingActionButtonOption:
                animationFab();
                Toast.makeText(this, "Option", Toast.LENGTH_SHORT).show();
                break;
            case R.id.floatingActionButtonHotel:
                getNearByPlaces(view, RESTAURANT);
                break;
            case R.id.floatingActionButtonGas:
                getNearByPlaces(view, GAS_STATION);
                break;
        }
    }
    final String RESTAURANT = "restaurant";
    final String GAS_STATION = "gas_station";
    final String HOSPITAL = "hospital";
    final String SCHOOL = "school";

    private void getNearByPlaces(View view, String nearByPlaces) {
        mGoogleMap.clear();
        String URL = getUrl(mGlobalLocation.getLatitude(),mGlobalLocation.getLongitude(),nearByPlaces);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mGoogleMap;
        DataTransfer[1] = URL;
        Log.d("RESTAURANT CLICK", URL);
        GetNearByPlacesData getNearByPlacesData = new GetNearByPlacesData(MainActivity.this);
        getNearByPlacesData.execute(DataTransfer);
    }




    private int PROXIMITY_RADIUS = 10000;
    private String getUrl(double latitude, double longitude, String nearbyPlaces) {
        StringBuilder googlePlaceURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceURL.append("location="+latitude+","+longitude);
        googlePlaceURL.append("&radius="+ PROXIMITY_RADIUS);
        googlePlaceURL.append("&type="+ nearbyPlaces);
        googlePlaceURL.append("&sensor=true");
        googlePlaceURL.append("&key="+"AIzaSyDPHhwqR20n0U9YQiCwI1zQdPD-Z3f8qLg");
        Log.d("getURL: ", googlePlaceURL.toString());
        return googlePlaceURL.toString();
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            requestingRuntimePermission(this, LIST_REQUESTING_LOCATION_PERMISSION, MY_PERMISSION_REQUEST_CODE_FINE_AND_COARSE_LOCATION);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSION_REQUEST_CODE_FINE_AND_COARSE_LOCATION){
            if(checkWhetherAllPermissionArePresent()){
                Toast.makeText(this, "All Permissions are available", Toast.LENGTH_SHORT).show();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }else{
                String permissionString = getDeniedPermissionAmongTheLocationPermissions().length == 1 ? "Permission" : "Permissions";
                Snackbar.make(findViewById(android.R.id.content), permissionString+"denied! Enable Permission(s) to work", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, getDeniedPermissionAmongTheLocationPermissions(), MY_PERMISSION_REQUEST_CODE_FINE_AND_COARSE_LOCATION);
                            }
                        });
            }
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(findViewById(android.R.id.content), "Connection Failed",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    Location mGlobalLocation;
    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Can't find Current Location", Toast.LENGTH_SHORT).show();
        }else{
            if(editTextSearch.getText().length()==0) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mGlobalLocation = location;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//                mGoogleMap.animateCamera(cameraUpdate);
                setMarkerToLocation(location, location.getLatitude(), location.getLongitude());
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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

            case R.id.mapPermission:
                startAppPermissionSetting();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayDataInList(List<HashMap<String, String>> listData) {
        RvAddressAdapter rvAddressAdapter = new RvAddressAdapter(listData, MainActivity.this);
        recyclerView.setAdapter(rvAddressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyCvZAj-cHeNVfvzjJg8vXysjr5wA1yOVfM")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
}
