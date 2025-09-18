package com.example.wifibasedattendanceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activity_session extends BaseAuthenticatedActivity {
    String branch, section, subject;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        
        
        ImageView gifImageView = findViewById(R.id.loading);

        Button btn = findViewById(R.id.end_session);

        Glide.with(this).asGif().load(R.drawable.loading).into(gifImageView);
        Intent intent = getIntent();
        if (intent != null) {
            branch = intent.getStringExtra("branch");
            section = intent.getStringExtra("section");
            subject = intent.getStringExtra("subject");
        }
        performDatabaseOperation(() -> createSession());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndSessionConfirmation();
            }
        });
    }

    private void showEndSessionConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("End Attendance Session")
            .setMessage("Are you sure you want to end this attendance session? Students will no longer be able to mark attendance.")
            .setPositiveButton("Yes, End Session", (dialog, which) -> {
                endSession();
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void createSession() {
        Log.d("Debug", "createSession() called");
        
        if (!isUserAuthenticated()) {
            Log.e("Debug", "User not authenticated, cannot create session");
            return;
        }

        long timestamp = System.currentTimeMillis();
        sessionId = "attendance_session_id_" + timestamp;
        Log.d("Debug", "Generated unique session ID: " + sessionId);
        
        checkSessionIdUniqueness();
    }
    
    
    private void checkSessionIdUniqueness() {
        DatabaseReference sessionRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceReport")
                .child(sessionId);
                
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.w("Debug", "Session ID collision detected: " + sessionId + ", generating new ID");
                    long newTimestamp = System.currentTimeMillis() + 1; // Add 1ms to ensure uniqueness
                    sessionId = "attendance_session_id_" + newTimestamp;
                    Log.d("Debug", "New unique session ID: " + sessionId);
                    createSessionData();
                } else {
                    Log.d("Debug", "Session ID is unique: " + sessionId);
                    createSessionData();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Debug", "Error checking session ID uniqueness: " + error.getMessage());
                createSessionData();
            }
        });
    }
    
    
    private long findNextAvailableSessionId(DataSnapshot dataSnapshot) {
        long nextId = 1;
        
        for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
            String sessionKey = sessionSnapshot.getKey();
            if (sessionKey != null && sessionKey.startsWith("attendance_session_id_")) {
                try {
                    String numericPart = sessionKey.substring("attendance_session_id_".length());
                    long existingId = Long.parseLong(numericPart);
                    
                    if (existingId >= nextId) {
                        nextId = existingId + 1;
                    }
                } catch (NumberFormatException e) {
                    Log.w("Debug", "Invalid session ID format: " + sessionKey);
                }
            }
        }
        
        Log.d("Debug", "Next available session ID: " + nextId);
        return nextId;
    }
    
    
    private void cleanupOrphanedSessions() {
        DatabaseReference attendanceReportRef = FirebaseDatabase.getInstance().getReference("AttendanceReport");
        attendanceReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Debug", "Checking for orphaned sessions...");
                    
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        String sessionKey = sessionSnapshot.getKey();
                        String sessionStatus = sessionSnapshot.child("session_status").getValue(String.class);
                        Long timestamp = sessionSnapshot.child("timestamp").getValue(Long.class);
                        
                        if (sessionStatus == null || timestamp == null) {
                            Log.w("Debug", "Found orphaned session: " + sessionKey + 
                                  " (status: " + sessionStatus + ", timestamp: " + timestamp + ")");
                            
                        }
                    }
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Debug", "Error checking for orphaned sessions: " + error.getMessage());
            }
        });
    }

    private void createSessionData() {
        DatabaseReference newReportRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceReport")
                .child(sessionId);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedDate = dateFormat.format(currentDate);

        Calendar calendar = Calendar.getInstance();
        String amPm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        String startTime = calendar.get(Calendar.HOUR_OF_DAY) + " " + amPm;
        int endTime = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        if (endTime > 12) {
            endTime = endTime - 12;
        }
        String s_endTime = endTime + " " + amPm;

        long startTimeMillis = System.currentTimeMillis();
        long endTimeMillis = startTimeMillis + (60 * 60 * 1000); // 1 hour from start

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("branch", branch);
        sessionData.put("section", section);
        sessionData.put("period_date", formattedDate);
        sessionData.put("start_time", startTime);
        sessionData.put("end_time", s_endTime);
        sessionData.put("subject", subject);
        sessionData.put("timestamp", startTimeMillis);
        sessionData.put("end_timestamp", endTimeMillis);
        sessionData.put("session_status", "active"); // active, ended, or expired
        sessionData.put("start_time_millis", startTimeMillis);

        newReportRef.setValue(sessionData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Debug", "Session data created successfully with ID: " + sessionId);
                createStudentsSectionInSession();
                
                cleanupOrphanedSessions();
            } else {
                Log.e("Debug", "Failed to create session: " + task.getException());
                Toast.makeText(activity_session.this, 
                    "Failed to create session. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    private String convertDivisionToAbbreviation(String fullDivision) {
        if (fullDivision == null) return "";
        
        if (fullDivision.contains("Div - 1")) return "A";
        if (fullDivision.contains("Div - 2")) return "B";
        if (fullDivision.contains("Div - 3")) return "C";
        if (fullDivision.contains("Div - 4")) return "D";
        if (fullDivision.contains("Div - 5")) return "E";
        
        Log.w("Debug", "Unknown division format: " + fullDivision + ", using as-is");
        return fullDivision;
    }
    
    
    private String convertGroupToAbbreviation(String fullGroup) {
        if (fullGroup == null) return "";
        
        if (fullGroup.contains("Group 1")) return "1";
        if (fullGroup.contains("Group 2")) return "2";
        if (fullGroup.contains("Group 3")) return "3";
        if (fullGroup.contains("Group 4")) return "4";
        if (fullGroup.contains("Group 5")) return "5";
        
        Log.w("Debug", "Unknown group format: " + fullGroup + ", using as-is");
        return fullGroup;
    }

    
    private void createStudentsSectionInSession() {
        if (!isUserAuthenticated()) {
            Log.e("Debug", "User not authenticated, cannot create students section");
            return;
        }
        
        Log.d("Debug", "Creating students section for session: " + sessionId + 
              " for branch: " + branch + ", section: " + section + ", subject: " + subject);
        
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");
        DatabaseReference sessionStudentsRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceReport")
                .child(sessionId)
                .child("Students");

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("Debug", "Found " + snapshot.getChildrenCount() + " total students");
                    
                    int initializedCount = 0;
                    int matchingStudentsCount = 0;
                    
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        String enrollmentNo = studentSnapshot.getKey();
                        String studentSection = studentSnapshot.child("section").getValue(String.class);
                        String studentGroup = studentSnapshot.child("group").getValue(String.class);
                        
                        Log.d("Debug", "Checking student: " + enrollmentNo + 
                              " (Section: " + studentSection + ", Group: " + studentGroup + ")");
                        
                        if (studentSection != null && studentSection.equals("7cse02")) {
                            matchingStudentsCount++;
                            Log.d("Debug", "Student " + enrollmentNo + " matches session criteria");

                            sessionStudentsRef.child(enrollmentNo).child("attendance_status").setValue("Not Marked")
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("Debug", "Successfully initialized session attendance for " + enrollmentNo);
                                    } else {
                                        Log.e("Debug", "Failed to initialize session attendance for " + enrollmentNo + ": " + task.getException());
                                    }
                                });

                            FirebaseDatabase.getInstance().getReference("Students")
                                    .child(enrollmentNo)
                                    .child("Attendance")
                                    .child(sessionId)
                                    .setValue("A") // default absent until marked
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("Debug", "Successfully initialized student attendance for " + enrollmentNo);
                                        } else {
                                            Log.e("Debug", "Failed to initialize student attendance for " + enrollmentNo + ": " + task.getException());
                                        }
                                    });
                            
                            initializedCount++;
                        } else {
                            Log.d("Debug", "Student " + enrollmentNo + " does not match session criteria " +
                                  "(Section: " + studentSection + " vs 7cse02)");
                        }
                    }
                    
                    Log.d("Debug", "Students section initialization completed. " +
                          "Total students: " + snapshot.getChildrenCount() + 
                          ", Matching students: " + matchingStudentsCount + 
                          ", Initialized: " + initializedCount);
                    
                    if (matchingStudentsCount == 0) {
                        Log.w("Debug", "No students found matching section: 7cse02");
                        Toast.makeText(activity_session.this, 
                            "Warning: No students found for section 7cse02", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("Debug", "Successfully included " + matchingStudentsCount + " students in session");
                        Toast.makeText(activity_session.this, 
                            "Session created with " + matchingStudentsCount + " students from section 7cse02", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w("Debug", "No students found in Students collection");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Debug", "Error creating students section: " + error.getMessage());
                handleDatabaseError(error);
            }
        });
    }

    private void endSession() {
        if (sessionId == null) {
            Toast.makeText(this, "Session not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Debug", "Ending session: " + sessionId);
        
        DatabaseReference sessionRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceReport")
                .child(sessionId);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("session_status", "ended");
        updates.put("end_timestamp", System.currentTimeMillis());
        
        sessionRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Debug", "Session ended successfully");
                Toast.makeText(this, "Session ended. Generating attendance report...", Toast.LENGTH_SHORT).show();
                
                showLogoutConfirmation();
            } else {
                Log.e("Debug", "Failed to end session: " + task.getException());
                Toast.makeText(this, "Failed to end session. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("End Session & Logout")
            .setMessage("Session ended successfully. Do you want to logout and return to login screen?")
            .setPositiveButton("Logout", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("View Report", (dialog, which) -> {
                Intent intent = new Intent(getApplicationContext(), AttendanceReportActivity.class);
                intent.putExtra("sessionId", sessionId);
                startActivity(intent);
            })
            .setIcon(android.R.drawable.ic_dialog_info)
            .show();
    }
}

