package com.simplecare.slidingmenu;


import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.simplecare.slidingmenu.adapter.NavDrawerListAdapter;
import com.simplecare.slidingmenu.model.NavDrawerItem;
import com.simplecare.slidingmenu.model.PersistData;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private Context con=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_main);
		con=this;

		
		/*
		 * set user selected Locale which will change text
		 */
		setLocale();
		
		
		
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		
		
		makeSlideList();

		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
	
	}
	
	
	 private void setLocale() {

	        String languageCode=PersistData.getStringData(con, "LNG");
	        
	        Log.e("from storage", languageCode+" ");

	        //languageCode="bn";
	        if(languageCode.equalsIgnoreCase(""))
	        {
	        	
	            /*
	            Choose device language for first time as User didn't select anything yet.
	             */
	            languageCode=Locale.getDefault().getLanguage();
	         
	            /*
	             * hard code english for first time.
	             */
	            PersistData.setStringData(con,"LNG", "en");

	        }
	        
	        
	        Log.e("Current language code is", languageCode);
	        
	        Locale locale = new Locale(languageCode);
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        con.getApplicationContext().getResources().updateConfiguration(config, null);


	    }
	/*
	 * make slide list
	 */

	private void makeSlideList() {
		// TODO Auto-generated method stub
		
		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbarmenu, menu);
        
        /*
         * change menu item title based on user selection.
         */
        
      
        
 
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
 
        return super.onCreateOptionsMenu(menu);
	}
	
	/*
	 * fo(non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 * 
	 * 
	 * for search bar
	 */
	
	@Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
 
    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.e("Search text is ",query+"" );
 
            /**
             * Use this query to display search results like 
             * 1. Getting the data from SQLite and showing in listview 
             * 2. Making webrequest and displaying the data 
             * For now we just display the query only
             */
Toast.makeText(con, "Query is "+query, 1000).show(); 
        }
 
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_rate:

		
			Toast.makeText(con, "rate button is clicked", 1000).show(); 

			return true;
			
		case R.id.action_best:

			
			Toast.makeText(con, "Best button is clicked", 1000).show(); 

			return true;
			
			
		case R.id.action_liked:

			
			Toast.makeText(con, "liked button is clicked", 1000).show(); 

			return true;
			
			
		case R.id.action_language:
		{
			Toast.makeText(con, "Language button is clicked", 1000).show(); 
			
			
			String lCode="en";
			
			if(PersistData.getStringData(con, "LNG").equalsIgnoreCase("en"))
			{
				
				/*
				 *english is there already, need bangla
				 */
				lCode="bn";
				
				
			
				
				
				
			}else if(PersistData.getStringData(con, "LNG").equalsIgnoreCase("bn"))
			
			{
				lCode="en";
				
				
			}
			
			
			changeLocateAndRestart(lCode);

			return true;
		}
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void changeLocateAndRestart(String lCode) {
		// TODO Auto-generated method stub
		
		
		    Locale locale = new Locale(lCode);
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        con.getApplicationContext().getResources().updateConfiguration(config, null);

	        
	        PersistData.setStringData(con, "LNG", lCode);
	   
	        Log.e("Storing language code ", lCode);
	        
	      
	        
	        /*
	         * most important, can create bug. Rememer the sequence, otherwise it won't work.
	         */
	     
	        
	        /*
	         * Call finish method first
	         * 
	         */
	        MainActivity.this.finish();
	        
	        /*
	         * then call same activity to restart.
	         */
	        
	        Intent i=new Intent(con, MainActivity.class);
		     
		    startActivity(i);

		
	}


	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		
		/*
		 *  active those code if you want to hide options menu when drawer is opened.
		 */
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_best).setVisible(!drawerOpen);
		menu.findItem(R.id.action_liked).setVisible(!drawerOpen);

		menu.findItem(R.id.action_rate).setVisible(!drawerOpen);

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new FindPeopleFragment();
			break;
		case 2:
			fragment = new PhotosFragment();
			break;
		case 3:
			fragment = new CommunityFragment();
			break;
		case 4:
			fragment = new PagesFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls

		
		
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * Store machanism
	 */
	
	
}
