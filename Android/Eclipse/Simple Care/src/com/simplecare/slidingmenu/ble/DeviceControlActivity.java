/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplecare.slidingmenu.ble;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.simplecare.slidingmenu.APointSetting;
import com.simplecare.slidingmenu.GPSTracker;
import com.simplecare.slidingmenu.R;


import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import android.annotation.SuppressLint;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import android.widget.Spinner;

public class DeviceControlActivity extends Activity implements OnClickListener  {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    
    WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	ListView list;
	Spinner sp;
	String wifis[];
	Context mContext;
    EditText editTextMessage,editTextMessage1;
	GPSTracker gps;
	public EditText et;


    private TextView mConnectionState;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    
    
   
    private Button button1 ; 

    
    private BluetoothLeService mBluetoothLeService;
  
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;



    private BluetoothGattCharacteristic characteristic,characteristic1,characteristic2,characteristic3,characteristic4,characteristic5,characteristic6;
    private BluetoothGattService mnotyGattService;;
    
    private BluetoothGattCharacteristic readCharacteristic,readCharacteristic1,readCharacteristic2,readCharacteristic3,readCharacteristic4,readCharacteristic5,readCharacteristic6;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];
    private String spinoutputdata = null;

  
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.ä°
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } 
            //
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	//TX
            	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("D709A00C-DA1A-4726-A33D-CF62B8F4C3D6"));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAEE"));
                characteristic1 = mnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C35"));
                characteristic2 = mnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C36"));
                characteristic3 = mnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C38"));
                characteristic4 = mnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAE6"));
                characteristic5 = mnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C40"));
                characteristic6 = mnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAE8"));
                //RX
                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("D709A00C-DA1A-4726-A33D-CF62B8F4C3D6"));
                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAE6"));
                readCharacteristic1 = readMnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C35"));
                readCharacteristic2 = readMnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C36"));
                readCharacteristic3 = readMnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C38"));
                readCharacteristic4 = readMnotyGattService.getCharacteristic(UUID.fromString("B882467F-77BC-4697-9A4A-4F3366BC6C40"));
                readCharacteristic5 = readMnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAE6"));
                readCharacteristic6 = readMnotyGattService.getCharacteristic(UUID.fromString("61DE21BC-6E02-4631-A0A7-1B6C7AF0DAE8"));
                
                Toast.makeText(getApplicationContext(), "Already get BLE Characteristic", Toast.LENGTH_SHORT).show();
            } 
            //?òæÁ§∫Êï∞?çÆ
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
      
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };    
    

    private void clearUI() {
        
        mDataField.setText(R.string.no_data);
    }
    
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        
        sp = (Spinner) findViewById(R.id.my_spinner1);
	    mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    wifiReciever = new WifiScanReceiver();
	    mainWifiObj.startScan();
		
		final TextView TextViewMessage = (TextView)findViewById(R.id.editText2);
		
		final TextView TextViewMessage1 = (TextView)findViewById(R.id.editText3);
	    
	    editTextMessage = (EditText)findViewById(R.id.editText1);
	    editTextMessage1 = (EditText)findViewById(R.id.editText0);
	    
		TextView skip,Setposition;

		skip = (TextView) findViewById(R.id.set);
		Setposition = (TextView) findViewById(R.id.setposition);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
     
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField =  (EditText) findViewById(R.id.data_value);
        
        SharedPreferences sh;
		sh=getSharedPreferences("login text", MODE_PRIVATE);
		final String email=sh.getString("loginemail", null);
		final String pass=sh.getString("loginpass", null);
        
      
        button1 = (Button) findViewById(R.id.button1);
    
     
       
        	button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				read();

			}
		});
        	
        	Setposition.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				
    				// create class object
    		        gps = new GPSTracker(DeviceControlActivity.this);

    				// check if GPS enabled		
    		        if(gps.canGetLocation()){
    		        	
    		        	double latitude = gps.getLatitude();
    		        	double longitude = gps.getLongitude();
    		        	
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
    				
    				   final int charaProp = characteristic.getProperties();
    	                
    	            
    	                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
    	                    // If there is an active notification on a characteristic, clear
    	                    // it first so it doesn't update the data field on the user interface.
    	                    if (mNotifyCharacteristic != null) {
    	                        mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
    	                        mNotifyCharacteristic = null;
    	                    }
    	                
    	                    //mBluetoothLeService.readCharacteristic(characteristic);
    	                    byte[] value = new byte[20];
    	                    value[0] = (byte) 0x00;
    	                    if(editTextMessage.getText().toString().equals("")){
    	                    	Toast.makeText(getApplicationContext(), "input password", Toast.LENGTH_SHORT).show();
    	                    	return;
    	                    }else{
    	     
    	                    	spinoutputdata = (String) sp.getSelectedItem(); 
    	                    	characteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    	                        characteristic.setValue(spinoutputdata);
    	                        mBluetoothLeService.writeCharacteristic(characteristic);
    	                        
    	                        
    	                        delay();
    	                        delay();
  
    	                        WriteBytes = editTextMessage.getText().toString().getBytes();
    	                        characteristic1.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    	                        characteristic1.setValue(WriteBytes);
    	                        mBluetoothLeService.writeCharacteristic(characteristic1);
    	                        
    	                        delay();
    	                        
    	                       // WriteBytes = editTextMessage1.getText().toString().getBytes();
    	                        characteristic2.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    	                        characteristic2.setValue(email);
    	                        mBluetoothLeService.writeCharacteristic(characteristic2);
    	                        
    	                        
    	                        
    	                        delay();
    	                        
    	                        WriteBytes = TextViewMessage.getText().toString().getBytes();
    	                        characteristic3.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0);
    	                        characteristic3.setValue(WriteBytes);
    	                        mBluetoothLeService.writeCharacteristic(characteristic3);
    	                 
    	                     
    	                        
    	                        
    	                        Toast.makeText(getApplicationContext(), "setting success", Toast.LENGTH_SHORT).show();
    	                    }
    	                }
    	                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
    	                    mNotifyCharacteristic = characteristic;
    	                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
    	                }
    	             
    	
    				
    				//Toast.makeText(DeviceControlActivity.this, "Setting Success", Toast.LENGTH_SHORT).show();
    		
    				//finish(); //terminated this activity
    				
    			}
    		});

        
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /*
	 * **************************************************************
	 * *****************************ËØªÂáΩ?ï∞*****************************
	 */
    private void read() {
    	//mBluetoothLeService.readCharacteristic(readCharacteristic);
    	//readCharacteristic??ÑÊï∞?çÆ??ëÁ?üÂ?òÂ?ñÔ?åÂ?ëÂá∫?öÁü•
    	mBluetoothLeService.readCharacteristic(readCharacteristic);
    	//mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
    	//Toast.makeText(this, "ËØªÊ?êÂ??", Toast.LENGTH_SHORT).show();
    	Toast.makeText(getApplicationContext(), "Read Success", Toast.LENGTH_SHORT).show();
	}
    
    private void delay() {
    	try{
            Thread.sleep(1000); // do nothing for 1000 miliseconds (1 second)
            }catch(InterruptedException e)
             {
              e.printStackTrace();
             }
    		}
    
    @Override
    protected void onResume() {
    	registerReceiver(wifiReciever, new IntentFilter(
	            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
    	unregisterReceiver(wifiReciever);
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }
    


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    class WifiScanReceiver extends BroadcastReceiver {
	    @SuppressLint("UseValueOf")
	    public void onReceive(Context c, Intent intent) {
	        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
	        wifis = new String[wifiScanList.size()];
	        for (int i = 0; i < wifiScanList.size(); i++) {
	            wifis[i] = ((wifiScanList.get(i).SSID));
	        }

	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceControlActivity.this, android.R.layout.simple_spinner_item, wifis);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        sp.setAdapter(adapter);

	    }
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}

