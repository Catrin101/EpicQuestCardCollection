package com.example.epicquestcardcollection.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Receiver simple para detectar cambios en la conectividad de red
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            boolean isConnected = isNetworkAvailable(context);

            if (isConnected) {
                Log.d(TAG, "Conexión a internet restaurada");
                onNetworkConnected(context);
            } else {
                Log.w(TAG, "Conexión a internet perdida");
                onNetworkDisconnected(context);
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void onNetworkConnected(Context context) {
        // Aquí podrías:
        // - Sincronizar datos pendientes
        // - Actualizar la UI si estás en una actividad
        // - Mostrar un mensaje toast
        android.widget.Toast.makeText(context, "Conexión restaurada ✓",
                android.widget.Toast.LENGTH_SHORT).show();
    }

    private void onNetworkDisconnected(Context context) {
        // Aquí podrías:
        // - Pausar operaciones de red
        // - Mostrar un mensaje de advertencia
        // - Actualizar el estado en la UI
        android.widget.Toast.makeText(context, "Sin conexión a internet ✗",
                android.widget.Toast.LENGTH_LONG).show();
    }
}
