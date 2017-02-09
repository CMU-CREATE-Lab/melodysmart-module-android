package org.cmucreatelab.android.melodysmart.listeners;

import android.util.Log;

import com.bluecreation.melodysmart.DataService;

import org.cmucreatelab.android.melodysmart.DeviceHandler;
import org.cmucreatelab.android.melodysmart.MessageQueue;
import org.cmucreatelab.android.melodysmart.models.MelodySmartMessage;

/**
 * Created by mike on 12/28/16.
 *
 * Creates a DataService.Listener instance using information from a Session.
 *
 */
public abstract class DataListener<M extends MessageQueue> implements DataService.Listener {

    private M messageQueue;
    private boolean serviceConnected = false;


    public boolean isServiceConnected() {
        return serviceConnected;
    }


    public void registerWithMessageQueue(M messageQueue, DataService service) {
        this.messageQueue = messageQueue;
        service.registerListener(this);
    }


    @Override
    public void onConnected(final boolean isFound) {
        Log.v(DeviceHandler.LOG_TAG,"DataListener.onConnected isFound="+isFound);
        serviceConnected = isFound;
        if (serviceConnected) {
            this.onConnected();
        }
    }


    @Override
    public void onReceived(final byte[] bytes) {
        MelodySmartMessage currentMessage = messageQueue.notifyMessageReceived();
        if (currentMessage == null) {
            Log.v(DeviceHandler.LOG_TAG,"DataListener.onReceived ignoring with null currentMessage");
        } else {
            String response = new String(bytes);
            Log.v(DeviceHandler.LOG_TAG,"DataListener.onReceived="+response);
            onMessageReceived(currentMessage,response);
        }
    }


    // abstract methods


    public abstract void onConnected();

    public abstract void onMessageReceived(MelodySmartMessage request, String response);

}
