package com.singlesoft.repaircon.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singlesoft.repaircon.R;
import com.singlesoft.repaircon.activitys.customerPage;
import com.singlesoft.repaircon.activitys.userPage;
import com.singlesoft.repaircon.models.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder>{

    private final List<User> userList;

    public UserAdapter(List<User> userList) {
        Collections.reverse(userList);
        this.userList = userList;
        sortCustomersByName();
}
    private void sortCustomersByName() {
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return user.getName().compareToIgnoreCase(t1.getName());
            }
        });
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getName());
        holder.number.setText(String.valueOf(user.getNumServices()));

        if(user.getUserType().equals("ADMIN")){
            holder.type.setText(R.string.from_admin);
        } else if(user.getUserType().equals("USER")) {
            holder.type.setText(R.string.from_user);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), userPage.class);
                intent.putExtra("user", user);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}