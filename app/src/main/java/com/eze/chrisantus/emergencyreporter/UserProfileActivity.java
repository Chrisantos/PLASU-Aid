package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView mTvName, mTvEmail, mTvMatNo, mTvGender, mTvLevel, mTvDepartment, mTvPhone
            , mTvBloodgroup, mTvGuardian_Phone_No;
    private TextView mTvEmailTag, mTvMatNoTag,mTvGenderTag, mTvLevelTag, mTvDepartmentTag, mTvPhoneTag
            , mTvBloodgroupTag, mTvGuardian_Phone_NoTag, mTvNameTag;

    private LinearLayout mLLMatNo, mLLLevel, mLLDepartment, mLLGuardian_Phone_No, mLL;
    private ProgressBar mProgressbar;

    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        String userKey = getIntent().getStringExtra("key");

        mTvName = findViewById(R.id.name);
        mTvEmail = findViewById(R.id.email);
        mTvMatNo = findViewById(R.id.matno);
        mTvGender = findViewById(R.id.gender);
        mTvLevel = findViewById(R.id.level);
        mTvDepartment = findViewById(R.id.dept);
        mTvPhone = findViewById(R.id.phone);
        mTvBloodgroup = findViewById(R.id.blood_group);
        mTvGuardian_Phone_No = findViewById(R.id.guardian);

        mTvNameTag = findViewById(R.id.name_tag);
        mTvEmailTag = findViewById(R.id.email_tag_tag);
        mTvMatNoTag = findViewById(R.id.matno_tag);
        mTvLevelTag = findViewById(R.id.level_tag);
        mTvDepartmentTag = findViewById(R.id.dept_tag);
        mTvPhoneTag = findViewById(R.id.phone_tag);
        mTvGenderTag = findViewById(R.id.gender_tag);
        mTvBloodgroupTag = findViewById(R.id.blood_group_tag);
        mTvGuardian_Phone_NoTag = findViewById(R.id.guardian_tag);

        mLLDepartment = findViewById(R.id.dept_ll);
        mLLGuardian_Phone_No = findViewById(R.id.guardian_ll);
        mLLLevel = findViewById(R.id.level_ll);
        mLLMatNo = findViewById(R.id.mat_ll);
        mLL = findViewById(R.id.ll);

        mProgressbar = findViewById(R.id.progressbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        mTvName.setTypeface(typeface);
        mTvEmail.setTypeface(typeface);
        mTvMatNo.setTypeface(typeface);
        mTvGender.setTypeface(typeface);
        mTvLevel.setTypeface(typeface);
        mTvDepartment.setTypeface(typeface);
        mTvPhone.setTypeface(typeface);
        mTvBloodgroup.setTypeface(typeface);
        mTvGuardian_Phone_No.setTypeface(typeface);

        mTvNameTag.setTypeface(typeface);
        mTvEmailTag.setTypeface(typeface);
        mTvMatNoTag.setTypeface(typeface);
        mTvLevelTag.setTypeface(typeface);
        mTvDepartmentTag.setTypeface(typeface);
        mTvPhoneTag.setTypeface(typeface);
        mTvGenderTag.setTypeface(typeface);
        mTvBloodgroupTag.setTypeface(typeface);
        mTvGuardian_Phone_NoTag.setTypeface(typeface);

        mUsersDatabase.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String authtype = dataSnapshot.child("authtype").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String level = dataSnapshot.child("level").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String matno = dataSnapshot.child("matno").getValue().toString();
                String dept = dataSnapshot.child("dept").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String bloodgroup = dataSnapshot.child("bloodgroup").getValue().toString();
                String guardian_phone_no = dataSnapshot.child("guardian_phone_no").getValue().toString();


                mLL.setVisibility(View.VISIBLE);
                mProgressbar.setVisibility(View.GONE);

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, UsersActivity.class));
        finish();
    }

}
