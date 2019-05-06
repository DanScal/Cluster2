package com.danscal.cluster2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.BasicHttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class addLocationFragment extends Fragment {

    EditText locationName;
    EditText locationAddress;
    EditText locationType;
    final String BASE_URL = "http://ec2-13-58-168-240.us-east-2.compute.amazonaws.com/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_location_fragmewnt, container, false);

        //figure out address conversion
        //otherwise just add coordinate edit texts

        Button searchBTN = view.findViewById(R.id.add_location_btn);
        searchBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                locationName = getView().findViewById(R.id.add_location_name_edit_text);
                locationAddress = getView().findViewById(R.id.add_location_addresss_edit_text);
                locationType = getView().findViewById(R.id.add_location_type_edit_text);
                String username = getDefaults("username", getActivity());
                String token = getDefaults("username", getActivity());

                String name = locationName.getText().toString();
                String address = locationAddress.getText().toString();
                String type = locationType.getText().toString();
                String lat = "";
                String lng = "";

                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                    if(addressList.size() > 0) {
                       lat = Double.toString(addressList.get(0).getLatitude());
                       lng = Double.toString(addressList.get(0).getLongitude());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }



                AsyncHttpClient client = new AsyncHttpClient();
//                RequestParams locationParams = new RequestParams();
//                //I think this is just username and token?
//
//                locationParams.add("name", name);
//                locationParams.add("address", address);
//                locationParams.add("type", type);
//                locationParams.add("latitude", latitude);
//                locationParams.add("longitude", longitude);


                JSONObject jdata = new JSONObject();
                try {
                    jdata.put("name", name);
                    jdata.put("address", address);
                    jdata.put("type", type);
                    jdata.put("username", username);
                    jdata.put("token", token);
                    jdata.put("longitude", lng);
                    jdata.put("latitude", lat);
                } catch (Exception ex) {}

                StringEntity entity;
                try {
                    entity = new StringEntity(jdata.toString());
                    //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    client.post(getActivity(), BASE_URL + "/addLocation" , entity , "application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println("***Success***" + statusCode);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            System.out.println("***Failure***" + statusCode + error);


                        }
                    });
                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        return view;
    }
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}


