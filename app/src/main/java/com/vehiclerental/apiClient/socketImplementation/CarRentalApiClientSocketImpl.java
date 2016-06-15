/**
 * CarRental
 *
 * This file provides a socket based implementation for the service communication
 */

package com.vehiclerental.apiClient.socketImplementation;

import android.util.Base64;

import com.vehiclerental.CarRentalApplication;
import com.vehiclerental.apiClient.CarRentalApiClient;
import com.vehiclerental.apiClient.OperationCodes;
import com.vehiclerental.contracts.BookingContract;
import com.vehiclerental.contracts.BookingSearchResultContract;
import com.vehiclerental.contracts.BranchContract;
import com.google.gson.reflect.TypeToken;
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
import com.vehiclerental.exceptions.ApiException;
import com.vehiclerental.exceptions.ApiInvalidParameterException;
import com.vehiclerental.exceptions.ApiUnauthorizedException;
import com.vehiclerental.exceptions.ApiUnavailableException;
import com.vehiclerental.preferences.PreferencesManager;
import com.vehiclerental.utils.SerializationUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

public class CarRentalApiClientSocketImpl implements CarRentalApiClient {

    //Communication details
    private String url;
    private int port;

    /**
     * Constructor
     *
     * @param url server IP or domain
     * @param port server port
     */
    public CarRentalApiClientSocketImpl(String url, int port) {
        this.url = url;
        this.port = port;
    }

