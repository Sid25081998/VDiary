package com.example.sridh.vdiary;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Sparsha Saha on 1/15/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Firebase database;
    Context context;
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        context=ctxt;
        Firebase.setAndroidContext(context);
        database= new Firebase(vClass.FIREBASE_URL);
        getHolidays(context);
        requestToDatabase();
    }
    void getHolidays(final Context context){
        Firebase.setAndroidContext(context);
        final Firebase database= new Firebase(vClass.FIREBASE_URL);
        database.child("Holidays").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String dateString = snapshot.getValue().toString();
                    Calendar c = Calendar.getInstance();
                    c.set(Integer.parseInt(dateString.substring(6)),Integer.parseInt(dateString.substring(3,5))-1,Integer.parseInt(dateString.substring(0,2)));
                    vClass.holidays.add(new holiday(c,snapshot.getKey()));
                }
                Gson serializer = new Gson();
                SharedPreferences.Editor holidays= context.getSharedPreferences("holidayPrefs",Context.MODE_PRIVATE).edit();
                holidays.putString("holidays",serializer.toJson(vClass.holidays));
                holidays.apply();
                updateWidget();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    void requestToDatabase(){
        SharedPreferences teacherPrefs = context.getSharedPreferences("teacherPrefs",Context.MODE_PRIVATE);
        String teacherJson = teacherPrefs.getString("customTeachers",null);
        if(teacherJson!=null){
            List<Cabin_Details> cabin_detailsList = (new Gson()).fromJson(teacherJson,new TypeToken<List<Cabin_Details>>(){}.getType());
            if (cabin_detailsList.size() > 0) {
                for (Cabin_Details editedTeacher : cabin_detailsList) {
                    database.child("custom").child(editedTeacher.name + "--" + editedTeacher.cabin).setValue(editedTeacher);
                }
            }
        }
    }

    void updateWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_today);
    }
}
