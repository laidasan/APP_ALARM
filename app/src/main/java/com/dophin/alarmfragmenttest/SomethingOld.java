package com.dophin.alarmfragmenttest;

public class SomethingOld {

    //原本在Alarm_Fragment


    //20190103將寫在內部的class搬移出去
    /*public static int ALARM_ID = 0;
    public static int ALARM_TIME = 1;
    public static int ALARM_REPEAT = 2;
    public static int ALARM_ISOPEN = 3;
    public static int ALARM_CALENDAR_TIME = 4;*/


    /* class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder>  {
        private Cursor alarmcursor;
        //以下的List為依照DataBase欄位提取資料存入到List，給AlarmAdapter的onBindViewHolder使用
        private List<String> alarm_times = new ArrayList<>();
        private List<String> alarm_repeats = new ArrayList<>();
        private List<String> alarm_isopens = new ArrayList<>();
        private List<String> alarm_Calen裡面dar_times = new ArrayList<>();





        //給ImageButton用
        private List<String> alarm_ids = new ArrayList<>();


        public AlarmAdapter() {
            super();
        }
        public AlarmAdapter(Cursor cursor) {
            datainit(cursor);
        }

        public void setOnItemClickLitener(AdapterView.OnItemClickListener onItemClickListener) {
            //mOnItemClickListener = onItemClickListener;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewtype)  {
            View view = LayoutInflater.from(context).inflate(R.layout.item_alarm_myself,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            //view.setOnClickListener(this);
            //MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_alarm_myself,parent,false));
            return holder;
        }


        //RecyclerView填寫Item時主要的方法
        @Override
        public void onBindViewHolder( MyViewHolder holder, int position) {
            Log.v("AlarmAdapter","Out If");
            if(alarmcursor.getCount() > 0) {
                Log.v("AlarmAdapter","onBindViewHolder");
                holder.alarmtime.setText(alarm_times.get(position));
                //如果一周七天都有重複就是每天
                if(alarm_repeats.size() == 7) {
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
                            imageonClick("關");
                            //鬧鐘關閉
                            am.cancel(sender);
                            break;
                        case "關" :
                            //更換圖片
                            alarm_open_close_button.setImageResource(R.drawable.ic_alarm_open);
                            //更新資料庫與資料
                            imageonClick("開");
                            //開啟鬧鐘
                            am.set(AlarmManager.RTC_WAKEUP,Long.parseLong(alarm_Calendar_times.get(getLayoutPosition())),sender);
                            break;

                    }
                    //20181231測試getLayoutPosition()
                    //alarmlabel.setText(Integer.toString(getLayoutPosition()));
                    //Log.v("LayoutPosition","imagebutton");


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
                    malarmadapter.notifyDataSetChanged();
                });


            }

            private void imageonClick(String isopen) {
            String[] strarray = new String[4];
            strarray[0] = alarm_times.get(getLayoutPosition());
            strarray[1] = alarm_repeats.get(getLayoutPosition());
            strarray[2] = isopen;
            strarray[3] = alarm_Calendar_times.get(getLayoutPosition());
            dbHelper.updateAlarmColock(alarm_ids.get(getLayoutPosition()),strarray);
            cursor = dbHelper.selectAlarmColock();
            datainit(cursor);
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




    }*/
}
