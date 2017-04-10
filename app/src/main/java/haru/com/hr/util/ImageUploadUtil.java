package haru.com.hr.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by myPC on 2017-04-06.
 */

public class ImageUploadUtil {

    public static String uploadPhoto(File file) {
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo", "photo.png", RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

            String url = "http://...";
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (UnknownHostException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
