package com.simplecare.slidingmenu;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterPageActivity extends Activity implements OnClickListener {
	
	public static final String serverUrl = "http://192.168.2.101/simple_care/api/app/add_user";
	
	static EditText editTextUsername,editTextPassword,editTextEmail,editTextCountry,editTextPhone;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		
		TextView register;
		
		register = (TextView) findViewById(R.id.Register);
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextCountry = (EditText) findViewById(R.id.editTextCountry);
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				registerUser();

			}
		});
		
	
	}

	
	
	   private void registerUser(){
		   final String username = editTextUsername.getText().toString().trim();
		   final String password = editTextPassword.getText().toString().trim();
		   final String email = editTextEmail.getText().toString().trim();
		   final String country = editTextCountry.getText().toString().trim();
		   final String phone = editTextPhone.getText().toString().trim();
	       

		   
	        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
	                new Response.Listener<String>() {
	 		   JSONParser jparser=new JSONParser();
			   int result=0;
	                    @Override
	                    public void onResponse(String response) {
	                        
	                        
	                        try {
	                            JSONObject jsonObject = new JSONObject(response);
	                            result=jsonObject.getInt("result");
	            				if(result==1){
	            					//Toast.makeText(RegisterPageActivity.this,response,Toast.LENGTH_LONG).show();
	            					Toast.makeText(RegisterPageActivity.this,"Registion success",Toast.LENGTH_LONG).show(); 
	            				}
	            				else 
	            					Toast.makeText(RegisterPageActivity.this,"Something wrong with server",Toast.LENGTH_LONG).show();
	            				
	                        } catch (JSONException e) {
	                            e.printStackTrace();
	                        }
	                        
	                    }
	                },
	                new Response.ErrorListener() {
	                    @Override
	                    public void onErrorResponse(VolleyError error) {
	                        Toast.makeText(RegisterPageActivity.this,error.toString(),Toast.LENGTH_LONG).show();
	                    }
	                })
	        {
	            @Override
	            protected Map<String,String> getParams(){
	                Map<String,String> params = new HashMap<String, String>();
	            //    params.put(KEY_USERNAME,username);
	    	        params.put("UserLoginID", username);
	    	        params.put("UserPassCode", password);
	    	        params.put("UserEmail", email);
	    	        params.put("UserName", country);
	    	        params.put("UserPhone", phone);
	                
	                //params.put(KEY_PASSWORD,password);
	               // params.put(KEY_EMAIL, email);
	    	        
	    	        
	                return params;
	            }
	 
	        } 
	        
	        ;
	 
	        RequestQueue requestQueue = Volley.newRequestQueue(this);
	        requestQueue.add(stringRequest);
	        
			Intent intent = new Intent(RegisterPageActivity.this,
					LogInPageActivity.class);
			startActivity(intent); 
	        
	    }
	   
	   
	



		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	
}
