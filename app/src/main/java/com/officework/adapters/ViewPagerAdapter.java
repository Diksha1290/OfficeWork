package com.officework.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish on 7/29/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    List<Fragment> objectList;

    public ViewPagerAdapter(FragmentManager manager, List<Fragment> objectList) {
        super(manager);
        this.objectList = objectList;
    }

/*@Override
public Fragment getItem(int position) {

return mFragmentList.get(position);
}*/

    @Override
    public int getCount() {
        return objectList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }


    @Override
    public Fragment getItem(int position) {
        return objectList.get(position);

    }

}