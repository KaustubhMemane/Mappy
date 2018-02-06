package com.kmema.android.mappy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kmema on 2/3/2018.
 */

class GetNearByPlacesData extends AsyncTask<Object, String, String>{
    private String googlePlacesData;
    private GoogleMap mMap;
    private String URL;
    DataAvailableListener.onDataAvailable onDataAvailable;
    public GetNearByPlacesData(MainActivity mainActivity) {
        onDataAvailable = mainActivity;
    }

    @Override
    protected String doInBackground(Object... objects) {

        try {
            Log.d("Near by places data", "Do it in background");
            mMap = (GoogleMap) objects[0];
            URL = (String) objects[1];
            DownLoadUrl downLoadUrl = new DownLoadUrl();
            googlePlacesData = downLoadUrl.readUrl(URL);
            Log.d("Google Places read task", "doInBackgroundExit");

        } catch (Exception e) {
            Log.d("Google Places read task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String data) {
        Log.d("GooglePlacesReadTask", "OnPost Execute Entered");
        List<HashMap<String, String>> nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse(data);
        showNearByPlaces(nearByPlacesList);

        Log.d("GooglePlacesReadTask", "OnPostExecute exit");

        super.onPostExecute(data);
    }

    private void showNearByPlaces(List<HashMap<String, String>> nearByPlacesList) {
        for (int i = 0; i < nearByPlacesList.size(); i++) {

            Log.d("onPostExecute", "Entered into showing locations");

            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googlePlace = nearByPlacesList.get(i);

            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            if(onDataAvailable == null)
            {

            }
            onDataAvailable.displayDataInList(nearByPlacesList);
        }
    }
}
