package com.alim.cse.noticebynu.Fragment.SubFragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alim.cse.noticebynu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedSyllabus extends Fragment {


    public SavedSyllabus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_syllabus, container, false);
    }

}
