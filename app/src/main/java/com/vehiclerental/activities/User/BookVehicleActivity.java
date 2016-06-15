/**
 * CarRental
 *
 * This activity proposes to the user to confirm a booking he previously selected
 * It displays a small summary of the booking details and asks for the credit card details with a form
 */

package com.vehiclerental.activities.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Guest.BookingSearchResultsActivity;
import com.vehiclerental.activities.Staff.BranchBookingsActivity;
import com.vehiclerental.activities.Staff.ChooseUserForBookingActivity;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.CreateBookingContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.DateUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookVehicleActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.carName)
    protected TextView carName;
    @Bind(R.id.bookingPrice)
    protected TextView bookingPrice;
    @Bind(R.id.carSeats)
    protected TextView carSeats;
    @Bind(R.id.carDoors)
    protected TextView carDoors;
    @Bind(R.id.carTransmission)
    protected TextView carTransmission;
    @Bind(R.id.pickupDate)
    protected TextView pickupDate;
    @Bind(R.id.returnDate)
    protected TextView returnDate;
    @Bind(R.id.creditCardNumberTextField)
    protected EditText creditCardNumberTextField;
    @Bind(R.id.creditCardExpirationMonthTextField)
    protected EditText creditCardExpirationMonthTextField;
    @Bind(R.id.creditCardExpirationYearTextField)
    protected EditText creditCardExpirationYearTextField;
    @Bind(R.id.creditCardCvcCodeTextField)
    protected EditText creditCardCvcCodeTextField;
    @Bind(R.id.userNameField)
    protected TextView userNameField;
    @Bind(R.id.payAndConfirmButton)
    protected Button payAndConfirmButton;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private BookingSearchResultContract selectedBookingContract;
    private UserContract selectedUserContract;
    private CreateBookingContract createBookingContract;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private BranchContract branchContract = PreferencesManager.getCurrentBranch(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vehicle);

        Bundle bundle = getIntent().getExtras();

        selectedBookingContract = SerializationUtils.deserialize(bundle.getString(BookingSearchResultsActivity.BOOKING_SEARCH_RESULT_SERIALIZED_KEY), BookingSearchResultContract.class);

        if (PreferencesManager.isStaffUser(this)) {
            selectedUserContract = SerializationUtils.deserialize(bundle.getString(ChooseUserForBookingActivity.USER_SELECTED_SERIALIZED_KEY), UserContract.class);
        }

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        if (branchContract == null) {
            Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_branch, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            if (PreferencesManager.isStaffUser(this)) {
                setTitle(String.format(getString(R.string.create_booking_title), branchContract.name));
            } else {
                setTitle(String.format(getString(R.string.book_vehicle_title), branchContract.name));
            }
        }

        carName.setText(selectedBookingContract.vehicle.name);
        carSeats.setText(String.format(getString(R.string.seats_number_formatted), selectedBookingContract.vehicle.seats));
        carDoors.setText(String.format(getString(R.string.doors_number_formatted), selectedBookingContract.vehicle.doors));
        carTransmission.setText(selectedBookingContract.vehicle.automaticTransmission ? getString(R.string.automatic_transmission) : getString(R.string.manual_transmission));
        bookingPrice.setText(String.format(getString(R.string.pounds_price), decimalFormat.format(Math.round(selectedBookingContract.price * 100.0) / 100.0)));
        pickupDate.setText(DateUtils.getFormattedDateFromIso8601String(selectedBookingContract.pickupDate));
        returnDate.setText(DateUtils.getFormattedDateFromIso8601String(selectedBookingContract.returnDate));

        if (PreferencesManager.isStaffUser(this)) {
            userNameField.setVisibility(View.VISIBLE);
            userNameField.setText(String.format(getString(R.string.user_formatted), selectedUserContract.fullName));
        }

        payAndConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBookingContract = new CreateBookingContract();
                createBookingContract.vehicleId = selectedBookingContract.vehicle.id;
                createBookingContract.vehicleBranchId = selectedBookingContract.vehicle.branch.id;
                createBookingContract.bookingBranchId = branchContract.id;
                createBookingContract.pickupDate = selectedBookingContract.pickupDate;
                createBookingContract.returnDate = selectedBookingContract.returnDate;

                //Card number
                if (creditCardNumberTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createBookingContract.creditCardNumber = creditCardNumberTextField.getText().toString();

                //card exp month
                if (creditCardExpirationMonthTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_exp_month, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                try {
                    int month = Integer.decode(creditCardExpirationMonthTextField.getText().toString());

                    if (month < 1 || month > 12) {
                        Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_exp_month, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    createBookingContract.creditCardExpirationMonth = creditCardExpirationMonthTextField.getText().toString();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_exp_month, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }


                //card exp year
                if (creditCardExpirationYearTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_exp_year, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                try {
                    Integer.decode(creditCardExpirationYearTextField.getText().toString());
                    createBookingContract.creditCardExpirationYear = creditCardExpirationYearTextField.getText().toString();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_exp_year, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }


                //card cvc
                if (creditCardCvcCodeTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(BookVehicleActivity.this, R.string.invalid_credit_card_cvc_code, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createBookingContract.creditCardCvcCode = creditCardCvcCodeTextField.getText().toString();

                if (PreferencesManager.isStaffUser(BookVehicleActivity.this)) {
                    createBookingContract.bookingOwnerUserId = selectedUserContract.id;
                }

                new DoBookVehicleTask().start();
            }
        });
    }


//############################## TASKS #############################################################

    private class DoBookVehicleTask extends AsyncTask<Void, Void, BookingContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(BookVehicleActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected BookingContract doInBackground(Void... params) {
            try {
                return apiClient.bookVehicle(createBookingContract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookingContract result) {
            Toast toast;

            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(BookVehicleActivity.this, apiException);
                return;
            }


            toast = Toast.makeText(BookVehicleActivity.this, R.string.booking_successfully_created, Toast.LENGTH_SHORT);
            toast.show();

            if (PreferencesManager.isStaffUser(BookVehicleActivity.this)) {
                Intent intent = new Intent(getBaseContext(), BranchBookingsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getBaseContext(), UserBookingsActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, BookingContract> start(){
            return this.execute();
        }
    }
}