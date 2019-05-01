package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MessageActvity extends AppCompatActivity {
    private EditText mEdMsg;
    private TextView mTvText, mTvMsg;
    private ProgressBar mProgressbar;
    private PersistData persistData;
    private DatabaseReference mSettingsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_actvity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        persistData = new PersistData(this);
        mSettingsDatabase = FirebaseDatabase.getInstance().getReference().child("settings");
        mSettingsDatabase.keepSynced(true);

        mEdMsg = findViewById(R.id.ed_msg);
        mTvMsg = findViewById(R.id.tv_msg);
        mTvText = findViewById(R.id.text1);
        mProgressbar = findViewById(R.id.progressbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mEdMsg.setTypeface(typeface);
        mTvText.setTypeface(typeface);
        mTvMsg.setTypeface(typeface);

        StringBuilder builder = new StringBuilder()
                .append(persistData.getDefaultMsg())
                .append(", I'm at:")
                .append("\nLatitude: " + persistData.getLatitude())
                .append("\nLongitude: " + persistData.getLongitude())
                .append("\n" + persistData.getAddress());


        mEdMsg.setText(persistData.getDefaultMsg());
        mTvMsg.setText(builder.toString());

    }

    private void saveSettings(final String text) {
        Map<String, String> settings = new HashMap<>();
        String fireNo = persistData.getFireNo();
        String securityNo = persistData.getSecurityNo();
        String clinicNo = persistData.getClinicNo();

        settings.put("fire_no", fireNo);
        settings.put("security_no", securityNo);
        settings.put("clinic_no", clinicNo);
        settings.put("defaultMsg", text);

        mSettingsDatabase.setValue(settings).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                persistData.setDefaultMsg(text);
                mProgressbar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActvity.this, "Failed", Toast.LENGTH_SHORT).show();
                mProgressbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String message = mEdMsg.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            mProgressbar.setVisibility(View.VISIBLE);
            saveSettings(message);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
