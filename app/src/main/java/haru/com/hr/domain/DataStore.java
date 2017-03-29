package haru.com.hr.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by myPC on 2017-03-24.
 */

public class DataStore {
    private static DataStore instance = null;
    private DataStore() { datas = new ArrayList<>(); }
    public static DataStore getInstance () {
        if(instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private List<PostingData> datas;

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

        }
        this.datas.add(postingData);
    }
}