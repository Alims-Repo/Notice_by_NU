package com.alim.cse.noticebynu.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alim.cse.noticebynu.Adapter.PagerAdapter;
import com.alim.cse.noticebynu.Fragment.SubFragment.SyllabusDegree;
import com.alim.cse.noticebynu.Fragment.SubFragment.SyllabusHonours;
import com.alim.cse.noticebynu.Fragment.SubFragment.SyllabusMasters;
import com.alim.cse.noticebynu.Fragment.SubFragment.SyllabusProfessionals;
import com.alim.cse.noticebynu.R;
import com.google.android.material.tabs.TabLayout;

public class SyllabusFragment extends Fragment {

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_syllabus, container, false);

        viewPager = rootView.findViewById(R.id.view_page);

        addTabs(viewPager);
        ((TabLayout) rootView.findViewById(R.id.tabLayout)).setupWithViewPager(viewPager);

        return rootView;
    }

    private void addTabs(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFrag(new SyllabusHonours(), "Honours");
        adapter.addFrag(new SyllabusProfessionals(), "Professionals");
        adapter.addFrag(new SyllabusDegree(), "Degree-Pass");
        adapter.addFrag(new SyllabusMasters(), "Masters");
        viewPager.setAdapter(adapter);
    }
}
