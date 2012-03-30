package com.nightshadelabs.node;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private Context context;
	private BluetoothService BTService;
	
	private final Handler mHandler = new Handler();
	
	static final int NUM_ITEMS = 4;

    private PagerAdapter mPagerAdapter;

    public ViewPager mPager;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        context = this;
        
        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction xact = fragMgr.beginTransaction();
        if (null == fragMgr.findFragmentByTag("main")) {
        	MainFragment main = new MainFragment();
        	//oreActivity kore = new KoreActivity();
            xact.add(R.id.pager, main, "main");
        }
        xact.commit();
        
        setFooter(R.id.footer_main);
        
        Button about = (Button)findViewById(R.id.about);
        
        about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				aboutClicked();
			}
		});
        
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
            Toast.makeText(this, "Large screen",Toast.LENGTH_LONG).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }
	
	protected void aboutClicked() {
		
		Button kore = (Button)findViewById(R.id.koreButton);
        Button clima = (Button)findViewById(R.id.climaButton);
        Button luma = (Button)findViewById(R.id.lumaButton);
        Button more = (Button)findViewById(R.id.moreButton);
        View footer_main = (View)findViewById(R.id.footer_main);
        
        Animation slideOutLeft = AnimationUtils.loadAnimation(context, R.anim.push_left_out);
        Animation slideOutRight = AnimationUtils.loadAnimation(context, R.anim.push_right_out);
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.push_bottom_out);
        
		kore.startAnimation(slideOutLeft);
		clima.startAnimation(slideOutRight);
		luma.startAnimation(slideOutLeft);
		more.startAnimation(slideOutRight);
		//footer_main.startAnimation(slideDown);
		
		Runnable r = new Runnable() {
			public void run () {
				AboutFragment about = new AboutFragment();
				
				// Execute a transaction, replacing any existing
		        // fragment with this one inside the frame.
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        ft.replace(R.id.pager, about);
		        ft.addToBackStack(null);
		        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		        ft.commit();
			}  
		};
		
		Handler mHandler = new Handler();
		mHandler.postDelayed(r, 400);

	}
	
	public void setFooter(int id){
		View footer_main = (View)findViewById(R.id.footer_main);
		View footer_sensor = (View)findViewById(R.id.footer_sensor);
		
		if(id == 0){
			footer_main.setVisibility(View.GONE);
			footer_sensor.setVisibility(View.GONE);
		}else if(id == R.id.footer_main){
			footer_main.setVisibility(View.VISIBLE);
			footer_sensor.setVisibility(View.GONE);
		}else if(id == R.id.footer_sensor){
			footer_main.setVisibility(View.GONE);
			footer_sensor.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Get local Bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// If BT is not on, show connect screen
        if (!mBluetoothAdapter.isEnabled()) {
        	Intent i = new Intent(context,BluetoothActivity.class);					
        	startActivity(i);
        }else{
        	
        	Node app = (Node) this.getApplication();
        	BTService = app.getBTService(mHandler);    	
        	        	
            if (BTService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (BTService.getState() == BluetoothService.STATE_NONE) {
                  // Start the Bluetooth chat services
                	BTService.start(); 
                }
            }
        }
	}
	

	@Override
	public void onBackPressed() {		
		
		if(mPager.getCurrentItem() == 0)
			super.onBackPressed();
		else
			mPager.setCurrentItem(0,false);

	}

	@Override
	public void onStop() {
		super.onStop();

		// Stop the Bluetooth services
		if (BTService != null) 
			BTService.stop();

	}
	
	public static class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
        	switch(position)
        	{
        	case 0: return new MainFragment();
    			//break;
        	case 1: return new KoreFragment();
        		//break;
        	case 2:return new ClimaFragment();
        		//break;
        	case 3:return new LumaFragment();
    			//break;
        	}
        	
			return null;
            
        }
    }
}
