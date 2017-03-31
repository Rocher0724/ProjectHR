package haru.com.hr.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import haru.com.hr.R;
import haru.com.hr.activity.LoginActivity;

/**
 * Created by myPC on 2017-03-30.
 */

public class AnimationUtil {
    private static final String TAG = "AnimationUtil";
    private Animation loginActivityTextAnim = null;
    private Animation loginActivityMainLogoAnim = null;
    private static Animation mainActivityButtomSpaceBlur = null;

    Context context;

    public AnimationUtil(Context context, String activity) {
        this.context = context;

        if(activity.equals("LoginActivity")){
            Log.e(TAG,"스위치문 들어왔다.");
            loginActivityAnimation();
        }

    }

    private void anyActivityAnimation() {
    }

    private void loginActivityAnimation () {
        if (loginActivityTextAnim == null) {
            Log.e(TAG,"loginActivityAnimation 작업");

            loginActivityTextAnim = new AlphaAnimation(0, 1);
            loginActivityTextAnim.setDuration(1000);
            loginActivityMainLogoAnim = AnimationUtils.loadAnimation
                    (context,
                            R.anim.login_logo_anim);   // 애니메이션 설정 파일
        }

    }
    public static Animation mainActivityAnimation() {
        mainActivityButtomSpaceBlur = new AlphaAnimation(0, 1);
        mainActivityButtomSpaceBlur.setDuration(500);

        return mainActivityButtomSpaceBlur;
    }

    public Animation getLoginActivityTextAnim() {
        return loginActivityTextAnim;
    }
    public Animation getLoginActivityMainLogoAnim() {
        return loginActivityMainLogoAnim;
    }

}
