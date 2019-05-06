package com.danscal.cluster2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class LandingPage extends AppCompatActivity {
    final String BASE_URL = "http://ec2-13-58-168-240.us-east-2.compute.amazonaws.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

    }

    public void registerUser(View view) {
        EditText getUsername = findViewById(R.id.enterUsername);
        final String username = getUsername.getText().toString();
        EditText getPassword = findViewById(R.id.enterPassword);
        final String password = getPassword.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jdata = new JSONObject();
        try {
            jdata.put("username", username);
            jdata.put("password", password);
        } catch (Exception ex) {}

        StringEntity entity;

        //have empty request params instead of doing the string? what's going on here

        try {
            entity = new StringEntity(jdata.toString());
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(getApplicationContext(), BASE_URL + "/register" , entity , "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("***Success***" + statusCode);
                String token;
                //deal with Response headers differently
                try {
                    Intent myProfileEditIntent = new Intent(LandingPage.this, MainActivity.class);
                    //error from here
                    JSONArray response = new JSONArray(new String(responseBody));
                    token = response.getJSONObject(0).get("token").toString();
                    String usernameResponse = response.getJSONObject(0).get("username").toString();

                    myProfileEditIntent.putExtra("username", usernameResponse);
                    myProfileEditIntent.putExtra("token", token);
                    startActivity(myProfileEditIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent myProfileEditIntent = new Intent(LandingPage.this, MainActivity.class);
                startActivity(myProfileEditIntent);
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

    public void loginUser(View view){
        EditText getUsername = findViewById(R.id.enterUsername);
        final String username = getUsername.getText().toString();
        EditText getPassword = findViewById(R.id.enterPassword);
        String password = getPassword.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();

//        JSONObject jdata = new JSONObject();
//        try {
//            jdata.put("username", username);
//            jdata.put("password", password);
//        } catch (Exception ex) {}

//        StringEntity entity;

        RequestParams requestParams = new RequestParams();
        requestParams.add("username", username);
        requestParams.add("password", password);
        Header contentType = new Header() {
            @Override
            public String getName() {
                return "Content-Type";
            }

            @Override
            public String getValue() {
                return "application/json";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        Header[] headers = new Header[1];
        headers[0] = contentType;


//        try {
            //entity = new StringEntity(jdata.toString());
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.get(this, BASE_URL + "/login", headers, requestParams, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    System.out.println("***Success***" + statusCode);
                    String token;
                    //deal with Response headers differently
                    try {
                        Intent myProfileEditIntent = new Intent(LandingPage.this, MainActivity.class);
                        //JSONArray response = new JSONArray(responseBody);
                        token = responseBody.getString("token");
                        String usernameResponse = responseBody.getString("username");

                        myProfileEditIntent.putExtra("username", usernameResponse);
                        myProfileEditIntent.putExtra("token", token);
                        startActivity(myProfileEditIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                    System.out.println("***Failure***" + statusCode + error);
                }
            });
        }
//        catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
    }


