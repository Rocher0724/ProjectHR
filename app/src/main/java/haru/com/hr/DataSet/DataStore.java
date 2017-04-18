package haru.com.hr.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myPC on 2017-04-17.
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

    private List<Results> datas;

    public List<Results> getDatas() {
        return datas;
    }

    public void setDatas(List<Results> datas) {
        this.datas.clear();
        for(Results realData : datas) {
            this.datas.add(realData);
        }
        this.datas = datas;
    }

    public void addData(Results realData) {
        if( datas.size() > 100 ) {
            // todo 가지고있을수 있는 데이터는 100개정도. 이후에는 10개씩 버리고 10개씩 받아오는식으로 해야할것같다.
        }
        this.datas.add(realData);
    }
}