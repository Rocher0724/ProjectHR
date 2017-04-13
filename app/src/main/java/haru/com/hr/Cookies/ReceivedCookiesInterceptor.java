package haru.com.hr.Cookies;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by myPC on 2017-04-12.
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    private DalgonaSharedpreferences mDsp;

    public ReceivedCookiesInterceptor(Context context){
        mDsp = DalgonaSharedpreferences.getInstance(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            // for each 부분을 축약하면 addAll 이 된다.
            cookies.addAll(originalResponse.headers("Set-Cookie"));
//            for (String header : originalResponse.headers("Set-Cookie")) {
//                cookies.add(header);
//            }

            mDsp.putHashSet(DalgonaSharedpreferences.KEY_COOKIE, cookies);

        }

        return originalResponse;
    }
}
