package io.github.brijoe.example.network;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.github.brijoe.DHInterceptor;
import io.github.brijoe.example.parse.HttpUrlHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {


    private static final String TAG = "ServiceFactory";

    private static final String BASE_URL = "http://navjacinth9.000webhostapp.com/json/";

    private static  BusService busService=create(BusService.class);

    public static BusService getBusService(){
       return busService;
    }

    private static <T> T create(Class<T> clazz) {

        Interceptor busInterceptor=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
               Request request= chain.request();
                // write your business
                Log.e(TAG, "intercept: "+request.url().toString() );
                return chain.proceed(request);

            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().clear();
        builder.writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                //your own interceptor
                .addInterceptor(busInterceptor)
                //add DHInterceptor
                .addInterceptor(new DHInterceptor())
                .readTimeout(30, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrlHelper.create(BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(clazz);
    }



}