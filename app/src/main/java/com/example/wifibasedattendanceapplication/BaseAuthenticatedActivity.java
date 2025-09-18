package com.example.wifibasedattendanceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Toast;
import android.os.Handler;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {
    
    protected FirebaseAuth mAuth;
    protected FirebaseUser currentUser;
    private static final String TAG = "BaseAuthenticated";
    private boolean isCheckingAuth = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    
    protected void checkAuthenticationState() {
        currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "Checking authentication state. Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));
        
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, redirecting to login");
            redirectToLogin();
        } else {
            Log.d(TAG, "User authenticated: " + currentUser.getEmail());
            onUserAuthenticated();
        }
    }
    
    
    protected void onUserAuthenticated() {
    }
    
    
    protected void redirectToLogin() {
        Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    
    protected void signOut() {
        mAuth.signOut();
        redirectToLogin();
    }
    
    
    protected boolean isUserAuthenticated() {
        boolean isAuth = mAuth.getCurrentUser() != null;
        Log.d(TAG, "isUserAuthenticated() called, result: " + isAuth + 
              (isAuth ? ", user: " + mAuth.getCurrentUser().getEmail() : ""));
        return isAuth;
    }
    
    
    protected void addAuthStateListener() {
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d(TAG, "User signed out, redirecting to login");
                    redirectToLogin();
                }
            }
        });
    }
    
    
    protected void handleDatabaseError(DatabaseError error) {
        Log.e(TAG, "Database error: " + error.getMessage() + " (Code: " + error.getCode() + ")");
        
        switch (error.getCode()) {
            case DatabaseError.PERMISSION_DENIED:
                Log.w(TAG, "Database permission denied, user may not be authenticated");
                refreshAuthToken();
                Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case DatabaseError.DISCONNECTED:
                Log.w(TAG, "Database disconnected, retrying...");
                Toast.makeText(this, "Connection lost. Retrying...", Toast.LENGTH_SHORT).show();
                break;
            case DatabaseError.UNAVAILABLE:
                Log.w(TAG, "Database unavailable, retrying...");
                Toast.makeText(this, "Service unavailable. Retrying...", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.e(TAG, "Unknown database error: " + error.getMessage());
                Toast.makeText(this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
    
    
    protected void refreshAuthToken() {
        if (currentUser != null) {
            currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Token refreshed successfully");
                    } else {
                        Log.w(TAG, "Token refresh failed: " + task.getException());
                        signOut();
                    }
                });
        }
    }
    
    
    protected boolean needsReauthentication() {
        if (currentUser != null) {
            return currentUser.isEmailVerified() == false;
        }
        return true;
    }
    
    
    protected void performDatabaseOperation(Runnable operation) {
        Log.d(TAG, "performDatabaseOperation() called");
        if (shouldAllowAccess()) {
            Log.d(TAG, "Access allowed, proceeding with operation");
            operation.run();
        } else {
            Log.w(TAG, "Access denied, calling onAuthenticationFailed");
            onAuthenticationFailed();
        }
    }
    
    
    protected boolean isLoginActivity() {
        String className = this.getClass().getSimpleName();
        return className.equals("LoginActivity") || 
               className.equals("LoginStudentActivity") || 
               className.equals("LoginFacultyActivity");
    }
    
    
    protected void ensureAuthenticated() {
        if (!isUserAuthenticated()) {
            Log.w(TAG, "User not authenticated, redirecting to login");
            redirectToLogin();
        }
    }
    
    
    protected boolean isFirebaseReady() {
        try {
            return mAuth != null && FirebaseDatabase.getInstance() != null;
        } catch (Exception e) {
            Log.e(TAG, "Firebase not ready: " + e.getMessage());
            return false;
        }
    }
    
    
    protected boolean isActivityValid() {
        return !isFinishing() && !isDestroyed() && !isChangingConfigurations();
    }
    
    
    protected boolean shouldAllowAccess() {
        return isUserAuthenticated();
    }
    
    
    protected void onAuthenticationFailed() {
        Log.w(TAG, "Authentication failed, redirecting to login");
        redirectToLogin();
    }
    
    
    protected void onAuthenticationSuccess() {
        Log.d(TAG, "Authentication successful");
    }
    
    
    protected void onAuthenticationStateChanged(boolean isAuthenticated) {
        Log.d(TAG, "Authentication state changed: " + isAuthenticated);
    }
    
    
    protected boolean shouldCheckAuthentication() {
        return true;
    }
    
    
    protected long getAuthenticationCheckDelay() {
        return 1000; // 1 second
    }
    
    
    protected boolean shouldRetryAuthenticationCheck() {
        return false;
    }
    
    
    protected long getAuthenticationCheckRetryDelay() {
        return 2000; // 2 seconds
    }
    
    
    protected int getAuthenticationCheckRetryCount() {
        return 0;
    }
    
    
    
    
    protected void onAuthenticationCheckFailed() {
        Log.w(TAG, "Authentication check failed");
        onAuthenticationFailed();
    }
    
    
    protected void onAuthenticationCheckSuccess() {
        Log.d(TAG, "Authentication check successful");
        onAuthenticationSuccess();
    }
    
    
    protected void onAuthenticationCheckRetryAttempt(int retryCount, int maxRetries) {
        Log.d(TAG, "Authentication check retry attempt: " + retryCount + "/" + maxRetries);
    }
    
    
    protected void onAuthenticationCheckRetryFailed(int maxRetries) {
        Log.w(TAG, "Authentication check retry failed after " + maxRetries + " attempts");
        onAuthenticationCheckFailed();
    }
    
    
    protected void onAuthenticationCheckRetrySuccess(int retryCount) {
        Log.d(TAG, "Authentication check retry successful after " + retryCount + " attempts");
        onAuthenticationCheckSuccess();
    }
    
    
    
    
    protected void onAuthenticationCheckRetry(int retryCount) {
        Log.d(TAG, "Authentication check retry: " + retryCount);
    }
    
    
    protected boolean hasValidToken() {
        if (currentUser != null) {
            return true;
        }
        return false;
    }
    
    
    protected boolean validateAuthentication() {
        if (!isUserAuthenticated()) {
            Log.w(TAG, "User not authenticated");
            return false;
        }
        
        if (!hasValidToken()) {
            Log.w(TAG, "User token not valid, refreshing...");
            refreshAuthToken();
            return false;
        }
        
        return true;
    }
    
    
    protected void performAuthenticatedOperation(Runnable operation) {
        if (validateAuthentication()) {
            operation.run();
        } else {
            Log.w(TAG, "Authentication validation failed, cannot perform operation");
        }
    }
    
    
    protected void handleAuthError(Exception e) {
        Log.e(TAG, "Authentication error: " + e.getMessage());
        if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException ||
            e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Invalid credentials. Please login again.", Toast.LENGTH_LONG).show();
            signOut();
        } else {
            Toast.makeText(this, "Authentication error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    
    protected boolean needsReauthenticationDueToSecurity() {
        if (currentUser != null) {
            long lastSignIn = currentUser.getMetadata().getLastSignInTimestamp();
            long currentTime = System.currentTimeMillis();
            long timeSinceLastSignIn = currentTime - lastSignIn;
            
            return timeSinceLastSignIn > 604800000; // 7 days in milliseconds
        }
        return true;
    }
    
    
    protected boolean performComprehensiveAuthCheck() {
        if (!isUserAuthenticated()) {
            Log.w(TAG, "User not authenticated");
            return false;
        }
        
        return true;
    }
    
    
    protected void performSecureDatabaseOperation(Runnable operation) {
        if (performComprehensiveAuthCheck()) {
            operation.run();
        } else {
            Log.w(TAG, "Comprehensive authentication check failed, cannot perform operation");
            refreshAuthToken();
            new Handler().postDelayed(() -> {
                if (performComprehensiveAuthCheck()) {
                    operation.run();
                } else {
                    Log.e(TAG, "Authentication check failed after retry, redirecting to login");
                    redirectToLogin();
                }
            }, 1000); // 1 second delay
        }
    }
}

