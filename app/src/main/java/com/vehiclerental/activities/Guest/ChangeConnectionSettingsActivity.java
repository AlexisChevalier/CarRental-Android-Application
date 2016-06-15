/**
 * CarRental
 *
 * This activity gives the ability to change the port and the IP of the server
 * It tests the correctness of the connection by fetching the branches
 */

package com.vehiclerental.activities.Guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.activities.Staff.BranchBookingsActivity;
import com.vehiclerental.activities.User.UserBookingsActivity;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.exceptions.ApiUnavailableException;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.LocationUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class ChangeConnectionSettingsActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.ipAddressField)
    protected EditText ipAddressField;
    @Bind(R.id.portField)
    protected EditText portField;
    @Bind(R.id.applyModificationsButton)
    protected Button applyModificationsButton;

    //private members
    private boolean comesFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_connection_settings);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        comesFromLogin = bundle.getBoolean(LoginActivity.COMES_FROM_LOGIN);

        String ipAddress = PreferencesManager.getHeadOfficeIpAddress(ChangeConnectionSettingsActivity.this);
        int port = PreferencesManager.getHeadOfficePort(ChangeConnectionSettingsActivity.this);

        ipAddressField.setText(ipAddress);
        portField.setText(String.valueOf(port));

        applyModificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String ipAddress = ipAddressField.getText().toString();
                    int port = Integer.valueOf(portField.getText().toString());

                    new TestServerSettingsTask(ipAddress, port).start();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(ChangeConnectionSettingsActivity.this, R.string.invalid_port_ip, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    //############################## TASKS #############################################################

    private class TestServerSettingsTask extends AsyncTask<Void, Void, List<BranchContract>> {

        ProgressDialog progressDialog;
        Exception apiException;
        CarRentalApiClient apiClient;
        String ipAddress;
        int port;

        public TestServerSettingsTask(String ipAddress, int port) {
            apiClient = CarRentalApiClientFactory.getApiClient(ipAddress, port);
            this.ipAddress = ipAddress;
            this.port = port;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ChangeConnectionSettingsActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<BranchContract> doInBackground(Void... params) {
            try {
                return apiClient.getBranches();
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<BranchContract> result) {
            Toast toast;
            progressDialog.dismiss();

            if (apiException != null) {
                if (apiException instanceof ApiUnavailableException) {
                    toast = Toast.makeText(ChangeConnectionSettingsActivity.this, R.string.system_found_at_ip_but_unavailable, Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(ChangeConnectionSettingsActivity.this, R.string.no_system_found_at_ip, Toast.LENGTH_SHORT);
                }
            } else {
                PreferencesManager.setHeadOfficeIpAddress(ChangeConnectionSettingsActivity.this, ipAddress);
                PreferencesManager.setHeadOfficePort(ChangeConnectionSettingsActivity.this, port);
                toast = Toast.makeText(ChangeConnectionSettingsActivity.this, R.string.system_found_and_working_at_ip, Toast.LENGTH_SHORT);

                Intent intent;
                if (comesFromLogin) {
                    intent = new Intent(getBaseContext(), LoginActivity.class);
                } else {
                    if (PreferencesManager.isLoggedIn(ChangeConnectionSettingsActivity.this)) {
                        if (PreferencesManager.isStaffUser(ChangeConnectionSettingsActivity.this)) {
                            intent = new Intent(getBaseContext(), BranchBookingsActivity.class);
                        } else {
                            intent = new Intent(getBaseContext(), UserBookingsActivity.class);
                        }
                    } else {
                        intent = new Intent(getBaseContext(), SearchAvailableVehicleActivity.class);
                    }
                }

                startActivity(intent);
            }

            progressDialog.dismiss();
            toast.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<BranchContract>> start(){
            return this.execute();
        }
    }

}