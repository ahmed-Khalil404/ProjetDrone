package tn.esprit.lssafe;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotification extends FirebaseMessagingService {
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message){
        String title =message.getNotification().getTitle();
        String text =message.getNotification().getBody();
        final String CHANEL_ID ="fast_Notification";
        NotificationChannel channel =new NotificationChannel(
                CHANEL_ID,
                "FAST_NOTIFICATION",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification =new Notification.Builder(this,CHANEL_ID)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.baseline_warning_24)
                .setContentText(text);
        NotificationManagerCompat.from(this).notify(1,notification.build());
        super.onMessageReceived(message);

    }
}


