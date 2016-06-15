/**
 * CarRental
 *
 * This file provides an initialization class, giving access to the api client from the rest of the application
 */

package com.vehiclerental.apiClient;

import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.apiClient.socketImplementation.CarRentalApiClientSocketImpl;
import com.vehiclerental.preferences.PreferencesManager;

public class CarRentalApiClientFactory {
    /**
     * Creates a new API client with the default IP and port values
     *
     * @return api client
     */
    public static CarRentalApiClient getApiClient() {
        return new CarRentalApiClientSocketImpl(
                PreferencesManager.getHeadOfficeIpAddress(CarRentalApplication.getAppContext()),
                PreferencesManager.getHeadOfficePort(CarRentalApplication.getAppContext()));
    }

    /**
     * Creates a new API client with the given IP and port values
     * @param ipAddress given ip address
     * @param port given port
     * @return api client
     */
    public static CarRentalApiClient getApiClient(String ipAddress, int port) {
        return new CarRentalApiClientSocketImpl(ipAddress, port);
    }
}
