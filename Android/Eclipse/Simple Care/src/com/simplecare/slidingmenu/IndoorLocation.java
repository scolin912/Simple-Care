package com.simplecare.slidingmenu;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

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
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import Jama.Matrix;






public class IndoorLocation extends Activity{
	
	int pointSetCount = 6;
    float[] target = {0.5f, 0.5f};//target position
    float noiseVariance = 0.05f;//gaussian noise variance
    float[] testArea = {0, 0, 1.0f, 1.0f};//left, bottom, right, top
    float[] beaconPositions = new float[pointSetCount*2];
    float[] beaconDistances = new float[pointSetCount];

    float width = testArea[2] - testArea[0];
    float height = testArea[3] - testArea[1];
    //limit the generated beacon must has enough space
    float minBeaconDist = (width<height?width:height)/3f;
	
	MqttAndroidClient client;
	
	boolean stopThread=false;
	
	String topic_indoor_location = "";
	
	static String MQTTHOST = "tcp://192.168.2.101:1883";
	static String USERNAME = "sco";//can't used null
	static String PASSWORD = "123";

	private TextView textView1;
	  
	  private SurfaceView sfv;
	  private SurfaceHolder sfh;

	  /*private Point greenXY = new Point(0, -13);						
	  private Point blueXY = new Point(-8, 13);
	  private Point purpleXY = new Point(8, 13);*/

	 /* private Point greenXY = new Point(4, 4);	//new Point(0, -13);					
	  private Point blueXY = new Point(9, 7);// new Point(-8, -13);
	  private Point purpleXY = new Point(9, 1); //new Point(-6, 8);*/
	  
	  private Point greenXY = new Point(-1, 0);	//new Point(0, -13);//第二個					
	  private Point blueXY = new Point(1, 1);// new Point(-8, -13);//第三個
	  private Point purpleXY = new Point(1, -1); //new Point(-6, 8);//第一個
	  
	  public static Point[] beacons;
	  private int beaconsNum =3 ;								

	
	  static double locationX = 0;	
	  static double locationY = 0;
	  
	 /* double a_r=14.5441438;
	  double b_r=3.1795862;
	  double c_r=3.2337256;*/
	  
	  double a_r=4;
	  double b_r=3;
	  double c_r=3.25;
	  
	  private void init(){
		  beacons = new Point[beaconsNum];
		  beacons[0] = greenXY;
		  beacons[1] = blueXY;
		  beacons[2] = purpleXY;
		  

	  }
	  
	  
		
		private Handler handler;
	
	  
	  
	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		  init();
		  super.onCreate(savedInstanceState);
	    
		  topic_indoor_location = "phone/indoor_background";
		  
		  setContentView(R.layout.indoorsurfaceview);
		  
		  textView1 = (TextView) findViewById(R.id.textView1);


		  sfv = (SurfaceView) findViewById(R.id.surfaceView1);
		  sfh = sfv.getHolder();
		  sfh.addCallback(new MySfvCallback(IndoorLocation.this));


		  
		  //double xypoint[] = getMeetingPoints(4.0, 4.0, 9.0, 7.0, 9.0, 1.0, 0.0025, 1.42, 6.96); //x1 y1 x2 y2 x3 y3 r1 r2 r3
		  //double xypoint[] = getMeetingPoints(0, -13, -8, -13, 8, 13, 0.0025, 1.42, 6.96); //x1 y1 x2 y2 x3 y3 r1 r2 r3
		 	 
		 
		  
