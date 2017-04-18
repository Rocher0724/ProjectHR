package haru.com.hr.domain;

import android.net.Uri;

import haru.com.hr.R;

/**
 * Created by myPC on 2017-03-28.
 */

public class TutorialData {

    private static TutorialData instance;

    private TutorialData() {
    }

    public static TutorialData getInstance() {
        if (instance == null) {
            instance = new TutorialData();
        }
        return instance;
    }

    String _id0 = "-1";
    String title0 = "0";
    String content0 = "0";
    Uri imageUrl0 = Uri.parse("https://img.clipartfest.com/c0cb1105f864119f935b35dcd49be4c0_large-printable-numbers-0-10-0_550-550.jpeg");
    int emotionUrl0 = R.drawable.emotion_soso_white;
    String date0 = "2017. 4. 1. 오후 7:01:27";


    String _id1 = "-1";
    String title1 = "이이이이이이이이이이이이이이이이";
    String content1 = "이이이이이이이이이이이이이이이이이이이이이이이이이이이이이" +
            "이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이" +
            "이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이이" +
            "이이이이이이이이이이이이이이이이이이이이이";
    Uri imageUrl1 = Uri.parse("http://www.okclipart.com/img10/fqwphpddofsuilmgdwdd.jpg");
    int emotionUrl1 = R.drawable.emotion_inlove_white;
    String date1 = "2017. 4. 2. 오후 7:01:27";

    String _id2 = "-1";
    String title2 = "이이이이이이이이이이이이이이이이";
    String content2 = "2";
    Uri imageUrl2 = Uri.parse("https://upload.wikimedia.org/wikipedia/en/thumb/4/4d/TV_2_(Norway)_logo.svg/140px-TV_2_(Norway)_logo.svg.png");
    int emotionUrl2 = R.drawable.emotion_sad_white;
    String date2 = "2017. 4. 3. 오후 7:01:27";

    String _id3 = "-1";
    String title3 = "3";
    String content3 = "3";
    Uri imageUrl3 = Uri.parse("http://img.ifreepic.com/1297/21297_icon.jpg");
    int emotionUrl3 = R.drawable.emotion_zzaing_white6;
    String date3 = "2017. 4. 4. 오후 7:01:27";

    String _id4 = "-1";
    String title4 = "4";
    String content4 = "4";
    Uri imageUrl4 = Uri.parse("http://i.ebayimg.com/images/g/BuwAAOxy-WxTCSn7/s-l300.jpg");
    int emotionUrl4 = R.drawable.emotion_angry_white;
    String date4 = "2017. 4. 5. 오후 7:01:27";

    public int getEmotionUrl0() {
        return emotionUrl0;
    }

    public int getEmotionUrl1() {
        return emotionUrl1;
    }

    public int getEmotionUrl2() {
        return emotionUrl2;
    }

    public int getEmotionUrl3() {
        return emotionUrl3;
    }

    public int getEmotionUrl4() {
        return emotionUrl4;
    }

    public String getDate0() {
        return date0;
    }

    public void setDate0(String date0) {
        this.date0 = date0;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getDate3() {
        return date3;
    }

    public void setDate3(String date3) {
        this.date3 = date3;
    }

    public String getDate4() {
        return date4;
    }

    public void setDate4(String date4) {
        this.date4 = date4;
    }

    public String get_id0() {
        return _id0;
    }

    public void set_id0(String _id0) {
        this._id0 = _id0;
    }

    public String getTitle0() {
        return title0;
    }

    public void setTitle0(String title0) {
        this.title0 = title0;
    }

    public String getContent0() {
        return content0;
    }

    public void setContent0(String content0) {
        this.content0 = content0;
    }

    public Uri getImageUrl0() {
        return imageUrl0;
    }

    public void setImageUrl0(Uri imageUrl0) {
        this.imageUrl0 = imageUrl0;
    }

    public String get_id1() {
        return _id1;
    }

    public void set_id1(String _id1) {
        this._id1 = _id1;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public Uri getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(Uri imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String get_id2() {
        return _id2;
    }

    public void set_id2(String _id2) {
        this._id2 = _id2;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public Uri getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(Uri imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String get_id3() {
        return _id3;
    }

    public void set_id3(String _id3) {
        this._id3 = _id3;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getContent3() {
        return content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }

    public Uri getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(Uri imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String get_id4() {
        return _id4;
    }

    public void set_id4(String _id4) {
        this._id4 = _id4;
    }

    public String getTitle4() {
        return title4;
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }

    public String getContent4() {
        return content4;
    }

    public void setContent4(String content4) {
        this.content4 = content4;
    }

    public Uri getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(Uri imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

}