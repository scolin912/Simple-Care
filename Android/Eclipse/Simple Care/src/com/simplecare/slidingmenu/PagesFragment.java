package com.simplecare.slidingmenu;


import com.simplecare.slidingmenu.ble.DeviceScanActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PagesFragment extends Fragment {
	private ListView listView;
    private View v;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    
	public PagesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 v = inflater.inflate(R.layout.list_view, container, false);
	        listView = (ListView)v.findViewById(R.id.listView1);
	        final String[] arr = new String[]{
	             "WiFi Setting","Indoor Location","Security Feature","Outdoor Traker"
	        };
	        ArrayAdapter<String> adapter = 
	            new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_list_item_1,arr);
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener(){

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position,
	                long id) {
	            	Intent intent;
	                // TODO Auto-generated method stub
	             
	                if(position==0)
	                {
	                	intent = new Intent(getActivity(), APointSetting.class);
	                	//intent = new Intent(getActivity(), DeviceScanActivity.class);
	                	startActivity(intent);
	                	//Toast.makeText(getActivity(), "您選的是第"+position+"張圖", Toast.LENGTH_LONG).show();
	                }
	                
	                if(position==1)
	                {
	                	intent = new Intent(getActivity(), IndoorLocation.class);
	                	startActivity(intent);
	                
	                }
	                
	                if(position==2)
	                {
	                	intent = new Intent(getActivity(), SecurityFeature.class);
	                	startActivity(intent);
	                	
	                }
	                
	                if(position==3)
	                {
	                	intent = new Intent(getActivity(), TrackerMap.class);
	                	startActivity(intent);
	                
	                }
	                
	            }

	        });
	        return v;
    }
	
	

	
}
