package com.eze.chrisantus.emergencyreporter;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.UserPOJO;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressbar;
    private FirebaseRecyclerAdapter<UserPOJO, UsersActivity.UsersViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mUsersDatabase;
    private String selectedKey = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mProgressbar = findViewById(R.id.progressbar);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getUsers();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AdminMainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUsers() {
        mUsersDatabase.keepSynced(true);
        FirebaseRecyclerOptions<UserPOJO> options =
                new FirebaseRecyclerOptions.Builder<UserPOJO>()
                        .setQuery(mUsersDatabase, UserPOJO.class)
                        .build();


        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UserPOJO, UsersActivity.UsersViewHolder>(options) {
                    @NonNull
                    @Override
                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(UsersActivity.this).inflate(R.layout.list_layout, null);
                        return new UsersViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UsersViewHolder holder, final int position, @NonNull final UserPOJO model) {
                        holder.setName(model.getName());
                        holder.setAuthType(model.getAuthtype());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                                intent.putExtra("key", selectedKey);
                                startActivity(intent);
                            }
                        });
                    }


                };
        Log.e("Chris", ""+firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        mUsersDatabase.keepSynced(true);


    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {
            TextView tvName = mView.findViewById(R.id.short_text);
            tvName.setText(name);
            tvName.setTypeface(typeface);
        }

        public void setAuthType(String authType) {
            TextView tvAuthType = mView.findViewById(R.id.auxilliary);
            tvAuthType.setText(authType);
            tvAuthType.setTypeface(typeface);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

}
