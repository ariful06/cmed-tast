package com.multithread.userlist.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.multithread.userlist.R;
import com.multithread.userlist.adapter.UserAdapter;
import com.multithread.userlist.model.User;
import com.multithread.userlist.viewmodel.MainActicityViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    MainActicityViewModel viewModel;
    List<User> userList;

    UserAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(MainActicityViewModel.class);

        if (isNetworkAvailable()) {
            if (isOnline()) {
                progressBar.setIndeterminate(true);
                getUserList();
            }
        }
    }
    private void getUserList() {
        viewModel.getAllUsers().observe(this, users -> {
            userList = users.getUsers();
            prepareRecyclerView(userList);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void prepareRecyclerView(List<User> userList) {
        adapter = new UserAdapter(this,userList);
        adapter.setClickListener(this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }


    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCallListener(int position, User user) {
        Toast.makeText(this, "CLicked "+ position, Toast.LENGTH_SHORT).show();
    }
}
