/**
 * CarRental
 *
 * This file provides a common interface for the service communication
 */

package com.vehiclerental.apiClient;

import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.ChangeBookingStatusContract;
import com.vehiclerental.contracts.CreateAccountRequestContract;
import com.vehiclerental.contracts.CreateBookingContract;
import com.vehiclerental.contracts.CreateUpdateVehicleContract;
import com.vehiclerental.contracts.GetBranchVehicleMovesContract;
import com.vehiclerental.contracts.SearchAvailableVehiclesRequestContract;
import com.vehiclerental.contracts.SearchUserContract;
import com.vehiclerental.contracts.SearchVehicleContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.contracts.VehicleContract;
import com.vehiclerental.contracts.VehicleMoveContract;
import com.vehiclerental.exceptions.ApiException;
import com.vehiclerental.exceptions.ApiInvalidParameterException;
import com.vehiclerental.exceptions.ApiUnauthorizedException;
import com.vehiclerental.exceptions.ApiUnavailableException;

import java.util.List;

public interface CarRentalApiClient {
    /**
     * Return the list of available branches
     *
     * @return list of branches
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<BranchContract> getBranches() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Return the result of the available vehicles with the given criteria
     *
     * @param contract search criteria
     * @return list of available vehicle results
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<BookingSearchResultContract> searchAvailableVehicles(SearchAvailableVehiclesRequestContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Create an user account with the given parameters
     *
     * @param contract account parameters
     * @return created account
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    UserContract createAccount(CreateAccountRequestContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Returns the current user account details
     *
     * @return user account details
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    UserContract getAccountDetails() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Returns the bookings for the current user in the current branch
     *
     * @return list of bookings for the user and branch
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<BookingContract> getUserBookingsForBranch() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Request a vehicle booking with the given criteria
     *
     * @param contract booking criteria
     * @return created booking
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    BookingContract bookVehicle(CreateBookingContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Returns all the bookings for the current branch
     *
     * @return the branch bookings list
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<BookingContract> getBranchBookings() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Sends a shutdown request to the server
     *
     * @return nothing
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    Void shutdownSystem() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Create an user account with the given parameters (requires staff level)
     *
     * @param contract the account parameters
     * @return the created account
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    UserContract createUser(CreateAccountRequestContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Create or update a vehicle with the given parameters in the current branch
     *
     * @param contract vehicle parameters
     * @return created or updated vehicle
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    VehicleContract createOrUpdateVehicle(CreateUpdateVehicleContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Search vehicles with the given criteria in the current branch
     *
     * @param contract search criteria
     * @return list of vehicles matching the criteria
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<VehicleContract> searchVehicles(SearchVehicleContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Search users with the given criteria (full text search on email and fullname)
     *
     * @param contract search criteria
     * @return list of users matching the criteria
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<UserContract> searchUser(SearchUserContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Update a booking status with the given parameters
     *
     * @param contract update parameters
     * @return updated booking
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    BookingContract changeBookingStatus(ChangeBookingStatusContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;

    /**
     * Returns the vehicle moves for the current branch matching the filter criteria
     *
     * @param contract filter criteria
     * @return list of bookings requiring moves
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    List<BookingContract> getVehiclesMove(GetBranchVehicleMovesContract contract)  throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException;
}
