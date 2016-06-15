/**
 * CarRental
 *
 * This activity displays a form to create a new vehicle in the current branch
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Guest.BookingSearchResultsActivity;
import com.vehiclerental.activities.User.BookVehicleActivity;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.CreateAccountRequestContract;
import com.vehiclerental.contracts.CreateUpdateVehicleContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.security.InvalidParameterException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateVehicleActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.vehicleName)
    protected EditText vehicleName;
    @Bind(R.id.vehicleRegistrationNumber)
    protected EditText vehicleRegistrationNumber;
    @Bind(R.id.numberOfDoors)
    protected EditText numberOfDoors;
    @Bind(R.id.numberOfSeats)
    protected EditText numberOfSeats;
    @Bind(R.id.automaticTransmission)
    protected CheckBox automaticTransmission;
    @Bind(R.id.vehicleTypePicker)
    protected Spinner vehicleTypePicker;
    @Bind(R.id.pricePerDay)
    protected EditText pricePerDay;
    @Bind(R.id.createVehicleButton)
    protected Button createVehicleButton;

    //private members
    private CreateUpdateVehicleContract createUpdateVehicleContract;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.create_vehicle_title));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticDataUtils.getVehicleTypesArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        vehicleTypePicker.setAdapter(spinnerArrayAdapter);
        vehicleTypePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createUpdateVehicleContract.type = StaticDataUtils.getVehicleTypesList().indexOf(StaticDataUtils.getVehicleTypesArray()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                createUpdateVehicleContract.type = -1;
            }
        });

        createUpdateVehicleContract = new CreateUpdateVehicleContract();

        createVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vehicleName.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateVehicleActivity.this, R.string.invalid_vehicle_name, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createUpdateVehicleContract.name = vehicleName.getText().toString();

                if (vehicleRegistrationNumber.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateVehicleActivity.this, R.string.invalid_vehicle_reg_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createUpdateVehicleContract.registrationNumber = vehicleRegistrationNumber.getText().toString();

                try {
                    int doors = Integer.parseInt(numberOfDoors.getText().toString());
                    if (doors < 1) {
                        throw new InvalidParameterException();
                    }
                    createUpdateVehicleContract.doors = doors;
                } catch (Exception e) {
                    Toast toast = Toast.makeText(CreateVehicleActivity.this, R.string.invalid_doors_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                try {
                    int seats = Integer.parseInt(numberOfSeats.getText().toString());
                    if (seats < 1) {
                        throw new InvalidParameterException();
                    }
                    createUpdateVehicleContract.seats = seats;
                } catch (Exception e) {
                    Toast toast = Toast.makeText(CreateVehicleActivity.this, R.string.invalid_seats_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                try {
                    double poundsPerDay = Double.parseDouble(pricePerDay.getText().toString());
                    if (poundsPerDay <= 0) {
                        throw new InvalidParameterException();
                    }
                    createUpdateVehicleContract.poundsPerDay = poundsPerDay;
                } catch (Exception e) {
                    Toast toast = Toast.makeText(CreateVehicleActivity.this, R.string.invalid_price_per_day, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                createUpdateVehicleContract.automaticTransmission = automaticTransmission.isChecked();

                new DoCreateVehicleTask().start();
            }
        });
    }

    //############################## TASKS #############################################################

    private class DoCreateVehicleTask extends AsyncTask<Void, Void, VehicleContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CreateVehicleActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected VehicleContract doInBackground(Void... params) {
            try {
                return apiClient.createOrUpdateVehicle(createUpdateVehicleContract);
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
                ActivityUtils.HandleException(CreateVehicleActivity.this, apiException);
                return;
            }

            if (result == null) {
                toast = Toast.makeText(CreateVehicleActivity.this, R.string.vehicle_creation_failed, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            toast = Toast.makeText(CreateVehicleActivity.this, R.string.vehicle_successfully_created, Toast.LENGTH_SHORT);
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
