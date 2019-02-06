package com.dophin.alarmfragmenttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CallAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String getStr = intent.getExtras().getString("RESULT");
        Log.v("wangxianming","RESULT = "+ getStr);
        Intent alarmIntent = new Intent(context,AlarmAgainSetting.class);
        //Bundle bundleRet = new Bundle();
        //bundleRet.putString("STR_RESULT",getStr);
        //alarmIntent.putExtras(bundleRet);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
