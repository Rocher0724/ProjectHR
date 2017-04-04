package haru.com.hr.domain;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.R;

/**
 * Created by myPC on 2017-04-03.
 */

public class WriteSpinnerDataLoader {
    private static final String TAG = "WriteSpinnerDataLoader";
    List<EmotionSpinnerData> datas;

    public WriteSpinnerDataLoader() {
        datas = new ArrayList<>();
        dataloader();
    }

//    private void dataloader() {
//        EmotionSpinnerData writeSpinnerData = new EmotionSpinnerData();
//        writeSpinnerData.setEmotionText("행복해요");
//        writeSpinnerData.setImgInDrawable(R.drawable.emotion_soso_white);
//        datas.add(writeSpinnerData);
//    }



    private void dataloader() {
        datas.add(new EmotionSpinnerData(R.drawable.emotion_inlove_white, "행복해요"));
        datas.add(new EmotionSpinnerData(R.drawable.emotion_soso_white, "그저그래요"));
        datas.add(new EmotionSpinnerData(R.drawable.emotion_zzaing_white6, "짜증나요"));
        datas.add(new EmotionSpinnerData(R.drawable.emotion_sad_white, "슬퍼요"));
        datas.add(new EmotionSpinnerData(R.drawable.emotion_angry_white, "화가나요"));
    }


    public List<EmotionSpinnerData> getDatas() {
        return datas;
    }
}