		  String clientId = MqttClient.generateClientId();
	        client =
	        new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
	                                      clientId);

	        MqttConnectOptions options = new MqttConnectOptions();
	        options.setUserName(USERNAME);
	        options.setPassword(PASSWORD.toCharArray());
	    	
	    	 try {
		            IMqttToken token = client.connect(options);
		            //IMqttToken token = client.connect();
		            token.setActionCallback(new IMqttActionListener() {
		                @Override
		                public void onSuccess(IMqttToken asyncActionToken) {
		                    // We are connected
		                	Toast.makeText(IndoorLocation.this,"Connected",Toast.LENGTH_LONG).show();
		                	setSubscription();
		                }
		         
		                @Override
		                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		                    // Something went wrong e.g. connection timeout or firewall problems
		                	Toast.makeText(IndoorLocation.this,"Connection Failed",Toast.LENGTH_LONG).show();
		         
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
		            
		            
		            	
		            	
		            	
		            //subText.setText(new String(message.getPayload()));
		            
		            
		           // Toast.makeText(SecurityFeature.this,new String(message.getPayload()),Toast.LENGTH_SHORT).show();
		            String result=new String(message.getPayload());
		            
		            
		           
		            String[] AfterSplit = result.split("\\\\");//特殊字元"\"前面要加\\才行割
		            a_r = Double.parseDouble(AfterSplit[1]);
		            b_r = Double.parseDouble(AfterSplit[2]);
		            c_r = Double.parseDouble(AfterSplit[3]);
		            
		          // System.out.println(a_r);
		          // System.out.println(b_r);
		          // System.out.println(c_r);
		  
		            
		            
		        
		            
		            
		            //String input = "10";
		           // int afterConvert = Integer.parseInt(input,2);
		            
		           // int a_r = Integer.parseInt(AfterSplit[1],5);
		          //  System.out.println(numbers[1]);
		          //  Toast.makeText(IndoorLocation.this,numbers[1],Toast.LENGTH_SHORT).show();
		            
	////////////////notification
		           /* NotificationManager mNotificationManager
		            = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		            
		            Intent notifyIntent = new Intent(IndoorLocation.this, MainActivity.class);
		            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            PendingIntent appIntent
		             = PendingIntent.getActivity(IndoorLocation.this, 0, notifyIntent, 0);
		            
		            Notification notification
		            = new Notification.Builder(IndoorLocation.this)
		              .setContentIntent(appIntent)
		              .setSmallIcon(R.drawable.ic_launcher) // 設置狀態列裡面的圖示（小圖示）　　
		              .setLargeIcon(BitmapFactory.decodeResource(IndoorLocation.this.getResources(), R.drawable.ic_launcher)) // 下拉下拉清單裡面的圖示（大圖示）
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
		           mNotificationManager.notify(0, notification);*/
		    ////////////////notification
		            }
		            
		            @Override
		            public void deliveryComplete(IMqttDeliveryToken token){
		            	
		            }



		            });
	         
	        //IMqttToken token = client.connect(options);
	         
	       
	       
		  
			  
			  
			  handler = new Handler() {
		            @Override
		            public void handleMessage(Message msg) {
		                super.handleMessage(msg);
		             // 要做的事情  
		                
		                
		                
		               // double xypoint[] = getMeetingPoints(4, 4, 9, 7, 9, 1, a_r, b_r, c_r); //x1 y1 x2 y2 x3 y3 r1 r2 r3
		                double xypoint[] = getMeetingPoints(-1, 0, 1, 1, 1, -1, a_r, b_r, c_r); //x1 y1 x2 y2 x3 y3 r1 r2 r3
			   		 	 
		              //calculate target position without noise
		                float[] a={-1f, 0f, 1f, 1f, 1f, -1f};

		                float[] b={(float) a_r,(float) b_r,(float) c_r};

		                float[] res = getMultiMeetingPoints(a, b);
		                
		               // float[] res = getMultiMeetingPoints(beaconPositions, beaconDistances);
		                Log.d("calc","res = " + (res[0]) + "," + (res[1]));
		                
		                //System.out.println(a);//8.021
		                //System.out.println(b);//4.13
		                
		               /* String sss=(String) msg.obj;
		                double ddd = Double.parseDouble(sss);
		                
		                double xypoint[] = getMeetingPoints(0, -13, -8, -13, -6, 8, ddd, b_r, c_r); //x1 y1 x2 y2 x3 y3 r1 r2 r3
		  			  */
		  			  locationX = xypoint[0];
		  			  locationY = xypoint[1];//??�為y??�相??��?�好?��?��??��??
		  			  
		                textView1.setText((String) msg.obj);
		                
		                
		                
		                MqttGetPositiion();
		                
		                
		            }
		        };
		        new Thread(new thread()).start();
			  
			  
		  

	  }
	  
	  
	  private void MqttGetPositiion(){
		  String topic = topic_indoor_location;
          topic_indoor_location = "hello/indoor_location";
          String message = "indoor";
          try {
              client.publish(topic, message.getBytes(),0,false);
             // Toast.makeText(SecurityFeature.this,topic,Toast.LENGTH_LONG).show();
              
          } catch (MqttException e) {
              e.printStackTrace();
          }
	    }
	  
	  @Override
	    protected void onDestroy() {//如果沒這個onDestroy，退到背景還是still run toast
			//timer.cancel();
		  stopThread=true;
	        super.onDestroy();
	    }
	  
	// 每秒更新textview
	    public class thread implements Runnable {
	        public void run() {
	            int c = 0;
	            while (!stopThread) {
	                try {
	                    Thread.sleep(1000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                // 将要更新的内容发给handler
	                Message msg = new Message();
	                msg.obj = "" + (c++);
	                handler.sendMessage(msg);
	            }
	        }
	    }
	  
	  
	  private void setSubscription(){
	    	try{
	    		client.subscribe(topic_indoor_location, 0);
	    	}
	    	catch (MqttException e) {
	            e.printStackTrace();
	        }
	    }


	  double norm (double x,double y) // get the norm of a vector
	  {
	      return (float) Math.pow(Math.pow(x,2)+Math.pow(y,2),0.5);
	  }
	  
	  private double[] getMeetingPoints(double x1, double y1, double x2, double y2, double x3, double y3, double r1, double r2, double r3)
	  {

		  double p2p1Distance = (Math.pow(Math.pow(x2-x1,2) + Math.pow(y2-y1,2),0.5)); 
		  
		 
		  //System.out.println(p2p1Distance); //5.83095 正確
		 

		  
		  double ex[] = {(x2-x1)/p2p1Distance, (y2-y1)/p2p1Distance};
		  
		  
		  //System.out.println(ex[0]);//0.85749正確
		  //System.out.println(ex[1]); //0.51449正確

		  double aux[] = {x3-x1,y3-y1}; 
		  
		 
		 // System.out.println(aux[0]); //5.0正確
		  //System.out.println(aux[1]); //-3.0正確
	      
	      //signed magnitude of the x component
		  double i = ex[0] * aux[0] + ex[1] * aux[1]; 
		  
		  
		  //System.out.println(i);//2.74397正確

		
		  double aux2[] = { x3-x1-i*ex[0], y3-y1-i*ex[1]};
		  
		  
		  //System.out.println(aux2[0]); //2.647058 不知道
		  
		 
		  //System.out.println(aux2[1]); //-4.41176 不知道
		  
		    DecimalFormat df = new DecimalFormat("##.000");//??�捨五入小數點第三�??
		    double d1 = Double.parseDouble(df.format(aux2[0]));
		    double d2 = Double.parseDouble(df.format(aux2[1]));
		    

			double ey[] = { d1 / norm (d1,d2), d2 / norm (d1,d2) };
	      //the signed magnitude of the y component
		    DecimalFormat df1 = new DecimalFormat("##.000");
		    double d11 = Double.parseDouble(df1.format(ey[0]));
		    double d21 = Double.parseDouble(df1.format(ey[1]));
		    
		    
			//System.out.println(d11); //0.514正確
			
			//System.out.println(d21);//0.858 正確
		  

		  
		 
		    //double j = d11 * d1 + d21 * d2;
		    double j = d11 * aux[0] + d21 * aux[1];
		    DecimalFormat jj = new DecimalFormat("##.000");
		    double jj1 = Double.parseDouble(jj.format(j));
		    
		    
			//System.out.println(jj1);//5.144正確


		  double x = ((Math.pow(r1,2) - Math.pow(r2,2) + Math.pow(p2p1Distance,2))/ (2 * p2p1Distance));
		  DecimalFormat xx = new DecimalFormat("##.000");
		  double xx1 = Double.parseDouble(xx.format(x));
		  
		  
		  //System.out.println(xx1); //3.516正確
		  
		  double y = (Math.pow(r1,2) - Math.pow(r3,2) + Math.pow(i,2) + Math.pow(jj1,2))/(2*jj1) - i*x/jj1;
		  DecimalFormat yy = new DecimalFormat("##.000");
		  double yy1 = Double.parseDouble(yy.format(y));
		  
		  
		  //System.out.println(yy1); //1.957正確

		 
		  double finalXY[] = {x1+ x*ex[0] + y*d11, y1+ x*ex[1] + y*d21};
		  
		  
		  DecimalFormat xy = new DecimalFormat("##.000");
		  double xy1 = Double.parseDouble(xy.format(finalXY[0]));
		  double xy2 = Double.parseDouble(xy.format(finalXY[1]));
		  
		  
		  
		  double finalXY_return[]={xy1,xy2};
		    
		 // System.out.println("xy1,xy2");
		 // System.out.println(xy1);//8.021
         // System.out.println(xy2);//4.13
    
          
          

	      return finalXY_return;

	  }
	  
	  
	    /**
	     * Multiple point positioning function.
	     * This function needs "jama" library to support matrix calculation.
	     * @param positions Beacon position. Format is {x1, y1, x2, y2, ...}
	     * @param distances Beacon to target distance. Format is {d1, d2, ...}
	     * @return Target position. Format is {x, y}
	     */
	    private float[] getMultiMeetingPoints(float[] positions, float[] distances){
	        //if input array length not correct, return NaN
	        float[] res = {Float.NaN, Float.NaN};
	        int pointCount = distances.length;
	        //pointCount must greater than 3
	        if(pointCount < 2){
	            return res;
	        }
	        //position data length must greater that double of pointCount
	        if(positions.length < pointCount*2){
	            return res;
	        }
	        //save first point as offset value
	        float[] offset = {positions[0], positions[1]};
	        //apply offset
	        for(int i = 0; i < pointCount; i++){
	            positions[i*2] -= offset[0];
	            positions[i*2 + 1] -= offset[1];
	        }
	        //create matrix
	        Matrix mPos = new Matrix(pointCount-1,2);
	        Matrix mDist = new Matrix(pointCount-1, 1);
	        //calculate values in matrix
	        for (int i = 0; i < pointCount-1; i++) {
	            mPos.set(i, 0, 2 * positions[0] - 2 * positions[(i + 1) * 2]);
	            mPos.set(i, 1, 2 * positions[1] - 2 * positions[(i + 1) * 2 + 1]);

	            double rsq = Math.pow(distances[i+1], 2) - Math.pow(distances[0], 2);
	            double xsq = Math.pow(positions[0], 2) - Math.pow(positions[(i + 1) * 2], 2);
	            double ysq = Math.pow(positions[0], 2) - Math.pow(positions[(i + 1) * 2 + 1], 2);
	            mDist.set(i, 0,rsq+xsq+ysq);
	        }
	        //inverse position matrix and times distance matrix
	        //it will use pseudo inverse when matrix is not a square matrix
	        Matrix mRes = mPos.inverse().times(mDist);
	        //add offset to result
	        res[0] = (float)mRes.get(0,0)+offset[0];
	        res[1] = (float)mRes.get(1,0)+offset[1];
	        //restore position data
	        for(int i = 0; i < pointCount; i++){
	            positions[i*2] += offset[0];
	            positions[i*2 + 1] += offset[1];
	        }
	        return res;
	    }
	
	

}
