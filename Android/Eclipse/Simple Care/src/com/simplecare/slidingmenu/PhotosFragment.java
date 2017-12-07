package com.simplecare.slidingmenu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotosFragment extends Fragment {
	
	public PhotosFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        //View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        View rootView = inflater.inflate(R.layout.indoorsurfaceview, container, false);
         
        return rootView;
    }
}
