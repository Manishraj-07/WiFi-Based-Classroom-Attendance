package com.example.wifibasedattendanceapplication;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class WifiAttendanceApplication extends Application {
    
    private static final String TAG = "WifiAttendanceApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "User signed in: " + firebaseAuth.getCurrentUser().getEmail());
                    firebaseAuth.getCurrentUser().getIdToken(true);
                } else {
                    Log.d(TAG, "User signed out");
                }
            }
        });
        
        Log.d(TAG, "Application initialized");
    }
}

