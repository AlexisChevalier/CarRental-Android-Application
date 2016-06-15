/**
 * CarRental
 *
 * This file provides a simple access to all the static (or nearly static) data of the system
 *
 * This includes:
 * - The vehicle types
 * - The vehicle statuses
 * - The branch list (which is only loaded once)
 */

package com.vehiclerental.utils;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.contracts.BranchContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticDataUtils {

    //Data structures to provide an easy access to the static data
    private static ArrayList<String> vehicleTypes;
    private static ArrayList<String> vehicleStatuses;
    private static Map<String, BranchContract> branchContractMap;

    /**
     * Registers all the available vehicle types in memory
     */
    private static void initializeVehicleTypes() {
        vehicleTypes = new ArrayList<>();
        vehicleTypes.add(0, CarRentalApplication.getAppContext().getString(R.string.small_car_type));
        vehicleTypes.add(1, CarRentalApplication.getAppContext().getString(R.string.family_car_type));
        vehicleTypes.add(2, CarRentalApplication.getAppContext().getString(R.string.small_van_type));
        vehicleTypes.add(3, CarRentalApplication.getAppContext().getString(R.string.large_van_type));
    }

    /**
     * Registers all the available vehicle status in memory
     */
    private static void initializeVehicleStatuses() {
        vehicleStatuses = new ArrayList<>();
        vehicleStatuses.add(0, CarRentalApplication.getAppContext().getString(R.string.available_status));
        vehicleStatuses.add(1, CarRentalApplication.getAppContext().getString(R.string.in_transit_status));
        vehicleStatuses.add(2, CarRentalApplication.getAppContext().getString(R.string.in_client_booking_status));
        vehicleStatuses.add(3, CarRentalApplication.getAppContext().getString(R.string.maintenance_status));
    }

    /**
     * Registers the branches in memory
     * We are using a hashmap, because the branches will be mapped through their name
     *
     * @param branches the branch list
     */
    public static void setBranches(List<BranchContract> branches) {
        branchContractMap = new HashMap<>();
        for (BranchContract branch : branches) {
            branchContractMap.put(branch.name, branch);
        }
    }

    /**
     * Returns the list of vehicle types, and initialize it if it has not been done yet
     *
     * @return the vehicle types list
     */
    public static ArrayList<String> getVehicleTypesList() {
        if (vehicleTypes == null) {
            initializeVehicleTypes();
        }

        return vehicleTypes;
    }

    /**
     * Returns the list of vehicle types as an array, and initialize it if it has not been done yet
     *
     * @return the vehicle types array
     */
    public static String[] getVehicleTypesArray() {
        return getVehicleTypesList().toArray(new String[getVehicleTypesList().size()]);
    }

    /**
     * Returns the list of vehicle statuses, and initialize it if it has not been done yet
     *
     * @return the vehicle statuses list
     */
    public static ArrayList<String> getVehicleStatusesList() {
        if (vehicleStatuses == null) {
            initializeVehicleStatuses();
        }

        return vehicleStatuses;
    }

    /**
     * Returns the list of vehicle statuses as an array, and initialize it if it has not been done yet
     *
     * @return the vehicle statuses array
     */
    public static String[] getVehicleStatusesArray() {
        return getVehicleStatusesList().toArray(new String[getVehicleStatusesList().size()]);
    }

    /**
     * Returns the array of the branch names
     *
     * @return the branch names arrays
     */
    public static String[] getBranchNamesArray() {
        return branchContractMap.keySet().toArray(new String[branchContractMap.size()]);
    }

    /**
     * Return the branch matching the given name
     *
     * @param name given branch name
     * @return the branch if it exists, null otherwise
     */
    public static BranchContract getBranchFromName(String name) {
        return branchContractMap.get(name);
    }
}
