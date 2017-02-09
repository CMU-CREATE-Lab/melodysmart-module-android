package org.cmucreatelab.android.melodysmart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.bluecreation.melodysmart.DataService;
import com.bluecreation.melodysmart.MelodySmartDevice;

import org.cmucreatelab.android.melodysmart.listeners.DataListener;
import org.cmucreatelab.android.melodysmart.listeners.DeviceListener;
import org.cmucreatelab.android.melodysmart.models.MelodySmartMessage;

/**
 * Created by mike on 12/27/16.
 *
 * Handles interfacing with MelodySmart packages
 *
 */
public abstract class DeviceHandler<M extends MessageQueue> {

    public static final String LOG_TAG = "helpers.melodysmart";

    private MelodySmartDevice mMelodySmartDevice;
    private M messageQueue;
    private DataListener dataListener = null;
    private DeviceListener deviceListener = null;


    private void unregisterListeners() {
        mMelodySmartDevice.unregisterListener(deviceListener);
        mMelodySmartDevice.getDataService().unregisterListener(dataListener);
    }


    private void registerListeners() {
        mMelodySmartDevice.registerListener(deviceListener);
        dataListener.registerWithMessageQueue(messageQueue, mMelodySmartDevice.getDataService());
    }


    public DeviceHandler(Context appContext) {
        mMelodySmartDevice = MelodySmartDevice.getInstance();
        mMelodySmartDevice.init(appContext);
        messageQueue = initializeMessageQueue();
    }


    // message-sending


    public DataService getDataService() {
        return mMelodySmartDevice.getDataService();
    }


    public void addMessage(MelodySmartMessage message) {
        messageQueue.addMessage(message);
    }


    public void clearMessages() {
        messageQueue.clear();
    }


    // connect/disconnect for a session


    /**
     * Determine if a MelodySmart device is currently connected.
     *
     * @return true if both the DataService listener and MelodySmart listeners exist and are still registered/connected.
     */
    public boolean isConnected() {
        if (dataListener == null || deviceListener == null)
            return false;
        return dataListener.isServiceConnected() && deviceListener.isDeviceConnected();
    }


    /**
     * Request to connect a MelodySmart device.
     *
     * @param bluetoothDevice: grab the MAC address.
     */
    public void connect(BluetoothDevice bluetoothDevice) {
        if (deviceListener != null || dataListener != null) {
            unregisterListeners();
        }
        this.deviceListener = initializeDeviceListener();
        this.dataListener = initializeDataListener();
        registerListeners();
        mMelodySmartDevice.connect(bluetoothDevice.getAddress());
    }


    /**
     * Request to disconnect from all MelodySmart devices.
     */
    public void disconnect() {
        mMelodySmartDevice.disconnect();
    }


    // BLE scanning


    /**
     * Toggles MelodySmart device scanning (i.e. scanning for Flutter devices).
     *
     * @param isScanning: true indicates that the LeScan should be started/restarted.
     * @param leScanCallback: A callback for when a bluetooth device is scanned.
     */
    public synchronized void setFlutterScanning(boolean isScanning, final BluetoothAdapter.LeScanCallback leScanCallback) {
        mMelodySmartDevice.stopLeScan(leScanCallback);
        if (isScanning) {
            mMelodySmartDevice.startLeScan(leScanCallback);
        }
    }


    // Abstract methods


    public abstract M initializeMessageQueue();

    public abstract DeviceListener initializeDeviceListener();

    public abstract DataListener initializeDataListener();

}
