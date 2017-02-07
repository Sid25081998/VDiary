package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sparsha Saha on 1/11/2017.
 */

public class AtBoot extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    AlarmManager alarmManager;
    Intent x;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("todoshared", MODE_PRIVATE);
        String f = sharedPreferences.getString("todolist", null);
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(f!=null)
            vClass.notes = Notification_Holder.convert_from_jason(f);

        //to-do reschedule
        for (int i=0;i<vClass.notes.size();i++)
        {
            x=new Intent(context,NotifyService.class);
            x.putExtra("fromClass","WorkSpace");
            Gson js=new Gson();
            Notification_Holder n=vClass.notes.get(i);
            String m=js.toJson(n);
            x.putExtra("notificationContent",m);
            pendingIntent=PendingIntent.getBroadcast(context,vClass.notes.get(i).id,x,0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, vClass.notes.get(i).cal.getTimeInMillis(),pendingIntent);
        }
        sharedPreferences= context.getSharedPreferences("academicPrefs",MODE_PRIVATE);
        String timeTableJson = sharedPreferences.getString("schedule",null);
        List<List<subject>> timeTable = (new Gson()).fromJson(timeTableJson,new TypeToken<List<List<subject>>>(){}.getType());
        if(timeTableJson!=null) scrapper.createNotification(context,timeTable);
        //Daily timetable reschedule end

        SharedPreferences widgetPrefs= context.getSharedPreferences("widgetPrefs",MODE_PRIVATE);
        if(widgetPrefs.getBoolean("isEnabled",false)){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent toWidgetService = new Intent(context,widgetServiceReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,toWidgetService,0);
            Calendar calendarMan = Calendar.getInstance();
            calendarMan.set(Calendar.HOUR,12);
            calendarMan.set(Calendar.MINUTE,0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarMan.getTimeInMillis(), 12 * 60 * 60 * 1000, pendingIntent);
        }
    }
}
