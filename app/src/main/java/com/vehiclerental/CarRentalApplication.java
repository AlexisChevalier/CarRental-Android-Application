/**
 * CarRental
 *
 * This file represents the global application, its role is to provide an application-wide access to
 * the context and to the SSL parameters
 */

package com.vehiclerental;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class CarRentalApplication extends Application {

    //Static access to context and ssl parameters
    private static Application application;
    private static SSLContext sslContext;

    /**
     * Returns the current application object
     *
     * @return application object
     */
    public static Application getApplication() {
        return application;
    }

    /**
     * Returns the current application context
     *
     * @return application context
     */
    public static Context getAppContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * Returns the current SSL parameters
     *
     * @return SSL context parameters
     */
    public static SSLContext getSslContext() {
        return sslContext;
    }

    @Override
    public void onCreate() {
        //At the beginning of the application, a reference to the app is stored and the SSL parameters are initialized
        super.onCreate();
        application = this;

        setupSslTrustStore();
    }

    /**
     * Sets up the SSL trust store, containing the encryption keys for encrypted socket communication
     *
     * The keystore was generated with this tool: http://www.keystore-explorer.org/downloads.php
     */
    private void setupSslTrustStore() {
        try {
            char[] password = "carrental".toCharArray();

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(this.getResources().openRawResource(R.raw.carrental_keystore), password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);

            // Create a SSLContext with the certificate
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
            Log.e("SSL", "Failed to create SSL context");
        }
    }
}