package ie.smartcommuter.controllers;

import ie.smartcommuter.R;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class NearbyStationsActivity extends TabActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.nearby);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header);
        
        TabHost tabHost = getTabHost();
        View tabView;
        Intent intent;
        TabHost.TabSpec tabSpec;
        
        intent = new Intent().setClass(this, NearbyStationsListActivity.class);
        tabView = createTabView(tabHost.getContext(), "List");
        tabSpec = tabHost.newTabSpec("List").setIndicator(tabView).setContent(intent);
        tabHost.addTab(tabSpec);
        
        intent = new Intent().setClass(this, NearbyStationsMapActivity.class);
        tabView = createTabView(tabHost.getContext(), "Map");
        tabSpec = tabHost.newTabSpec("Map").setIndicator(tabView).setContent(intent);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }
    
    private static View createTabView(final Context context, final String text) {
    	View view = LayoutInflater.from(context).inflate(R.layout.app_tabs, null);
    	TextView tv = (TextView) view.findViewById(R.id.tabsTextView);
    	tv.setText(text);
    	return view;
    }
}
