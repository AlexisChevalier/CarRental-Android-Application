/**
 * CarRental
 *
 * This activity proposes a list of the current user bookings (past, present and future bookings)
 */

package com.vehiclerental.activities.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Guest.SearchAvailableVehicleActivity;
import com.vehiclerental.adapters.BookingListAdapter;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserBookingsActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.booking_list)
    protected ListView bookingListView;
    @Bind(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    //private members
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private BookingListAdapter bookingListAdapter;
    private BranchContract branchContract = PreferencesManager.getCurrentBranch(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        if (branchContract == null) {
            Toast toast = Toast.makeText(UserBookingsActivity.this, R.string.invalid_branch, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            setTitle(String.format(getString(R.string.user_bookings_title), branchContract.name));
        }

        List<BookingContract> bookings = new ArrayList<>();

        bookingListAdapter = new BookingListAdapter(this, bookings);

        bookingListView.setItemsCanFocus(false);
        bookingListView.setAdapter(bookingListAdapter);

        bookingListView.setClickable(false);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchAvailableVehicleActivity.class);
                startActivity(intent);
            }
        });

        new GetBookingsTask().start();
    }


//############################## TASKS #############################################################

    /*
     * Fetches branches
     */
    private class GetBookingsTask extends AsyncTask<Void, Void, List<BookingContract>> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(UserBookingsActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<BookingContract> doInBackground(Void... params) {
            try {
                return apiClient.getUserBookingsForBranch();
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BookingContract> result) {
            Toast toast;
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(UserBookingsActivity.this, apiException);
                return;
            }

            bookingListAdapter.setBookings(result);

            if (result.size() == 0) {
                toast = Toast.makeText(UserBookingsActivity.this, R.string.no_booking_at_branch, Toast.LENGTH_SHORT);
                toast.show();
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<BookingContract>> start(){
            return this.execute();
        }
    }

}
