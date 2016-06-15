/**
 * CarRental
 *
 * This adapter handles a list of vehicle search results
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
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.utils.StaticDataUtils;

import java.text.DecimalFormat;
import java.util.List;

public class VehicleListAdapter extends BaseAdapter {

    private List<VehicleContract> data = null;
    private LayoutInflater layoutInflater;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public VehicleListAdapter(Context context, List<VehicleContract> items) {
        layoutInflater = LayoutInflater.from(context);
        data = items;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public VehicleContract getItem(int position) {
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
            v = layoutInflater.inflate(R.layout.vehicle_list_item, null);
        }

        VehicleContract ve = getItem(position);

        if (ve != null) {
            TextView carName = (TextView) v.findViewById(R.id.carName);
            TextView carSeats = (TextView) v.findViewById(R.id.carSeats);
            TextView carDoors = (TextView) v.findViewById(R.id.carDoors);
            TextView carTransmission = (TextView) v.findViewById(R.id.carTransmission);
            TextView carPricePerDay = (TextView) v.findViewById(R.id.carPricePerDay);
            TextView carRegistrationNumber = (TextView) v.findViewById(R.id.carRegistrationNumber);
            TextView carStatus = (TextView) v.findViewById(R.id.carStatus);

            if (carName != null) {
                carName.setText(ve.name);
            }
            if (carSeats != null) {
                carSeats.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.seats_number_formatted), ve.seats));
            }
            if (carDoors != null) {
                carDoors.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.doors_number_formatted), ve.doors));
            }
            if (carTransmission != null) {
                carTransmission.setText(ve.automaticTransmission ?
                        CarRentalApplication.getAppContext().getString(R.string.automatic_transmission)
                        :
                        CarRentalApplication.getAppContext().getString(R.string.manual_transmission));
            }
            if (carPricePerDay != null) {
                carPricePerDay.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.pounds_per_day), decimalFormat.format(Math.round(ve.poundsPerDay * 100.0) / 100.0)));
            }
            if (carRegistrationNumber != null) {
                carRegistrationNumber.setText(ve.registrationNumber);
            }
            if (carStatus != null) {
                carStatus.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.vehicle_status_formatted),StaticDataUtils.getVehicleStatusesList().get(ve.status)));
            }
        }

        return v;
    }
}
