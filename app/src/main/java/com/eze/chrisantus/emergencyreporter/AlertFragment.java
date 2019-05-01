package com.eze.chrisantus.emergencyreporter;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.chrisantus.emergencyreporter.Utils.Constants;
import com.eze.chrisantus.emergencyreporter.Utils.PersistData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertFragment extends Fragment implements LocationListener{
    private Button mBtnFire, mBtnSecurity, mBtnClinic;
    private Typeface mTypeface;
    private TextView mTvText, mTvText2, mTvText3, mTvLocation, mTvLongitude, mTvLatitude;
    private PersistData persistData;

    private static final int CALL_FIRE_PERMISSION_CODE = 1;
    private static final int CALL_SECURITY_PERMISSION_CODE = 2;
    private static final int CALL_AMBULANCE_PERMISSION_CODE = 3;

    LocationManager locationManager;
    String mprovider;
    Geocoder geocoder;
    List<Address> addresses;
    Criteria criteria;

    private DatabaseReference mSettingsDatabase;

    private String fireNo, securityNo, clinicNo, defaultMsg;

    public AlertFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        mSettingsDatabase = FirebaseDatabase.getInstance().getReference().child("settings");
        mSettingsDatabase.keepSynced(true);

        mBtnClinic = view.findViewById(R.id.btn_clinic);
        mBtnSecurity = view.findViewById(R.id.btn_security);
        mBtnFire = view.findViewById(R.id.btn_fire);

        mTvLocation = view.findViewById(R.id.tv_location);
        mTvLongitude = view.findViewById(R.id.tv_longitude);
        mTvLatitude = view.findViewById(R.id.tv_latitude);
        mTvText = view.findViewById(R.id.text);
        mTvText2 = view.findViewById(R.id.text2);
        mTvText3 = view.findViewById(R.id.text3);

        if (getContext() != null) {
            persistData = new PersistData(getContext());
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            mBtnFire.setTypeface(mTypeface);
            mBtnSecurity.setTypeface(mTypeface);
            mBtnClinic.setTypeface(mTypeface);
            mTvText.setTypeface(mTypeface);
            mTvText2.setTypeface(mTypeface);
            mTvText3.setTypeface(mTypeface);
            mTvLocation.setTypeface(mTypeface);
            mTvLongitude.setTypeface(mTypeface);
            mTvLatitude.setTypeface(mTypeface);
        }

        mBtnClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_ambulance, Constants.clinicCode);
            }
        });

        mBtnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_security_agents, Constants.securityCode);
            }
        });

        mBtnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialog(R.string.phone_number_of_fire_service, Constants.fireCode);
            }
        });

        getSettings();

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        if (locationManager != null && getContext() != null) {
            mprovider = locationManager.getBestProvider(criteria, false);

            if (mprovider != null && !mprovider.equals("")) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();

                } else {
                    Location location = locationManager.getLastKnownLocation(mprovider);
                    if (location != null) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();

                        locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    String city = addresses.get(0).getLocality();
                                    String lga = addresses.get(0).getSubAdminArea();
                                    String state = addresses.get(0).getAdminArea();

                                    final StringBuilder builder = new StringBuilder()
                                            .append(city)
                                            .append(" - ")
                                            .append(lga)
                                            .append(", ")
                                            .append(state);

                                    if (getActivity() != null) {
                                        (getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTvLocation.setText(builder.toString());
                                                mTvLatitude.setText(String.valueOf(latitude));
                                                mTvLongitude.setText(String.valueOf(longitude));
                                            }
                                        });
                                    }

                                    persistData.setLatitude(String.valueOf(latitude));
                                    persistData.setLongitude(String.valueOf(longitude));
                                    persistData.setAddress(builder.toString());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                        if (location != null)
                            onLocationChanged(location);
                        else
                            Toast.makeText(getContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();

                    }

                }
            } else {
                Toast.makeText(getContext(), mprovider, Toast.LENGTH_SHORT).show();
            }
        }


        return view;
    }

    private void setUpDialog(final int type, final String emergencySC) {
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout2, null);
        Button btnCall = view1.findViewById(R.id.btn_call);
        Button btnSMS = view1.findViewById(R.id.btn_sms);
        TextView tvTitle = view1.findViewById(R.id.title);

        btnCall.setTypeface(mTypeface);
        btnSMS.setTypeface(mTypeface);
        tvTitle.setTypeface(mTypeface);

        new AlertDialog.Builder(getContext()).setView(view1).create().show();

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case R.string.phone_number_of_fire_service:
                        callPhone(persistData.getFireNo(), CALL_FIRE_PERMISSION_CODE);
                        break;
                    case R.string.phone_number_of_ambulance:
                        callPhone(persistData.getClinicNo(), CALL_AMBULANCE_PERMISSION_CODE);
                        break;
                    case R.string.phone_number_of_security_agents:
                        callPhone(persistData.getSecurityNo(), CALL_SECURITY_PERMISSION_CODE);
                        break;
                }
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SMSActivity.class);
                intent.putExtra(Constants.alertToSMSIntentKey, emergencySC);
                Log.d("btnSMS", "onClick: " + emergencySC);
                startActivity(intent);
            }
        });
    }

    private void callPhone(String message, int permissionCode) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + message)));
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                    permissionCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_FIRE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(persistData.getFireNo(), CALL_FIRE_PERMISSION_CODE);
                }
                break;
            case CALL_AMBULANCE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(persistData.getClinicNo(), CALL_AMBULANCE_PERMISSION_CODE);
                }
                break;
            case CALL_SECURITY_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(persistData.getSecurityNo(), CALL_SECURITY_PERMISSION_CODE);
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        mTvLatitude.setText(String.valueOf(latitude));
        mTvLongitude.setText(String.valueOf(longitude));

        persistData.setLatitude(String.valueOf(latitude));
        persistData.setLongitude(String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

                    persistData.setDefaultMsg(defaultMsg);
                    persistData.setFireNo(fireNo);
                    persistData.setSecurityNo(securityNo);
                    persistData.setClinicNo(clinicNo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
