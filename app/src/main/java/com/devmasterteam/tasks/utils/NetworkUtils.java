package com.devmasterteam.tasks.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {

    /**
     * Verifica se existe conexão com internet
     */
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
