package haru.com.hr;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by myPC on 2017-03-20.
 */

public class Remote {

    public static String postJson(String siteUrl, String data) {
        String result = "";

        if(!siteUrl.startsWith("http")) {
            siteUrl = "http://" + siteUrl;
        }

        try {
            URL url = new URL(siteUrl);
            url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //-----------------------------POST 데이터 전송처리----------------------------
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();

            os.write(data.getBytes());

            os.flush();
            os.close();

            //-----------------------------------------------------------------------------

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 4.1 서버 연결로부터 스트림을 얻고 버퍼래퍼로 감싼다.
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // 4.2 반복문은 돌면서 버퍼의 데이터를 읽어온다.
                StringBuilder builderResult = new StringBuilder();
                String lineOfData = "";
                while ((lineOfData = br.readLine()) != null) {
                    builderResult.append(lineOfData);
                }
                connection.disconnect();
                return builderResult.toString();

            } else {
                Log.e("HTTOConnection", "Error Code = " + responseCode);
            }

        } catch (Exception e) {
            result = "Exception : " + e.getMessage();
        }


        return result;
    }
}
