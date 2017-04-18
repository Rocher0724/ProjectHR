package haru.com.hr.domain;

import java.util.ArrayList;
import java.util.List;

import haru.com.hr.RealData.Results;


/**
 * Created by myPC on 2017-03-24.
 */

public class DataStoreTemp {
    private static DataStoreTemp instance = null;
    private DataStoreTemp() { datas = new ArrayList<>(); }
    public static DataStoreTemp getInstance () {
        if(instance == null) {
            instance = new DataStoreTemp();
        }
        return instance;
    }

    private List<Results> datas;

    public List<PostingData> getDatas() {
        return datas;
    }

    public void setDatas(List<PostingData> datas) {
        this.datas.clear();
        for(PostingData postingData : datas) {
            this.datas.add(postingData);
        }
        this.datas = datas;
    }

    public void addData(PostingData postingData) {
        if( datas.size() > 100 ) {
            // todo 가지고있을수 있는 데이터는 100개정도. 이후에는 10개씩 버리고 10개씩 받아오는식으로 해야할것같다.
        }
        this.datas.add(postingData);
    }
}
