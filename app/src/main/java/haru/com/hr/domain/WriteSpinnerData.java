package haru.com.hr.domain;

import com.bumptech.glide.Glide;

import haru.com.hr.R;
import haru.com.hr.adapter.WriteSpinnerAdapter;

/**
 * Created by myPC on 2017-04-03.
 */

public class WriteSpinnerData {
    private int imgInDrawable;
//    = R.drawable.emotion_happy_white;
    private String emotionText;

    public WriteSpinnerData() {

    }

    public WriteSpinnerData(int imgInDrawable, String emotionText){
        this.imgInDrawable = imgInDrawable;
        this.emotionText = emotionText;
    }

    public int getImgInDrawable() {
        return imgInDrawable;
    }

    public void setImgInDrawable(int imgInDrawable) {
        this.imgInDrawable = imgInDrawable;
    }

    public String getEmotionText() {
        return emotionText;
    }

    public void setEmotionText(String emotionText) {
        this.emotionText = emotionText;
    }
}
