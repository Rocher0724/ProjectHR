package haru.com.hr.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by myPC on 2017-04-14.
 */

public class Token {
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

}
