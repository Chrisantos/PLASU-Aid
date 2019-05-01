package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button mBtnSignin;
    private EditText mEdEmail, mEdPassword;
    private TextView mTvEReporter1,mTvEReporter2, mTvSignin, mTvKeepMeSignedIn;
    private TextView mTvSignup, mTvSignupClick;
    private CheckBox mCheckbox;
    private ProgressBar mProgressbar;
    private FirebaseAuth mAuth;

    public static String IS_CHECKED = "is_checked";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mBtnSignin = findViewById(R.id.btn_login);
        mEdPassword = findViewById(R.id.ed_password);
        mEdEmail = findViewById(R.id.ed_email);
        mTvEReporter1 = findViewById(R.id.ereporter1);
        mTvEReporter2 = findViewById(R.id.ereporter2);
        mTvSignin = findViewById(R.id.tv_signin);
        mTvKeepMeSignedIn = findViewById(R.id.text);
        mTvSignup = findViewById(R.id.tv_signup);
        mTvSignupClick = findViewById(R.id.tv_click);

        mCheckbox = findViewById(R.id.checkbox);
        mProgressbar = findViewById(R.id.progressbar);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Regular.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Sansation-Bold.ttf");
        mBtnSignin.setTypeface(typeface);
        mEdPassword.setTypeface(typeface);
        mEdEmail.setTypeface(typeface);
        mTvEReporter1.setTypeface(typeface);
        mTvEReporter2.setTypeface(typeface);
        mTvSignin.setTypeface(typeface2);
        mTvKeepMeSignedIn.setTypeface(typeface);
        mTvSignup.setTypeface(typeface);
        mTvSignupClick.setTypeface(typeface2);

        mBtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEdEmail.getText().toString();
                String password = mEdPassword.getText().toString();
                if (!validateForm(email, password)) {
                    return;
                }
                mProgressbar.setVisibility(View.VISIBLE);
                mBtnSignin.setEnabled(false);
                signIn(email, password);

            }
        });

        mTvSignupClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

    }

    private void signIn(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        new PersistData(getApplicationContext()).setUserDetails("", email, "", "",
                                "", "", "", "", "", "");

                        mBtnSignin.setEnabled(true);
                        mProgressbar.setVisibility(View.GONE);
                        Intent intent;
                        if (email.contains(Constants.authorizedAdmin)) {
                            intent = new Intent(getApplicationContext(), AdminMainActivity.class);

                        } else {
                            intent = new Intent(getApplicationContext(), MainActivity.class);

                        }

                        if (mCheckbox.isChecked()) {
                            intent.putExtra(IS_CHECKED, true);
                        } else {
                            intent.putExtra(IS_CHECKED, false);
                        }
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mBtnSignin.setEnabled(true);
                        mProgressbar.setVisibility(View.GONE);
                    }
                });


    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEdEmail.setError("Required.");
            valid = false;
        } else {
            mEdEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mEdPassword.setError("Required.");
            valid = false;
        } else {
            mEdPassword.setError(null);
        }

        return valid;
    }
}
