/**
 * CarRental
 *
 * This activity displays a form to edit a previously selected vehicle
 *
 * The ability to change the vehicle location has been disabled because it causes a side effect on the vehicle moves
 * It is still possible to update the vehicle status
 */

package com.vehiclerental.activities.Staff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.CreateUpdateVehicleContract;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditVehicleActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.carName)
    protected TextView carName;
    @Bind(R.id.carPricePerDay)
    protected TextView carPricePerDay;
    @Bind(R.id.carSeats)
    protected TextView carSeats;
    @Bind(R.id.carDoors)
    protected TextView carDoors;
    @Bind(R.id.carTransmission)
    protected TextView carTransmission;
    @Bind(R.id.carRegistrationNumber)
    protected TextView carRegistrationNumber;
    @Bind(R.id.carStatus)
    protected TextView carStatus;
    //Disabled because of side effect
    /*@Bind(R.id.vehicleBranchPicker)
    protected Spinner vehicleBranchPicker;*/
    @Bind(R.id.vehicleStatusPicker)
    protected Spinner vehicleStatusPicker;
    @Bind(R.id.updateVehicleButton)
    protected Button updateVehicleButton;

    //private members
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private VehicleContract vehicleContract;
    private CreateUpdateVehicleContract updateContract;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        Bundle bundle = getIntent().getExtras();

        vehicleContract = SerializationUtils.deserialize(bundle.getString(SearchVehicleActivity.SELECTED_VEHICLE_SERIALIZED_KEY), VehicleContract.class);

        updateContract = new CreateUpdateVehicleContract();
        updateContract.isUpdateOperation = true;
        updateContract.id = vehicleContract.id;
        //Disabled because of side effect
        //updateContract.newBranchId = vehicleContract.branch.id;
        updateContract.newStatusId = vehicleContract.branch.id;

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.update_vehicle_title));

        carName.setText(vehicleContract.name);
        carSeats.setText(String.format(getString(R.string.seats_number_formatted), vehicleContract.seats));
        carDoors.setText(String.format(getString(R.string.doors_number_formatted), vehicleContract.doors));
        carRegistrationNumber.setText(vehicleContract.registrationNumber);
        carTransmission.setText(vehicleContract.automaticTransmission ? getString(R.string.automatic_transmission) : getString(R.string.manual_transmission));
        carPricePerDay.setText(String.format(getString(R.string.pounds_per_day), decimalFormat.format(Math.round(vehicleContract.poundsPerDay * 100.0) / 100.0)));
        carStatus.setText(String.format(CarRentalApplication.getAppContext().getString(R.string.vehicle_status_formatted), StaticDataUtils.getVehicleStatusesList().get(vehicleContract.status)));

        //Disabled because of side effect
        /*final ArrayAdapter<String> spinnerBranchArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticDataUtils.getBranchNamesArray());
        spinnerBranchArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleBranchPicker.setAdapter(spinnerBranchArrayAdapter);
        for (int i = 0; i < spinnerBranchArrayAdapter.getCount(); i++) {
            if (spinnerBranchArrayAdapter.getItem(i).equals(vehicleContract.branch.name)) {
                vehicleBranchPicker.setSelection(i);
                break;
            }
        }*/

        final ArrayAdapter<String> spinnerStatusArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticDataUtils.getVehicleStatusesArray());
        spinnerStatusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleStatusPicker.setAdapter(spinnerStatusArrayAdapter);
        for (int i = 0; i < spinnerStatusArrayAdapter.getCount(); i++) {
            if (i == vehicleContract.status) {
                vehicleStatusPicker.setSelection(i);
                break;
            }
        }

        //Disabled because of side effect
        /*vehicleBranchPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateContract.newBranchId = StaticDataUtils.getBranchFromName(spinnerBranchArrayAdapter.getItem(position)).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateContract.newBranchId = vehicleContract.branch.id;
            }
        });*/
        vehicleStatusPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateContract.newStatusId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateContract.newStatusId = vehicleContract.status;
            }
        });

        updateVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoUpdateVehicleTask().start();
            }
        });
    }

    //############################## TASKS #############################################################

    private class DoUpdateVehicleTask extends AsyncTask<Void, Void, VehicleContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(EditVehicleActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected VehicleContract doInBackground(Void... params) {
            try {
                return apiClient.createOrUpdateVehicle(updateContract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(VehicleContract result) {
            Toast toast;
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(EditVehicleActivity.this, apiException);
                return;
            }

            if (result == null) {
                toast = Toast.makeText(EditVehicleActivity.this, R.string.vehicle_update_failed, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            toast = Toast.makeText(EditVehicleActivity.this, R.string.vehicle_update_sucess, Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(getBaseContext(), BranchBookingsActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, VehicleContract> start(){
            return this.execute();
        }
    }

}
