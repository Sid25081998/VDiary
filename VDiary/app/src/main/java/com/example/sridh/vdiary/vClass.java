package com.example.sridh.vdiary;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sid on 8/28/2016.
 */
public class vClass {
    public static List<subject> subList = new ArrayList<subject>();
    public static List<List<subject>> timeTable = new ArrayList<>();
    public static String semStart;
    public static String cat1;
    public static String cat2;
    public static String fat;
    public static int width;
    public static int  height;
    public static List<Notification_Holder> notes=new ArrayList<>();
    public static void setStatusBar(Window window, Context context) {
        if(Build.VERSION.SDK_INT>=21){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context,R.color.taskbar_orange));
        }
    }  //CHANGE THE COLOR OF THE STATUS BAR
    public static List<teacher> teachers= new ArrayList<>();
    public static  List<Cabin_Details> cablist=new ArrayList<>();
    public static String FIREBASE_URL= "https://vdiary-a25b2.firebaseio.com/";
    //PARAMETERS FOR CHANGING THE LINKS
    public static String SEM = "WS";
    public static String VERSION= "1.0";
}
class Note
{
    String title;
    String note;
    String time;
    String date;
}
class Cabin_Details
{
    public String name;
    public String cabin;
    public String others;

    public void setName(String name) {
        this.name = name;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
class subject {
    public String code;
    public String title;
    public String teacher;
    public String attString;
    public String room;
    public int ctd;
    public String startTime;
    public String endTime;
    public String type;
    public int notif_id;
}


class teacher{
    String name;
    String cabin;
    public String getName(){
        return this.name;
    }
    public String getCabin(){
        return this.cabin;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setCabin(String cabin){
        this.cabin=cabin;
    }
}

