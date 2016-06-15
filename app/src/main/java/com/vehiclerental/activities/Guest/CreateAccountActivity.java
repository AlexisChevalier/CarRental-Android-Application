/**
 * CarRental
 *
 * This activity displays a form to create an account on the server
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
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.CreateAccountRequestContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.fullNameTextField)
    protected EditText fullNameTextField;

    @Bind(R.id.emailTextField)
    protected EditText emailTextField;

    @Bind(R.id.phoneTextField)
    protected EditText phoneNumberTextField;

    @Bind(R.id.passwordTextField)
    protected EditText passwordTextField;

    @Bind(R.id.confirmPasswordTextField)
    protected EditText confirmPasswordTextField;

    @Bind(R.id.streetTextField)
    protected EditText streetTextField;

    @Bind(R.id.cityTextField)
    protected EditText cityTextField;

    @Bind(R.id.postalCodeTextField)
    protected EditText postalCodeTextField;

    @Bind(R.id.countryTextField)
    protected EditText countryTextField;

    @Bind(R.id.createAccountButton)
    protected Button createAccountButton;

    //private members
    private CreateAccountRequestContract createAccountRequestContract;
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ButterKnife.bind(this);

        createAccountRequestContract = new CreateAccountRequestContract();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //FullName
                if (fullNameTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_fullname, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.fullName = fullNameTextField.getText().toString();

                //Email
                if (emailTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_email_address, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.emailAddress = emailTextField.getText().toString();

                //PhoneNumber
                if (phoneNumberTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_phone_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.phoneNumber = phoneNumberTextField.getText().toString();

                //Password
                if (passwordTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_password, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.password = passwordTextField.getText().toString();

                //Password confirmation
                if (!passwordTextField.getText().toString().equals(confirmPasswordTextField.getText().toString())) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.passwords_doesnt_match, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                //Street
                if (streetTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_street, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.address_street = streetTextField.getText().toString();

                //City
                if (cityTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_city, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.address_city = cityTextField.getText().toString();

                //Postal Code
                if (postalCodeTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_postal_code, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.address_postalCode = postalCodeTextField.getText().toString();

                //Country
                if (countryTextField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(CreateAccountActivity.this, R.string.invalid_country, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                createAccountRequestContract.address_country = countryTextField.getText().toString();

                new DoCreateAccountTask().start();
            }
        });
    }

    //############################## TASKS #############################################################

    private class DoCreateAccountTask extends AsyncTask<Void, Void, UserContract> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CreateAccountActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected UserContract doInBackground(Void... params) {
            try {
                return apiClient.createAccount(createAccountRequestContract);
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
                ActivityUtils.HandleException(CreateAccountActivity.this, apiException);
                return;
            }

            if (result == null) {
                PreferencesManager.clearSession(CreateAccountActivity.this);

                return;
            }

            //Updating local user
            PreferencesManager.setLoggedUser(CreateAccountActivity.this, result);
            PreferencesManager.setUserPassword(CreateAccountActivity.this, createAccountRequestContract.password);

            toast = Toast.makeText(CreateAccountActivity.this, R.string.user_successfully_registered, Toast.LENGTH_SHORT);
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
