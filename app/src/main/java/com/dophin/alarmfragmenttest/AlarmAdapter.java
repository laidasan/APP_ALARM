package com.dophin.alarmfragmenttest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> implements  View.OnClickListener{
    private Context context;
    private DataBaseHelper dbHelper;
    private Cursor cursor;
    private Calendar c;

    public static int ALARM_ID = 0;
    public static int ALARM_TIME = 1;
    public static int ALARM_REPEAT = 2;
    public static int ALARM_ISOPEN = 3;
    public static int ALARM_CALENDAR_TIME = 4;
    private Cursor alarmcursor;
    //以下的List為依照DataBase欄位提取資料存入到List，給AlarmAdapter的onBindViewHolder使用
    private List<String> alarm_times = new ArrayList<>();
    private List<String> alarm_repeats = new ArrayList<>();
    private List<String> alarm_isopens = new ArrayList<>();
    private List<String> alarm_Calendar_times = new ArrayList<>();
    //給ImageButton用
    private List<String> alarm_ids = new ArrayList<>();


    private OnItemClickLitener monItemClickLitener;

    public interface OnItemClickLitener {
        void OnItmeClick(View view,int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener monItemClickLitener) {
        this.monItemClickLitener = monItemClickLitener;
    }

    public AlarmAdapter(List<String> alarm_times,List<String> alarm_repeats,List<String> alarm_isopens) {
        super();
        this.alarm_times = alarm_times;
        this.alarm_repeats = alarm_repeats;
        this.alarm_isopens = alarm_isopens;
    }
    public AlarmAdapter(Cursor cursor,Context context) {
        datainit(cursor);
        this.context = context;
    }
    public AlarmAdapter() {

    }

    @Override
    public AlarmAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewtype)  {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm_myself,parent,false);
        AlarmAdapter.MyViewHolder holder = new AlarmAdapter.MyViewHolder(view);
        view.setOnClickListener(this);
        //MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_alarm_myself,parent,false));
        return holder;
    }


    //RecyclerView填寫Item時主要的方法
    @Override
    public void onBindViewHolder(AlarmAdapter.MyViewHolder holder, int position) {
        Log.v("AlarmAdapter","Out If");
        if(alarmcursor.getCount() > 0) {
            holder.itemView.setTag(position);
            Log.v("AlarmAdapter","onBindViewHolder");
            holder.alarmtime.setText(alarm_times.get(position));
            if(alarm_repeats.get(position).split(",").length == 7) {
                holder.alarmlabel.setText("每天");
            }else {
                holder.alarmlabel.setText(alarm_repeats.get(position));
            }
            switch (alarm_isopens.get(position)) {
                case "開":
                    holder.alarm_open_close_button.setImageResource(R.drawable.ic_alarm_open);
                    break;
                case "關":
                    holder.alarm_open_close_button.setImageResource(R.drawable.ic_alarm_close);
                    break;
            }
        } else {
            Log.v("AlarmAdapter","NoonBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        return alarmcursor.getCount();
    }


    @Override
    public void onClick(View view) {
        if(monItemClickLitener != null) {
            monItemClickLitener.OnItmeClick(view,(int)view.getTag());
        }
    }




    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView alarmtitleimage;
        TextView alarmtime;
        TextView alarmlabel;
        ImageButton alarm_open_close_button;
        ImageButton alarm_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            alarmtitleimage = (ImageView) itemView.findViewById(R.id.alarm_titleimage_myself);
            alarmtime = (TextView)itemView.findViewById(R.id.alarm_time_myself);
            alarmlabel = (TextView)itemView.findViewById(R.id.alarm_label_myself);
            alarm_open_close_button = (ImageButton)itemView.findViewById(R.id.alarm_open_close_imagebutton);
            alarm_delete = (ImageButton)itemView.findViewById(R.id.alarm_delete_button);

            //20190101設定監聽器的方式要改，主要在Adapter外面做

            //open or close imageButtonClickListener
            alarm_open_close_button.setOnClickListener(listener -> {
                //20181231
                Intent intent = new Intent(context,CallAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(  //第二個參數為requestCode 這邊從List中取出是字串型態，所以使用Integer.Valueof
                        context,Integer.valueOf(alarm_ids.get(getLayoutPosition())),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                //20190101
                //createPendingIntentByLayoutPosition(getLayoutPosition());

                switch (alarm_isopens.get(getLayoutPosition())) {
                    case "開":
                        //換成關的圖片
                        alarm_open_close_button.setImageResource(R.drawable.ic_alarm_close);
                        //更新資料庫
                        imageonClickopentoclose();
                        //鬧鐘關閉
                        am.cancel(sender);
                        break;
                    case "關" :
                        //更換圖片
                        alarm_open_close_button.setImageResource(R.drawable.ic_alarm_open);
                        //更新資料庫與資料
                        //imageonClick("開");
                        //開啟鬧鐘

                        //2019/1/4 增加repeat比對功能跟設置時間比現在時間早會響鈴的BUG
                        Long newsqlTime;
                        Long sqlTime = Long.valueOf( alarm_Calendar_times.get(getLayoutPosition()));
                        c = Calendar.getInstance();
                        Calendar crepeat = Calendar.getInstance();
                        //int dayOfNow = c.get(Calendar.DAY_OF_WEEK);
                        int[] datOfNum = Contants.getDayOfNum(alarm_repeats.get(getLayoutPosition()));
                        int dayOfNow = Contants.getmyDayOfWeek(c.get(Calendar.DAY_OF_WEEK));

                        if(Contants.passOrNot(sqlTime) ){
                            //建議新的時間newsqlTime
                            int repeatdiffer = Contants.compareNowAndNext(datOfNum,dayOfNow);
                            newsqlTime = sqlTime + ( (24*60*60*1000) * Contants.getdifferday(sqlTime) );
                            crepeat.setTimeInMillis(newsqlTime);
                            crepeat.add(Calendar.DAY_OF_WEEK,repeatdiffer);
                            newsqlTime = crepeat.getTimeInMillis();
                            am.set(AlarmManager.RTC_WAKEUP,newsqlTime,sender);
                            imageonClickCloseToOpen(Long.toString(newsqlTime));
                            Log.v("imagebutton","if");
                        }else {
                            //這邊的是舊的time
                            am.set(AlarmManager.RTC_WAKEUP,sqlTime,sender);
                            imageonClickCloseToOpen(Long.toString(sqlTime));
                            Log.v("imagebutton","else");
                        }
                        break;
                }
            });
            //imageButtonClcikListener


            //delete imagebutton clickListener
            alarm_delete.setOnClickListener(listener -> {
                dbHelper = new DataBaseHelper(context);
                dbHelper.deleteAlarmColock(alarm_ids.get(getLayoutPosition()));
                //20190101 可能會有問題，測試只有一個鬧鐘的時候刪除看會不會有BUG
                Intent intent = new Intent(context,CallAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(  //第二個參數為requestCode 這邊從List中取出是字串型態，所以使用Integer.Valueof
                        context,Integer.valueOf(alarm_ids.get(getLayoutPosition())),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.cancel(sender);
                //注意:資料更新要在取消鬧鐘之後(am.cancel)做，因為是刪除鬧鐘，所以先把資料庫資料刪掉後更新Adapter後再am.cancel的話會找不到利用ID做的resquestCode!!
                cursor = dbHelper.selectAlarmColock();
                datainit(cursor);
                notifyDataSetChanged();
            });


        }

        private void imageonClickopentoclose() {
            //主要srarray[2]的值做改變
            dbHelper = new DataBaseHelper(context);
            String[] strarray = new String[4];
            strarray[0] = alarm_times.get(getLayoutPosition());
            strarray[1] = alarm_repeats.get(getLayoutPosition());
            strarray[2] = "關";
            strarray[3] = alarm_Calendar_times.get(getLayoutPosition());
            Log.v("strarry",alarm_ids.get(getLayoutPosition()));
            Log.v("strarry",strarray[0]);
            Log.v("strarry",strarray[1]);
            Log.v("strarry",strarray[2]);
            Log.v("strarry",strarray[3]);
            dbHelper.updateAlarmColock(alarm_ids.get(getLayoutPosition()),strarray);
            cursor = dbHelper.selectAlarmColock();
            datainit(cursor);
            dbHelper.close();
            cursor.close();
        }

        private void imageonClickCloseToOpen(String sqltime) {
            dbHelper = new DataBaseHelper(context);
            String[] strarray = new String[4];
            strarray[0] = alarm_times.get(getLayoutPosition());
            strarray[1] = alarm_repeats.get(getLayoutPosition());
            strarray[2] = "開";
            strarray[3] = sqltime;
            dbHelper.updateAlarmColock(alarm_ids.get(getLayoutPosition()),strarray);
            cursor = dbHelper.selectAlarmColock();
            datainit(cursor);
            dbHelper.close();
            cursor.close();
        }

        //private void createPendingIntentByLayoutPosition(int position) {
        //    Intent intent = new Intent(context,CallAlarm.class);
        //    PendingIntent sender = PendingIntent.getBroadcast(  //第二個參數為requestCode 這邊從List中取出是字串型態，所以使用Integer.Valueof
        //            context,Integer.valueOf(alarm_ids.get(position)),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //    AlarmManager am =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //}





    }


    //更新資料，也有用在AlarmAdapter的建構式，主要用來從Cursor中提取資料存到ArrayList裡面
    public void datainit(Cursor cursor) {
        alarm_times.clear();
        alarm_repeats.clear();
        alarm_isopens.clear();
        alarm_ids.clear();
        alarm_Calendar_times.clear();
        alarmcursor = cursor;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for(int i = 0;i < cursor.getCount();i++) {
                cursor.moveToPosition(i);
                alarm_times.add(cursor.getString(ALARM_TIME));
                alarm_repeats.add(cursor.getString(ALARM_REPEAT));
                alarm_isopens.add(cursor.getString(ALARM_ISOPEN));
                alarm_ids.add(cursor.getString(ALARM_ID));
                alarm_Calendar_times.add(cursor.getString(ALARM_CALENDAR_TIME));
                Log.v("AlarmAdapter","建構式");
            }
        }else {
            Toast.makeText(context,"Please Create a alarmcolock!!",Toast.LENGTH_SHORT).show();
            Log.v("AlarmAdapter","資料庫沒資料");
        }
    }




}

