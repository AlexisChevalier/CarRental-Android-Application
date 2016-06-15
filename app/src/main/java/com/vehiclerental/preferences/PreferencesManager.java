/**
 * CarRental
 *
 * This file provides preference manager, it is an abstraction layer for the SharedPreferences system, which also provides an in-memory cache
 *
 * At the moment the file is not too large, but if more preferences were to be added, other files should be created to avoid a bloated class
 */

package com.vehiclerental.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.preference.PreferenceManager;

import com.vehiclerental.contracts.BranchContract;
import com.vehiclerental.contracts.UserContract;
import com.vehiclerental.utils.SerializationUtils;

public class PreferencesManager {

    //SharedPreferences storage keys
    private static final String PREF_USER_LABEL = "logged_user";
    private static final String PREF_PASSWORD_LABEL = "password_user";
    private static final String PREF_BRANCH_LABEL = "current_branch";
    private static final String PREF_IP_ADDRESS_LABEL = "server_ip";
    private static final String PREF_PORT_LABEL = "server_port";

    //Default values for the connection system
    private static final String DEFAULT_IP_ADDRESS = "161.73.147.225";
    private static final int DEFAULT_PORT = 5106;

    //In-Memory cache, avoids a reload from the shared preferences every time
    private static UserContract currentUser;
    private static BranchContract currentBranch;
    private static String userPassword;
    private static String ipAddress;
    private static Integer port;

    /**
     * Returns the SharedPreferences for the given context
     *
     * @param ctx current context
     * @return SharedPreferences instance
     */
    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Updates the current branch
     *
     * @param ctx current context
     * @param contract newly selected branch
     */
    public static void setCurrentBranch(Context ctx, BranchContract contract) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_BRANCH_LABEL, SerializationUtils.serialize(contract));
        editor.apply();
        currentBranch = contract;
    }

    /**
     * Returns the current branch, loads it from the SharedPreferences if not yet in memory
     *
     * @param ctx current context
     * @return the current branch
     */
    public static BranchContract getCurrentBranch(Context ctx) {
        if (currentBranch == null) {
            currentBranch = SerializationUtils.deserialize(getSharedPreferences(ctx).getString(PREF_BRANCH_LABEL, null), BranchContract.class);
        }

        return currentBranch;
    }

    /**
     * Returns the current authentication state
     *
     * @param ctx current context
     * @return true if logged in, false otherwise
     */
    public static boolean isLoggedIn(Context ctx) {
        UserContract loggedUser = getLoggedUser(ctx);

        return !(loggedUser == null);
    }

    /**
     * Returns the current staff authorization state
     *
     * @param ctx current context
     * @return true if logged in AND staff member, false otherwise
     */
    public static boolean isStaffUser(Context ctx) {
        UserContract loggedUser = getLoggedUser(ctx);

        return loggedUser != null && loggedUser.isStaff;
    }

    /**
     * Returns the current logged user, loads it from the SharedPreferences if not yet in memory
     *
     * @param ctx current context
     * @return the current user if logged in, null otherwise
     */
    public static UserContract getLoggedUser(Context ctx) {
        if (currentUser == null) {
            currentUser = SerializationUtils.deserialize(getSharedPreferences(ctx).getString(PREF_USER_LABEL, null), UserContract.class);
        }

        return currentUser;
    }

    /**
     * Updates the current user
     *
     * @param ctx current context
     * @param user current user
     */
    public static void setLoggedUser(Context ctx, UserContract user) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_LABEL, SerializationUtils.serialize(user));
        currentUser = user;
        editor.apply();
    }

    /**
     * Remove the current user settings
     *
     * @param ctx current context
     */
    public static void clearSession(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_LABEL);
        editor.remove(PREF_PASSWORD_LABEL);
        editor.remove(PREF_BRANCH_LABEL);
        currentUser = null;
        userPassword = null;
        currentBranch = null;
        editor.apply();
    }

    /**
     * Change the current user password
     *
     * @param ctx current context
     * @param password new password
     */
    public static void setUserPassword(Context ctx, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD_LABEL, password);
        editor.apply();
        userPassword = password;
    }

    /**
     * Returns the current user password, loads it if not done yet
     *
     * @param ctx current context
     * @return current password
     */
    public static String getUserPassword(Context ctx) {
        if (userPassword == null) {
            userPassword = getSharedPreferences(ctx).getString(PREF_PASSWORD_LABEL, null);
        }

        return userPassword;
    }

    /**
     * Update head office IP address
     *
     * @param ctx current context
     * @param newIpAddress new IP address
     */
    public static void setHeadOfficeIpAddress(Context ctx, String newIpAddress) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_IP_ADDRESS_LABEL, newIpAddress);
        ipAddress = newIpAddress;
        editor.apply();
    }

    /**
     * Returns the current head office IP address, loads it if not done yet
     * @param ctx current context
     * @return the current head office IP address
     */
    public static String getHeadOfficeIpAddress(Context ctx) {
        if (ipAddress == null) {
            ipAddress = getSharedPreferences(ctx).getString(PREF_IP_ADDRESS_LABEL, null);

            if (ipAddress == null) {
                setHeadOfficeIpAddress(ctx, DEFAULT_IP_ADDRESS);
            }
        }

        return ipAddress;
    }

    /**
     * Update head office port
     *
     * @param ctx current context
     * @param newPort new port
     */
    public static void setHeadOfficePort(Context ctx, int newPort) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_PORT_LABEL, newPort);
        port = newPort;
        editor.apply();
    }

    /**
     * Returns the current head office port, loads it if not done yet
     * @param ctx current context
     * @return the current head office port
     */
    public static int getHeadOfficePort(Context ctx) {
        if (port == null) {
            port = getSharedPreferences(ctx).getInt(PREF_PORT_LABEL, -1);

            // 0 Should not be used as a port anyway.
            if (port <= 0) {
                setHeadOfficePort(ctx, DEFAULT_PORT);
            }
        }

        return port;
    }
}
