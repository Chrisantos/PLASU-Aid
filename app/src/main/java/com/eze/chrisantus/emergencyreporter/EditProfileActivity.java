package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.eze.chrisantus.emergencyreporter.Utils.UserPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private EditText mEdName, mEdMatNo, mEdLevel, mEdDepartment, mEdPhone, mEdGuardianNo;
    private Button mBtnSave;
    private Spinner mSpinnerBloodgroup, mSpinnerGender;
    private ProgressBar mProgressBar;
    private TextView mTvText1, mTvText2, mTvText3, mTvText4, mTvText5, mTvText6, mTvText7, mTvText8;
    private LinearLayout mLLMatNo, mLLLevel, mLLDepartment, mLLGuardian_Phone_No;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;
    private PersistData persistData;
    private String[] BLOODGROUPSPINNERLIST = {"Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private String[] GENDERSPINNERLIST = {"Gender", "Male", "Female"};

    private String bloodgroup, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        persistData = new PersistData(this);

        mEdName = findViewById(R.id.ed_name);
        mEdMatNo = findViewById(R.id.ed_mat_no);
        mEdDepartment = findViewById(R.id.ed_dept);
        mEdPhone = findViewById(R.id.ed_phone);
        mEdLevel = findViewById(R.id.ed_level);
        mSpinnerBloodgroup = findViewById(R.id.spinner_bloodgroup);
        mSpinnerGender = findViewById(R.id.spinner_gender);
        mEdGuardianNo = findViewById(R.id.ed_guardian);
        mProgressBar = findViewById(R.id.progressbar);
        mBtnSave = findViewById(R.id.btn_save);

        mTvText1 = findViewById(R.id.text1);
        mTvText2 = findViewById(R.id.text2);
        mTvText3 = findViewById(R.id.text3);
        mTvText4 = findViewById(R.id.text4);
        mTvText5 = findViewById(R.id.text5);
        mTvText6 = findViewById(R.id.text6);
        mTvText7 = findViewById(R.id.text7);
        mTvText8 = findViewById(R.id.text8);

        mLLDepartment = findViewById(R.id.dept_ll);
        mLLGuardian_Phone_No = findViewById(R.id.guardian_ll);
        mLLLevel = findViewById(R.id.level_ll);
        mLLMatNo = findViewById(R.id.mat_ll);

        ArrayAdapter<String> adapterBloodgroup = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, BLOODGROUPSPINNERLIST);
        mSpinnerBloodgroup.setAdapter(adapterBloodgroup);

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, GENDERSPINNERLIST);
        mSpinnerGender.setAdapter(adapterGender);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mEdName.setTypeface(typeface);
        mEdMatNo.setTypeface(typeface);
        mEdDepartment.setTypeface(typeface);
        mEdLevel.setTypeface(typeface);
        mEdPhone.setTypeface(typeface);
        mEdGuardianNo.setTypeface(typeface);
        mBtnSave.setTypeface(typeface);

        mTvText1.setTypeface(typeface);
        mTvText2.setTypeface(typeface);
        mTvText3.setTypeface(typeface);
        mTvText4.setTypeface(typeface);
        mTvText5.setTypeface(typeface);
        mTvText6.setTypeface(typeface);
        mTvText7.setTypeface(typeface);
        mTvText8.setTypeface(typeface);

        switch (persistData.getAuthtype()) {
            case "student": {
                mEdName.setText(persistData.getName());
                mEdMatNo.setText(persistData.getMatno());
                mEdDepartment.setText(persistData.getDept());
                mEdPhone.setText(persistData.getPhone());
                mEdLevel.setText(persistData.getLevel());
                mEdGuardianNo.setText(persistData.getGuardianNo());

                break;
            }
            case "admin":
            case "lecturer": {
                mEdName.setText(persistData.getName());
                mEdPhone.setText(persistData.getPhone());

                mLLLevel.setVisibility(View.GONE);
                mLLMatNo.setVisibility(View.GONE);
                mLLDepartment.setVisibility(View.GONE);
                mLLGuardian_Phone_No.setVisibility(View.GONE);
                break;

            }
        }

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEdName.getText().toString();
                String matno = mEdMatNo.getText().toString();
                String level = mEdLevel.getText().toString();
                String dept = mEdDepartment.getText().toString();
                String phone = mEdPhone.getText().toString();
                String guardian_no = mEdGuardianNo.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    editDetails(name, matno, level, dept, phone, gender, bloodgroup, guardian_no);
                }

            }
        });

        mSpinnerBloodgroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                bloodgroup = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                gender = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void editDetails(final String name, final String matno, final String level, final String dept, final String phone,
                             final String gender, final String bloodgroup, final String guardian_no) {
        UserPOJO userPOJO = new UserPOJO();
        userPOJO.setName(name);
        userPOJO.setEmail(mUser.getEmail());
        userPOJO.setPhone(phone);
        userPOJO.setLevel(level);
        userPOJO.setDept(dept);
        userPOJO.setMatno(matno);


//        Map<String, String> userMap = new HashMap<>();
//        userMap.put("name", name);
//        userMap.put("phone", phone);
//        userMap.put("level", level);
//        userMap.put("dept", dept);
//        userMap.put("matno", matno);
//        userMap.put("email", mUser.getEmail());
//        userMap.put("authtype", persistData.getAuthtype());

        if (bloodgroup.equals("Blood Group")) {
            Toast.makeText(this, "Invalid blood group selected", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            return;

        } else {
            userPOJO.setBloodgroup(bloodgroup);
//            userMap.put("bloodgroup", bloodgroup);
        }

        if (gender.equals("Gender")) {
            Toast.makeText(this, "Invalid gender selected", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
            return;

        } else {
            userPOJO.setGender(gender);
//            userMap.put("gender", gender);
        }

        userPOJO.setGuardian_phone_no(guardian_no);
//        userMap.put("guardian_phone_no", guardian_no);

//        mUserDatabase.child(mUser.getUid()).setValue(userMap).addOnCompleteListener(
        mUserDatabase.child(mUser.getUid()).push().setValue(userPOJO).addOnCompleteListener(
                new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    persistData.setUserDetails(name, mUser.getEmail(), matno, gender, level, dept, phone,
                            bloodgroup, guardian_no, persistData.getAuthtype());
                    onBackPressed();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed",
                            Toast.LENGTH_SHORT).show();
                }
                mProgressBar.setVisibility(View.GONE);
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
        Intent intent;
        if (mUser.getEmail().contains(Constants.authorizedAdmin)) {
            intent = new Intent(this, AdminProfileActivity.class);

        } else {
            intent = new Intent(this, MainActivity.class);

        }

        startActivity(intent);
        finish();
    }
}
