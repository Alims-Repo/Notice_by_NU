package com.alim.cse.noticebynu.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alim.cse.noticebynu.Adapter.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alim.cse.noticebynu.Fragment.SubFragment.SavedNotice;
import com.alim.cse.noticebynu.Fragment.SubFragment.SavedSyllabus;
import com.alim.cse.noticebynu.R;
import com.google.android.material.tabs.TabLayout;

public class SavedFragment extends Fragment {

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved, container, false);

        viewPager = rootView.findViewById(R.id.view_page);

        addTabs(viewPager);
        ((TabLayout) rootView.findViewById(R.id.tabLayout)).setupWithViewPager(viewPager);
        return rootView;
    }
    private void addTabs(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFrag(new SavedSyllabus(), "Syllabus");
        adapter.addFrag(new SavedNotice(), "Notice");
        viewPager.setAdapter(adapter);
    }
}
