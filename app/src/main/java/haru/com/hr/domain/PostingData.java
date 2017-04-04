package haru.com.hr.domain;

import android.net.Uri;

import java.util.Date;

/**
 * 3/27 : 지정된 데이터는 제목, 컨텐츠, 이미지(사진), 감정표현(사진) 이다.
 */

public class PostingData {
    //실제로 쓸 데이터
//    String _id;
//    String title;
//    String content;
//    Date postDate;
//    Uri photoUrl;
//    Uri stateUrl;


    String _id;
    String title;
    String content;
    String nDate;
    Uri imageUrl;
    int emotionUrl;


    public int getEmotionUrl() {
        return emotionUrl;
    }

    public void setEmotionUrl(int emotionUrl) {
        this.emotionUrl = emotionUrl;
    }

    public String getnDate() { return nDate; }
    public void setnDate(String nDate) { this.nDate = nDate; }
    public String get_id() { return _id; }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Uri getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void clear() {
        _id = null;
        title = null;
        content = null;
        nDate = null;
        imageUrl = null;
        emotionUrl = 0;
    }
}
