package com.danscal.cluster2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class searchFragment extends Fragment implements OnMapReadyCallback {

    final String BASE_URL = "http://ec2-13-58-168-240.us-east-2.compute.amazonaws.com/";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    String friendUsername;
    SearchView searchView;
    //boolean searchReady = false;
    Button searchBTN;
    OnMapReadyCallback mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        mMapView = view.findViewById(R.id.search_map_view);
        searchBTN = view.findViewById(R.id.search_friend_button);
        mContext = this;
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.getMapAsync(mContext);

            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        String username = getDefaults("username", getActivity());
        String token = getDefaults("token", getActivity());
        searchView = getView().findViewById(R.id.search_view);
        friendUsername = searchView.getQuery().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<FavoriteLocation> favoritesList = new ArrayList<>();
        JSONObject jdata = new JSONObject();

        try {
            jdata.put("username", username);
            jdata.put("token", token);
            jdata.put("friendUsername", friendUsername);


        } catch (Exception ex) {}

        StringEntity entity;

        try {
            entity = new StringEntity(jdata.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.get(getActivity(), BASE_URL + "/searchFriendMap", entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String lng = null;
                    String lat = null;
                    String name = null;
                    String type = null;
                    String address = null;

                    try {
                        JSONArray response = new JSONArray(responseBody.toString());
                        for(int i = 0; i < response.length(); i++){
                            JSONObject curObject = response.getJSONObject(i);

                            lng = curObject.getString("lng");
                            lat = curObject.getString("lat");
                            name = curObject.getString("name");
                            type = curObject.getString("type");
                            address = curObject.getString("address");

                        }

                        favoritesList.add(new FavoriteLocation(name, type, address, Double.parseDouble(lat), Double.parseDouble(lng)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println("*Failure*" + statusCode + error);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        } else {
            // Permission has already been granted
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            for(FavoriteLocation newMarker: favoritesList){
                map.addMarker(new MarkerOptions().position(new LatLng(newMarker.lat, newMarker.lng)).title(newMarker.name));
            }


        }
        //there will be a for loop here to gather all of the markers to put on the map frm the lat/lng
        //Double lat = Double.parseDouble(getDefaults("lat", getActivity()));
        //Double lng = Double.parseDouble(getDefaults("lng", getActivity()));
        //String name = getDefaults("name", getActivity());


        //addMapMarkers(map);
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }
    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

}


