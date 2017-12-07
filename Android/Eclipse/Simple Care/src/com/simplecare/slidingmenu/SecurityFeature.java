package com.simplecare.slidingmenu;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;



public class SecurityFeature extends Activity implements OnClickListener {
	MqttAndroidClient client;
	String topicArm = "";
	String topicDisArm = "";
	
	String topic_arm_disarm = "";
	
	static String MQTTHOST = "tcp://192.168.2.101:1883";
	static String USERNAME = "sco";//can't used null
	static String PASSWORD = "123";
	
	SharedPreferences sh;
	
	TextView subText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security_feature);
		
		sh=getSharedPreferences("login text", MODE_PRIVATE);
		String email=sh.getString("loginemail", null);
		
		subText = (TextView)findViewById(R.id.subText);
		
		//topicArm = "/arm"+email; //將這個email下面的主機全部arm
		topicArm = "home/"+email;
		
		topicDisArm = "home/"+email;//將這個email下面的主機全部disarm
		
		topic_arm_disarm = "home/"+email;
		
	       String clientId = MqttClient.generateClientId();
	        client =
	        new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
	                                      clientId);

	       MqttConnectOptions options = new MqttConnectOptions();
	        options.setUserName(USERNAME);
	        options.setPassword(PASSWORD.toCharArray());
	         
	        //IMqttToken token = client.connect(options);
	         
	        try {
	            IMqttToken token = client.connect(options);
	            //IMqttToken token = client.connect();
	            token.setActionCallback(new IMqttActionListener() {
	                @Override
	                public void onSuccess(IMqttToken asyncActionToken) {
	                    // We are connected
	                	Toast.makeText(SecurityFeature.this,"Connected",Toast.LENGTH_LONG).show();
	                	setSubscription();
	                }
	         
	                @Override
	                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
	                    // Something went wrong e.g. connection timeout or firewall problems
	                	Toast.makeText(SecurityFeature.this,"Connection Failed",Toast.LENGTH_LONG).show();
	         
	                }
	            });
	        } catch (MqttException e) {
	            e.printStackTrace();
	        }
	        
	        client.setCallback(new MqttCallback(){
	            @Override
	            public void connectionLost(Throwable cause){
	            	
	            }
	            
	            @Override
	            public void messageArrived(String topic, MqttMessage message)throws Exception{
	            	// TODO Auto-generated method stub
	           // subText.setText(new String(message.getPayload()));	
	          //  vibrator.vibrate(500);
	          //  myRingtone.play();
	            
	            
	            	
	            	
	            	
	            subText.setText(new String(message.getPayload()));
	            
	            
	           // Toast.makeText(SecurityFeature.this,new String(message.getPayload()),Toast.LENGTH_SHORT).show();
	            String result=new String(message.getPayload());
	            Toast.makeText(SecurityFeature.this,result,Toast.LENGTH_SHORT).show();
	            
////////////////notification
	            NotificationManager mNotificationManager
	            = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	            
	            Intent notifyIntent = new Intent(SecurityFeature.this, MainActivity.class);
	            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            PendingIntent appIntent
	             = PendingIntent.getActivity(SecurityFeature.this, 0, notifyIntent, 0);
	            
	            Notification notification
	            = new Notification.Builder(SecurityFeature.this)
	              .setContentIntent(appIntent)
	              .setSmallIcon(R.drawable.ic_launcher) // 設置狀態列裡面的圖示（小圖示）　　
	              .setLargeIcon(BitmapFactory.decodeResource(SecurityFeature.this.getResources(), R.drawable.ic_launcher)) // 下拉下拉清單裡面的圖示（大圖示）
	              .setTicker("notification on status bar.") // 設置狀態列的顯示的資訊
	              .setWhen(System.currentTimeMillis())// 設置時間發生時間
	              .setAutoCancel(true) // 設置通知被使用者點擊後是否清除  //notification.flags = Notification.FLAG_AUTO_CANCEL;
	              .setContentTitle("Notification Title") // 設置下拉清單裡的標題
	              .setContentText("Notification Content")// 設置上下文內容
	              .setOngoing(true)      //true使notification变为ongoing，用户不能手动清除  // notification.flags = Notification.FLAG_ONGOING_EVENT; notification.flags = Notification.FLAG_NO_CLEAR;

	              .setDefaults(Notification.DEFAULT_ALL) //使用所有默認值，比如聲音，震動，閃屏等等
	           // .setDefaults(Notification.DEFAULT_VIBRATE) //使用默認手機震動提示
	           // .setDefaults(Notification.DEFAULT_SOUND) //使用默認聲音提示
	           // .setDefaults(Notification.DEFAULT_LIGHTS) //使用默認閃光提示
	           // .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND) //使用默認閃光提示 與 默認聲音提示

	           // .setVibrate(vibrate) //自訂震動長度
	           // .setSound(uri) //自訂鈴聲
	           // .setLights(0xff00ff00, 300, 1000) //自訂燈光閃爍 (ledARGB, ledOnMS, ledOffMS)
	               .build();

	           //把指定ID的通知持久的發送到狀態條上
	           mNotificationManager.notify(0, notification);
	    ////////////////notification
	            }
	            
	            @Override
	            public void deliveryComplete(IMqttDeliveryToken token){
	            	
	            }



	            });
	        
	}
	
	public void Arm(View v){
        //String topic = topicArm;
        String topic = topic_arm_disarm;
        String message = "Armed";
        try {
            client.publish(topic, message.getBytes(),0,false);
           // Toast.makeText(SecurityFeature.this,topic,Toast.LENGTH_LONG).show();
            
        } catch (MqttException e) {
            e.printStackTrace();
        }
	}
	
	public void disArm(View v){
        //String topic = topicDisArm;
        String topic = topic_arm_disarm;
        String message = "Disarmed";
        try {
            client.publish(topic, message.getBytes(),0,false);
           // Toast.makeText(SecurityFeature.this,topic,Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
	}

	 private void setSubscription(){
	    	try{
	    		client.subscribe(topic_arm_disarm, 0);
	    	}
	    	catch (MqttException e) {
	            e.printStackTrace();
	        }
	    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
