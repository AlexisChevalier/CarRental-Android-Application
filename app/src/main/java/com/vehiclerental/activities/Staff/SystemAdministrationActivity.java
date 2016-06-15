/**
 * CarRental
 *
 * This activity displays the current status of the system (offline or online) and a button to shutdown the system
 */

package com.vehiclerental.activities.Staff;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vehiclerental.R;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SystemAdministrationActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.systemStatusText)
    protected TextView systemStatusText;
    @Bind(R.id.shutDownSystemButton)
    protected Button shutDownSystemButton;

    //private members
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_administration);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        setTitle(getString(R.string.system_administration_title));

        shutDownSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShutdownSystemTask().start();
            }
        });

        new CheckSystemTask().start();
    }


    //############################## TASKS #############################################################

    private class CheckSystemTask extends AsyncTask<Void, Void, UserContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SystemAdministrationActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected UserContract doInBackground(Void... params) {
            try {
                return apiClient.getAccountDetails();
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final UserContract result) {
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(SystemAdministrationActivity.this, apiException);
                return;
            }

            if (result == null) {
                shutDownSystemButton.setVisibility(View.GONE);
                systemStatusText.setText(R.string.system_is_offline);
            } else {
                shutDownSystemButton.setVisibility(View.VISIBLE);
                systemStatusText.setText(R.string.system_is_online);
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, UserContract> start(){
            return this.execute();
        }
    }

    /*
     * Shutdown system
     */
    private class ShutdownSystemTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SystemAdministrationActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                return apiClient.shutdownSystem();
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Void result) {
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(SystemAdministrationActivity.this, apiException);
                return;
            }

            shutDownSystemButton.setVisibility(View.GONE);
            systemStatusText.setText(R.string.system_is_offline);

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, Void> start(){
            return this.execute();
        }
    }

}
