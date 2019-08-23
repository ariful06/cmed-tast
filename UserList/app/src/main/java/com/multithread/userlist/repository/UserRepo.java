package com.multithread.userlist.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.multithread.userlist.model.User;
import com.multithread.userlist.model.Users;
import com.multithread.userlist.network.ApiClient;
import com.multithread.userlist.network.ApiInterface;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class UserRepo {

    private static final String TAG = "UserRepo";

    private MutableLiveData<Users> mutableLiveData;

    private Users users;
    private List<User> userList;

    private Application application;

    public UserRepo(Application application){
        this.application = application;
    }

    public MutableLiveData<Users> getMutableLiveUserList(){
        mutableLiveData = new MutableLiveData<>();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Observable<Users> usersObservable  = apiInterface.getAllUserList();
        usersObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Users users) {
                        if (users != null){
                            userList = users.getUsers();
                            mutableLiveData.postValue(users);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return mutableLiveData;
    }

}
