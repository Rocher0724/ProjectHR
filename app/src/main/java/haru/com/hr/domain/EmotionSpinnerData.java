package haru.com.hr.domain;

/**
 * Created by myPC on 2017-04-03.
 */

public class EmotionSpinnerData {
    private int imgInDrawable;
    private String emotionText;

    public EmotionSpinnerData() {

    }

    public EmotionSpinnerData(int imgInDrawable, String emotionText){
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
