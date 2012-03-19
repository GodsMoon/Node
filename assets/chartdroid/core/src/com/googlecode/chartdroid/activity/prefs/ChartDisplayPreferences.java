package com.googlecode.chartdroid.activity.prefs;

import com.googlecode.chartdroid.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ChartDisplayPreferences extends PreferenceActivity {

	public final static String PREFKEY_BAR_SHADING = "bar_shading";
	public final static String PREFKEY_SCREENSHOT_TRANSPARENCY = "screenshot_transparency";
	
	// XXX These didn't work they way I thought...
	public final static String PREFKEY_SCREENSHOT_ALLOW_CUSTOM_SIZE = "screenshot_allow_custom_size";
	public final static String PREFKEY_SCREENSHOT_WIDTH = "screenshot_width";
	
	public final static String PREFKEY_ENABLE_GRID_LINES = "enable_grid_lines";
	public final static String PREFKEY_ENABLE_HORIZONTAL_GRID_LINES = "enable_grid_lines_horizontal";
	public final static String PREFKEY_ENABLE_VERTICAL_GRID_LINES = "enable_grid_lines_vertical";

	public final static boolean DEFAULT_ENABLE_GRID_LINES = true;
	public final static boolean DEFAULT_ENABLE_HORIZONTAL_GRID_LINES = true;
	public final static boolean DEFAULT_ENABLE_VERTICAL_GRID_LINES = false;
	
	
	public final static String SHARED_PREFS_NAME = "chart_display_prefs";
	
	// ========================================================================
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        getPreferenceManager().setSharedPreferencesName(SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.chart_display_settings);
    }
}
