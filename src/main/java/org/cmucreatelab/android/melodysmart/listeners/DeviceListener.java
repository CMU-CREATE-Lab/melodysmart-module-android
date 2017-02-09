package org.cmucreatelab.android.melodysmart.listeners;

import android.util.Log;

import com.bluecreation.melodysmart.BLEError;
import com.bluecreation.melodysmart.DeviceDatabase;
import com.bluecreation.melodysmart.MelodySmartListener;

import org.cmucreatelab.android.melodysmart.DeviceHandler;

/**
 * Created by mike on 12/28/16.
 *
 * Creates a MelodySmartListener instance using information from a Session.
 *
 */
public abstract class DeviceListener implements MelodySmartListener {

    private boolean deviceConnected = false;


    public boolean isDeviceConnected() {
        return deviceConnected;
    }


    @Override
    public void onDeviceConnected() {
        Log.v(DeviceHandler.LOG_TAG, "DeviceListener.onDeviceConnected");
        deviceConnected = true;
        this.onConnected();
    }


    @Override
    public void onDeviceDisconnected(final BLEError bleError) {
        Log.v(DeviceHandler.LOG_TAG, "DeviceListener.onDeviceDisconnected");
        deviceConnected = false;
        this.onDisconnected(bleError);
    }


    // unused implementations
    public void onOtauAvailable() {}
    public void onOtauRecovery(DeviceDatabase.DeviceData deviceData) {}


    // abstract methods

    public abstract void onConnected();

    public abstract void onDisconnected(final BLEError bleError);

}
