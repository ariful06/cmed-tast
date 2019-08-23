package com.multithread.userlist.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.multithread.userlist.model.Users;
import com.multithread.userlist.repository.UserRepo;

public class MainActicityViewModel extends AndroidViewModel {

    private UserRepo userRepo;

    public MainActicityViewModel(@NonNull Application application) {
        super(application);
        userRepo = new UserRepo(application);
    }

    public LiveData<Users> getAllUsers(){
        return userRepo.getMutableLiveUserList();
    }

}
