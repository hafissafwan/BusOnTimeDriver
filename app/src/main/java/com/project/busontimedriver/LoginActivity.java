package com.project.busontimedriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    Button button,button2;
    EditText userName,userPass;
    List<DriverO> driverOS = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (mDriverO.getUserGuid()!=null){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }

        mDbRef.child("drivers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null){
                    DriverO driverO = dataSnapshot.getValue(DriverO.class);
                    driverOS.add(driverO);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userName = findViewById(R.id.name);
        userPass = findViewById(R.id.pass);
        button = findViewById(R.id.login);
        button2 = findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userName.getText().toString().equals("")&&!userPass.getText().toString().equals("")) {
                    joinUs(userName.getText().toString(), userPass.getText().toString());
                }else {
                    Toast.makeText(LoginActivity.this,"Please Enter All Fields.",0).show();
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                    finish();
            }
        });
    }


    public void joinUs(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Auth", "signInWithEmail:success");
                    for (int i=0;i<driverOS.size();i++) {
                        if (driverOS.get(i).getUserEmail().equals(userName.getText().toString())){
                            mDriverO=driverOS.get(i);
                        }
                    }
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("mDriverO", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();

                    Gson gson = new Gson();
                    String json = gson.toJson(mDriverO);
                    editor.putString("mDriverO", json); // Storing string

                    editor.apply();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    Toast.makeText(LoginActivity.this,"Welcome Back!",0).show();

                    finish();
                } else {
                    // If sign in fails, display a message to the user.

                    Log.w("Auth", "signInWithEmail:failure", task.getException());
                    if (task.getException()!=null){
                        if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")){
                            Toast.makeText(LoginActivity.this,"The Password entered is Incorrect.",0).show();

                        }
                    }
                }
            }
        });
    }
}
