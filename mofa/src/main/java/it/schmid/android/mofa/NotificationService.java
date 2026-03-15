package it.schmid.android.mofa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/**
 *
 * @author schmida
 * Class for creating notification for the download of data
 */
public class NotificationService {
    private final Context mContext;
    private final int NOTIFICATION_ID = 1;
    private final int NOTIFICATION_ID_F = 2;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent = null;
    private final Boolean mShowDetails;

    public NotificationService(Context context, Boolean showDetails) {
        mContext = context;
        mShowDetails = showDetails;
    }

    public void createNotification(int notIcon, CharSequence aText, String fullText) {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        mNotification = new NotificationCompat.Builder(mContext, MofaApplication.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(mContentIntent)
                .setSmallIcon(notIcon)
                .setTicker(aText)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(fullText)
                .setContentText(aText)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void completed(int notIcon, CharSequence aText, String fullText) {
        mNotificationManager.cancel(NOTIFICATION_ID);
        Intent notificationIntent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        mNotification = new NotificationCompat.Builder(mContext, MofaApplication.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(mContentIntent)
                .setSmallIcon(notIcon)
                .setTicker(aText)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(fullText)
                .setContentText(aText)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID_F, mNotification);
    }

    public void completedWithDetails(int notIcon, CharSequence aText, String fullText, String shortText) {
        mNotificationManager.cancel(NOTIFICATION_ID);
        Intent notificationIntent = new Intent(mContext, DetailsDialog.class);
        notificationIntent.putExtra("DATA", fullText);
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_ONE_SHOT);
        mNotification = new NotificationCompat.Builder(mContext, MofaApplication.NOTIFICATION_CHANNEL_ID)
                .setContentIntent(mContentIntent)
                .setSmallIcon(notIcon)
                .setTicker(aText)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(shortText)
                .setContentText(aText)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID_F, mNotification);
    }
}
