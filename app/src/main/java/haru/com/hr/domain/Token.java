package haru.com.hr.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by myPC on 2017-04-14.
 */

public class Token extends AppCompatActivity {
    private String key;

    private static Token instance = null;
    private Token() {

    }
    public static Token getInstance () {
        if(instance == null) {
            instance = new Token();
        }
        return instance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        return token;
    }

    public void setToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }
}
