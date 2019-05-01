package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.alert:
                    setTitle("Alert");
                    addFragment(new AlertFragment());
                    return true;
                case R.id.tips:
                    setTitle("Tips");
                    addFragment(new TipsFragment());
                    return true;
                case R.id.profile:
                    setTitle("Profile");
                    addFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragment(new AlertFragment());
        setTitle("Alert");
        mAuth = FirebaseAuth.getInstance();

        boolean isChecked = getIntent().getBooleanExtra(LoginActivity.IS_CHECKED, false);

        if (isChecked && mAuth.getCurrentUser() != null) {
            new PersistData(this).setLogin(true);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void addFragment(Fragment fragment) {
        if (fragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                    .replace(R.id.content, fragment)
                    .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_user, menu);
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

        }

        return super.onOptionsItemSelected(item);
    }
}
