package com.eze.chrisantus.emergencyreporter;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class TipsFragment extends Fragment {
    private Button mBtnSecurity, mBtnClinic, mBtnFire;


    public TipsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        mBtnClinic = view.findViewById(R.id.btn_clinical1);
        mBtnFire = view.findViewById(R.id.btn_fire1);
        mBtnSecurity = view.findViewById(R.id.btn_security1);

        if (getContext() != null) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            mBtnFire.setTypeface(typeface);
            mBtnSecurity.setTypeface(typeface);
            mBtnClinic.setTypeface(typeface);
        }

        mBtnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerIntent(Constants.securityCode);
            }
        });

        mBtnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerIntent(Constants.fireCode);
            }
        });

        mBtnClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerIntent(Constants.clinicCode);
            }
        });

        return view;
    }

    private void triggerIntent(String key) {
        Intent intent = new Intent(getContext(), TipsListActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

}
