package com.nightshadelabs.node;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;

public class MainFragment extends Fragment {
	
	MainActivity context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	 
	    ScrollView view = (ScrollView) inflater.inflate(R.layout.main_nav, container, false);
	 
	    context = (MainActivity) getActivity();
	    context.setFooter(R.id.footer_main);
        
        Button kore = (Button)view.findViewById(R.id.koreButton);
        Button clima = (Button)view.findViewById(R.id.climaButton);
        Button luma = (Button)view.findViewById(R.id.lumaButton);
        Button more = (Button)view.findViewById(R.id.moreButton);
        
        Animation slideInLeft = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
        Animation slideInRight = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
        
		kore.startAnimation(slideInLeft);
		clima.startAnimation(slideInRight);
		luma.startAnimation(slideInLeft);
		more.startAnimation(slideInRight);
        
        kore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				KoreFragment kore = new KoreFragment();				
                animateOut2NewFragment(kore,v);
			}
		});
        
        clima.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				ClimaFragment clima = new ClimaFragment();
				animateOut2NewFragment(clima,v);
			}
		});
        
        luma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				LumaFragment luma = new LumaFragment();
				animateOut2NewFragment(luma,v);
				
			}
		});
	    
	    return view;
	}
	
	protected void animateOut2NewFragment(final Fragment f,View v) {
		
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_right_out);
		v.startAnimation(animation);
		
		Runnable r = new Runnable() {
			public void run () {
				
				if(f instanceof KoreFragment)
					context.mPager.setCurrentItem(1,true);
				if(f instanceof ClimaFragment)
					context.mPager.setCurrentItem(2,false);
				if(f instanceof LumaFragment)
					context.mPager.setCurrentItem(3,false);
				
				
				// Execute a transaction, replacing any existing
		        // fragment with this one inside the frame.
		        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
		        ft.replace(R.id.pager, f);
		        ft.addToBackStack(null);
		        //ft.setCustomAnimations(R.animator.slide_in, R.animator.slide_in);
		        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		        ft.commit();*/
			}  
		};
		
		Handler mHandler = new Handler();
		mHandler.postDelayed(r, 400);

	}

	@Override
	public void onPause() {
		super.onPause();
		
		
	}
    
}