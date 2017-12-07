package com.simplecare.slidingmenu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
public class TrackerMap extends Activity {

    private double lat1,lng1,lat2,lng2;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_map);
        lat1 = 25.038963; //User home location
        lng1 = 121.536624; //User home location
        lat2 = 25.048051; //Now user location
        lng2 = 121.517071; //Now user location
        WebView wv_map = (WebView)findViewById(R.id.wv);
        wv_map.getSettings().setJavaScriptEnabled(true);
//class use AndroidFunction pass to WebView
        wv_map.addJavascriptInterface(TrackerMap.this , "AndroidFunction");
//open website
        wv_map.loadUrl("file:///android_asset/index.html");
   }

    @JavascriptInterface
    public double getLat1(){ 
        return lat1;
    }
    @JavascriptInterface
    public double getLng1(){
        return lng1;
    }
    @JavascriptInterface
    public double getLat2(){
        return lat2;
    }
    @JavascriptInterface
    public double getLng2(){
        return lng2;
    }
}
