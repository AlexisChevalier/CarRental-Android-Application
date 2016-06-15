/**
 * CarRental
 *
 * This adapter handles a list of available vehicle search result items
 */

package com.vehiclerental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.contracts.BookingSearchResultContract;

import java.text.DecimalFormat;
import java.util.List;

public class BookingSearchResultListAdapter extends BaseAdapter {

    private List<BookingSearchResultContract> data = null;
    private LayoutInflater layoutInflater;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public BookingSearchResultListAdapter(Context context, List<BookingSearchResultContract> items) {
        layoutInflater = LayoutInflater.from(context);
        data = items;
    }

    public void setVehicles(List<BookingSearchResultContract> branches) {
        this.data = branches;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BookingSearchResultContract getItem(int position) {
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
            v = layoutInflater.inflate(R.layout.booking_search_result_list_item, null);
        }

        BookingSearchResultContract bookingResult = getItem(position);

        if (v != null) {
            /* CREATE VIEW */
            TextView carName = (TextView) v.findViewById(R.id.carName);
            TextView carSeats = (TextView) v.findViewById(R.id.carSeats);
            TextView carDoors = (TextView) v.findViewById(R.id.carDoors);
            TextView carTransmission = (TextView) v.findViewById(R.id.carTransmission);
            TextView bookingPrice = (TextView) v.findViewById(R.id.bookingPrice);
            TextView carPricePerDay = (TextView) v.findViewById(R.id.carPricePerDay);
            TextView moveCarDisclaimer = (TextView) v.findViewById(R.id.moveCarDisclaimer);
            TextView carRegistrationNumber = (TextView) v.findViewById(R.id.carRegistrationNumber);

            if (carName != null) {
                carName.setText(bookingResult.vehicle.name);
            }
            if (carSeats != null) {
                carSeats.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.seats_number_formatted), bookingResult.vehicle.seats));
            }
            if (carDoors != null) {
                carDoors.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.doors_number_formatted), bookingResult.vehicle.doors));
            }
            if (carTransmission != null) {
                carTransmission.setText(bookingResult.vehicle.automaticTransmission ? CarRentalApplication.getAppContext().getString(R.string.automatic_transmission) : CarRentalApplication.getAppContext().getString(R.string.manual_transmission));
            }
            if (bookingPrice!= null) {
                bookingPrice.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.pounds_price), decimalFormat.format(Math.round(bookingResult.price * 100.0) / 100.0)));
            }
            if (carPricePerDay != null) {
                carPricePerDay.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.pounds_per_day), decimalFormat.format(Math.round(bookingResult.vehicle.poundsPerDay * 100.0) / 100.0)));
            }
            if (carRegistrationNumber != null) {
                carRegistrationNumber.setText(bookingResult.vehicle.registrationNumber);
            }
            if (moveCarDisclaimer != null) {
                if (bookingResult.requireVehicleMove) {
                    moveCarDisclaimer.setVisibility(View.VISIBLE);
                }
            }
        }

        return v;
    }
}
