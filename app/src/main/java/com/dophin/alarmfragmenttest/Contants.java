package com.dophin.alarmfragmenttest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Contants {
    public static Map<String,Integer> mapWeek = new HashMap<>();

    Contants() {}

    public static void addMapWeek() {
        mapWeek.put("周一",1);
        mapWeek.put("周二",2);
        mapWeek.put("周三",3);
        mapWeek.put("周四",4);
        mapWeek.put("周五",5);
        mapWeek.put("周六",6);
        mapWeek.put("周日",7);
    }

    public static int getMapWeek(String str) {
        Contants.addMapWeek();
        int dayOfMapWeek = 0;
        if(str != null) {
            dayOfMapWeek = mapWeek.get(str);
        }
        return dayOfMapWeek;
    }

    public static String[] getDatetimeString() {
        Date date = new Date();
        String[] tempStr = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str =format.format(date);
        tempStr[0] = str.substring(0,10);
        tempStr[1] = str.substring(11,str.length());
        return tempStr;
    }


    public static boolean differSetTimeAndNowTime(Long setTime) {
        if (setTime >= getNowTimeMills()){
            return true;
        }else {
            return false;
        }
    }
    public static long getNowTimeMills() {
        return System.currentTimeMillis();
    }

    //檢查資料庫裡的設定時間有沒有現在時間
    public static boolean passOrNot(Long sqlTime) {
        if(getNowTimeMills() >= sqlTime) {
            return true;
        }else {
            return false;
        }
    }
    public static int getdifferday(Long passedtime) {
        Calendar nowtime = Calendar.getInstance();
        Calendar cpassedtime = Calendar.getInstance();
        cpassedtime.setTimeInMillis(passedtime);
        int differday= (int) ( (nowtime.getTimeInMillis() - passedtime) / (24 * 60 * 60 * 1000) ) ;
        cpassedtime.add(Calendar.DAY_OF_WEEK,differday);
        if(nowtime.get(Calendar.DAY_OF_WEEK) != cpassedtime.get(Calendar.DAY_OF_WEEK)){
            differday += 1;
        }
        return differday;

    }

    //周一到周日的回傳直，因為原本的DAY_OF_WEEK星期一是從2開始，而禮拜日是1我們自己轉成從1開始
    public static int getSetDay(String str) {
        int day = 0;
        switch (str){
            case "周一":
                day = 1;
                break;
            case "周二":
                day = 2;
                break;
            case "周三":
                day = 3;
                break;
            case "周四":
                day = 4;
                break;
            case "周五":
                day = 5;
                break;
            case "周六":
                day = 6;
                break;
            case "周日":
                day = 7;
                break;
            default:
                break;
        }
        return day;
    }

    //將資料庫中的repeat_times轉成相應的int 星期日為1 星期一為2 以此類推
    public static int[] getDayOfNum(String str) {
        String[] strs= str.split(",");
        int[] dayOfInt = new int[strs.length];
        for (int i  = 0 ;i <strs.length;i++) {
            dayOfInt[i] = getSetDay(strs[i]);
        }
        return dayOfInt;
    }

    public static int compareNowAndNext(int[] dayOfNum,int dayOfNow) {
        boolean alreadyfindnext = false;
        int differ = 0;
        for(int i = 0;i<dayOfNum.length;i++){
            if(dayOfNum[i] > dayOfNow && !alreadyfindnext){
                differ =  dayOfNum[i] -dayOfNow;
                alreadyfindnext = true;
            }else {
                differ = (7 - dayOfNow) + dayOfNum[0];
                alreadyfindnext = true;
            }
        }
        return differ;
    }

    public static int getmyDayOfWeek(int systemdayOfWeek) {
        int myDayOfWeek = 0;
        switch (systemdayOfWeek) {
            case 2:
                myDayOfWeek = 1;
                break;
            case 3:
                myDayOfWeek = 2;
                break;
            case 4:
                myDayOfWeek = 3;
                break;
            case 5:
                myDayOfWeek = 4;
                break;
            case 6:
                myDayOfWeek = 5;
                break;
            case 7:
                myDayOfWeek = 6;
                break;
            case 1:
                myDayOfWeek = 7;
                break;
            default:
                break;
        }
        return myDayOfWeek;
    }

    public static long getDifferMillis(int differDays) {
        return differDays * 24 * 60 * 60 * 1000;
    }

    public static int compareDayNowToNext(int nowDay,int nextDay) {
        if(nextDay > nowDay) {
            return (nextDay - nowDay);
        }else if(nextDay == nowDay) {
            return 0;
        }else {
            return  (7 - (nowDay - nextDay));
        }
    }

    public static Map<String,Integer> nowWeek = new HashMap<>();

    public static void addNowWeek() {
        nowWeek.put("1",7);
        nowWeek.put("2",6);
        nowWeek.put("3",5);
        nowWeek.put("4",4);
        nowWeek.put("5",3);
        nowWeek.put("6",2);
        nowWeek.put("7",1);
    }

    //取得今天的日期 星期日為1 星期一為2 依此類推
    public static int getNowDayOfWeek() {
        Calendar nowCal = Calendar.getInstance();
        Date nowDate = new Date(System.currentTimeMillis());
        nowCal.setTime(nowDate);
        //int nowNum= nowCal.get(nowCal.DAY_OF_WEEK);
        //String nowNumStr = String.valueOf(nowNum);
        //Contants.addNowWeek();
        //int nowDayOfWeek = 0;
        //if(nowNumStr != null) {
            //nowDayOfWeek = nowWeek.get(String.valueOf(nowNumStr));
        //}
        return nowCal.DAY_OF_WEEK;
    }


    public static int getResultDifferDay(int[] in,int nowDay) {
        int result = 0;
        for(int i = 0;i < in.length;i++) {
            if(in[i] >= nowDay) {
                result = in[i];
                break;
            }

        }
        if(result == 0) {
            result=in[0];
        }
        return result;
    }











}
