package haru.com.hr.Cookies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

/**
 * Created by myPC on 2017-04-12.
 */

public class DalgonaSharedpreferences {

    public static final String KEY_COOKIE = "com.dalgonakit.key.cookie";

    private Context mContext;
    private SharedPreferences pref;

    private static DalgonaSharedpreferences instance = null;

    public static DalgonaSharedpreferences getInstance(Context context){
        if(instance==null){
            instance = new DalgonaSharedpreferences(context);
        }

        return instance;
    }

    public DalgonaSharedpreferences(Context c) {
        mContext = c;
        final String PREF_NAME = c.getPackageName();
        pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }


    public void putHashSet(String key, HashSet<String> set){
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }


    public HashSet<String> getHashSet(String key, HashSet<String> dftValue){
        try {
            return (HashSet<String>)pref.getStringSet(key, dftValue);
        } catch (Exception e) {
            e.printStackTrace();
            return dftValue;
        }
    }
}
