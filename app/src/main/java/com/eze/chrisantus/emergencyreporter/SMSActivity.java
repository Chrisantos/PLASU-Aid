package com.eze.chrisantus.emergencyreporter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SMSActivity extends AppCompatActivity {
    private EditText mEdMsg;
    private Button mBtnSend, mBtnYes;
    private TextView mTvText;
    private PersistData persistData;
    private String message = "hey!";
    private String emergencyNumber = "123", myNumber = "123";
    private static final int PERMISSION_CODE = 1;
    private DatabaseReference mAlertsDatabase, mUserDatabase, mSettingsDatabase;
    private String fireNo, securityNo, clinicNo, defaultMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        persistData = new PersistData(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mAlertsDatabase = FirebaseDatabase.getInstance().getReference().child("alerts");
        mAlertsDatabase.keepSynced(true);
        mSettingsDatabase = FirebaseDatabase.getInstance().getReference().child("settings");
        mSettingsDatabase.keepSynced(true);

        mEdMsg = findViewById(R.id.ed_msg);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnYes = findViewById(R.id.btn_yes);
        mTvText = findViewById(R.id.text1);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mEdMsg.setTypeface(typeface);
        mBtnSend.setTypeface(typeface);
        mTvText.setTypeface(typeface);
        mBtnYes.setTypeface(typeface);

        String key = getIntent().getStringExtra(Constants.alertToSMSIntentKey);
        Log.d("key intent", "onCreate: " + key);
        if (!TextUtils.isEmpty(key)) {
            switch (key) {
                case Constants.fireCode:
                    emergencyNumber = persistData.getFireNo();
                    break;
                case Constants.clinicCode:
                    emergencyNumber = persistData.getClinicNo();
                    break;
                case Constants.securityCode:
                    emergencyNumber = persistData.getSecurityNo();
                    break;
            }
        }

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = persistData.getDefaultMsg();
                sendSMSUsingIntent(message, emergencyNumber, myNumber);
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mEdMsg.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    if (ContextCompat.checkSelfPermission(SMSActivity.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                       sendSMSUsingIntent(message, emergencyNumber, myNumber);

                    } else {
                        ActivityCompat.requestPermissions(SMSActivity.this, new String[]{Manifest.permission.SEND_SMS},
                                PERMISSION_CODE);
                    }
                }
            }
        });

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myNumber = dataSnapshot.child("phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    private void sendSMS(String message, String phoneNumber) {
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//        Toast.makeText(this, "Message sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
//    }

    private void sendSMSUsingIntent(String message, String phoneNumber, String myPhoneNumber) {
        StringBuilder builder = new StringBuilder()
                .append(message)
                .append(", at:")
                .append("\nLatitude: " + persistData.getLatitude())
                .append("\nLongitude: " + persistData.getLongitude())
                .append("\n" + persistData.getAddress());

        saveAlertMessage(builder.toString(), myPhoneNumber);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", new String(phoneNumber));
        intent.putExtra("sms_body", builder.toString());
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(Intent.createChooser(intent, "Send SMS via: "));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMSUsingIntent(message, emergencyNumber, myNumber);
                }

                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAlertMessage(String message, String sender) {
        ListPOJO pojo = new ListPOJO(message, sender);
        mAlertsDatabase.push().setValue(pojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SMSActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
