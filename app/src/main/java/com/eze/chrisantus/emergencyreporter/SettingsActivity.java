package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.ListPOJO;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private TextView mTvMessage, mTvFireNumber, mTvSecurityNumber, mTvClinicNumber;
    private TextView mTvText, mTvText1, mTvText2, mTvText3, mTvText4;
    private LinearLayout mLayoutMsg, mLayoutFire, mLayoutSec, mLayoutClinic;
    private Typeface typeface;
    private PersistData persistData;
    private DatabaseReference mSettingsDatabase;

    private String fireNo, securityNo, clinicNo, defaultMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        persistData = new PersistData(getApplicationContext());

        mSettingsDatabase = FirebaseDatabase.getInstance().getReference().child("settings");
        mSettingsDatabase.keepSynced(true);

        mTvMessage = findViewById(R.id.message);
        mTvFireNumber = findViewById(R.id.fire_number);
        mTvSecurityNumber = findViewById(R.id.security_number);
        mTvClinicNumber = findViewById(R.id.clinic_number);
        mTvText = findViewById(R.id.text);
        mTvText1 = findViewById(R.id.text1);
        mTvText2 = findViewById(R.id.text2);
        mTvText3 = findViewById(R.id.text3);
        mTvText4 = findViewById(R.id.text4);

        mLayoutFire = findViewById(R.id.layout_fire);
        mLayoutSec = findViewById(R.id.layout_security);
        mLayoutClinic = findViewById(R.id.layout_clinic);
        mLayoutMsg = findViewById(R.id.layout_msg);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mTvMessage.setTypeface(typeface);
        mTvFireNumber.setTypeface(typeface);
        mTvSecurityNumber.setTypeface(typeface);
        mTvClinicNumber.setTypeface(typeface);
        mTvText.setTypeface(typeface);
        mTvText1.setTypeface(typeface);
        mTvText2.setTypeface(typeface);
        mTvText3.setTypeface(typeface);
        mTvText4.setTypeface(typeface);

        mTvFireNumber.setText(persistData.getFireNo());
        mTvSecurityNumber.setText(persistData.getSecurityNo());
        mTvClinicNumber.setText(persistData.getClinicNo());
        mTvMessage.setText(persistData.getDefaultMsg());

        mLayoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MessageActvity.class));
                finish();
            }
        });

        mLayoutFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_fire_service, "911");
            }
        });

        mLayoutClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_ambulance, "911");
            }
        });

        mLayoutSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_security_agents, "911");
            }
        });

        getSettings();


    }

    private void setUpDialog(final int title, String number) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout3, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvOk = view.findViewById(R.id.tv_ok);
        final TextView editText = view.findViewById(R.id.edittext);

        tvTitle.setTypeface(typeface);
        tvCancel.setTypeface(typeface);
        tvOk.setTypeface(typeface);
        editText.setTypeface(typeface);

        editText.setText(number);
        tvTitle.setText(title);

        switch (title) {
            case R.string.phone_number_of_fire_service:
                editText.setText(persistData.getFireNo());
                break;
            case R.string.phone_number_of_ambulance:
                editText.setText(persistData.getClinicNo());
                break;
            case R.string.phone_number_of_security_agents:
                editText.setText(persistData.getSecurityNo());
                break;
        }

        new AlertDialog.Builder(this).setView(view).create().show();

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = editText.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    switch (title) {
                        case R.string.phone_number_of_fire_service:
                            saveSettings(number, Constants.fireCode);
                            break;
                        case R.string.phone_number_of_ambulance:
                            saveSettings(number, Constants.clinicCode);
                            break;
                        case R.string.phone_number_of_security_agents:
                            saveSettings(number, Constants.securityCode);
                            break;
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();

            }
        });
    }

    private void saveSettings(final String text, final String action) {
        Map<String, String> settings = new HashMap<>();

        switch (action) {
            case Constants.fireCode:
                fireNo = text;
                break;
            case Constants.securityCode:
                securityNo = text;
                break;
            case Constants.clinicCode:
                clinicNo = text;
                break;
        }
        settings.put("fire_no", fireNo);
        settings.put("security_no", securityNo);
        settings.put("clinic_no", clinicNo);
        settings.put("defaultMsg", defaultMsg);

        Toast.makeText(SettingsActivity.this, "Saving..", Toast.LENGTH_SHORT).show();
        mSettingsDatabase.setValue(settings).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (action) {
                    case Constants.fireCode:
                        persistData.setFireNo(text);
                        break;
                    case Constants.securityCode:
                        persistData.setSecurityNo(text);
                        break;
                    case Constants.clinicCode:
                        persistData.setClinicNo(text);
                        break;
                }
                Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains(Constants.authorizedAdmin)) {
            intent = new Intent(this, AdminMainActivity.class);

        } else {
            intent = new Intent(this, MainActivity.class);

        }

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSettings() {
        mSettingsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.child("fire_no").getValue() != null
                        && dataSnapshot.child("security_no").getValue() != null
                        && dataSnapshot.child("clinic_no").getValue() != null
                        && dataSnapshot.child("default_msg").getValue() != null) {

                    fireNo = dataSnapshot.child("fire_no").getValue().toString();
                    securityNo = dataSnapshot.child("security_no").getValue().toString();
                    clinicNo = dataSnapshot.child("clinic_no").getValue().toString();
                    defaultMsg = dataSnapshot.child("default_msg").getValue().toString();

                    mTvFireNumber.setText(fireNo);
                    mTvSecurityNumber.setText(securityNo);
                    mTvClinicNumber.setText(clinicNo);
                    mTvMessage.setText(defaultMsg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
