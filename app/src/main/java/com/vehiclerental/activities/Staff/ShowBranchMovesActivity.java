/**
 * CarRental
 *
 * This activity displays a list of the future outgoing vehicle moves for this branch
 *
 * By clicking a button the user can switch between the incoming vehicle moves and the outgoing ones
 */

package com.vehiclerental.activities.Staff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.vehiclerental.R;
import com.vehiclerental.adapters.VehicleListAdapter;
import com.vehiclerental.adapters.VehicleMoveListAdapter;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.CarRentalApiClientFactory;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.GetBranchVehicleMovesContract;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.SerializationUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowBranchMovesActivity extends AppCompatActivity {

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.moves_list)
    protected ListView movesListView;
    @Bind(R.id.switch_moves_type_button)
    protected Button switchMovesTypeButton;

    //private members
    private VehicleMoveListAdapter vehicleMoveListAdapter;
    private BranchContract branchContract = PreferencesManager.getCurrentBranch(this);
    private CarRentalApiClient apiClient = CarRentalApiClientFactory.getApiClient();
    private boolean outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_branch_moves);

        outgoing = true;

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        if (branchContract == null) {
            Toast toast = Toast.makeText(ShowBranchMovesActivity.this, getString(R.string.invalid_branch), Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            setTitle(String.format(getString(R.string.outgoing_vehicles_moves_title), branchContract.name));
        }

        switchMovesTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetMovesTask().start();
            }
        });

        List<BookingContract> moves = new ArrayList<>();
        vehicleMoveListAdapter = new VehicleMoveListAdapter(this, moves);
        vehicleMoveListAdapter.setOutgoing(outgoing);

        movesListView.setAdapter(vehicleMoveListAdapter);

        new GetMovesTask().start();
    }


//############################## TASKS #############################################################

    private class GetMovesTask extends AsyncTask<Void, Void, List<BookingContract>> {

        ProgressDialog progressDialog;
        Exception apiException;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ShowBranchMovesActivity.this);
            progressDialog.setMessage(getString(R.string.loading_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<BookingContract> doInBackground(Void... params) {
            try {
                GetBranchVehicleMovesContract contract = new GetBranchVehicleMovesContract();
                contract.outgoing = !outgoing;
                return apiClient.getVehiclesMove(contract);
            } catch (Exception e) {
                apiException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BookingContract> result) {
            Toast toast;
            progressDialog.dismiss();

            if (apiException != null) {
                ActivityUtils.HandleException(ShowBranchMovesActivity.this, apiException);
                return;
            }

            vehicleMoveListAdapter.setMoves(result);

            outgoing = !outgoing;
            if (outgoing) {
                setTitle(String.format(getString(R.string.outgoing_vehicles_moves_title), branchContract.name));
                switchMovesTypeButton.setText(R.string.switch_to_incoming_vehicles_button);
            } else {
                setTitle(String.format(getString(R.string.incoming_vehicles_moves_title), branchContract.name));
                switchMovesTypeButton.setText(R.string.switch_to_outgoing_vehicles_button);
            }
            vehicleMoveListAdapter.setOutgoing(outgoing);

            if (result.size() == 0) {
                if (outgoing) {
                    toast = Toast.makeText(ShowBranchMovesActivity.this, R.string.no_outgoing_vehicles, Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(ShowBranchMovesActivity.this, R.string.no_incoming_vehicles, Toast.LENGTH_SHORT);
                }
                toast.show();
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public AsyncTask<Void, Void, List<BookingContract>> start(){
            return this.execute();
        }
    }

}