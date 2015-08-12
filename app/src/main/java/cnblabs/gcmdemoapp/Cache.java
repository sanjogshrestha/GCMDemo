package cnblabs.gcmdemoapp;


        import android.content.Context;
        import android.content.SharedPreferences;
        import android.preference.PreferenceActivity;
        import android.util.Base64;
        import android.util.Log;

        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.ObjectInputStream;
        import java.io.ObjectOutputStream;
        import java.util.HashMap;
        import java.util.zip.GZIPInputStream;
        import java.util.zip.GZIPOutputStream;

/**
 * Created by Sanjog Shrestha on 2/12/2015.
 */
public class Cache {

    public static final int CACHE_LOACATION_MEMORY = 0;
    public static final int CACHE_LOACATION_DISK = 1;


    private static final String CACHE_PREFERENCE = "application_cache";

    public static HashMap<String, Object> mMemoryCache = new HashMap<String, Object>();


    /**
     * save the data into the cache with the specified key and in the specified memory location
     * @param key key for the objective, declare in this class itself
     * @param pContext app context
     * @param dataToCache data to save
     * @param cacheLocation location for the data to save
     */
    public static void putData(String key,Context pContext,Object dataToCache, int cacheLocation) {
        switch (cacheLocation) {
            case CACHE_LOACATION_MEMORY:
                saveDataToMemory(key, pContext, dataToCache);
                break;
            case CACHE_LOACATION_DISK:
                saveDataToDisk(key,pContext,dataToCache);
                break;

            default:
                break;
        }
    }

    /**
     * retrive the the data previously saved in the cache
     * @param key key for the data
     * @param pContext app context
     * @return data object, as it was saved, null if it doesn't exists
     */
    public static Object getData(String key, Context pContext) {
        Object lreturnData = null;
        SharedPreferences prefs = pContext.getSharedPreferences(CACHE_PREFERENCE, PreferenceActivity.MODE_MULTI_PROCESS);
        String data = prefs.getString(key, null);
        if(data != null){
            try {
                lreturnData = deserializeObjectFromString(data);
            } catch (Exception e) {
                Log.e("App Cache", "failed to get the data from cache for:" + key);
                e.printStackTrace();
            }
        }

        return lreturnData;
    }

    /**
     * saves the data into the disk , which is a permanent data
     * @param key
     * @param pContext
     * @param dataToCache
     */
    public static void saveDataToDisk(String key,Context pContext, Object dataToCache) {
        SharedPreferences prefs = pContext.getSharedPreferences(CACHE_PREFERENCE, PreferenceActivity.MODE_MULTI_PROCESS);
        SharedPreferences.Editor lEditor = prefs.edit();
        try {
            lEditor.putString(key, serializeObjectToString(dataToCache));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lEditor.commit();
    }

    /**
     * saves the data into memory, which will be wiped out when the user exists the app
     * @param key
     * @param pContext
     * @param dataToCache
     */
    public static void saveDataToMemory(String key,Context pContext, Object dataToCache) {
        mMemoryCache.put(key, dataToCache);
    }

    /**
     * get the data from memory cache
     * @param key
     * @return object stored with the specified key, or NULL
     */
    public static Object getDataFromMemory(String key) {
        return mMemoryCache.get(key);
    }

    public static String serializeObjectToString(Object object) throws Exception  {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
        objectOut.writeObject(object);
        objectOut.close();
        byte[] bytes = baos.toByteArray();
        String objectString = new String(Base64.encode(bytes, Base64.DEFAULT));

        return objectString;
    }

    public static Object deserializeObjectFromString(String objectString) throws Exception  {

        byte[] bytes = Base64.decode(objectString, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
        Object lObject = objectIn.readObject();
        objectIn.close();

        return lObject;
    }
}

