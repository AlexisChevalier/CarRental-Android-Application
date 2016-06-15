/**
 * CarRental
 *
 * This activity asks for the user to choose a branch in order to use the application
 * A list of branch is fetched from the server and displayed with a text-based filter
 *
 * Also, the user localisation is used in order to propose the nearest branch to the localisation
 */

package com.vehiclerental.activities.Guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.vehiclerental.R;
import com.vehiclerental.activities.Staff.BranchBookingsActivity;
import com.vehiclerental.activities.Staff.ChooseUserForBookingActivity;
import com.vehiclerental.activities.User.UserBookingsActivity;
import com.vehiclerental.adapters.BranchListAdapter;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.LocationUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class ChooseBranchActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.branch_list)
    protected ListView branchListView;
    @Bind(R.id.nearest_branch_button)
    protected Button nearestBranchButton;
    @Bind(R.id.search_branch_field)
    protected EditText search_branch_field;
    @Bind(R.id.nearest_branch_label)
    protected TextView nearestBranchLabel;

    //private members
    private BranchListAdapter branchListAdapter;
    private BranchContract nearestBranch;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_branch);

        ButterKnife.bind(this);

        branchListView.setDivider(null);
        branchListView.setDividerHeight(0);

        //Geolocation
        LocationGooglePlayServicesProvider provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        List<BranchContract> branches = new ArrayList<>();

        branchListAdapter = new BranchListAdapter(this, branches);

        branchListView.setAdapter(branchListAdapter);

        branchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BranchContract contract = branchListAdapter.getItem(position);
                PreferencesManager.setCurrentBranch(ChooseBranchActivity.this, contract);

                //Opening app
                Intent intent;
                if (PreferencesManager.isLoggedIn(ChooseBranchActivity.this)) {
                    if (PreferencesManager.isStaffUser(ChooseBranchActivity.this)) {
                        intent = new Intent(getBaseContext(), BranchBookingsActivity.class);
                    } else {
                        intent = new Intent(getBaseContext(), UserBookingsActivity.class);
                    }
                } else {
                    intent = new Intent(getBaseContext(), SearchAvailableVehicleActivity.class);
                }

                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        nearestBranchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.setCurrentBranch(ChooseBranchActivity.this, nearestBranch);

                //Opening app
                Intent intent;
                if (PreferencesManager.isLoggedIn(ChooseBranchActivity.this)) {
                    if (PreferencesManager.isStaffUser(ChooseBranchActivity.this)) {
                        intent = new Intent(getBaseContext(), BranchBookingsActivity.class);
                    } else {
                        intent = new Intent(getBaseContext(), UserBookingsActivity.class);
                    }
                } else {
                    intent = new Intent(getBaseContext(), SearchAvailableVehicleActivity.class);
                }

                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        new GetBranchesTask().start();

        search_branch_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChooseBranchActivity.this.branchListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmartLocation.with(ChooseBranchActivity.this).location().stop();
    }

    //############################## TASKS #############################################################

    private class GetBranchesTask extends AsyncTask<Void, Void, List<BranchContract>> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ChooseBranchActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<BranchContract> doInBackground(Void... params) {
            try {
                return apiClient.getBranches();
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<BranchContract> result) {
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(ChooseBranchActivity.this, apiException);
                return;
            }
            branchListAdapter.setBranches(result);

            //Store branches for other usages
            StaticDataUtils.setBranches(result);

            SmartLocation.with(ChooseBranchActivity.this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        nearestBranch = LocationUtils.getNearestBranch(location, result);

                        if (nearestBranch == null) {
                            nearestBranchLabel.setText(R.string.cant_find_position);
                        } else {
                            nearestBranchButton.setText(nearestBranch.name);
                            nearestBranchLabel.setText(R.string.nearest_branch_label);
                            nearestBranchButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<BranchContract>> start(){
            return this.execute();
        }
    }

}
