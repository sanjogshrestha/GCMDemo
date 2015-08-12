package cnblabs.gcmdemoapp;

/*
 * Created by Sanjog Shrestha on 14/03/15.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService
{
	public GcmIntentService()
    {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
    {
		Bundle extras = intent.getExtras();
        System.out.println("extras="+extras);
		String msg =intent.getExtras().getString("message");
        System.out.println("GCM Message="+msg);

        //savePushMessage(msg);
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        System.out.println("GCM="+gcm);

        String messageType = gcm.getMessageType(intent);
        System.out.println("GCM Type="+messageType);
        System.out.println("mesage="+extras.getString("title"));

        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("message"));

        if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				showNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				showNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work.
				for (int i = 0; i < 5; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				// Post notification of received message.
				showNotification(msg);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private void showNotification(String msg)
    {
        Uri uri= Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.honk);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Test") // title for notification
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg) // message for notification
                .setSound(uri)
				.setAutoCancel(true); // clear notification after click

		Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);//Intent.FLAG_ACTIVITY_NEW_TASK
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
}
