/**
 * CarRental
 *
 * This activity displays the result of the search for available vehicle as a list
 * It is possible to click on a result to book the specified vehicle
 */

package com.vehiclerental.activities.Guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Staff.ChooseUserForBookingActivity;
import com.vehiclerental.activities.User.BookVehicleActivity;
import com.vehiclerental.adapters.BookingSearchResultListAdapter;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.SearchAvailableVehiclesRequestContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingSearchResultsActivity extends AppCompatActivity {

    public final static String BOOKING_SEARCH_RESULT_SERIALIZED_KEY = "booking_search_result_serialized";

    @Bind(R.id.vehicle_list)
    protected ListView vehicleListView;

    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private BookingSearchResultListAdapter bookingSearchResultListAdapter;
    private SearchAvailableVehiclesRequestContract searchAvailableVehiclesRequestContract;
    private BranchContract branchContract = PreferencesManager.getCurrentBranch(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_search_results);

        searchAvailableVehiclesRequestContract = new SearchAvailableVehiclesRequestContract();
        Bundle bundle = getIntent().getExtras();
        searchAvailableVehiclesRequestContract.pickupDate = bundle.getString(SearchAvailableVehicleActivity.PICKUP_DATE_KEY);
        searchAvailableVehiclesRequestContract.returnDate = bundle.getString(SearchAvailableVehicleActivity.RETURN_DATE_KEY);
        searchAvailableVehiclesRequestContract.vehicleType = bundle.getInt(SearchAvailableVehicleActivity.VEHICLE_TYPE_KEY);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        if (branchContract == null) {
            Toast toast = Toast.makeText(BookingSearchResultsActivity.this, getString(R.string.invalid_branch), Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            setTitle(String.format(getString(R.string.booking_search_results_title_formatted), branchContract.name));
        }

        List<BookingSearchResultContract> bookingSearchResults = new ArrayList<>();

        bookingSearchResultListAdapter = new BookingSearchResultListAdapter(this, bookingSearchResults);

        vehicleListView.setAdapter(bookingSearchResultListAdapter);

        //Opening app
        if (PreferencesManager.isLoggedIn(BookingSearchResultsActivity.this)) {
            if (PreferencesManager.isStaffUser(BookingSearchResultsActivity.this)) {
                vehicleListView.setClickable(true);
                vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BookingSearchResultContract selectedBooking = bookingSearchResultListAdapter.getItem(position);
                        Intent intent = new Intent(getBaseContext(), ChooseUserForBookingActivity.class);
                        intent.putExtra(BOOKING_SEARCH_RESULT_SERIALIZED_KEY, SerializationUtils.serialize(selectedBooking));
                        startActivity(intent);
                    }
                });
            } else {
                vehicleListView.setClickable(true);
                vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BookingSearchResultContract selectedBooking = bookingSearchResultListAdapter.getItem(position);
                        Intent intent = new Intent(getBaseContext(), BookVehicleActivity.class);
                        intent.putExtra(BOOKING_SEARCH_RESULT_SERIALIZED_KEY, SerializationUtils.serialize(selectedBooking));
                        startActivity(intent);
                    }
                });
            }
        } else {
            vehicleListView.setClickable(true);
            vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast toast = Toast.makeText(BookingSearchResultsActivity.this, R.string.login_register_required_book_vehicle, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }

        new GetVehiclesTask().start();
    }


//############################## TASKS #############################################################

    private class GetVehiclesTask extends AsyncTask<Void, Void, List<BookingSearchResultContract>> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(BookingSearchResultsActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<BookingSearchResultContract> doInBackground(Void... params) {
            try {
                return apiClient.searchAvailableVehicles(searchAvailableVehiclesRequestContract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BookingSearchResultContract> result) {
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(BookingSearchResultsActivity.this, apiException);
                return;
            }

            if (result.size() == 0) {
                Toast toast = Toast.makeText(BookingSearchResultsActivity.this, R.string.no_vehicle_found_criteria, Toast.LENGTH_SHORT);
                toast.show();
            }

            bookingSearchResultListAdapter.setVehicles(result);

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<BookingSearchResultContract>> start(){
            return this.execute();
        }
    }

}
