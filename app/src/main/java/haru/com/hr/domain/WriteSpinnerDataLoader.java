package haru.com.hr.domain;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.R;

/**
 * Created by myPC on 2017-04-03.
 */

public class WriteSpinnerDataLoader {
    private static final String TAG = "WriteSpinnerDataLoader";
    List<WriteSpinnerData> datas;

    public WriteSpinnerDataLoader() {
        datas = new ArrayList<>();
        dataloader();
    }

//    private void dataloader() {
//        WriteSpinnerData writeSpinnerData = new WriteSpinnerData();
//        writeSpinnerData.setEmotionText("행복해요");
//        writeSpinnerData.setImgInDrawable(R.drawable.emotion_happy_white);
//        datas.add(writeSpinnerData);
//    }



    private void dataloader() {
        datas.add(new WriteSpinnerData(R.drawable.emotion_inlove_white, "행복해요"));
        datas.add(new WriteSpinnerData(R.drawable.emotion_sad_white, "슬퍼요"));
        datas.add(new WriteSpinnerData(R.drawable.emotion_angry_white, "화가나요"));
        datas.add(new WriteSpinnerData(R.drawable.emotion_soso_white6, "그저그래요"));
        datas.add(new WriteSpinnerData(R.drawable.emotion_happy_white, "행복해요"));
    }


    public List<WriteSpinnerData> getDatas() {
        return datas;
    }
}
