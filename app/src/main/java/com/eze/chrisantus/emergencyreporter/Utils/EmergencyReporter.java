package com.eze.chrisantus.emergencyreporter.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class EmergencyReporter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
