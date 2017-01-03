package com.example.sridh.vdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class workSpace extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static SharedPreferences shared;
    public static SharedPreferences.Editor editor;
    static Context context;
    public static  List<Cabin_Details> cablist;
    List<Notification_Holder> noti_todo;
    static int id=0;

    static ListView resultList;
    @Override
    public void onBackPressed() {
            if(resultList.getVisibility()==View.VISIBLE){
                resultList.setVisibility(View.INVISIBLE);
            }
            else finish();
        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);
        context =this;
        noti_todo=new ArrayList<>();
        //Toast.makeText(context, Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault())+"", Toast.LENGTH_SHORT).show();


       // Notification_Creator nc=new Notification_Creator("x","y","z",context);  //Test notification
        //nc.create_notification();
        shared=getSharedPreferences("todoshared",MODE_PRIVATE);




        //Get vClass.notes list from shared preferences
        String get_list=shared.getString("todolist",null);
        if(get_list!=null) {
            vClass.notes=Notification_Holder.convert_from_jason(get_list);
        }
        //vClass.notes is initialized

        editor=shared.edit();
        id=shared.getInt("identifier",0);
        setToolbars();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        cablist=new ArrayList<>();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        vClass.setStatusBar(getWindow(),getApplicationContext());

    }
    void setToolbars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.workspacetoptoolbar);
        toolbar.setTitle("Workspace");
        setSupportActionBar(toolbar);
    }  //SET THE TOOLBARS FOR THE WORKSPACE CLASS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workspace_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toSchedule) {
            Intent i=new Intent(workSpace.this,schedule.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);

        }
        else if(id==R.id.refresh){
            scrapper.tryRefresh=true;
            startActivity(new Intent(workSpace.this,scrapper.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        EditText teacherSearch;
        List<teacher> searchResult;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView=null;
            switch(getArguments().getInt(ARG_SECTION_NUMBER)-1)
            {
                case 1:

                    //Initializing cablist from shared preferences
                    String temp=shared.getString("list","");
                    Type type= new TypeToken<List<Cabin_Details>>(){}.getType();


                    if(!temp.equals("")) {
                        Gson json = new Gson();
                        cablist = json.fromJson(temp, type);
                    }
                    //cablist initialized

                    rootView=inflater.inflate(R.layout.fragment_teachers,container,false);
                    setSearcher(rootView);
                    FloatingActionButton fab=(FloatingActionButton)rootView.findViewById(R.id.teachers_add);
                    ListView lv=(ListView)rootView.findViewById(R.id.teachers_list);
                    final listAdapter_teachers mad=new listAdapter_teachers(context,cablist);
                    showSubject.todoList=mad;
                    listAdapter_searchTeacher.teacherAdapter=mad;
                    lv.setAdapter(mad);
                    fab.setOnClickListener(new View.OnClickListener() { //Onclick Listener for floating action Button
                        @Override
                        public void onClick(View v) {
                            showCabinAlertDialog(mad);
                        }
                    });

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final AlertDialog.Builder build=new AlertDialog.Builder(context);
                            final AlertDialog aler;
                            final LinearLayout lin=new LinearLayout(context);
                            lin.setOrientation(LinearLayout.HORIZONTAL);
                            final Button delete=new Button(context);
                            delete.setText("Delete");
                            lin.addView(delete);
                            build.setView(lin);
                            aler=build.create();
                            aler.show();
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cablist.remove(position);
                                    mad.updatecontent(cablist);
                                    aler.cancel();
                                    Gson json=new Gson();
                                    String k=json.toJson(cablist);
                                    editor.putString("list",k);
                                    editor.apply();

                                }
                            });



                        }
                    });
                    break;
                case 0:
                    rootView=inflater.inflate(R.layout.fragment_courses,container,false);
                    ListView lview=(ListView)rootView.findViewById(R.id.course_listview);
                    listAdapter_courses cadd=new listAdapter_courses(context,vClass.subList);
                    lview.setAdapter(cadd);
                    lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent showSubjectIntent =new Intent(context,showSubject.class);
                            showSubjectIntent.putExtra("position",position);
                            context.startActivity(showSubjectIntent);
                        }
                    });
                    break;
                case 2:
                    rootView=inflater.inflate(R.layout.fragment_notes,container,false);
                    ListView lview1=(ListView)rootView.findViewById(R.id.notes_list);
                    final listAdapter_todo adap=new listAdapter_todo(context,vClass.notes);
                    lview1.setAdapter(adap);
                    FloatingActionButton fb=(FloatingActionButton)rootView.findViewById(R.id.notes_add);


                    //Enter each element of listview
                    fb.setOnClickListener(new View.OnClickListener() { //Floating action button onclick listener
                        @Override
                        public void onClick(View v) {

                            final EditText title;
                            EditText other;
                            TimePicker time;
                            DatePicker date;
                            Button ok;


                            final AlertDialog alert;
                            View root=getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_todo,null);
                            title=(EditText) root.findViewById(R.id.title);
                            other=(EditText)root.findViewById(R.id.note);
                            time=(TimePicker)root.findViewById(R.id.timePicker);
                            date=(DatePicker)root.findViewById(R.id.datePicker);
                            ok=(Button)root.findViewById(R.id.enterbutton);
                            AlertDialog.Builder bui=new AlertDialog.Builder(context);
                            bui.setView(root);
                            alert=bui.create();
                            alert.show();

                            //onlick of button ok
                            final DatePicker finalDate = date;
                            final TimePicker finalTime = time;
                            final EditText finalTitle = title;
                            final EditText finalOther = other;
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Calendar c=Calendar.getInstance();
                                    c.set(finalDate.getYear(),finalDate.getMonth(),finalDate.getDayOfMonth(),finalTime.getCurrentHour(),finalTime.getCurrentMinute());

                                    if(finalTitle.getText().toString()!=null && finalTitle.getText().toString().equals("")!=true && finalOther.getText().toString()!=null && finalOther.getText().toString().equals("")!=true) {
                                        Notification_Holder n=new Notification_Holder(c,finalTitle.getText().toString(),finalOther.getText().toString());
                                        vClass.notes.add(n);
                                        schedule_todo_notification(n);
                                        Gson json = new Gson();
                                        String temporary = json.toJson(vClass.notes);
                                        editor.putString("todolist", temporary);
                                        editor.apply();
                                        adap.update(vClass.notes);
                                        alert.cancel();
                                    }
                                    else
                                        Toast.makeText(getContext(), "Both title and note must contain some text", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                    // OnItemclicklistener for Click for each item for listview1
                     lview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                             final LinearLayout layouts=new LinearLayout(context);
                             final Button del=new Button(context);
                             del.setText("Delete");
                             layouts.addView(del);
                             final AlertDialog ale;
                             AlertDialog.Builder buil=new AlertDialog.Builder(context);
                             buil.setView(layouts);
                             ale=buil.create();
                             ale.show();

                             del.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     vClass.notes.remove(position);
                                     adap.update(vClass.notes);
                                     ale.cancel();
                                     editor.putString("todolist",Notification_Holder.convert_to_jason(vClass.notes));
                                     editor.apply();
                                 }
                             });

                         }
                     });
            }

            return rootView;
        }

        public void schedule_todo_notification(Notification_Holder n) {
            AlarmManager alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(getActivity(),NotifyService.class);
            Gson js=new Gson();
            String f=js.toJson(n);
            intent.putExtra("one",f);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(getContext(),id,intent,0);
            id++;
            editor.putInt("identifier",id);
            alarmManager.set(AlarmManager.RTC_WAKEUP,n.cal.getTimeInMillis(),pendingIntent);
        }

        void showCabinAlertDialog(final listAdapter_teachers cabinListAdapter){
            final AlertDialog.Builder alertBuilder= new AlertDialog.Builder(context);
            final View alertCabinView=getActivity().getLayoutInflater().inflate(R.layout.floatingview_add_cabin,null);
            alertBuilder.setView(alertCabinView);
            final AlertDialog alert = alertBuilder.create();
            alertCabinView.findViewById(R.id.alert_cabin_addButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name= ((TextView)alertCabinView.findViewById(R.id.alert_cabin_teacherName)).getText().toString();
                    String cabin=((TextView)alertCabinView.findViewById(R.id.alert_cabin_cabinAddress)).getText().toString();
                    String comment=((TextView)alertCabinView.findViewById(R.id.alert_cabin_comment)).getText().toString();
                    if(name.trim().equals("")|| cabin.trim().equals("")){
                        Toast.makeText(context,"Invalid Data !",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Cabin_Details c=new Cabin_Details();
                        c.name= name;
                        c.cabin=cabin;
                        c.others=comment;
                        cablist.add(c);
                        cabinListAdapter.updatecontent(cablist);
                        alert.cancel();
                        Gson json=new Gson();
                        String k=json.toJson(cablist);
                        editor.putString("list",k);
                        editor.apply();
                    }
                }
            });
            alert.show();
        }  //CREATE AND HANDLES THE ALERT DIALOG BOX TO ADD CABIN


        void setSearcher(View view){
            searchResult= new ArrayList<>();
            teacherSearch=(EditText)view.findViewById(R.id.teachers_searchText);
            resultList=(ListView)view.findViewById(R.id.teachers_search_list);
            final listAdapter_searchTeacher searchAdapter= new listAdapter_searchTeacher(context,searchResult);
            resultList.setAdapter(searchAdapter);
            teacherSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    searchResult.clear();
                    String search=teacherSearch.getText().toString();
                    if(!(search.length()<2)){
                        resultList.setVisibility(View.VISIBLE);
                        for(int i=0;i<vClass.teachers.size();i++){
                            teacher var = vClass.teachers.get(i);
                            if(var.name.toLowerCase().contains(search.toLowerCase())){
                                searchResult.add(var);
                            }
                        }
                    }
                    else{
                        resultList.setVisibility(View.INVISIBLE);
                    }
                    searchAdapter.update(searchResult);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Courses";
                case 1:
                    return "Teachers";
                case 2:
                    return "Tasks";
            }
            return null;
        }
    }
}