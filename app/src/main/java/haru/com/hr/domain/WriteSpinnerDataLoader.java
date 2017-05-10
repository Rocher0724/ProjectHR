package haru.com.hr.domain;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.R;

/**
 * Created by myPC on 2017-04-03.
 */

public class WriteSpinnerDataLoader {
    private static final String TAG = "WriteSpinnerDataLoader";
    private List<EmotionSpinnerData> datas;

    public WriteSpinnerDataLoader() {
        datas = new ArrayList<>();
        dataloader();
    }


    private void dataloader() {
        datas.add(new EmotionSpinnerData(1, R.drawable.emotion_inlove_white, "행복해요"));
        datas.add(new EmotionSpinnerData(2, R.drawable.emotion_soso_white, "그저그래요"));
        datas.add(new EmotionSpinnerData(3, R.drawable.emotion_zzaing_white6, "짜증나요"));
        datas.add(new EmotionSpinnerData(4, R.drawable.emotion_sad_white, "슬퍼요"));
        datas.add(new EmotionSpinnerData(5, R.drawable.emotion_angry_white, "화가나요"));
    }


    public List<EmotionSpinnerData> getDatas() {
        return datas;
    }
}
