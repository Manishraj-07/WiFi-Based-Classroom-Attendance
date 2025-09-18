package com.example.wifibasedattendanceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends BaseAuthenticatedActivity {

    Button btn_login_to_faculty, btn_login_to_student, btn_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login_to_faculty = findViewById(R.id.btn_login_to_faculty);
        btn_login_to_student = findViewById(R.id.btn_login_to_student);
        btn_about = findViewById(R.id.btn_about);

        btn_login_to_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginStudentActivity.class));
            }
        });

        btn_login_to_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginFacultyActivity.class));
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        });
    }
}
