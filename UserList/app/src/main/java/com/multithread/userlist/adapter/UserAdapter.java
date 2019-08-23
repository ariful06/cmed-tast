package com.multithread.userlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.multithread.userlist.R;
import com.multithread.userlist.model.User;
import com.multithread.userlist.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private List<User> userList;
    private Context context;
    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public UserAdapter(Context context, List<User> list) {
        this.userList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User userInfo = userList.get(position);
        holder.tvUserName.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
        holder.tvUserMobileNumber.setText(userInfo.getPhones().getMobile());
        if (userInfo.getGender().equals(Constants.FEMALE)) {
            Glide.with(context).load(Constants.IMAGE_URL_PREFIX_WEMEN + userInfo.getPhoto() + ".jpg").into(holder.profileImage);
        } else if (userInfo.getGender().equals(Constants.MALE)) {
            Glide.with(context).load(Constants.IMAGE_URL_PREFIX_MEN + userInfo.getPhoto() + ".jpg").into(holder.profileImage);
        }
        holder.itemView.setOnClickListener(view -> itemClickListener.onCallListener(position, userList.get(position)));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

     class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image)
        CircleImageView profileImage;

        @BindView(R.id.user_name)
        TextView tvUserName;

        @BindView(R.id.user_mobile_number)
        TextView tvUserMobileNumber;
         UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onCallListener(int position, User user);
    }
}
