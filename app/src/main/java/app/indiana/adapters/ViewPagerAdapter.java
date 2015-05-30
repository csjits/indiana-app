package app.indiana.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.indiana.views.HotPostsView;
import app.indiana.views.MyPostsView;
import app.indiana.views.NewPostsView;

/**
 * Created by chris on 04.05.2015.
 * https://gist.github.com/joseedwin84/9838fdc895e5c66a923a#file-viewpageradapter-java
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence mTitles[];
    int mNumTabs;
    HotPostsView mHotPostsView;
    NewPostsView mNewPostsView;
    MyPostsView mMyPostsView;

    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[], int numTabs) {
        super(fm);
        mTitles = titles;
        mNumTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            if (mHotPostsView == null) mHotPostsView = new HotPostsView();
            return mHotPostsView;
        } else if (position == 1) {
            if (mNewPostsView == null) mNewPostsView = new NewPostsView();
            return mNewPostsView;
        } else {
            if (mMyPostsView == null) mMyPostsView = new MyPostsView();
            return mMyPostsView;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mNumTabs;
    }

    public Fragment getView(String type) {
        if (type == "new") {
            return mNewPostsView;
        }
        if (type == "my") {
            return mMyPostsView;
        }
        return mHotPostsView;
    }

    public int getPosition(String type) {
        for (int i = 0; i < mTitles.length; i++) {
            if (String.valueOf(mTitles[i]).equalsIgnoreCase(type)) {
                return i;
            }
        }
        return -1;
    }
}
