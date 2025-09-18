package com.example.wifibasedattendanceapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class TakeAttendanceActivity extends BaseAuthenticatedActivity {
    Spinner branchSpinner, sectionSpinner, subjectSpinner;
    Button btn_startAttendanceSession;
    String[] branchArray, sectionArray, subjectArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);


        Init();
        Buttons();
    }

    private void Buttons() {
        btn_startAttendanceSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelectionValid()) {
                    String selectedBranch = branchSpinner.getSelectedItem().toString();
                    String selectedSection = sectionSpinner.getSelectedItem().toString();
                    String selectedSubject = subjectSpinner.getSelectedItem().toString();

                    Intent intent = new Intent(getApplicationContext(), activity_session.class);
                    intent.putExtra("branch", selectedBranch);
                    intent.putExtra("section", selectedSection);
                    intent.putExtra("subject", selectedSubject);

                    startActivity(intent);
                } else {
                    Toast.makeText(TakeAttendanceActivity.this, "Please select all options", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isSelectionValid() {
        return (branchSpinner.getSelectedItemPosition() > 0) &&
               (sectionSpinner.getSelectedItemPosition() > 0) &&
               (subjectSpinner.getSelectedItemPosition() > 0);
    }

    private void Init() {
        branchSpinner = findViewById(R.id.branchSpinner);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        btn_startAttendanceSession = findViewById(R.id.btn_startAttendanceSession);
        
        branchArray = new String[]{
            "Select Branch: â–¼",
            "CSE"
        };
        
        sectionArray = new String[]{
            "Select Section: â–¼",
            "7cse02"
        };
        
        subjectArray = new String[]{
            "Select Subject: â–¼",
            "Data Structures and Algorithms (CSE101)",
            "Database Management Systems (CSE102)"
        };
        
        ArrayAdapter<String> branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, branchArray);
        branchArrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        branchSpinner.setAdapter(branchArrayAdapter);
        branchSpinner.setSelection(0, false);
        
        ArrayAdapter<String> sectionArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, sectionArray);
        sectionArrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        sectionSpinner.setAdapter(sectionArrayAdapter);
        sectionSpinner.setSelection(0, false);
        
        ArrayAdapter<String> subjectArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, subjectArray);
        subjectArrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        subjectSpinner.setAdapter(subjectArrayAdapter);
        subjectSpinner.setSelection(0, false);
    }

}
