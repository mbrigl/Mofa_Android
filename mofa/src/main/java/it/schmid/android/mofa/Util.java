package it.schmid.android.mofa;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by schmida on 02.09.14.
 */
public final class Util {
    public static String getJSONString(JSONObject jsonObject, String key){
        String data = null;
        try {
            data = jsonObject.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
    public static int getJSONInt(JSONObject jsonObject, String key) {
        int data;
        try {
            data = jsonObject.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
    public static double getJSONDouble(JSONObject jsonObject, String key){
        double data = 0;
        try {
            data = jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
