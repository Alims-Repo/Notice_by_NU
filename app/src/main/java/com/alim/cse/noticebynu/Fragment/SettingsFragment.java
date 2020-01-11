package com.alim.cse.noticebynu.Fragment;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alim.cse.noticebynu.Database.AppSettings;
import com.alim.cse.noticebynu.ErrorActivity;
import com.alim.cse.noticebynu.MainActivity;
import com.alim.cse.noticebynu.R;
import com.alim.cse.noticebynu.Services.Updater;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Objects;

public class SettingsFragment extends Fragment implements Updater.Callbacks {

    Updater updater;
    LinearLayout update;
    LinearLayout error;
    Chip auto, on, off;
    Chip n_on, n_off;
    AppSettings appSettings;
    ChipGroup notification;
    ChipGroup darkMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        appSettings = new AppSettings(getActivity());
        darkMode = rootView.findViewById(R.id.dark_mode);
        notification = rootView.findViewById(R.id.saved);

        error = rootView.findViewById(R.id.error);
        update = rootView.findViewById(R.id.check_update);
        auto = rootView.findViewById(R.id.auto);
        on = rootView.findViewById(R.id.on);
        off = rootView.findViewById(R.id.off);
        n_on = rootView.findViewById(R.id.n_on);
        n_off = rootView.findViewById(R.id.n_off);

        updater = new Updater(getActivity());
        updater.registerClient(getActivity());

        switch (appSettings.getTHEME()) {
            case 0:
                darkMode.check(R.id.auto);
                ChipDisable(auto,on,off);
                break;
            case 1:
                darkMode.check(R.id.on);
                ChipDisable(on,off,auto);
                break;
            case 2:
                darkMode.check(R.id.off);
                ChipDisable(off,auto,on);
                break;
        }

        if (appSettings.getAUTOSAVE()) {
            notification.check(R.id.n_on);
            n_on.setClickable(false);
        }
        else {
            notification.check(R.id.n_off);
            n_off.setClickable(false);
        }

        darkMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                switch (i) {
                    case R.id.auto:
                        appSettings.setTHEME(0);
                        ChipDisable(auto,on,off);
                        break;
                    case R.id.on:
                        appSettings.setTHEME(1);
                        ChipDisable(on,off,auto);
                        break;
                    case R.id.off:
                        appSettings.setTHEME(2);
                        ChipDisable(off,auto,on);
                        break;
                }
                Recreate();
            }
        });

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ErrorActivity.class));
            }
        });

        notification.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if (i==R.id.n_on) {
                    n_on.setClickable(false);
                    n_off.setClickable(true);
                    appSettings.setAUTOSAVE(true);
                } else {
                    n_on.setClickable(true);
                    n_off.setClickable(false);
                    appSettings.setAUTOSAVE(false);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updater.new Version().execute();
            }
        });

        return rootView;
    }

    private void Recreate() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("FROM","SETTINGS");
                startActivity(i);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getActivity().finish();
            }
        }, 200);
    }

    private void ChipDisable(Chip one,Chip two, Chip three) {
        one.setClickable(false);
        two.setClickable(true);
        three.setClickable(true);
    }

    @Override
    public void updateClient(int call) {

    }
}