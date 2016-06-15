/**
 * CarRental
 *
 * This adapter handles a list of branches
 *
 * It also includes a text-based filter, allowing the user to filter branch results with a text based search
 */

package com.vehiclerental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.vehiclerental.R;
import com.vehiclerental.contracts.BranchContract;

import java.util.ArrayList;
import java.util.List;

public class BranchListAdapter extends BaseAdapter implements Filterable {

    private List<BranchContract> originalData = null;
    private List<BranchContract> filteredData = null;
    private LayoutInflater layoutInflater;

    public BranchListAdapter(Context context, List<BranchContract> items) {
        layoutInflater = LayoutInflater.from(context);
        originalData = items;
        filteredData = items;
    }

    public void setBranches(List<BranchContract> branches) {
        this.originalData = branches;
        this.filteredData = branches;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public BranchContract getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = layoutInflater.inflate(R.layout.branch_list_item, null);
        }

        BranchContract b = getItem(position);

        if (b != null) {
            Button button = (Button) v.findViewById(R.id.branch_list_button);

            if (button != null) {
                button.setText(b.name);
            }
        }

        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                filteredData = (ArrayList<BranchContract>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();
                ArrayList<BranchContract> resultList = new ArrayList<>(getCount());

                String currentBranchName;

                for (int i = 0; i < originalData.size(); i++) {
                    currentBranchName = originalData.get(i).name;
                    if (currentBranchName.toLowerCase().contains(filterPattern)) {
                        resultList.add(originalData.get(i));
                    }
                }

                results.values = resultList;
                results.count = resultList.size();

                return results;
            }
        };
    }
}
