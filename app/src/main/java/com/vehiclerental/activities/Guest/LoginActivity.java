/**
 * CarRental
 *
 * This activity is the entry point of the application
 * It displays a login form with multiple buttons to connect, register, just browse the vehicles or change the connection settings
 */

package com.vehiclerental.activities.Guest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    //public message passing keys
    public final static String COMES_FROM_LOGIN = "comes_from_login";

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.emailTextField)
    protected EditText emailTextField;
    @Bind(R.id.paswordTextField)
    protected EditText passwordTextField;
    @Bind(R.id.loginButton)
    protected Button loginButton;
    @Bind(R.id.createAccountButton)
    protected Button createAccountButton;
    @Bind(R.id.browseOnlyButton)
    protected Button browseOnlyButton;
    @Bind(R.id.changeConnectionSettingsButton)
    protected Button changeConnectionSettingsButton;

    //private members
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        changeConnectionSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarRentalApplication.getAppContext(), ChangeConnectionSettingsActivity.class);
                intent.putExtra(COMES_FROM_LOGIN, true);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Setting temporary account details for the API client
            UserContract user = new UserContract();
            user.emailAddress = emailTextField.getText().toString();
            PreferencesManager.setLoggedUser(LoginActivity.this, user);
            PreferencesManager.setUserPassword(LoginActivity.this, passwordTextField.getText().toString());

            new DoLoginTask().start();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
            startActivity(intent);
            }
        });

        browseOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            PreferencesManager.clearSession(LoginActivity.this);
            Intent intent = new Intent(getBaseContext(), ChooseBranchActivity.class);
            startActivity(intent);
            }
        });

        //Skips login if already in memory
        UserContract user = PreferencesManager.getLoggedUser(LoginActivity.this);
        String password = PreferencesManager.getUserPassword(LoginActivity.this);
        if (user != null && password != null) {
            Intent intent = new Intent(getBaseContext(), ChooseBranchActivity.class);
            startActivity(intent);
        }
    }

//############################## TASKS #############################################################

    /*
     * Do Login
     */
    private class DoLoginTask extends AsyncTask<Void, Void, UserContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
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
        protected void onPostExecute(UserContract result) {
            Toast toast;

            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(LoginActivity.this, apiException);
                return;
            }

            if (result == null) {
                toast = Toast.makeText(LoginActivity.this, R.string.invalid_credentials_error, Toast.LENGTH_SHORT);

                PreferencesManager.clearSession(LoginActivity.this);
                toast.show();

                return;
            }

            //Updating local user
            PreferencesManager.setLoggedUser(LoginActivity.this, result);

            toast = Toast.makeText(LoginActivity.this, R.string.successfully_logged_in, Toast.LENGTH_SHORT);
            toast.show();

            //Opening app
            Intent intent = new Intent(getBaseContext(), ChooseBranchActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, UserContract> start(){
            return this.execute();
        }
    }
}