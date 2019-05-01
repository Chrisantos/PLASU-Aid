package com.eze.chrisantus.emergencyreporter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.ListPOJO;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TipsListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private ProgressBar mProgressbar;
    private FirebaseRecyclerAdapter<ListPOJO, TipsViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mTipsDatabase, mUserDatabase;
    private String selectedKey = " ", email = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_list);

        final String key = getIntent().getStringExtra("key");
        getSupportActionBar().setTitle(key);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mTipsDatabase = FirebaseDatabase.getInstance().getReference().child("tips").child(key.toLowerCase());
        mTipsDatabase.keepSynced(true);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFab = findViewById(R.id.fab);
        mProgressbar = findViewById(R.id.progressbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.contains(Constants.authorizedAdmin)) {
                    Intent intent = new Intent(getApplicationContext(), AddTipsActivity.class);
                    intent.putExtra("key", key);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(TipsListActivity.this, "Only Admin has this privilege",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mTipsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getTips();

    }

    public void setUpDialogAdmin(String tips) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setMessage(tips)
                .setPositiveButton("CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("DELETE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProgressbar.setVisibility(View.VISIBLE);
                                deleteTips(selectedKey);
                            }
                        });
        alertDialog.show();
    }

    public void setUpDialogRegularUser(String tips) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setMessage(tips)
                .setPositiveButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        alertDialog.show();
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
        if (email.contains(Constants.authorizedAdmin)) {
            intent = new Intent(this, AdminMainActivity.class);

        } else {
            intent = new Intent(this, MainActivity.class);

        }

        startActivity(intent);
        finish();
    }

    private void getTips() {
        mTipsDatabase.keepSynced(true);
        FirebaseRecyclerOptions<ListPOJO> options =
                new FirebaseRecyclerOptions.Builder<ListPOJO>()
                .setQuery(mTipsDatabase, ListPOJO.class)
                .build();


        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ListPOJO, TipsViewHolder>(options) {
                    @NonNull
                    @Override
                    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(TipsListActivity.this).inflate(R.layout.list_layout, null);
                        return new TipsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull TipsViewHolder holder, final int position, @NonNull final ListPOJO model) {
                        holder.setShortText(model.getInFo());
                        holder.setDate(model.getAux());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                if (email.contains(Constants.authorizedAdmin)) {
                                    setUpDialogAdmin(model.getInFo());
                                } else {
                                    setUpDialogRegularUser(model.getInFo());
                                }
                            }
                        });
                    }


                };
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        mTipsDatabase.keepSynced(true);


    }

    public class TipsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setShortText(String text) {
            TextView tvText = mView.findViewById(R.id.short_text);
            tvText.setText(text);
            tvText.setTypeface(typeface);
        }

        public void setDate(String date) {
            TextView tvDate = mView.findViewById(R.id.auxilliary);
            tvDate.setText(date);
            tvDate.setTypeface(typeface);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    private void deleteTips(String key) {
        mTipsDatabase.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TipsListActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TipsListActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();

            }
        });
        mProgressbar.setVisibility(View.GONE);
        firebaseRecyclerAdapter.startListening();
    }
}
