package com.dophin.alarmfragmenttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    public final static String DATABASW_NAME = "alarm_db";
    public final static int DATABASE_VERSION = 1;

    private final static String ALARM_COLOCK_TABLE = "alarmcolock";
    public final static String ALARM_ID = "_id";
    public final static String ALARM_TIME = "alarmtime"; //alarm time
    public final static String ALARM_REPEAT = "alarmrepeat";//alarm repeate is or not
    public final static String ALARM_ISOPEN = "alarmisopen";//alarm open is 0r not
    public final static String ALARM_CALENDER_TIME = "alarmcalendertime"; //alarm is kind(1 is shuangseqiu ,2 is da le tou )
    public final static String ALARM_SPARE1 = "alarmspare1";//spare1
    public final static String ALARM_SPARE2 = "alarmspare2";//spare2
    public final static String ALARM_SPARE3 = "alarmspare3";//spare3
    public DataBaseHelper(Context context) {
        super(context,DATABASW_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String  sql = "create table "+ALARM_COLOCK_TABLE+" ("
                +ALARM_ID+" integer primary key autoincrement, "
                +ALARM_TIME+" text, "
                +ALARM_REPEAT+" text, "
                +ALARM_ISOPEN+" text, "
                +ALARM_CALENDER_TIME+" text, "
                +ALARM_SPARE1+" text, "
                +ALARM_SPARE2+" text, "
                +ALARM_SPARE3+" text )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        String sql = "drop table if exists "+ALARM_COLOCK_TABLE;
        db.execSQL(sql);
    }

    public long insertAlarmColock(String[] strArray){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conv = new ContentValues();
        conv.put(ALARM_TIME, strArray[0]);
        conv.put(ALARM_REPEAT, strArray[1]);
        conv.put(ALARM_ISOPEN, strArray[2]);
        conv.put(ALARM_CALENDER_TIME, strArray[3]);
        return db.insert(ALARM_COLOCK_TABLE, null, conv);
    }

    public Cursor selectAlarmColock(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ALARM_COLOCK_TABLE, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor getAlarmColock(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String where = ALARM_ID+"=?";
        String[] whereValues = {id};
        Cursor cursor = db.query(ALARM_COLOCK_TABLE, null, where, whereValues, null, null, null);
        return cursor;
    }

    public void deleteAlarmColock(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ALARM_ID+"=?";
        String[] whereValues = {id};
        db.delete(ALARM_COLOCK_TABLE, where, whereValues);
    }

    public int updateAlarmColock(String id,String[] strArray){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ALARM_ID+"=?";
        String[] whereValues = {id};
        ContentValues cv = new ContentValues();
        cv.put(ALARM_TIME, strArray[0]);
        cv.put(ALARM_REPEAT, strArray[1]);
        cv.put(ALARM_ISOPEN, strArray[2]);
        cv.put(ALARM_CALENDER_TIME, strArray[3]);
        Log.v("wangxianming", "cv : "+cv.get("ALARM_TIME")+cv.get("ALARM_REPEAT")+cv.get("ALARM_ISOPEN")+cv.get("ALARM_KIND"));
        return db.update(ALARM_COLOCK_TABLE, cv, where, whereValues);
    }

    //20181022為了測試修改的refreshHelper()而寫的方法，刪除ALARM_COLOCK_TABLE後再呼叫onCreate方法創立一個
    public void DropAndCreatNew() {
            SQLiteDatabase db = this.getWritableDatabase();
        String sql = "drop table " + ALARM_COLOCK_TABLE;
        db.execSQL(sql);
        onCreate(db);

    }

    //20181231測試RecyclerView
    public long inserttest () {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conv = new ContentValues();
        conv.put(ALARM_TIME,"10:00");
        conv.put(ALARM_REPEAT,"Nothing");
        conv.put(ALARM_ISOPEN,"開");
        conv.put(ALARM_CALENDER_TIME,"512121");
        return db.insert(ALARM_COLOCK_TABLE,null,conv);
    }

    //20181231測試修改後使用notify方法能否成功
    public int updatetime(String id,String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conv = new ContentValues();
        String where = ALARM_ID + "=?";
        String[] whereValues = {id};
        conv.put(ALARM_TIME,time);
        return db.update(ALARM_COLOCK_TABLE,conv,where,whereValues);
    }
}


