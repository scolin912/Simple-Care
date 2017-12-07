package com.simplecare.slidingmenu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class APointSetting extends Activity implements OnClickListener {
	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	ListView list;
	Spinner sp;
	String wifis[];
	Context mContext;
	
    EditText editTextMessage,editTextMessage1;
    

	GPSTracker gps;
    
	 private static final String TAG = "Client";
	private Socket socket;
   public SeekBar seekBar;
	private static int SERVERPORT_MAIN = 0;
	private static String SERVER_MAIN = "";
	private String serverinputdata = null;
	private String serveroutputdata = null;
	public EditText et;
	private Handler mHandler;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b_point_setting);
	    sp = (Spinner) findViewById(R.id.my_spinner1);
	    mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    wifiReciever = new WifiScanReceiver();
	    mainWifiObj.startScan();
	    
		SERVERPORT_MAIN = 80;
		SERVER_MAIN = "10.10.10.1"; //Linkit7697
	    //SERVER_MAIN = "192.168.4.1"; //esp8266
	    //SERVER_MAIN = "192.168.1.1"; //ameba
		new Thread(new ClientThread()).start();
		mHandler = new Handler();
    //    mHandler.post(mUpdate);
		
		final TextView TextViewMessage = (TextView)findViewById(R.id.editText2);
		
		final TextView TextViewMessage1 = (TextView)findViewById(R.id.editText3);
	    
	    editTextMessage = (EditText)findViewById(R.id.editText1);
	    editTextMessage1 = (EditText)findViewById(R.id.editText0);
	    	    

		TextView skip,Setposition;

		skip = (TextView) findViewById(R.id.set);
		Setposition = (TextView) findViewById(R.id.setposition);
		
		SharedPreferences sh;
		sh=getSharedPreferences("login text", MODE_PRIVATE);
		final String email=sh.getString("loginemail", null);
		final String pass=sh.getString("loginpass", null);


		
		Setposition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// create class object
		        gps = new GPSTracker(APointSetting.this);

				// check if GPS enabled		
		        if(gps.canGetLocation()){
		        	
		        	double latitude = gps.getLatitude();
		        	double longitude = gps.getLongitude();
		        	
		        	// \n is for new line
		        	//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
		        	
		        	CharSequence cs=String.valueOf(latitude);
		        	CharSequence cs1=String.valueOf(longitude);
		        	TextViewMessage.setText(cs);
		        	TextViewMessage1.setText(cs1); 
		        }else{
		        	// can't get location
		        	// GPS or Network is not enabled
		        	// Ask user to enable GPS/network in settings
		        	gps.showSettingsAlert();
		        }
				
			}
		});
		
		skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
	    		try {
	    			
	    			PrintWriter out = new PrintWriter(new BufferedWriter(
	    					new OutputStreamWriter(socket.getOutputStream())),
	    					true);

	    			serveroutputdata = "\\"+"v"+"\\"+(String) sp.getSelectedItem()+"\\"+editTextMessage.getText().toString()+"\\"+editTextMessage1.getText().toString()+"\\"+TextViewMessage.getText().toString()+"\\"+TextViewMessage1.getText().toString()+"\\"+email+"\\"+"\r\n"; //set point A and select WiFi AMEBA
	    			out.println(serveroutputdata);
	    			
	    			//et.setText("");

	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
				
				
	
				TextView tv = (TextView) v;
				Toast.makeText(APointSetting.this, "Setting Success", Toast.LENGTH_SHORT).show();
		
				finish(); //terminated this activity
				
			}
		});
		


	}
	
 
	class ClientThread implements Runnable {

		@Override
		public void run() {
			
			BufferedReader br;

			
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_MAIN);
				socket = new Socket(serverAddr, SERVERPORT_MAIN);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
				
				while (true) {
	
				    serverinputdata=br.readLine();
				    Log.i(TAG, "DATA:"+ serverinputdata);
	
				}
		
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onPause() {
	    unregisterReceiver(wifiReciever);
	    super.onPause();
	}

	protected void onResume() {
	    registerReceiver(wifiReciever, new IntentFilter(
	            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    super.onResume();
	}

	class WifiScanReceiver extends BroadcastReceiver {
	    @SuppressLint("UseValueOf")
	    public void onReceive(Context c, Intent intent) {
	        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
	        wifis = new String[wifiScanList.size()];
	        for (int i = 0; i < wifiScanList.size(); i++) {
	            wifis[i] = ((wifiScanList.get(i).SSID));
	        }

	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(APointSetting.this, android.R.layout.simple_spinner_item, wifis);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        sp.setAdapter(adapter);

	    }
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
