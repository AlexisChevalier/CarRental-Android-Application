/**
 * CarRental
 *
 * This adapter handles a list of user search results
 */

package com.vehiclerental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vehiclerental.R;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.utils.DateUtils;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private List<UserContract> data = null;
    private LayoutInflater layoutInflater;

    public UserListAdapter(Context context, List<UserContract> items) {
        layoutInflater = LayoutInflater.from(context);
        data = items;
    }

    public void setUsers(List<UserContract> branches) {
        this.data = branches;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public UserContract getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = layoutInflater.inflate(R.layout.user_list_item, null);
        }

        UserContract user = getItem(position);

        if (user != null) {
            /* CREATE VIEW */
            TextView userName = (TextView) v.findViewById(R.id.userName);
            TextView userEmail = (TextView) v.findViewById(R.id.userEmail);

            if (userName != null) {
                userName.setText(user.fullName);
            }
            if (userEmail != null) {
                userEmail.setText(user.emailAddress);
            }
        }

        return v;
    }
}
