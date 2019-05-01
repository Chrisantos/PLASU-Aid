package com.eze.chrisantus.emergencyreporter.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistData {
    private static final String PREF_NAME = "persist_home";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String LEVEL = "level";
    private static final String MATNO = "matno";
    private static final String DEPT = "dept";
    private static final String GENDER = "gender";
    private static final String PHONE = "phone";
    private static final String BLOODGROUP = "bloodgroup";
    private static final String GUARDIAN_NO = "guardian_no";
    private static final String AUTHTYPE = "authtype";

    private static final String DEFAULT_MSG = "mssg";
    private static final String FIRE_NO = "fire";
    private static final String SECURITY_NO = "sec";
    private static final String CLINIC_NO = "cinic";

    private static final String KEY = "isloggedin";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ADDRESS = "address";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public PersistData(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setUserDetails(String name, String email, String matno, String gender,
            String level, String dept, String phone, String bloodgroup, String guardian_no, String authtype){
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(MATNO, matno);
        editor.putString(GENDER, gender);
        editor.putString(LEVEL, level);
        editor.putString(DEPT, dept);
        editor.putString(PHONE, phone);
        editor.putString(BLOODGROUP, bloodgroup);
        editor.putString(GUARDIAN_NO, guardian_no);
        editor.putString(AUTHTYPE, authtype);

        editor.apply();
    }

    public void setDefaultMsg(String msg) {
        editor.putString(DEFAULT_MSG, msg);
        editor.apply();
    }
    public void setFireNo(String number) {
        editor.putString(FIRE_NO, number);
        editor.apply();
    }
    public void setSecurityNo(String number) {
        editor.putString(SECURITY_NO, number);
        editor.apply();
    }
    public void setClinicNo(String number) {
        editor.putString(CLINIC_NO, number);
        editor.apply();
    }

    public void setLogin(boolean isloggedIn){
        editor.putBoolean(KEY, isloggedIn);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return preferences.getBoolean(KEY, false);
    }

    public String getName(){return preferences.getString(NAME, "");}
    public String getEmail(){return preferences.getString(EMAIL, "");}
    public String getMatno(){return preferences.getString(MATNO, "");}
    public String getGender(){return preferences.getString(GENDER, "");}
    public String getLevel(){return preferences.getString(LEVEL, "");}
    public String getDept(){return preferences.getString(DEPT, "");}
    public String getPhone(){return preferences.getString(PHONE, "");}
    public String getBloodgroup(){return preferences.getString(BLOODGROUP, "");}
    public String getGuardianNo(){return preferences.getString(GUARDIAN_NO, "");}
    public String getAuthtype(){return preferences.getString(AUTHTYPE, "");}

    public String getDefaultMsg(){return preferences.getString(DEFAULT_MSG, "");}
    public String getFireNo(){return preferences.getString(FIRE_NO, "");}
    public String getSecurityNo(){return preferences.getString(SECURITY_NO, "");}
    public String getClinicNo(){return preferences.getString(CLINIC_NO, "");}

    public void setLongitude(String longitude) {
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }
    public void setLatitude(String latitude) {
        editor.putString(LATITUDE, latitude);
        editor.apply();
    }
    public void setAddress(String address) {
        editor.putString(ADDRESS, address);
        editor.apply();
    }
    public String getLongitude(){return preferences.getString(LONGITUDE, "");}
    public String getLatitude(){return preferences.getString(LATITUDE, "");}
    public String getAddress(){return preferences.getString(ADDRESS, "");}
}
