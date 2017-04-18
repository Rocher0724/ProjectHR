package haru.com.hr.domain;

/**
 * Created by myPC on 2017-04-03.
 */

public class EmotionSpinnerData {
//    private int status_code;
    private int imgInDrawable;
    private String emotionText;

    public EmotionSpinnerData() {

    }

    //todo 눈으로 봤을땐 첫번째 인자가 필요없는것 같아서 지워놨다. 실행시 문제없으면 삭제하기
    public EmotionSpinnerData(int status_code, int imgInDrawable, String emotionText){
//        this.status_code = status_code;
        this.imgInDrawable = imgInDrawable;
        this.emotionText = emotionText;
    }

    public int getImgInDrawable() {
        return imgInDrawable;
    }

//    public int getStatus_code() {
//        return status_code;
//    }

//    public void setStatus_code(int status_code) {
//        this.status_code = status_code;
//    }

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
