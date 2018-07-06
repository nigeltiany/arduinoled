package com.tiany.nigel.arduinoled.paid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.tiany.nigel.arduinoled.R;


public class MainActivity extends AppCompatActivity {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    String FragManage;

    public void setTabFragManage(String t){
        FragManage = t;
    }

    public String getTabFragManage(){
        return FragManage;
    }

    ViewPager viewpager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            DB_Handler db_handler = new DB_Handler(getBaseContext());
            db_handler.defaultPreferences();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        viewpager = (ViewPager) findViewById(R.id.mainpager);
        PagerAdapter pageManager = new PagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pageManager);
    }
}

