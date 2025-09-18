package com.example.wifibasedattendanceapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_presence_recorded extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_recorded);
        
        Button showReportBtn = findViewById(R.id.show_report);
        Button exitBtn = findViewById(R.id.exit);
        
        showReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_presence_recorded.this, AttendanceReportActivity.class);
                startActivity(intent);
            }
        });
        
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
