package com.eze.chrisantus.emergencyreporter;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView mTvName, mTvEmail, mTvMatNo, mTvGender, mTvLevel, mTvDepartment, mTvPhone
            , mTvBloodgroup, mTvGuardian_Phone_No;
    private TextView mTvEmailTag, mTvMatNoTag,mTvGenderTag, mTvLevelTag, mTvDepartmentTag, mTvPhoneTag
            , mTvBloodgroupTag, mTvGuardian_Phone_NoTag;
    private ImageView mImageIcon;

    private LinearLayout mLLMatNo, mLLLevel, mLLDepartment, mLLGuardian_Phone_No, mLL;
    private ProgressBar mProgressbar;

    private FloatingActionButton mFab;

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String authtype = "";


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mUserDatabase.keepSynced(true);

        mTvName = view.findViewById(R.id.name);
        mTvEmail = view.findViewById(R.id.email);
        mTvMatNo = view.findViewById(R.id.matno);
        mTvGender = view.findViewById(R.id.gender);
        mTvLevel = view.findViewById(R.id.level);
        mTvDepartment = view.findViewById(R.id.dept);
        mTvPhone = view.findViewById(R.id.phone);
        mTvBloodgroup = view.findViewById(R.id.blood_group);
        mTvGuardian_Phone_No = view.findViewById(R.id.guardian);

        mTvEmailTag = view.findViewById(R.id.email_tag_tag);
        mTvMatNoTag = view.findViewById(R.id.matno_tag);
        mTvLevelTag = view.findViewById(R.id.level_tag);
        mTvDepartmentTag = view.findViewById(R.id.dept_tag);
        mTvPhoneTag = view.findViewById(R.id.phone_tag);
        mTvGenderTag = view.findViewById(R.id.gender_tag);
        mTvBloodgroupTag = view.findViewById(R.id.blood_group_tag);
        mTvGuardian_Phone_NoTag = view.findViewById(R.id.guardian_tag);

        mImageIcon = view.findViewById(R.id.star);

        mLLDepartment = view.findViewById(R.id.dept_ll);
        mLLGuardian_Phone_No = view.findViewById(R.id.guardian_ll);
        mLLLevel = view.findViewById(R.id.level_ll);
        mLLMatNo = view.findViewById(R.id.mat_ll);
        mLL = view.findViewById(R.id.ll);

        mProgressbar = view.findViewById(R.id.progressbar);
        mFab = view.findViewById(R.id.fab);

        if (getContext() != null) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Sansation-Bold.ttf");

            mTvName.setTypeface(typeface1);
            mTvEmail.setTypeface(typeface);
            mTvMatNo.setTypeface(typeface);
            mTvGender.setTypeface(typeface);
            mTvLevel.setTypeface(typeface);
            mTvDepartment.setTypeface(typeface);
            mTvPhone.setTypeface(typeface);
            mTvBloodgroup.setTypeface(typeface);
            mTvGuardian_Phone_No.setTypeface(typeface);

            mTvEmailTag.setTypeface(typeface);
            mTvMatNoTag.setTypeface(typeface);
            mTvLevelTag.setTypeface(typeface);
            mTvDepartmentTag.setTypeface(typeface);
            mTvPhoneTag.setTypeface(typeface);
            mTvGenderTag.setTypeface(typeface);
            mTvBloodgroupTag.setTypeface(typeface);
            mTvGuardian_Phone_NoTag.setTypeface(typeface);
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                authtype = dataSnapshot.child("authtype").getValue().toString();

                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String level = dataSnapshot.child("level").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String matno = dataSnapshot.child("matno").getValue().toString();
                String dept = dataSnapshot.child("dept").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String bloodgroup = dataSnapshot.child("bloodgroup").getValue().toString();
                String guardian_phone_no = dataSnapshot.child("guardian_phone_no").getValue().toString();

                if (getContext() != null)
                    new PersistData(getContext()).setUserDetails(name, email, matno, gender, level,
                            dept, phone, bloodgroup, guardian_phone_no, authtype);

                mLL.setVisibility(View.VISIBLE);
                mProgressbar.setVisibility(View.GONE);

                if (email.contains(Constants.authorizedAdmin)) {
                    mImageIcon.setVisibility(View.VISIBLE);
                }

                switch (authtype) {
                    case "student": {
                        mTvName.setText(name);
                        mTvEmail.setText(email);
                        mTvLevel.setText(level);
                        mTvPhone.setText(phone);
                        mTvMatNo.setText(matno);
                        mTvDepartment.setText(dept);
                        mTvGender.setText(gender);
                        mTvBloodgroup.setText(bloodgroup);
                        mTvGuardian_Phone_No.setText(guardian_phone_no);
                        break;
                    }
                    case "admin":
                    case "lecturer": {
                        mTvName.setText(name);
                        mTvEmail.setText(email);
                        mTvPhone.setText(phone);
                        mTvGender.setText(gender);
                        mTvBloodgroup.setText(bloodgroup);
                        mLLLevel.setVisibility(View.GONE);
                        mLLMatNo.setVisibility(View.GONE);
                        mLLDepartment.setVisibility(View.GONE);
                        mLLGuardian_Phone_No.setVisibility(View.GONE);
                        break;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


}
