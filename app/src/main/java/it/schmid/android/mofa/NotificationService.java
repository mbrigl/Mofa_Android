package it.schmid.android.mofa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/**
 * @author schmida
 * Class for creating notification for the download of data
 * TODO: Improvements for later versions
 */
public class NotificationService {
    private final Context mContext;
    private final int NOTIFICATION_ID = 1;
    private final int NOTIFICATION_ID_F = 2;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent = null;
    private CharSequence mContentTitle;
    private final Boolean mShowDetails;

    public NotificationService(Context context, Boolean showDetails) {
        mContext = context;
        mShowDetails = showDetails; //variable to check if we launch activity to show details
    }

    @SuppressWarnings("deprecation")
    public void createNotification(int notIcon, CharSequence aText, String fullText) {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity
                (mContext, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        //mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        int icon = notIcon;
        CharSequence tickerText = aText; //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = builder.setContentIntent(mContentIntent)
                .setSmallIcon(icon).setTicker(tickerText).setWhen(when)
                .setAutoCancel(true).setContentTitle(fullText)
                .setContentText(tickerText).build();

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    @SuppressWarnings("deprecation")
    public void completed(int notIcon, CharSequence aText, String fullText) {
        //remove the notification from the status bar
        Intent notificationIntent;
        mNotificationManager.cancel(NOTIFICATION_ID);
        int icon = notIcon;
        CharSequence tickerText = aText;
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);
        mContentTitle = fullText;
        notificationIntent = new Intent();
        mContentIntent = null;
        mContentIntent = PendingIntent.getActivity
                (mContext, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        //mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        mNotification = builder.setContentIntent(mContentIntent)
                .setSmallIcon(icon).setTicker(tickerText).setWhen(when)
                .setAutoCancel(true).setContentTitle(fullText)
                .setContentText(tickerText).build();
        //mNotification.setLatestEventInfo(mContext, mContentTitle, "", mContentIntent);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFICATION_ID_F, mNotification);
    }
}
