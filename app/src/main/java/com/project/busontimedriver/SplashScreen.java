package com.project.busontimedriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        ImageView imageView =findViewById(R.id.splash);
        Picasso.get().load(R.drawable.logo_bus).centerCrop().fit().noFade().into(imageView);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        },1000*2);
    }
}
