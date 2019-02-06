package com.dophin.alarmfragmenttest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Alarm_Fragment extends Fragment {
    private Context context;
    private  DataBaseHelper dbHelper;
    private Cursor cursor;

    private RecyclerView mrecyclerview;
    private AlarmAdapter malarmadapter;
    private  boolean fragmentopened = false;

    public static int ALARM_ID = 0;
    public static int ALARM_TIME = 1;
    public static int ALARM_REPEAT = 2;
    public static int ALARM_ISOPEN = 3;
    public static int ALARM_CALENDAR_TIME = 4;


    private List<String> alarm_times = new ArrayList<>();
    private List<String> alarm_repeats = new ArrayList<>();
    private List<String> alarm_isopens = new ArrayList<>();
    private List<String> alarm_Calendar_times = new ArrayList<>();
    //給ImageButton用
    private List<String> alarm_ids = new ArrayList<>();


    private Calendar c = Calendar.getInstance();

    @Override
    public void onAttach(Context context) {
        dbHelper = new DataBaseHelper(context);
        cursor = dbHelper.selectAlarmColock();
        if(getArguments() != null) {
            alarm_ids = getArguments().getStringArrayList("ID");
            alarm_times = getArguments().getStringArrayList("TIME");
            alarm_repeats=getArguments().getStringArrayList("REPEAT");
            alarm_isopens = getArguments().getStringArrayList("ISOPEN");
            alarm_Calendar_times = getArguments().getStringArrayList("CALENDAR_TIME");
            //fragmentopened = getArguments().getBoolean("fragmnetopened");
        }else  {
            Log.v("onAttach","No information");
        }
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alarm,container,false);
        Log.v("Calendar",Integer.toString(c.get(Calendar.DAY_OF_WEEK)));
        dbHelper = new DataBaseHelper(context);
        dbHelper.DropAndCreatNew();
        cursor = dbHelper.selectAlarmColock();
        mrecyclerview = view.findViewById(R.id.alarm_recyclerview);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(context));
        mrecyclerview.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));  //畫底線
        if(cursor.getCount() > 0 && malarmadapter == null) {                                                    //資料庫有資料才配Adater給RecyclerView，不然會錯喔(下面Adapter設置的關係)
            mrecyclerview.setAdapter(malarmadapter = new AlarmAdapter(cursor,context));
            //設置ItemView的Click事件
            setClickLitener(malarmadapter);
            fragmentopened = true;
        }else  if(fragmentopened && malarmadapter != null){
            mrecyclerview.setAdapter(malarmadapter = new AlarmAdapter(alarm_times,alarm_repeats,alarm_isopens));
           setClickLitener(malarmadapter);
           Toast.makeText(context,"fregmentopened",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context,"現在沒鬧鐘喔!",Toast.LENGTH_SHORT).show();
            Log.v("onCreateView","if else");
        }

        setfloatingbuttonListener(view);
        dbHelper.close();
        cursor.close();
        Log.v("alarmcreate","onCreateView");
        return view;
    }

    public static Alarm_Fragment newInstence(Context context) {
        DataBaseHelper mdbhelper = new DataBaseHelper(context);
        mdbhelper.DropAndCreatNew();
        Cursor mcursor = mdbhelper.selectAlarmColock();

        Alarm_Fragment mfragment = new Alarm_Fragment();
        //以下的List為依照DataBase欄位提取資料存入到List，給AlarmAdapter的onBindViewHolder使用
        ArrayList<String> alarm_times = new ArrayList<>();
        ArrayList<String> alarm_repeats = new ArrayList<>();
        ArrayList<String> alarm_isopens = new ArrayList<>();
        ArrayList<String> alarm_Calendar_times = new ArrayList<>();
        //給ImageButton用
        ArrayList<String> alarm_ids = new ArrayList<>();
        if(mcursor.getCount() >0 && mcursor.getCount() != -1) {
            for(int i = 0;i < mcursor.getCount();i++) {
                alarm_ids.add(mcursor.getString(ALARM_ID));
                alarm_times.add(mcursor.getString(ALARM_TIME));
                alarm_repeats.add(mcursor.getString(ALARM_REPEAT));
                alarm_isopens.add(mcursor.getString(ALARM_ISOPEN));
                alarm_Calendar_times.add(mcursor.getString(ALARM_CALENDAR_TIME));
                Log.v("newInstence","have cursor");
            }
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("ID",alarm_ids);
            bundle.putStringArrayList("TIME",alarm_times);
            bundle.putStringArrayList("REPEAT",alarm_repeats);
            bundle.putStringArrayList("ISOPEN",alarm_isopens);
            bundle.putStringArrayList("CALENDAR_TIME",alarm_Calendar_times);
            //bundle.putBoolean("fragmentopened", fragmentopened);
            mfragment.setArguments(bundle);
        }else  {
            Log.v("newInstence","NO CURSOR");
        }
        return mfragment;
    }




    //此方法為點floatingActionbutton的監聽器，叫出TimperPicker來選擇鬧鐘時間，選擇完按下確定後會更新RecycerView與資料庫(都是初始化的值)
    //比如說一新增鬧鐘就是打開的狀態，直接新增到AlarmManger
    public void setfloatingbuttonListener(View view) {
        dbHelper = new DataBaseHelper(context);
        String[] strarray = new String[4];
        view.findViewById(R.id.alarm_floatingactionbotton).setOnClickListener(onClickListener -> {
            c.setTimeInMillis(System.currentTimeMillis());
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            int mDay = c.get(Calendar.DAY_OF_WEEK);

            new TimePickerDialog(context,
                    (timepickerview, hourOfDay, minute) -> {
                        c.setTimeInMillis(System.currentTimeMillis());
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);


                        if( Contants.passOrNot(c.getTimeInMillis())) {
                            c.add(Calendar.DAY_OF_WEEK,1);
                        }

                        Log.v("Calendar","FlootingButton的"+Long.toString(c.getTimeInMillis()));


                        //記下時間是因為從關閉重新開啟鬧鐘的時候要記下時間!!
                        String tmpS1 = format(hourOfDay) + "：" + format(minute);
                        Long time = c.getTimeInMillis();
                        strarray[0] = tmpS1;
                        strarray[1] = "周一,周二,周三,周四,周五,周六,周日";
                        strarray[2] = "開";
                        strarray[3] = time.toString();
                        dbHelper.insertAlarmColock(strarray);
                        cursor = dbHelper.selectAlarmColock();

                        //這邊的判斷為，是否已經有配過Adapter了，有的話將是Adapter中的資料更新後，再用notifyDataSetChanged()的方式去更新RecyclerView就好
                        //notifyDataSetChanged()會在onBindViweHolder一次
                        //沒有先配過Adapter的情況是資料庫沒有資料的時候
                        if (malarmadapter != null) {
                            malarmadapter.datainit(cursor);
                            malarmadapter.notifyDataSetChanged();
                        }else {
                            mrecyclerview.setAdapter(malarmadapter = new AlarmAdapter(cursor,context));
                        }


                        //依照id delete掉資料後，爾後Insert的資料不會從delete的id開始insert，是從最後一個往後insert
                        //所以這裡使用id來讓PendingIntent的requesCode來區分是不同的PendingIntent
                        //這樣才不會把原本的PendingIntent覆蓋過去
                        cursor.moveToLast();
                        int requesCode = cursor.getInt(0);
                        Intent intent = new Intent(context,CallAlarm.class);
                        PendingIntent sender = PendingIntent.getBroadcast(
                                context,requesCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        //Log.v("requesCode",Integer.toString(requesCode));
                        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),sender);
                        Toast.makeText(context,
                                "设置闹钟时间为" + tmpS1,
                                Toast.LENGTH_SHORT).show();
                        dbHelper.close();
                        cursor.close();
                    }, mHour, mMinute, false).show();
        });

    }

    public void setClickLitener(AlarmAdapter alarmAdapter) {
        alarmAdapter.setOnItemClickLitener(  (itemview,position) -> {
            Toast.makeText(context,"Click",Toast.LENGTH_SHORT).show();
            Log.v("onClick","GO");
        });
    }

    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }


}
