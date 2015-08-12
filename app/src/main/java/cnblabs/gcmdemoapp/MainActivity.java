package cnblabs.gcmdemoapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class MainActivity extends Activity{

    Button btnRegId;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "758501175464";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "registration_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
        regid = getRegistrationId(MainActivity.this);
        if (TextUtils.isEmpty(regid)) {
            new RegisterBackground().execute();
        } else {
            //Constants.printLog("GCM Registration Id is ","User already Registered " + regid);
            System.out.println("GCM Registration Id is" +regid+"User already Registered");
        }

    }

    // GCM stuff
    private String getRegistrationId(Context context) {
        String registrationId = null;
        Object lObject = Cache.getData(PROPERTY_REG_ID, MainActivity.this);
        if (lObject != null) {
            registrationId = (String) lObject;
            if (registrationId.isEmpty()) {

                System.out.println("Gcm Registration"+ "Registration not found.");

                return "";
            }
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        Object lObject1 = Cache.getData(PROPERTY_APP_VERSION,
                MainActivity.this);
        if (lObject1 != null) {
            int registeredVersion = (Integer) lObject1;
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {

                System.out.println("Gcm Registration" + "App version changed.");

                return "";
            }
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);


                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
    public class RegisterBackground extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }
                regid = gcm.register(PROJECT_NUMBER);
                System.out.println("RegistrationId is " + regid);
                msg = "Device registered, registration ID = " + regid;

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }
    }


}