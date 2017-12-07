package com.simplecare.slidingmenu;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class MySfvCallback implements Callback {
	
	 private Timer timer;
	 private TimerTask task;
	 
	private final String TAG = "Indoor Location::SurfaceView";
	private final Context context;
	
	
	//private int xGridNum = 18;
	private int xGridNum = 5;
	//private int xGridNum = 28;
	
	private int middleX;
	private int middleY;
	private int w, h;
	private int gridWidth;
	
	public MySfvCallback(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	private void init(SurfaceHolder sfh){
		w = sfh.getSurfaceFrame().width();
		h = sfh.getSurfaceFrame().height();
		gridWidth = w/xGridNum;
		middleX = w/2;
		middleY = h/2;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		startTimer(holder);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		stopTimer();
	}
	
	public void drawOneStep(SurfaceHolder holder){
		Canvas canvas = holder.lockCanvas();
		drawBackground(canvas);
		drawBeacons(IndoorLocation.beacons, canvas);
		drawLocation(IndoorLocation.locationX, IndoorLocation.locationY, canvas);
		holder.unlockCanvasAndPost(canvas);
	}
	
	private void drawBackground(Canvas canvas){
		//canvas.drawColor(Color.rgb(0x11, 0x33, 0x11));
		canvas.drawColor(Color.rgb(0xDE, 0xDE, 0xDE));//背景的color
		
		//draw lines 
		Paint paint = new Paint();
		//paint.setColor(Color.rgb(0x33, 0xcc, 0x33));
		paint.setColor(Color.rgb(0xE2, 0xC2, 0xDE));//線的color
		paint.setStrokeWidth(3);
		
		/////////////這裡在畫座標線
		int i = 0;  
		//draw the vertical lines
		do {
			canvas.drawLine(middleX+i*gridWidth, 0, middleX+i*gridWidth, h, paint);
			canvas.drawLine(middleX-i*gridWidth, 0, middleX-i*gridWidth, h, paint);
			i++;
		} while (middleX+i*gridWidth<w && middleX-i*gridWidth>0);
		
		//draw the horizontal lines
		int j = 0;
		do {
			canvas.drawLine(0, middleY+j*gridWidth, w, middleY+j*gridWidth, paint);
			canvas.drawLine(0, middleY-j*gridWidth, w, middleY-j*gridWidth, paint);
			j++;
		} while (middleY+j*gridWidth<h && middleY-j*gridWidth>0);
		
		Log.d(TAG, "The i and j is " + i + " " + j);
	}
		/////////////這裡在畫座標線
	
	private void drawBeacons(Point[] beacons, Canvas canvas){//beacon的位置
		Bitmap beaconBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.beacon_gray);
		
		for(int i=0; i<beacons.length; i++){
			drawBitmap(beacons[i].x, beacons[i].y, canvas, beaconBmp);
		}
	}
	
	private void drawLocation(double x, double y, Canvas canvas){//location的位置
		Bitmap feet = BitmapFactory.decodeResource(context.getResources(), R.drawable.feet);
		drawBitmap(x, y, canvas, feet);
	}
	
	private void drawBitmap(double x, double y, Canvas canvas, Bitmap bmp){
		int bmpW = bmp.getWidth();
		int bmpH = bmp.getHeight();
		double scale = bmpW > bmpH? 0.5*bmpW/(double) gridWidth: 0.5*bmpH/(double) gridWidth;
		bmp = Bitmap.createScaledBitmap(bmp, (int) (bmpW/scale), (int) (bmpH/scale), true);
		
		int[] xy = convertCoordination(x, y, bmp);
		
		canvas.drawBitmap(bmp, xy[0], xy[1], new Paint());
	}
	
	private int[] convertCoordination(double x, double y, Bitmap bitmap){
		int[] pixelXY = {0, 0};
		pixelXY[0] = (int) (middleX + x*gridWidth - bitmap.getWidth()/2);
		pixelXY[1] = (int) (middleY + y*gridWidth - bitmap.getHeight()/2);
		return pixelXY;
	}
	
	/**
	   * 启动定时器后台线程
	   */
	
	  public void startTimer(final SurfaceHolder holder) {
	      timer = new Timer();
	      task = new TimerTask() {
	          @Override
	          public void run() {
	              //在定时器线程中调用绘图方法
	             // draw();
	        	  init(holder);
	      		drawOneStep(holder);
	          }
	      };
	      //设置定时器每隔0.1秒启动这个task,实现动画效果
	      timer.schedule(task, 100, 100);
	  }
	  /**
	   * 停止定时器线程的方法
	   */
	  public void stopTimer() {
	      timer.cancel();
	  }
	  
	  
}
