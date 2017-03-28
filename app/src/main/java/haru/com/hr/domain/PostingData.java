package haru.com.hr.domain;

import android.net.Uri;

/**
 * 3/27 : 지정된 데이터는 제목, 컨텐츠, 이미지(사진), 감정표현(사진) 이다.
 */

public class PostingData {
    String _id;
    String title;
    String content;
    Uri imageUrl;
    Uri emotionUrl;

    public String get_id() {
        return _id;
    }

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

    public Uri getEmotionUrl() {
        return emotionUrl;
    }

    public void setEmotionUrl(Uri emotionUrl) {
        this.emotionUrl = emotionUrl;
    }
}
