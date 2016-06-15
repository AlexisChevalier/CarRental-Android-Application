/**
 * CarRental
 *
 * This activity displays a form allowing the user to search for an available booking with diverse criteria
 */

package com.vehiclerental.activities.Guest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.vehiclerental.R;
import com.vehiclerental.contracts.SearchAvailableVehiclesRequestContract;
import com.vehiclerental.utils.ActivityUtils;
import com.vehiclerental.utils.DateUtils;
import com.vehiclerental.utils.StaticDataUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchAvailableVehicleActivity extends AppCompatActivity {

    //public message passing keys
    public final static String PICKUP_DATE_KEY = "pickup_date";
    public final static String RETURN_DATE_KEY = "return_date";
    public final static String VEHICLE_TYPE_KEY = "vehicle_type";

    //Automatic binding with Butterknife (external library)
    @Bind(R.id.vehicleTypePicker)
    protected Spinner vehicleTypeSpinner;
    @Bind(R.id.pickupDatePicker)
    protected DatePicker pickupDatePicker;
    @Bind(R.id.returnDatePicker)
    protected DatePicker returnDatePicker;
    @Bind(R.id.searchVehiclesButton)
    protected Button searchVehiclesButton;

    //private members
    private Calendar pickupDateCalendar;
    private Calendar returnDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_available_vehicle);

        ButterKnife.bind(this);
        ActivityUtils.buildDrawer(this);

        final SearchAvailableVehiclesRequestContract searchAvailableVehiclesRequestContract = new SearchAvailableVehiclesRequestContract();

        setTitle(getString(R.string.search_vehicle_title));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticDataUtils.getVehicleTypesArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(spinnerArrayAdapter);
        searchAvailableVehiclesRequestContract.vehicleType = -1;
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchAvailableVehiclesRequestContract.vehicleType = StaticDataUtils.getVehicleTypesList().indexOf(StaticDataUtils.getVehicleTypesArray()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchAvailableVehiclesRequestContract.vehicleType = -1;
            }
        });

        pickupDateCalendar = DateUtils.getCurrentDate();
        pickupDateCalendar.add(Calendar.DATE, 1);
        pickupDatePicker.setMinDate(pickupDateCalendar.getTime().getTime());
        pickupDatePicker.init(pickupDateCalendar.get(Calendar.YEAR), pickupDateCalendar.get(Calendar.MONTH), pickupDateCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                pickupDateCalendar.set(year, monthOfYear, dayOfMonth);
            }
        });


        returnDateCalendar = DateUtils.getCurrentDate();
        returnDateCalendar.add(Calendar.DATE, 2);
        returnDatePicker.setMinDate(returnDateCalendar.getTime().getTime());
        returnDatePicker.init(returnDateCalendar.get(Calendar.YEAR), returnDateCalendar.get(Calendar.MONTH), returnDateCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                returnDateCalendar.set(year, monthOfYear, dayOfMonth);
            }
        });

        searchVehiclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast;
                Calendar minDate = DateUtils.getCurrentDate();
                minDate.add(Calendar.DAY_OF_YEAR, 1);

                if (DateUtils.compareCalendar(pickupDateCalendar, minDate) == DateUtils.DATE1_BEFORE_DATE2) { // If pickup date before min date
                    toast = Toast.makeText(SearchAvailableVehicleActivity.this, R.string.invalid_pickup_date, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (DateUtils.compareCalendar(returnDateCalendar, pickupDateCalendar) <= DateUtils.DATE1_EQUAL_DATE2) { // If return date before or equal pickup date
                    toast = Toast.makeText(SearchAvailableVehicleActivity.this, R.string.invalid_return_date, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                searchAvailableVehiclesRequestContract.pickupDate = DateUtils.getIso8601DateString(pickupDateCalendar);
                searchAvailableVehiclesRequestContract.returnDate = DateUtils.getIso8601DateString(returnDateCalendar);

                if (searchAvailableVehiclesRequestContract.vehicleType == -1) {
                    toast = Toast.makeText(SearchAvailableVehicleActivity.this, R.string.invalid_vehicle_type, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Intent intent = new Intent(getBaseContext(), BookingSearchResultsActivity.class);
                intent.putExtra(SearchAvailableVehicleActivity.PICKUP_DATE_KEY, searchAvailableVehiclesRequestContract.pickupDate);
                intent.putExtra(SearchAvailableVehicleActivity.RETURN_DATE_KEY, searchAvailableVehiclesRequestContract.returnDate);
                intent.putExtra(SearchAvailableVehicleActivity.VEHICLE_TYPE_KEY, searchAvailableVehiclesRequestContract.vehicleType);

                startActivity(intent);
            }
        });
    }


}
