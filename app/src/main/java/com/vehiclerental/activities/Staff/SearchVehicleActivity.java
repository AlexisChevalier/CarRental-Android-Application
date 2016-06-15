/**
 * CarRental
 *
 * This activity displays a form to search vehicles in the current branch either by registration number or type
 */

package com.vehiclerental.activities.Staff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.SearchVehicleContract;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchVehicleActivity extends AppCompatActivity {

    //public message passing keys
    public final static String VEHICLE_SEARCH_RESULTS_LIST_SERIALIZED_KEY = "vehicle_search_results_list_serialized";
    public final static String SELECTED_VEHICLE_SERIALIZED_KEY = "selected_serialized";

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.vehicleTypePicker)
    protected Spinner vehicleTypePicker;
    @Bind(R.id.registrationNumberField)
    protected EditText registrationNumberField;
    @Bind(R.id.searchVehiclesButton)
    protected Button searchVehiclesButton;

    //private members
    private SearchVehicleContract searchVehicleContract;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_vehicle);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.search_vehicle_title));

        searchVehicleContract = new SearchVehicleContract();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticDataUtils.getVehicleTypesArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        vehicleTypePicker.setAdapter(spinnerArrayAdapter);
        vehicleTypePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchVehicleContract.vehicleTypeId = StaticDataUtils.getVehicleTypesList().indexOf(StaticDataUtils.getVehicleTypesArray()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchVehicleContract.vehicleTypeId = -1;
            }
        });

        searchVehiclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchVehicleContract.registrationNumber = registrationNumberField.getText().toString();

                new SearchVehiclesTask().start();
            }
        });
    }

    //############################## TASKS #############################################################

    private class SearchVehiclesTask extends AsyncTask<Void, Void, List<VehicleContract>> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SearchVehicleActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<VehicleContract> doInBackground(Void... params) {
            try {
                return apiClient.searchVehicles(searchVehicleContract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<VehicleContract> result) {
            Toast toast;

            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(SearchVehicleActivity.this, apiException);
                return;
            }

            if (result == null) {
                toast = Toast.makeText(SearchVehicleActivity.this, R.string.invalid_search_criteria, Toast.LENGTH_SHORT);
                toast.show();

                return;
            }

            if (!searchVehicleContract.registrationNumber.isEmpty()) {
                if (result.size() >= 1) {
                    Intent intent = new Intent(getBaseContext(), EditVehicleActivity.class);
                    intent.putExtra(SELECTED_VEHICLE_SERIALIZED_KEY, SerializationUtils.serialize(result.get(0)));
                    startActivity(intent);
                } else {
                    toast = Toast.makeText(SearchVehicleActivity.this, R.string.no_vehicle_found_for_registration_number, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                if (result.size() == 0) {
                    toast = Toast.makeText(SearchVehicleActivity.this, R.string.no_vehicle_found_for_criteria, Toast.LENGTH_SHORT);
                    toast.show();
                }

                Intent intent = new Intent(getBaseContext(), VehicleSearchResultsActivity.class);
                intent.putExtra(VEHICLE_SEARCH_RESULTS_LIST_SERIALIZED_KEY, SerializationUtils.serialize(result));
                startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<VehicleContract>> start(){
            return this.execute();
        }
    }
}
