/**
 * CarRental
 *
 * This adapter handles a list of user vehicle moves for a branch
 *
 * It includes a few helper methods to help displaying the correct values
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
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.DateUtils;

import java.util.Calendar;
import java.util.List;

public class VehicleMoveListAdapter extends BaseAdapter {

    private boolean outgoing;
    private List<BookingContract> data = null;
    private LayoutInflater layoutInflater;

    public VehicleMoveListAdapter(Context context, List<BookingContract> items) {
        layoutInflater = LayoutInflater.from(context);
        data = items;
    }

    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public void setMoves(List<BookingContract> branches) {
        this.data = branches;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BookingContract getItem(int position) {
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
            v = layoutInflater.inflate(R.layout.vehicle_move_list_item, null);
        }

        BookingContract b = getItem(position);

        if (!viewShouldBeDisplayed(b)) {
            return null;
        }

        if (b != null) {
            /* CREATE VIEW */
            TextView carName = (TextView) v.findViewById(R.id.carName);
            TextView carRegistrationNumber = (TextView) v.findViewById(R.id.carRegistrationNumber);
            TextView moveDate = (TextView) v.findViewById(R.id.moveDate);
            TextView moveBranch = (TextView) v.findViewById(R.id.moveBranch);

            if (carName != null) {
                carName.setText(b.vehicle.name);
            }
            if (carRegistrationNumber != null) {
                carRegistrationNumber.setText(b.vehicle.registrationNumber);
            }
            if (moveDate != null) {
                moveDate.setText(getCorrectBranchName(b));
            }
            if (moveBranch != null) {
                moveBranch.setText(getCorrectDate(b));
            }
        }

        return v;
    }

    /**
     * Determines which branch name should be displayed based on the booking details
     *
     * @param bookingContract the booking representation
     * @return the branch name to display
     */
    private String getCorrectBranchName(BookingContract bookingContract) {
        if (bookingContract.branch.equals(PreferencesManager.getCurrentBranch(CarRentalApplication.getAppContext()).name)) {
            //Booking is on this branch
            if (outgoing) {
                //This is the vehicle branch
                return String.format(CarRentalApplication.getAppContext().getString(R.string.to_destination_formatted), bookingContract.vehicle.branch.name);
            } else {
                //This is the other branch
                return String.format(CarRentalApplication.getAppContext().getString(R.string.from_destination_formatted), bookingContract.branch);
            }
        } else {
            //Booking is on the other branch
            if (outgoing) {
                //This is the other branch
                return String.format(CarRentalApplication.getAppContext().getString(R.string.to_destination_formatted), bookingContract.branch);
            } else {
                //This is the vehicle branch
                return String.format(CarRentalApplication.getAppContext().getString(R.string.from_destination_formatted), bookingContract.vehicle.branch.name);
            }
        }
    }

    /**
     * Determine if the view should be displayed based on the booking details (if the move is still relevant)
     *
     * @param bookingContract the booking representation
     * @return true if the view should be displayed, false otherwise
     */
    private boolean viewShouldBeDisplayed(BookingContract bookingContract) {
        Calendar returnDate = DateUtils.getCalendarFromIso8601String(bookingContract.vehicleMove.vehicleReturnDate);
        Calendar moveDate = DateUtils.getCalendarFromIso8601String(bookingContract.vehicleMove.vehicleMoveDate);
        Calendar todayDate = DateUtils.getCurrentDate();

        if (bookingContract.branch.equals(PreferencesManager.getCurrentBranch(CarRentalApplication.getAppContext()).name)) {
            //Booking is on this branch
            if (outgoing) {
                //This is the end
                if (DateUtils.compareCalendar(todayDate, returnDate) == DateUtils.DATE1_AFTER_DATE2) {
                    return false;
                }
            } else {
                //This is the start
                if (DateUtils.compareCalendar(todayDate, moveDate) == DateUtils.DATE1_AFTER_DATE2) {
                    return false;
                }
            }
        } else {
            //Booking is on the other branch
            if (outgoing) {
                //This is the start
                if (DateUtils.compareCalendar(todayDate, moveDate) == DateUtils.DATE1_AFTER_DATE2) {
                    return false;
                }
            } else {
                //This is the end
                if (DateUtils.compareCalendar(todayDate, returnDate) == DateUtils.DATE1_AFTER_DATE2) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Determine which date should be displayed based on the booking details
     * @param bookingContract the booking representation
     * @return the date to display, formatted
     */
    private String getCorrectDate(BookingContract bookingContract) {
        if (bookingContract.branch.equals(PreferencesManager.getCurrentBranch(CarRentalApplication.getAppContext()).name)) {
            //Booking is on this branch
            if (outgoing) {
                //This is the end
                return String.format(CarRentalApplication.getAppContext().getString(R.string.date_formatted), DateUtils.getFormattedDateFromIso8601String(bookingContract.vehicleMove.vehicleReturnDate));
            } else {
                //This is the start
                return String.format(CarRentalApplication.getAppContext().getString(R.string.date_formatted), DateUtils.getFormattedDateFromIso8601String(bookingContract.vehicleMove.vehicleMoveDate));
            }
        } else {
            //Booking is on the other branch
            if (outgoing) {
                //This is the start
                return String.format(CarRentalApplication.getAppContext().getString(R.string.date_formatted), DateUtils.getFormattedDateFromIso8601String(bookingContract.vehicleMove.vehicleMoveDate));
            } else {
                //This is the end
                return String.format(CarRentalApplication.getAppContext().getString(R.string.date_formatted), DateUtils.getFormattedDateFromIso8601String(bookingContract.vehicleMove.vehicleReturnDate));
            }
        }
    }
}
