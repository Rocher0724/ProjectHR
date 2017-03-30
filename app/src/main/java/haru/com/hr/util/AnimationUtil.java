package haru.com.hr.util;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import haru.com.hr.R;
import haru.com.hr.activity.LoginActivity;

/**
 * Created by myPC on 2017-03-30.
 */

public class AnimationUtil {
    private Animation loginActivityTextAnim = null;
    private Animation loginActivityMainLogoAnim = null;

    Context context;

    public AnimationUtil(Context context) {
        this.context = context;
        loginActivityAnimation();
    }
    public void loginActivityAnimation () {
        if (loginActivityTextAnim == null) {
            loginActivityTextAnim = new AlphaAnimation(0, 1);
            loginActivityTextAnim.setDuration(1000);
            loginActivityMainLogoAnim = AnimationUtils.loadAnimation
                    (context,
                            R.anim.login_logo_anim);   // 애니메이션 설정 파일
        }
    }


    public Animation getLoginActivityTextAnim() {
        return loginActivityTextAnim;
    }
    public Animation getLoginActivityMainLogoAnim() {
        return loginActivityMainLogoAnim;
    }

}
