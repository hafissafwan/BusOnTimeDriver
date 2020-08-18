package com.project.busontimedriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public abstract class BaseActivity extends AppCompatActivity {

    protected DriverO mDriverO;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDbRef;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference().child("Db");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("mDriverO", 0);

        Gson gson = new Gson();
        String json;

        json = pref.getString("mDriverO", null);
        mDriverO = gson.fromJson(json, DriverO.class);

        if (mDriverO==null){
            mDriverO = new DriverO();
        }else {
            if (mDriverO.getUserEmail()!=null) {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            DriverO driverO = dataSnapshot.getValue(DriverO.class);
                            if (driverO!=null){
                                if (mAuth.getCurrentUser()!=null) {
                                    if (mAuth.getCurrentUser().getEmail().equals(driverO.getUserEmail())) {
                                        mDriverO = driverO;
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("mDriverO", 0); // 0 - for private mode
                                        SharedPreferences.Editor editor = pref.edit();

                                        Gson gson = new Gson();
                                        String json = gson.toJson(mDriverO);
                                        editor.putString("mDriverO", json); // Storing string

                                        editor.apply();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mDbRef.child("drivers").child(mDriverO.getUserGuid()).addValueEventListener(valueEventListener);
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener!=null) {
            if (mDriverO != null) {
                if (mDriverO.getUserGuid() != null) {
                    mDbRef.child("drivers").child(mDriverO.getUserGuid()).removeEventListener(valueEventListener);
                }
            }
        }
    }
}
