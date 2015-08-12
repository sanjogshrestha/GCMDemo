package cnblabs.gcmdemoapp;

/**
 * Created by Sanjog Shrestha on 14/03/15.
 */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that GcmIntentService will handle the intent.
        System.out.println("gcm message received-GcmBroadcastReceiver");
        String message = intent.getExtras().getString("message");
        System.out.println("gcm message received-GcmBroadcastReceiver ="+message);
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
