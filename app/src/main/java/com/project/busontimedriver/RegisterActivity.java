package com.project.busontimedriver;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.UUID;

public class RegisterActivity extends BaseActivity {

    Button button;
    EditText userName,userPass, userEmail, userLicense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.name);
        userPass = findViewById(R.id.pass);
        userLicense = findViewById(R.id.license);
        userEmail = findViewById(R.id.email);
        button = findViewById(R.id.register);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userName.getText().toString().isEmpty()
                        &&!userPass.getText().toString().isEmpty()
                        &&!userEmail.getText().toString().isEmpty()
                        &&!userLicense.getText().toString().isEmpty()){
                    if (userPass.getText().toString().length()>=8) {
                        DriverO driverO = new DriverO();
                        driverO.setApproved(false);
                        driverO.setLicenseNumber(userLicense.getText().toString());
                        driverO.setUserEmail(userEmail.getText().toString());
                        driverO.setUserName(userName.getText().toString());
                        driverO.setUserGuid(UUID.randomUUID().toString());
                        mDbRef.child("drivers").child(driverO.getUserGuid()).setValue(driverO);
                        joinUs(userEmail.getText().toString(), userPass.getText().toString());
                    }else {
                        Toast.makeText(RegisterActivity.this,"Password should be atleast 8 characters",0).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"Please Complete All Fields",0).show();
                }
            }
        });


    }


    public void joinUs(final String email, final String password){
        Toast.makeText(RegisterActivity.this, "joinUs called", Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "taskSuccess", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            Toast.makeText(getApplicationContext(),"Welcome To Bus On Time",0).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                Log.d("Auth", "existingUserDetect:success");
                                /*//

                                NOTE : below function to be activated when we add sign-in-with-link !


                                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if (task.isSuccessful()) {
                                            SignInMethodQueryResult result = task.getResult();
                                            List<String> signInMethods = result.getSignInMethods();
                                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                                // User can sign in with email/password
                                            } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                                                // User can sign in with email/link
                                            }
                                        } else {
                                            Log.e(TAG, "Error getting sign in methods for user", task.getException());
                                        }
                                    }
                                });*/
                                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("Auth", "signInWithEmail:success");

                                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            Toast.makeText(RegisterActivity.this,"Welcome Back!",0).show();

                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Log.w("Auth", "signInWithEmail:failure", task.getException());
                                            if (task.getException()!=null){
                                                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")){
                                                    Toast.makeText(RegisterActivity.this,"The Password entered is Incorrect.",0).show();

                                                }
                                            }
                                        }
                                    }
                                });
                            }

                        }

                    }
                });
    }

}
