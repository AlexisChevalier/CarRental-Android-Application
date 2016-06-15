/**
 * CarRental
 *
 * This activity displays two ways to choose an user for a booking, either by searching an email or fullname, or by creating a new user
 * It also displays the user search results
 * When an account is clicked, it brings the user to the next step of the booking
 */

package com.vehiclerental.activities.Staff;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Guest.BookingSearchResultsActivity;
import com.vehiclerental.activities.User.BookVehicleActivity;
import com.vehiclerental.adapters.UserListAdapter;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.SearchAvailableVehiclesRequestContract;
import com.vehiclerental.contracts.SearchUserContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseUserForBookingActivity extends AppCompatActivity {

    //public message passing keys
    public final static String USER_SELECTED_SERIALIZED_KEY = "user_selected_serialized";

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.user_list)
    protected ListView userListView;
    @Bind(R.id.search_user_field)
    protected EditText search_user_field;
    @Bind(R.id.create_new_user_account)
    protected Button createNewUserAccountButton;

    //private members
    private UserListAdapter userListAdapter;
    private SearchUserTask searchTask = null;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private BookingSearchResultContract selectedBookingContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_for_booking);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.choose_user_title));

        Bundle bundle = getIntent().getExtras();

        selectedBookingContract = SerializationUtils.deserialize(bundle.getString(BookingSearchResultsActivity.BOOKING_SEARCH_RESULT_SERIALIZED_KEY), BookingSearchResultContract.class);

        List<UserContract> users = new ArrayList<>();

        userListAdapter = new UserListAdapter(this, users);

        userListView.setAdapter(userListAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserContract selectedUser = userListAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), BookVehicleActivity.class);
                intent.putExtra(BookingSearchResultsActivity.BOOKING_SEARCH_RESULT_SERIALIZED_KEY, SerializationUtils.serialize(selectedBookingContract));
                intent.putExtra(USER_SELECTED_SERIALIZED_KEY, SerializationUtils.serialize(selectedUser));
                startActivity(intent);
            }
        });

        createNewUserAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateUserActivity.class);
                intent.putExtra(BookingSearchResultsActivity.BOOKING_SEARCH_RESULT_SERIALIZED_KEY, SerializationUtils.serialize(selectedBookingContract));
                startActivity(intent);
            }
        });

        search_user_field.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();
            private final long DELAY_MS = 500;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                //Will prevent the request to be thrown at each letter
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (searchTask != null) {
                                        searchTask.cancel(true);
                                    }
                                    searchTask = new SearchUserTask(s.toString());
                                    searchTask.start();
                                }
                            });
                        }
                    },
                    DELAY_MS
                );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //############################## TASKS #############################################################

    private class SearchUserTask extends AsyncTask<Void, Void, List<UserContract>> {

        SearchUserContract contract = new SearchUserContract();

        public SearchUserTask(String term) {
            contract.searchTerm = term;
        }

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ChooseUserForBookingActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<UserContract> doInBackground(Void... params) {
            try {
                return apiClient.searchUser(contract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<UserContract> result) {
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(ChooseUserForBookingActivity.this, apiException);
                return;
            }

            userListAdapter.setUsers(result);

            if (result.size() == 0) {
                Toast toast = Toast.makeText(ChooseUserForBookingActivity.this, R.string.no_user_found_for_criteria, Toast.LENGTH_SHORT);
                toast.show();
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<UserContract>> start(){
            return this.execute();
        }
    }
}
