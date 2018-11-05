package com.ngoucoorp.cameroonguide.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.activities.MainActivity;
import com.ngoucoorp.cameroonguide.fragments.NotificationFragment;
import com.ngoucoorp.cameroonguide.models.PNewsData;
import com.ngoucoorp.cameroonguide.R;

import java.lang.reflect.Type;

import static android.app.PendingIntent.getActivity;


/**
 * Created by Panacea-Soft on 8/11/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        if(!remoteMessage.getData().get("newsData").isEmpty()){
            Gson gson = new Gson();
            Type newsType = new TypeToken<PNewsData>() {
            }.getType();
            GlobalData.notifData = gson.fromJson(remoteMessage.getData().get("newsData"), newsType);
            Utils.psLog("apres larrivee");
            Utils.psLog(GlobalData.notifData.toString());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("notif_data", remoteMessage.getData().get("newsData"));
            editor.apply();
        }

        /* Important : Please don't change this "message" because if you change this, need to update at PHP.  */
        showNotification(remoteMessage.getData().get("message"));


    }

    private void showNotification(String message) {

        if(Utils.activity != null){
            if(Utils.activity.fragment != null) {
                if (Utils.activity.fragment instanceof NotificationFragment) {
                    Utils.activity.savePushMessage(message);
                    Utils.activity.refreshNotification();
                }
            }
        }

        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("msg", message);
        String displayMessage = "";

            i.putExtra("show_noti", true);
            displayMessage = "You've received new message.";


        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(GlobalData.notifData.title!=null?GlobalData.notifData.title:getResources().getString(R.string.app_name))
                .setContentText(GlobalData.notifData.description.substring(0, Math.min(GlobalData.notifData.description.length(), 120)) + "...")
                .setSmallIcon(R.drawable.cover)
                .setContentIntent(pendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        builder.setDefaults(defaults);

        // Set autocancel
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
