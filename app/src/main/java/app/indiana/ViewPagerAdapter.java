package app.indiana;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by chris on 04.05.2015.
 * https://gist.github.com/joseedwin84/9838fdc895e5c66a923a#file-viewpageradapter-java
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence mTitles[];
    int mNumbOfTabs;


    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.mTitles = mTitles;
        this.mNumbOfTabs = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            HotTab hotTab = new HotTab();
            return hotTab;
        } else {
            NewTab newTab = new NewTab();
            return newTab;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }
}
