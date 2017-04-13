package haru.com.hr.Cookies;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by myPC on 2017-04-12.
 */

public class AddCookiesInterceptor implements Interceptor{

    private DalgonaSharedpreferences mDsp;

    public AddCookiesInterceptor(Context context){
        mDsp = DalgonaSharedpreferences.getInstance(context);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) mDsp.getHashSet(DalgonaSharedpreferences.KEY_COOKIE, new HashSet<String>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.e("OkHttp", "Adding Header: " + cookie);
        }

        return chain.proceed(builder.build());
    }
}
