package com.dophin.alarmfragmenttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AlarmAgainSetting extends AppCompatActivity {
    private Ringtone alarmMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultAlarmMediaPlayer();
        if(alarmMedia !=null && !alarmMedia.isPlaying()){
            alarmMedia.play();
        }
        new AlertDialog.Builder(this)
                .setTitle("Here We Go")
                .setMessage("GOGOGO")
                .setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                alarmMedia.stop();
                            }
                        }).setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        alarmMedia.stop();
                    }
                }).show();

    }
    public void defaultAlarmMediaPlayer() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmMedia = RingtoneManager.getRingtone(this,notification);
    }
}
