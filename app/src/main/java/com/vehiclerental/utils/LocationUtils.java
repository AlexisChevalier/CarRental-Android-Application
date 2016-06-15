/**
 * CarRental
 *
 * This file provides multiple location abstraction methods in order to get the nearest branch to the user
 */

package com.vehiclerental.utils;

import android.location.Location;

import com.vehiclerental.contracts.BranchContract;

import java.util.List;

import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class LocationUtils {

    //Constants used in the calculation
    private final static double EARTH_RADIUS_IN_KM = 6371;

    /**
     * Returns the nearest branch to the user position
     * This is not a particularly powerful algorithm but the number of branch is expected to be small at this time
     *
     * @param currentLocation user location
     * @param branchContractList list of branches
     * @return the nearest branch
     */
    public static BranchContract getNearestBranch(Location currentLocation, List<BranchContract> branchContractList) {
        if (branchContractList.size() == 0) {
            return null;
        }

        if (currentLocation == null) {
            return null;
        }

        BranchContract nearestBranch = branchContractList.get(0);
        double nearestBranchDistance = getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), nearestBranch.latitude, nearestBranch.longitude);

        for (BranchContract branchContract : branchContractList) {
            double currentBranchDistance = getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), branchContract.latitude, branchContract.longitude);
            if (currentBranchDistance < nearestBranchDistance) {
                nearestBranch = branchContract;
                nearestBranchDistance = currentBranchDistance;
            }
        }

        return nearestBranch;
    }

    /**
     * Calculates the spacial distance between two points using Haversine formula
     *
     * @param lat1 latitude of the first point
     * @param lng1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lng2 longitude of the second point
     * @return the distance between the two points
     */
    private static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        Double latDistance = degreesToRadians(lat2-lat1);
        Double lgnDistance = degreesToRadians(lng2-lng1);

        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(degreesToRadians(lat1)) * Math.cos(degreesToRadians(lat2)) *
                        Math.sin(lgnDistance / 2) * Math.sin(lgnDistance / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_IN_KM * c;
    }

    /**
     * Converts degrees value to radians
     *
     * @param degrees degrees value
     * @return radians value
     */
    private static double degreesToRadians(double degrees) {
        return (degrees * Math.PI / 180.0);
    }
}
