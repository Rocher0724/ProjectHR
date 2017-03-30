package haru.com.hr.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by myPC on 2017-03-29.
 */


public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private final long secondBackPressedTimeSetting = 2000;

    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + secondBackPressedTimeSetting) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + secondBackPressedTimeSetting) {
            activity.finish();
            toast.cancel(); }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);

        toast.show();
    }
}


