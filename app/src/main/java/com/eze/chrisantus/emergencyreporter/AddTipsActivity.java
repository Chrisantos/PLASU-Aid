package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.ListPOJO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTipsActivity extends AppCompatActivity {
    private EditText mEdTips;
    private Button mBtnSave;
    private TextView mTvText;
    private ProgressBar mProgressBar;
    private DatabaseReference mTipsDatabase;
    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tips);

        key = getIntent().getStringExtra("key");
        getSupportActionBar().setTitle(key);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTipsDatabase = FirebaseDatabase.getInstance().getReference().child("tips").child(key.toLowerCase());
        mTipsDatabase.keepSynced(true);

        mEdTips = findViewById(R.id.ed_tips);
        mBtnSave = findViewById(R.id.btn_save);
        mTvText = findViewById(R.id.text);
        mProgressBar = findViewById(R.id.progressbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mEdTips.setTypeface(typeface);
        mBtnSave.setTypeface(typeface);
        mTvText.setTypeface(typeface);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tips = mEdTips.getText().toString();
                if (!TextUtils.isEmpty(tips)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    saveTips(tips);
                }
            }
        });
    }

    private void saveTips(String tips) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        ListPOJO pojo = new ListPOJO(tips, dateFormat.format(date));
        mTipsDatabase.push().setValue(pojo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddTipsActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TipsListActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddTipsActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), TipsListActivity.class);
        intent.putExtra("key", key);
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
}
