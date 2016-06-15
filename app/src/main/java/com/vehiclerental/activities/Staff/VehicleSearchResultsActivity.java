/**
 * CarRental
 *
 * This activity displays the search results of a vehicle search, by clicking on a result, the staff user can update the corresponding vehicle
 */

package com.vehiclerental.activities.Staff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.R;
import com.vehiclerental.adapters.VehicleListAdapter;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VehicleSearchResultsActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.vehicle_list)
    protected ListView vehicleListView;

    //private members
    private VehicleListAdapter vehicleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_search_results);

        Bundle bundle = getIntent().getExtras();

        Type jsonType = new TypeToken<List<VehicleContract>>() {}.getType();
        List<VehicleContract> vehicles = SerializationUtils.deserialize(bundle.getString(SearchVehicleActivity.VEHICLE_SEARCH_RESULTS_LIST_SERIALIZED_KEY), jsonType);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.vehicle_search_results_title));

        vehicleListAdapter = new VehicleListAdapter(this, vehicles);

        vehicleListView.setAdapter(vehicleListAdapter);

        vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VehicleContract selectedBooking = vehicleListAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), EditVehicleActivity.class);
                intent.putExtra(SearchVehicleActivity.SELECTED_VEHICLE_SERIALIZED_KEY, SerializationUtils.serialize(selectedBooking));
                startActivity(intent);
            }
        });

    }
}