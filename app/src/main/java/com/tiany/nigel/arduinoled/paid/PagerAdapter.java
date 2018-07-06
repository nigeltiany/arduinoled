package com.tiany.nigel.arduinoled.paid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Nigel on 10/24/2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private String tabtitles[] = new String[] { "Home", "Setup Pins", "Set Colors", "Play" };

    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new BluetoothV2();
            case 1:
                return new Config_Activity();
            case 2:
                return new Color_Activity();
            case 3:
                return new play_Activity();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

}