    /**
     * Return the list of available branches through a socket connection
     *
     * @return list of branches
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<BranchContract> getBranches() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<BranchContract>>() {}.getType();
        return doRequest(OperationCodes.GET_BRANCHES, null, jsonType);
    }

    /**
     * Return the result of the available vehicles with the given criteria through a socket connection
     *
     * @param contract search criteria
     * @return list of available vehicle results
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<BookingSearchResultContract> searchAvailableVehicles(SearchAvailableVehiclesRequestContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<BookingSearchResultContract>>() {}.getType();
        return doRequest(OperationCodes.SEARCH_AVAIL_VEHICLES, contract, jsonType);
    }

    /**
     * Create an user account with the given parameters through a socket connection
     *
     * @param contract account parameters
     * @return created account
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public UserContract createAccount(CreateAccountRequestContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<UserContract>() {}.getType();
        return doRequest(OperationCodes.CREATE_ACCOUNT, contract, jsonType);
    }

    /**
     * Returns the current user account details through a socket connection
     *
     * @return user account details
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public UserContract getAccountDetails() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<UserContract>() {}.getType();
        return doRequest(OperationCodes.GET_ACCOUNT_DETAILS, null, jsonType);
    }

    /**
     * Returns the bookings for the current user in the current branch through a socket connection
     *
     * @return list of bookings for the user and branch
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<BookingContract> getUserBookingsForBranch() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<BookingContract>>() {}.getType();
        return doRequest(OperationCodes.GET_USER_BOOKINGS, null, jsonType);
    }

    /**
     * Request a vehicle booking with the given criteria through a socket connection
     *
     * @param contract booking criteria
     * @return created booking
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public BookingContract bookVehicle(CreateBookingContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<BookingContract>() {}.getType();
        return doRequest(OperationCodes.BOOK_VEHICLE, contract, jsonType);
    }

    /**
     * Returns all the bookings for the current branch through a socket connection
     *
     * @return the branch bookings list
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<BookingContract> getBranchBookings() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<BookingContract>>() {}.getType();
        return doRequest(OperationCodes.GET_BRANCH_BOOKINGS, null, jsonType);
    }

    /**
     * Sends a shutdown request to the server through a socket connection
     *
     * @return nothing
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public Void shutdownSystem() throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<Void>() {}.getType();
        return doRequest(OperationCodes.SHUTDOWN_SYSTEM, null, jsonType);
    }

    /**
     * Create an user account with the given parameters through a socket connection (requires staff level)
     *
     * @param contract the account parameters
     * @return the created account
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public UserContract createUser(CreateAccountRequestContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<UserContract>() {}.getType();
        return doRequest(OperationCodes.CREATE_USER, contract, jsonType);
    }

    /**
     * Create or update a vehicle with the given parameters in the current branch through a socket connection
     *
     * @param contract vehicle parameters
     * @return created or updated vehicle
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public VehicleContract createOrUpdateVehicle(CreateUpdateVehicleContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<VehicleContract>() {}.getType();
        return doRequest(OperationCodes.UPDATE_OR_CREATE_VEHICLE, contract, jsonType);
    }

    /**
     * Search vehicles with the given criteria in the current branch through a socket connection
     *
     * @param contract search criteria
     * @return list of vehicles matching the criteria
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<VehicleContract> searchVehicles(SearchVehicleContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<VehicleContract>>() {}.getType();
        return doRequest(OperationCodes.SEARCH_ALL_VEHICLES, contract, jsonType);
    }

    /**
     * Search users with the given criteria (full text search on email and fullname) through a socket connection
     *
     * @param contract search criteria
     * @return list of users matching the criteria
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<UserContract> searchUser(SearchUserContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<UserContract>>() {}.getType();
        return doRequest(OperationCodes.SEARCH_USER, contract, jsonType);
    }

    /**
     * Update a booking status with the given parameters through a socket connection
     *
     * @param contract update parameters
     * @return updated booking
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public BookingContract changeBookingStatus(ChangeBookingStatusContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<BookingContract>() {}.getType();
        return doRequest(OperationCodes.CHANGE_BOOKING_STATUS, contract, jsonType);
    }

    /**
     * Returns the vehicle moves for the current branch matching the filter criteria through a socket connection
     *
     * @param contract filter criteria
     * @return list of bookings requiring moves
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiException if there was a general error during the request
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    @Override
    public List<BookingContract> getVehiclesMove(GetBranchVehicleMovesContract contract) throws ApiUnauthorizedException, ApiException, ApiInvalidParameterException, ApiUnavailableException {
        Type jsonType = new TypeToken<List<BookingContract>>() {}.getType();
        return doRequest(OperationCodes.GET_VEHICLE_MOVES, contract, jsonType);
    }

    /**
     * Converts the given parameters to SocketRequestMessage, proceed to the request and parse the response
     *
     * @param operationCode standard request operation code
     * @param contract object to send as request entity
     * @param returnType expected return type
     * @param <RE> generic expected return type
     * @param <CO> generic request entity type
     * @return Return the expected object deserialized (of generic type RE)
     * @throws ApiException if there was a general API error
     * @throws ApiUnauthorizedException if the request wasn't authenticated or authorized
     * @throws ApiInvalidParameterException if there was an error with a parameter
     * @throws ApiUnavailableException if the API is unavailable
     */
    private <RE, CO> RE doRequest(int operationCode, CO contract, Type returnType) throws ApiException, ApiUnauthorizedException, ApiInvalidParameterException, ApiUnavailableException {

        //Request preparation
        SocketRequestMessage request = new SocketRequestMessage();
        request.OperationCode = operationCode;
        if (contract != null) {
            request.SerializedObject = SerializationUtils.serialize(contract);
        }

        //Request processing
        try {
            //Authenticate requet if possible
            request = applyAuthentication(request);
            //Apply current branch to request if possible
            request = applyBranch(request);
            SocketResponseMessage response = SocketClient.doRequest(this.url, this.port, request);

            //Error handling
            if (response == null) {
                throw new ApiException(500, "Unknown error");
            }
            switch (response.status) {
                case 200: {
                    //Success: return result deserialized
                    return SerializationUtils.deserialize(response.serializedObject, returnType);
                }
                case 401: {
                    throw new ApiUnauthorizedException(response.error);
                }
                case 400: {
                    throw new ApiInvalidParameterException(response.error);
                }
                case 503: {
                    throw new ApiUnavailableException();
                }
                default: {
                    throw new ApiException(response.status, response.error);
                }
            }
        } catch (IOException e) {
            throw new ApiUnavailableException();
        } catch (ClassNotFoundException e) {
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * Update the given request with the current branch id if possible
     *
     * @param requestMessage current request object
     * @return updated request object
     */
    private SocketRequestMessage applyBranch(SocketRequestMessage requestMessage) {
        BranchContract branchContract = PreferencesManager.getCurrentBranch(CarRentalApplication.getAppContext());

        if (branchContract != null) {
            requestMessage.BranchId = branchContract.id;
        }

        return requestMessage;
    }

    /**
     * Update the given request with the current user if possible
     *
     * @param requestMessage current request object
     * @return updated request object
     */
    private SocketRequestMessage applyAuthentication(SocketRequestMessage requestMessage) {

        if (!PreferencesManager.isLoggedIn(CarRentalApplication.getAppContext())) {
            return requestMessage;
        }

        UserContract user = PreferencesManager.getLoggedUser(CarRentalApplication.getAppContext());
        String password = PreferencesManager.getUserPassword(CarRentalApplication.getAppContext());

        try {
            //Encoding auth parameters as Basic Auth (Base64(email:password))
            String basicAuth = user.emailAddress + ":" + password;
            byte[] data;
            data = basicAuth.getBytes("UTF-8");
            requestMessage.BasicAuth = Base64.encodeToString(data, Base64.DEFAULT);
            return requestMessage;
        } catch (UnsupportedEncodingException e) {
            return requestMessage;
        }
    }
}
