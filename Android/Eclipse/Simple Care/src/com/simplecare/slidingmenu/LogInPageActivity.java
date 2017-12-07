package com.simplecare.slidingmenu;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogInPageActivity extends Activity implements OnClickListener {
	
	
	static EditText loginText;
	static EditText passText;
	SharedPreferences sh;
	
	public static final String serverUrl = "http://192.168.2.101/simple_care/api/app/get_reg_user";
	//public static final String serverUrl = "http://192.168.43.178/web_sourcefiles/login.php";

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
																// ActionBar
		sh=getSharedPreferences("login text", MODE_PRIVATE);
		String email=sh.getString("loginemail", null);
		String pass=sh.getString("loginpass", null);

		if(email!=null && pass!=null ){
			loginText.setText(email);
			passText.setText(pass);
			//login.performClick();
		}
		loginText = (EditText) findViewById(R.id.editText1);
		passText = (EditText) findViewById(R.id.editText2);
		
		TextView Login, Register, Skip;
		Login = (TextView) findViewById(R.id.Login);
		Register = (TextView) findViewById(R.id.Register);
		Skip = (TextView) findViewById(R.id.Skip);
		
		Login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				get_reg_user();
				
				
			}
		});
		
		Register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			//	Toast.(this, "Toast",  Toast.LENGTH_LONG).show();
				//TextView tv = (TextView) v;
				//Toast.makeText(LogInPageActivity.this, "TEST", Toast.LENGTH_SHORT).show();
			   
				Intent intent = new Intent(LogInPageActivity.this,
						RegisterPageActivity.class);
				startActivity(intent);
				
				
				
			}
		});
		
		Skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			//	Toast.(this, "Toast",  Toast.LENGTH_LONG).show();
				//TextView tv = (TextView) v;
				//Toast.makeText(LogInPageActivity.this, "TEST", Toast.LENGTH_SHORT).show();
			   
				Intent intent = new Intent(LogInPageActivity.this,
						MainActivity.class);
				startActivity(intent);
				
				
				
			}
		});
		
		
	}


	
	
	   private void get_reg_user(){
			final String LoginText = loginText.getText().toString().trim();
			final String PassText = passText.getText().toString().trim();
		     
		        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
		                new Response.Listener<String>() {
		 		     JSONParser jparser=new JSONParser();
				     String result;
		                    @Override
		                    public void onResponse(String response) {
		                  
		                        try {
		                            JSONObject jsonObject = new JSONObject(response);
		                            String result=jsonObject.getString("result");
		                           // Toast.makeText(LogInPageActivity.this,result,Toast.LENGTH_LONG).show();
		            				if(result=="1"){
		            					SharedPreferences.Editor edit=sh.edit();
		            					edit.putString("loginemail", LoginText);
		            					edit.putString("loginpass", PassText);
		            					//edit.putBoolean("type", driver);
		            					edit.commit();
		                                
		            					//Toast.makeText(RegisterPageActivity.this,response,Toast.LENGTH_LONG).show();
		            					Toast.makeText(LogInPageActivity.this,"Login Success",Toast.LENGTH_LONG).show(); 
		            					Intent intent = new Intent(LogInPageActivity.this,
		            							MainActivity.class);
		            					startActivity(intent);
		            				}
		            				else 
		            					Toast.makeText(LogInPageActivity.this,result,Toast.LENGTH_LONG).show();
		            				
		                        } catch (JSONException e) {
		                            e.printStackTrace();
		                        } 
		                        
		                    }
		                },
		                new Response.ErrorListener() {
		                    @Override
		                    public void onErrorResponse(VolleyError error) {
		                        Toast.makeText(LogInPageActivity.this,error.toString(),Toast.LENGTH_LONG).show();
		                    }
		                })
		        {
		            @Override
		            protected Map<String,String> getParams(){
		                Map<String,String> params = new HashMap<String, String>();
		            //    params.put(KEY_USERNAME,username);
		    	        params.put("email", LoginText);
		    	        params.put("password", PassText);
		                
		                //params.put(KEY_PASSWORD,password);
		               // params.put(KEY_EMAIL, email);

		                return params;
		            }
		 
		        }
		        ;
				 
		        RequestQueue requestQueue = Volley.newRequestQueue(this);
		        requestQueue.add(stringRequest);
	        
	    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
