package com.multithread.userlist.network;


import com.multithread.userlist.model.Users;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("intrvw/users.json")
    Observable<Users> getAllUserList();
}

