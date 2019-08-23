package com.multithread.userlist.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.multithread.userlist.util.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    ;

    static Retrofit retrofit;
    public static Retrofit getApiClient(){
        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit== null){
            retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }

}
