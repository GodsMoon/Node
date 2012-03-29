package com.nightshadelabs.node;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
                             
		ScrollView view = (ScrollView) inflater.inflate(R.layout.about, container, false);
		
		((MainActivity) getActivity()).setFooter(0);
		
		return view;
    }
}
