package com.eze.chrisantus.emergencyreporter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class AlertsListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressbar;
    private FirebaseRecyclerAdapter<ListPOJO, AlertsListActivity.AlertsViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mAlertsDatabase;
    private String selectedKey = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAlertsDatabase = FirebaseDatabase.getInstance().getReference().child("alerts");
        mAlertsDatabase.keepSynced(true);

        mProgressbar = findViewById(R.id.progressbar);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAlertsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getAlertMessages();
    }

    public void setUpDialog(String alerts) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setMessage(alerts)
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
                                deleteAlerts(selectedKey);
                            }
                        });
        alertDialog.show();
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

    private void getAlertMessages() {
        mAlertsDatabase.keepSynced(true);
        FirebaseRecyclerOptions<ListPOJO> options =
                new FirebaseRecyclerOptions.Builder<ListPOJO>()
                        .setQuery(mAlertsDatabase, ListPOJO.class)
                        .build();


        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ListPOJO, AlertsListActivity.AlertsViewHolder>(options) {
                    @NonNull
                    @Override
                    public AlertsListActivity.AlertsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(AlertsListActivity.this).inflate(R.layout.list_layout, null);
                        return new AlertsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AlertsListActivity.AlertsViewHolder holder, final int position, @NonNull final ListPOJO model) {
                        holder.setShortText(model.getInFo());
                        holder.setSender(model.getAux());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedKey = getSnapshots().getSnapshot(position).getKey();
                                setUpDialog(model.getInFo());
                            }
                        });
                    }


                };
        Log.e("Chris", ""+firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        mAlertsDatabase.keepSynced(true);


    }

    public class AlertsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        public AlertsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setShortText(String text) {
            TextView tvText = mView.findViewById(R.id.short_text);
            tvText.setText(text);
            tvText.setTypeface(typeface);
        }

        public void setSender(String sender) {
            TextView tvSender = mView.findViewById(R.id.auxilliary);
            tvSender.setText(sender);
            tvSender.setTypeface(typeface);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    private void deleteAlerts(String key) {
        mAlertsDatabase.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AlertsListActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AlertsListActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();

            }
        });

        mProgressbar.setVisibility(View.GONE);
        firebaseRecyclerAdapter.startListening();
    }
}
