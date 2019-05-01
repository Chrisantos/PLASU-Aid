package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        mAuth = FirebaseAuth.getInstance();

        boolean isChecked = getIntent().getBooleanExtra(LoginActivity.IS_CHECKED, false);

        if (isChecked && mAuth.getCurrentUser() != null) {
            new PersistData(this).setLogin(true);
        }

        TipsFragment fragment = new TipsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_in)
                .replace(R.id.content, fragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {

            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout1, null);
            Button btnYes = view.findViewById(R.id.yes);
            TextView tvTitle = view.findViewById(R.id.title);

            tvTitle.setText(R.string.signout_dialog);
            tvTitle.setTypeface(face);
            btnYes.setTypeface(face);

            new AlertDialog.Builder(this).setView(view).create().show();

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    new PersistData(getApplicationContext()).setLogin(false);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            });
        } else if (id == android.R.id.home) {
            onBackPressed();

        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();

        } else if (id == R.id.extra) {
            startActivity(new Intent(this, AlertsListActivity.class));
            finish();

        } else if (id == R.id.profile) {
            startActivity(new Intent(this, AdminProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
