package com.yjisolutions.music;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class spleshScreen extends AppCompatActivity {
    private int splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh_screen);

        splash = 500;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(spleshScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        },splash);

    }
}