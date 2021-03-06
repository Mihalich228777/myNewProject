package com.example.mynewproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btnLogOut;
    ConstraintLayout mainElemMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnLogOut);
        mainElemMain = findViewById(R.id.mainElemMain);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            });

        changeActivity();
    }


    private void changeActivity() {
        ImageButton profileBtn = findViewById(R.id.redirectToProfileBtn);
        ImageButton chatBtn = findViewById(R.id.redirectToChatBtn);
        ImageButton calendarBtn = findViewById(R.id.redirectToCalendarBtn);
        ImageButton estimatesBtn = findViewById(R.id.redirectToEstimatesBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
                finish();
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Chat.class));
                finish();
            }
        });
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Calendar.class));
                finish();
            }
        });
        estimatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Estimates.class));
                finish();
            }
        });
    }


}