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
public abstract class DataListener<T extends MelodySmartMessage, M extends MessageQueue<T>> implements DataService.Listener {

    private M messageQueue;
    private boolean serviceConnected = false;
    private DeviceHandler parent;


    public DataListener(DeviceHandler parent) {
        this.parent = parent;
    }


    public boolean isServiceConnected() {
        return serviceConnected;
    }


    public DeviceHandler getParent() {
        return parent;
    }


    public void registerWithMessageQueue(M messageQueue, DataService service) {
        this.messageQueue = messageQueue;
        service.registerListener(this);
    }


    @Override
    public void onConnected(final boolean isFound) {
        Log.v(DeviceHandler.LOG_TAG,"DataListener.onConnected isFound="+isFound);
        serviceConnected = isFound;
        // needed for receiving message responses
        this.parent.getDataService().enableNotifications(serviceConnected);
        if (serviceConnected) {
            this.onConnected();
        }
    }


    @Override
    public void onReceived(final byte[] bytes) {
        String response = new String(bytes);
        T currentMessage = messageQueue.notifyMessageReceived(response);
        Log.v(DeviceHandler.LOG_TAG,"DataListener.onReceived="+response);
        if (currentMessage == null) {
            Log.v(DeviceHandler.LOG_TAG,"DataListener.onReceived ignoring with null currentMessage");
        } else {
            onMessageReceived(currentMessage);
        }
    }


    // abstract methods


    public abstract void onConnected();

    public abstract void onMessageReceived(T request);

}
