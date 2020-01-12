package com.juniperuser.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.juniperuser.R;

import java.util.Map;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification().getTitle().equals("Cancel"))
        {
            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable(){

                @Override
                public void run() {
                    Toast.makeText(MyFirebaseMessaging.this,""+remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(remoteMessage.getNotification().getTitle().equals("Arrived"))
        {
            showArrivedNotifiastion(remoteMessage.getNotification().getBody());
        }
    }

    private void showArrivedNotifiastion(String body) {
        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),
                0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Arrived")
                .setContentText(body)
                .setContentIntent(contentIntent);
        NotificationManager manager=(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());
    }


}
