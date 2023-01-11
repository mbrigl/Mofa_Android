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
 * TODO: Improvements for later versions
 */
public class NotificationService {
	private Context mContext;
	private int NOTIFICATION_ID = 1;
	private int NOTIFICATION_ID_F=2;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private PendingIntent mContentIntent = null;
	private CharSequence mContentTitle;
	private Boolean mShowDetails;
	public NotificationService(Context context, Boolean showDetails)
		    {
		        mContext = context;
		        mShowDetails = showDetails; //variable to check if we launch activity to show details
		    }
	
	@SuppressWarnings("deprecation")
	public void createNotification(int notIcon, CharSequence aText, String fullText) {
	   //get the notification manager
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		Intent notificationIntent = new Intent();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
			mContentIntent = PendingIntent.getActivity
					(mContext, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
		}
		else
		{
			mContentIntent = PendingIntent.getActivity
					(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		}
		//mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
		int icon = notIcon;
		CharSequence tickerText = aText; //Initial text that appears in the status bar
		long when = System.currentTimeMillis();
		mNotification = builder.setContentIntent(mContentIntent)
				.setSmallIcon(icon).setTicker(tickerText).setWhen(when)
				.setAutoCancel(true).setContentTitle(fullText)
				.setContentText(tickerText).build();
	  //create the notification

	     // mNotification = new Notification(icon, tickerText, when);
	      //create the content which is shown in the notification pulldown
	    //  mContentTitle = fullText; //Full title of the notification in the pull down
	     // CharSequence contentText = "0% complete"; //Text of the notification in the pull down

     //add the additional content and intent to the notification
	     // mNotification.setLatestEventInfo(mContext, mContentTitle, "", mContentIntent);
     //make this notification appear in the 'Ongoing events' section
	    //  mNotification.flags = Notification.FLAG_ONGOING_EVENT;
	      
	  //show the notification
	      mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}
	
	    /**
		     * Receives progress updates from the background task and updates the status bar notification appropriately
		     * @param percentageComplete
		     */
	
	    @SuppressWarnings("deprecation")
		public void progressUpdate(int percentageComplete) {
	
	        //build up the new status message
	
	      //  CharSequence contentText = percentageComplete + "% complete";
		        //publish it to the status bar
		      //  mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

		       // mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		    }
	  /**
  * called when the background task is complete, this removes the notification from the status bar.
	   * We could also use this to add a new ‘task complete’ notification
		     */
		    @SuppressWarnings("deprecation")
			public void completed(int notIcon, CharSequence aText, String fullText)    {
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
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
					mContentIntent = PendingIntent.getActivity
							(mContext, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
				}
				else
				{
					mContentIntent = PendingIntent.getActivity
							(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
				}
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
		    //not used for the moment
		    @SuppressWarnings("deprecation")
			public void completedWithDetails(int notIcon, CharSequence aText, String fullText, String shortText)    {
		        //remove the notification from the status bar
		        Intent notificationIntent;
		    	mNotificationManager.cancel(NOTIFICATION_ID);
		        int icon = notIcon;
			    CharSequence tickerText = aText;
			    long when = System.currentTimeMillis();
			    mNotification = new Notification(icon, tickerText, when);
			    mContentTitle = shortText;
			    notificationIntent = new Intent(mContext, DetailsDialog.class);
				notificationIntent.putExtra("DATA", fullText);
			    
			    
			    mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
			   // mNotification.setLatestEventInfo(mContext, mContentTitle, "", mContentIntent);
			    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			    mNotificationManager.notify(NOTIFICATION_ID_F, mNotification);
			    
		    }


}
