/**
 * CarRental
 *
 * This adapter handles a list of booking items
 *
 * Every element contains a button used to validate or invalidate a booking
 */

package com.vehiclerental.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.ChangeBookingStatusContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.DateUtils;

import java.text.DecimalFormat;
import java.util.List;

public class BookingListAdapter extends BaseAdapter {

    private List<BookingContract> data = null;
    private LayoutInflater layoutInflater;
    private Context context;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public BookingListAdapter(Context context, List<BookingContract> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = items;
    }

    public void setBookings(List<BookingContract> branches) {
        this.data = branches;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BookingContract getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = layoutInflater.inflate(R.layout.booking_list_item, null);
        }

        final BookingContract b = getItem(position);

        if (b != null) {
            /* CREATE VIEW */
            TextView carName = (TextView) v.findViewById(R.id.carName);
            TextView carSeats = (TextView) v.findViewById(R.id.carSeats);
            TextView carDoors = (TextView) v.findViewById(R.id.carDoors);
            TextView carTransmission = (TextView) v.findViewById(R.id.carTransmission);
            TextView bookingPrice = (TextView) v.findViewById(R.id.bookingPrice);
            TextView pickupDate = (TextView) v.findViewById(R.id.pickupDate);
            TextView returnDate = (TextView) v.findViewById(R.id.returnDate);
            TextView carRegistrationNumber = (TextView) v.findViewById(R.id.carRegistrationNumber);
            TextView validationStatus = (TextView) v.findViewById(R.id.validationStatus);
            Button changeBookingValidationButton = (Button) v.findViewById(R.id.changeBookingValidationButton);

            if (carName != null) {
                carName.setText(b.vehicle.name);
            }
            if (carSeats != null) {
                carSeats.setText(String.format(context.getString(R.string.seats_number_formatted), b.vehicle.seats));
            }
            if (carDoors != null) {
                carDoors.setText(String.format(context.getString(R.string.doors_number_formatted), b.vehicle.doors));
            }
            if (carTransmission != null) {
                carTransmission.setText(b.vehicle.automaticTransmission ? context.getString(R.string.automatic_transmission) : context.getString(R.string.manual_transmission));
            }
            if (bookingPrice!= null) {
                bookingPrice.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.pounds_price), decimalFormat.format(Math.round(b.price * 100.0) / 100.0)));
            }
            if (pickupDate != null) {
                pickupDate.setText(DateUtils.getFormattedDateFromIso8601String(b.pickupDate));
            }
            if (returnDate != null) {
                returnDate.setText(DateUtils.getFormattedDateFromIso8601String(b.returnDate));
            }
            if (carRegistrationNumber != null) {
                carRegistrationNumber.setText(b.vehicle.registrationNumber);
            }
            if (validationStatus != null) {
                if (b.bookingValidated) {
                    validationStatus.setText(R.string.status_accepted);
                } else {
                    validationStatus.setText(R.string.status_not_accepted);
                }
            }
            if (changeBookingValidationButton != null) {
                if (PreferencesManager.isStaffUser(CarRentalApplication.getAppContext())) {
                    changeBookingValidationButton.setVisibility(View.VISIBLE);
                    if (b.bookingValidated) {
                        changeBookingValidationButton.setText(R.string.invalidate_booking_label);
                    } else {
                        changeBookingValidationButton.setText(R.string.validate_booking);
                    }

                    changeBookingValidationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DoChangeBookingStatusTask(b, position).start();
                        }
                    });
                }
            }
        }

        return v;
    }

//############################## TASKS #############################################################

    private class DoChangeBookingStatusTask extends AsyncTask<Void, Void, BookingContract> {

        ProgressDialog progressDialog;
        Exception apiException;
        BookingContract booking;
        int positionInAdapter;

        public DoChangeBookingStatusTask(BookingContract contract, int positionInAdapter) {
            this.booking = contract;
            this.positionInAdapter = positionInAdapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected BookingContract doInBackground(Void... params) {
            try {
                ChangeBookingStatusContract contract = new ChangeBookingStatusContract();
                contract.bookingId = booking.id;
                contract.bookingValidated = !booking.bookingValidated;
                return apiClient.changeBookingStatus(contract);
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
                ActivityUtils.HandleException(context, apiException);
                return;
            }

            data.set(positionInAdapter, result);
            notifyDataSetChanged();

            toast = Toast.makeText(context, R.string.successfully_updated_booking, Toast.LENGTH_SHORT);
            toast.show();
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
