package com.example.wifibasedattendanceapplication;


public class WifiConfig {
    
    public static final String[] UNIVERSITY_WIFI_SSIDS = {
        "vivo1920",
        "University_Campus",
        "Campus_WiFi",
    };
    
    public static final String[] UNIVERSITY_WIFI_BSSIDS = {
        "26:ba:0d:90:d2:e4",   // Original BSSID from the code
    };
    
    public static final String[] UNIVERSITY_WIFI_KEYWORDS = {
        "university", 
        "campus",
        "college",
        "institute"
    };
    
    public static final boolean TRUST_MODE_ENABLED = true; // Set to true to trust WiFi connections when SSID is unknown
    public static final boolean ALLOW_UNKNOWN_SSID = true; // Set to true to allow access when SSID is "<unknown ssid>"
    
    
    public static boolean shouldTrustCurrentConnection() {
        return TRUST_MODE_ENABLED;
    }
    
    
    public static boolean shouldAllowUnknownSSID() {
        return ALLOW_UNKNOWN_SSID;
    }
    
    
    public static boolean isUniversityWiFiSSID(String ssid) {
        android.util.Log.d("WifiConfig", "Checking SSID: " + ssid);
        
        if (ssid == null || ssid.isEmpty()) {
            android.util.Log.d("WifiConfig", "SSID is null or empty");
            return false;
        }
        
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
            android.util.Log.d("WifiConfig", "Removed quotes, SSID now: " + ssid);
        }
        
        String ssidLower = ssid.toLowerCase();
        android.util.Log.d("WifiConfig", "SSID lowercase: " + ssidLower);
        
        for (String universitySSID : UNIVERSITY_WIFI_SSIDS) {
            android.util.Log.d("WifiConfig", "Checking against university SSID: " + universitySSID);
            if (ssid.equals(universitySSID)) {
                android.util.Log.d("WifiConfig", "Exact SSID match found: " + universitySSID);
                return true;
            }
        }
        
        for (String keyword : UNIVERSITY_WIFI_KEYWORDS) {
            android.util.Log.d("WifiConfig", "Checking keyword: " + keyword);
            if (ssidLower.contains(keyword.toLowerCase())) {
                android.util.Log.d("WifiConfig", "Keyword match found: " + keyword);
                return true;
            }
        }
        
        android.util.Log.d("WifiConfig", "No SSID match found");
        return false;
    }
    
    
    public static boolean isUniversityWiFiBSSID(String bssid) {
        android.util.Log.d("WifiConfig", "Checking BSSID: " + bssid);
        
        if (bssid == null || bssid.isEmpty()) {
            android.util.Log.d("WifiConfig", "BSSID is null or empty");
            return false;
        }
        
        for (String universityBSSID : UNIVERSITY_WIFI_BSSIDS) {
            android.util.Log.d("WifiConfig", "Checking against university BSSID: " + universityBSSID);
            if (universityBSSID.equals(bssid)) {
                android.util.Log.d("WifiConfig", "BSSID match found: " + universityBSSID);
                return true;
            }
        }

        
        android.util.Log.d("WifiConfig", "No BSSID match found");
        return false;
    }
}

