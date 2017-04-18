package haru.com.hr.domain;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.R;

/**
 * Created by myPC on 2017-04-03.
 */

public class MainMoaSpinnerDataLoader {
    private static final String TAG = "MainMoaSpinnerDataLoader";
    private static MainMoaSpinnerDataLoader instance;
    List<EmotionSpinnerData> datas;


    private MainMoaSpinnerDataLoader() {
        datas = new ArrayList<>();
        dataloader();
    }
    public static MainMoaSpinnerDataLoader getInstance() {
        if(instance == null) {
            instance = new MainMoaSpinnerDataLoader();
        }
        return instance;
    }

    private void dataloader() {
        datas.add(new EmotionSpinnerData(0, R.drawable.emotion_dummy, "전체보기"));
        datas.add(new EmotionSpinnerData(1, R.drawable.emotion_inlove_white, "행복해요"));
        datas.add(new EmotionSpinnerData(2, R.drawable.emotion_soso_white, "그저그래요"));
        datas.add(new EmotionSpinnerData(3, R.drawable.emotion_sad_white, "슬퍼요"));
        datas.add(new EmotionSpinnerData(4, R.drawable.emotion_zzaing_white6, "짜증나요"));
        datas.add(new EmotionSpinnerData(5, R.drawable.emotion_angry_white, "화가나요"));
    }


    public List<EmotionSpinnerData> getDatas() {
        return datas;
    }
}
