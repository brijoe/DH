package io.github.brijoe;



import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 *
 * A DH interceptor based on OkHTTP's {@link Interceptor},which is used to record network log.
 *
 * @author Brijoe
 */
public final class DHInterceptor implements Interceptor {

    LogRepository logRepository = new LogRepository(DH.getContext());

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //if disabled,do nothing and return directly.
        if(!DH.getEnabled()){
            return chain.proceed(request);
        }
        long t1 = System.nanoTime();
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        NetworkLog networkLog = new NetworkLog();
        networkLog.setId(getRequestId());
        networkLog.setDate(new Date().getTime());
        networkLog.setDuration((t2 - t1) / 1e6d);
        networkLog.setErrorClientDesc("");
        networkLog.setRequestHeaders(request.headers().toString());
        networkLog.setResponseHeaders(String.valueOf(response.headers()));
        networkLog.setRequestType(request.method());
        networkLog.setResponseCode(String.valueOf(response.code()));
        String body = response.body().string();
        networkLog.setResponseData(body);
        networkLog.setUrl(String.valueOf(request.url()));
        networkLog.setPostData(bodyToString(request));

        MediaType contentType = response.body().contentType();

        //insert this record into db
        logRepository.insert(networkLog);

        return response.newBuilder().body(ResponseBody.create(contentType, body)).build();
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "";
        }
    }
    private static long getRequestId() {
        Calendar calendar = Calendar.getInstance();
        return Long.valueOf(calendar.getTimeInMillis());
    }
}
