package com.eze.chrisantus.emergencyreporter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.eze.chrisantus.emergencyreporter.Utils.UserPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.eze.chrisantus.emergencyreporter.LoginActivity.IS_CHECKED;

public class RegisterActivity extends AppCompatActivity {
    private Button mBtnSignup;
    private EditText mEdName, mEdEmail, mEdPassword;
    private TextView mTvEReporter1, mTvEReporter2, mTvSignup, mTvKeepMeSignedIn;
    private TextView mTvSignin, mTvSigninClick;
    private Spinner mSpinnerBloodgroup, mSpinnerAuthType;
    private ProgressBar mProgressbar;
    private CheckBox mCheckbox;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String[] AUTHTYPESPINNERLIST = {"Type", "Student", "Lecturer"};
    private String[] BLOODGROUPSPINNERLIST = {"Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private String bloodgroup, authtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mBtnSignup = findViewById(R.id.btn_register);
        mEdName = findViewById(R.id.ed_name);
        mEdPassword = findViewById(R.id.ed_password);
        mEdEmail = findViewById(R.id.ed_email);
        mTvEReporter1 = findViewById(R.id.ereporter1);
        mTvEReporter2 = findViewById(R.id.ereporter2);
        mTvSignup = findViewById(R.id.tv_signup);
        mTvKeepMeSignedIn = findViewById(R.id.text);
        mTvSignin = findViewById(R.id.tv_signin);
        mTvSigninClick = findViewById(R.id.tv_click);

        ArrayAdapter<String> adapterBloodgroup = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, BLOODGROUPSPINNERLIST);

        ArrayAdapter<String> adapterAuthType = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, AUTHTYPESPINNERLIST);

        mSpinnerAuthType = findViewById(R.id.spinner_authtype);
        mSpinnerBloodgroup = findViewById(R.id.spinner_bloodgroup);

        mSpinnerAuthType.setAdapter(adapterAuthType);
        mSpinnerBloodgroup.setAdapter(adapterBloodgroup);

        mCheckbox = findViewById(R.id.checkbox);
        mProgressbar = findViewById(R.id.progressbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");
        mBtnSignup.setTypeface(typeface);
        mEdName.setTypeface(typeface);
        mEdPassword.setTypeface(typeface);
        mEdEmail.setTypeface(typeface);
        mTvEReporter1.setTypeface(typeface);
        mTvEReporter2.setTypeface(typeface);
        mTvSignup.setTypeface(typeface2);
        mTvKeepMeSignedIn.setTypeface(typeface);
        mTvSignin.setTypeface(typeface);
        mTvSigninClick.setTypeface(typeface2);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEdName.getText().toString();
                String email = mEdEmail.getText().toString();
                String password = mEdPassword.getText().toString();
                if (!validateForm(name, email, password)) {
                    return;
                }

                mProgressbar.setVisibility(View.VISIBLE);
                mBtnSignup.setEnabled(false);
                createAccount(name, email, password, bloodgroup);

            }
        });

        mTvSigninClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
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

        mSpinnerAuthType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                authtype = parent.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void createAccount(final String name, final String email, String password, final String bloodgroup) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        final String uID = user.getUid();

                        UserPOJO userPOJO = new UserPOJO();
                        userPOJO.setName(name);
                        userPOJO.setBloodgroup(bloodgroup);
                        userPOJO.setEmail(email);
                        userPOJO.setPhone("2343");
                        userPOJO.setLevel("null");
                        userPOJO.setDept("null");
                        userPOJO.setMatno("null");
                        userPOJO.setGender("null");
                        userPOJO.setGuardian_phone_no("null");

                        if (email.contains(Constants.authorizedAdmin)) {
                            userPOJO.setAuthtype("admin");
                        } else {
                            userPOJO.setAuthtype(authtype.toLowerCase());
                        }


                        /*Map<String, String> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("bloodgroup", bloodgroup);
                        userMap.put("email", email);
                        userMap.put("phone", "123456");
                        userMap.put("level", "null");
                        userMap.put("dept", "null");
                        userMap.put("matno", "null");
                        userMap.put("gender", "null");
                        userMap.put("guardian_phone_no", "null");

                        if (email.contains(Constants.authorizedAdmin)) {
                            userMap.put("authtype", "admin");
                        } else {
                            userMap.put("authtype", authtype.toLowerCase());
                        }

                        mUserDatabase.child(uID).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() { */
                        mUserDatabase.push().setValue(userPOJO)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String nAuthtype;
                                if (email.contains(Constants.authorizedAdmin)) {
                                    nAuthtype = "admin";
                                } else {
                                    nAuthtype = authtype.toLowerCase();
                                }
                                new PersistData(getApplicationContext()).setUserDetails(name, email, "", "",
                                        "", "", "", bloodgroup, "", nAuthtype);

                                Intent intent;
                                if (email.contains(Constants.authorizedAdmin)) {
                                    intent = new Intent(getApplicationContext(), AdminMainActivity.class);

                                } else {
                                    intent = new Intent(getApplicationContext(), MainActivity.class);

                                }
                                mProgressbar.setVisibility(View.GONE);
                                mBtnSignup.setEnabled(true);

                                if (mCheckbox.isChecked()) {
                                    intent.putExtra(IS_CHECKED, true);
                                } else {
                                    intent.putExtra(IS_CHECKED, false);
                                }
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Database creation failed.",
                                        Toast.LENGTH_SHORT).show();
                                mProgressbar.setVisibility(View.GONE);
                                mBtnSignup.setEnabled(true);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressbar.setVisibility(View.GONE);
                        mBtnSignup.setEnabled(true);
                    }
                });

    }

    private boolean validateForm(String name, String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            mEdName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEdEmail.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mEdPassword.setError("Required.");
            valid = false;
        }

        return valid;
    }
}
