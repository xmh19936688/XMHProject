package com.example.bmobexample.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.bmobexample.R;

import cn.bmob.push.PushConstants;

public class MyMessageReceiver extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 获取推送消息
        String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
        Log.i("BmobClient", "收到的推送消息：" + message);
        Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show();
        // 发送通知
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Notification n = new Notification();
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        Notification.Builder builder = new Notification.Builder(context);
        Notification n = builder
                .setContentTitle("消息")
                .setContentText(message)
                .setContentIntent(pi)
                .build();
        n.icon = R.drawable.ic_launcher;
        n.tickerText = "BmobExample收到消息推送";
        n.when = System.currentTimeMillis();
        //n.flags=Notification.FLAG_ONGOING_EVENT;
        n.defaults |= Notification.DEFAULT_SOUND;
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, n);
    }

}
