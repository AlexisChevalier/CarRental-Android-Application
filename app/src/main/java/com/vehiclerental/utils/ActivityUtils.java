/**
 * CarRental
 *
 * This file contains helpers for activities
 */

package com.vehiclerental.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.activities.Guest.ChangeConnectionSettingsActivity;
import com.vehiclerental.activities.Guest.ChooseBranchActivity;
import com.vehiclerental.activities.Guest.LoginActivity;
import com.vehiclerental.activities.Guest.SearchAvailableVehicleActivity;
import com.vehiclerental.activities.Staff.BranchBookingsActivity;
import com.vehiclerental.activities.Staff.CreateVehicleActivity;
import com.vehiclerental.activities.Staff.SearchVehicleActivity;
import com.vehiclerental.activities.Staff.ShowBranchMovesActivity;
import com.vehiclerental.activities.Staff.SystemAdministrationActivity;
import com.vehiclerental.activities.User.UserBookingsActivity;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.exceptions.ApiInvalidParameterException;
import com.vehiclerental.exceptions.ApiUnauthorizedException;
import com.vehiclerental.exceptions.ApiUnavailableException;
import com.vehiclerental.preferences.PreferencesManager;

public class ActivityUtils {

    /**
     * This method will display an error message depending on which error has been thrown by the API
     *
     * @param context current context
     * @param apiException expression thrown by the API client
     */
    public static void HandleException(Context context, Exception apiException) {
        Toast toast;
        if (apiException instanceof ApiUnauthorizedException) {
            toast = Toast.makeText(context, R.string.error_invalid_account_credentials, Toast.LENGTH_SHORT);
            PreferencesManager.clearSession(context);
        } else if (apiException instanceof ApiInvalidParameterException) {
            toast = Toast.makeText(context, String.format(context.getString(R.string.ends_with_a_dot), apiException.getMessage()), Toast.LENGTH_SHORT);
        } else if (apiException instanceof ApiUnavailableException) {
            toast = Toast.makeText(context, R.string.error_server_not_available, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(context, R.string.error_server_not_reachable, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * This method will build the left drawer on the android application in every activity which calls it
     *
     * The drawer is composed of many different items, each of them executing a specific action when clicked
     *
     * @param activity the current activity
     */
    public static void buildDrawer(final AppCompatActivity activity) {
        BranchContract branchContract = PreferencesManager.getCurrentBranch(activity);
        String branchName = branchContract == null ? activity.getString(R.string.no_branch_selected) : branchContract.name;

        /* Login */
        PrimaryDrawerItem loginItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_perm_identity_black_24dp)
            .withName(R.string.login_register_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    PreferencesManager.clearSession(activity);
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), LoginActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    return true;
                }
            }
        );

        /* Change connection settings */
        PrimaryDrawerItem changeIpSettings = new PrimaryDrawerItem()
                .withIcon(R.drawable.ic_settings_black_24dp)
                .withName(R.string.change_server_settings_menu_item)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                   @Override
                                                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), ChangeConnectionSettingsActivity.class);
                    intent.putExtra(LoginActivity.COMES_FROM_LOGIN, false);
                    activity.startActivity(intent);
                    return true;
               }
            }
        );

        /* Logout */
        PrimaryDrawerItem logoutItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_perm_identity_black_24dp)
            .withName(R.string.logout_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    PreferencesManager.clearSession(activity);
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    return true;
                }
            }
        );

        /* Change Branch */
        PrimaryDrawerItem changeBranchItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_room_black_24dp)
            .withName(String.format(activity.getString(R.string.change_branch_menu_item), branchName))
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), ChooseBranchActivity.class);
                    activity.startActivity(intent);
                    return true;
                }
            }
        );

        /* Search Vehicle */
        PrimaryDrawerItem searchBookingItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_search_black_24dp)
            .withName(R.string.search_available_vehicles_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), SearchAvailableVehicleActivity.class);
                    activity.startActivity(intent);
                    return true;
                }
            }
        );

        /* USER: Show user bookings */
        PrimaryDrawerItem userBookings = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_view_list_black_24dp)
            .withName(R.string.user_bookings_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), UserBookingsActivity.class);
                    activity.startActivity(intent);
                    return true;
                }
            }
        );

        /* ADMIN: Search vehicle */
        PrimaryDrawerItem searchVehicleItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_search_black_24dp)
            .withName(R.string.search_vehcile_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                               @Override
                                               public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                   Intent intent = new Intent(CarRentalApplication.getAppContext(), SearchVehicleActivity.class);
                   activity.startActivity(intent);
                   return true;
                }
            }
        );

        /* ADMIN: DO Client booking vehicle */
        PrimaryDrawerItem bookForClientItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_directions_car_black_24dp)
            .withName(R.string.make_client_booking_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                               @Override
                                               public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                   Intent intent = new Intent(CarRentalApplication.getAppContext(), SearchAvailableVehicleActivity.class);
                   activity.startActivity(intent);
                   return true;
                    }
            }
        );

        /* ADMIN: AddVehicle */
        PrimaryDrawerItem addVehicleItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_add_black_24dp)
            .withName(R.string.add_vehicle_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                   @Override
                                                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                   Intent intent = new Intent(CarRentalApplication.getAppContext(), CreateVehicleActivity.class);
                   activity.startActivity(intent);
                   return true;
                                                   }
            }
        );

        /* ADMIN: Show branch bookings */
        PrimaryDrawerItem showBranchBookingsItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_view_list_black_24dp)
            .withName(R.string.view_branch_bookings_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                               @Override
                                               public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                   Intent intent = new Intent(CarRentalApplication.getAppContext(), BranchBookingsActivity.class);
                   activity.startActivity(intent);
                   return true;
                                               }
            }
        );

        /* ADMIN: Show Moves */
        PrimaryDrawerItem showMovesItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_swap_horiz_black_24dp)
            .withName(R.string.view_branch_moves_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                   @Override
                                                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                   Intent intent = new Intent(CarRentalApplication.getAppContext(), ShowBranchMovesActivity.class);
                   activity.startActivity(intent);
                   return true;
                                                   }
            }
        );

        /* ADMIN: sys admin */
        PrimaryDrawerItem sysAdminItem = new PrimaryDrawerItem()
            .withIcon(R.drawable.ic_power_settings_new_black_24dp)
            .withName(R.string.server_control_menu_item)
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                   @Override
                                                   public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    Intent intent = new Intent(CarRentalApplication.getAppContext(), SystemAdministrationActivity.class);
                    activity.startActivity(intent);
                    return true;
                                                   }
            }
        );

        //Instantiate the builder
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(activity)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1);

        //Instantiate the header builder
        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.drawer_header)
                .withSelectionListEnabledForSingleProfile(false);

        //Finalize configuration, depending on user leverl
        if (PreferencesManager.isLoggedIn(activity)) {
            UserContract user = PreferencesManager.getLoggedUser(activity);

            if (PreferencesManager.isStaffUser(activity)) {
                //Staff drawer
                AccountHeader headerResult = headerBuilder
                    .addProfiles(new ProfileDrawerItem()
                        .withName(String.format(activity.getString(R.string.staff_user_label), user.fullName))
                        .withEmail(user.emailAddress)
                        .withIcon(R.drawable.icon))
                    .build();

                drawerBuilder
                    .withAccountHeader(headerResult)
                    .addDrawerItems(logoutItem, changeBranchItem, searchVehicleItem, addVehicleItem, bookForClientItem, showBranchBookingsItem, showMovesItem, sysAdminItem, changeIpSettings);
            } else {
                //User drawer
                AccountHeader headerResult = headerBuilder
                    .addProfiles(new ProfileDrawerItem()
                        .withName(user.fullName)
                        .withEmail(user.emailAddress)
                        .withIcon(R.drawable.icon))
                    .build();

                drawerBuilder
                    .withAccountHeader(headerResult)
                    .addDrawerItems(logoutItem, changeBranchItem, searchBookingItem, userBookings, changeIpSettings);
            }
        } else {
            //User drawer
            AccountHeader headerResult = headerBuilder
                .addProfiles(new ProfileDrawerItem()
                    .withName(activity.getString(R.string.not_logged_in))
                    .withIcon(R.drawable.icon))
                .build();

            drawerBuilder
                .withAccountHeader(headerResult)
                .addDrawerItems(loginItem, changeBranchItem, searchBookingItem, changeIpSettings);
        }

        //Build the drawer
        Drawer drawer = drawerBuilder.build();
    }
}
